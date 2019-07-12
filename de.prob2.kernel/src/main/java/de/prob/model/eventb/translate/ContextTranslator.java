package de.prob.model.eventb.translate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.be4.classicalb.core.parser.node.AAbstractConstantsContextClause;
import de.be4.classicalb.core.parser.node.AAxiomsContextClause;
import de.be4.classicalb.core.parser.node.AConstantsContextClause;
import de.be4.classicalb.core.parser.node.ADeferredSetSet;
import de.be4.classicalb.core.parser.node.AEventBContextParseUnit;
import de.be4.classicalb.core.parser.node.AExtendsContextClause;
import de.be4.classicalb.core.parser.node.ASetsContextClause;
import de.be4.classicalb.core.parser.node.ATheoremsContextClause;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.PContextClause;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.PSet;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Context;
import de.prob.model.eventb.EventBAxiom;
import de.prob.model.eventb.EventBConstant;
import de.prob.model.representation.Set;
import de.prob.util.Tuple2;

public class ContextTranslator {

	private final Context context;
	private final Map<Node, Tuple2<String, String>> nodeInfos = new HashMap<>();

	public ContextTranslator(final Context context) {
		this.context = context;
	}

	public Map<Node, Tuple2<String, String>> getNodeInfos() {
		return nodeInfos;
	}

	public Node translateContext() {
		AEventBContextParseUnit ast = new AEventBContextParseUnit();
		ast.setName(new TIdentifierLiteral(context.getName()));

		List<PContextClause> clauses = new ArrayList<>();
		clauses.add(processExtends());
		clauses.addAll(processConstants());
		clauses.addAll(processAxiomsAndTheorems());
		clauses.add(processSets());

		ast.setContextClauses(clauses);
		return ast;
	}

	private AExtendsContextClause processExtends() {
		List<TIdentifierLiteral> extended = new ArrayList<>();
		for (Context c : context.getExtends()) {
			extended.add(new TIdentifierLiteral(c.getName()));
		}
		return new AExtendsContextClause(extended);
	}

	private List<PContextClause> processConstants() {
		List<PContextClause> constants = new ArrayList<>();
		List<PExpression> concrete = new ArrayList<>();
		List<PExpression> abstractC = new ArrayList<>();

		for (EventBConstant eventBConstant : context.getConstants()) {
			if (eventBConstant.isAbstract()) {
				abstractC.add((PExpression) ((EventB) eventBConstant
						.getExpression()).getAst());
			} else {
				concrete.add((PExpression) ((EventB) eventBConstant
						.getExpression()).getAst());
			}
		}

		constants.add(new AConstantsContextClause(concrete));
		constants.add(new AAbstractConstantsContextClause(abstractC));
		return constants;
	}

	private List<PContextClause> processAxiomsAndTheorems() {
		List<PContextClause> axiomsAndThms = new ArrayList<>();
		List<PPredicate> axioms = new ArrayList<>();
		List<PPredicate> thms = new ArrayList<>();

		for (EventBAxiom axiom : context.getAxioms()) {
			PPredicate ppred = (PPredicate) ((EventB) axiom.getPredicate())
					.getAst();
			nodeInfos.put(ppred, new Tuple2<>(context.getName(), axiom.getName()));
			if (axiom.isTheorem()) {
				thms.add(ppred);
			} else {
				axioms.add(ppred);
			}
		}

		axiomsAndThms.add(new AAxiomsContextClause(axioms));
		axiomsAndThms.add(new ATheoremsContextClause(thms));
		return axiomsAndThms;
	}

	private ASetsContextClause processSets() {
		List<PSet> sets = new ArrayList<>();

		for (Set bSet : context.getSets()) {
			List<TIdentifierLiteral> names = new ArrayList<>();
			names.add(new TIdentifierLiteral(bSet.getName()));
			sets.add(new ADeferredSetSet(names));
		}

		return new ASetsContextClause(sets);
	}
}
