package de.prob.ui.worksheet.editors.browserFunction;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

import de.prob.ui.worksheet.editors.Worksheet;

public class domReady extends BrowserFunction {
	//private Logger logger=LoggerFactory.getLogger(domReady.class);
	private Worksheet editor;
	
	public domReady(Browser browser, String name,Worksheet editor) {
		super(browser, name);
		this.editor=editor;
	}
	@Override
	public Object function(Object[] arguments) {
		
		//logger.debug("java domReady function called from javascript");
		//TODO add method to Editor for the case of a browser refresh
		System.out.println(editor.isWorksheetLoaded());
		if(editor.isWorksheetLoaded()){
			this.getBrowser().execute("refreshDocument("+editor.getSubSessionId()+");");
		}else if(editor.isNewDocument()){
			this.getBrowser().execute("newDocument("+editor.getSubSessionId()+");");
		}else{
			String content=editor.getInitialContent();
			content = content.replace("'", "\'");
			content = content.replace("\n", "\\n");
			content = content.replace("\r", "\\r");
			this.getBrowser().execute("var contentx='" + content + "';loadDocument(contentx," + editor.getSubSessionId() + ");");
		}
		return super.function(arguments);
	}

}
