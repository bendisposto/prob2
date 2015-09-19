package de.prob.model.eventb.algorithm

import de.prob.model.eventb.MachineModifier
import de.prob.model.eventb.ModelModifier
import de.prob.statespace.StateSpace

abstract class TranslationAlgorithm {

	abstract MachineModifier run(MachineModifier machineM, Block algorithm);
}
