package de.prob.worksheet.editor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "@type")
@JsonSubTypes({ 
		@Type(value = JavascriptEditor.class, name = "javascript"),
		@Type(value = HTMLEditor.class, name = "HTMLEditor"),
		@Type(value = HTMLErrorEditor.class, name = "errorHtml")})
@JsonIgnoreProperties(ignoreUnknown = true)
public interface IWorksheetEditor {

	@JsonProperty(value = "html")
	public String getHTMLContent();

	@JsonProperty(value = "content")
	public String getEditorContent();

	@JsonProperty(value = "init")
	public String getInitializationFunction();

	@JsonProperty(value = "getContent")
	public String getGetContentScript();

	@JsonProperty(value = "setContent")
	public String getSetContentScript();

	@JsonProperty(value = "destroy")
	public String getDestroyScript();

	@JsonProperty(value = "cssURLs")
	public String[] getCSSHREFs();

	@JsonProperty(value = "jsURLs")
	public String[] getJavascriptHREFs();

	@JsonProperty(value = "html")
	public void setHTMLContent(String htmlContent);

	@JsonProperty(value = "content")
	public void setEditorContent(String editorContent);

	@JsonIgnore
	public void setInitializationFunction(String initFunction);

	@JsonIgnore
	public void setGetContentScript(String script);

	@JsonIgnore
	public void setDestroyScript(String script);

	@JsonIgnore
	public void setSetContentScript(String script);

	@JsonProperty(value = "cssURLs")
	public void setCSSHREFs(String[] cssHref);

	@JsonProperty(value = "jsURLs")
	public void setJavascriptHREFs(String[] javascriptHrefs);

	public void addJavascriptHref(String href);

	public void addCSSHref(String href);
}
