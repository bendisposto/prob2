package de.prob.ui;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
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

	private final Collection<Job> jobs = new ArrayList<Job>();
	private final IJobChangeListener jobFinishedListener = new JobFinishedListener();

	// IMAGES
	public static final String IMG_FILTER = "IMG_FILTER";
	public static final String IMG_FORWARD = "IMG_FORWARD";
	public static final String IMG_BACK = "IMG_BACK";
	public static final String IMG_RESUME = "IMG_RESUME";
	public static final String IMG_SEARCH = "IMG_SEARCH";
	public static final String IMG_DISABLED = "IMG_DISABLED";
	public static final String IMG_TIMEOUT = "IMG_TIMEOUT";
	public static final String IMG_ENABLED = "IMG_ENABLED";
	public static final String IMG_SELECTED = "IMG_SELECTED";
	public static final String IMG_DOUBLECLICK = "IMG_DOUBLECLICK";
	public static final String OVERLAY = "OVERLAY";
	public static final String CHANGE_STAR = "change_star";
	public static final String IMG_RELOAD = "IMG_RELOAD";
	public static final String IMG_SPLASH = "IMG_SPLASH";
	public static final String IMG_LOADING = "IMG_LOADING";

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
		cancelAllJobs();
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
		reg.put(IMG_SELECTED,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/selected.png"));
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

	}

	private void cancelAllJobs() {
		synchronized (jobs) {
			for (final Job job : jobs) {
				job.cancel();
			}
		}
	}

	public void registerJob(final Job job) {
		synchronized (jobs) {
			jobs.add(job);
			job.addJobChangeListener(jobFinishedListener);
		}
	}

	private class JobFinishedListener extends JobChangeAdapter {
		@Override
		public void done(final IJobChangeEvent event) {
			super.done(event);
			synchronized (jobs) {
				jobs.remove(event.getJob());
			}
		}
	}

}
