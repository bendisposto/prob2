package de.prob.animator.domainobjects;

import java.util.Objects;

public class MachineFileInformation {

	private final String name;
	
	private final String extension;
	
	private final String path;
	
	public MachineFileInformation(final String name, final String extension, final String path) {
		this.name = name;
		this.extension = extension;
		this.path = path;
	}
	
	public String getName() {
		return name;
	}
	
	public String getExtension() {
		return extension;
	}
	
	public String getPath() {
		return path;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(o == null || this.getClass() != o.getClass()) {
			return false;
		}
		MachineFileInformation obj = (MachineFileInformation) o;
		return name.equals(obj.name) && extension.equals(obj.extension) && path.equals(obj.path);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, extension, path);
	}
	
}
