package de.prob.model.eventb.algorithm;

import de.prob.model.eventb.MachineModifier;

interface ITranslationAlgorithm {

	public MachineModifier run(MachineModifier machineM, Block algorithm);
}
