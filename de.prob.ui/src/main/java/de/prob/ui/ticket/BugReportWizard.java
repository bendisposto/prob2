package de.prob.ui.ticket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.internal.ConfigurationInfo;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

@SuppressWarnings("restriction")
public class BugReportWizard extends Wizard {

	Logger logger = LoggerFactory.getLogger(BugReportWizard.class);

	private WizardPage1 page1;
	private WizardPage2 page2;
	private WizardPage3 page3;

	private String email = "";
	private String summary = "";
	private Boolean addTrace = true;
	private String description = "";

	private boolean savedUsername = false;
	private String username = "";
	private String password = "";

	private static final Preferences TICKET_PREFS = Platform
			.getPreferencesService().getRootNode().node(InstanceScope.SCOPE)
			.node("prob_ticket_preferences");

	public BugReportWizard() {
		super();
		setNeedsProgressMonitor(true);
		this.email = TICKET_PREFS.get("email", "");
		this.savedUsername = TICKET_PREFS.getBoolean("saveUsr", false);
		this.username = savedUsername ? TICKET_PREFS.get("usr", "") : "";
		this.password = savedUsername ? TICKET_PREFS.get("pswd", "") : "";

	}

	public BugReportWizard(final String summary, final Boolean addTrace,
			final String description) {
		super();
		setNeedsProgressMonitor(true);

		this.email = TICKET_PREFS.get("email", "");
		this.savedUsername = TICKET_PREFS.getBoolean("saveUsr", false);
		this.username = savedUsername ? TICKET_PREFS.get("usr", "") : "";
		this.password = savedUsername ? TICKET_PREFS.get("pswd", "") : "";
		this.summary = summary;
		this.addTrace = addTrace;
		this.description = description;
	}

	@Override
	public void addPages() {
		page1 = new WizardPage1(email, summary, description, addTrace, true);
		page2 = new WizardPage2(username, password, savedUsername);
		page3 = new WizardPage3();
		addPage(page1);
		addPage(page2);
		addPage(page3);

	}

