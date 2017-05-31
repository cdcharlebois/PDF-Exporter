# PDF-Exporter

A Mendix module for filling form-enabled PDF files using data in a Mendix application. A test project is included with sample data.

## Dependencies

This module relies on:
- **Community Commons**
- **Model Reflection**

## Configuration

This module works in a very similar way to the Excel Exporter module. The general concept is:
 - configure an output mapping at runtime
 - use the BuildPDF java action with an input entity and this output mapping to genereate a PDF file

 In the Model, you can make your base entity (the one from which you will pull the data to merge into the PDF) a specialization of `PDF_Exporter.AnyObject` and then show the **Template_Select** form to trigger the document generation process.

## Mapping

When you create a new mapping, you'll specify a form-enabled PDF document and the intended input entity, then click "Start Mapping"

The module will read your PDF file, and overlay the PDF field names on top of your document. On the page that appears, you'll see each field found in the PDF on the left, and the document itself (with overlaid field names) on the right side.

For each field that you'd like to map, double click on the field from the list (you can search on field name if the list is large), and map the attribute you want to load into that field.

### Static mapping

The module supports two mapping types: static and dynamic. Static mapping is simply "hard-coded" text that you want to place into a PDF field. When mapping, select static, and enter the text you wish to use. Enter "true" or "false" to populate a checkbox field correctly.

### Dynamic mapping

Dynamic mapping is the other option. This is the one to use if you want to insert Mendix entity data into your PDF file. You can either select attributes direct from your input entity, or over an association form another entity. You can follow associations up to 5 layers deep if neccessary.
