package de.prob.animator.prologast;

import java.util.List;

/**
 * PrologASTNode used to simplify the structure given by prolog
 */
public abstract class PrologASTNode {
    private PrologASTNode left;
    private List<PrologASTNode> right;

    PrologASTNode(){
        this.left = null;
        this.right = null;
    }

    PrologASTNode(PrologASTNode left, List<PrologASTNode> right){
        this.left = left;
        this.right = right;
    }

    public PrologASTNode getLeft(){
        return left;
    }

    public List<PrologASTNode> getRight(){
        return right;
    }

    void setLeft(PrologASTNode left){
        this.left = left;
    }

    void setRight(List<PrologASTNode> right){
        this.right = right;
    }

    public String toString(){
        return "[Node]\n";
    }
}
