package de.prob.animator.domainobjects;

import de.be4.classicalb.core.parser.node.Node;
import de.hhu.stups.prob.translator.BValue;

public interface IBEvalElement extends IEvalElement {
	public Node getAst();

	public BValue translate();
}
