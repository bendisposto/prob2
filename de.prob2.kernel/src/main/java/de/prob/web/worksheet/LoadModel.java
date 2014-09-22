package de.prob.web.worksheet;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.google.common.io.Files;

import de.prob.animator.command.EvalstoreCreateByStateCommand;
import de.prob.model.representation.AbstractModel;
import de.prob.statespace.StateSpace;
import de.prob.web.FileBrowserServlet;

public class LoadModel extends AbstractBox {

	private String content;

	public LoadModel() {
	}

	@Override
	public void setContent(final Map<String, String[]> data) {
		content = data.get("text")[0];
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> render(final BindingsSnapshot snapshot) {
		File file = new File(content);
		if (!FileBrowserServlet.validFile(file, "prob")) {
			return pack(makeHtml(id,
					"<span class='illegal_file'> Not a valid ProB file. </span>"));
		} else {
			String name = load_file(file.getAbsolutePath());
			return pack(makeHtml(id, "<b>" + content
					+ " has been loaded and stored in " + name + " </b>"));
		}
	}

	private String load_file(final String filename) {
		ScriptEngine groovy = owner.getGroovy();
		String command = "";
		String name = "model";
		String extension = Files.getFileExtension(filename);
		if (extension.equals("mch") || extension.equals("ref")
				|| extension.equals("imp")) {
			command = "api.b_load('" + filename + "')";
		}
		if (extension.equals("eventb")) {
			command = "api.eb_load('" + filename + "')";
		}
		if (extension.equals("csp")) {
			command = "api.csp_load('" + filename + "')";
		}
		if (!command.equals("")) {
			try {
				AbstractModel result = (AbstractModel) groovy.eval(command);
				StateSpace statespace = result.getStateSpace();
				EvalstoreCreateByStateCommand c = new EvalstoreCreateByStateCommand(
						"root");
				statespace.execute(c);
				long store = c.getEvalstoreId();

				Bindings bindings = groovy
						.getBindings(ScriptContext.GLOBAL_SCOPE);
				bindings.put(name, result);
				bindings.put("store", store);
				return name;
			} catch (ScriptException e) {
				e.printStackTrace();
			}
		}
		return "ERROR";
	}

	@Override
	protected String getContentAsJson() {
		return content;
	}

	@Override
	protected String getTemplate() {
		return "/ui/worksheet/boxes/load_model.html";
	}

	@Override
	protected boolean useCodemirror() {
		return false;
	}

	@Override
	public EChangeEffect changeEffect() {
		return EChangeEffect.EVERYTHING_BELOW;
	}

}
