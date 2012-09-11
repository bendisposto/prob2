package de.prob.ui;

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
	private static final Injector INJECTOR = ServletContextListener.INJECTOR;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		Main instance = INJECTOR.getInstance(Main.class);
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
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
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

}
