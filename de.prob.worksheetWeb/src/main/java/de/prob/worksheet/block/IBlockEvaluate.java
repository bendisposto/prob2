package de.prob.worksheet.block;

import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface IBlockEvaluate {
	@JsonIgnore
	@XmlTransient
	public abstract boolean isImmediateEvaluation();

	@JsonIgnore
	public abstract void setImmediateEvaluation(boolean immediateEvaluation);

	@JsonIgnore
	@XmlTransient
	public abstract boolean isInputAndOutput();

	@JsonIgnore
	public abstract void setInputAndOutput(boolean inputAndOuput);

	@JsonIgnore
	@XmlTransient
	public abstract String getOverrideEditorContent();

	@XmlTransient
	public abstract boolean isNeitherInNorOutput();

	public abstract void setNeitherInNorOutput(boolean neitherInNorOutput);

}
