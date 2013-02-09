package de.prob.worksheet.api.classicalB;

public class WorksheetAPIListenerEvent {
	// types
	public static final int TYPE_ERROR = 0;
	public static final int TYPE_ACTION = 1;
	public static final int TYPE_OUTPUT = 2;
	// names
	public static final int NAME_HISTORY = 0;
	public static final int NAME_STATE = 1;
	public static final int NAME_EVALUATION = 2;
	public static final int NAME_ANIMATION = 3;
	public static final int NAME_API = 4;

	public int type;
	public int name;
	public Object[] data;

}
