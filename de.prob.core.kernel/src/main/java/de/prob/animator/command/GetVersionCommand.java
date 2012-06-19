package de.prob.animator.command;

import com.google.common.base.Joiner;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class GetVersionCommand implements ICommand {

	private String major, minor, service, qualifier, svnrevision,
			lastchangeddate, prologinfo;

	private static final String VAR_MAJOR = "Major";
	private static final String VAR_MINOR = "Minor";
	private static final String VAR_SERVICE = "Service";
	private static final String VAR_QUALIFIER = "Qualifier";
	private static final String VAR_REVISION = "SvnRevision";
	private static final String VAR_CHANGEDATE = "LastChangedDate";
	private static final String VAR_PROLOGVERSION = "PrologInfo";

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("get_version").printVariable(VAR_MAJOR)
				.printVariable(VAR_MINOR).printVariable(VAR_SERVICE)
				.printVariable(VAR_QUALIFIER).printVariable(VAR_REVISION)
				.printVariable(VAR_CHANGEDATE).printVariable(VAR_PROLOGVERSION)
				.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		//FIXME check for nullness
		major = bindings.get(VAR_MAJOR).getFunctor();
		minor = bindings.get(VAR_MINOR).getFunctor();
		service = bindings.get(VAR_SERVICE).getFunctor();
		qualifier = bindings.get(VAR_QUALIFIER).getFunctor();
		svnrevision = bindings.get(VAR_REVISION).getFunctor();
		lastchangeddate = bindings.get(VAR_CHANGEDATE).getFunctor();
		prologinfo = bindings.get(VAR_PROLOGVERSION).getFunctor();

	}

	public String getMajor() {
		return major;
	}

	public String getMinor() {
		return minor;
	}

	public String getService() {
		return service;
	}

	public String getQualifier() {
		return qualifier;
	}

	public String getSvnrevision() {
		return svnrevision;
	}

	public String getLastchangeddate() {
		return lastchangeddate;
	}

	public String getProloginfo() {
		return prologinfo;
	}

	public String getVersionString() {
		return Joiner.on('.').join(major, minor, service) + "-" + qualifier
				+ " (" + svnrevision + ")\nLast changed: " + lastchangeddate
				+ "\nProlog: " + prologinfo;
	}

}
