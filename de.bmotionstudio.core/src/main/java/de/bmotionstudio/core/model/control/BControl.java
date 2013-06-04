/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.model.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySource;

import de.bmotionstudio.core.AttributeConstants;
import de.bmotionstudio.core.BMotionImage;
import de.bmotionstudio.core.editor.command.CopyPasteHelper;
import de.bmotionstudio.core.internal.BControlPropertySource;
import de.bmotionstudio.core.model.BMotionGuide;
import de.bmotionstudio.core.model.PropertyChangeSupportObject;
import de.bmotionstudio.core.model.attribute.AbstractAttribute;
import de.bmotionstudio.core.model.attribute.AttributeSourceConnections;
import de.bmotionstudio.core.model.attribute.AttributeTargetConnections;
import de.bmotionstudio.core.model.attribute.BAttributeCoordinates;
import de.bmotionstudio.core.model.attribute.BAttributeCustom;
import de.bmotionstudio.core.model.attribute.BAttributeHeight;
import de.bmotionstudio.core.model.attribute.BAttributeID;
import de.bmotionstudio.core.model.attribute.BAttributeMisc;
import de.bmotionstudio.core.model.attribute.BAttributeSize;
import de.bmotionstudio.core.model.attribute.BAttributeVisible;
import de.bmotionstudio.core.model.attribute.BAttributeWidth;
import de.bmotionstudio.core.model.attribute.BAttributeX;
import de.bmotionstudio.core.model.attribute.BAttributeY;
import de.bmotionstudio.core.model.attribute.ConnectionList;
import de.bmotionstudio.core.model.event.Event;
import de.bmotionstudio.core.model.observer.Observer;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.Trace;

/**
 * 
 * A Control is a graphical representation of some aspects of the model.
 * Typically we use labels, images or buttons to represent informations. For
 * instance, if we model a system that has a temperature and a threshold
 * temperature that triggers a cool down, we might simply use two labels
 * displaying both values, or maybe we can incorporate both information into a
 * gauge display. It is also possible to define new controls for domain specific
 * visualizations.
 * 
 * @author Lukas Ladenberger
 * 
 */
