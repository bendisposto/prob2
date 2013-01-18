/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.thoughtworks.xstream.XStream;

import de.bmotionstudio.core.model.BMotionGuide;
import de.bmotionstudio.core.model.Simulation;
import de.bmotionstudio.core.model.VisualizationView;
import de.bmotionstudio.core.model.control.BConnection;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.control.Visualization;

/**
 * The activator class controls the plug-in life cycle
 */
public class BMotionEditorPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.bmotionstudio.core";

	public static final String FILEEXT_STUDIO = "bmso";

	public static List<ISimulationListener> openSimulationListeners = new ArrayList<ISimulationListener>();

	// The shared instance
	private static BMotionEditorPlugin plugin;

//	private static HashMap<String, IConfigurationElement> controlExtensions = new HashMap<String, IConfigurationElement>();

	private static HashMap<String, IConfigurationElement> observerExtensions = new HashMap<String, IConfigurationElement>();

	private static HashMap<String, IConfigurationElement> schedulerExtensions = new HashMap<String, IConfigurationElement>();

	private static HashMap<Class<?>, IBControlService> controlServices = new HashMap<Class<?>, IBControlService>();

	IExtensionRegistry registry = Platform.getExtensionRegistry();

	public static Map<String, Simulation> openSimulations = new HashMap<String, Simulation>();

	/**
	 * The constructor
	 */
	public BMotionEditorPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		initExtensionClasses();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}


	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static BMotionEditorPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Get the active workbench page.
	 * 
	 * @return current active workbench page
	 */
	public static IWorkbenchPage getActivePage() {
		return getDefault().internalGetActivePage();
	}

	/**
	 * Getting the current active page from the active workbench window.
	 * <p>
	 * 
	 * @return current active workbench page
	 */
	private IWorkbenchPage internalGetActivePage() {
		return getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}

	private void initBControlServices() {

		IExtensionPoint extensionPoint = registry
				.getExtensionPoint("de.bmotionstudio.core.control");
		for (IExtension extension : extensionPoint.getExtensions()) {

			for (IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {

				if ("control".equals(configurationElement.getName())) {

					try {
						IBControlService service = (IBControlService) configurationElement
								.createExecutableExtension("service");
						controlServices.put(service.getControlClass(), service);
					} catch (CoreException e) {
						e.printStackTrace();
					}

				}

			}

		}

	}
	
	private void initExtensionClass(String extensionPointID,
			ArrayList<String> elementIDs, String getAttribute,
			HashMap<String, IConfigurationElement> hashMap) {

		IExtensionPoint extensionPoint = registry
				.getExtensionPoint(extensionPointID);
		for (IExtension extension : extensionPoint.getExtensions()) {

			for (IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {

				if (elementIDs.contains(configurationElement.getName())) {

					String atr = configurationElement
							.getAttribute(getAttribute);

					hashMap.put(atr, configurationElement);

				}

			}

		}

	}

	private void initExtensionClasses() {

		ArrayList<String> elementIDs = new ArrayList<String>();
//		elementIDs.add("control");
//		initExtensionClass("de.bmotionstudio.core.control", elementIDs,
//				"id", controlExtensions);

//		elementIDs.clear();
//		elementIDs.add("control");
//		initExtensionClass("de.bmotionstudio.core.control", elementIDs,
//				"id", controlServices);

		elementIDs.clear();
		elementIDs.add("observer");
		initExtensionClass("de.bmotionstudio.core.observer", elementIDs,
				"class", observerExtensions);

		initBControlServices();
		
		// TODO: Reimplement me!!!
//		elementIDs.clear();
//		elementIDs.add("schedulerEvent");
//		initExtensionClass("de.bmotionstudio.gef.editor.schedulerEvent",
//				elementIDs, "class", schedulerExtensions);

	}

//	public static IConfigurationElement getControlExtension(String ident) {
//		return controlExtensions.get(ident);
//	}

	public static IConfigurationElement getObserverExtension(String ident) {
		return observerExtensions.get(ident);
	}

	public static IConfigurationElement getSchedulerExtension(String ident) {
		return schedulerExtensions.get(ident);
	}

	public static HashMap<String, IConfigurationElement> getSchedulerExtensions() {
		return schedulerExtensions;
	}

	public static HashMap<Class<?>, IBControlService> getControlServices() {
		return controlServices;
	}

	public static void setAliases(XStream xstream) {
		xstream.alias("simulation", Simulation.class);
		xstream.alias("view", VisualizationView.class);
		xstream.alias("control", BControl.class);
		xstream.alias("visualization", Visualization.class);
		xstream.alias("guide", BMotionGuide.class);
		xstream.alias("connection", BConnection.class);
	}

	// public static void openSimulation(Simulation simulation) {
	// openSimulations.put(simulation.getProjectFile().getName(), simulation);
	// for (ISimulationListener l : openSimulationListeners)
	// l.openSimulation(simulation);
	// }
	//
	// public static void closeSimulation(Simulation simulation) {
	// openSimulations.remove(simulation.getProjectFile().getName());
	// for (ISimulationListener l : openSimulationListeners)
	// l.closeSimulation(simulation);
	// }
	//
	// public static Map<String, Simulation> getOpenSimulations() {
	// return openSimulations;
	// }

}
