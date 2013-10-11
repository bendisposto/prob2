package de.prob.model.eventb.translate;

import java.io.File;
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

public class EventBTranslator {

	public EventBTranslator(final EventBModel model, final String fileName) {
		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = parserFactory.newSAXParser();

			File modelFile = new File(fileName);
			model.setModelFile(modelFile);

			String directory = fileName.substring(0, fileName.lastIndexOf('/'));
			String workspacePath = directory.substring(0,
					directory.lastIndexOf('/'));

			File theoryFile = new File(directory + "/TheoryPath.tcl");
			Set<IFormulaExtension> typeEnv;
			if (!theoryFile.exists()) {
				typeEnv = new HashSet<IFormulaExtension>();
			} else {
				TheoryXmlHandler theoryHandler = new TheoryXmlHandler(model,
						workspacePath);
				saxParser.parse(modelFile, theoryHandler);
				typeEnv = theoryHandler.getTypeEnv();
			}

			DefaultHandler xmlHandler = null;
			if (fileName.endsWith(".bcc")) {
				xmlHandler = new ContextXmlHandler(model, fileName, true,
						typeEnv);
			} else {
				xmlHandler = new MachineXmlHandler(model, fileName, true,
						typeEnv);
			}

			saxParser.parse(modelFile, xmlHandler);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
