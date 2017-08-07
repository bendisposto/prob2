package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.TypeCheckResult;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class FormulaTypecheckCommand extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "evaluate_formula_typecheck";
	private final IEvalElement formula;
	private final String TYPE = "Type";
	private final String ERRORS = "Errors";
	private TypeCheckResult result;

	public FormulaTypecheckCommand(IEvalElement formula) {
		this.formula = formula;
	}

	@Override
	public void writeCommand(IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.printAtom(formula.getKind().getPrologName());
		formula.printProlog(pto);
		pto.printVariable("_"); // we don't need the typed formula back and
								// eliminating it decreases the size of the
								// result prolog term
		pto.printVariable(TYPE);
		pto.printVariable(ERRORS);
		pto.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		String type = bindings.get(TYPE).toString();
		ListPrologTerm errs = BindingGenerator.getList(bindings.get(ERRORS));
		List<String> errors = new ArrayList<String>();
		for (PrologTerm pt : errs) {
			errors.add(pt.toString());
		}
		result = new TypeCheckResult(type, errors);
	}

	public TypeCheckResult getResult() {
		return result;
	}

}
