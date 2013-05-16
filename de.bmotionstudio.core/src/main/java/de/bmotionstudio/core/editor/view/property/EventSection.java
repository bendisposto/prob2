package de.bmotionstudio.core.editor.view.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptor;
import org.eclipse.ui.views.properties.tabbed.ITabSelectionListener;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import de.bmotionstudio.core.ActionConstants;
import de.bmotionstudio.core.BMotionEditorPlugin;
import de.bmotionstudio.core.BMotionImage;
import de.bmotionstudio.core.editor.VisualizationViewPart;
import de.bmotionstudio.core.editor.action.CopyEventAction;
import de.bmotionstudio.core.editor.action.PasteEventAction;
import de.bmotionstudio.core.editor.action.RemoveEventAction;
import de.bmotionstudio.core.editor.wizard.event.EventWizard;
import de.bmotionstudio.core.model.VisualizationView;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.control.BControlPropertyConstants;
import de.bmotionstudio.core.model.event.Event;

public class EventSection extends AbstractPropertySection implements
		PropertyChangeListener, ITabSelectionListener {

	private Composite container;

	private BControl selectedControl;

	private ListViewer listViewer;

	private Composite rightContainer;
	
//	private ObserverHelpAction helpAction;
	private Action action;
	
	private BMotionPropertySheetPage propertySheetPage;
	
	private Event selectedEvent;
	
	private MenuManager contextMenuManager, menuManager,
			subMenuManager;

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {

		super.setInput(part, selection);

		if (selection != null && selection instanceof StructuredSelection) {
			Object firstElement = ((StructuredSelection) selection)
					.getFirstElement();
			if (firstElement instanceof AbstractEditPart) {
				Object model = ((AbstractEditPart) firstElement).getModel();
				if (model instanceof BControl) {
					if (selectedControl != null)
						selectedControl.removePropertyChangeListener(this);
					selectedControl = (BControl) model;
					selectedControl.addPropertyChangeListener(this);
					if (!listViewer.getControl().isDisposed()) {
						List<Event> values = selectedControl.getEvents();
						listViewer.setInput(values);
						if (values.size() > 0) {
							Event firstEvent = values.iterator().next();
							listViewer.setSelection(new StructuredSelection(
									firstEvent));
						} else {
//							helpAction.setEnabled(false);
							if (rightContainer != null)
								rightContainer.dispose();
						}
					}
				}
			}
		}

	}

	public void createActions() {
		
//		helpAction = new ObserverHelpAction();
			
		action = new Action("New Event", SWT.DROP_DOWN) {

			@Override
			public ImageDescriptor getImageDescriptor() {
				return BMotionImage.getImageDescriptor(
						BMotionEditorPlugin.PLUGIN_ID,
						"icons/icon_event.png");
			}

		};
		action.setMenuCreator(new IMenuCreator() {

			@Override
			public Menu getMenu(Menu parent) {
				return null;
			}

			@Override
			public Menu getMenu(Control parent) {
				if (menuManager == null) {
					menuManager = new MenuManager();
					menuManager.createContextMenu(parent);
					menuManager.setRemoveAllWhenShown(true);
					menuManager.addMenuListener(new IMenuListener() {
						@Override
						public void menuAboutToShow(IMenuManager manager) {
							updateMenuManager((MenuManager) manager,
									selectedControl, false);
						}
					});
				}
				return menuManager.getMenu();
			}

			@Override
			public void dispose() {
			}

		});
		
	}
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage propertySheetPage) {

		super.createControls(parent, propertySheetPage);
		
		this.propertySheetPage = (BMotionPropertySheetPage) propertySheetPage;
		this.propertySheetPage.addTabSelectionListener(this);
				
		createActions();
		
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);
		listViewer = new ListViewer(container);
		listViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

			@Override
			public Object[] getElements(Object inputElement) {
				List<?> list = (List<?>) inputElement;
				return list.toArray();
			}

		});
		listViewer.setLabelProvider(new ILabelProvider() {

			@Override
			public void removeListener(ILabelProviderListener listener) {
			}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			@Override
			public void dispose() {
			}

			@Override
			public void addListener(ILabelProviderListener listener) {
			}

			@Override
			public String getText(Object element) {
				Event o = (Event) element;
				return o.getName();
			}

			@Override
			public Image getImage(Object element) {
				return null;
			}

		});

		GridData layoutData = new GridData(GridData.FILL_VERTICAL);
		layoutData.widthHint = 120;
		listViewer.getControl().setLayoutData(layoutData);
		listViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
