package de.prob.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.analysis.DepthFirstAdapter;
import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.node.AMachineHeader;
import de.be4.classicalb.core.parser.node.Start;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.prob.ProBException;
import de.prob.animator.IAnimator;
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
	private String name = null;

	static class NameFinder extends DepthFirstAdapter {

		public String name;

		@Override
		public void outAMachineHeader(final AMachineHeader node) {
			LinkedList<TIdentifierLiteral> nameL = node.getName();
			if (nameL.size() == 1) {
				name = nameL.get(0).getText();
			} else {
				ArrayList<String> list = new ArrayList<String>();
				for (TIdentifierLiteral t : nameL) {
					list.add(t.getText());
				}
				name = Joiner.on(".").join(list);
			}
		}
	}

	private ICommand getLoadCommand(final File model) throws ProBException {
		ICommand loadcmd = new ICommand() {
			@Override
			public void writeCommand(final IPrologTermOutput pto)
					throws ProBException {
				pto.openTerm("load_b_project");
				PrologTerm loadTerm = getLoadTerm(model);// sets name as a side
															// effect
				pto.printAtom(name);
				pto.printTerm(loadTerm);
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
		return loadcmd;
	}

	private PrologTerm getLoadTerm(final File model) throws ProBException {
		logger.trace("Parsing main file '{}'", model.getAbsolutePath());
		BParser bParser = new BParser();
		try {
			Start ast = bParser.parseFile(model, false);
			NameFinder nameFinder = new NameFinder();
			ast.apply(nameFinder);
			this.name = nameFinder.name;
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
	public StaticInfo load(final IAnimator animator, final File f)
			throws ProBException {
		animator.execute(getLoadCommand(f));
		GetMachineObjectsCommand getInfo = new GetMachineObjectsCommand();
		animator.execute(getInfo);
		return transformIntoStaticInfo(getInfo);
	}

	private StaticInfo transformIntoStaticInfo(
			final GetMachineObjectsCommand getInfo) {
		// TODO Auto-generated method stub
		return null;
	}

}
