package de.prob.scripting;

import java.util.Collection;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.model.eventb.Context;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.AbstractElement;

public class EventBFactory {

	private final Provider<EventBModel> modelProvider;
//	private final Pattern p1 = Pattern
//			.compile("^emf_model\\('(.*?)',\\\"(.*?)\\\"\\)\\.");
//	private final Pattern p2 = Pattern.compile("^package\\((.*?)\\)\\.");

	@Inject
	public EventBFactory(final Provider<EventBModel> modelProvider) {
		this.modelProvider = modelProvider;
	}

//	private EventBModel load(final String s, final String mainComponent)
//			throws IOException {
//		EventBModel eventBModel = modelProvider.get();
//		// byte[] bytes = Base64.decodeBase64(s.getBytes());
//		// XMLResourceImpl r2 = new XMLResourceImpl();
//		// r2.load(new ByteArrayInputStream(bytes), new HashMap<Object,
//		// Object>());
//		// eventBModel.initialize(p, mainComponent);
//		return eventBModel;
//	}

//	private EventBModel load(final File f) throws IOException {
//		List<String> lines = readFile(f);
//		String loadcmd = null, emfmodel = null, mainmodel = null;
//		for (String string : lines) {
//			Matcher m1 = p1.matcher(string);
//			Matcher m2 = p2.matcher(string);
//			if (m1.find()) {
//				mainmodel = m1.group(1);
//				emfmodel = m1.group(2);
//			} else if (m2.find()) {
//				loadcmd = m2.group(1);
//			}
//		}
//
//		EventBModel res = load(emfmodel, mainmodel);
//		final ICommand loadcommand = new LoadEventBCommand(loadcmd);
//		res.getStatespace().execute(loadcommand);
//		res.getStatespace().execute(new StartAnimationCommand());
//		res.getStatespace().setLoadcmd(loadcommand);
//
//		return res;
//
//	}

//	public final List<String> readFile(final File machine) throws IOException {
//		ArrayList<String> res = new ArrayList<String>();
//		FileInputStream fstream = new FileInputStream(machine);
//		try {
//			BufferedReader br = new BufferedReader(new InputStreamReader(
//					fstream));
//			String line;
//			while ((line = br.readLine()) != null) {
//				if (!line.trim().isEmpty()) {
//					res.add(line);
//				}
//			}
//			return res;
//		} finally {
//			fstream.close();
//		}
//	}

	public EventBModel load(final AbstractElement mainComponent,
			final Collection<EventBMachine> machines,
			final Collection<Context> contexts) {
		EventBModel model = modelProvider.get();

		model.setMainComponent(mainComponent);
		model.addMachines(machines);
		model.addContexts(contexts);

		model.isFinished();

		return model;
	}
//
//	private void addContexts(final Context context, final Set<Context> contexts) {
//		contexts.add(context);
//
//		Set<Context> extended = context.getChildrenOfType(Context.class);
//		for (Context extend : extended) {
//			addContexts(extend, contexts);
//		}
//	}

//	private void addMachines(final EventBMachine machine,
//			final Set<EventBMachine> machines, final Set<Context> contexts) {
//		machines.add(machine);
//		Set<Context> sees = machine.getChildrenOfType(Context.class);
//		for (Context context : sees) {
//			addContexts(context, contexts);
//		}
//		Set<Machine> refines = machine.getChildrenOfType(Machine.class);
//		for (Machine refinedMachine : refines) {
//			addMachines((EventBMachine) refinedMachine, machines, contexts);
//		}
//	}
}
