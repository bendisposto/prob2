package de.prob.animator.prologast;

import de.prob.animator.domainobjects.ProBEvalElement;
import de.prob.prolog.term.PrologTerm;

public class ASTFormula extends PrologASTNode{
    private PrologTerm formula;

    ASTFormula(){
        super();
    }

    public ProBEvalElement getFormula(){
        PrologTerm term = this.formula.getArgument(1);
        String prettyPrint = this.formula.getArgument(2).getFunctor();
        return new ProBEvalElement(term, prettyPrint);
    }

    public void setFormula(PrologTerm formula) {
        this.formula = formula;
    }

    public String toString(){
        return "[Formula]\n" + this.formula;
    }
}
