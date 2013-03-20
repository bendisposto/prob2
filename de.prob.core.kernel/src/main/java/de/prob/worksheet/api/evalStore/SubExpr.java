package de.prob.worksheet.api.evalStore;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

import de.be4.classicalb.core.parser.analysis.DepthFirstAdapter;
import de.be4.classicalb.core.parser.node.AConjunctPredicate;
import de.be4.classicalb.core.parser.node.ADisjunctPredicate;
import de.be4.classicalb.core.parser.node.AGreaterPredicate;
import de.be4.classicalb.core.parser.node.AIdentifierExpression;
import de.be4.classicalb.core.parser.node.AIntegerExpression;
import de.be4.classicalb.core.parser.node.ALessPredicate;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.be4.classicalb.core.parser.node.Token;

public class SubExpr extends DepthFirstAdapter {
	public ArrayList<String> exps = new ArrayList<String>();
	private ArrayDeque<String> stack = new ArrayDeque<String>();
	public ArrayDeque<JNode> Nodes = new ArrayDeque<JNode>();

	@Override
	public void caseADisjunctPredicate(ADisjunctPredicate node) {
		JNode myNode = new JNode();

		super.caseADisjunctPredicate(node);

		String right = stack.pop();
		String left = stack.pop();
		stack.push("(" + left + " or " + right + ")");
		exps.add(left + " or " + right);
		myNode.setName(left + " or " + right);

		JNode nodeRight = Nodes.pop();
		JNode nodeLeft = Nodes.pop();
		myNode.addChildren(nodeLeft);
		myNode.addChildren(nodeRight);
		Nodes.push(myNode);

	}

	@Override
	public void caseAConjunctPredicate(AConjunctPredicate node) {
		JNode myNode = new JNode();

		super.caseAConjunctPredicate(node);

		String right = stack.pop();
		String left = stack.pop();
		stack.push("(" + left + " & " + right + ")");
		exps.add(left + " & " + right);
		myNode.setName(left + " & " + right);

		JNode nodeRight = Nodes.pop();
		JNode nodeLeft = Nodes.pop();
		myNode.addChildren(nodeLeft);
		myNode.addChildren(nodeRight);
		Nodes.push(myNode);

	}

	@Override
	public void caseALessPredicate(ALessPredicate node) {
		JNode myNode = new JNode();

		super.caseALessPredicate(node);

		String right = stack.pop();
		String left = stack.pop();
		stack.push("(" + left + " < " + right + ")");
		exps.add(left + " < " + right);
		System.out.println(left + " < " + right);
		myNode.setName(left + " < " + right);

		JNode nodeRight = Nodes.pop();
		JNode nodeLeft = Nodes.pop();
		myNode.addChildren(nodeLeft);
		myNode.addChildren(nodeRight);
		Nodes.push(myNode);
	}

	@Override
	public void caseAGreaterPredicate(AGreaterPredicate node) {
		JNode myNode = new JNode();

		super.caseAGreaterPredicate(node);

		String right = stack.pop();
		String left = stack.pop();
		stack.push("(" + left + " > " + right + ")");
		exps.add(left + " > " + right);
		System.out.println(left + " > " + right);
		myNode.setName(left + " > " + right);

		JNode nodeRight = Nodes.pop();
		JNode nodeLeft = Nodes.pop();
		myNode.addChildren(nodeLeft);
		myNode.addChildren(nodeRight);
		Nodes.push(myNode);

	}

	@Override
	public void caseAIntegerExpression(AIntegerExpression node) {
		JNode myNode = new JNode();
		super.caseAIntegerExpression(node);
		stack.push(node.getLiteral().getText());
		myNode.setName(node.getLiteral().getText() + "<br/>&nbsp;");
		myNode.setColor("#00FF00");
		Nodes.push(myNode);
	}

	@Override
	public void caseAIdentifierExpression(AIdentifierExpression node) {
		super.caseAIdentifierExpression(node);
		Iterator<TIdentifierLiteral> it = node.getIdentifier().iterator();
		while (it.hasNext()) {
			String next = it.next().getText();
			stack.push(next);
			exps.add(next);
			System.out.println(next);
			JNode myNode = new JNode();
			myNode.setName(next);
			Nodes.push(myNode);
		}
	}

	@Override
	public void defaultOut(Node node) {
		if (node instanceof Token) {
			System.out.println(((Token) node).getText());
		} else if (node instanceof AIdentifierExpression
				|| node instanceof AIntegerExpression
				|| node instanceof AGreaterPredicate
				|| node instanceof ALessPredicate
				|| node instanceof AConjunctPredicate
				|| node instanceof ADisjunctPredicate) {
			System.out.println(node.getClass().getName());
		}
		super.defaultOut(node);
	}

}
