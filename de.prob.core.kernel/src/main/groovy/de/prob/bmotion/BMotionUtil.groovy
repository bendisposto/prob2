package de.prob.bmotion

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import javax.script.Bindings
import javax.script.ScriptContext
import javax.script.ScriptEngine

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Function
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonParser

import de.prob.model.representation.AbstractModel
import de.prob.scripting.Api
import de.prob.statespace.AnimationSelector
import de.prob.statespace.FormalismType
import de.prob.statespace.Trace
import de.prob.ui.api.ITool
import de.prob.ui.api.ToolRegistry
import de.prob.web.WebUtils

class BMotionUtil {

	def static List<IBMotionScript> createObservers(String templatePath, String jsonPaths, BMotionStudioSession bms) {
		List<IBMotionScript> observers = new ArrayList<IBMotionScript>()
		if (templatePath != null && jsonPaths != null) {
			def templateFolder = BMotionUtil.getTemplateFolder(templatePath)
			Map<String, Object> scope = new HashMap<String, Object>();
			scope.put("eval", new EvalExpression());

			String[] paths = jsonPaths.split(",")
			for (path in paths) {
				String jsonPath = templateFolder + File.separator + path
				File f = new File(jsonPath)
				if (f.exists()) {
					WebUtils.render(f.getPath(), scope);
					String rendered = f.getText()
					JsonParser parser = new JsonParser()
					JsonElement element = parser.parse(rendered)
					if (!(JsonElement instanceof JsonNull)) {
						observers << new Observer(element, bms)
					}
				}
			}
		}
		return observers
	}

	def static void evaluateGroovy(ScriptEngine evaluator, String templatePath, Map<String, String> parameters, BMotionStudioSession bms) {
		String scriptPaths = parameters.get("script")
		if (templatePath != null && scriptPaths != null) {
			def templateFolder = BMotionUtil.getTemplateFolder(templatePath)
			Bindings bindings = evaluator.getBindings(ScriptContext.GLOBAL_SCOPE)
			bindings.putAll(parameters)
			bindings.put("bms", bms)
			bindings.put("templateFolder", templateFolder)

			String[] paths = scriptPaths.split(",")
			for (path in paths) {
				evaluator.eval(new File(templateFolder + File.separator + path).getText(), bindings)
			}
		}
	}

	private static class EvalExpression implements Function<String, Object> {
		@Override
		public Object apply(final String input) {
			try {
				return eval(input.replace("\\\\", "\\"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	def static AbstractModel loadModel(Api api, AnimationSelector animations, String modelPath, String templatePath) {

		def AbstractModel model = null

		if(modelPath != null) {
			String formalism = BMotionUtil.getFormalism(modelPath)
			def path = BMotionUtil.getTemplateFolder(templatePath) + File.separator +  modelPath
			model = Eval.x(api, "x.${formalism}_load('$path')")
		} else {
			def Trace trace = animations.getCurrentTrace()
			if(trace != null) {
				model = trace.getModel();
			}
		}

		return model
	}
	
	def static ITool loadTool(AbstractModel model, AnimationSelector animations, ToolRegistry toolRegistry) {
		ITool tool = null
		if (model != null) {
			if(model.getFormalismType() == FormalismType.B) {
				return new BAnimation(model, animations, toolRegistry);
			} else if (model.getFormalismType() == FormalismType.CSP) {
				return new CSPAnimation(model, animations, toolRegistry)
			}
		}
		return tool
	}

	def static String getFormalism(final String machinePath) {
		String lang = null;
		if (machinePath.endsWith(".csp")) {
			return "csp";
		} else if (machinePath.endsWith(".buc") || machinePath.endsWith(".bcc")
		|| machinePath.endsWith(".bum") || machinePath.endsWith(".bcm")) {
			return "eventb";
		} else if (machinePath.endsWith(".mch")) {
			return "b";
		} else if (machinePath.endsWith(".tla")) {
			return "tla";
		}
		return lang;
	}

	def static String getTemplateFolder(String templatePath) {
		return new File(templatePath).getParent()
	}
	
	
	def static void writeStringToFile(String str, File file) {
		try {
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			FileOutputStream fop = new FileOutputStream(file);
			// get the content in bytes
			byte[] contentInBytes = str.getBytes();
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	def static String readFile(final String filename) {
		String content = null;
		File file = new File(filename);
		try {
			FileReader reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			content = new String(chars);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	def static void fixSvgImageTags(Document template) {
		for (Element e : template.getElementsByTag("svg")) {
			// Workaround, since jsoup renames svg image tags to img
			// tags ...
			Elements imgTags = e.getElementsByTag("img");
			imgTags.tagName("image");
		}
	}
	
	def static String getFullTemplatePath(String templatePath) {
		if (!new File(templatePath).isAbsolute()) {
			String homedir = System.getProperty("bms.home");
			if (homedir != null)
				return templatePath = homedir + templatePath;
			return templatePath = System.getProperty("user.home")
					+ templatePath;
		}
		return templatePath;
	}
	
}
