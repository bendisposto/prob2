/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.google.inject.Injector;
import com.thoughtworks.xstream.XStream;

import de.bmotionstudio.core.model.BMotionGuide;
import de.bmotionstudio.core.model.VisualizationView;
import de.bmotionstudio.core.model.control.BConnection;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.control.Visualization;
import de.bmotionstudio.core.util.BMotionUtil;
import de.bmotionstudio.core.util.PerspectiveUtil;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IModelChangedListener;
import de.prob.statespace.StateSpace;
import de.prob.webconsole.ServletContextListener;

/**
 * The activator class controls the plug-in life cycle
 */
public class BMotionEditorPlugin extends AbstractUIPlugin implements
		IModelChangedListener, IWorkbenchListener {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.bmotionstudio.core";

	public static final String FILEEXT_STUDIO = "bmso";

	// The shared instance
	private static BMotionEditorPlugin plugin;

//	private static HashMap<String, IConfigurationElement> controlExtensions = new HashMap<String, IConfigurationElement>();

	private static HashMap<String, IConfigurationElement> observerExtensions = new HashMap<String, IConfigurationElement>();

	private static HashMap<String, IConfigurationElement> schedulerExtensions = new HashMap<String, IConfigurationElement>();

	private static HashMap<Class<?>, IBControlService> controlServicesClass = new HashMap<Class<?>, IBControlService>();
	private static HashMap<String, IBControlService> controlServicesId = new HashMap<String, IBControlService>();

	IExtensionRegistry registry = Platform.getExtensionRegistry();

	Injector injector = ServletContextListener.INJECTOR;

	final AnimationSelector selector = injector
			.getInstance(AnimationSelector.class);
	
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
		selector.registerModelChangedListener(this);
		PlatformUI.getWorkbench().addWorkbenchListener(this);
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
		selector.unregisterModelChangedListener(this);
		PlatformUI.getWorkbench().removeWorkbenchListener(this);
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
						String id = configurationElement.getAttribute("id");
						controlServicesClass.put(service.getControlClass(), service);
						controlServicesId.put(id, service);
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
				"id", observerExtensions);

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

	public static HashMap<Class<?>, IBControlService> getControlServicesClass() {
		return controlServicesClass;
	}

	public static HashMap<String, IBControlService> getControlServicesId() {
		return controlServicesId;
	}
	
	public static void setAliases(XStream xstream) {
		xstream.alias("view", VisualizationView.class);
		xstream.alias("control", BControl.class);
		xstream.alias("visualization", Visualization.class);
		xstream.alias("guide", BMotionGuide.class);
		xstream.alias("connection", BConnection.class);
	}

	@Override
	public void modelChanged(StateSpace s) {

		File modelFile = s.getModel().getModelFile();

		// Save old and close old perspective (if exists)
		IPerspectiveDescriptor currentPerspective = BMotionStudio
				.getCurrentPerspective();
		File currentModelFile = BMotionStudio.getCurrentModelFile();

		// Close and save old perspective
		if (currentPerspective != null && currentModelFile != null) {
			// If yes ...
			// Export the current perspective
			File perspectiveFile = PerspectiveUtil
					.getPerspectiveFileFromModelFile(currentModelFile);
			PerspectiveUtil.exportPerspective(currentPerspective,
					perspectiveFile);
			// Close and delete current perspective, before opening the new one
			PerspectiveUtil.closePerspective(currentPerspective);
			PerspectiveUtil.deletePerspective(currentPerspective);
		}

		// Open new perspective
		IPerspectiveDescriptor perspective = PerspectiveUtil
				.openPerspective(modelFile);
		BMotionUtil.initVisualizationViews(modelFile);
		BMotionStudio.setCurrentModelFile(modelFile);
		BMotionStudio.setCurrentPerspective(perspective);

	}

	@Override
	public boolean preShutdown(IWorkbench workbench, boolean forced) {
		// TODO reimplement me!!!
//		IPerspectiveDescriptor currentPerspective = ProBConfiguration
//				.getCurrentPerspective();
//		IFile currentModelFile = ProBConfiguration.getCurrentModelFile();
//		// Close and save old perspective
//		if (currentPerspective != null && currentModelFile != null) {
//			// If yes ...
//			// Export the current perspective
//			IFile perspectiveFile = currentModelFile.getProject().getFile(
//					PerspectiveUtil.getPerspectiveFileName(currentModelFile));
//			PerspectiveUtil.exportPerspective(currentPerspective,
//					perspectiveFile);
//		}
		return true;
	}

	@Override
	public void postShutdown(IWorkbench workbench) {
	}

}
