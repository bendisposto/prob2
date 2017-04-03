package de.prob.animator.prologast;

import java.util.List;

public class ASTFormula extends PrologASTNode{
    private String formula;

    ASTFormula(){
        super();
    }

    public ASTFormula(PrologASTNode left, List<PrologASTNode> right){
        super(left, right);
    }

    public String getFormula(){
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String toString(){
        return "[Formula]\n" + this.formula;
    }
}
