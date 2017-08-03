package de.prob.animator.prologast;

import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.ProBEvalElement;
import de.prob.prolog.term.PrologTerm;

public class ASTFormula extends PrologASTNode{
    private final PrologTerm formula;

    ASTFormula(PrologTerm formula) {
        super();
        this.formula = formula;
    }

    public ProBEvalElement getFormula(FormulaExpand expand) {
        PrologTerm term = this.formula.getArgument(1);
        String prettyPrint = this.formula.getArgument(2).getFunctor();
        return new ProBEvalElement(term, prettyPrint, expand);
    }

    public ProBEvalElement getFormula() {
        return this.getFormula(FormulaExpand.TRUNCATE);
    }

    public String toString(){
        return "\n[Formula] : " + this.formula;
    }
}
