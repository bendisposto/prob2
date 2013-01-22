package de.bmotionstudio.core.editor.view.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptor;
import org.eclipse.ui.views.properties.tabbed.ITabSelectionListener;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import de.bmotionstudio.core.BMotionEditorPlugin;
import de.bmotionstudio.core.BMotionImage;
import de.bmotionstudio.core.BMotionStudio;
import de.bmotionstudio.core.IBControlService;
import de.bmotionstudio.core.editor.VisualizationViewPart;
import de.bmotionstudio.core.editor.action.ObserverHelpAction;
import de.bmotionstudio.core.editor.part.BMSAbstractEditPart;
import de.bmotionstudio.core.editor.wizard.observer.ObserverWizard;
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
	
	private BMotionPropertySheetPage propertySheetPage;
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		
		super.setInput(part, selection);
		
		if (selection != null && selection instanceof StructuredSelection) {
			Object firstElement = ((StructuredSelection) selection)
					.getFirstElement();
			if (firstElement instanceof BMSAbstractEditPart) {
				if(selectedControl != null)
					selectedControl.removePropertyChangeListener(this);
				selectedControl = (BControl) ((BMSAbstractEditPart) firstElement)
						.getModel();
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
	
	public void createActions() {
		helpAction = new ObserverHelpAction();
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
		// layout.horizontalSpacing = 0;
		// layout.verticalSpacing = 0;
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
						Observer o = (Observer) firstElement;
						ObserverWizard wizard = o
								.getWizard(Display.getDefault()
										.getActiveShell(), selectedControl);
						// IWizardPage page = wizard.getPages()[0];
						rightContainer = new Composite(container, SWT.NONE);
						rightContainer.setLayoutData(new GridData(
								GridData.FILL_BOTH));
						rightContainer.setLayout(new FillLayout());
						wizard.createDialogArea(rightContainer);
						helpAction.setEnabled(true);
						helpAction.setObserverID(o.getClass().getName());
					}
				}
				container.layout();
			}
		});
		
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		
		if (event.getPropertyName().equals(
				BControlPropertyConstants.PROPERTY_ADD_OBSERVER)
				|| event.getPropertyName().equals(
						BControlPropertyConstants.PROPERTY_REMOVE_OBSERVER))
			listViewer.refresh();

	}
	
	@Override
	public void tabSelected(ITabDescriptor tabDescriptor) {
		this.propertySheetPage.getSite().getActionBars().getToolBarManager().removeAll();
		this.propertySheetPage.getSite().getActionBars().getMenuManager().removeAll();
		if(tabDescriptor.getLabel().equals("Observer")) {
			this.propertySheetPage.getSite().getActionBars().getToolBarManager().add(helpAction);
		}
		this.propertySheetPage.getSite().getActionBars().getToolBarManager().update(true);
		buildObserverMenu(this.propertySheetPage.getSite().getActionBars().getMenuManager(),selectedControl);
	}
	
	private void buildObserverMenu(IMenuManager menu, BControl control) {

		final MenuManager handleObserverMenu = new MenuManager("New Observer",
				BMotionImage.getImageDescriptor(BMotionEditorPlugin.PLUGIN_ID,
						"icons/icon_observer.gif"), "observerMenu");
		menu.add(handleObserverMenu);

		IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
				.getExtensionPoint(
						"de.bmotionstudio.core.includeObserver");

		for (IExtension extension : extensionPoint.getExtensions()) {
			for (IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {

				if ("include".equals(configurationElement.getName())) {

					String langID = configurationElement
							.getAttribute("language");

					if (langID != null
							&& langID.equals(BMotionStudio
									.getCurrentSimulation().getLanguage())) {
					
						for (IConfigurationElement configC : configurationElement
								.getChildren("control")) {

							String cID = configC.getAttribute("id");
							
							IBControlService controlService = BMotionEditorPlugin
									.getControlServicesId().get(cID);
			
							if (controlService != null
									&& control.getClass().equals(
											controlService.getControlClass())) {
								
								for (IConfigurationElement configO : configC
										.getChildren("observer")) {

									VisualizationViewPart viewPart = (VisualizationViewPart) getPart();

									String oID = configO.getAttribute("id");
									IAction action = viewPart
											.getActionRegistry().getAction(
													"de.bmotionstudio.core.observerAction."
															+ oID);

									// TODO: Get correct name of observer
									String name = oID;
									
									action.setText(name);

									handleObserverMenu.add(action);

								}

							}

						}

					}

				}

			}
		}

	}

}
