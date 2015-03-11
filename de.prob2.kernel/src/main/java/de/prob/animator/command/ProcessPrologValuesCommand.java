package de.prob.animator.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.prob.animator.domainobjects.TranslatedEvalResult;
import de.prob.animator.domainobjects.ValueTranslator;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class ProcessPrologValuesCommand extends AbstractCommand {

	private final PrologTerm value;
	private final Map<String, PrologTerm> solutions;
	private final String PROCESSED_VALUE = "NewValue";
	private final String PROCESSED_SOLUTIONS = "NewSolutions";
	TranslatedEvalResult evalResult;

	public ProcessPrologValuesCommand(final PrologTerm value,
			final Map<String, PrologTerm> solutions) {
		this.value = value;
		this.solutions = solutions;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("process_prolog_values");
		pto.printTerm(value);
		pto.openList();
		for (Entry<String, PrologTerm> e : solutions.entrySet()) {
			pto.openTerm("sol");
			pto.printAtom(e.getKey());
			pto.printTerm(e.getValue());
			pto.closeTerm();
		}
		pto.closeList();
		pto.printVariable(PROCESSED_VALUE);
		pto.printVariable(PROCESSED_SOLUTIONS);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		ValueTranslator translator = new ValueTranslator();
		Object v = translator.toGroovy(bindings.get(PROCESSED_VALUE));
		Map<String, Object> solutions = new HashMap<String, Object>();
		ListPrologTerm sols = BindingGenerator.getList(bindings
				.get(PROCESSED_SOLUTIONS));
		for (PrologTerm pt : sols) {
			CompoundPrologTerm cpt = BindingGenerator.getCompoundTerm(pt, 2);
			String key = cpt.getArgument(1).getFunctor();
			Object sol = translator.toGroovy(cpt.getArgument(2));
			solutions.put(key, sol);
		}
		evalResult = new TranslatedEvalResult(v, solutions);
	}

	public TranslatedEvalResult getResult() {
		return evalResult;
	}

}
