/**
 * 
 */
package de.prob.animator.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * This command sends a list of expression IDs and a state ID to ProB and
 * retrieves a list of values of their corresponding values in that state.
 * 
 * @see EvaluationExpandCommand
 * @see EvaluationGetTopLevelCommand
 * @author plagge
 */
public class EvaluationGetValuesCommand extends AbstractCommand {

	private final List<String> ids;
	private final String stateId;
	private final Map<String, Object> values = new HashMap<String, Object>();

	private final String VALUES = "Values";

	public EvaluationGetValuesCommand(final List<String> ids,
			final String stateId) {
		this.ids = ids;
		this.stateId = stateId;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("evaluation_get_values");
		pto.openList();
		for (String id : ids) {
			pto.printAtomOrNumber(id);
		}
		pto.closeList();
		pto.printAtomOrNumber(stateId);
		pto.printVariable(VALUES);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		ListPrologTerm vs = BindingGenerator.getList(bindings.get(VALUES));

		for (String id : ids) {
			PrologTerm pt = vs.get(ids.indexOf(id));
			Object value = null;
			if (pt.getFunctor().equals("p")) {
				CompoundPrologTerm cpt = BindingGenerator
						.getCompoundTerm(pt, 1);
				if (cpt.getArgument(1).getFunctor().equals("true")) {
					value = Boolean.TRUE;
				} else {
					value = Boolean.FALSE;
				}
			} else if (pt.getFunctor().equals("v")) {
				CompoundPrologTerm cpt = BindingGenerator
						.getCompoundTerm(pt, 1);
				value = cpt.getArgument(1).getFunctor();
			} else {
				value = pt.getFunctor();
			}
			values.put(id, value);
		}
	}

	public Map<String, Object> getValues() {
		return values;
	}

}
