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
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.ui.worksheet.WorksheetEditorInput;
import de.prob.webconsole.WebConsole;

//import org.eclipse.core.internal.resources.ResourceException;

public class Worksheet extends EditorPart implements DisposeListener,
		LocationListener {
	static Logger logger = LoggerFactory.getLogger(Worksheet.class);
	// Worksheet States
	private boolean worksheetLoaded = false;
	private boolean dirty = false;

	Browser worksheetBrowser;
	private static int SubSessionIdCounter = 0;
	private int subSessionId;

	private boolean newDocument = true;
	private String initialContent = "";
	private String sessionID;

	public Worksheet() {
		Worksheet.SubSessionIdCounter++;
		subSessionId = Worksheet.SubSessionIdCounter;
	}

	public void setDirty(boolean dirty) {
		if (this.dirty != dirty) {
			this.dirty = dirty;
			firePropertyChange(IEditorPart.PROP_DIRTY);
			if (!dirty)
				cleanDirty();
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		Worksheet.logger.trace("{}", monitor);
		// TODO add tests for files on other filesystems and linked files

		IEditorInput input = getEditorInput();
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
			// TODO Auto-generated catch blocks
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
			Worksheet.logger.debug("Save canceled");
		}

	}

	private InputStream getContentInputStream() {

		URL url = null;
		HttpURLConnection con = null;
		OutputStreamWriter writer = null;
		InputStream inStream = null;
		String body = "";

		try {
			url = new URL("http://localhost:"+WebConsole.getPort()+"/saveDocument");

			body = Worksheet.addPOSTParameter(body, "worksheetSessionId",
					Integer.toString(subSessionId));

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
				cookie = "JSESSIONID=" + sessionID;
			if (!cookie.contains("JSESSIONID"))
				cookie += ";JSESSIONID=" + sessionID;

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
			url = new URL("http://localhost:"+WebConsole.getPort()+"/closeDocument");

			body = Worksheet.addPOSTParameter(body, "worksheetSessionId",
					Integer.toString(subSessionId));

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
				cookie = "JSESSIONID=" + sessionID;
			if (!cookie.contains("JSESSIONID"))
				cookie += ";JSESSIONID=" + sessionID;

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
		IProgressMonitor monitor = getEditorSite().getActionBars()
				.getStatusLineManager().getProgressMonitor();
		IEditorInput input = getEditorInput();
		// initialize and create SaveAsDialog
		SaveAsDialog dialog = new SaveAsDialog(getSite().getShell());

		if (getEditorInput() instanceof FileEditorInput
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
		if (res != Window.OK) {
			monitor.setCanceled(true);
		} else {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IFile file = workspace.getRoot().getFile(dialog.getResult());
			try {
				if (file.exists()) {
					file.setContents(getContentInputStream(), true, true,
							monitor);
					setDirty(false);
					setInputWithNotify(new FileEditorInput(file));
				} else {
					file.create(getContentInputStream(), true, monitor);
					setDirty(false);
					setInputWithNotify(new FileEditorInput(file));
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

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
			setInitialContent(getContentFromFileEditorInput((FileEditorInput) input));
			setNewDocument(false);
		} else if (input instanceof FileStoreEditorInput) {
			setInitialContent(getContentFromFileStoreEditorInput((FileStoreEditorInput) input));
			setNewDocument(false);
		} else if (input instanceof WorksheetEditorInput) {
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
			ret = Worksheet.getStringFromInputStream(inStream);
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
			ret = Worksheet.getStringFromInputStream(stream);
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

	private void cleanDirty() {
		worksheetBrowser
				.execute("$('.ui-worksheet').worksheet('setDirty',false)");
	}

	// FIXME set port dynamically
	@Override
	public void createPartControl(Composite parent) {
		parent.addDisposeListener(this);
		worksheetBrowser = new Browser(parent, SWT.NONE);
		worksheetBrowser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		worksheetBrowser.addLocationListener(this);
		new de.prob.ui.worksheet.editors.browserFunction.setDirty(
				worksheetBrowser, "setDirty", this);
		new de.prob.ui.worksheet.editors.browserFunction.domReady(
				worksheetBrowser, "domReady", this);
		new de.prob.ui.worksheet.editors.browserFunction.setSessionId(
				worksheetBrowser, "editorSetSessionId", this);
		new de.prob.ui.worksheet.editors.browserFunction.setWorksheetLoaded(
				worksheetBrowser, "setWorksheetLoaded", this);
		worksheetBrowser.setUrl("http://localhost:"+WebConsole.getPort()+"/worksheet.html");

	}

	@Override
	public void dispose() {
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

	static int coun = 0;

	@Override
	public void widgetDisposed(DisposeEvent e) {
		Worksheet.coun++;
		System.out.println("disposed " + Worksheet.coun);
	}

	@Override
	public void changing(LocationEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void changed(LocationEvent event) {
		// TODO Auto-generated method stub

	}
}
