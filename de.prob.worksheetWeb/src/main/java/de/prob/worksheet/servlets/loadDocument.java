package de.prob.worksheet.servlets;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.prob.worksheet.WorksheetDocument;

@WebServlet(urlPatterns={"/loadDocument"})
public class loadDocument extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7762787871923711945L;
	Logger logger = LoggerFactory.getLogger(loadDocument.class);


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		/*try {
			testSchema();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		this.logParameters(req);
		resp.setCharacterEncoding("UTF-8");
		
		// initialize the session
		this.setSessionProperties(req.getSession());

		// Load the session attibutes
		HashMap<String, Object> attributes = this.getSessionAttributes(req.getSession(), req.getParameter("worksheetSessionId"));

		
		// load or create the document
		WorksheetDocument doc = this.loadDocumentFromXml(attributes,req.getParameter("documentXML"));
		attributes = new HashMap<String, Object>();
		attributes.put("document", doc);

			
		
		// store the session attributes
		this.setSessionAttributes(req.getSession(), req.getParameter("worksheetSessionId"), attributes);

		// print the json string to the response
		final ObjectMapper mapper = new ObjectMapper();
		resp.setStatus(HttpServletResponse.SC_ACCEPTED);
		//FIXME sometimes a OutOfMemoryError:PermGen space occur (Bug 1000)
		resp.getWriter().print(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(doc));
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object> getSessionAttributes(HttpSession session, String wsid) {
		HashMap<String, Object> attributes = (HashMap<String, Object>) session.getAttribute(wsid);
		if (attributes == null){
			attributes = new HashMap<String, Object>();
			logger.debug("New 'Sub'session initialized with id  :"+wsid);
		}
		logger.debug("Session attributes: "+attributes.toString());
		return attributes;
	}

	private void setSessionAttributes(HttpSession session, String wsid, HashMap<String, Object> attributes) {
		logger.debug("Session attributes: "+attributes.toString());
		session.setAttribute(wsid, attributes);
	}

	private void setSessionProperties(HttpSession session) {
		if (session.isNew()) {
			logger.debug("New Session initialized");
			session.setMaxInactiveInterval(-1);
		}
	}
	
	private WorksheetDocument loadDocumentFromXml(HashMap<String, Object> attributes,String documentXML) {
		WorksheetDocument doc = (WorksheetDocument) attributes.get("document");
		if (doc != null) {
			logger.warn("Document has already been loaded for this editor");
		}
		StringReader reader=new StringReader(documentXML);
		doc=JAXB.unmarshal(reader, WorksheetDocument.class);
		return doc;

	}
	private void logParameters(HttpServletRequest req){
		String[] params={"worksheetSessionId","documentXML"};
		String msg="{ ";
		for(int x=0;x<params.length;x++){
			if(x!=0)msg+=" , ";
			msg+=params[x]+" : "+req.getParameter(params[x]);
		}
		msg+=" }";
		logger.debug(msg);
		
	}
	
	public void testSchema() throws  IOException, JAXBException
	{
		// stolen from http://arthur.gonigberg.com/2010/04/26/jaxb-generating-schema-from-object-model/
	    // grab the context
	    JAXBContext context = JAXBContext.newInstance( WorksheetDocument.class );

	    String out;
	    final StringWriter writer=new StringWriter();
	    // generate the schema
	    context.generateSchema(
	            // need to define a SchemaOutputResolver to store to
	            new SchemaOutputResolver() {
				
	                @Override
	                public Result createOutput( String ns, String file )
	                        throws IOException
	                {
	                    // save the schema to the list
	                    StreamResult res=new StreamResult(writer);
	                    res.setSystemId("no-id");
	                    return res;
	                }
	            } );
    	System.out.println(writer.toString());

	}
}
