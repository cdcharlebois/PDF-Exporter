package pdf_exporter.lib;
//import org.apache.commons.io.FilenameUtils;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
//import org.apache.pdfbox.pdmodel.edit.PDPageContentStream; // deprecated in 2.0
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
//import org.apache.pdfbox.pdmodel.interactive.form.PDCheckbox; // deprecated in 2.0
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
//import org.apache.pdfbox.pdmodel.interactive.form.PDRadioCollection; // deprecated in 2.0; ignored here.
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
//import org.apache.pdfbox.pdmodel.interactive.form.PDTextbox; // deprecated in 2.0 (see TextField)

import pdf_exporter.proxies.PDFField;
import pdf_exporter.proxies.PDF_FormType;

import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;

public class PDFInjector {

	//Generates an array of the form fields present in the given PDF document
	public static List<MXPDField> getFieldList (PDDocument pdfDoc) throws Exception {
		//Get the field list
		PDDocumentCatalog docCatalog = pdfDoc.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();
		@SuppressWarnings("unchecked")
		COSArrayList<PDField> fieldList = (COSArrayList<PDField>) acroForm.getFields();

		Map<String, Integer> fieldPageDict = getFormFieldPageMap(pdfDoc);
		
		ArrayList<MXPDField> mxFieldList = new ArrayList<MXPDField>();
		for (PDField f: fieldList) {
			int page  = fieldPageDict.get(f.getFullyQualifiedName());
			//call recursive function to get fields and their children
			mxFieldList.add(createMXPDField(f, page));
			
		}
		return mxFieldList;
	}
	
	//Generates an array of the form fields present in the given PDF document, in a list of Mendix PDF_Field entities
	public static List<IMendixObject> getFieldList_MX (PDDocument pdfDoc,pdf_exporter.proxies.PDFDocument mxPDFDoc, IContext i) throws Exception {
		//Get the list of fields
		List<MXPDField> fieldList = PDFInjector.getFieldList(pdfDoc);
		
		//Build a list of mendix objects based on the list of fields
		List<IMendixObject> returnList = new ArrayList<IMendixObject>();
		for (MXPDField f : fieldList) {
			pdf_exporter.proxies.PDFField fx = new pdf_exporter.proxies.PDFField(i);
			fx.setName(f.getName());
			fx.setValue(f.getValue());
			fx.setParentName(f.getParent());
			
			fx.setPDFField_PDFDoc(mxPDFDoc);
			fx.setLength(f.getLength());
			fx.setPage(f.getPage());
			
			//We need to convert the types into the Mendix enumeration values
			String fieldType = f.getType();
			
			if(fieldType.equals("PDTextbox")) {fx.setFieldType(PDF_FormType.Text);}
			else if(fieldType.equals("PDCheckbox") && f.getParent() == null) {fx.setFieldType(PDF_FormType.Checkbox);}
			else if(fieldType.equals("PDCheckbox")) {fx.setFieldType(PDF_FormType.Bullet);}
			else if(fieldType.equals("PDRadioCollection")) {
				fx.setFieldType(PDF_FormType.Bullet_List);
				//Add children as options
				List<MXPDField> children = f.getChildren();
				for(MXPDField c: children) {
					pdf_exporter.proxies.PDFChoice opt = new pdf_exporter.proxies.PDFChoice(i);
					opt.setPDFChoice_PDFField(fx);
					opt.setOptionValue(c.getName());
				}
			}
			
			IMendixObject m = fx.getMendixObject();
			returnList.add(m);
		}
		
		//Return the final list
		return returnList;
	}
	//Only pass back the list of fields with no parent. For text and check boxes this won't matter.
	//For radio button collections, the selected child value should be stored in the parent's "value" and will be used to set the right radio button
	public static PDDocument setFieldList (PDDocument pdfDoc, ArrayList<MXPDField> mxFieldList) throws Exception {
		//Get the field list
		PDDocumentCatalog docCatalog = pdfDoc.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();
		@SuppressWarnings("unchecked")
		COSArrayList<PDField> fieldList = (COSArrayList<PDField>) acroForm.getFields();

		//Create a dictionary out of the field list, so we can search it easily
		HashMap<String,String> fieldMap = new HashMap<String,String>();
		for(MXPDField mf : mxFieldList) {
			fieldMap.put(mf.getName(), mf.getValue());
		}
		
		//For each field in the form, look for a matching field in the list and update its value
		for(PDField f : fieldList) {
			String newVal = fieldMap.get(f.getFullyQualifiedName());
			if (newVal != null) {
				f.setValue(newVal);
			}
			
			if(f instanceof PDCheckBox) {
				PDCheckBox c = (PDCheckBox)f;
				if(newVal != null) c.check();
				else c.unCheck(); 
			}
		}
		
		return pdfDoc;
	}
	//Overloaded method that handles a list of Mendix PDF_Field objects
	public static PDDocument setFieldList (PDDocument pdfDoc, List<pdf_exporter.proxies.PDFField> mxFieldList, boolean LockFields, boolean populateFields) throws Exception {
		
		//Get the field list
		PDDocumentCatalog docCatalog = pdfDoc.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();
//		@SuppressWarnings("unchecked")
		COSArrayList<PDField> fieldList = (COSArrayList<PDField>) acroForm.getFields();

		//Create a dictionary out of the field list, so we can search it easily
		HashMap<String,PDFField> fieldMap = new HashMap<String,PDFField>();
		for(PDFField mf : mxFieldList) {
			fieldMap.put(mf.getName(), mf);
		}
		
		Map<String, Integer> fieldPageDict = getFormFieldPageMap(pdfDoc);
//		@SuppressWarnings("unchecked")
//		List<PDPage> docPages = docCatalog.getAllPages(); deprecated
		PDPageTree pdPageTree = docCatalog.getPages();
		
		//For each field in the form, look for a matching field in the list and update its value
		//Or if populateFields == false, then write on top of the document"
		for(PDField f : fieldList) {
			
			PDFField pf = fieldMap.get(f.getFullyQualifiedName());
			if (pf != null && pf.getValue() != null) {
				String newVal = pf.getValue();
				if(populateFields) {
					f.setValue(newVal);
				} else {					
					String overflowVal = writeTextOverField(f,newVal, pdfDoc, pdPageTree, fieldPageDict, false, Color.BLACK, PDType1Font.HELVETICA, 10, true);
					if (overflowVal.length()>0) {
						String currentAddendumVal = pf.getAddendumValue();
						if (currentAddendumVal != null && currentAddendumVal.length() > 0)
							pf.setAddendumValue(pf.getAddendumValue() + '\n' + overflowVal);
						else
							pf.setAddendumValue(overflowVal);
					}
				}
			
				if(f instanceof PDCheckBox) {
					PDCheckBox c = (PDCheckBox)f;
					if(newVal != null) c.check();
					else c.unCheck(); 
				}
			
				//Lock fields if the boolean says so
				f.setReadOnly(LockFields);
			}
		}
		return pdfDoc;
	}
	
