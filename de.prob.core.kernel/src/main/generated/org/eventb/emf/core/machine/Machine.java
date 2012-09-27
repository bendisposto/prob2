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

import org.eventb.emf.core.EventBNamedCommentedComponentElement;

import org.eventb.emf.core.context.Context;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Machine</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eventb.emf.core.machine.Machine#getRefines <em>Refines</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.Machine#getRefinesNames <em>Refines Names</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.Machine#getSees <em>Sees</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.Machine#getSeesNames <em>Sees Names</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.Machine#getVariables <em>Variables</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.Machine#getInvariants <em>Invariants</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.Machine#getVariant <em>Variant</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.Machine#getEvents <em>Events</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eventb.emf.core.machine.MachinePackage#getMachine()
 * @model
 * @generated
 */
public interface Machine extends EventBNamedCommentedComponentElement {
	/**
	 * Returns the value of the '<em><b>Refines</b></em>' reference list.
	 * The list contents are of type {@link org.eventb.emf.core.machine.Machine}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Refines</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Refines</em>' reference list.
	 * @see org.eventb.emf.core.machine.MachinePackage#getMachine_Refines()
	 * @model
	 * @generated
	 */
	EList<Machine> getRefines();

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
	 * @see org.eventb.emf.core.machine.MachinePackage#getMachine_RefinesNames()
	 * @model transient="true" volatile="true" derived="true"
	 * @generated
	 */
	EList<String> getRefinesNames();

	/**
	 * Returns the value of the '<em><b>Sees</b></em>' reference list.
	 * The list contents are of type {@link org.eventb.emf.core.context.Context}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sees</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sees</em>' reference list.
	 * @see org.eventb.emf.core.machine.MachinePackage#getMachine_Sees()
	 * @model
	 * @generated
	 */
	EList<Context> getSees();

	/**
	 * Returns the value of the '<em><b>Sees Names</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sees Names</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sees Names</em>' attribute list.
	 * @see org.eventb.emf.core.machine.MachinePackage#getMachine_SeesNames()
	 * @model transient="true" volatile="true" derived="true"
	 * @generated
	 */
	EList<String> getSeesNames();

	/**
	 * Returns the value of the '<em><b>Variables</b></em>' containment reference list.
	 * The list contents are of type {@link org.eventb.emf.core.machine.Variable}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Variables</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Variables</em>' containment reference list.
	 * @see org.eventb.emf.core.machine.MachinePackage#getMachine_Variables()
	 * @model containment="true"
	 * @generated
	 */
	EList<Variable> getVariables();

	/**
	 * Returns the value of the '<em><b>Invariants</b></em>' containment reference list.
	 * The list contents are of type {@link org.eventb.emf.core.machine.Invariant}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Invariants</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Invariants</em>' containment reference list.
	 * @see org.eventb.emf.core.machine.MachinePackage#getMachine_Invariants()
	 * @model containment="true"
	 * @generated
	 */
	EList<Invariant> getInvariants();

	/**
	 * Returns the value of the '<em><b>Variant</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Variant</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Variant</em>' containment reference.
	 * @see #setVariant(Variant)
	 * @see org.eventb.emf.core.machine.MachinePackage#getMachine_Variant()
	 * @model containment="true"
	 * @generated
	 */
	Variant getVariant();

	/**
	 * Sets the value of the '{@link org.eventb.emf.core.machine.Machine#getVariant <em>Variant</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Variant</em>' containment reference.
	 * @see #getVariant()
	 * @generated
	 */
	void setVariant(Variant value);

	/**
	 * Returns the value of the '<em><b>Events</b></em>' containment reference list.
	 * The list contents are of type {@link org.eventb.emf.core.machine.Event}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Events</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Events</em>' containment reference list.
	 * @see org.eventb.emf.core.machine.MachinePackage#getMachine_Events()
	 * @model containment="true"
	 * @generated
	 */
	EList<Event> getEvents();

} // Machine
