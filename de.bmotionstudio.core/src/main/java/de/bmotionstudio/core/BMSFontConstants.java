package de.bmotionstudio.core;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class BMSFontConstants {

	public static final Font FONT_NORMAL;
	public static final Font FONT_SMALL;
	public static final Font FONT_TINY;
	public static final Font FONT_BOLD;
	public static final Font FONT_BOLD_ITALIC;
	public static final Font FONT_SMALL_ITALIC;
	public static final Font FONT_SMALL_BOLD;

	static {
		Font f = JFaceResources.getTextFont();
		FONT_NORMAL = makeFont(f, 10, SWT.NORMAL);
		FONT_TINY = makeFont(f, 6, SWT.NORMAL);
		FONT_SMALL = makeFont(f, 8, SWT.NORMAL);
		FONT_BOLD = makeFont(f, 10, SWT.BOLD);
		FONT_BOLD_ITALIC = makeFont(f, 10, SWT.BOLD | SWT.ITALIC);
		FONT_SMALL_ITALIC = makeFont(f, 8, SWT.ITALIC);
		FONT_SMALL_BOLD = makeFont(f, 8, SWT.BOLD);
	}
	
	private static Font makeFont(final Font font, final int size,
			final int style)
	{
		FontData[] fontData = font.getFontData();
		for (int i = 0; i < fontData.length; ++i) {
			fontData[i].setHeight(size);
			fontData[i].setStyle(style);
		}

		IWorkbench wb = PlatformUI.getWorkbench();
		
		return new Font(wb.getDisplay(), fontData);
	}
	
}
