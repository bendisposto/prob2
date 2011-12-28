package de.prob.cli;

import de.prob.ProBException;

interface IProBConnection {

	void disconnect();

	String send(String term) throws ProBException;
}
