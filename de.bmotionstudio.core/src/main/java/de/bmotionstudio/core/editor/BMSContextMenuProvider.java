/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor;

import java.util.Arrays;
import java.util.HashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.ActionFactory;

import de.bmotionstudio.core.BMotionEditorPlugin;
import de.bmotionstudio.core.BMotionImage;
import de.bmotionstudio.core.IBControlService;
import de.bmotionstudio.core.model.control.BControl;

public class BMSContextMenuProvider extends ContextMenuProvider {

	private ActionRegistry actionRegistry;

	// private String[] eventIDs = { AttributeConstants.EVENT_MOUSECLICK };

	private String language;
	
	public BMSContextMenuProvider(EditPartViewer viewer,
			ActionRegistry registry, String language) {
		super(viewer);
		setActionRegistry(registry);
		this.language = language;
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {

		IAction action;

		GEFActionConstants.addStandardActionGroups(menu);

		action = getActionRegistry().getAction(ActionFactory.UNDO.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);

		action = getActionRegistry().getAction(ActionFactory.REDO.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);

		action = getActionRegistry().getAction(ActionFactory.COPY.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_COPY, action);

		action = getActionRegistry().getAction(ActionFactory.PASTE.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_COPY, action);

		action = getActionRegistry().getAction(ActionFactory.DELETE.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

		Object sel = ((IStructuredSelection) getViewer().getSelection())
				.getFirstElement();

		if (sel instanceof AbstractEditPart) {
			AbstractEditPart editPart = (AbstractEditPart) sel;
			// buildCustomMenu(menu, editPart);
			buildObserverMenu(menu, editPart);
			buildEventMenu(menu, editPart);
		}

	}

	// private void buildCustomMenu(IMenuManager menu, AbstractEditPart
	// editPart) {
	//
	// Object model = editPart.getModel();
	//
	// if (model instanceof BControl) {
	//
	// IExtensionPoint extensionPoint = registry
	// .getExtensionPoint("de.bmotionstudio.core.installMenu");
	// for (IExtension extension : extensionPoint.getExtensions()) {
	// for (IConfigurationElement configurationElement : extension
	// .getConfigurationElements()) {
	//
	// if ("menu".equals(configurationElement.getName())) {
	//
	// try {
	//
	// IInstallMenu installMenuClass = (IInstallMenu) configurationElement
	// .createExecutableExtension("class");
	//
	// installMenuClass.installMenu(menu,
	// getActionRegistry());
	//
	// } catch (final CoreException e) {
	// e.printStackTrace();
	// }
	//
	// }
	//
	// }
	//
	// }
	//
	// }
	//
	// }

	private void buildObserverMenu(IMenuManager menu, AbstractEditPart editPart) {

		Object model = editPart.getModel();

		BControl control = null;

		if (model instanceof BControl)
			control = (BControl) model;
		else
			return;

		final MenuManager handleObserverMenu = new MenuManager("New Observer",
				BMotionImage.getImageDescriptor(BMotionEditorPlugin.PLUGIN_ID,
						"icons/icon_observer.gif"), "observerMenu");
		menu.appendToGroup(GEFActionConstants.GROUP_ADD, handleObserverMenu);

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
									&& control.getClass().equals(
											controlService.getControlClass())) {

								for (IConfigurationElement configO : configC
										.getChildren("observer")) {

									String oID = configO.getAttribute("id");
									IAction action = getActionRegistry()
											.getAction(
													"de.bmotionstudio.core.observerAction."
															+ oID);

									IConfigurationElement observerExtension = BMotionEditorPlugin
											.getObserverExtension(oID);
									String oName = observerExtension
											.getAttribute("name");

									action.setText(oName);

									if (handleObserverMenu.find(action.getId()) == null)
										handleObserverMenu.add(action);

								}

							}

						}

					}

				}

			}
		}

	}

	private void buildEventMenu(IMenuManager menu, AbstractEditPart editPart) {

		final MenuManager handleMenu = new MenuManager("New Event",
				BMotionImage.getImageDescriptor(BMotionEditorPlugin.PLUGIN_ID,
						"icons/icon_event.png"), "eventMenu");
		menu.appendToGroup(GEFActionConstants.GROUP_ADD, handleMenu);

		HashMap<String, IConfigurationElement> eventExtensions = BMotionEditorPlugin
				.getEventExtensions();
		for (IConfigurationElement config : eventExtensions.values()) {

			String langID = config.getAttribute("language");
			String[] split = langID.split(",");
			if (Arrays.asList(split).contains(language)) {
				String id = config.getAttribute("id");
				String name = config.getAttribute("name");
				IAction action = getActionRegistry().getAction(
						"de.bmotionstudio.core.eventAction." + id);
				action.setText(name);
				if (handleMenu.find(action.getId()) == null)
					handleMenu.add(action);
			}

		}

	}

	private ActionRegistry getActionRegistry() {
		return actionRegistry;
	}

	private void setActionRegistry(ActionRegistry registry) {
		actionRegistry = registry;
	}

}