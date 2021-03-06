package de.prob.model.representation;

import java.io.File;
import java.util.Map;

import com.github.krukow.clj_lang.PersistentHashMap;

import com.google.inject.Inject;

import de.prob.animator.command.LoadCSPCommand;
import de.prob.animator.domainobjects.CSP;
import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.prolog.output.PrologTermStringOutput;
import de.prob.scripting.StateSpaceProvider;
import de.prob.statespace.FormalismType;
import de.prob.statespace.StateSpace;

public class CSPModel extends AbstractModel {
	private final String content;
	private final CSPElement mainComponent;
	
	@Inject
	public CSPModel(final StateSpaceProvider ssProvider) {
		this(ssProvider, null, null, null);
	}

	public CSPModel(final StateSpaceProvider ssProvider, String content, File modelFile, CSPElement mainComponent) {
		super(ssProvider, PersistentHashMap.emptyMap(), new DependencyGraph(), modelFile);
		this.content = content;
		this.mainComponent = mainComponent;
	}

	public CSPModel create(final String content, final File modelFile) {
		return new CSPModel(getStateSpaceProvider(), content, modelFile, new CSPElement(modelFile.getName()));
	}

	public String getContent() {
		return content;
	}

	@Override
	public IEvalElement parseFormula(final String formula, final FormulaExpand expand) {
		return new CSP(formula, this);
	}

	@Override
	public FormalismType getFormalismType() {
		return FormalismType.CSP;
	}

	@Override
	public boolean checkSyntax(final String formula) {
		try {
			CSP element = (CSP) parseFormula(formula, FormulaExpand.TRUNCATE);
			element.printProlog(new PrologTermStringOutput());

			return true;
		} catch (EvaluationException e) {
			return false;
		}

	}

	@Override
	public StateSpace load(final AbstractElement mainComponent, final Map<String, String> preferences) {
		return getStateSpaceProvider().loadFromCommand(this, mainComponent, preferences, new LoadCSPCommand(getModelFile().getAbsolutePath()));
	}

	@Override
	public AbstractElement getComponent(String name) {
		if (mainComponent != null && name != null && name.equals(mainComponent.getName())) {
			return mainComponent;
		}

		return null;
	}

	@Override
	public Object getProperty(String name) {
		AbstractElement component = getComponent(name);
		return component != null ? component : super.getProperty(name);
	}

	public Object getAt(String name) {
		return getComponent(name);
	}
}
