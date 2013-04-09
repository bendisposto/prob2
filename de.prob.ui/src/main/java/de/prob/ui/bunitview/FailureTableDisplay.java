/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/**
 *
 */
package de.prob.ui.bunitview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import de.prob.ui.Activator;

public class FailureTableDisplay implements ITraceDisplay {
	private final Table fTable;

	private final Image fExceptionIcon = Activator.getDefault()
			.getImageRegistry().get(Activator.JUNIT_CAUGHT_EXCEPTION);

	private final Image fStackIcon = Activator.getDefault().getImageRegistry()
			.get(Activator.JUNIT_STACK);

	private final Font COURIER;

	public FailureTableDisplay(final Table table) {
		fTable = table;
		COURIER = new Font(table.getDisplay(), "Monospace", 10, SWT.NONE);
		fTable.getParent().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(final DisposeEvent e) {
				COURIER.dispose();
				disposeIcons();
			}
		});
	}

	@Override
	public void addTraceLine(final int lineType, final String label) {
		TableItem tableItem = newTableItem();
		tableItem.setFont(COURIER);

		switch (lineType) {
		case TextualTrace.LINE_TYPE_EXCEPTION:
			tableItem.setImage(fExceptionIcon);
			break;
		case TextualTrace.LINE_TYPE_STACKFRAME:
			tableItem.setImage(fStackIcon);
			break;
		case TextualTrace.LINE_TYPE_NORMAL:
		default:
			break;
		}
		tableItem.setText(label);
	}

	public Image getExceptionIcon() {
		return fExceptionIcon;
	}

	public Image getStackIcon() {
		return fStackIcon;
	}

	public Table getTable() {
		return fTable;
	}

	private void disposeIcons() {
		if (fExceptionIcon != null && !fExceptionIcon.isDisposed()) {
			fExceptionIcon.dispose();
		}
		if (fStackIcon != null && !fStackIcon.isDisposed()) {
			fStackIcon.dispose();
		}
	}

	TableItem newTableItem() {
		return new TableItem(fTable, SWT.NONE);
	}
}
