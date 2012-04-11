package de.prob.scripting;

import java.io.File;
import java.util.List;

import jline.Completor;

public class FileCompletor implements Completor {

	@Override
	public int complete(final String buf, final int cursor,
			final List candidates) {
		String buffer = (buf == null) ? "" : buf;
		File file = new File(buffer);
		// if (file.exists())
		return 0;
	}

}