//				helpAction.setEnabled(false);
				if (rightContainer != null)
					rightContainer.dispose();
				if (event.getSelection() != null
						&& event.getSelection() instanceof StructuredSelection) {
					Object firstElement = ((StructuredSelection) event
							.getSelection()).getFirstElement();
					if (firstElement instanceof Event) {
						if (selectedEvent != null)
							selectedEvent
									.removePropertyChangeListener(EventSection.this);
						selectedEvent = (Event) firstElement;
						selectedEvent
								.addPropertyChangeListener(EventSection.this);
						EventWizard wizard = selectedEvent.getWizard(
								Display.getDefault().getActiveShell(),
								selectedControl);
						// IWizardPage page = wizard.getPages()[0];
						rightContainer = new Composite(container, SWT.NONE);
						rightContainer.setLayoutData(new GridData(
								GridData.FILL_BOTH));
						rightContainer.setLayout(new FillLayout());
						wizard.createDialogArea(rightContainer);
//						helpAction.setEnabled(true);
//						helpAction.setObserverID(selectedEvent.getClass()
//								.getName());
					}
				}
				container.layout();
			}
		});

	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {

		if (event.getPropertyName().equals(
				BControlPropertyConstants.PROPERTY_ADD_EVENT)
				|| event.getPropertyName().equals(
						BControlPropertyConstants.PROPERTY_REMOVE_EVENT)
				|| event.getPropertyName().equals("name"))
			listViewer.refresh();

		if (event.getSource() instanceof Event) {
			IWorkbenchPart part = getPart();
			if (part instanceof VisualizationViewPart) {
				VisualizationViewPart viewPart = (VisualizationViewPart) part;
				viewPart.setDirty(true);
			}
		}

	}
	
	@Override
	public void tabSelected(ITabDescriptor tabDescriptor) {
		
		this.propertySheetPage.getSite().getActionBars().getToolBarManager()
				.removeAll();
		this.propertySheetPage.getSite().getActionBars().getMenuManager()
				.removeAll();

		if (tabDescriptor.getLabel().equals("Events")) {

			// Add help action
//			this.propertySheetPage.getSite().getActionBars()
//					.getToolBarManager().add(helpAction);
			
			// Add observer action
			this.propertySheetPage.getSite().getActionBars()
					.getToolBarManager().add(action);
			
			// Create context menu
			if (contextMenuManager == null) {
				contextMenuManager = new MenuManager();
				contextMenuManager.createContextMenu(listViewer.getControl());
				contextMenuManager.setRemoveAllWhenShown(true);
				contextMenuManager.addMenuListener(new IMenuListener() {
					@SuppressWarnings("unchecked")
					@Override
					public void menuAboutToShow(IMenuManager manager) {
						
						updateMenuManager((MenuManager) manager,
								selectedControl, true);

						ISelection selection = listViewer.getSelection();
						ActionRegistry actionRegistry = (ActionRegistry) getPart()
								.getAdapter(ActionRegistry.class);
						
						if (!selection.isEmpty()) {

							RemoveEventAction removeAction = (RemoveEventAction) actionRegistry
									.getAction(ActionConstants.ACTION_REMOVE_EVENT);
							removeAction.setText("Remove Event");
							removeAction.setControl(selectedControl);
							removeAction.setEvent(selectedEvent);
							manager.add(removeAction);

							CopyEventAction copyAction = (CopyEventAction) actionRegistry
									.getAction(ActionConstants.ACTION_COPY_EVENT);
							copyAction.setText("Copy Event");
							
							IStructuredSelection ss = (IStructuredSelection) selection;
							List<?> list = ss.toList();
							copyAction.setList((List<Event>) list);
							manager.add(copyAction);
							
						}
						
						PasteEventAction pasteEventAction = (PasteEventAction) actionRegistry
								.getAction(ActionConstants.ACTION_PASTE_EVENT);
						pasteEventAction.setText("Paste Event");
						pasteEventAction.setControl(selectedControl);
						pasteEventAction.update();
						manager.add(pasteEventAction);

					}
				});
				listViewer.getControl().setMenu(contextMenuManager.getMenu());
			}
					
		}

		this.propertySheetPage.getSite().getActionBars().getToolBarManager()
				.update(true);
		this.propertySheetPage.getSite().getActionBars().getMenuManager()
				.update(true);

	}
		
	private void updateMenuManager(MenuManager manager,
			BControl selectedControl, boolean asSubmenu) {

		if (manager == null || selectedControl == null)
			return;

		IWorkbenchPart part = getPart();
		if (part instanceof VisualizationViewPart) {

			VisualizationViewPart visualizationViewPart = (VisualizationViewPart) part;
			VisualizationView visualizationView = visualizationViewPart
					.getVisualizationView();
			String language = visualizationView.getLanguage();

			MenuManager fmanager = manager;

			if (asSubmenu) {
				if (subMenuManager == null)
					subMenuManager = new MenuManager("New Event",
							BMotionImage.getImageDescriptor(
									BMotionEditorPlugin.PLUGIN_ID,
									"icons/icon_event.png"), "eventMenu");
				subMenuManager.removeAll();
				fmanager = subMenuManager;
				manager.add(fmanager);

				ActionRegistry actionRegistry = (ActionRegistry) getPart()
						.getAdapter(ActionRegistry.class);

				HashMap<String, IConfigurationElement> eventExtensions = BMotionEditorPlugin
						.getEventExtensions();
				for (IConfigurationElement config : eventExtensions.values()) {

					String langID = config.getAttribute("language");
					String[] split = langID.split(",");
					if (Arrays.asList(split).contains(language)) {
						String id = config.getAttribute("id");
						String name = config.getAttribute("name");
						IAction action = actionRegistry
								.getAction("de.bmotionstudio.core.eventAction."
										+ id);
						action.setText(name);
						if (fmanager.find(action.getId()) == null)
							fmanager.add(action);
					}

				}

			}

		}

	}
	
}
