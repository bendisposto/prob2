package de.prob.animator.command;


import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class DeserializeStateCommand implements ICommand {

    private String id;
    private final String state;

    public DeserializeStateCommand(String state) {
        this.state = state;
    }

    public void processResult(ISimplifiedROMap<String, PrologTerm> bindings){
        this.id = bindings.get("Id").toString();
    }

    public void writeCommand(IPrologTermOutput pto) {
        pto.openTerm("deserialize").printVariable("Id").printAtom(state).closeTerm();
    }

    public String getId() {
        return id;
    }

    public String getState() {
        return state;
    }

}
