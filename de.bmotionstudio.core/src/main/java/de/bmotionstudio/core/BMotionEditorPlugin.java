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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.google.inject.Injector;

import de.bmotionstudio.core.editor.VisualizationViewPart;
import de.bmotionstudio.core.util.BMotionUtil;
import de.bmotionstudio.core.util.PerspectiveUtil;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.Trace;
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

	private static HashMap<String, IConfigurationElement> observerExtensions = new HashMap<String, IConfigurationElement>();
	private static HashMap<String, IConfigurationElement> eventExtensions = new HashMap<String, IConfigurationElement>();
	private static HashMap<Class<?>, IBControlService> controlServicesClass = new HashMap<Class<?>, IBControlService>();
	private static HashMap<String, IBControlService> controlServicesId = new HashMap<String, IBControlService>();

	private StateSpace currentStateSpace = null;
	private Trace currentHistory = null;

	private IExtensionRegistry registry = Platform.getExtensionRegistry();

	private Injector injector = ServletContextListener.INJECTOR;

	private final AnimationSelector selector = injector
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
	 * 
	 * @return current active workbench page or null if not exists
	 */
	private IWorkbenchPage internalGetActivePage() {
		IWorkbenchWindow activeWorkbenchWindow = getWorkbench()
				.getActiveWorkbenchWindow();
		if (activeWorkbenchWindow != null)
			return activeWorkbenchWindow.getActivePage();
		return null;
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

		elementIDs.clear();
		elementIDs.add("observer");
		initExtensionClass("de.bmotionstudio.core.observer", elementIDs, "id",
				observerExtensions);

		initBControlServices();

		elementIDs.clear();
		elementIDs.add("event");
		initExtensionClass("de.bmotionstudio.core.event", elementIDs, "id",
				eventExtensions);

	}

	public static IConfigurationElement getObserverExtension(String ident) {
		return observerExtensions.get(ident);
	}

	public static IConfigurationElement getEventExtension(String ident) {
		return eventExtensions.get(ident);
	}

	public static HashMap<String, IConfigurationElement> getEventExtensions() {
		return eventExtensions;
	}

	public static HashMap<Class<?>, IBControlService> getControlServicesClass() {
		return controlServicesClass;
	}

	public static HashMap<String, IBControlService> getControlServicesId() {
		return controlServicesId;
	}
	
	@Override
	public void modelChanged(final StateSpace s) {

		// If a model change was triggered, try to switch the perspective
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {

				File newModelFile = s.getModel().getModelFile();

				if (currentStateSpace == null
						|| (currentStateSpace != null && !(newModelFile
								.getPath().equals(currentStateSpace.getModel()
								.getModelFile().getPath())))) {

					String newLanguage = BMotionUtil.getLanguageFromModel(s
							.getModel());
					IPerspectiveDescriptor currentPerspective = BMotionStudio
							.getCurrentPerspective();

					// Save old and close old perspective (if exists)
					if (currentPerspective != null && currentStateSpace != null) {

						File currentModelFile = currentStateSpace.getModel()
								.getModelFile();

						String currentLanguage = BMotionUtil
								.getLanguageFromModel(currentStateSpace
										.getModel());
						// If yes ...
						// Export the current perspective
						File perspectiveFile = PerspectiveUtil
								.getPerspectiveFileFromModelFile(currentModelFile);
						PerspectiveUtil.exportPerspective(currentPerspective,
								perspectiveFile);
						// Close and delete current perspective, before opening
						// the
						// new one
						PerspectiveUtil.closePerspective(currentPerspective);
						// Check if dirty view parts exist, if yes prompt the
						// user
						// for saving the dirty visualization parts
						VisualizationViewPart[] visualizationViewParts = BMotionUtil
								.getVisualizationViewParts(currentModelFile,
										currentLanguage);
						for (VisualizationViewPart visPart : visualizationViewParts) {
							if (visPart.isDirty()) {
								if (currentHistory != null)
									selector.changeCurrentAnimation(currentHistory);
								return;
							}
						}
						PerspectiveUtil.deletePerspective(currentPerspective);

					}

					// Switch to new perspective
					IPerspectiveDescriptor perspective = PerspectiveUtil
							.openPerspective(newModelFile);
					BMotionUtil.initVisualizationViews(newModelFile,
							newLanguage);
					BMotionStudio.setCurrentPerspective(perspective);

				}

				currentStateSpace = s;
				currentHistory = selector.getCurrentTrace();

			}

		});

	}

	@Override
	public boolean preShutdown(IWorkbench workbench, boolean forced) {
		// If the current perspective is a ProB perspective, export before
		// closing eclipse
		IPerspectiveDescriptor currentPerspective = workbench
				.getActiveWorkbenchWindow().getActivePage().getPerspective();
		File currentModelFile = null;
		if (currentStateSpace != null)
			currentModelFile = currentStateSpace.getModel().getModelFile();
		IPerspectiveDescriptor probPerspective = BMotionStudio
				.getCurrentPerspective();

		if (currentModelFile == null || probPerspective == null)
			return true;

		if (currentPerspective.getLabel().startsWith("ProB_")) {
			PerspectiveUtil.exportPerspective(currentPerspective,
					PerspectiveUtil
							.getPerspectiveFileFromModelFile(currentModelFile));
		} else {
			PerspectiveUtil.switchPerspective(probPerspective);
			PerspectiveUtil.exportPerspective(probPerspective, PerspectiveUtil
					.getPerspectiveFileFromModelFile(currentModelFile));
			PerspectiveUtil.switchPerspective(currentPerspective);
		}
		return true;
	}

	@Override
	public void postShutdown(IWorkbench workbench) {
	}

}
