package de.prob.scripting;

import java.util.List;

import jline.Completor;

public class FileCompletor implements Completor {

	@Override
	public int complete(final String buf, final int cursor,
			@SuppressWarnings("rawtypes") final List candidates) {
		// String buffer = (buf == null) ? "" : buf;
		// File file = new File(buffer);
		// if (file.exists())
		return -1;
	}

}
