package de.prob.animator.prologast;

import java.util.ArrayList;
import java.util.List;

/**
 * PrologASTNode used to simplify the structure given by prolog
 */
public abstract class PrologASTNode {
    private List<PrologASTNode> subnodes;

    PrologASTNode(){
        this.subnodes = new ArrayList<>();
    }

    PrologASTNode(List<PrologASTNode> subnodes) {
        this.subnodes = subnodes;
    }

    public List<PrologASTNode> getSubnodes() {
        return subnodes;
    }

    void setSubnodes(List<PrologASTNode> subnodes){
        this.subnodes = subnodes;
    }

    public String toString(){
        return "\n[Node]";
    }
}
