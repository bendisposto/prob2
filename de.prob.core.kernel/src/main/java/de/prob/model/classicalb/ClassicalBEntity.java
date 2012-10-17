package de.prob.model.classicalb;

import java.util.ArrayList;
import java.util.List;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.representation.FormulaUUID;
import de.prob.model.representation.IEntity;

public class ClassicalBEntity extends ClassicalB implements IEntity {

	public FormulaUUID uuid = new FormulaUUID();

	public ClassicalBEntity(final String code) throws BException {
		super(code);
	}

	@Override
	public List<IEntity> getChildren() {
		return new ArrayList<IEntity>();
	}

	@Override
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return false;
	}

}
