// This file was generated by Mendix Modeler.
//
// WARNING: Code you write here will be lost the next time you deploy the project.

package pdf_exporter.proxies;

public class AnyObject
{
	private final com.mendix.systemwideinterfaces.core.IMendixObject anyObjectMendixObject;

	private final com.mendix.systemwideinterfaces.core.IContext context;

	/**
	 * Internal name of this entity
	 */
	public static final java.lang.String entityName = "PDF_Exporter.AnyObject";

	/**
	 * Enum describing members of this entity
	 */
	public enum MemberNames
	{
		AnyObject_PDFTemplate("PDF_Exporter.AnyObject_PDFTemplate");

		private java.lang.String metaName;

		MemberNames(java.lang.String s)
		{
			metaName = s;
		}

		@Override
		public java.lang.String toString()
		{
			return metaName;
		}
	}

	public AnyObject(com.mendix.systemwideinterfaces.core.IContext context)
	{
		this(context, com.mendix.core.Core.instantiate(context, "PDF_Exporter.AnyObject"));
	}

	protected AnyObject(com.mendix.systemwideinterfaces.core.IContext context, com.mendix.systemwideinterfaces.core.IMendixObject anyObjectMendixObject)
	{
		if (anyObjectMendixObject == null)
			throw new java.lang.IllegalArgumentException("The given object cannot be null.");
		if (!com.mendix.core.Core.isSubClassOf("PDF_Exporter.AnyObject", anyObjectMendixObject.getType()))
			throw new java.lang.IllegalArgumentException("The given object is not a PDF_Exporter.AnyObject");

		this.anyObjectMendixObject = anyObjectMendixObject;
		this.context = context;
	}

	/**
	 * @deprecated Use 'AnyObject.load(IContext, IMendixIdentifier)' instead.
	 */
	@Deprecated
	public static pdf_exporter.proxies.AnyObject initialize(com.mendix.systemwideinterfaces.core.IContext context, com.mendix.systemwideinterfaces.core.IMendixIdentifier mendixIdentifier) throws com.mendix.core.CoreException
	{
		return pdf_exporter.proxies.AnyObject.load(context, mendixIdentifier);
	}

	/**
	 * Initialize a proxy using context (recommended). This context will be used for security checking when the get- and set-methods without context parameters are called.
	 * The get- and set-methods with context parameter should be used when for instance sudo access is necessary (IContext.getSudoContext() can be used to obtain sudo access).
	 */
	public static pdf_exporter.proxies.AnyObject initialize(com.mendix.systemwideinterfaces.core.IContext context, com.mendix.systemwideinterfaces.core.IMendixObject mendixObject)
	{
		if (com.mendix.core.Core.isSubClassOf("SimpleOrderModule.Order", mendixObject.getType()))
			return simpleordermodule.proxies.Order.initialize(context, mendixObject);

		return new pdf_exporter.proxies.AnyObject(context, mendixObject);
	}

	public static pdf_exporter.proxies.AnyObject load(com.mendix.systemwideinterfaces.core.IContext context, com.mendix.systemwideinterfaces.core.IMendixIdentifier mendixIdentifier) throws com.mendix.core.CoreException
	{
		com.mendix.systemwideinterfaces.core.IMendixObject mendixObject = com.mendix.core.Core.retrieveId(context, mendixIdentifier);
		return pdf_exporter.proxies.AnyObject.initialize(context, mendixObject);
	}

	public static java.util.List<? extends pdf_exporter.proxies.AnyObject> load(com.mendix.systemwideinterfaces.core.IContext context, java.lang.String xpathConstraint) throws com.mendix.core.CoreException
	{
		java.util.List<pdf_exporter.proxies.AnyObject> result = new java.util.ArrayList<pdf_exporter.proxies.AnyObject>();
		for (com.mendix.systemwideinterfaces.core.IMendixObject obj : com.mendix.core.Core.retrieveXPathQuery(context, "//PDF_Exporter.AnyObject" + xpathConstraint))
			result.add(pdf_exporter.proxies.AnyObject.initialize(context, obj));
		return result;
	}

