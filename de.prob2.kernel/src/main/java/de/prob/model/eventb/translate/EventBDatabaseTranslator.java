package de.prob.model.eventb.translate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eventb.core.ast.extension.IFormulaExtension;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.AbstractElement;

public class EventBDatabaseTranslator {

	private AbstractElement mainComponent;

	public EventBDatabaseTranslator(final EventBModel model,
			final String fileName) throws FileNotFoundException {
		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = parserFactory.newSAXParser();

			File modelFile = new File(fileName);
			String fullFileName = modelFile.getAbsolutePath();
			model.setModelFile(modelFile);

			String directory = fullFileName.substring(0,
					fullFileName.lastIndexOf(File.separatorChar));
			String workspacePath = directory.substring(0,
					fullFileName.lastIndexOf(File.separatorChar));

			File theoryFile = new File(directory + File.separator
					+ "TheoryPath.tcl");
			Set<IFormulaExtension> typeEnv;
			if (!theoryFile.exists()) {
				typeEnv = new HashSet<IFormulaExtension>();
			} else {
				TheoryXmlHandler theoryHandler = new TheoryXmlHandler(model,
						workspacePath);
				saxParser.parse(theoryFile, theoryHandler);
				typeEnv = theoryHandler.getTypeEnv();
			}

			DefaultHandler xmlHandler = null;
			mainComponent = null;
			if (fileName.endsWith(".bcc")) {
				xmlHandler = new ContextXmlHandler(model, fullFileName,
						typeEnv);
				mainComponent = ((ContextXmlHandler) xmlHandler).getContext();
			} else {
				xmlHandler = new MachineXmlHandler(model, fullFileName,
						typeEnv);
				mainComponent = ((MachineXmlHandler) xmlHandler).getMachine();
			}

			saxParser.parse(modelFile, xmlHandler);

			model.isFinished();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			if (e instanceof FileNotFoundException) {
				throw (FileNotFoundException) e;
			}
		}
	}
	
	public AbstractElement getMainComponent() {
		return mainComponent;
	}
}
