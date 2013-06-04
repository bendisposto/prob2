package de.bmotionstudio.core.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.RedoAction;
import org.eclipse.gef.ui.actions.SelectAllAction;
import org.eclipse.gef.ui.actions.ToggleGridAction;
import org.eclipse.gef.ui.actions.ToggleRulerVisibilityAction;
import org.eclipse.gef.ui.actions.ToggleSnapToGeometryAction;
import org.eclipse.gef.ui.actions.UndoAction;
import org.eclipse.gef.ui.actions.UpdateAction;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.gef.ui.rulers.RulerComposite;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;

import com.google.inject.Injector;

import de.bmotionstudio.core.ActionConstants;
import de.bmotionstudio.core.editor.action.AddEventAction;
import de.bmotionstudio.core.editor.action.AddObserverAction;
import de.bmotionstudio.core.editor.action.CopyAction;
import de.bmotionstudio.core.editor.action.CopyEventAction;
import de.bmotionstudio.core.editor.action.CopyObserverAction;
import de.bmotionstudio.core.editor.action.LockVisualizationAction;
import de.bmotionstudio.core.editor.action.PasteAction;
import de.bmotionstudio.core.editor.action.PasteEventAction;
import de.bmotionstudio.core.editor.action.PasteObserverAction;
import de.bmotionstudio.core.editor.action.RemoveEventAction;
import de.bmotionstudio.core.editor.action.RemoveObserverAction;
import de.bmotionstudio.core.editor.action.SaveAction;
import de.bmotionstudio.core.editor.part.BMSEditPartFactory;
import de.bmotionstudio.core.editor.view.library.AttributeTransferDropTargetListener;
import de.bmotionstudio.core.editor.view.outline.BMotionOutlinePage;
import de.bmotionstudio.core.editor.view.property.BMotionPropertySheetPage;
import de.bmotionstudio.core.model.BMotionRuler;
import de.bmotionstudio.core.model.VisualizationView;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.control.Visualization;
import de.bmotionstudio.core.model.observer.Observer;
import de.bmotionstudio.core.util.BMotionUtil;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.Trace;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.webconsole.ServletContextListener;

