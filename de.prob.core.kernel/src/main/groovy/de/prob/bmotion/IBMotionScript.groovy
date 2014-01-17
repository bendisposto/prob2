package de.prob.bmotion;

import java.util.Map;

import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

interface IBMotionScript
{ 
	void traceChanged(Trace trace, Map<String,Object> formulas)
	void modelChanged(StateSpace statespace)
}