	/**
	 * Commit the changes made on this proxy object.
	 */
	public final void commit() throws com.mendix.core.CoreException
	{
		com.mendix.core.Core.commit(context, getMendixObject());
	}

	/**
	 * Commit the changes made on this proxy object using the specified context.
	 */
	public final void commit(com.mendix.systemwideinterfaces.core.IContext context) throws com.mendix.core.CoreException
	{
		com.mendix.core.Core.commit(context, getMendixObject());
	}

	/**
	 * Delete the object.
	 */
	public final void delete()
	{
		com.mendix.core.Core.delete(context, getMendixObject());
	}

	/**
	 * Delete the object using the specified context.
	 */
	public final void delete(com.mendix.systemwideinterfaces.core.IContext context)
	{
		com.mendix.core.Core.delete(context, getMendixObject());
	}
	/**
	 * @return value of AnyObject_PDFTemplate
	 */
	public final pdf_exporter.proxies.PDFTemplate getAnyObject_PDFTemplate() throws com.mendix.core.CoreException
	{
		return getAnyObject_PDFTemplate(getContext());
	}

	/**
	 * @param context
	 * @return value of AnyObject_PDFTemplate
	 */
	public final pdf_exporter.proxies.PDFTemplate getAnyObject_PDFTemplate(com.mendix.systemwideinterfaces.core.IContext context) throws com.mendix.core.CoreException
	{
		pdf_exporter.proxies.PDFTemplate result = null;
		com.mendix.systemwideinterfaces.core.IMendixIdentifier identifier = getMendixObject().getValue(context, MemberNames.AnyObject_PDFTemplate.toString());
		if (identifier != null)
			result = pdf_exporter.proxies.PDFTemplate.load(context, identifier);
		return result;
	}

	/**
	 * Set value of AnyObject_PDFTemplate
	 * @param anyobject_pdftemplate
	 */
	public final void setAnyObject_PDFTemplate(pdf_exporter.proxies.PDFTemplate anyobject_pdftemplate)
	{
		setAnyObject_PDFTemplate(getContext(), anyobject_pdftemplate);
	}

	/**
	 * Set value of AnyObject_PDFTemplate
	 * @param context
	 * @param anyobject_pdftemplate
	 */
	public final void setAnyObject_PDFTemplate(com.mendix.systemwideinterfaces.core.IContext context, pdf_exporter.proxies.PDFTemplate anyobject_pdftemplate)
	{
		if (anyobject_pdftemplate == null)
			getMendixObject().setValue(context, MemberNames.AnyObject_PDFTemplate.toString(), null);
		else
			getMendixObject().setValue(context, MemberNames.AnyObject_PDFTemplate.toString(), anyobject_pdftemplate.getMendixObject().getId());
	}

	/**
	 * @return the IMendixObject instance of this proxy for use in the Core interface.
	 */
	public final com.mendix.systemwideinterfaces.core.IMendixObject getMendixObject()
	{
		return anyObjectMendixObject;
	}

	/**
	 * @return the IContext instance of this proxy, or null if no IContext instance was specified at initialization.
	 */
	public final com.mendix.systemwideinterfaces.core.IContext getContext()
	{
		return context;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;

		if (obj != null && getClass().equals(obj.getClass()))
		{
			final pdf_exporter.proxies.AnyObject that = (pdf_exporter.proxies.AnyObject) obj;
			return getMendixObject().equals(that.getMendixObject());
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return getMendixObject().hashCode();
	}

	/**
	 * @return String name of this class
	 */
	public static java.lang.String getType()
	{
		return "PDF_Exporter.AnyObject";
	}

	/**
	 * @return String GUID from this object, format: ID_0000000000
	 * @deprecated Use getMendixObject().getId().toLong() to get a unique identifier for this object.
	 */
	@Deprecated
	public java.lang.String getGUID()
	{
		return "ID_" + getMendixObject().getId().toLong();
	}
}
