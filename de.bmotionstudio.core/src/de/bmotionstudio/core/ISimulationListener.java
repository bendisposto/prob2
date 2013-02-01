package de.bmotionstudio.core;

import de.bmotionstudio.core.model.Simulation;

public interface ISimulationListener {

	public void openSimulation(Simulation simulation);

	public void closeSimulation(Simulation simulation);

}
