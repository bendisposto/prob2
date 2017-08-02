package de.prob.model.brules;

import java.io.File;
import java.util.Map;

import com.github.krukow.clj_lang.PersistentHashMap;

import com.google.inject.Inject;

import de.be4.classicalb.core.parser.rules.RulesProject;

import de.prob.animator.command.LoadRulesProjectCommand;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.DependencyGraph;
import de.prob.model.representation.Machine;
import de.prob.model.representation.ModelElementList;
import de.prob.scripting.StateSpaceProvider;
import de.prob.statespace.FormalismType;
import de.prob.statespace.StateSpace;

public class RulesModel extends AbstractModel {

	private RulesProject project;

	@Inject
	public RulesModel(StateSpaceProvider stateSpaceProvider) {
		super(stateSpaceProvider,
				PersistentHashMap
						.<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>>emptyMap(),
				new DependencyGraph(), null);
	}

	public RulesModel(StateSpaceProvider stateSpaceProvider, File file, RulesProject project) {
		super(stateSpaceProvider,
				PersistentHashMap
						.<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>>emptyMap(),
				new DependencyGraph(), file);
		this.project = project;
	}

	@Override
	public AbstractElement getComponent(String name) {
		return getChildrenOfType(Machine.class).getElement(name);
	}

	public RulesModel create(File file, RulesProject project) {
		return new RulesModel(stateSpaceProvider, file, project);
	}

	@Override
	public IEvalElement parseFormula(String formula, FormulaExpand expand) {
		throw new AssertionError("Currently not supported");
	}

	@Override
	public boolean checkSyntax(String formula) {
		throw new AssertionError("Currently not supported");
	}

	@Override
	public FormalismType getFormalismType() {
		return FormalismType.B;
	}

	@Override
	public StateSpace load(AbstractElement mainComponent, Map<String, String> preferences) {
		return stateSpaceProvider.loadFromCommand(this, mainComponent, preferences,
				new LoadRulesProjectCommand(project, modelFile));
	}

	public LoadRulesProjectCommand getLoadCommand() {
		return new LoadRulesProjectCommand(project, modelFile);
	}

	public RulesProject getRulesProject() {
		return this.project;
	}

}
