package de.prob.model;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.ProBException;
import de.prob.animator.command.GetMachineObjectsCommand;
import de.prob.animator.command.ICommand;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class ClassicalBModelFactory implements IModelFactory {

	Logger logger = LoggerFactory.getLogger(ClassicalBModelFactory.class);

	@Override
	public ICommand getLoadCommand(final File model, final String name)
			throws ProBException {
		return new ICommand() {

			@Override
			public void writeCommand(final IPrologTermOutput pto)
					throws ProBException {
				pto.openTerm("load_b_project");
				pto.printAtom(name);
				pto.printTerm(getLoadTerm(model));
				pto.printVariable("Errors");
				pto.closeTerm();
				pto.printAtom("start_animation");
			}

			@Override
			public void processResult(
					final ISimplifiedROMap<String, PrologTerm> bindings)
					throws ProBException {
				ListPrologTerm e = (ListPrologTerm) bindings.get("Errors");
				if (!e.isEmpty()) {
					for (PrologTerm prologTerm : e) {
						logger.error("Error from Prolog: '{}'", prologTerm);
					}
					throw new ProBException();
				}
			}
		};

	}

	private PrologTerm getLoadTerm(final File model) throws ProBException {
		logger.trace("Parsing main file '{}'", model.getAbsolutePath());
		BParser bParser = new BParser();
		try {
			Start ast = bParser.parseFile(model, false);
			final RecursiveMachineLoader rml = new RecursiveMachineLoader(
					model.getParent());
			rml.loadAllMachines(model, ast, null, bParser.getDefinitions());
			StructuredPrologOutput output = new StructuredPrologOutput();
			StructuredPrologOutput out = new StructuredPrologOutput();
			logger.trace("Done with parsing '{}'", model.getAbsolutePath());
			rml.printAsProlog(output);

			Collection<PrologTerm> sentences = output.getSentences();
			out.openList();
			Iterator<PrologTerm> iterator = sentences.iterator();
			iterator.next();
			iterator.next();
			while (iterator.hasNext()) {
				CompoundPrologTerm prologTerm = (CompoundPrologTerm) iterator
						.next();
				out.printTerm(prologTerm.getArgument(1));
			}
			out.closeList();
			out.fullstop();
			return out.getSentences().iterator().next();
		} catch (IOException e) {
			logger.error("IO Error {}.", e.getLocalizedMessage());
			logger.debug("Details", e);
			throw new ProBException();
		} catch (BException e) {
			logger.error("Parser Error. {}.", e.getLocalizedMessage());
			logger.debug("Details", e);
			throw new ProBException();
		}
	}

	@Override
	public StateTemplate generate(final GetMachineObjectsCommand getInfo) {
		StateTemplateLabel constants = new StateTemplateLabel("Constants");
		for (String elem : getInfo.getConstants()) {
			constants.addChild(new StateTemplateValue(elem, ""));
		}

		StateTemplateLabel variables = new StateTemplateLabel("Variables");
		for (String elem : getInfo.getVariables()) {
			variables.addChild(new StateTemplateValue(elem, ""));
		}

		StateTemplate result = new StateTemplate();
		result.addChild(constants);
		result.addChild(variables);
		result.addChild(new StateTemplateValue("$timeout", "false"));
		result.addChild(new StateTemplateValue("$invariant_violated", "false"));
		return null;
	}
}