	public static PDDocument annotatePDFFieldNames (PDDocument pdfDoc) throws Exception{

		// TODO Auto-generated method stub
		
		PDDocumentCatalog docCatalog = pdfDoc.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();
                
		@SuppressWarnings("unchecked")
		COSArrayList<PDField> fieldList = (COSArrayList<PDField>) acroForm.getFields();	
		Map<String, Integer> fieldPageDict = getFormFieldPageMap(pdfDoc);
//		@SuppressWarnings("unchecked")
//		List<PDPage> docPages = docCatalog.getAllPages();
		PDPageTree pdPageTree = docCatalog.getPages();
		
		//Set the field value to the field's actual name
		for(PDField c : fieldList) {
			try 			{
				String s = c.getFullyQualifiedName();
				writeTextOverField(c,c.getFullyQualifiedName(), pdfDoc, pdPageTree, fieldPageDict, true, Color.RED, PDType1Font.COURIER, 8, false);
			}
			catch (Exception e) {System.out.println(e);}
		}
		
		return pdfDoc;

	}
	
	private static String writeTextOverField(PDField c, String value,PDDocument pdfDoc,PDPageTree docPages, Map<String,Integer> fieldPageDict, boolean offsetText, Color color, PDFont font, int fontSize, boolean multiline) throws IOException{
		PDPageContentStream contentStream = new PDPageContentStream(pdfDoc, docPages.get(fieldPageDict.get(c.getFullyQualifiedName())-1), true, true, true);
		String toReturn = "";
		
		//If this is a text box, just print the field's name
		if(c instanceof PDTextField) {
			PDRectangle cLoc = getFieldArea(c);
			if(cLoc != null) {
				if(offsetText) toReturn =  printStringToPDF(value, contentStream,cLoc.getLowerLeftX() ,cLoc.getLowerLeftY()+10, cLoc, color, font, fontSize, multiline);
				else toReturn =  printStringToPDF(value, contentStream, cLoc.getLowerLeftX(),cLoc.getUpperRightY()-12, cLoc, color, font, fontSize, multiline);
			}
		}
		//If this is a check box, just print the field's name, shifted a bit so it doesn't overlap with the checkbox itself
		else if(c instanceof PDCheckBox) {
			PDRectangle cLoc = getFieldArea(c);
			
			if(cLoc != null) {
				if(offsetText) toReturn =  printStringToPDF(value, contentStream,cLoc.getLowerLeftX()+20 ,cLoc.getLowerLeftY()+15, cLoc, color, font, fontSize, multiline);
				else toReturn =  printStringToPDF(value, contentStream, cLoc.getLowerLeftX(),cLoc.getLowerLeftY(), cLoc, color, font, fontSize, multiline);
			}
		}
		//If this is a radio button set, print the name and the child selector names
		/*
		 * IGNORE RADIO BUTTONS FOR NOW
		 * */
//		else if(c instanceof PDRadioCollection) {
//			List<COSObjectable> kids = c.getKids();
//			PDField kid0 = (PDField)kids.get(0);
//			PDRectangle cLoc = getFieldArea(kid0);
//			String toPrint = c.getFullyQualifiedName() + " (";
//			for(COSObjectable k: kids) {
//				if(k instanceof PDCheckBox) {
//					PDCheckBox kF = (PDCheckBox)k;
//					toPrint = toPrint + kF.getOnValue() + ",";
//				}
//			}
//			toPrint = toPrint.substring(0,toPrint.length()-1);
//			toPrint = toPrint + ")";
//			
//			if(cLoc != null) 
//				toReturn =  printStringToPDF(toPrint, contentStream, cLoc.getUpperRightX(),cLoc.getUpperRightY(), cLoc, color, font, fontSize, multiline);
//		
//		}
		
		//stop the fields from being editable, since there's no point in editing them in the output doc
		c.setReadOnly(true);
				
		contentStream.close();
		return toReturn;
	}

