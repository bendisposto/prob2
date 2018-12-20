package de.prob.animator.domainobjects;

import java.util.Objects;

public class AnimationMatrixEntry {
	public static final class Image extends AnimationMatrixEntry {
		private final int imageNumber;
		
		public Image(final int row, final int column, final int imageNumber) {
			super(row, column);
			
			this.imageNumber = imageNumber;
		}
		
		public int getImageNumber() {
			return this.imageNumber;
		}
	}
	
	public static final class Text extends AnimationMatrixEntry {
		private final String text;
		
		public Text(final int row, final int column, final String text) {
			super(row, column);
			
			Objects.requireNonNull(text, "text");
			
			this.text = text;
		}
		
		public String getText() {
			return this.text;
		}
	}
	
	private final int row;
	private final int column;
	
	AnimationMatrixEntry(final int row, final int column) {
		super();
		
		this.row = row;
		this.column = column;
	}
	
	public int getRow() {
		return this.row;
	}
	
	public int getColumn() {
		return this.column;
	}
}
