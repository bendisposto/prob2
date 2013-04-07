package de.prob.rodin.translate;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eventb.core.IEventBProject;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCExtendsContext;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCRefinesMachine;
import org.rodinp.core.RodinDBException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class EventBRootSerializer implements JsonSerializer<IEventBRoot> {

	Map<String, ISCMachineRoot> machines = new HashMap<String, ISCMachineRoot>();
	Map<String, ISCContextRoot> contexts = new HashMap<String, ISCContextRoot>();
	private IEventBProject eventBProject;

	@Override
	public JsonElement serialize(final IEventBRoot src, final Type typeOfSrc,
			final JsonSerializationContext context) {
		JsonArray serialized = new JsonArray();

		eventBProject = src.getEventBProject();
		String id = src.getElementType().getId();
		if (id.equals("org.eventb.core.machineFile")) {
			serialized.add(new JsonPrimitive("EventBMachine"));

			ISCMachineRoot scMachineRoot = eventBProject.getSCMachineRoot(src
					.getElementName());

			serialized
					.add(new JsonPrimitive(extractMachineName(scMachineRoot)));

			visitMachine(scMachineRoot);
		}
		if (id.equals("org.eventb.core.contextFile")) {
			serialized.add(new JsonPrimitive("Context"));

			ISCContextRoot scContextRoot = eventBProject.getSCContextRoot(src
					.getElementName());

			serialized
					.add(new JsonPrimitive(extractContextName(scContextRoot)));

			visitContext(scContextRoot);
		}

		serialized.add(serializeMachines(machines.values(), context));
		serialized.add(serializeContexts(contexts.values(), context));

		File modelFile = src.getUnderlyingResource().getRawLocation().toFile();
		serialized.add(new JsonPrimitive(modelFile.getAbsolutePath()));
		return serialized;
	}

	private JsonArray serializeMachines(
			final Collection<ISCMachineRoot> values,
			final JsonSerializationContext context) {
		JsonArray serialized = new JsonArray();

		MachineRootSerializer serializer = new MachineRootSerializer();
		for (ISCMachineRoot iscMachineRoot : values) {
			serialized.add(serializer.serialize(iscMachineRoot,
					ISCMachineRoot.class, context));
		}

		return serialized;
	}

	private JsonArray serializeContexts(
			final Collection<ISCContextRoot> values,
			final JsonSerializationContext context) {
		JsonArray serialized = new JsonArray();

		ContextRootSerializer serializer = new ContextRootSerializer();
		for (ISCContextRoot iscContextRoot : values) {
			serialized.add(serializer.serialize(iscContextRoot,
					ISCContextRoot.class, context));
		}

		return serialized;
	}

	private String extractMachineName(final ISCMachineRoot scMachineRoot) {
		return scMachineRoot.getComponentName();
	}

	private String extractContextName(final ISCContextRoot scContextRoot) {
		return scContextRoot.getComponentName();
	}

	public void visitMachine(final ISCMachineRoot root) {
		String name = root.getComponentName();
		if (machines.containsKey(name)) {
			return;
		}

		try {
			for (ISCRefinesMachine iscRefinesMachine : root
					.getSCRefinesClauses()) {
				String bareName = iscRefinesMachine.getAbstractSCMachine()
						.getBareName();
				visitMachine(eventBProject.getSCMachineRoot(bareName));
			}

			for (ISCInternalContext iscInternalContext : root
					.getSCSeenContexts()) {
				String bareName = iscInternalContext.getComponentName();
				visitContext(eventBProject.getSCContextRoot(bareName));
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void visitContext(final ISCContextRoot root) {
		String name = root.getComponentName();
		if (contexts.containsKey(name)) {
			return;
		}

		try {
			for (ISCExtendsContext iscExtendsContext : root
					.getSCExtendsClauses()) {
				String compName = iscExtendsContext.getAbstractSCContext()
						.getRodinFile().getBareName();
				visitContext(eventBProject.getSCContextRoot(compName));
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
		}

	}

}
