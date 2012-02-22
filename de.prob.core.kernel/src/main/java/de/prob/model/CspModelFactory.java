package de.prob.model;

import java.io.File;

import de.prob.ProBException;
import de.prob.animator.IAnimator;

public class CspModelFactory implements IModelFactory {

	@Override
	public StaticInfo load(final IAnimator animator, final File f)
			throws ProBException {
		// load CSp file

		return new StaticInfo();
	}

}
