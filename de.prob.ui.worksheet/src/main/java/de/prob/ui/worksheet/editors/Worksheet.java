package de.prob.ui.worksheet.editors;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

import de.prob.ui.worksheet.WorksheetEditorInput;

//import org.eclipse.core.internal.resources.ResourceException;

public class Worksheet extends EditorPart {
	// Worksheet States
	private boolean browserLoaded = false;
	private boolean documentReady = false;
	private boolean worksheetLoaded = false;
	private boolean dirty = false;

	Browser worksheetBrowser;
	private static int SubSessionIdCounter = 0;
	private int subSessionId;

	private boolean newDocument = true;
	private String initialContent = "";
	private String sessionID;

	public Worksheet() {
		SubSessionIdCounter++;
		subSessionId = SubSessionIdCounter;
	}

	public void setDirty(boolean dirty) {
		if (this.dirty != dirty) {
			this.dirty = dirty;
			this.firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO add tests for files on other filesystems and linked files

		IEditorInput input = this.getEditorInput();
		if (input instanceof FileEditorInput) {
			setContentToFileEditorInput((FileEditorInput) input, monitor);
			setDirty(false);
		} else if (input instanceof FileStoreEditorInput) {
			setContentToFileStoreEditorInput((FileStoreEditorInput) input,
					monitor);
			setDirty(false);
		} else if (input instanceof WorksheetEditorInput) {
			doSaveAs();
		}

	}

	private void setContentToFileStoreEditorInput(FileStoreEditorInput input,
			IProgressMonitor monitor) {
		InputStream contentStream = getContentInputStream();
		String content = Worksheet.getStringFromInputStream(contentStream);
		URLConnection con = null;
		BufferedWriter out = null;
		try {
			IFileStore fileStore = EFS.getStore(input.getURI());
			IFileInfo inf = fileStore.fetchInfo();
			File file;
			if (!inf.exists()) {
				file = new File(input.getURI());
				file.createNewFile();
			}

			out = new BufferedWriter(new OutputStreamWriter(
					fileStore.openOutputStream(EFS.NONE, monitor), "UTF-8"));
			out.write(content);
			out.flush();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Add proper handling(e.g.
			// e.getStatus().getCode()==EFS.ERROR_EXISTS)

			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (contentStream != null)
					contentStream.close();
			} catch (IOException e) {
				try {
					if (out != null)
						out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}

		}
		// TODO Auto-generated method stub
	}

	private void setContentToFileEditorInput(FileEditorInput input,
			IProgressMonitor monitor) {
		InputStream contentStream = getContentInputStream();
		// if (FileEditorInput.isLocalFile(input.getFile())) {
		try {
			input.getFile().setContents(contentStream, true, true, monitor);
			setDirty(false);
		} catch (CoreException e) {
			// TODO add handling of Core Exception
			e.printStackTrace();
		} catch (OperationCanceledException e) {
			System.out.println("Save canceled");
		}

	}

	private InputStream getContentInputStream() {

		URL url = null;
		HttpURLConnection con = null;
		OutputStreamWriter writer = null;
		InputStream inStream = null;
		String body = "";

		try {
			url = new URL("http://localhost:8080/worksheet/saveDocument");

			body = Worksheet.addPOSTParameter(body, "worksheetSessionId",
					Integer.toString(this.subSessionId));

			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded; charset=utf-8");
			con.setRequestProperty("Content-Length",
					String.valueOf(body.length()));
			con.setRequestProperty("Accept-Charset", "UTF-8");
			String cookie = con.getRequestProperty("Cookie");
			if (cookie == null)
				cookie = "JSESSIONID=" + this.sessionID;
			if (!cookie.contains("JSESSIONID"))
				cookie += ";JSESSIONID=" + this.sessionID;

			con.setRequestProperty("Cookie", cookie);
			writer = new OutputStreamWriter(con.getOutputStream());
			writer.write(body);
			writer.flush();

			inStream = con.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return inStream;

	}

	private void sendCloseDocument() {

		URL url = null;
		HttpURLConnection con = null;
		OutputStreamWriter writer = null;
		String body = "";

		try {
			url = new URL("http://localhost:8080/worksheet/closeDocument");

			body = Worksheet.addPOSTParameter(body, "worksheetSessionId",
					Integer.toString(this.subSessionId));

			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded; charset=utf-8");
			con.setRequestProperty("Content-Length",
					String.valueOf(body.length()));
			con.setRequestProperty("Accept-Charset", "UTF-8");
			String cookie = con.getRequestProperty("Cookie");
			if (cookie == null)
				cookie = "JSESSIONID=" + this.sessionID;
			if (!cookie.contains("JSESSIONID"))
				cookie += ";JSESSIONID=" + this.sessionID;

			con.setRequestProperty("Cookie", cookie);
			writer = new OutputStreamWriter(con.getOutputStream());
			writer.write(body);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return;

	}

	private static String addPOSTParameter(String parameterString,
			String parameterName, String parameterValue) {
		if (!parameterString.equals("")) {
			parameterString += "&";
		}
		try {
			parameterString += URLEncoder.encode(parameterName, "UTF-8") + "="
					+ URLEncoder.encode(parameterValue, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Do a format c: when this happens ;)
			e.printStackTrace();
		}
		return parameterString;
	}

	@Override
	public void doSaveAs() {
		// FIXME files are not shown in save as dialog
		IProgressMonitor monitor = this.getEditorSite().getActionBars()
				.getStatusLineManager().getProgressMonitor();
		IEditorInput input = this.getEditorInput();
		// initialize and create SaveAsDialog
		SaveAsDialog dialog = new SaveAsDialog(getSite().getShell());

		if (this.getEditorInput() instanceof FileEditorInput
				&& FileEditorInput.isLocalFile(((FileEditorInput) input)
						.getFile())) {
			IFile oldFile = ((FileEditorInput) input).getFile();
			dialog.setOriginalFile(oldFile);
		} else {
			dialog.setOriginalName("worksheet.prob_wsh");
		}

		dialog.create();
		// open SaveAsDialog and check ReturnCode
		int res = dialog.open();
		if (res != Window.OK)
			monitor.setCanceled(true);

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IFile file = workspace.getRoot().getFile(dialog.getResult());

		try {
			file.create(this.getContentInputStream(), false, monitor);
			this.setInputWithNotify(new FileEditorInput(file));

		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// get Progressmonitor
		// doSaveAs2();

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		if (!(input instanceof FileEditorInput
				|| input instanceof WorksheetEditorInput || input instanceof FileStoreEditorInput))
			throw new PartInitException(
					"Worksheet Editor can't handle Inputs of type "
							+ input.getClass().getName());
		setInput(input);
		setSite(site);

		if (input instanceof FileEditorInput) {
			System.out.println("FileEditorInput");
			setInitialContent(getContentFromFileEditorInput((FileEditorInput) input));
			setNewDocument(false);
		} else if (input instanceof FileStoreEditorInput) {
			System.out.println("FileStoreEditorInput");
			setInitialContent(getContentFromFileStoreEditorInput((FileStoreEditorInput) input));
			setNewDocument(false);
		} else if (input instanceof WorksheetEditorInput) {
			System.out.println("WorksheetEditorInput");
			setInitialContent(getContentFromWorksheetEditorInput((WorksheetEditorInput) input));
			setNewDocument(true);
		}
	}

	private String getContentFromFileEditorInput(FileEditorInput input)
			throws PartInitException {
		InputStream inStream = null;
		String ret = "";
		try {
			if (FileEditorInput.isLocalFile(input.getFile())) {
				if (!input.getFile().isSynchronized(IResource.DEPTH_ZERO))
					input.getFile().refreshLocal(IResource.DEPTH_ZERO, null);
				input.getFile().setCharset("utf-8", null);
				inStream = input.getFile().getContents();
			} else {
				input.getFile().setCharset("utf-8", null);
				inStream = input.getStorage().getContents();
			}
			ret = getStringFromInputStream(inStream);
		} catch (CoreException e) {
			// TODO add complete Exception Handling for FileEditorInput
			throw new PartInitException(e.getStatus());
		} finally {
			if (inStream != null)
				try {
					inStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return ret;
	}

	private String getContentFromFileStoreEditorInput(FileStoreEditorInput input)
			throws PartInitException {
		InputStream stream = null;
		String ret = "";
		try {
			stream = input.getURI().toURL().openStream();
			ret = getStringFromInputStream(stream);
		} catch (MalformedURLException e) {
			// TODO add complete Exception Handling for FileStoreEditorInput
			throw new PartInitException(e.getLocalizedMessage());
		} catch (IOException e) {
			throw new PartInitException(e.getLocalizedMessage());
		} finally {
			if (stream != null)
				try {
					stream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return ret;
	}

	private String getContentFromWorksheetEditorInput(WorksheetEditorInput input) {
		return "";
	}

	private static String getStringFromInputStream(InputStream in) {
		BufferedReader rd = null;
		StringBuffer sb = null;
		try {
			rd = new BufferedReader(new InputStreamReader(in, "utf-8"));
			sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (sb == null)
			return "";
		return sb.toString();
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	// FIXME set port dynamically
	@Override
	public void createPartControl(Composite parent) {

		worksheetBrowser = new Browser(parent, SWT.NONE);
		worksheetBrowser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		new de.prob.ui.worksheet.editors.browserFunction.setDirty(
				worksheetBrowser, "setDirty", this);
		new de.prob.ui.worksheet.editors.browserFunction.domReady(
				worksheetBrowser, "domReady", this);
		new de.prob.ui.worksheet.editors.browserFunction.setSessionId(
				worksheetBrowser, "editorSetSessionId", this);
		new de.prob.ui.worksheet.editors.browserFunction.setWorksheetLoaded(
				worksheetBrowser, "setWorksheetLoaded", this);
		worksheetBrowser.setUrl("http://localhost:8080/worksheet/");

		// Snippet Start (from
		// http://wiki.eclipse.org/FAQ_Close_All_Editors_On_Shutdown)
		// TODO find a better solution maybe just remove the snippet
		// (http://wiki.eclipse.org/Eclipse_Plug-in_Development_FAQ#How_do_I_prevent_a_particular_editor_from_being_restored_on_the_next_workbench_startup.3F)
		IWorkbench workbench = PlatformUI.getWorkbench();
		final IWorkbenchPage activePage = workbench.getActiveWorkbenchWindow()
				.getActivePage();

		// workbench.getWorkingSetManager().addPropertyChangeListener(this);

		workbench.addWorkbenchListener(new IWorkbenchListener() {
			@Override
			public boolean preShutdown(IWorkbench workbench, boolean forced) {
				if (activePage != null) {
					IEditorReference[] refs = activePage.getEditorReferences();
					if (refs != null) {
						for (IEditorReference ref : refs)
							if (ref.getPart(false) instanceof Worksheet)
								activePage.closeEditor(
										(IEditorPart) ref.getPart(false), true);
					}
				}
				return true;
			}

			@Override
			public void postShutdown(IWorkbench workbench) {

			}
		});
	}

	@Override
	public void dispose() {
		this.sendCloseDocument();
		super.dispose();
	}

	@Override
	public void setFocus() {
		worksheetBrowser.setFocus();
	}

	public boolean isNewDocument() {
		return newDocument;
	}

	public void setNewDocument(boolean newDocument) {
		this.newDocument = newDocument;
	}

	public boolean isWorksheetLoaded() {
		return worksheetLoaded;
	}

	public void setWorksheetLoaded(boolean worksheetLoaded) {
		this.worksheetLoaded = worksheetLoaded;
	}

	public String getInitialContent() {
		return initialContent;
	}

	public void setInitialContent(String initialContent) {
		this.initialContent = initialContent;
	}

	public int getSubSessionId() {
		return subSessionId;
	}

	public void setSubSessionId(int subSessionId) {
		this.subSessionId = subSessionId;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
}
