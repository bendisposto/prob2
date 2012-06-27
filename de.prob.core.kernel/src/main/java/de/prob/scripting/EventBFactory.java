package de.prob.scripting;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eventb.emf.core.CorePackage;
import org.eventb.emf.core.Project;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.animator.command.LoadBProjectCommand;
import de.prob.animator.command.LoadEventBCommand;
import de.prob.animator.command.notImplemented.StartAnimationCommand;
import de.prob.model.eventb.EventBModel;

public class EventBFactory {

	private final Provider<EventBModel> modelProvider;
	private final Pattern p1 = Pattern
			.compile("^model\\(\\\"(.*?)\\\"\\)\\.");
	private final Pattern p2 = Pattern
			.compile("^package\\((.*?)\\)\\.");

	@Inject
	public EventBFactory(final Provider<EventBModel> modelProvider) {
		this.modelProvider = modelProvider;
	}

	CorePackage f = CorePackage.eINSTANCE; // As a side effect the EMF stuff is
											// initialized! Hurray

	public EventBModel load(final String s) throws IOException {
		EventBModel eventBModel = modelProvider.get();
		byte[] bytes = Base64.decodeBase64(s.getBytes());
		XMLResourceImpl r2 = new XMLResourceImpl();
		r2.load(new ByteArrayInputStream(bytes), new HashMap<Object, Object>());
		Project p = (Project) r2.getContents().get(0);
		eventBModel.initialize(p);
		return eventBModel;
	}

	public EventBModel load(final File f) throws IOException {
		List<String> lines = readFile(f);
		String loadcmd = null, emfmodel = null;
		for (String string : lines) {
			Matcher m1 = p1.matcher(string);
			Matcher m2 = p2.matcher(string);
			if (m1.find()) {
				emfmodel = m1.group(1);
			} else if (m2.find()) {
				loadcmd = m2.group(1);;
			}
		}
		EventBModel res = load(emfmodel);
		
		res.getStatespace().execute(new LoadEventBCommand(loadcmd));
		res.getStatespace().execute(new StartAnimationCommand());
		
		
		return res;

	}

	public final List<String> readFile(final File machine)
 throws IOException {
		ArrayList<String> res = new ArrayList<String>();
		FileInputStream fstream = new FileInputStream(machine);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					fstream));
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.trim().isEmpty())
					res.add(line);
			}
			return res;
		} finally {
			fstream.close();
		}
	}

}
