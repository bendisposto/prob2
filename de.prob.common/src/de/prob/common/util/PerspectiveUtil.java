package de.prob.common.util;

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
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import de.prob.common.ProBConfiguration;

public class PerspectiveUtil {
	
	public static String PROB_PERSPECTIVE_FILE_EXTENSION = "probp";

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

		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchPage page = workbench.getActiveWorkbenchWindow()
				.getActivePage();
		// We need to save the perspective first
		page.savePerspectiveAs(perspectiveDescriptor);
		
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

	public static IPerspectiveDescriptor openPerspective(IFile modelFile) {

		ProBConfiguration.setCurrentModelFile(modelFile);
		
		IWorkbench workbench = PlatformUI.getWorkbench();
		
		// Try to get the corresponding perspective
		IPerspectiveRegistry perspectiveRegistry = workbench
				.getPerspectiveRegistry();
		String perspectiveId = getPerspectiveIdFromFile(modelFile);
		IPerspectiveDescriptor perspective = perspectiveRegistry
				.findPerspectiveWithId(perspectiveId);

		// Yes --> just switch to this perspective
		if (perspective != null) {
			PerspectiveUtil.switchPerspective(perspective);
		} else {
			// Check if a corresponding perspective file exists
			IFile perspectiveFile = modelFile.getProject().getFile(
					getPerspectiveFileName(modelFile));
			if (perspectiveFile.exists()) {
				PerspectiveUtil.importPerspective(perspectiveFile,
						perspectiveId);
				perspective = perspectiveRegistry
						.findPerspectiveWithId(perspectiveId);
				PerspectiveUtil.switchPerspective(perspective);
			} else {
				// No --> create a new perspective
				IPerspectiveDescriptor originalPerspectiveDescriptor = perspectiveRegistry
						.findPerspectiveWithId("de.prob.ui.perspective");
				PerspectiveUtil
						.switchPerspective(originalPerspectiveDescriptor);
				perspective = perspectiveRegistry.clonePerspective(
						perspectiveId, perspectiveId,
						originalPerspectiveDescriptor);
				// Save the perspective
				PerspectiveUtil.switchPerspective(perspective);
				PerspectiveUtil.exportPerspective(perspective, perspectiveFile);
			}
		}

		return perspective;

	}
	
	public static String getPerspectiveIdFromFile(IFile modelFile) {
		return "ProB_"
				+ modelFile.getName().replace(
						"." + modelFile.getFileExtension(), "");
	}

	public static String getPerspectiveFileName(IFile modelFile) {
		return modelFile.getName().replace("." + modelFile.getFileExtension(),
				"." + PROB_PERSPECTIVE_FILE_EXTENSION);
	}
	
}
