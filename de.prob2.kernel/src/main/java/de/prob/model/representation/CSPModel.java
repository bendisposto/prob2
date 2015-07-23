package de.prob.model.representation;

import java.io.File;
import java.util.Map;

import com.github.krukow.clj_lang.PersistentHashMap;
import com.google.inject.Inject;

import de.prob.animator.command.LoadCSPCommand;
import de.prob.animator.domainobjects.CSP;
import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.prolog.output.PrologTermStringOutput;
import de.prob.scripting.StateSpaceProvider;
import de.prob.statespace.FormalismType;
import de.prob.statespace.StateSpace;

public class CSPModel extends AbstractModel {

	private String content;

	@Inject
	public CSPModel(final StateSpaceProvider ssProvider) {
		super(ssProvider);
	}

	public CSPModel(final StateSpaceProvider ssProvider, String content, File modelFile, PersistentHashMap<String, AbstractElement> components) {
		super(ssProvider,
				PersistentHashMap.<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>>emptyMap(),
				new DependencyGraph(), components, modelFile);
		this.content = content;
	}

	public CSPModel create(final String content, final File modelFile) {
		return new CSPModel(stateSpaceProvider, content, modelFile, (PersistentHashMap<String, AbstractElement>) components.assoc(modelFile.getName(), new CSPElement(modelFile.getName())));
	}

	public String getContent() {
		return content;
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

	@Override
	public StateSpace load(final AbstractElement mainComponent,
			final Map<String, String> preferences) {
		return stateSpaceProvider.loadFromCommand(this, mainComponent,
				preferences, new LoadCSPCommand(modelFile.getAbsolutePath()));
	}

}
