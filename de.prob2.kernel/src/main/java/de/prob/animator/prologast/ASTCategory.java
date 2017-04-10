package de.prob.animator.prologast;

import java.util.List;

public class ASTCategory extends PrologASTNode{
    private boolean expanded;
    private boolean propagated;
    private String name;

    ASTCategory(){
        super();
    }

    ASTCategory(PrologASTNode left, List<PrologASTNode> right){
        super(left, right);
    }

    void setExpanded(boolean expanded){
        this.expanded = expanded;
    }

    void setPropagated(boolean propagated){
        this.propagated = propagated;
    }

    public boolean isExpanded(){
        return expanded;
    }

    public boolean isPropagated(){
        return propagated;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public String toString(){
        return "[Category]\n"+this.name+((isExpanded())?("\n[expanded]"):"\n[]")+((isPropagated())?("\n[propagated]"):"\n[]");
    }
}
