package de.prob.web;

public interface IDurable {
	String save();

	void restore(String serialized);

	EPersistenceStore getStorage();
}
