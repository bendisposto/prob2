package de.prob.bmotion

import javax.script.Bindings
import javax.script.ScriptContext

import com.google.common.base.Function
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonParser

import de.prob.model.representation.AbstractModel
import de.prob.scripting.Api
import de.prob.scripting.GroovySE
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

	def void evaluateGroovy(GroovySE evaluator, String templatePath, Map<String, String> parameters, BMotionStudioSession bms) {
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

	def static ITool loadTool(String templatePath, String modelPath, Api api, AnimationSelector animations, ToolRegistry toolRegistry) {
		ITool tool = null
		if (modelPath != null) {
			String formalism = BMotionUtil.getFormalism(modelPath)
			def path = BMotionUtil.getTemplateFolder(templatePath) + File.separator +  modelPath
			AbstractModel model = Eval.x(api, "x.${formalism}_load('$path')")
			if(model.getFormalismType() == FormalismType.B) {
				return new BAnimation(model, animations, toolRegistry);
			} else if (model.getFormalismType() == FormalismType.CSP) {
				return new CSPAnimation(model, animations, toolRegistry)
			}
		} else {
			def Trace trace = animations.getCurrentTrace()
			if(trace != null) {
				if(trace.getModel().getFormalismType() == FormalismType.B) {
					return new BAnimation(trace, animations, toolRegistry)
				} else if(trace.getModel().getFormalismType() == FormalismType.CSP) {
					return new CSPAnimation(trace, animations)
				}
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
}
