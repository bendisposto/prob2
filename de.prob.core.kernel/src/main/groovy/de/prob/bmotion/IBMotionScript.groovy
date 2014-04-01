package de.prob.bmotion;

import java.util.Map;

import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

interface IBMotionScript
{ 
	public void traceChanged(Trace trace)
	public void modelChanged(StateSpace statespace)
}