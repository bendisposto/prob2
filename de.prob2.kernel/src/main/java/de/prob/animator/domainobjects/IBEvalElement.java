package de.prob.animator.domainobjects;

import de.be4.classicalb.core.parser.node.Node;
import de.prob.translator.types.BObject;

public interface IBEvalElement extends IEvalElement {
	public Node getAst();

	public BObject translate();
}
