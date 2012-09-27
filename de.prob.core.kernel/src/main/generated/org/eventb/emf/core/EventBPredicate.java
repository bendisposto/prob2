/**
 * Copyright (c) 2006-2010
 * University of Southampton, Heinrich-Heine University Dusseldorf and others.
 * All rights reserved. This program and the accompanying materials  are made
 * available under the terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 *
 * $Id$
 */
package org.eventb.emf.core;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Event BPredicate</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eventb.emf.core.EventBPredicate#getPredicate <em>Predicate</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eventb.emf.core.CorePackage#getEventBPredicate()
 * @model abstract="true"
 * @generated
 */
public interface EventBPredicate extends EObject {
	/**
	 * Returns the value of the '<em><b>Predicate</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Predicate</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Predicate</em>' attribute.
	 * @see #setPredicate(String)
	 * @see org.eventb.emf.core.CorePackage#getEventBPredicate_Predicate()
	 * @model default="true" required="true"
	 * @generated
	 */
	String getPredicate();

	/**
	 * Sets the value of the '{@link org.eventb.emf.core.EventBPredicate#getPredicate <em>Predicate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Predicate</em>' attribute.
	 * @see #getPredicate()
	 * @generated
	 */
	void setPredicate(String value);

} // EventBPredicate
