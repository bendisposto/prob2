package de.prob.web;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;

import javax.servlet.AsyncContext;

import de.prob.web.data.SessionResult;

// FIXME Reference to Javascript 
/**
 * Interface for views that are session based. Classes that implement this
 * interface can be accessed using the URL
 * http://<IP>:<PORT>/sessions/<FULLY-QUALIFIED-CLASSNAME>. The class is
 * automatically wrapped into a session context. Use this Interface together
 * with a long polling on the JavaScript side.
 * 
 * In most cases you want to use the default implementation
 * {@link AbstractSession}.
 * 
 * 
 * @author bendisposto
 * 
 */
public interface ISession {

	/**
	 * This method is used for AJAX requests.It gets the map of parameters from
	 * the request as the input and should return a Callable that produces an
	 * instance of {@link SessionResult}. This is basically a tuple containing
	 * the session object a result object which can be serialized by GSON.
	 * 
	 * The Callable will be submitted to a {@link CompletionService}. After the
	 * result is available the result object will be sent to all clients of the
	 * session.
	 * 
	 * @see https://code.google.com/p/google-gson/
	 * 
	 * @param parameterMap
	 * @param results
	 * @return
	 */
	Callable<SessionResult> command(Map<String, String[]> parameterMap);

	/**
	 * This method is used for HTML Requests, e g., for the initial loading of
	 * the view or for reloading. The implementation must return a String
	 * containing valid HTML.
	 * 
	 * @param id
	 * 
	 * @param parameterMap
	 * @return
	 */
	String html(String id, Map<String, String[]> parameterMap);

	/**
	 * returns the UUID associated with this session. AbstractSession offers a
	 * default implementation
	 * 
	 * @return
	 */
	UUID getSessionUUID();

	/**
	 * Send all pending updates.
	 * 
	 * @param client
	 *            ID of the browser
	 * @param lastinfo
	 * @param context
	 */
	void sendPendingUpdates(String client, int lastinfo, AsyncContext context);

	void submit(Object... result);

	int getResponseCount();

	void reload(String client, int lastinfo, AsyncContext context);

}
