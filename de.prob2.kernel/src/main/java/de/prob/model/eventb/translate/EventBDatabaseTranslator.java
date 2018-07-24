package de.prob.model.eventb.translate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.AbstractElement;

import org.eventb.core.ast.extension.IFormulaExtension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.xml.sax.SAXException;

public class EventBDatabaseTranslator {
	private static final Logger logger = LoggerFactory.getLogger(EventBDatabaseTranslator.class);

	private AbstractElement mainComponent;
	private EventBModel model;

	public EventBDatabaseTranslator(EventBModel m,
			final String fileName) throws FileNotFoundException {
		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = parserFactory.newSAXParser();

			File modelFile = new File(fileName);
			String fullFileName = modelFile.getAbsolutePath();
			this.model = m.setModelFile(modelFile);

			String directory = fullFileName.substring(0,
					fullFileName.lastIndexOf(File.separatorChar));
			String workspacePath = directory.substring(0,
					directory.lastIndexOf(File.separatorChar));

			File theoryFile = new File(directory + File.separator
					+ "TheoryPath.tcl");
			Set<IFormulaExtension> typeEnv;
			if (!theoryFile.exists()) {
				typeEnv = new HashSet<>();
			} else {
				TheoryXmlHandler theoryHandler = new TheoryXmlHandler(this.model,
						workspacePath);
				saxParser.parse(theoryFile, theoryHandler);
				typeEnv = theoryHandler.getTypeEnv();
				this.model = theoryHandler.getModel();
			}

			mainComponent = null;
			if (fileName.endsWith(".bcc")) {
				final ContextXmlHandler xmlHandler = new ContextXmlHandler(this.model, fullFileName, typeEnv);
				saxParser.parse(modelFile, xmlHandler);
				mainComponent = xmlHandler.getContext();
				this.model = xmlHandler.getModel();
			} else {
				final MachineXmlHandler xmlHandler = new MachineXmlHandler(this.model, fullFileName, typeEnv);
				saxParser.parse(modelFile, xmlHandler);
				mainComponent = xmlHandler.getMachine();
				this.model = xmlHandler.getModel();
			}
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException | ParserConfigurationException | SAXException e) {
			logger.error("Error during EventB translation", e);
		}
	}

	public AbstractElement getMainComponent() {
		return mainComponent;
	}

	public EventBModel getModel() {
		return model;
	}
}
