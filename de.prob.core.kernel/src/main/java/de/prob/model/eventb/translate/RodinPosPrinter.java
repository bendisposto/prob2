package de.prob.model.eventb.translate;

import java.util.HashMap;
import java.util.Map;

import org.parboiled.common.Tuple2;

import de.be4.classicalb.core.parser.analysis.prolog.PositionPrinter;
import de.be4.classicalb.core.parser.node.Node;
import de.prob.prolog.output.IPrologTermOutput;

public class RodinPosPrinter implements PositionPrinter {

	private IPrologTermOutput pout;
	private final Map<Node, Tuple2<String, String>> nodeInfos = new HashMap<Node, Tuple2<String, String>>();

	public void addNodeInfos(final Map<Node, Tuple2<String, String>> infos) {
		nodeInfos.putAll(infos);
	}

	@Override
	public void setPrologTermOutput(final IPrologTermOutput pout) {
		this.pout = pout;
	}

	@Override
	public void printPosition(final Node node) {
		Tuple2<String, String> tuple = nodeInfos.get(node);
		if (tuple == null) {
			pout.printAtom("none");
		} else {
			pout.openTerm("rodinpos");
			pout.printAtom(tuple.a);
			pout.printAtom(tuple.b);
			pout.emptyList();
			pout.closeTerm();
		}

	}

}
