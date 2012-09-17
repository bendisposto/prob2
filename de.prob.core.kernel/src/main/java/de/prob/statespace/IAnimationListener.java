package de.prob.statespace;


public interface IAnimationListener {
	public void currentStateChanged(History oldHistory, History newHistory);
}
