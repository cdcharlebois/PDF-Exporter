// This file was generated by Mendix Modeler.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package communitycommons.actions;

import communitycommons.StringUtils;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.webui.CustomJavaAction;

/**
 * Performs a regular expression. Identical to the microflow expression funciton 'replaceAll'. Useful to be used from java, or in older Mendix versions. 
 * For the regexp specification see:
 * http://download.oracle.com/javase/1.4.2/docs/api/java/util/regex/Pattern.html
 * 
 * A decent regexp tester can be found at:
 * http://www.fileformat.info/tool/regex.htm
 */
public class RegexReplaceAll extends CustomJavaAction<String>
{
	private String haystack;
	private String needleRegex;
	private String replacement;

	public RegexReplaceAll(IContext context, String haystack, String needleRegex, String replacement)
	{
		super(context);
		this.haystack = haystack;
		this.needleRegex = needleRegex;
		this.replacement = replacement;
	}

	@Override
	public String executeAction() throws Exception
	{
		// BEGIN USER CODE
		return StringUtils.regexReplaceAll(haystack, needleRegex, replacement);
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 */
	@Override
	public String toString()
	{
		return "RegexReplaceAll";
	}

	// BEGIN EXTRA CODE
	// END EXTRA CODE
}
