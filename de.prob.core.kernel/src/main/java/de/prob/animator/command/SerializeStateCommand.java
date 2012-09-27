package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class SerializeStateCommand implements ICommand {

    private final String id;
    private String state;

    public SerializeStateCommand(String id) {
        this.id = id;
    }

    @Override
    public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
         state = PrologTerm.atomicString(bindings.get("State"));
    }

    @Override
    public void writeCommand(IPrologTermOutput pto) {
        pto.openTerm("serialize").printAtomOrNumber(id).printVariable("State").closeTerm();
    }

    public String getId() {
        return id;
    }

    public String getState() {
        return state;
    }

}
