/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core;

import org.eclipse.core.resources.IFile;

import de.bmotionstudio.core.model.control.Visualization;

/**
 * @author Lukas Ladenberger
 * 
 */
public interface ILanguageService {

	public void startProBAnimator(Visualization v);

	public boolean isLanguageFile(IFile f);

}
