package de.prob.model.representation;


/**
 * Allows adding additional information to any element within the model domain
 * @author joy
 *
 */
public class ElementComment extends AbstractElement {
	private String comment;

	public ElementComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}
}
