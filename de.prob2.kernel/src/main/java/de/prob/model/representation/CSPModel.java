package de.prob.model.representation;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.animator.domainobjects.CSP;
import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.prolog.output.PrologTermStringOutput;
import de.prob.statespace.FormalismType;
import de.prob.statespace.StateSpace;

public class CSPModel extends AbstractModel {

	private String content;

	@Inject
	public CSPModel(final Provider<StateSpace> ssProvider) {
		super(ssProvider);
	}

	public void init(final String content, final File modelFile) {
		this.content = content;
		this.modelFile = modelFile;
		extractModelDir(modelFile, "CSP_MODEL");
	}

	public String getContent() {
		return content;
	}

	@Override
	public AbstractElement getComponent(final String name) {
		if (name.equals(modelFile.getName())) {
			return getMainComponent();
		}
		return null;
	}

	@Override
	public Map<String, AbstractElement> getComponents() {
		return Collections.emptyMap();
	}

	@Override
	public AbstractElement getMainComponent() {
		return new CSPElement(modelFile.getName());
	}

	@Override
	public IEvalElement parseFormula(final String formula) {
		return new CSP(formula, this);
	}

	@Override
	public FormalismType getFormalismType() {
		return FormalismType.CSP;
	}

	@Override
	public boolean checkSyntax(final String formula) {
		try {
			CSP element = (CSP) parseFormula(formula);
			element.printProlog(new PrologTermStringOutput());
			;
			return true;
		} catch (EvaluationException e) {
			return false;
		}
	}

}
