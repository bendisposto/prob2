/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.part;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.ButtonModel;
import org.eclipse.draw2d.ChangeEvent;
import org.eclipse.draw2d.ChangeListener;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.google.inject.Injector;

import de.bmotionstudio.core.AttributeConstants;
import de.bmotionstudio.core.editor.figure.AbstractBMotionFigure;
import de.bmotionstudio.core.editor.view.library.AbstractLibraryCommand;
import de.bmotionstudio.core.editor.view.library.AttributeRequest;
import de.bmotionstudio.core.model.VisualizationView;
import de.bmotionstudio.core.model.attribute.AbstractAttribute;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.control.BControlPropertyConstants;
import de.bmotionstudio.core.model.control.Visualization;
import de.bmotionstudio.core.model.event.Event;
import de.bmotionstudio.core.model.observer.IObserverListener;
import de.bmotionstudio.core.model.observer.Observer;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.Trace;
import de.prob.webconsole.ServletContextListener;

public abstract class BMSAbstractEditPart extends AbstractGraphicalEditPart
		implements PropertyChangeListener, IObserverListener, IAdaptable,
		NodeEditPart {
	
	private Injector injector = ServletContextListener.INJECTOR;
	final AnimationSelector selector = injector
			.getInstance(AnimationSelector.class);
	
//	private History currentHistory;
	
	private final Cursor cursorHover = new Cursor(Display.getCurrent(),
			SWT.CURSOR_HAND);
	private final Cursor cursorDefault = new Cursor(Display.getCurrent(),
			SWT.CURSOR_ARROW);
	
	protected ConnectionAnchor anchor;

	private Label tooltipLabel;
	
//	private File modelFile;
	
	private ChangeListener changeListener = new ChangeListener() {

		@Override
		public void handleStateChanged(ChangeEvent event) {

			if (!getCastedModel().getEvents().isEmpty()) {
				if (event.getPropertyName().equals(
						ButtonModel.MOUSEOVER_PROPERTY))
					getFigure().setCursor(cursorHover);
			}

			if (event.getPropertyName().equals(ButtonModel.PRESSED_PROPERTY)) {
				AbstractBMotionFigure f = (AbstractBMotionFigure) getFigure();
				Trace currentHistory = selector.getCurrentTrace();
				if (f.getModel().isPressed())
					executeEvent(currentHistory, Event.CLICK_ACTION);
			}

		}

	};

	private String[] layoutAttributes = {
			BControlPropertyConstants.PROPERTY_LAYOUT,
			BControlPropertyConstants.PROPERTY_LOCATION,
			AttributeConstants.ATTRIBUTE_X,
			AttributeConstants.ATTRIBUTE_Y, AttributeConstants.ATTRIBUTE_WIDTH,
			AttributeConstants.ATTRIBUTE_HEIGHT };

	public void activate() {
		if (!isActive()) {
			super.activate();
			((BControl) getModel()).addPropertyChangeListener(this);
			VisualizationView visualizationView = ((BControl) getModel())
					.getVisualization().getVisualizationView();
			visualizationView.addPropertyChangeListener(this);
			if (getFigure() instanceof AbstractBMotionFigure) {
				AbstractBMotionFigure af = (AbstractBMotionFigure) getFigure();
				af.activateFigure();
				updateClickable(af, visualizationView.isLocked());
			}
		}
	}

	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			((BControl) getModel()).removePropertyChangeListener(this);
			((BControl) getModel()).getVisualization().getVisualizationView()
					.removePropertyChangeListener(this);
			if (getFigure() instanceof AbstractBMotionFigure) {
				AbstractBMotionFigure af = (AbstractBMotionFigure) getFigure();
				af.deactivateFigure();
			}
		}
	}

	protected abstract IFigure createEditFigure();

	@Override
	protected void createEditPolicies() {
		prepareEditPolicies();
	}

	protected abstract void prepareEditPolicies();

	@Override
	protected IFigure createFigure() {
		IFigure fig = createEditFigure();
//		if (!(this instanceof VisualizationPart))
//			fig.setToolTip(getTooltipFigure());
		return fig;
	}

	@Override
	public void performRequest(Request req) {
		if (req.getType().equals(RequestConstants.REQ_OPEN)) {
			try {
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				page.showView(IPageLayout.ID_PROP_SHEET);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void refreshVisuals() {
		IFigure figure = getFigure();
		BControl model = (BControl) getModel();
		for (Entry<String, AbstractAttribute> e : model.getAttributes()
				.entrySet()) {
			PropertyChangeEvent evt = new PropertyChangeEvent(model,
					e.getKey(), null, e.getValue().getValue());
			refreshEditFigure(figure, model, evt);
		}
		refreshEditLayout(figure, model);
	}

	public abstract void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent pEvent);

	protected void refreshEditLayout(IFigure figure, BControl control) {
		if (!(control instanceof Visualization)) {
			figure.setPreferredSize(control.getDimension());
			if (figure.getParent() != null)
				figure.getParent().setConstraint(figure,
						new Rectangle(control.getLayout()));
		}
	}

	private void updateClickable(AbstractBMotionFigure f, boolean locked) {
		f.setLocked(locked);
		f.setEnabled(locked);
		if (locked) {
			f.addChangeListener(changeListener);
		} else {
			f.removeChangeListener(changeListener);
			f.setCursor(cursorDefault);
		}
	}
	
	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		
		final IFigure figure = (IFigure) getFigure();
		final BControl model = (BControl) getModel();
		String propName = evt.getPropertyName();

		if (evt.getSource() instanceof VisualizationView
				&& propName.equals("locked")
				&& figure instanceof AbstractBMotionFigure) {
			Boolean locked = Boolean.valueOf(evt.getNewValue().toString());
			AbstractBMotionFigure f = (AbstractBMotionFigure) figure;
			updateClickable(f, locked);
		}
		
		if (BControlPropertyConstants.SOURCE_CONNECTIONS.equals(propName)) {
			refreshSourceConnections();
		} else if (BControlPropertyConstants.TARGET_CONNECTIONS
				.equals(propName)) {
			refreshTargetConnections();
		}
		if (propName.equals(BControlPropertyConstants.PROPERTY_ADD_CHILD)
				|| propName
						.equals(BControlPropertyConstants.PROPERTY_REMOVE_CHILD)) {
			refreshChildren();
		} else if (Arrays.asList(layoutAttributes).contains(propName)) {
			// Layout attribute
			refreshEditLayout(figure, model);
		} else {
			// Custom attribute
			refreshEditFigure(figure, model, evt);
		}	
		
	}

	public List<BControl> getModelChildren() {
		return new ArrayList<BControl>();
	}

	public void executeEvent(Trace history, String event) {
		getCastedModel().executeEvent(history, event);
	}

	@Override
	public void addedObserver(BControl control, Observer observer) {
	}

	@Override
	public void removedObserver(BControl control) {

	}

	public AbstractLibraryCommand getLibraryCommand(AttributeRequest request) {
		return null;
	}

	protected BControl getCastedModel() {
		return (BControl) getModel();
	}

	protected IFigure getTooltipFigure() {
		Figure fig = new Figure();
		fig.setLayoutManager(new FlowLayout());
		tooltipLabel = new Label();
		fig.add(tooltipLabel);
		return fig;
	}
	
	protected ConnectionAnchor getConnectionAnchor() {
		if (anchor == null) {
			anchor = new ChopboxAnchor(getFigure());
		}
		return anchor;
	}

//	public void historyChange(History history) {
//
//		if (history == null || tooltipLabel == null)
//			return;
//
//		if (modelFile == null)
//			modelFile = history.getModel().getModelFile();
//
//		// Proceed only if the state can be evaluated and the visualization
//		// corresponds to the active animation
//		if (!history.getStatespace().canBeEvaluated(history.getCurrentState())
//				|| !(history.getModel().getModelFile().getName()
//						.equals(modelFile.getName())))
//			return;
//
//		if ((currentHistory != null && currentHistory.getCurrentState() == history
//				.getCurrentState()))
//			return;
//
//		System.out.println("BLUB");
//		
//		List<Event> events = getCastedModel().getEvents();
//		StringBuilder str = new StringBuilder();
//		Iterator<Event> iterator = events.iterator();
//		while (iterator.hasNext()) {
//			Event e = iterator.next();
//			str.append(e.getTooltipText(history, getCastedModel()));
//			if (iterator.hasNext())
//				str.append("\n");
//		}
//		tooltipLabel.setText(str.toString());
//		currentHistory = history;
//	}
		
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections
	 * ()
	 */
	protected List<?> getModelSourceConnections() {
		return getCastedModel().getSourceConnectionInstances();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections
	 * ()
	 */
	protected List<?> getModelTargetConnections() {
		return getCastedModel().getTargetConnectionInstances();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef
	 * .ConnectionEditPart)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connection) {
		return getConnectionAnchor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef
	 * .Request)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return getConnectionAnchor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef
	 * .ConnectionEditPart)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connection) {
		return getConnectionAnchor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef
	 * .Request)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return getConnectionAnchor();
	}
	
}
