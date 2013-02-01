/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.handler;

import java.net.MalformedURLException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class OpenWebsiteHandler extends AbstractHandler {

	private static final String URL = "http://www.stups.uni-duesseldorf.de/BMotionStudio/";

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
					.openURL(new java.net.URL(URL));
		} catch (PartInitException e) {
			//TODO: Handle exceptions!!!
			// final String message = "Part init exception occurred\n"
			// + e.getLocalizedMessage();
		} catch (MalformedURLException e) {
			// final String message =
			// "This really should never happen unless the http protocol changes";
		}
		return null;
	}

}
