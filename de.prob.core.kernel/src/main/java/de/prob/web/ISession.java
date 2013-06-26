package de.prob.web;

import java.util.Map;
import java.util.UUID;

import com.google.common.util.concurrent.ListenableFuture;

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
	ListenableFuture<Object> requestJson(Map<String, String[]> parameterMap);

	/**
	 * This method is used for HTML Requests, e g., for the initial loading of
	 * the view or for reloading. The implementation must return a String
	 * containing valid HTML.
	 * 
	 * @param parameterMap
	 * @return
	 */
	String requestHtml(Map<String, String[]> parameterMap);

	/**
	 * Will be called when producing the object. AbstractSession offers a
	 * default implementation for this method.
	 * 
	 * @param id
	 */
	void setUuid(UUID id);

	/**
	 * returns the UUID associated with this instance. AbstractSession offers a
	 * default implementation
	 * 
	 * @return
	 */
	UUID getUuid();

}