	protected static String printStringToPDF(String text, final PDPageContentStream contentStream, float x, float y, final PDRectangle rect, Color color, PDFont font, int fontSize, boolean multiline) throws IOException {
		
		contentStream.setNonStrokingColor(color);
		
		if(multiline) {
		
			final float fontWidth = font.getFontDescriptor().getFontBoundingBox().getWidth() / 1000 * fontSize;
			final float numCharsPerLine = (rect.getUpperRightX() - rect.getLowerLeftX()) / fontWidth * 2;
			final float fontHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
			int maxLines = (int)((rect.getUpperRightY() - rect.getLowerLeftY()) / fontHeight);
			
	        String[] lines1 = text.split("\n");
	        List<String> lines2 = new ArrayList<String>();
	        
	        for (String s: lines1) {
	        	String newLine = "";
	        	String[] words = s.split(" ");
	        	for (String w: words) {
	        		if (newLine.length() + w.length() <= numCharsPerLine) {
	        			newLine = newLine.concat(" ");
	        			newLine = newLine.concat(w);
	        		} else {
	        			lines2.add(newLine);
	        			newLine = "";
	        			newLine = newLine.concat(w);
	        		}
	        	}
	        	lines2.add(newLine);
	        }
	        
	        List<String> linesToPrint;
	        String linesToReturn = "";
	        
	        if(lines2.size() > maxLines) {
	        	if (maxLines > 1) maxLines--; //if not everything will fit, leaves 1 spare line for a "CONTINUED" message
	        	linesToPrint = lines2.subList(0, maxLines);
	        	
	        	List<String> temp = new ArrayList<String>();
	        	temp.addAll(linesToPrint);
	        	temp.add("<CONTINUED ON NEXT PAGE>");
	        	linesToPrint = temp;
	        	
	        	List<String> linesToReturnList = lines2.subList(maxLines,lines2.size());
	        	for(String s: linesToReturnList) {
	        		linesToReturn = linesToReturn + s + "\n";
	        	}
	        	if(linesToReturn.length() > 1) {
	        		linesToReturn = linesToReturn.substring(0, linesToReturn.length()-1);
	        	}
	        } else {
	        	linesToPrint = lines2;
	        }
	        
			printMultipleLines(contentStream, linesToPrint,x,y,font,fontSize);
			return linesToReturn;
		} else {
			contentStream.beginText();
			contentStream.setTextTranslation(x, y);		
	        contentStream.setFont(font, fontSize);
	        contentStream.showText(text);
	        contentStream.endText();
	        return "";
		}
	}
	
	private static void printMultipleLines(PDPageContentStream contentStream, List<String> lines, float x, float y, PDFont font, int fontSize) throws IOException {
		  if (lines.size() == 0) {
		    return;
		  }
		  final int numberOfLines = lines.size(); 
		  final float fontHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
	      contentStream.setFont(font, fontSize);
		  
		  contentStream.beginText();
		  contentStream.appendRawCommands(fontHeight + " TL\n");
		  contentStream.setTextTranslation( x, y);
		  for (int i = 0; i < numberOfLines; i++) {
		    contentStream.showText(lines.get(i));
		    if (i < numberOfLines - 1) {
		      contentStream.appendRawCommands("T*\n");
		    }
		  }
		  contentStream.endText();
	}
	
