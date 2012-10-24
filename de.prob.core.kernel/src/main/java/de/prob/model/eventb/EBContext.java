package de.prob.model.eventb;

import java.util.Arrays;

import org.eventb.emf.core.context.Axiom;
import org.eventb.emf.core.context.CarrierSet;
import org.eventb.emf.core.context.Constant;
import org.eventb.emf.core.context.Context;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.IEntity;
import de.prob.model.representation.Label;

public class EBContext extends EventBElement {

	public Label sets = new Label("Sets");
	public Label constants = new Label("Constants");
	public Label axioms = new Label("Axioms");

	public EBContext(final Context element) {
		super(element);

		for (final CarrierSet carrierSet : element.getSets()) {
			sets.addChild(new EventB(carrierSet.doGetName()));
		}

		for (final Constant constant : element.getConstants()) {
			constants.addChild(new EventB(constant.doGetName()));
		}

		for (final Axiom axiom : element.getAxioms()) {
			axioms.addChild(new EventB(axiom.getPredicate()));
		}

		children.addAll(Arrays
				.asList(new IEntity[] { sets, constants, axioms }));
	}
}