public abstract class BControl extends PropertyChangeSupportObject implements
		IAdaptable, Cloneable {

	private List<BControl> children;

	private List<Observer> observer;

	private List<Event> events;

	private Map<String, AbstractAttribute> attributes;

	private BMotionGuide verticalGuide, horizontalGuide;

	private transient Rectangle layout = null;

	private transient Point location = null;

	private transient BControl parent;
	
	private transient boolean newControl;
	
	public BControl() {
		this.children = new ArrayList<BControl>();
		this.observer = new ArrayList<Observer>();
		this.events = new ArrayList<Event>();
		this.attributes = new HashMap<String, AbstractAttribute>();
		this.newControl = true;
		init();
	}

	protected Object readResolve() {
		// Populate parent
		for (BControl child : getChildren())
			child.setParent(this);
		this.newControl = false;
		init();
		return this;
	}

	public void removeConnection(BConnection conn) {
		if (conn == null) {
			throw new IllegalArgumentException();
		}
		if (conn.getSource().equals(getID())) {
			getSourceConnections().remove(conn.getID());
			firePropertyChange(BControlPropertyConstants.SOURCE_CONNECTIONS,
					null, conn);
		} else if (conn.getTarget().equals(getID())) {
			getTargetConnections().remove(conn.getID());
			firePropertyChange(BControlPropertyConstants.TARGET_CONNECTIONS,
					null, conn);
		}
	}

	public void addConnection(BConnection conn) {
		if (conn == null || conn.getSource().equals(conn.getTarget())) {
			throw new IllegalArgumentException();
		}
		if (conn.getSource().equals(getID())) {
			getSourceConnections().add(conn.getID());
			firePropertyChange(BControlPropertyConstants.SOURCE_CONNECTIONS,
					null, conn);
		} else if (conn.getTarget().equals(getID())) {
			getTargetConnections().add(conn.getID());
			firePropertyChange(BControlPropertyConstants.TARGET_CONNECTIONS,
					null, conn);
		}
	}

	protected void init() {
		// Init standard control attributes
		initStandardAttributes();
		// Init custom control attributes
		initAttributes();
	}

	private void initStandardAttributes() {

		// Init unique ID
		String ID;
		if (this instanceof Visualization)
			ID = "visualization";
		// TODO: Reimplement me!!!
		// else if (visualization == null)
		ID = UUID.randomUUID().toString();
		// else
		// ID = (visualization.getMaxIDString(type));

		BAttributeID aID = new BAttributeID(ID);
		aID.setGroup(AbstractAttribute.ROOT);
		initAttribute(aID);

		BAttributeMisc aMisc = new BAttributeMisc("");
		aMisc.setGroup(AbstractAttribute.ROOT);
		initAttribute(aMisc);
		
		// Init location and size attributes
		BAttributeCoordinates aCoordinates = new BAttributeCoordinates(null);
		aCoordinates.setGroup(AbstractAttribute.ROOT);
		initAttribute(aCoordinates);
		
		BAttributeX aX = new BAttributeX(100);
		aX.setGroup(aCoordinates);
		initAttribute(aX);
		
		BAttributeY aY = new BAttributeY(100);
		aY.setGroup(aCoordinates);
		initAttribute(aY);
		
		BAttributeSize aSize = new BAttributeSize(null);
		aSize.setGroup(AbstractAttribute.ROOT);
		initAttribute(aSize);
		
		BAttributeWidth aWidth = new BAttributeWidth(100);
		aWidth.setGroup(aSize);
		initAttribute(aWidth);

		BAttributeHeight aHeight = new BAttributeHeight(100);
		aHeight.setGroup(aSize);
		initAttribute(aHeight);

		// Init visible and this attribute
		BAttributeVisible aVisible = new BAttributeVisible(true);
		aVisible.setGroup(AbstractAttribute.ROOT);
		initAttribute(aVisible);

		BAttributeCustom aCustom = new BAttributeCustom("");
		aCustom.setGroup(AbstractAttribute.ROOT);
		initAttribute(aCustom);

		AttributeSourceConnections aSourceConnections = new AttributeSourceConnections(
				new ConnectionList());
		aSourceConnections.setGroup(AbstractAttribute.ROOT);
		initAttribute(aSourceConnections);

		AttributeTargetConnections aTargetConnections = new AttributeTargetConnections(
				new ConnectionList());
		aTargetConnections.setGroup(AbstractAttribute.ROOT);
		initAttribute(aTargetConnections);

	}

	protected abstract void initAttributes();

	public String getID() {
		return getAttributeValue(AttributeConstants.ATTRIBUTE_ID).toString();
	}

	public AbstractAttribute getAttribute(String attributeID) {
		return getAttributes().get(attributeID);
	}

	public Object getAttributeValue(String attributeID) {

		AbstractAttribute atr = attributes.get(attributeID);

		if (atr != null) {
			return atr.getValue();
		} else {
			// TODO: handle error/exception (no such attribute)
			return null;
		}

	}

	public boolean setAttributeValue(String attributeID, Object value) {
		return setAttributeValue(attributeID, value, true, true);
	}

	public boolean setAttributeValue(String attributeID, Object value,
			Boolean firePropertyChange) {
		return setAttributeValue(attributeID, value, firePropertyChange, true);
	}

	public boolean setAttributeValue(String attributeID, Object value,
			Boolean firePropertyChange, Boolean setInitVal) {

		AbstractAttribute atr = attributes.get(attributeID);

		if (atr == null) {
			return false;
			// TODO: Throw some error!?!
		}

		atr.setControl(this);

		if ((atr.getValue() != null && atr.getValue().equals(value))
				|| !atr.isEditable())
			return true;

		atr.setValue(value, firePropertyChange, setInitVal);

		return true;

	}

	public void restoreDefaultValue(String attributeID) {
		AbstractAttribute atr = attributes.get(attributeID);
		if (atr != null) {
			atr.restoreValue();
			Object oldVal = atr.getValue();
			Object initValue = atr.getInitValue();
			firePropertyChange(attributeID, oldVal, initValue);
		}
	}

	public boolean hasAttribute(String attributeID) {
		return attributes.containsKey(attributeID);
	}

	public void setLayout(Rectangle newLayout) {
		Rectangle oldLayout = getLayout();
		layout = newLayout;
		setAttributeValue(AttributeConstants.ATTRIBUTE_X, newLayout.x, false);
		setAttributeValue(AttributeConstants.ATTRIBUTE_Y, newLayout.y, false);
		setAttributeValue(AttributeConstants.ATTRIBUTE_WIDTH, newLayout.width,
				false);
		setAttributeValue(AttributeConstants.ATTRIBUTE_HEIGHT,
				newLayout.height, false);
		firePropertyChange(BControlPropertyConstants.PROPERTY_LAYOUT,
				oldLayout, newLayout);
	}

	public Rectangle getLayout() {

		String widthStr = getAttributeValue(AttributeConstants.ATTRIBUTE_WIDTH)
				.toString();
		String heightStr = getAttributeValue(
				AttributeConstants.ATTRIBUTE_HEIGHT).toString();
		String xStr = getAttributeValue(AttributeConstants.ATTRIBUTE_X)
				.toString();
		String yStr = getAttributeValue(AttributeConstants.ATTRIBUTE_Y)
				.toString();

		// TODO: check if strings are a correct integers

		try {

			int width = Integer.valueOf(widthStr);
			int height = Integer.valueOf(heightStr);
			int x = Integer.valueOf(xStr);
			int y = Integer.valueOf(yStr);

			if (layout == null) {
				layout = new Rectangle(x, y, width, height);
			} else {
				layout.x = x;
				layout.y = y;
				layout.width = width;
				layout.height = height;
			}

		} catch (NumberFormatException e) {
			// We ignore number format exceptions, however we should return an
			// error message here
			// TODO: return error message
		}

		return layout;

	}

	public void setLocation(Point newLocation) {
		Point oldLocation = getLocation();
		location = newLocation;
		setAttributeValue(AttributeConstants.ATTRIBUTE_X, newLocation.x, false);
		setAttributeValue(AttributeConstants.ATTRIBUTE_Y, newLocation.y, false);
		firePropertyChange(BControlPropertyConstants.PROPERTY_LOCATION,
				oldLocation, newLocation);
	}

	public Point getLocation() {
		int x = Integer.valueOf(getAttributeValue(
				AttributeConstants.ATTRIBUTE_X).toString());
		int y = Integer.valueOf(getAttributeValue(
				AttributeConstants.ATTRIBUTE_Y).toString());
		if (location == null) {
			location = new Point(x, y);
		} else {
			location.x = x;
			location.y = y;
		}
		return location;
	}

	public Dimension getDimension() {
		int width = Integer.valueOf(getAttributeValue(
				AttributeConstants.ATTRIBUTE_WIDTH).toString());
		int height = Integer.valueOf(getAttributeValue(
				AttributeConstants.ATTRIBUTE_HEIGHT).toString());
		return new Dimension(width, height);
	}

	public void addChild(BControl child) {
		addChild(child, -1);
	}

	public void addChild(BControl child, int index) {
		child.setParent(this);
		if (index >= 0) {
			children.add(index, child);
		} else {
			children.add(child);
		}
		firePropertyChange(BControlPropertyConstants.PROPERTY_ADD_CHILD, index,
				child);
	}

	public void removeAllChildren() {
		getChildren().clear();
		firePropertyChange(BControlPropertyConstants.PROPERTY_REMOVE_CHILD,
				null, null);
	}

	public boolean removeChild(int index) {
		BControl control = children.get(index);
		return removeChild(control);
	}

	public boolean removeChild(BControl child) {
		boolean b = children.remove(child);
		if (b)
			firePropertyChange(BControlPropertyConstants.PROPERTY_REMOVE_CHILD,
					child, null);
		return b;
	}

	public List<BControl> getChildren() {
		if (children == null)
			children = new ArrayList<BControl>();
		return children;
	}

	public void setChildrenArray(List<BControl> children) {
		this.children = children;
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}

	public BControl getChild(String ID) {
		for (BControl bcontrol : children) {
			String bcontrolID = bcontrol.getAttributeValue(
					AttributeConstants.ATTRIBUTE_ID).toString();
			if (bcontrolID != null) {
				if (bcontrolID.equals(ID))
					return bcontrol;
			}
		}
		return null;
	}

	public List<Observer> getObservers() {
		if (observer == null)
			observer = new ArrayList<Observer>();
		return observer;
	}

	public void addObserver(Observer observer) {
		getObservers().add(observer);
		firePropertyChange(BControlPropertyConstants.PROPERTY_ADD_OBSERVER,
				observer, null);
	}

	public void removeObserver(Observer observer) {
		if (getObservers().remove(observer)) {
			firePropertyChange(
					BControlPropertyConstants.PROPERTY_REMOVE_OBSERVER, observer, null);
		}
	}

	public List<Event> getEvents() {
		if (events == null)
			events = new ArrayList<Event>();
		return events;
	}
	
	public void addEvent(Event event) {
		getEvents().add(event);
		firePropertyChange(BControlPropertyConstants.PROPERTY_ADD_EVENT, event,
				null);
	}

	public void removeEvent(Event event) {
		if (getEvents().remove(event))
			firePropertyChange(BControlPropertyConstants.PROPERTY_REMOVE_EVENT,
					event, null);
	}

	public Map<String, AbstractAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, AbstractAttribute> attributes) {
		this.attributes = attributes;
	}

	public void setParent(BControl parent) {
		this.parent = parent;
	}

	public BControl getParent() {
		return this.parent;
	}

	public Visualization getVisualization() {
		return getVisualizationRecursive(getParent());
	}
	
	private Visualization getVisualizationRecursive(BControl parent) {
		if (parent instanceof Visualization)
			return (Visualization) parent;
		else
			return getVisualizationRecursive(parent.getParent());
	}
	
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IPropertySource.class) {
			return new BControlPropertySource(this);
		}
		return null;
	}

	public boolean contains(BControl child) {
		return children.contains(child);
	}

	@Override
	public BControl clone() throws CloneNotSupportedException {

		BControl clonedControl = (BControl) super.clone();

		clonedControl.setParent(getParent());

		// Clone attributes
		Map<String, AbstractAttribute> newProperties = new HashMap<String, AbstractAttribute>();
		for (Entry<String, AbstractAttribute> e : getAttributes().entrySet()) {
			AbstractAttribute idAtr = e.getValue().clone();
			newProperties.put(e.getKey(), idAtr);
		}
		clonedControl.setAttributes(newProperties);
		// TODO Reimplement me!!!
		clonedControl.setAttributeValue(AttributeConstants.ATTRIBUTE_ID, UUID
				.randomUUID().toString());

		// Clone children
		clonedControl.setChildrenArray(new ArrayList<BControl>());
		Iterator<BControl> it = getChildren().iterator();
		while (it.hasNext()) {
			BControl next = (BControl) it.next();
			BControl childClone = next.clone();
			CopyPasteHelper cHelper = (CopyPasteHelper) Clipboard.getDefault()
					.getContents();
			if (cHelper != null)
				cHelper.getAlreadyClonedMap().put(next, childClone);
			clonedControl.addChild(childClone);
		}

		// Clone observer
		clonedControl.setObservers(new ArrayList<Observer>());
		for (Observer observer : getObservers()) {
			clonedControl.addObserver(observer.clone());
		}

		// Clone events
		clonedControl.setEvents(new ArrayList<Event>());
		for (Event event : getEvents()) {
			clonedControl.addEvent(event.clone());
		}

		return clonedControl;

	}

	public Map<Observer, List<IEvalElement>> prepareObserver(Trace history) {
		Map<Observer, List<IEvalElement>> formulaMap = new HashMap<Observer, List<IEvalElement>>();
		for (Observer observer : getObservers())
			formulaMap.put(observer, prepareObserver(observer, history));
		return formulaMap;
	}

	public List<IEvalElement> prepareObserver(Observer observer, Trace history) {
		return observer.prepareObserver(history, BControl.this);
	}
	
	public void checkObserver(Trace history,
			Map<String, EvaluationResult> results) {

		for (AbstractAttribute a : getAttributes().values())
			a.restoreValue();

		for (Observer observer : getObservers()) {
			observer.check(history, BControl.this, results);
		}
		Visualization visualization = getVisualization();
		// TODO: Currently connection observer are checked twice (source +
		// target) => change this, so that observer are checked only on time per
		// state!!!
		for (String con : getSourceConnections().getConnections()) {
			BConnection connection = visualization.getConnection(con);
			if (connection != null)
				connection.checkObserver(history, results);
		}
		for (String con : getTargetConnections().getConnections()) {
			BConnection connection = visualization.getConnection(con);
			if (connection != null)
				connection.checkObserver(history, results);
		}

	}
	
	public void afterCheckObserver(Trace history) {
		for (Observer observer : getObservers()) {
			observer.afterCheck(history, BControl.this);
		}
		Visualization visualization = getVisualization();
		// TODO: Currently connection observer are checked twice (source +
		// target) => change this, so that observer are checked only on time per
		// state!!!
		for (String con : getSourceConnections().getConnections()) {
			BConnection connection = visualization.getConnection(con);
			if (connection != null)
				connection.afterCheckObserver(history);
		}
		for (String con : getTargetConnections().getConnections()) {
			BConnection connection = visualization.getConnection(con);
			if (connection != null)
				connection.afterCheckObserver(history);
		}
	}

	public void executeEvent(Trace history, String event) {
		if (hasAttribute(AttributeConstants.ATTRIBUTE_ENABLED)) {
			if (!(Boolean) getAttributeValue(AttributeConstants.ATTRIBUTE_ENABLED))
				return;
		}
		for (Event e : getEvents()) {
			e.execute(history, this);
		}
	}

	public void setVerticalGuide(BMotionGuide verticalGuide) {
		this.verticalGuide = verticalGuide;
	}

	public BMotionGuide getVerticalGuide() {
		return verticalGuide;
	}

	public void setHorizontalGuide(BMotionGuide horizontalGuide) {
		this.horizontalGuide = horizontalGuide;
	}

	public BMotionGuide getHorizontalGuide() {
		return horizontalGuide;
	}

	/**
	 * Return a List of outgoing Connections.
	 */
	public ConnectionList getSourceConnections() {
		AbstractAttribute atrSourceConnections = getAttribute(AttributeConstants.ATTRIBUTE_SOURCE_CONNECTIONS);
		return (ConnectionList) atrSourceConnections.getValue();
	}

	/**
	 * Return a List of incoming Connections.
	 */
	public ConnectionList getTargetConnections() {
		AbstractAttribute atrTargetConnections = getAttribute(AttributeConstants.ATTRIBUTE_TARGET_CONNECTIONS);
		return (ConnectionList) atrTargetConnections.getValue();
	}

	/**
	 * Return a List of outgoing Connections.
	 */
	public List<BConnection> getSourceConnectionInstances() {
		List<BConnection> connectionInstanceList = new ArrayList<BConnection>();
		List<String> sourceConnections = getSourceConnections().getConnections();
		for (String con : sourceConnections) {
			BConnection connection = getVisualization().getConnection(con);
			if (connection != null)
				connectionInstanceList.add(connection);
		}
		return connectionInstanceList;
	}

	/**
	 * Return a List of incoming Connections.
	 */
	public List<BConnection> getTargetConnectionInstances() {
		List<BConnection> connectionInstanceList = new ArrayList<BConnection>();
		List<String> targetConnections = getTargetConnections().getConnections();
		for (String con : targetConnections) {
			BConnection connection = getVisualization().getConnection(con);
			if (connection != null)
				connectionInstanceList.add(connection);
		}
		return connectionInstanceList;
	}

	
	public boolean hasConnections() {
		return !getTargetConnections().getConnections().isEmpty()
				|| !getSourceConnections().getConnections().isEmpty();
	}

	public void setObservers(List<Observer> observers) {
		this.observer = observers;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	protected void initAttribute(AbstractAttribute atr) {

		AbstractAttribute oldAtr = getAttribute(atr.getID());

		// If a new control is created via the editor (not from the saved file)
		// set the saved value of the file
		if (oldAtr != null && !newControl) {
			atr.setValue(oldAtr.getValue());
			atr.setDefaultValue(oldAtr.getDefaultValue());
		}
		
		getAttributes().put(atr.getID(), atr);

	}

	public boolean canHaveChildren() {
		return false;
	}

	public String getValueOfData() {
		return getAttributeValue(AttributeConstants.ATTRIBUTE_CUSTOM)
				.toString();
	}

	public Image getIcon() {
		return BMotionImage.getControlImage(getClass().getName());
	}

}
