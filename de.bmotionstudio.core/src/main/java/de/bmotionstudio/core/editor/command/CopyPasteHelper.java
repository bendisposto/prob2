/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.observer.Observer;

public class CopyPasteHelper {

	private List<BControl> controlList = new ArrayList<BControl>();
	private List<Observer> observerList = new ArrayList<Observer>();
	
	private Map<BControl, BControl> alreadyCloned = new HashMap<BControl, BControl>();
	
	private int distance = 10;

	public CopyPasteHelper(ArrayList<BControl> list, int distance) {
		this.controlList = list;
		this.setDistance(distance);
	}

	public CopyPasteHelper(List<Observer> list) {
		this.observerList = list;
	}

	public void setControlList(ArrayList<BControl> list) {
		this.controlList = list;
	}

	public List<BControl> getControlList() {
		return controlList;
	}
	
	public void setObserverList(ArrayList<Observer> list) {
		this.observerList = list;
	}

	public List<Observer> getObserverList() {
		return observerList;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getDistance() {
		return distance;
	}

	public Map<BControl, BControl> getAlreadyClonedMap() {
		return alreadyCloned;
	}

}
