package de.prob.visualization;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

import de.prob.animator.command.FilterStatesForPredicateCommand;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.IStatesCalculatedListener;
import de.prob.statespace.OpInfo;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;

public class DynamicTransformer extends Transformer implements
		IStatesCalculatedListener {

	private transient final IEvalElement predicate;
	private transient final StateSpace space;
	private transient final List<StateId> filtered;

	public DynamicTransformer(final IEvalElement predicate,
			final StateSpace space) {
		super("");
		this.predicate = predicate;
		this.space = space;
		filtered = space.getStatesFromPredicate(predicate);
		space.registerStateSpaceListener(this);
		List<String> toConvert = new ArrayList<String>();
		for (StateId id : filtered) {
			toConvert.add("#r" + id.getId());
		}
		updateSelector(Joiner.on(",").join(toConvert));
	}

	@Override
	public void newTransitions(final List<? extends OpInfo> newOps) {
		List<StateId> toFilter = new ArrayList<StateId>();
		for (OpInfo opInfo : newOps) {
			StateId src = space.getVertex(opInfo.src);
			StateId dest = space.getVertex(opInfo.dest);
			if (!filtered.contains(src)) {
				toFilter.add(src);
			}
			if (!filtered.contains(dest)) {
				toFilter.add(dest);
			}
		}
		if (!toFilter.isEmpty()) {
			FilterStatesForPredicateCommand cmd = new FilterStatesForPredicateCommand(
					predicate, toFilter);
			space.execute(cmd);
			List<String> filteredIds = cmd.getFiltered();
			String newSelector = recalculateSelector(filteredIds);
			updateSelector(newSelector);
		}
	}

	private String recalculateSelector(final List<String> f) {
		List<String> toConvert = new ArrayList<String>();
		for (String string : f) {
			filtered.add(space.getVertex(string));
			toConvert.add("#r" + string);
		}
		String newSelector = Joiner.on(",").join(toConvert);
		if (selector == "") {
			return newSelector;
		}
		if (newSelector == "") {
			return selector;
		}
		return selector + "," + newSelector;
	}

}
