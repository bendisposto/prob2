/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class BMotionImage {

	private static ImageRegistry imageReg = new ImageRegistry();
	private static boolean isInit = false;

	public static final String IMG_LOGO_B = "logo_b";
	public static final String IMG_LOGO_BMOTION = "logo_bmotion";
	public static final String IMG_LOGO_BMOTION64 = "logo_bmotion64";
	public static final String IMG_ICON_BMOTION_RUN = "icon_bmotion_run";
	public static final String IMG_ICON_CHOP = "icon_chop";
	public static final String IMG_ICON_DELETE = "icon_delete";
	public static final String IMG_ICON_DELETE21 = "icon_delete21";
	public static final String IMG_ICON_EDIT = "icon_edit";
	public static final String IMG_ICON_CHECKED = "icon_checked";
	public static final String IMG_ICON_UNCHECKED = "icon_unchecked";
	public static final String IMG_RADIOBUTTON_CHECKED = "img_radiobutton_checked";
	public static final String IMG_RADIOBUTTON_UNCHECKED = "img_radiobutton_unchecked";
	public static final String IMG_ICON_OBSERVER = "icon_observer";
	public static final String IMG_ICON_LOADING = "icon_loading";
	public static final String IMG_ICON_LIBRARY = "icon_library";
	public static final String IMG_ICON_UP = "icon_up";
	public static final String IMG_ICON_DOWN = "icon_down";
	public static final String IMG_ICON_CONNECTION16 = "icon_connection16";
	public static final String IMG_ICON_CONNECTION24 = "icon_connection24";
	public static final String IMG_ICON_NEW_WIZ = "icon_new_wiz";
	public static final String IMG_ICON_DELETE_EDIT = "icon_delete_edit";
	public static final String IMG_ICON_TR_UP = "icon_tr_up";
	public static final String IMG_ICON_TR_LEFT = "icon_tr_left";
	public static final String IMG_ICON_CONTROL_HIDDEN = "icon_control_hidden";
	public static final String IMG_ICON_HELP = "icon_help";
	public static final String IMG_ICON_JPG = "icon_jpg";
	public static final String IMG_ICON_GIF = "icon_gif";
	public static final String IMG_SPLASH = "splash";

	public static ImageDescriptor getImageDescriptor(final String path) {
		return getImageDescriptor(BMotionEditorPlugin.PLUGIN_ID, path);
	}

	public static ImageDescriptor getImageDescriptor(final String pluginID,
			final String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(pluginID, path);
	}

	public static void registerImage(final String key, final String path) {
		ImageDescriptor desc = getImageDescriptor(path);
		imageReg.put(key, desc);
	}

	public static void registerImage(final String key, final String pluginID,
			final String path) {
		ImageDescriptor desc = getImageDescriptor(pluginID, path);
		imageReg.put(key, desc);
	}

	public static Image getImage(final String key) {
		if (!isInit)
			initializeImageRegistry();
		return imageReg.get(key);
	}

	public static Image getBControlImage(final String bcontrolID) {
		if (!isInit)
			initializeImageRegistry();
		return getImage("icon_control_" + bcontrolID);
	}

	private static void initializeImageRegistry() {

		registerImage(IMG_LOGO_B, "icons/logo_b.gif");
		registerImage(IMG_LOGO_BMOTION, "icons/logo_bmotion.png");
		registerImage(IMG_LOGO_BMOTION64, "icons/logo_bmotion_64.png");
		registerImage(IMG_ICON_BMOTION_RUN, "icons/icon_run.png");
		registerImage(IMG_ICON_CHOP, BMotionEditorPlugin.PLUGIN_ID,
				"icons/icon_chop.gif");
		registerImage(IMG_ICON_DELETE, BMotionEditorPlugin.PLUGIN_ID,
				"icons/icon_delete.gif");
		registerImage(IMG_ICON_DELETE21, BMotionEditorPlugin.PLUGIN_ID,
				"icons/icon_delete21.png");
		registerImage(IMG_ICON_CHECKED, BMotionEditorPlugin.PLUGIN_ID,
				"icons/controls/icon_checked.gif");
		registerImage(IMG_ICON_UNCHECKED, BMotionEditorPlugin.PLUGIN_ID,
				"icons/controls/icon_unchecked.gif");
		registerImage(IMG_ICON_EDIT, BMotionEditorPlugin.PLUGIN_ID,
				"icons/icon_edit.gif");
		registerImage(IMG_ICON_LOADING, BMotionEditorPlugin.PLUGIN_ID,
				"icons/icon_loading.gif");
		registerImage(IMG_ICON_LIBRARY, BMotionEditorPlugin.PLUGIN_ID,
				"icons/icon_library.gif");
		registerImage(IMG_ICON_UP, BMotionEditorPlugin.PLUGIN_ID,
				"icons/icon_up.gif");
		registerImage(IMG_ICON_DOWN, BMotionEditorPlugin.PLUGIN_ID,
				"icons/icon_down.gif");
		registerImage(IMG_ICON_CONNECTION16, BMotionEditorPlugin.PLUGIN_ID,
				"icons/icon_connection16.gif");
		registerImage(IMG_ICON_CONNECTION24, BMotionEditorPlugin.PLUGIN_ID,
				"icons/icon_connection24.gif");
		registerImage(IMG_ICON_CONTROL_HIDDEN, BMotionEditorPlugin.PLUGIN_ID,
				"icons/icon_invisible.gif");
		registerImage(IMG_ICON_NEW_WIZ, "org.eclipse.ui",
				"$nl$/icons/full/etool16/new_wiz.gif");
		registerImage(IMG_ICON_DELETE_EDIT, "org.eclipse.ui",
				"$nl$/icons/full/etool16/delete_edit.gif");
		registerImage(IMG_ICON_DELETE_EDIT, "org.eclipse.ui",
				"$nl$/icons/full/etool16/delete_edit.gif");
		registerImage(IMG_ICON_HELP, BMotionEditorPlugin.PLUGIN_ID,
				"icons/eclipse16/linkto_help.gif");
		registerImage(IMG_ICON_TR_UP, BMotionEditorPlugin.PLUGIN_ID,
				"icons/eclipse16/updated_co.gif");
		registerImage(IMG_ICON_TR_LEFT, BMotionEditorPlugin.PLUGIN_ID,
				"icons/eclipse16/updated_col.gif");
		registerImage(IMG_RADIOBUTTON_CHECKED, BMotionEditorPlugin.PLUGIN_ID,
				"icons/controls/icon_radiobutton_c.gif");
		registerImage(IMG_RADIOBUTTON_UNCHECKED, BMotionEditorPlugin.PLUGIN_ID,
				"icons/controls/icon_radiobutton_uc.gif");

		registerImage(IMG_ICON_JPG, BMotionEditorPlugin.PLUGIN_ID,
				"icons/icon_jpg.gif");
		registerImage(IMG_ICON_GIF, BMotionEditorPlugin.PLUGIN_ID,
				"icons/icon_gif.gif");

		registerImage(IMG_ICON_OBSERVER, BMotionEditorPlugin.PLUGIN_ID,
				"icons/icon_observer.gif");

		registerImage(IMG_SPLASH, BMotionEditorPlugin.PLUGIN_ID,
				"icons/splash.jpg");

		registerBControlImages();

		isInit = true;

	}

	private static void registerBControlImages() {

		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		final IExtensionPoint extensionPoint = registry
				.getExtensionPoint("de.bmotionstudio.core.control");

		for (final IExtension extension : extensionPoint.getExtensions()) {

			for (final IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {

				if ("control".equals(configurationElement.getName())) {

					final String icon = configurationElement
							.getAttribute("icon");
					final String ID = configurationElement.getAttribute("id");
					final String sourcePluginID = configurationElement
							.getContributor().getName();

					final String key = "icon_control_" + ID;

					registerImage(key, sourcePluginID, icon);

				}

			}

		}

	}

}