	protected static PDRectangle getFieldArea(PDField field) {
		PDRectangle r = null;
			try {
				r = field.getWidgets().get(0).getRectangle(); // default to first annotation 
	        } catch (Exception ex) {
	            System.out.println("No rectangle available to get");
	        }		
		return r;
	}
	
	@SuppressWarnings("unchecked")
	protected static Map<String, Integer> getFormFieldPageMap(PDDocument pdfDoc) throws IOException {
	    PDDocumentCatalog docCatalog = pdfDoc.getDocumentCatalog();

//	    List<PDPage> pages = docCatalog.getAllPages();
	    PDPageTree pdPageTree = docCatalog.getPages();
	    Map<String, Integer> fieldPageMap = new HashMap<String, Integer>();
	    Map<COSDictionary, Integer> pageNrByAnnotDict = new HashMap<COSDictionary, Integer>();
	    for (int i = 0; i < pdPageTree.getCount(); i++) {
	        PDPage page = pdPageTree.get(i);
	        for (PDAnnotation annotation : page.getAnnotations())
	            pageNrByAnnotDict.put(annotation.getCOSObject(), i + 1);
	    }

	    PDAcroForm acroForm = docCatalog.getAcroForm();
	    
	    for (PDField field : (List<PDField>)acroForm.getFields()) {
	        COSDictionary fieldDict = field.getCOSObject();

	        List<Integer> annotationPages = new ArrayList<Integer>();
	        /* 
	         * IGNORED
	         */
//	        List<COSObjectable> kids = field.get();
//	        if (kids != null) {
//	            for (COSObjectable kid : kids) {
//	                COSBase kidObject = kid.getCOSObject();
//	                if (kidObject instanceof COSDictionary)
//	                    annotationPages.add(pageNrByAnnotDict.get(kidObject));
//	            }
//	        }

	        Integer mergedPage = pageNrByAnnotDict.get(fieldDict);

	        if (mergedPage == null)
	            if (annotationPages.isEmpty())
	                System.out.printf("i Field '%s' not referenced (invisible).\n", field.getFullyQualifiedName());
	            else
	            {
	                System.out.printf("a Field '%s' referenced by separate annotation on %s.\n", field.getFullyQualifiedName(), annotationPages);
	        		fieldPageMap.put(field.getFullyQualifiedName(), annotationPages.get(0));
	            }
	        else 
	            if (annotationPages.isEmpty())
	            {
	                System.out.printf("m Field '%s' referenced as merged on %s.\n", field.getFullyQualifiedName(), mergedPage);
	        		fieldPageMap.put(field.getFullyQualifiedName(), mergedPage);
	            }
	            else
	                System.out.printf("x Field '%s' referenced as merged on %s and by separate annotation on %s. (Not allowed!)\n", field.getFullyQualifiedName(), mergedPage, annotationPages);
	    }
	    return fieldPageMap;
	}
	
	protected static MXPDField createMXPDField(PDField f, int page) throws Exception{
		String name = f.getFullyQualifiedName();
		String value = f.getValueAsString();
		String parentName = "";
		String type = f.getClass().getSimpleName();
		
		PDField p = f.getParent();
		if (p != null) {
			parentName = p.getFullyQualifiedName();
		}

		int length = f.getCOSObject().getInt(COSName.getPDFName("MaxLen"));
		
		if(length==-1) {
			COSDictionary parentDict = (COSDictionary) f.getCOSObject().getDictionaryObject(COSName.PARENT);
			if(parentDict != null) {
				length = parentDict.getInt(COSName.getPDFName("MaxLen"));
			}
		}
		
		//If this is a text box, all the fields are set fine by default so do nothing
		if(f instanceof PDTextField) {
		}
		//If this is a check box with a parent, we need to get the "on" value and save that as the name 
		if(f instanceof PDCheckBox && parentName != null) {	
			PDCheckBox c =  (PDCheckBox)f;
			name = c.getOnValue();
		}
		//If this is a radio button set, all the fields are set fine by default
		//However, this field type has children, so get those and set their parents properly
		List<MXPDField> children = new ArrayList<MXPDField>();
		/* 
		 * IGNORED
		 */
//		if(f instanceof PDRadioCollection) {
//			List<COSObjectable> kids = f.getKids();
//			if (kids != null) {
//				for (COSObjectable k: kids) {
//					if (k instanceof PDField) {
//						PDField c = (PDField)k;
//						//recurse to create and add children
//						children.add(createMXPDField(c, page));
//					}
//				}
//			}
//		}
		
		MXPDField pf = new MXPDField(name,value,parentName,type,length,page,children);
		return pf;
	}
}

