package de.prob.cli;

import com.google.common.base.Joiner;
import com.google.common.collect.ComparisonChain;

public class CliVersionNumber implements Comparable<CliVersionNumber> {
	public final String major;
	public final String minor;
	public final String service;
	public final String qualifier;
	public final String revision;
	private final String version;

	public CliVersionNumber(String major, String minor, String service,
			String qualifier, String revision) {
		this.major = major;
		this.minor = minor;
		this.service = service;
		this.qualifier = qualifier;
		this.revision = revision;
		this.version = Joiner.on('.').join(major, minor, service) + "-"
				+ qualifier
				+ (!revision.isEmpty() ? " (" + revision + ")" : "");
	}

	@Override
	public String toString() {
		return version;
	}

	@Override
	public int compareTo(CliVersionNumber o) {
		return ComparisonChain.start().compare(major, o.major)
				.compare(minor, o.minor).compare(service, o.service)
				.compare(qualifier, o.qualifier).compare(revision, o.revision)
				.result();
	}

}
