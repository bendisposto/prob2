package test;

import de.be4.classicalb.core.parser.analysis.ExtendedDFAdapter;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.Start;
import de.be4.classicalb.core.parser.node.Token;

public class Ast2String extends ExtendedDFAdapter {
	private final StringBuilder builder = new StringBuilder();

	@Override
	public String toString() {
		return builder.toString();
	}

	@Override
	public void defaultIn(final Node node) {
		super.defaultIn(node);
		builder.append(node.getClass().getSimpleName());
		builder.append("(");
	}

	@Override
	public void defaultCase(final Node node) {
		super.defaultCase(node);
		if (node instanceof Token) {
			builder.append(((Token) node).getText());
		} else {
			builder.append(node.toString());
		}

	}

	@Override
	public void defaultOut(final Node node) {
		super.defaultOut(node);
		builder.append(")");
	}

	@Override
	public void beginList(final Node parent) {
		builder.append('[');
	}

	@Override
	public void betweenListElements(final Node parent) {
		builder.append(',');
	}

	@Override
	public void endList(final Node parent) {
		builder.append(']');
	}

	@Override
	public void betweenChildren(final Node parent) {
		builder.append(',');
	}

	@Override
	public void caseStart(final Start node) {
		inStart(node);
		node.getPParseUnit().apply(this);
		node.getEOF().apply(this);
		outStart(node);
	}

	public static String getTreeAsString(final Start startNode) throws BException {
		final Ast2String ast2String = new Ast2String();
		startNode.apply(ast2String);
		return ast2String.toString();
	}
}
