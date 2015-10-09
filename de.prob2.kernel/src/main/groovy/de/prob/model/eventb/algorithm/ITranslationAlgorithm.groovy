package de.prob.model.eventb.algorithm;

import de.prob.model.eventb.MachineModifier
import de.prob.model.representation.ModelElementList

interface ITranslationAlgorithm {

	public MachineModifier run(MachineModifier machineM, Block algorithm, ModelElementList<Procedure> procedures);
}
