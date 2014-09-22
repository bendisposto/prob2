package de.prob.cli;

public class ProcessHandle {

	private final String key;
	private final Process process;

	public ProcessHandle(final Process process, final String key) {
		this.process = process;
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public Process getProcess() {
		return process;
	}

}
