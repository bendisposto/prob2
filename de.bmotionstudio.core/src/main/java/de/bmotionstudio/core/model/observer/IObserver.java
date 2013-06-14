/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.model.observer;

import java.util.List;
import java.util.Map;

import de.bmotionstudio.core.model.control.BControl;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.Trace;

public interface IObserver {

	public List<IEvalElement> prepareObserver(Trace history,
			BControl control);

	/**
	 * This method is called after every state change. The method tells the
	 * control how it has to look like and to behave.
	 * 
	 * @param history
	 *            The running animation
	 * @param bcontrol
	 *            The corresponding control
	 */
	public void check(Trace history, BControl control,
			Map<String, EvaluationResult> results);

	public void afterCheck(Trace history, BControl control);

	public String getName();

	public void setName(String name);

	public String getDescription();

	public String getType();
	
}
