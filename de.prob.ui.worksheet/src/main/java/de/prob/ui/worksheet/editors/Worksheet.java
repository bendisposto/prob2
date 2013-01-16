package de.prob.ui.worksheet.editors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

//TODO start instance of jetty Server
public class Worksheet extends EditorPart {
	Browser worksheetBrowser;

	@Override
	public void doSave(IProgressMonitor monitor) {
		if (this.getEditorInput() instanceof IFileEditorInput) {
			FileEditorInput input = (FileEditorInput) this.getEditorInput();
			if (FileEditorInput.isLocalFile(input.getFile())) {
				InputStream stream = httpGETInputStream("http://localhost:8080/worksheet/getFile");
				try {
					input.getFile().setContents(stream, false, true, monitor);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				doSaveAs2(monitor);
			}
		} else if (this.getEditorInput() instanceof IURIEditorInput) {
			// TODO add FileSave Handling of URIEditorInputs (e.g. files outside
			// the workspace)
		}
	}

	private void doSaveAs2(IProgressMonitor monitor) {
		// check if FileEditorInput or URIEditorInput
		IEditorInput input = getEditorInput();

		if (input instanceof IFileEditorInput) {
			IFileEditorInput fileEditorInput = (IFileEditorInput) input;
			IFile oldFile = fileEditorInput.getFile();
			// initialize and create SaveAsDialog
			SaveAsDialog dialog = new SaveAsDialog(getSite().getShell());
			if (!FileEditorInput.isLocalFile(oldFile)) {
				// this is a new File
				dialog.setOriginalName("worksheet.prob_wsh");
			} else {
				dialog.setOriginalFile(oldFile);
			}
			dialog.create();
			// open SaveAsDialog and check ReturnCode
			int res = dialog.open();
			if (res != SaveAsDialog.OK)
				monitor.setCanceled(true);

			// create the new File
			IPath filePath = dialog.getResult();
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IFile file = workspace.getRoot().getFile(filePath);
			// create new FileEditorInput and set it to this editors input
			FileEditorInput newInput = new FileEditorInput(file);
			setInput(newInput);
			// doSave now;
			InputStream stream = httpGETInputStream("http://localhost:8080/worksheet/getFile");
			try {
				file.create(stream, false, monitor);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (input instanceof IURIEditorInput) {
			// TODO add FileSaveAs Handling of URIEditorInputs (e.g. files
			// outside the workspace)
		}
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

		// get Progressmonitor
		doSaveAs2(this.getEditorSite().getActionBars().getStatusLineManager().getProgressMonitor());

	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if (input instanceof FileEditorInput || input instanceof IURIEditorInput) {
			initDocument();
			postContent("Test Content");
			setInput(input);
			setSite(site);
			/*
			 * try { fileStore =
			 * EFS.getStore(((IURIEditorInput)input).getURI()); } catch
			 * (CoreException ex) { String
			 * msg="Error while opening URI: \n"+((IURIEditorInput
			 * )input).getURI();
			 * MessageDialog.openError(this.getSite().getShell(), "Error", msg);
			 * return; } IFile file = getWorkspaceFile(fileStore); if (file !=
			 * null) newInput = new FileEditorInput(file); else newInput = new
			 * FileStoreEditorInput(fileStore);
			 * 
			 * FileStore x = new File((IURIEditorInput)
			 * input).getURI().setSite(site);
			 */
			// TODO maybe check if a selectionListener makes sense for anything
		} else {
			throw new PartInitException("Editor just accepts a FileEditor Input");
		}
	}

	private String readInputStream(InputStream stream) {
		return "";
	}

	private InputStream httpGETInputStream(String url) {
		URL remote_url;
		HttpURLConnection con = null;
		InputStream ret = null;
		try {
			remote_url = new URL(url);
			con = (HttpURLConnection) remote_url.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			ret = con.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
		return ret;
	}

	private String httpGETContent(String url) {
		String ret = "";
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(httpGETInputStream(url)));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			ret = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
		return ret;
	}

	private boolean initDocument() {
		URL remote_url;
		HttpURLConnection con = null;
		InputStream ret = null;
		try {
			remote_url = new URL("http://localhost:8080/worksheet/initDocument");
			con = (HttpURLConnection) remote_url.openConnection();
			con.setRequestMethod("POST");
			con.connect();
			int respCode = con.getResponseCode();
			if (respCode != HttpURLConnection.HTTP_ACCEPTED)
				return false;

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			con.disconnect();
		}
		return true;
	}

	private boolean postContent(String content) {
		URL remote_url;
		HttpURLConnection con = null;
		InputStream stream = null;

		try {
			remote_url = new URL("http://localhost:8080/worksheet/setContent");
			con = (HttpURLConnection) remote_url.openConnection();

			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.connect();
			con.getOutputStream().write(("Content=" + content).getBytes());
			int respCode = con.getResponseCode();
			if (respCode != HttpURLConnection.HTTP_ACCEPTED)
				return false;
		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			con.disconnect();
		}
		return true;
	}

	private String httpPOSTContent(String url) {
		String ret = "";
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(httpGETInputStream(url)));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			ret = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
		return ret;
	}

	@Override
	public boolean isDirty() {
		//Boolean res = Boolean.parseBoolean(httpGETContent("http://localhost:8080/worksheet/isDirty"));
		// TODO check if this is the right place;
		boolean res=false;
		return res;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	@Override
	public void createPartControl(Composite parent) {

		worksheetBrowser = new Browser(parent, SWT.NONE);
		worksheetBrowser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		worksheetBrowser.setUrl("http://localhost:8080/worksheet");
	}

	@Override
	public void setFocus() {
		worksheetBrowser.setFocus();
	}

	class fireDocumentChanged extends BrowserFunction {

		public fireDocumentChanged(Browser browser) {
			super(browser, "fireDocumentChanged");

		}

		@Override
		public Object function(Object[] arguments) {
			super.function(arguments);
			firePropertyChange(PROP_DIRTY);
			return null;
		}

	}
}
