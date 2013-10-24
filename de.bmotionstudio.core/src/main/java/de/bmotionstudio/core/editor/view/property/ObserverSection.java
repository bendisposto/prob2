package de.bmotionstudio.core.editor.view.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
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
import de.bmotionstudio.core.IBControlService;
import de.bmotionstudio.core.editor.VisualizationViewPart;
import de.bmotionstudio.core.editor.action.CopyObserverAction;
import de.bmotionstudio.core.editor.action.ObserverHelpAction;
import de.bmotionstudio.core.editor.action.PasteObserverAction;
import de.bmotionstudio.core.editor.action.RemoveObserverAction;
import de.bmotionstudio.core.editor.wizard.observer.ObserverWizard;
import de.bmotionstudio.core.model.VisualizationView;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.control.BControlPropertyConstants;
import de.bmotionstudio.core.model.observer.Observer;

public class ObserverSection extends AbstractPropertySection implements
		PropertyChangeListener, ITabSelectionListener {

	private Composite container;

	private BControl selectedControl;

	private ListViewer listViewer;

	private Composite rightContainer;
	
	private ObserverHelpAction helpAction;
	private Action observerAction;
	
	private BMotionPropertySheetPage propertySheetPage;
	
	private Observer selectedObserver;
	
	private MenuManager observerMenuManager, observerSubMenuManager;

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
						List<Observer> values = selectedControl.getObservers();
						listViewer.setInput(values);
						if (values.size() > 0) {
							Observer firstObserver = values.iterator().next();
							listViewer.setSelection(new StructuredSelection(
									firstObserver));
						} else {
							helpAction.setEnabled(false);
							if (rightContainer != null)
								rightContainer.dispose();
						}
					}
				}
			}
		}

	}

	public void createActions() {
		
		helpAction = new ObserverHelpAction();
			
		observerAction = new Action("New Observer", SWT.DROP_DOWN) {

			@Override
			public ImageDescriptor getImageDescriptor() {
				return BMotionImage.getImageDescriptor(
						BMotionEditorPlugin.PLUGIN_ID,
						"icons/icon_observer.gif");
			}

		};
		observerAction.setMenuCreator(new IMenuCreator() {

			@Override
			public Menu getMenu(Menu parent) {
				return null;
			}

			@Override
			public Menu getMenu(Control parent) {
				if (observerMenuManager == null) {
					observerMenuManager = new MenuManager();
					observerMenuManager.createContextMenu(parent);
					observerMenuManager.setRemoveAllWhenShown(true);
					observerMenuManager.addMenuListener(new IMenuListener() {
						@Override
						public void menuAboutToShow(IMenuManager manager) {
							updateMenuManager((MenuManager) manager,
									selectedControl, false);
						}
					});
				}
				return observerMenuManager.getMenu();
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
				List<?> observerList = (List<?>) inputElement;
				return observerList.toArray();
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
				Observer o = (Observer) element;
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
				helpAction.setEnabled(false);
				if (rightContainer != null)
					rightContainer.dispose();
				if (event.getSelection() != null
						&& event.getSelection() instanceof StructuredSelection) {
					Object firstElement = ((StructuredSelection) event
							.getSelection()).getFirstElement();
					if (firstElement instanceof Observer) {
						if (selectedObserver != null)
							selectedObserver
									.removePropertyChangeListener(ObserverSection.this);
						selectedObserver = (Observer) firstElement;
						selectedObserver
								.addPropertyChangeListener(ObserverSection.this);
						ObserverWizard wizard = selectedObserver.getWizard(
								Display.getDefault().getActiveShell(),
								selectedControl);
						// IWizardPage page = wizard.getPages()[0];
						rightContainer = new Composite(container, SWT.NONE);
						rightContainer.setLayoutData(new GridData(
								GridData.FILL_BOTH));
						rightContainer.setLayout(new FillLayout());
						wizard.createDialogArea(rightContainer);
						helpAction.setEnabled(true);
						helpAction.setObserverID(selectedObserver.getClass()
								.getName());
					}
				}
				container.layout();
			}
		});
		
		createContextMenu(listViewer);

	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {

		if (event.getPropertyName().equals(
				BControlPropertyConstants.PROPERTY_ADD_OBSERVER)
				|| event.getPropertyName().equals(
						BControlPropertyConstants.PROPERTY_REMOVE_OBSERVER)
				|| event.getPropertyName().equals("name"))
			listViewer.refresh();

		if (event.getSource() instanceof Observer) {
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

		if (tabDescriptor.getLabel().equals("Observer")) {

			// Add help action
			this.propertySheetPage.getSite().getActionBars()
					.getToolBarManager().add(helpAction);
			
			// Add observer action
			this.propertySheetPage.getSite().getActionBars()
					.getToolBarManager().add(observerAction);
					
		}

		this.propertySheetPage.getSite().getActionBars().getToolBarManager()
				.update(true);
		this.propertySheetPage.getSite().getActionBars().getMenuManager()
				.update(true);

	}
	
	private void createContextMenu(ListViewer viewer) {

		MenuManager contextMenuManager = new MenuManager();
		contextMenuManager.createContextMenu(listViewer.getControl());
		contextMenuManager.setRemoveAllWhenShown(true);
		contextMenuManager.addMenuListener(new IMenuListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void menuAboutToShow(IMenuManager manager) {

				updateMenuManager((MenuManager) manager, selectedControl, true);

				ISelection selection = listViewer.getSelection();
				ActionRegistry actionRegistry = (ActionRegistry) getPart()
						.getAdapter(ActionRegistry.class);

				if (!selection.isEmpty()) {

					RemoveObserverAction removeObserverAction = (RemoveObserverAction) actionRegistry
							.getAction(ActionConstants.ACTION_REMOVE_OBSERVER);
					removeObserverAction.setText("Remove Observer");
					removeObserverAction.setControl(selectedControl);
					removeObserverAction.setObserver(selectedObserver);
					manager.add(removeObserverAction);

					CopyObserverAction copyObserverAction = (CopyObserverAction) actionRegistry
							.getAction(ActionConstants.ACTION_COPY_OBSERVER);
					copyObserverAction.setText("Copy Observer");

					IStructuredSelection ss = (IStructuredSelection) selection;
					List<?> list = ss.toList();
					copyObserverAction.setList((List<Observer>) list);
					manager.add(copyObserverAction);

				}

				PasteObserverAction pasteObserverAction = (PasteObserverAction) actionRegistry
						.getAction(ActionConstants.ACTION_PASTE_OBSERVER);
				pasteObserverAction.setText("Paste Observer");
				pasteObserverAction.setControl(selectedControl);
				pasteObserverAction.update();
				manager.add(pasteObserverAction);

			}
		});
		viewer.getControl().setMenu(contextMenuManager.getMenu());

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
				if (observerSubMenuManager == null)
					observerSubMenuManager = new MenuManager("New Observer",
							BMotionImage.getImageDescriptor(
									BMotionEditorPlugin.PLUGIN_ID,
									"icons/icon_observer.gif"), "observerMenu");
				observerSubMenuManager.removeAll();
				fmanager = observerSubMenuManager;
				manager.add(fmanager);
			}

			IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
					.getExtensionPoint("de.bmotionstudio.core.includeObserver");

			for (IExtension extension : extensionPoint.getExtensions()) {
				for (IConfigurationElement configurationElement : extension
						.getConfigurationElements()) {

					if ("include".equals(configurationElement.getName())) {

						String langID = configurationElement
								.getAttribute("language");

						if (langID != null && langID.equals(language)) {

							for (IConfigurationElement configC : configurationElement
									.getChildren("control")) {

								String cID = configC.getAttribute("id");

								IBControlService controlService = BMotionEditorPlugin
										.getControlServicesId().get(cID);

								if (controlService != null
										&& selectedControl.getClass().equals(
												controlService
														.getControlClass())) {

									for (IConfigurationElement configO : configC
											.getChildren("observer")) {

										ActionRegistry actionRegistry = (ActionRegistry) getPart()
												.getAdapter(
														ActionRegistry.class);

										if (actionRegistry != null) {

											String oID = configO
													.getAttribute("id");
											IAction action = actionRegistry
													.getAction("de.bmotionstudio.core.observerAction."
															+ oID);

											IConfigurationElement observerExtension = BMotionEditorPlugin
													.getObserverExtension(oID);
											String oName = observerExtension
													.getAttribute("name");
											action.setText(oName);

											if (fmanager.find(action.getId()) == null)
												fmanager.add(action);

										}

									}

								}

							}

						}

					}

				}
			}

		}

	}
	
}
