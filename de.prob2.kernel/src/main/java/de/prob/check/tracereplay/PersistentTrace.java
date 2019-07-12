package de.prob.check.tracereplay;

import java.util.ArrayList;
import java.util.List;

import de.prob.statespace.Trace;
import de.prob.statespace.Transition;

public class PersistentTrace {

    private final List<PersistentTransition> transitionList = new ArrayList<>();

    public PersistentTrace(Trace trace, int count) {
        List<Transition> list = trace.getTransitionList();
        transitionList.add(new PersistentTransition(list.get(count-1), true,
                null));
        for (int i = count-2; i >= 0; i--) {
            PersistentTransition trans = new PersistentTransition(list.get(i), true,
                    transitionList.get(0));
            transitionList.add(0,trans);
        }
    }

    public PersistentTrace(Trace trace) {
        this(trace, trace.getTransitionList().size());
    }

    public List<PersistentTransition> getTransitionList() {
        return this.transitionList;
    }
}
