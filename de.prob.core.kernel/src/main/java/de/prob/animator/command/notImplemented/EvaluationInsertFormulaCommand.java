/**
 * 
 */
package de.prob.animator.command.notImplemented;

import de.prob.animator.command.ICommand;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

//import de.prob.core.Animator;
//import de.prob.core.LanguageDependendAnimationPart;
//import de.prob.core.domainobjects.EvaluationElement;
//import de.prob.exceptions.ProBException;
//import de.prob.parser.ISimplifiedROMap;
//import de.prob.parserbase.ProBParseException;
//import de.prob.parserbase.ProBParserBaseAdapter;
//import de.prob.prolog.output.IPrologTermOutput;
//import de.prob.prolog.term.PrologTerm;

/**
 * This commands registers a formula (given as an already parsed PrologTerm) in
 * the Prolog core and returns an EvaluationElement that can be used to evaluate
 * the formula and retrieve its subformulas.
 * 
 * @author plagge
 */
public class EvaluationInsertFormulaCommand implements ICommand {

	// FIXME: This command must be refactored with EvaluationElement object
	// public static enum FormulaType {
	// PREDICATE, EXPRESSION
	// };
	//
	// private static final String VARNAME_ID = "ID";
	// private final PrologTerm rawExpression;
	// private PrologTerm id;
	//
	// public static EvaluationElement insertPredicate(final String formula)
	// throws ProBException, UnsupportedOperationException,
	// ProBParseException {
	// return insertFormula(getParser().parsePredicate(formula, false));
	// }
	//
	// public static EvaluationElement insertExpression(final String formula)
	// throws ProBException, UnsupportedOperationException,
	// ProBParseException {
	// return insertFormula(getParser().parseExpression(formula, false));
	// }
	//
	// public static EvaluationElement insertFormula(final Animator animator,
	// final FormulaType type, final String formula) throws ProBException,
	// UnsupportedOperationException, ProBParseException {
	// final ProBParserBaseAdapter parser = getParser(animator);
	// final PrologTerm parsed;
	// switch (type) {
	// case EXPRESSION:
	// parsed = parser.parseExpression(formula, false);
	// break;
	// case PREDICATE:
	// parsed = parser.parsePredicate(formula, false);
	// break;
	// default:
	// throw new IllegalArgumentException("Unsupported formula type: "
	// + type);
	// }
	// return insertFormula(parsed);
	// }
	//
	// private static ProBParserBaseAdapter getParser() {
	// return getParser(Animator.getAnimator());
	// }
	//
	// private static ProBParserBaseAdapter getParser(final Animator animator) {
	// final LanguageDependendAnimationPart ldp = animator
	// .getLanguageDependendPart();
	// if (ldp == null) {
	// throw new UnsupportedOperationException(
	// "The current formalism does not allow parsing of formulas");
	// } else {
	// return new ProBParserBaseAdapter(ldp);
	// }
	// }
	//
	// public EvaluationInsertFormulaCommand(final PrologTerm rawExpression) {
	// this.rawExpression = rawExpression;
	// }
	//
	// public void processResult(
	// final ISimplifiedROMap<String, PrologTerm> bindings){
	// //id = bindings.get(VARNAME_ID);
	// }
	//
	// public void writeCommand(final IPrologTermOutput pto) {
	// pto.openTerm("evaluation_insert_formula");
	// rawExpression.toTermOutput(pto);
	// pto.printAtom("user");
	// pto.printVariable(VARNAME_ID);
	// pto.closeTerm();
	// }
	//
	// public PrologTerm getId() {
	// return id;
	// }

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		// TODO Auto-generated method stub

	}

}
