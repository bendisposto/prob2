/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Brock Janiczak (brockj@tpg.com.au)
 *         - https://bugs.eclipse.org/bugs/show_bug.cgi?id=102236: [JUnit] display execution time next to each test
 *******************************************************************************/
package de.prob.ui.junitview;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.PageBook;

import com.google.common.collect.SetMultimap;

public class TestViewer {

	private final JUnitView fJUnitView;
	private PageBook fViewerbook;
	private TreeViewer viewer;
	private TestViewLabelProvider labelProvider;
	private TestViewContentProvider contentProvider;

	public TestViewer(final Composite parent, final JUnitView junitview) {
		fJUnitView = junitview;
		createTestViewers(parent);
	}

	private void createTestViewers(final Composite parent) {
		fViewerbook = new PageBook(parent, SWT.NULL);
		viewer = new TreeViewer(fViewerbook, SWT.V_SCROLL | SWT.SINGLE);

		labelProvider = new TestViewLabelProvider();
		contentProvider = new TestViewContentProvider();
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(labelProvider);

		fViewerbook.showPage(viewer.getTree());
	}

	public Control getTestViewerControl() {
		return fViewerbook;
	}

	public void update(final SetMultimap<String, Object> map) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				contentProvider.update(map);
				labelProvider.update(map);
				viewer.setInput(map.keySet());
				viewer.refresh();
				fViewerbook.showPage(viewer.getTree());
				viewer.expandAll();
			}
		});
	}

	public TreeViewer getViewer() {
		return viewer;
	}
}
