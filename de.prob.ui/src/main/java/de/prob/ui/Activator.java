package de.prob.ui;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.google.inject.Injector;

import de.prob.Main;
import de.prob.webconsole.ServletContextListener;
import de.prob.webconsole.WebConsole;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.prob.ui"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	public static final Injector INJECTOR = ServletContextListener.INJECTOR;

	public static final Main MAIN = INJECTOR.getInstance(Main.class);

	// IMAGES
	public static final String IMG_FILTER = "IMG_FILTER";
	public static final String IMG_FORWARD = "IMG_FORWARD";
	public static final String IMG_BACK = "IMG_BACK";
	public static final String IMG_RESUME = "IMG_RESUME";
	public static final String IMG_SEARCH = "IMG_SEARCH";
	public static final String IMG_DISABLED = "IMG_DISABLED";
	public static final String IMG_TIMEOUT = "IMG_TIMEOUT";
	public static final String IMG_ENABLED = "IMG_ENABLED";
	public static final String IMG_DOUBLECLICK = "IMG_DOUBLECLICK";
	public static final String OVERLAY = "OVERLAY";
	public static final String CHANGE_STAR = "change_star";
	public static final String IMG_RELOAD = "IMG_RELOAD";
	public static final String IMG_SPLASH = "IMG_SPLASH";
	public static final String IMG_LOADING = "IMG_LOADING";

	// JUnit Icons
	public static final String JUNIT_ERROR_OVR = "JUNIT_ERROR_OVR";
	public static final String JUNIT_FAILED_OVR = "JUNIT_FAILED_OVR";
	public static final String JUNIT_TSUITE_RUN = "JUNIT_TSUITE_RUN";
	public static final String JUNIT_TSUITE_FAIL = "JUNIT_TSUITE_FAIL";
	public static final String JUNIT_TSUITE_ERROR = "JUNIT_TSUITE_ERROR";
	public static final String JUNIT_TSUITE_OK = "JUNIT_TSUITE_OK";
	public static final String JUNIT_TSUITE = "JUNIT_TSUITE";
	public static final String JUNIT_STACK = "JUNIT_STACK";
	public static final String JUNIT_TEST_ERR = "JUNIT_TEST_ERR";
	public static final String JUNIT_TEST_FAIL = "JUNIT_TEST_FAIL";
	public static final String JUNIT_TEST_OK = "JUNIT_TEST_OK";
	public static final String JUNIT_CAUGHT_EXCEPTION = "JUNIT_CAUGHT_EXCEPTION";

	/**
	 * The constructor
	 */
	public Activator() {
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

		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					WebConsole.run(new Runnable() {
						@Override
						public void run() {
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		new Thread(r).start();
		plugin = this;
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
	public static Activator getDefault() {
		return plugin;
	}

	@Override
	protected void initializeImageRegistry(final ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		reg.put(IMG_FORWARD,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/forward.gif"));
		reg.put(IMG_BACK,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/back.gif"));
		reg.put(IMG_FILTER,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/filter_ps.gif"));
		reg.put(IMG_RESUME,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/resume.gif"));
		reg.put(IMG_SEARCH,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/search_src.gif"));
		reg.put(IMG_DISABLED,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/disabled.png"));
		reg.put(IMG_TIMEOUT,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/timeout.png"));
		reg.put(IMG_ENABLED,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/enabled.png"));
		reg.put(IMG_DOUBLECLICK,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/doubleclick.png"));
		reg.put(OVERLAY,
				imageDescriptorFromPlugin(PLUGIN_ID,
						"icons/splash_overlay_x.png"));
		reg.put(CHANGE_STAR,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/star.png"));
		reg.put(IMG_RELOAD,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/refresh.gif"));
		reg.put(IMG_SPLASH,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/probsplash.png"));
		reg.put(IMG_LOADING,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/icon_loading.gif"));
		
		// JUnit Icons
		reg.put(JUNIT_ERROR_OVR,
				imageDescriptorFromPlugin(PLUGIN_ID,
						"icons/junit/ovr16/error_ovr.gif"));
		reg.put(JUNIT_FAILED_OVR,
				imageDescriptorFromPlugin(PLUGIN_ID,
						"icons/junit/ovr16/failed_ovr.gif"));
		reg.put(JUNIT_TSUITE,
				imageDescriptorFromPlugin(PLUGIN_ID,
						"icons/junit/obj16/tsuite.gif"));
		reg.put(JUNIT_TSUITE_OK,
				imageDescriptorFromPlugin(PLUGIN_ID,
						"icons/junit/obj16/tsuiteok.gif"));
		reg.put(JUNIT_TSUITE_ERROR,
				imageDescriptorFromPlugin(PLUGIN_ID,
						"icons/junit/obj16/tsuiteerror.gif"));
		reg.put(JUNIT_TSUITE_FAIL,
				imageDescriptorFromPlugin(PLUGIN_ID,
						"icons/junit/obj16/tsuitefail.gif"));
		reg.put(JUNIT_TSUITE_RUN,
				imageDescriptorFromPlugin(PLUGIN_ID,
						"icons/junit/obj16/tsuiterun.gif"));
		reg.put(JUNIT_STACK,
				imageDescriptorFromPlugin(PLUGIN_ID,
						"icons/junit/obj16/stkfrm_obj.gif"));
		reg.put(JUNIT_TEST_ERR,
				imageDescriptorFromPlugin(PLUGIN_ID,
						"icons/junit/obj16/testerr.gif"));
		reg.put(JUNIT_TEST_FAIL,
				imageDescriptorFromPlugin(PLUGIN_ID,
						"icons/junit/obj16/testfail.gif"));
		reg.put(JUNIT_TEST_OK,
				imageDescriptorFromPlugin(PLUGIN_ID,
						"icons/junit/obj16/testok.gif"));
		reg.put(JUNIT_CAUGHT_EXCEPTION,
				imageDescriptorFromPlugin(PLUGIN_ID,
						"icons/junit/obj16/exc_catch.gif"));

	}
}
