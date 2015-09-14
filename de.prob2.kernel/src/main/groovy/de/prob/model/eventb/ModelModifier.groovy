package de.prob.model.eventb

import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.Main
import de.prob.model.eventb.theory.Theory
import de.prob.model.eventb.translate.TheoryExtractor
import de.prob.model.representation.ElementComment
import de.prob.model.representation.Machine
import de.prob.model.representation.ModelElementList
import de.prob.model.representation.DependencyGraph.ERefType
import de.prob.scripting.EventBFactory


public class ModelModifier extends AbstractModifier {

	EventBModel model

	/**
	 * Creates an interface to allow the user to mutate the model object.
	 * The user can also specify an additional parameter 'startProB' which will
	 * determine if a ProB instance will be bound to the new
	 * model class. If not, a ProB instance can be lazily created later by calling
	 * the getStateSpace() method on the model object.
	 * @param model to be copied
	 * @param startProB default = true
	 */
	public ModelModifier(EventBModel model, Set<IFormulaExtension> typeEnvironment=Collections.emptySet()) {
		super(typeEnvironment)
		this.model = model
	}

	def ModelModifier() {
		this(Main.getInjector().getInstance(EventBFactory.class).modelCreator.get())
	}

	def ModelModifier context(HashMap properties, Closure definition) {
		def props = validateProperties(properties, [name: String, "extends": [String, null], comment: [String, null]])
		def model = this.model
		def name = properties["name"]
		def oldcontext = model.getContexts().getElement(name)
		def c = oldcontext ?: new Context(name)
		if (props["comment"]) {
			c = c.addTo(ElementComment.class, new ElementComment(props["comment"]))
		}

		def extended = c.getExtends() ? c.getExtends()[0] : null
		def ext = props["extends"]
		if (ext) {
			Context ctx = model.getContexts().getElement(ext)
			if (ctx == null) {
				throw new IllegalArgumentException("Tried to load context $ext but could not find it.")
			}
			if (extended) {
				model = model.removeRelationship(name, extended.getName(), ERefType.EXTENDS)
			}
			model = model.addRelationship(name, ctx.getName(), ERefType.EXTENDS)
			extended = ctx
		}

		def cm = new ContextModifier(c, typeEnvironment)
		if (extended) {
			cm = cm.setExtends(extended)
		}
		cm = cm.make(definition)
		model = oldcontext ? model.replaceIn(Context.class, oldcontext, cm.getContext()) :
				model.addContext(cm.getContext())
		new ModelModifier(model, typeEnvironment)
	}

	def ModelModifier machine(HashMap properties, Closure definition) {
		def props = validateProperties(properties, [name: String, refines: [String, null], sees: [List, []], comment: [String, null]])
		def model = this.model
		def name = props["name"]
		def oldmachine = model.getMachines().getElement(name)
		EventBMachine m = oldmachine ?: new EventBMachine(name)
		if (props["comment"]) {
			m = m.addTo(ElementComment.class, new ElementComment(props["comment"]))
		}

		EventBMachine refined = m.getRefines() ? m.getRefines()[0] : null
		def refines = props["refines"]
		if (refines) {
			EventBMachine machine = model.getMachines().getElement(refines)
			if (machine == null) {
				throw new IllegalArgumentException("Tried to load machine ${refines} but could not find it")
			}
			if (refined) {
				model = model.removeRelationship(name, refined.getName(), ERefType.REFINES)
			}
			model = model.addRelationship(name, machine.getName(), ERefType.REFINES)
			refined = machine
		}

		ModelElementList<Context> seenContexts = m.getSees()
		def sees = props["sees"].findAll { !seenContexts.hasProperty(it) }
		sees.each { c ->
			Context context = model.getContexts().getElement(c)
			if (context == null) {
				throw new IllegalArgumentException("Tried to load context $c but could not find it")
			}
			model = model.addRelationship(name, c, ERefType.SEES)
			seenContexts = seenContexts.addElement(context)
		}
		def mm = new MachineModifier(m, typeEnvironment)
		if (refined) {
			mm = mm.setRefines(refined)
		}
		mm = mm.setSees(seenContexts).make(definition)
		model = oldmachine ? model.replaceIn(Machine.class, oldmachine, mm.getMachine()) :
				model.addMachine(mm.getMachine())
		new ModelModifier(model, typeEnvironment)
	}

	def ModelModifier refine(String machineName, String refinementName) {
		final EventBMachine m = model.getMachines().getElement(machineName)
		if (m == null) {
			throw new IllegalArgumentException("Can only refine an existing machine in the model")
		}

		ModelModifier modelM = machine(name: refinementName, refines: machineName) {
			m.variables.each { variable(it) }
			m.events.each { Event e ->
				refine(name: e.getName(), extended: true) {}
			}
		}
		modelM
	}

	def ModelModifier make(Closure definition) {
		runClosure definition
	}

	def ModelModifier replaceContext(Context oldContext, Context newContext) {
		new ModelModifier(model.replaceIn(Context.class, oldContext, newContext))
	}

	def ModelModifier replaceMachine(EventBMachine oldMachine, EventBMachine newMachine) {
		new ModelModifier(model.replaceIn(Machine.class, oldMachine, newMachine))
	}

	def ModelModifier loadTheories(LinkedHashMap properties) {
		validateProperties(properties, [workspace: String, project: String, theories: String[]])
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = parserFactory.newSAXParser();

		Map<String, Theory> theoryMap = [:]
		ModelElementList<Theory> theories = new ModelElementList<Theory>()
		HashSet<IFormulaExtension> types = new HashSet<IFormulaExtension>()
		types.addAll(typeEnvironment)
		validate('theories', properties["theories"]).each { String name ->
			def workspace = validate('workspace', properties["workspace"])
			def project = validate('project', properties["project"])
			validate('name', name)
			TheoryExtractor extractor = new TheoryExtractor(workspace, project, name, theoryMap);
			saxParser.parse(new File(workspace + File.separator + project + File.separator + name + ".dtf"), extractor);
			theories = theories.addMultiple(extractor.getTheories())
			types.addAll(extractor.getTypeEnv())
		}
		def model = model.set(Theory.class, theories)
		new ModelModifier(model, types)
	}
}
