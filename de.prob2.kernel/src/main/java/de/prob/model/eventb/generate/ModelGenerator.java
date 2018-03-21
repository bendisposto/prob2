package de.prob.model.eventb.generate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.be4.eventbalg.core.parser.BException;
import de.be4.eventbalg.core.parser.EventBParser;
import de.be4.eventbalg.core.parser.node.Start;

import de.prob.Main;
import de.prob.model.eventb.EventBModel;
import de.prob.model.eventb.ModelModifier;
import de.prob.scripting.StateSpaceProvider;

public class ModelGenerator {

	private ModelModifier modelM;

	public ModelGenerator(final String path, final String projectName)
			throws IOException, BException {
		EventBModel model = new EventBModel(Main.getInjector().getInstance(
				StateSpaceProvider.class));
		File file = new File(path);
		checkFile(file, true);
		ModelModifier modelM = extractTheories(model, path);

		File[] files = file.listFiles((dir, name) -> {
			String lowercaseName = name.toLowerCase();
			return lowercaseName.endsWith(".emch")
				|| lowercaseName.endsWith(".ctx")
				|| lowercaseName.endsWith(".procedure");
		});
		Map<String, String> components = new HashMap<>();
		if (files != null) {
			for (File f : files) {
				checkFile(f, false);
				String text = readFile(f);
				components.put(f.getName(), text);
			}
		}
		this.modelM = addComponents(modelM, components);
	}

	@SuppressWarnings("unchecked")
	private ModelModifier extractTheories(final EventBModel model, final String path) throws IOException {
		File theoryPath = new File(path + File.separator + "TheoryPath.json");
		if (!theoryPath.exists()) {
			return new ModelModifier(model);
		}
		ModelModifier mm = new ModelModifier(model);
		String file = readFile(theoryPath);
		Gson gson = new Gson();
		Type type = new TypeToken<Map<String, List<String>>>() {}.getType();
		Map<String, List<String>> fromJson = gson.fromJson(file, type);
		for (Map.Entry<String, List<String>> theory : fromJson.entrySet()) {
			LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
			properties.put("workspace", path);
			properties.put("project", theory.getKey());
			properties.put("theories", theory.getValue());
			mm = mm.loadTheories(properties);
		}

		return mm;
	}

	public String readFile(final File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			return sb.toString();
		} finally {
			br.close();
		}
	}

	public void checkFile(final File file, final boolean directory)
			throws IOException {
		if (!file.exists()) {
			throw new FileNotFoundException(file.getAbsolutePath());
		} else if (file.isDirectory() != directory) {
			String expected = directory ? "directory" : "file";
			String was = directory ? "file" : "directory";
			throw new IOException("Expected " + file.getAbsolutePath()
					+ " to be a " + expected + " but was a " + was);
		}
	}

	public EventBModel getModel() {
		return modelM.getModel();
	}

	private ModelModifier addComponents(ModelModifier modelM,
			Map<String, String> components) throws BException {
		for (Map.Entry<String, String> e : components.entrySet()) {
			String name = e.getKey();
			if (name.endsWith(".emch")) {
				name = name.substring(0, name.length() - 5);
			} else if (name.endsWith(".ctx")) {
				name = name.substring(0, name.length() - 4);
			} else if (name.endsWith(".procedure")) {
				name = name.substring(0, name.length() - 10);
				modelM = addComponent(modelM, name, e.getValue(), components);
				continue;
			}
			if (modelM.getModel().getComponent(name) == null) {
				modelM = addComponent(modelM, name, e.getValue(), components);
			}
		}
		return modelM;
	}

	private ModelModifier addComponent(ModelModifier modelM, String name,
			String componentDesc, Map<String, String> components)
					throws BException {
		EventBParser parser = new EventBParser();
		Start ast = parser.parse(componentDesc, false);
		ReferenceExtractor e = new ReferenceExtractor();
		ast.apply(e);
		if (e.isContext()) {
			for (String s : e.getExtends()) {
				String fileN = s + ".ctx";
				if (modelM.getModel().getComponent(fileN) == null) {
					if (components.get(fileN) == null) {
						throw new IllegalArgumentException(
								"no component description for context " + s);
					}
					modelM = addComponent(modelM, s, components.get(fileN),
							components);
				}
			}
			modelM = addComponent(modelM, ast);
		} else if (e.isMachine()) {
			for (String s : e.getSees()) {
				String fileN = s + ".ctx";
				if (modelM.getModel().getComponent(fileN) == null) {
					if (components.get(fileN) == null) {
						throw new IllegalArgumentException(
								"no component description for context " + s);
					}
					modelM = addComponent(modelM, s, components.get(fileN),
							components);
				}
			}
			for (String s : e.getRefines()) {
				String fileN = s + ".emch";
				if (modelM.getModel().getComponent(fileN) == null) {
					if (components.get(fileN) == null) {
						throw new IllegalArgumentException(
								"no component description for machine " + s);
					}
					modelM = addComponent(modelM, s, components.get(fileN),
							components);
				}
			}
			modelM = addComponent(modelM, ast);
		}
		return modelM;
	}

	public ModelModifier addComponent(final ModelModifier modelM,
			final Start ast) throws BException {
		ComponentExtractor modelE = new ComponentExtractor(modelM);
		ast.apply(modelE);
		return modelE.getModelModifier();
	}
}
