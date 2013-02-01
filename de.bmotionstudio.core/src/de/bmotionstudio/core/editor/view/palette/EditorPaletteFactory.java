/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.view.palette;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteToolbar;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;

import de.bmotionstudio.core.BMotionImage;
import de.bmotionstudio.core.IBControlService;
import de.bmotionstudio.core.IInstallPaletteEntry;
import de.bmotionstudio.core.editor.BControlCreationFactory;
import de.bmotionstudio.core.service.control.BConnectionService;

public class EditorPaletteFactory {

	private HashMap<String, PaletteDrawer> groupMap = new HashMap<String, PaletteDrawer>();

	/**
	 * Creates the PaletteRoot and adds all palette elements. Use this factory
	 * method to create a new palette for your graphical editor.
	 * 
	 * @param visualization
	 * 
	 * @param editor
	 * 
	 * @return a new PaletteRoot
	 */
	public PaletteRoot createPalette() {
		PaletteRoot palette = new PaletteRoot();
		palette.add(createToolsGroup(palette));
		createControls(palette);
		createFromExtension(palette);
		return palette;
	}

	private void createFromExtension(PaletteRoot palette) {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry
				.getExtensionPoint("de.bmotionstudio.core.paletteEntry");
		// Iterate over controls
		for (IExtension extension : extensionPoint.getExtensions()) {
			for (IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {
				if ("entry".equals(configurationElement.getName())) {
					try {
						IInstallPaletteEntry entry = (IInstallPaletteEntry) configurationElement
								.createExecutableExtension("class");
						entry.installPaletteEntry(palette, groupMap);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void createControls(PaletteRoot palette) {

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry
				.getExtensionPoint("de.bmotionstudio.core.control");

		// Iterate over groups
		for (IExtension extension : extensionPoint.getExtensions()) {

			for (IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {

				if ("group".equals(configurationElement.getName())) {

					String groupID = configurationElement.getAttribute("id");
					String groupName = configurationElement
							.getAttribute("name");

					PaletteDrawer componentsDrawer = new PaletteDrawer(
							groupName);
					if (!groupMap.containsKey(groupID))
						groupMap.put(groupID, componentsDrawer);

				}

			}

		}

		// Iterate over controls
		for (IExtension extension : extensionPoint.getExtensions()) {

			for (IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {

				if ("control".equals(configurationElement.getName())) {

					String groupID = configurationElement
							.getAttribute("groupid");
					PaletteDrawer groupDrawer = groupMap.get(groupID);

					if (groupDrawer != null) {

						// boolean createDefaultToolEntry = true;

						try {
							IBControlService service = (IBControlService) configurationElement
									.createExecutableExtension("service");
							String name = configurationElement
									.getAttribute("name");
							String icon = configurationElement
									.getAttribute("icon");
							String sourcePluginID = configurationElement
									.getContributor().getName();
							if (service.showInPalette()) {
								ToolEntry toolEntry = service.createToolEntry(
										name, icon, sourcePluginID, service);
								if (toolEntry != null) {
									groupDrawer.add(toolEntry);
								}
							}
						} catch (CoreException e) {
							// I think we can ignore the exception since
							// we create a default tool entry which is
							// independent from the configuration
							// element
						}

						// if (createDefaultToolEntry)
						// groupDrawer.add(createDefaultToolEntry(type,
						// visualization, configurationElement));

					}

				}

			}

		}

		for (Map.Entry<String, PaletteDrawer> entry : groupMap.entrySet()) {
			if (entry.getValue().getChildren().size() > 0)
				palette.add(entry.getValue());
		}

	}

	/**
	 * Create the "Tools" group.
	 * 
	 * @param visualization
	 */
	private PaletteContainer createToolsGroup(PaletteRoot palette) {
		PaletteToolbar toolbar = new PaletteToolbar("Tools");
		// Add a selection tool to the group
		ToolEntry tool = new PanningSelectionToolEntry();
		toolbar.add(tool);
		palette.setDefaultEntry(tool);
		// Add a marquee tool to the group
		toolbar.add(new MarqueeToolEntry());
		// Add connector tool to the group
		toolbar.add(new ConnectionCreationToolEntry("Connection",
				"Universal Connector", new BControlCreationFactory(
						new BConnectionService()), BMotionImage
						.getImageDescriptor("icons/icon_connection16.gif"),
				BMotionImage.getImageDescriptor("icons/icon_connection24.gif")));
		return toolbar;
	}

}
