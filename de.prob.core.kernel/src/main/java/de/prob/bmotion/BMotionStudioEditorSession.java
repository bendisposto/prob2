package de.prob.bmotion;

import static java.io.File.separator;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.AsyncContext;

import com.google.inject.Inject;

import de.prob.model.representation.AbstractModel;
import de.prob.scripting.Api;

public class BMotionStudioEditorSession extends AbstractBMotionStudioSession {

	@Inject
	public BMotionStudioEditorSession(Api api) {
		super(api);
	}

	@Override
	public void initSession() {

		Object machinePath = getParameterMap().get("model");

		System.out.println("BMS: Initialise Model " + machinePath);

		// If the template references a specific model, try to load this model
		if (machinePath != null) {

			File machineFile = new File(machinePath.toString());

			if (!machineFile.isAbsolute())
				machinePath = getTemplateFolder() + separator + machinePath;

			String formalism = getFormalism(machinePath.toString());

			if (formalism != null) {

				try {
					Method method = getApi().getClass().getMethod(
							formalism + "_load", String.class);
					setModel((AbstractModel) method.invoke(getApi(),
							machinePath));
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}

		}

	}

	@Override
	public String html(String clientid, Map<String, String[]> parameterMap) {
		return null;
	}

	@Override
	public void reload(String client, int lastinfo, AsyncContext context) {
	}

}
