package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.animator.domainobjects.CSP;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Checks CSP assertions from the CSP specification, as well as new on-the-fly
 * defined CSP assertions (assertions which are not in the CSP specification)
 * 
 * @author dobrikov
 * 
 */

/*
 * Groovy script example for checking CSP assertion:
 * 
 * m = api.csp_load(
 * "/Users/ivo/ProB/probprivate/public_examples/CSP/other/Ivo/Deterministic1.csp"
 * )
 * 
 * h = m as Trace
 * 
 * s = h as StateSpace x = new
 * CSP("assert not NonDeterm3 :[ deterministic [F] ]",m) y = new
 * CSP("assert not NDet :[deterministic [FD]]",m) z = new
 * CSP("assert not NDet1 :[deterministic [F]]",m)
 * 
 * import de.prob.animator.command.* command = new CSPAssertionsCommand([x,y,z])
 * s.execute(command) command.getResults() // getting the list of results
 * command.getResultTraces() // getting the list of possible counter example
 * traces
 */

public class CSPAssertionsCommand extends AbstractCommand {
	private static final String PROLOG_COMMAND_NAME = "check_csp_assertions";
	private static final String RESULT_VARIABLE = "Results";
	private static final String RESULT_TRACES_VARIABLE = "ResultTraces";
	private final List<CSP> evalElements;
	private final List<String> results = new ArrayList<>();
	private final List<ListPrologTerm> resultTraces = new ArrayList<>();

	public CSPAssertionsCommand(final List<CSP> evalElements) {
		this.evalElements = evalElements;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {

		ListPrologTerm prologTermResults = BindingGenerator.getList(bindings
				.get(RESULT_VARIABLE));

		ListPrologTerm prologTermResultTraces = BindingGenerator
				.getList(bindings.get(RESULT_TRACES_VARIABLE));

		for (PrologTerm term : prologTermResults) {
			if (term != null) {
				results.add(term.getFunctor());
			}
		}

		for (PrologTerm term : prologTermResultTraces) {
			if (term instanceof ListPrologTerm) {
				resultTraces.add(BindingGenerator.getList(term));
			}
		}
	}

	public List<String> getResults() {
		return results;
	}

	// TODO: We should find a way to translate these traces into ProB Traces.
	// Maybe we can create a class implementing ITraceDescriptor that does the
	// translation
	public List<ListPrologTerm> getResultTraces() {
		return resultTraces;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pout) {
		pout.openTerm(PROLOG_COMMAND_NAME);
		pout.openList();

		// print parsed expressions/predicates
		for (CSP term : evalElements) {
			term.printPrologAssertion(pout);
		}
		pout.closeList();
		pout.printVariable(RESULT_VARIABLE);
		pout.printVariable(RESULT_TRACES_VARIABLE);
		pout.closeTerm();
	}
}
