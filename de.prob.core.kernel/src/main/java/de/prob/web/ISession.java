package de.prob.web;

import java.util.Map;
import java.util.UUID;

// FIXME Reference to Javascript 
/**
 * Interface for views that are session based. Classes that implement this
 * interface can be accessed using the URL
 * http://<IP>:<PORT>/sessions/<FULLY-QUALIFIED-CLASSNAME>. The class is
 * automatically wrapped into a session context. Use this Interface togehter
 * with a long polling on the JavaScript side.
 * 
 * 
 * @author bendisposto
 * 
 */
public interface ISession {

	/**
	 * This method is used for AJAX requests. Ajax request must set a parameter
	 * called ajax to true. The impementation should return a Future containing
	 * an Object that can be converted to JSON using Google's GSON libarray.
	 * 
	 * @see https://code.google.com/p/google-gson/
	 * 
	 * @param parameterMap
	 * @return
	 */
	void command(Map<String, String[]> parameterMap);

	/**
	 * This method is used for HTML Requests, e g., for the initial loading of
	 * the view or for reloading. The implementation must return a String
	 * containing valid HTML.
	 * 
	 * @param parameterMap
	 * @return
	 */
	String html(Map<String, String[]> parameterMap);

	/**
	 * returns the UUID associated with this instance. AbstractSession offers a
	 * default implementation
	 * 
	 * @return
	 */
	UUID getUuid();

	Object[] updatesSince(int lastinfo);

}
