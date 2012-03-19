/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.ProBException;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public final class GetTraceCommand implements ICommand {

	private static final String TRACE_VARIABLE = "Trace";
	Logger logger = LoggerFactory.getLogger(GetTraceCommand.class);

	private final static class Occurence {
		private final String text;

		private int count;

		public Occurence(final String text) {
			this.text = text;
			this.count = 1;
		}

		public synchronized void inc() {
			this.count++;
		}

		@Override
		public synchronized String toString() {
			return text + ((count > 1) ? " (" + count + " times)" : "");
		}

	}

	private List<String> trace;

	private GetTraceCommand() {
	}

	public List<String> getTrace() {
		return trace;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) throws ProBException {
		List<Occurence> res = new LinkedList<Occurence>();

		try {
			ListPrologTerm list = BindingGenerator.getList(bindings.get(TRACE_VARIABLE));
		
			Occurence current = null;
			for (PrologTerm term : list) {
				if (current == null || !current.text.equals(term.toString())) {
					current = new Occurence(term.toString());
					res.add(current);
				} else {
					current.inc();
				}
			}

			final List<String> actions = new ArrayList<String>();
			for (Occurence occurence : res) {
				actions.add(occurence.toString());
			}

			this.trace = actions;
		} catch (ResultParserException e) {
			logger.error("Result from Prolog was not as expected.", e);
			throw new ProBException();
		}

		
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("find_shortest_trace_to_current_state")
				.printVariable(TRACE_VARIABLE).closeTerm();
	}

}
