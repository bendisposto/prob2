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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Annotation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eventb.emf.core.Annotation#getSource <em>Source</em>}</li>
 *   <li>{@link org.eventb.emf.core.Annotation#getDetails <em>Details</em>}</li>
 *   <li>{@link org.eventb.emf.core.Annotation#getEventBObject <em>Event BObject</em>}</li>
 *   <li>{@link org.eventb.emf.core.Annotation#getContents <em>Contents</em>}</li>
 *   <li>{@link org.eventb.emf.core.Annotation#getReferences <em>References</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eventb.emf.core.CorePackage#getAnnotation()
 * @model
 * @generated
 */
public interface Annotation extends EventBObject {
	/**
	 * Returns the value of the '<em><b>Source</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Source</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Source</em>' attribute.
	 * @see #setSource(String)
	 * @see org.eventb.emf.core.CorePackage#getAnnotation_Source()
	 * @model
	 * @generated
	 */
	String getSource();

	/**
	 * Sets the value of the '{@link org.eventb.emf.core.Annotation#getSource <em>Source</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source</em>' attribute.
	 * @see #getSource()
	 * @generated
	 */
	void setSource(String value);

	/**
	 * Returns the value of the '<em><b>Details</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link java.lang.String},
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Details</em>' map isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Details</em>' map.
	 * @see org.eventb.emf.core.CorePackage#getAnnotation_Details()
	 * @model mapType="org.eventb.emf.core.StringToStringMapEntry<org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString>"
	 * @generated
	 */
	EMap<String, String> getDetails();

	/**
	 * Returns the value of the '<em><b>Event BObject</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.eventb.emf.core.EventBObject#getAnnotations <em>Annotations</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Event BObject</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Event BObject</em>' container reference.
	 * @see #setEventBObject(EventBObject)
	 * @see org.eventb.emf.core.CorePackage#getAnnotation_EventBObject()
	 * @see org.eventb.emf.core.EventBObject#getAnnotations
	 * @model opposite="annotations" resolveProxies="false"
	 * @generated
	 */
	EventBObject getEventBObject();

	/**
	 * Sets the value of the '{@link org.eventb.emf.core.Annotation#getEventBObject <em>Event BObject</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Event BObject</em>' container reference.
	 * @see #getEventBObject()
	 * @generated
	 */
	void setEventBObject(EventBObject value);

	/**
	 * Returns the value of the '<em><b>Contents</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.EObject}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Contents</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Contents</em>' containment reference list.
	 * @see org.eventb.emf.core.CorePackage#getAnnotation_Contents()
	 * @model containment="true"
	 * @generated
	 */
	EList<EObject> getContents();

	/**
	 * Returns the value of the '<em><b>References</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.EObject}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>References</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>References</em>' reference list.
	 * @see org.eventb.emf.core.CorePackage#getAnnotation_References()
	 * @model
	 * @generated
	 */
	EList<EObject> getReferences();

} // Annotation
