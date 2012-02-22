package de.prob.model;

import java.io.File;

import de.prob.ProBException;
import de.prob.animator.IAnimator;

public interface IModelFactory {
	StaticInfo load(IAnimator animator, File f) throws ProBException;
}
