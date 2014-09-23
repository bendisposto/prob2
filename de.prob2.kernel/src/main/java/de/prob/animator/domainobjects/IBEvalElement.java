package de.prob.animator.domainobjects;

import de.be4.classicalb.core.parser.node.Node;
import de.prob.prolog.output.IPrologTermOutput;

public interface IBEvalElement extends IEvalElement {
	public Node getAst();
	public void printProlog(IPrologTermOutput pout);
}