	@Override
	public boolean performFinish() {

		TICKET_PREFS.put("email", page1.getEmail());
		boolean saveUsr = page2.isSaveUsr();
		if (saveUsr) {
			TICKET_PREFS.putBoolean("saveUsr", true);
			TICKET_PREFS.put("usr", page2.getUsername());
			TICKET_PREFS.put("pswd", page2.getPassword());
		} else {
			TICKET_PREFS.remove("saveUsr");
			TICKET_PREFS.remove("usr");
			TICKET_PREFS.remove("pswd");
		}
		try {
			TICKET_PREFS.flush();
		} catch (BackingStoreException e) {
			logger.error(
					"Problem while storing preferences. "
							+ e.getLocalizedMessage(), e);
		}

		URI jiraServerUri;
		try {
			IssueInputBuilder issueBuilder = new IssueInputBuilder("PROBCORE",
					(long) 1, page1.getSummary());
			issueBuilder.setDescription("FROM: " + page1.getEmail() + "\n\n"
					+ page1.getDetailedDescription());

			jiraServerUri = new URI("http://jira.cobra.cs.uni-duesseldorf.de/");

			AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
			JiraRestClient client = factory.createWithBasicHttpAuthentication(
					jiraServerUri, page2.getUsername(), page2.getPassword());

			Promise<BasicIssue> createIssue = client.getIssueClient()
					.createIssue(issueBuilder.build());

			BasicIssue f = createIssue.get();

			// Creating issues currently doesn't work
			String issueURI = "http://jira.cobra.cs.uni-duesseldorf.de/rest/api/2/issue/"
					+ f.getKey();

			if (page1.isAddTrace()) {
				URI attachmentUri = new URI(issueURI + "/attachments");
				client.getIssueClient().addAttachments(attachmentUri,
						page3.getAttachmentInputs());
			}

		} catch (URISyntaxException e) {
			logger.error("The website url was incorrect");
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	// private File addInstallationDetailsToTicket() throws IOException {
	// // Installation Details
	// File[] installationDetailsFiles = new File[] { /* fetchPlugIns(), */
	// fetchConfiguration(), fetchErrorLog() };
	// File zipFile;
	// zipFile = File.createTempFile("InstallationDetails", ".tmp");
	// compressFiles(installationDetailsFiles, zipFile);
	//
	// return zipFile;
	// }

	// private void addTraceFileToTicket(final Ticket ticket) {
	// // Trace File
	// if (isEnabled) {
	// try {
	// File tmpFile = File.createTempFile("ProBTrace", ".tmp");
	// tmpFile.deleteOnExit();
	//
	// String data = Animator.getAnimator().getTrace()
	// .getTraceAsString();
	// String fileName = tmpFile.getAbsoluteFile().toString();
	//
	// OutputStreamWriter writer;
	// writer = new OutputStreamWriter(new FileOutputStream(fileName));
	// BufferedWriter out = new BufferedWriter(writer);
	// out.write(data, 0, data.length());
	// out.close();
	//
	// Attachment a = new Attachment(tmpFile.getAbsolutePath()
	// .toString(), "current trace");
	// a.setFilename("ProBTraceFile.txt");
	// ticket.addAttachment(a);
	//
	// } catch (IOException e) {
	// // Logger.notifyUserWithoutBugreport("Error adding trace file",
	// // e);
	// }
	// }
	// }

	private File fetchErrorLog() {
		// Error Log
		String filename = Platform.getLogFileLocation().toOSString();

		try {
			File errorFile = File.createTempFile("ErrorLog", ".txt");
			errorFile.deleteOnExit();

			BufferedReader input = new BufferedReader(new FileReader(filename));
			OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream(errorFile));
			BufferedWriter output = new BufferedWriter(writer);

			String line;
			boolean doCopy = false;
			String today = String.format("%1$tY-%1$tm-%1$td",
					Calendar.getInstance()); // current Date similar to
												// YYYY-MM-DD

			while ((line = input.readLine()) != null) {
				if (doCopy || line.startsWith("!SESSION " + today)) {
					doCopy = true;
					output.write(line);
					output.newLine();
				}
			}

			input.close();
			output.close();

			return errorFile;

		} catch (IOException e) {
			logger.error("Error while fetching Error Log", e);
		}
		return null;
	}

	private File fetchConfiguration() {
		// Configuration
		String summary = "";
		try {
			summary = ConfigurationInfo.getSystemSummary();
		} catch (Exception e) {
			StringBuffer sb = new StringBuffer(
					"Exception while getting System Summary.\n");
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			sb.append(sw.toString());
			summary = sb.toString();
		}
		try {
			File configFile = File.createTempFile("Configuration", ".txt");
			configFile.deleteOnExit();

			PrintWriter out = new PrintWriter(configFile);
			out.print(summary);
			out.close();

			return configFile;

		} catch (IOException e) {
			logger.error("Error while fetching Configuration", e);
		}
		return null;
	}

	//
	// private File fetchPlugIns() {
	// // Plug-ins
	// try {
	// File plugInsFile = File.createTempFile("PlugIns", ".txt");
	// plugInsFile.deleteOnExit();
	//
	// OutputStreamWriter writer;
	// writer = new OutputStreamWriter(new FileOutputStream(plugInsFile));
	// BufferedWriter output = new BufferedWriter(writer);
	//
	// for (Bundle b : Activator.getDefault().getInstalledBundles()) {
	// output.write(b.toString());
	// output.newLine();
	// }
	//
	// output.close();
	// return plugInsFile;
	//
	// } catch (IOException e) {
	// logger.error("Error while fetching Plug-ins", e);
	// }
	// return null;
	// }

	private void compressFiles(final File[] inputFiles, final File zipFile) {
		try {
			byte[] buf = new byte[4096];
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
					zipFile));
			for (int i = 0; i < inputFiles.length; i++) {
				File inFile = inputFiles[i];
				FileInputStream inStream = new FileInputStream(inFile);
				out.putNextEntry(new ZipEntry(inFile.getName()));
				int len;
				while ((len = inStream.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				inStream.close();
			}
			out.close();

		} catch (IOException e) {
			logger.error("Error while compressing Files", e);
		}
	}

}
