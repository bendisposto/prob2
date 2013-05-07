package de.prob.ui.eventb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;

import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.AbstractModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.History;
import de.prob.statespace.IAnimationListener;
import de.prob.statespace.StateSpace;
import de.prob.webconsole.ServletContextListener;

public class ResourceUtil implements IAnimationListener {

	public static class ModificationListener implements IResourceChangeListener {

		private final IPath path;
		private final File modelFile;

		public ModificationListener(final IFile resource, final File modelFile) {
			this.modelFile = modelFile;
			if (resource == null) {
				path = null;
			} else {
				path = resource.getProject().getFullPath();
			}
		}

		@Override
		public void resourceChanged(final IResourceChangeEvent event) {
			if (path != null) {
				final IResourceDelta delta = event.getDelta();
				IResourceDelta member = delta.findMember(path);
				if (member != null) {
					List<StateSpace> statespaces = animations.getStatespaces();
					for (StateSpace stateSpace : statespaces) {
						AbstractModel model = stateSpace.getModel();
						if (model.getModelFile().equals(modelFile)) {
							model.setDirty();
						}
					}
				}
			}
		}
	}

	private static ResourceUtil instance = null;
	private static List<ModificationListener> listeners = new ArrayList<ResourceUtil.ModificationListener>();
	private static final AnimationSelector animations = ServletContextListener.INJECTOR
			.getInstance(AnimationSelector.class);

	private ResourceUtil() {
	}

	public static void registerResourceListener(final IEventBRoot r,
			final EventBModel m) {
		IFile resource = extractResource(r);
		File file = r.getUnderlyingResource().getRawLocation().toFile();
		ModificationListener listener = new ModificationListener(resource, file);
		listeners.add(listener);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener);
	}

	private static IFile extractResource(final IEventBRoot rootElement) {
		IFile resource = null;
		if (rootElement == null) {
			resource = null;
		} else if (rootElement instanceof IMachineRoot) {
			resource = ((IMachineRoot) rootElement).getSCMachineRoot()
					.getResource();
		} else if (rootElement instanceof IContextRoot) {
			resource = ((IContextRoot) rootElement).getSCContextRoot()
					.getResource();
		}
		return resource;
	}

	public static ResourceUtil getInstance() {
		if (instance == null) {
			instance = new ResourceUtil();
		}
		return instance;
	}

	@Override
	public void currentStateChanged(final History oldHistory,
			final History newHistory) {
	}

	@Override
	public void removeHistory(final History history) {

	}
}
