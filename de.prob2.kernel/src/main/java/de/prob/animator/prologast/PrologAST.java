package de.prob.animator.prologast;

import de.prob.animator.domainobjects.ProBEvalElement;
import de.prob.parser.BindingGenerator;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

import java.util.ArrayList;
import java.util.List;

/**
 *The left node is the category given by Prolog, the right node is a formula or another category inside the root-category
 */
public class PrologAST {
    private PrologASTNode root;

    public PrologAST(ListPrologTerm nodes){
        this.root = buildAST(nodes);
    }

    public PrologASTNode getRoot(ListPrologTerm nodes){
        return root;
    }

    private PrologASTNode buildAST(ListPrologTerm nodes){
        List<ASTCategory> categoryList = new ArrayList<>();
        for(int i = 0; i < nodes.size(); i++){
            ASTCategory temp = (ASTCategory)makeASTNode(nodes.get(i));
            categoryList.add(temp);
        }
        for(int i = categoryList.size()-1; i > 0; i--){
            ASTCategory temp1 = categoryList.get(i);
            ASTCategory temp2 = categoryList.get(i-1);
            temp2.setLeft(temp1);
        }
        return categoryList.get(0);
    }


    private PrologASTNode makeASTNode(PrologTerm node){
        if(node.getFunctor().equals("formula")){
            ASTFormula formula = new ASTFormula();
            formula.setFormula(node);
            return formula;
        } else if(node.getFunctor().equals("category")){
            ASTCategory category = new ASTCategory();
            category.setExpanded(node.getArgument(2).toString().contains("expanded"));
            category.setPropagated(node.getArgument(2).toString().contains("propagated"));
            category.setName(node.getArgument(1).getFunctor());
            category.setRight(makeRightNodes(BindingGenerator.getList(node.getArgument(3))));
            return category;
        }
        return null;
    }


    private List<PrologASTNode> makeRightNodes(ListPrologTerm subnodes){
        List<PrologASTNode> rightList = new ArrayList<>();
        for(PrologTerm m : subnodes){
            rightList.add(makeASTNode(m));
        }
        return rightList;
    }

    /*For debugging only*/
    private void testRun(){
        System.out.println("TRY PRINT");
        PrologASTNode temp = root;
        while(temp != null){
            System.out.println("ROOT");
            System.out.println(temp.toString());
            System.out.println("LEFT");
            if(temp.getLeft() != null)
                System.out.println(temp.getLeft().toString());
            System.out.println("RIGHT");
            if(temp.getRight() != null) {
                List<PrologASTNode> rightList = temp.getRight();
                for (PrologASTNode n : rightList) {
                    System.out.println(n.toString());
                }
            }
            temp = temp.getLeft();
        }
    }

}
