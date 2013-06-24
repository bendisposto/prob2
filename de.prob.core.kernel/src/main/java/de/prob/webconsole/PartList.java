package de.prob.webconsole;

import java.util.ArrayList;
import java.util.Arrays;

class PartList extends ArrayList<String> {

	private static final long serialVersionUID = -5668244262489304794L;

	public PartList(String[] split) {
		super(Arrays.asList(split));
	}

	@Override
	public String get(int index) {
		if (index >= this.size())
			return "";
		else
			return super.get(index);
	}

}
