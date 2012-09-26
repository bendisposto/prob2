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
package org.eventb.emf.core.machine;

import org.eclipse.emf.common.util.EList;

import org.eventb.emf.core.EventBNamedCommentedElement;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Event</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eventb.emf.core.machine.Event#getConvergence <em>Convergence</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.Event#isExtended <em>Extended</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.Event#getRefines <em>Refines</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.Event#getRefinesNames <em>Refines Names</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.Event#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.Event#getGuards <em>Guards</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.Event#getWitnesses <em>Witnesses</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.Event#getActions <em>Actions</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eventb.emf.core.machine.MachinePackage#getEvent()
 * @model
 * @generated
 */
public interface Event extends EventBNamedCommentedElement {
	/**
	 * Returns the value of the '<em><b>Convergence</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eventb.emf.core.machine.Convergence}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Convergence</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Convergence</em>' attribute.
	 * @see org.eventb.emf.core.machine.Convergence
	 * @see #setConvergence(Convergence)
	 * @see org.eventb.emf.core.machine.MachinePackage#getEvent_Convergence()
	 * @model
	 * @generated
	 */
	Convergence getConvergence();

	/**
	 * Sets the value of the '{@link org.eventb.emf.core.machine.Event#getConvergence <em>Convergence</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Convergence</em>' attribute.
	 * @see org.eventb.emf.core.machine.Convergence
	 * @see #getConvergence()
	 * @generated
	 */
	void setConvergence(Convergence value);

	/**
	 * Returns the value of the '<em><b>Extended</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Extended</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Extended</em>' attribute.
	 * @see #isSetExtended()
	 * @see #unsetExtended()
	 * @see #setExtended(boolean)
	 * @see org.eventb.emf.core.machine.MachinePackage#getEvent_Extended()
	 * @model unsettable="true"
	 * @generated
	 */
	boolean isExtended();

	/**
	 * Sets the value of the '{@link org.eventb.emf.core.machine.Event#isExtended <em>Extended</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Extended</em>' attribute.
	 * @see #isSetExtended()
	 * @see #unsetExtended()
	 * @see #isExtended()
	 * @generated
	 */
	void setExtended(boolean value);

	/**
	 * Unsets the value of the '{@link org.eventb.emf.core.machine.Event#isExtended <em>Extended</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetExtended()
	 * @see #isExtended()
	 * @see #setExtended(boolean)
	 * @generated
	 */
	void unsetExtended();

	/**
	 * Returns whether the value of the '{@link org.eventb.emf.core.machine.Event#isExtended <em>Extended</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Extended</em>' attribute is set.
	 * @see #unsetExtended()
	 * @see #isExtended()
	 * @see #setExtended(boolean)
	 * @generated
	 */
	boolean isSetExtended();

	/**
	 * Returns the value of the '<em><b>Refines</b></em>' reference list.
	 * The list contents are of type {@link org.eventb.emf.core.machine.Event}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Refines</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Refines</em>' reference list.
	 * @see org.eventb.emf.core.machine.MachinePackage#getEvent_Refines()
	 * @model
	 * @generated
	 */
	EList<Event> getRefines();

	/**
	 * Returns the value of the '<em><b>Refines Names</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Refines Names</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Refines Names</em>' attribute list.
	 * @see org.eventb.emf.core.machine.MachinePackage#getEvent_RefinesNames()
	 * @model transient="true" volatile="true" derived="true"
	 * @generated
	 */
	EList<String> getRefinesNames();

	/**
	 * Returns the value of the '<em><b>Parameters</b></em>' containment reference list.
	 * The list contents are of type {@link org.eventb.emf.core.machine.Parameter}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parameters</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameters</em>' containment reference list.
	 * @see org.eventb.emf.core.machine.MachinePackage#getEvent_Parameters()
	 * @model containment="true"
	 * @generated
	 */
	EList<Parameter> getParameters();

	/**
	 * Returns the value of the '<em><b>Guards</b></em>' containment reference list.
	 * The list contents are of type {@link org.eventb.emf.core.machine.Guard}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Guards</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Guards</em>' containment reference list.
	 * @see org.eventb.emf.core.machine.MachinePackage#getEvent_Guards()
	 * @model containment="true"
	 * @generated
	 */
	EList<Guard> getGuards();

	/**
	 * Returns the value of the '<em><b>Witnesses</b></em>' containment reference list.
	 * The list contents are of type {@link org.eventb.emf.core.machine.Witness}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Witnesses</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Witnesses</em>' containment reference list.
	 * @see org.eventb.emf.core.machine.MachinePackage#getEvent_Witnesses()
	 * @model containment="true"
	 * @generated
	 */
	EList<Witness> getWitnesses();

	/**
	 * Returns the value of the '<em><b>Actions</b></em>' containment reference list.
	 * The list contents are of type {@link org.eventb.emf.core.machine.Action}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Actions</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Actions</em>' containment reference list.
	 * @see org.eventb.emf.core.machine.MachinePackage#getEvent_Actions()
	 * @model containment="true"
	 * @generated
	 */
	EList<Action> getActions();

} // Event
