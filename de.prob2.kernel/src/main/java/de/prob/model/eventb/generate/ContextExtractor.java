package de.prob.model.eventb.generate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.be4.eventbalg.core.parser.node.AAxiom;
import de.be4.eventbalg.core.parser.node.ACarrierSet;
import de.be4.eventbalg.core.parser.node.AConstant;
import de.be4.eventbalg.core.parser.node.ADerivedAxiom;
import de.be4.eventbalg.core.parser.node.TComment;
import de.be4.eventbalg.core.parser.node.Token;

import de.prob.model.eventb.Context;
import de.prob.model.eventb.ContextModifier;
import de.prob.model.eventb.ModelGenerationException;

import org.eventb.core.ast.extension.IFormulaExtension;

public class ContextExtractor extends ElementExtractor {

	private ContextModifier contextM;

	public ContextExtractor(final ContextModifier contextM,
			Set<IFormulaExtension> typeEnv) {
		super(typeEnv);
		this.contextM = contextM;
	}

	public Context getContext() {
		return contextM.getContext();
	}

	@Override
	public void caseAAxiom(final AAxiom node) {
		try {
			contextM = contextM.axiom(node.getName().getText(), node
					.getPredicate().getText(), false, getComment(node
							.getComments()));
		} catch (ModelGenerationException e) {
			handleException(e, node);
		}
	}

	@Override
	public void caseADerivedAxiom(final ADerivedAxiom node) {
		try {
			contextM = contextM.axiom(node.getName().getText(), node
					.getPredicate().getText(), true, getComment(node
							.getComments()));
		} catch (ModelGenerationException e) {
			handleException(e, node);
		}
	}

	@Override
	public void caseACarrierSet(final ACarrierSet node) {
		try {
			contextM = contextM.set(node.getName().getText(),
					getComment(node.getComments()));
		} catch (ModelGenerationException e) {
			handleException(e, node);
		}
	}

	@Override
	public void caseAConstant(final AConstant node) {
		try {
			contextM = contextM.constant(node.getName().getText(),
					getComment(node.getComments()));
		} catch (ModelGenerationException e) {
			handleException(e, node);
		}
	}

	public String getComment(List<TComment> comments) {
		return comments.stream().map(Token::getText).collect(Collectors.joining("\n"));
	}

}
