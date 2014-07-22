package de.prob.web.worksheet;

import com.google.inject.Singleton;

import de.prob.Main;
import de.prob.web.views.Worksheet;

@Singleton
public class BoxFactory {

	public BoxFactory() {
	}

	public IBox create(Worksheet owner, int id, String type) {
		return create(owner, String.valueOf(id), type);
	}

	@SuppressWarnings("unchecked")
	public IBox create(Worksheet owner, String id, String type) {
		String className = "de.prob.web.worksheet." + type;

		Class<IBox> clazz = null;
		IBox box = null;
		try {
			clazz = (Class<IBox>) Class.forName(className);
			box = Main.getInjector().getInstance(clazz);
			box.setId(id);
			box.setOwner(owner);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

		return box;
	}

}
