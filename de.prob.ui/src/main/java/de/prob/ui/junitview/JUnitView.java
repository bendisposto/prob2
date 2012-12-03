package de.prob.ui.junitview;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.google.inject.Injector;

import de.prob.statespace.History;
import de.prob.ui.Activator;
import de.prob.ui.junitview.parts.CounterPanel;
import de.prob.ui.junitview.parts.JUnitProgressBar;
import de.prob.webconsole.ServletContextListener;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class JUnitView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.prob.ui.junitview.JUnitView";

	private TableViewer viewer;
	private History currentHistory; // FIXME not used?

	Injector injector = ServletContextListener.INJECTOR;

	private CounterPanel fCounterPanel;
	private JUnitProgressBar fProgressBar;
	private Composite fCounterComposite;
	private SashForm fSashForm;
	private Clipboard fClipboard;
	private TestViewer fTestViewer;

	private FailureTrace fFailureTrace;

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		@Override
		public String getColumnText(final Object obj, final int index) {
			return getText(obj);
		}

		@Override
		public Image getColumnImage(final Object obj, final int index) {
			return null;
		}

		@Override
		public Image getImage(final Object obj) {
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(final Composite parent) {

		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		parent.setLayout(gridLayout);

		fCounterComposite = createProgressCountPanel(parent);
		fCounterComposite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));
		SashForm sashForm = createSashForm(parent);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

	}

	protected Composite createProgressCountPanel(final Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		layout.numColumns = 1;

		fCounterPanel = new CounterPanel(composite);
		fCounterPanel.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));
		fProgressBar = new JUnitProgressBar(composite);
		fProgressBar.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));
		return composite;
	}

	private SashForm createSashForm(final Composite parent) {
		fSashForm = new SashForm(parent, SWT.VERTICAL);

		ViewForm top = new ViewForm(fSashForm, SWT.NONE);

		Composite empty = new Composite(top, SWT.NONE);
		empty.setLayout(new Layout() {
			@Override
			protected Point computeSize(final Composite composite,
					final int wHint, final int hHint, final boolean flushCache) {
				return new Point(1, 1); // (0, 0) does not work with
										// super-intelligent ViewForm
			}

			@Override
			protected void layout(final Composite composite,
					final boolean flushCache) {
			}
		});
		top.setTopLeft(empty); // makes ViewForm draw the horizontal separator
								// line ...
		fTestViewer = new TestViewer(top, this);
		top.setContent(fTestViewer.getTestViewerControl());

		ViewForm bottom = new ViewForm(fSashForm, SWT.NONE);

		CLabel label = new CLabel(bottom, SWT.NONE);
		label.setText("Failure Trace");
		label.setImage(Activator.getDefault().getImageRegistry()
				.get(Activator.JUNIT_STACK));
		bottom.setTopLeft(label);
		fFailureTrace = new FailureTrace(bottom, this);
		bottom.setContent(fFailureTrace.getComposite());

		fSashForm.setWeights(new int[] { 50, 50 });
		return fSashForm;
	}

	@Override
	public void setFocus() {

	}
}