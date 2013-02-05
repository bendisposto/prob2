package de.bmotionstudio.core.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IExportedPreferences;
import org.eclipse.core.runtime.preferences.IPreferenceFilter;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.PreferenceFilterEntry;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import de.bmotionstudio.core.editor.VisualizationViewPart;
import de.bmotionstudio.core.model.Simulation;
import de.bmotionstudio.core.model.VisualizationView;
import de.prob.ui.PerspectiveFactory;

public class PerspectiveUtil {

	public static void deletePerspective(
			IPerspectiveDescriptor perspectiveDescriptor) {
		IPerspectiveRegistry perspectiveRegistry = PlatformUI.getWorkbench()
				.getPerspectiveRegistry();
		perspectiveRegistry.deletePerspective(perspectiveDescriptor);
	}

	public static void closePerspective(
			IPerspectiveDescriptor perspectiveDescriptor) {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		page.closePerspective(perspectiveDescriptor, false, true);
	}

	public static void switchPerspective(
			IPerspectiveDescriptor perspectiveDescriptor) {
		switchPerspective(perspectiveDescriptor.getId());
	}

	public static void switchPerspective(String perspectiveID) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		try {
			workbench.showPerspective(perspectiveID,
					workbench.getActiveWorkbenchWindow());
		} catch (WorkbenchException e) {
		}
	}

	public static void exportPerspective(
			final IPerspectiveDescriptor perspectiveDescriptor,
			final IFile targetPerspectiveFile) {

		Assert.isNotNull(perspectiveDescriptor);

		FileOutputStream fos = null;
		ByteArrayInputStream inputStream = null;

		try {

			IPreferenceFilter[] transfers = null;

			transfers = new IPreferenceFilter[1];

			// For export all create a preference filter that can export
			// all nodes of the Instance and Configuration scopes
			transfers[0] = new IPreferenceFilter() {
				public String[] getScopes() {
					return new String[] { InstanceScope.SCOPE };
				}

				public Map<String, PreferenceFilterEntry[]> getMapping(
						String scope) {
					Map<String, PreferenceFilterEntry[]> map = new HashMap<String, PreferenceFilterEntry[]>();
					map.put("org.eclipse.ui.workbench",
							new PreferenceFilterEntry[] { new PreferenceFilterEntry(
									perspectiveDescriptor.getId() + "_persp") });
					return map;
				}
			};

			String content = "";

			inputStream = new ByteArrayInputStream(content.getBytes());
			NullProgressMonitor monitor = new NullProgressMonitor();
			if (!targetPerspectiveFile.exists()) {
				targetPerspectiveFile.create(inputStream, true, monitor);
			} else {
				targetPerspectiveFile.setContents(inputStream, true, false,
						monitor);
			}

			File exportFile = new File(targetPerspectiveFile.getLocationURI());
			fos = new FileOutputStream(exportFile);
			IPreferencesService service = Platform.getPreferencesService();
			service.exportPreferences(service.getRootNode(), transfers, fos);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null)
					fos.close();
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static VisualizationViewPart createVisualizationViewPart(
			String secId, VisualizationView visualizationView)
			throws PartInitException {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();
		VisualizationViewPart visualizationViewPart = (VisualizationViewPart) activePage
				.showView(VisualizationViewPart.ID, secId,
						IWorkbenchPage.VIEW_VISIBLE);
		return visualizationViewPart;
	}

	public static void importPerspective(final IFile perspectiveFile,
			final String perspectiveID) {

		FileInputStream fis = null;

		try {

			IPreferenceFilter[] transfers = null;
			transfers = new IPreferenceFilter[1];

			// Only import if a perspective file exists
			if (perspectiveFile.exists()) {

				File exportFile = new File(perspectiveFile.getLocationURI());
				fis = new FileInputStream(exportFile);
				IPreferencesService service = Platform.getPreferencesService();
				// service.importPreferences(fis);
				IExportedPreferences prefs = service.readPreferences(fis);
				transfers[0] = new IPreferenceFilter() {
					public String[] getScopes() {
						return new String[] { InstanceScope.SCOPE };
					}

					public Map<String, PreferenceFilterEntry[]> getMapping(
							String scope) {
						Map<String, PreferenceFilterEntry[]> map = new HashMap<String, PreferenceFilterEntry[]>();
						map.put("org.eclipse.ui.workbench",
								new PreferenceFilterEntry[] { new PreferenceFilterEntry(
										perspectiveID + "_persp") });
						return map;
					}
				};
				service.applyPreferences(prefs, transfers);
			}

		} catch (FileNotFoundException e) {
		} catch (CoreException e) {
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static IPerspectiveDescriptor openPerspective(IFile projectFile) {

		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		// Try to get the corresponding perspective
		IPerspectiveRegistry perspectiveRegistry = page.getWorkbenchWindow()
				.getWorkbench().getPerspectiveRegistry();
		String perspectiveId = getPerspectiveIdFromFile(projectFile);
		IPerspectiveDescriptor perspective = perspectiveRegistry
				.findPerspectiveWithId(perspectiveId);

		// Yes --> just switch to this perspective
		if (perspective != null) {
			PerspectiveUtil.switchPerspective(perspective);
		} else {
			// Check if a corresponding perspective file exists
			IFile perspectiveFile = projectFile.getProject().getFile(
					getPerspectiveFileName(projectFile));
			if (perspectiveFile.exists()) {
				PerspectiveUtil.importPerspective(perspectiveFile,
						perspectiveId);
				perspective = perspectiveRegistry
						.findPerspectiveWithId(perspectiveId);
				PerspectiveUtil.switchPerspective(perspective);
			} else {
				// No --> create a new perspective
				IPerspectiveDescriptor originalPerspectiveDescriptor = perspectiveRegistry
						.findPerspectiveWithId(PerspectiveFactory.PROB_PERSPECTIVE);
				PerspectiveUtil
						.switchPerspective(originalPerspectiveDescriptor);
				perspective = perspectiveRegistry.clonePerspective(
						perspectiveId, perspectiveId,
						originalPerspectiveDescriptor);
				// save the perspective
				page.savePerspectiveAs(perspective);
			}

		}

		return perspective;

	}
	
	public static String getPerspectiveIdFromFile(IFile projectFile) {
		return "ProB_" + projectFile.getName().replace(".bmso", "");
	}

	public static String getPerspectiveFileName(IFile projectFile) {
		return projectFile.getName().replace(".bmso", ".bmsop");
	}

	public static void initViews(Simulation simulation) {

		IWorkbenchPage activePage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IWorkbenchPartSite site = activePage.getActivePart().getSite();
		
		VisualizationViewPart visualizationViewPart = null;
		
		for (Map.Entry<String, VisualizationView> entry : simulation
				.getVisualizationViews().entrySet()) {
			
			String secId = entry.getKey();
			VisualizationView visView = entry.getValue();
			IViewReference viewReference = site.getPage().findViewReference(
					VisualizationViewPart.ID, secId);
			
			// Check if view already exists
			if (viewReference != null) {
				visualizationViewPart = (VisualizationViewPart) viewReference
						.getPart(true);
			} else {
				// If not, create a new one
				try {
					visualizationViewPart = PerspectiveUtil
							.createVisualizationViewPart(secId, visView);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}

			if (visualizationViewPart != null
					&& !visualizationViewPart.isInitialized()) {
				visualizationViewPart.init(simulation, visView);
			}
			
		}
		
		if(visualizationViewPart != null)
			activePage.activate(visualizationViewPart);

		// Close all unused visualization views
		for (IViewReference viewReference : site.getPage().getViewReferences()) {
			if (viewReference.getId().equals(VisualizationViewPart.ID)) {
				if (!simulation.getVisualizationViews().containsKey(
						viewReference.getSecondaryId()))
					site.getPage().hideView(viewReference);
			}
		}

	}
	
}