public class VisualizationViewPart extends ViewPart implements
		CommandStackListener, PropertyChangeListener, IAnimationChangeListener,
		ITabbedPropertySheetPageContributor, ISaveablePart2 {

	public static String ID = "de.bmotionstudio.core.view.VisualizationView";
	
	private Injector injector = ServletContextListener.INJECTOR;
	
	private EditDomain editDomain;

	private VisualizationView visualizationView;

	private ActionRegistry actionRegistry;

	private BMotionSelectionSynchronizer selectionSynchronizer;

	private RulerComposite container;

	private GraphicalViewer graphicalViewer;

	private ScalableRootEditPart rootEditPart;

	private boolean isInitialized = false;

	private Composite parent;
	
	private KeyHandler sharedKeyHandler;
	
	private File visualizationFile;
	
	private boolean dirty;
	
	private File modelFile;
	
	private List<String> selectionActions = new ArrayList<String>();
	private List<String> stackActions = new ArrayList<String>();
	private List<String> propertyActions = new ArrayList<String>();
	
	private String[] viewerProperties = new String[] {
			SnapToGrid.PROPERTY_GRID_VISIBLE, SnapToGrid.PROPERTY_GRID_ENABLED,
			RulerProvider.PROPERTY_RULER_VISIBILITY,
			SnapToGeometry.PROPERTY_SNAP_ENABLED };

	private PropertyChangeListener viewerListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			String propertyName = event.getPropertyName();
			if (Arrays.asList(viewerProperties).contains(propertyName)) {
				setDirty(true);
			}
		}
	};
	
	private ZoomListener zoomListener = new ZoomListener() {
		@Override
		public void zoomChanged(double zoom) {
			setDirty(true);
		}
	};

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class type) {

		// // Adapter for zoom manager
		if (type == ZoomManager.class)
			return ((ScalableRootEditPart) getGraphicalViewer()
					.getRootEditPart()).getZoomManager();

		// Adapter for content outline page
		if (type == IContentOutlinePage.class) {
			return new BMotionOutlinePage(this);
		}

		if (type == ActionRegistry.class)
			return getActionRegistry();

		if (type == CommandStack.class)
			return getCommandStack();

		// Adapter for property page
		if (type == IPropertySheetPage.class) {
			// BMotionPropertyPage page = new BMotionPropertyPage(
			// getCommandStack(), getActionRegistry().getAction(
			// ActionFactory.UNDO.getId()), getActionRegistry()
			// .getAction(ActionFactory.REDO.getId()));
			// page.setRootEntry(new CustomSortPropertySheetEntry(
			// getCommandStack()));
			return new BMotionPropertySheetPage(this);
		}
		
		if (type == IPropertySheetEntry.class) {
			return new CustomSortPropertySheetEntry(getCommandStack());
		}
		
		if (type == ActionRegistry.class) {
			return getActionRegistry();
		}

		return super.getAdapter(type);

	}

	public SelectionSynchronizer getSelectionSynchronizer() {
		if (selectionSynchronizer == null)
			selectionSynchronizer = new BMotionSelectionSynchronizer();
		return selectionSynchronizer;
	}

	public GraphicalViewer getGraphicalViewer() {
		return graphicalViewer;
	}
	
	/**
	 * Lazily creates and returns the action registry.
	 * 
	 * @return the action registry
	 */
	public ActionRegistry getActionRegistry() {
		if (actionRegistry == null)
			actionRegistry = new ActionRegistry();
		return actionRegistry;
	}

	public EditDomain getEditDomain() {
		return editDomain;
	}

	protected CommandStack getCommandStack() {
		if (getEditDomain() != null)
			return getEditDomain().getCommandStack();
		return null;
	}

	public VisualizationView getVisualizationView() {
		return visualizationView;
	}

	private void createActions() {

		ActionRegistry registry = getActionRegistry();
		IAction action;

		action = new UndoAction(this);
		registry.registerAction(action);
		getStackActions().add(action.getId());

		action = new RedoAction(this);
		registry.registerAction(action);
		getStackActions().add(action.getId());

		action = new CopyAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new PasteAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new DeleteAction((IWorkbenchPart) this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());
		
		action = new SelectAllAction(this);
		registry.registerAction(action);

		ZoomManager manager = rootEditPart.getZoomManager();
		getActionRegistry().registerAction(new ZoomInAction(manager));
		getActionRegistry().registerAction(new ZoomOutAction(manager));

		double[] zoomLevels = new double[] { 0.25, 0.5, 0.75, 1.0, 1.5, 2.0,
				2.5, 3.0, 4.0, 5.0, 10.0, 20.0 };
		manager.setZoomLevels(zoomLevels);
		ArrayList<String> zoomContributions = new ArrayList<String>();
		zoomContributions.add(ZoomManager.FIT_ALL);
		zoomContributions.add(ZoomManager.FIT_HEIGHT);
		zoomContributions.add(ZoomManager.FIT_WIDTH);
		manager.setZoomLevelContributions(zoomContributions);
		
		getActionRegistry().registerAction(
				new ToggleRulerVisibilityAction(getGraphicalViewer()) {
					@Override
					public void run() {
						super.run();
						setChecked(!isChecked());
					}
				});
		getActionRegistry().registerAction(
				new ToggleSnapToGeometryAction(getGraphicalViewer()) {
					@Override
					public void run() {
						super.run();
						setChecked(!isChecked());
					}
				});
		getActionRegistry().registerAction(
				new ToggleGridAction(getGraphicalViewer()) {
					@Override
					public void run() {
						super.run();
						setChecked(!isChecked());
					}
				});

		action = new LockVisualizationAction((IWorkbenchPart) this);
		getActionRegistry().registerAction(action);
		getStackActions().add(action.getId());
		
		installObserverActions();
		installEventActions();
		
	}

	private void installObserverActions() {

		IAction action;
		ActionRegistry registry = getActionRegistry();

		// Register Add Observer Actions
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = reg
				.getExtensionPoint("de.bmotionstudio.core.observer");
		for (IExtension extension : extensionPoint.getExtensions()) {
			for (IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {

				if ("observer".equals(configurationElement.getName())) {

					String oID = configurationElement.getAttribute("id");
					action = new AddObserverAction(this);
					action.setId("de.bmotionstudio.core.observerAction." + oID);
					((AddObserverAction) action).setObserverId(oID);
					registry.registerAction(action);
					getSelectionActions().add(
							"de.bmotionstudio.core.observerAction." + oID);

				}

			}

		}
		
		// Register Remove Observer Action
		RemoveObserverAction removeObserverAction = new RemoveObserverAction(
				this);
		removeObserverAction.setId(ActionConstants.ACTION_REMOVE_OBSERVER);
		registry.registerAction(removeObserverAction);

		CopyObserverAction copyObserverAction = new CopyObserverAction(this);
		copyObserverAction.setId(ActionConstants.ACTION_COPY_OBSERVER);
		registry.registerAction(copyObserverAction);

		PasteObserverAction pasteObserverAction = new PasteObserverAction(this);
		pasteObserverAction.setId(ActionConstants.ACTION_PASTE_OBSERVER);
		registry.registerAction(pasteObserverAction);
		
	}

	private void installEventActions() {

		IAction action;
		ActionRegistry registry = getActionRegistry();

		// Register Add Observer Actions
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = reg
				.getExtensionPoint("de.bmotionstudio.core.event");
		for (IExtension extension : extensionPoint.getExtensions()) {
			for (IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {

				if ("event".equals(configurationElement.getName())) {

					String oID = configurationElement.getAttribute("id");
					action = new AddEventAction(this);
					action.setId("de.bmotionstudio.core.eventAction." + oID);
					((AddEventAction) action).setEventId(oID);
					registry.registerAction(action);
					getSelectionActions().add(
							"de.bmotionstudio.core.eventAction." + oID);

				}

			}

		}

		RemoveEventAction removeAction = new RemoveEventAction(this);
		removeAction.setId(ActionConstants.ACTION_REMOVE_EVENT);
		registry.registerAction(removeAction);

		CopyEventAction copyAction = new CopyEventAction(this);
		copyAction.setId(ActionConstants.ACTION_COPY_EVENT);
		registry.registerAction(copyAction);

		PasteEventAction pasteAction = new PasteEventAction(this);
		pasteAction.setId(ActionConstants.ACTION_PASTE_EVENT);
		registry.registerAction(pasteAction);

	}
	
	@Override
	public void dispose() {
		unregister();
		super.dispose();
	}

	private void unregister() {
		if (getCommandStack() != null)
			getCommandStack().removeCommandStackListener(this);
		if (getActionRegistry() != null)
			getActionRegistry().dispose();
		if (getVisualizationView() != null)
			getVisualizationView().removePropertyChangeListener(this);
		if (getGraphicalViewer() != null)
			getGraphicalViewer().removePropertyChangeListener(viewerListener);
		if (rootEditPart != null)
			rootEditPart.getZoomManager().removeZoomListener(zoomListener);
		setInitialized(false);
	}

	@Override
	public void commandStackChanged(EventObject event) {
		updateActions(stackActions);
		setDirty(getCommandStack().isDirty());
	}

	/**
	 * A convenience method for updating a set of actions defined by the given
	 * List of action IDs. The actions are found by looking up the ID in the
	 * {@link #getActionRegistry() action registry}. If the corresponding action
	 * is an {@link UpdateAction}, it will have its <code>update()</code> method
	 * called.
	 * 
	 * @param actionIds
	 *            the list of IDs to update
	 */
	protected void updateActions(List<String> actionIds) {
		ActionRegistry registry = getActionRegistry();
		Iterator<String> iter = actionIds.iterator();
		while (iter.hasNext()) {
			IAction action = registry.getAction(iter.next());
			if (action instanceof UpdateAction) {
				((UpdateAction) action).update();
			}
		}
	}

	protected List<String> getStackActions() {
		return stackActions;
	}

	protected List<String> getPropertyActions() {
		return propertyActions;
	}

	protected List<String> getSelectionActions() {
		return selectionActions;
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		this.container = new RulerComposite(parent, SWT.NONE);
		this.editDomain = new EditDomain();	
	}
	
	@Override
	public void setFocus() {
		this.container.setFocus();
	}

	public void init(File visualizationFile) {
		this.visualizationFile = visualizationFile;
		init(BMotionUtil.getVisualizationViewFromFile(visualizationFile));
	}

	public void init(VisualizationView visualizationView) {
		this.visualizationView = visualizationView;
		this.visualizationView.addPropertyChangeListener(this);
		this.graphicalViewer = new ScrollingGraphicalViewer();
		this.graphicalViewer.createControl(this.container);
		this.editDomain.getCommandStack().addCommandStackListener(this);
		Visualization visualization = visualizationView.getVisualization();
		configureGraphicalViewer();
		hookGraphicalViewer();
		createActions();
		buildActions();
		createMenu(getViewSite());
		getGraphicalViewer().setContents(visualization);
		final AnimationSelector selector = injector
				.getInstance(AnimationSelector.class);
		Trace currentHistory = selector.getCurrentTrace();
		String partName = visualizationView.getName();
		if (currentHistory != null) {
			modelFile = currentHistory.getModel().getModelFile();
			partName = partName + " (" + modelFile.getName() + ")";
			selector.registerAnimationChangeListener(this);
			setInitialized(true);
		}
		setPartName(partName);
	}
	
	protected void hookGraphicalViewer() {
		getSelectionSynchronizer().addViewer(getGraphicalViewer());
		getSite().setSelectionProvider(getGraphicalViewer());
	}

	public void configureGraphicalViewer() {

		rootEditPart = new ScalableRootEditPart();
		rootEditPart.setViewer(graphicalViewer);
		graphicalViewer.setRootEditPart(rootEditPart);
		graphicalViewer.setEditPartFactory(new BMSEditPartFactory());
		container
				.setGraphicalViewer((ScrollingGraphicalViewer) graphicalViewer);
		graphicalViewer.setEditDomain(getEditDomain());
		graphicalViewer.setKeyHandler(new GraphicalViewerKeyHandler(
				graphicalViewer).setParent(getCommonKeyHandler()));
		graphicalViewer
				.addDropTargetListener(new BControlTransferDropTargetListener(
						graphicalViewer));
		graphicalViewer.getControl().setBackground(ColorConstants.white);
		graphicalViewer
				.addDropTargetListener(new AttributeTransferDropTargetListener(
						graphicalViewer, getSite().getPart()));
		graphicalViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						updateActions(selectionActions);
					}
				});
		ContextMenuProvider provider = new BMSContextMenuProvider(
				graphicalViewer, getActionRegistry(),
				visualizationView.getLanguage());
		graphicalViewer.setContextMenu(provider);
		
		loadProperties(visualizationView);
		
		graphicalViewer.addPropertyChangeListener(viewerListener);
		rootEditPart.getZoomManager().addZoomListener(zoomListener);
		
	}

	private void buildActions() {
				
		IActionBars bars = getViewSite().getActionBars();
		ActionRegistry ar = getActionRegistry();
		
		bars.setGlobalActionHandler(ActionFactory.UNDO.getId(),
				ar.getAction(ActionFactory.UNDO.getId()));
		bars.setGlobalActionHandler(ActionFactory.REDO.getId(),
				ar.getAction(ActionFactory.REDO.getId()));

		bars.setGlobalActionHandler(ActionFactory.COPY.getId(),
				ar.getAction(ActionFactory.COPY.getId()));
		bars.setGlobalActionHandler(ActionFactory.PASTE.getId(),
				ar.getAction(ActionFactory.PASTE.getId()));

		bars.setGlobalActionHandler(ActionFactory.DELETE.getId(),
				ar.getAction(ActionFactory.DELETE.getId()));

		bars.updateActionBars();

	}

	private void createMenu(final IViewSite viewSite) {

		viewSite.getActionBars().getToolBarManager()
				.add(getActionRegistry().getAction(ActionFactory.UNDO.getId()));
		viewSite.getActionBars().getToolBarManager()
				.add(getActionRegistry().getAction(ActionFactory.REDO.getId()));
		viewSite.getActionBars().getToolBarManager()
				.add(getActionRegistry().getAction(ActionFactory.COPY.getId()));
		viewSite
				.getActionBars()
				.getToolBarManager()
				.add(getActionRegistry().getAction(ActionFactory.PASTE.getId()));
		viewSite
				.getActionBars()
				.getToolBarManager()
				.add(getActionRegistry()
						.getAction(ActionFactory.DELETE.getId()));

		viewSite.getActionBars().getToolBarManager().add(new Separator());

		viewSite.getActionBars()
				.getToolBarManager()
				.add(getActionRegistry().getAction(
						ActionConstants.ACTION_LOCK_VISUALIZATION));

		ZoomComboContributionItem zoomCombo = new ZoomComboContributionItem(
				getSite().getPage()) {
			protected int computeWidth(Control control) {
				return 75;
			}
		};
		zoomCombo.setZoomManager(rootEditPart.getZoomManager());
		viewSite.getActionBars().getToolBarManager().add(zoomCombo);

		viewSite.getActionBars().getToolBarManager()
				.add(getActionRegistry().getAction(GEFActionConstants.ZOOM_IN));
		viewSite.getActionBars()
				.getToolBarManager()
				.add(getActionRegistry().getAction(GEFActionConstants.ZOOM_OUT));

		viewSite.getActionBars()
				.getMenuManager()
				.add(getActionRegistry().getAction(
						GEFActionConstants.TOGGLE_GRID_VISIBILITY));
		viewSite.getActionBars()
				.getMenuManager()
				.add(getActionRegistry().getAction(
						GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY));
		viewSite.getActionBars()
				.getMenuManager()
				.add(getActionRegistry().getAction(
						GEFActionConstants.TOGGLE_RULER_VISIBILITY));

		viewSite.getActionBars().updateActionBars();

	}

	protected void loadProperties(VisualizationView visualizationView) {

		// Ruler properties
		BMotionRuler ruler = visualizationView.getRuler(PositionConstants.WEST);
		RulerProvider provider = null;
		if (ruler != null)
			provider = new BMotionRulerProvider(ruler);
		getGraphicalViewer().setProperty(RulerProvider.PROPERTY_VERTICAL_RULER,
				provider);
		ruler = visualizationView.getRuler(PositionConstants.NORTH);
		provider = null;
		if (ruler != null)
			provider = new BMotionRulerProvider(ruler);
		getGraphicalViewer().setProperty(
				RulerProvider.PROPERTY_HORIZONTAL_RULER, provider);
		getGraphicalViewer().setProperty(
				RulerProvider.PROPERTY_RULER_VISIBILITY,
				visualizationView.isRulerVisible());

		getGraphicalViewer().setProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED,
				visualizationView.isSnapToGeometryEnabled());
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_ENABLED,
				visualizationView.isGridEnabled());
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE,
				visualizationView.isGridEnabled());

		getGraphicalViewer().setProperty(
				MouseWheelHandler.KeyGenerator.getKey(SWT.NONE),
				MouseWheelZoomHandler.SINGLETON);

		rootEditPart.getZoomManager().setZoom(visualizationView.getZoom());

	}
	
	protected void saveProperties() {
		getVisualizationView().setRulerVisible(
				((Boolean) getGraphicalViewer().getProperty(
						RulerProvider.PROPERTY_RULER_VISIBILITY))
						.booleanValue());
		getVisualizationView().setGridEnabled(
				((Boolean) getGraphicalViewer().getProperty(
						SnapToGrid.PROPERTY_GRID_ENABLED)).booleanValue());
		getVisualizationView().setSnapToGeometry(
				((Boolean) getGraphicalViewer().getProperty(
						SnapToGeometry.PROPERTY_SNAP_ENABLED)).booleanValue());
		getVisualizationView().setZoom(rootEditPart.getZoomManager().getZoom());
	}
	
	/**
	 * Returns the KeyHandler with common bindings for both the Outline and
	 * Graphical Views. For example, delete is a common action.
	 */
	public KeyHandler getCommonKeyHandler() {

		if (sharedKeyHandler == null) {

			sharedKeyHandler = new KeyHandler();

			sharedKeyHandler.put(
					KeyStroke.getPressed(SWT.F2, 0),
					getActionRegistry().getAction(
							GEFActionConstants.DIRECT_EDIT));

			sharedKeyHandler
					.put(KeyStroke.getPressed(SWT.DEL, 127, 0),
							getActionRegistry().getAction(
									ActionFactory.DELETE.getId()));

			sharedKeyHandler.put(KeyStroke.getPressed('+', SWT.KEYPAD_ADD, 0),
					getActionRegistry().getAction(GEFActionConstants.ZOOM_IN));

			sharedKeyHandler.put(
					KeyStroke.getPressed('-', SWT.KEYPAD_SUBTRACT, 0),
					getActionRegistry().getAction(GEFActionConstants.ZOOM_OUT));
			
		}

		return sharedKeyHandler;

	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public void setInitialized(boolean isInitialized) {
		this.isInitialized = isInitialized;
	}

	public Composite getParent() {
		return parent;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		String propertyName = evt.getPropertyName();
		
		if (propertyName.equals("name")) {
			String name = evt.getNewValue().toString();
			final AnimationSelector selector = injector
					.getInstance(AnimationSelector.class);
			String modelName = selector.getCurrentTrace().getModel()
					.getModelFile().getName();
			setPartName(name + " (" + modelName + ")");
		}

	}

	@Override
	public void traceChange(Trace history) {
		if (visualizationView != null)
			checkObserver(history);
	}

	public void checkObserver(Trace history) {
				
		if(history == null)
			return;
		
		// Proceed only if the state can be evaluated and the visualization
		// corresponds to the active animation
		if (!history.getStateSpace().canBeEvaluated(history.getCurrentState())
				|| !(history.getModel().getModelFile().getName()
						.equals(modelFile.getName())))
			return;

		// Collect all controls		
		Visualization visualization = visualizationView.getVisualization();
		List<BControl> allBControls = new ArrayList<BControl>();
		allBControls.add(visualization);
		collectAllBControls(allBControls, visualization);
		
		// Collect all evaluation elements from observer
		List<IEvalElement> l = new ArrayList<IEvalElement>();
		for (BControl c : allBControls) {
			Map<Observer, List<IEvalElement>> prepareObserver = c
					.prepareObserver(history);
			for (List<IEvalElement> ll : prepareObserver.values()) {
				for (IEvalElement f : ll) {
					l.add(f);
				}
			}
		}
		
		// Get all evaluation results at once
		Map<String, EvaluationResult> results = BMotionUtil
				.getEvaluationResults(history, l);

		// Check observer with evaluation results
		for (BControl c : allBControls)
			c.checkObserver(history, results);
		for (BControl c : allBControls)
			c.afterCheckObserver(history);

	}

	private void collectAllBControls(List<BControl> allBControls,
			BControl control) {

		if (control.getChildren().isEmpty())
			return;

		for (BControl bcontrol : control.getChildren()) {
			allBControls.add(bcontrol);
			collectAllBControls(allBControls, bcontrol);
		}

	}

	@Override
	public String getContributorId() {
		return getSite().getId();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		saveProperties();
		SaveAction saveAction = new SaveAction(visualizationView,
				visualizationFile);
		saveAction.run();
		getCommandStack().markSaveLocation();
		firePropertyChange(ISaveablePart.PROP_DIRTY);
	}

	@Override
	public void doSaveAs() {
		// Nothing to do here, this is never allowed
		throw new IllegalAccessError("No way to enter this method.");
	}

	public void setDirty(boolean dirty) {
		if (isDirty() != dirty) {
			this.dirty = dirty;
			firePropertyChange(ISaveablePart.PROP_DIRTY);
		}
	}
	
	@Override
	public boolean isDirty() {
		return this.dirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public boolean isSaveOnCloseNeeded() {
		return true;
	}

	@Override
	public int promptToSaveOnClose() {
		return BMotionUtil.openSaveDialog();
	}

}
