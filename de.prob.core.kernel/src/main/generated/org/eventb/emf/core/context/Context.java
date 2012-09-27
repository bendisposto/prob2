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
package org.eventb.emf.core.context;

import org.eclipse.emf.common.util.EList;

import org.eventb.emf.core.EventBNamedCommentedComponentElement;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Context</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eventb.emf.core.context.Context#getExtends <em>Extends</em>}</li>
 *   <li>{@link org.eventb.emf.core.context.Context#getExtendsNames <em>Extends Names</em>}</li>
 *   <li>{@link org.eventb.emf.core.context.Context#getSets <em>Sets</em>}</li>
 *   <li>{@link org.eventb.emf.core.context.Context#getConstants <em>Constants</em>}</li>
 *   <li>{@link org.eventb.emf.core.context.Context#getAxioms <em>Axioms</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eventb.emf.core.context.ContextPackage#getContext()
 * @model
 * @generated
 */
public interface Context extends EventBNamedCommentedComponentElement {
	/**
	 * Returns the value of the '<em><b>Extends</b></em>' reference list.
	 * The list contents are of type {@link org.eventb.emf.core.context.Context}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Extends</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Extends</em>' reference list.
	 * @see org.eventb.emf.core.context.ContextPackage#getContext_Extends()
	 * @model
	 * @generated
	 */
	EList<Context> getExtends();

	/**
	 * Returns the value of the '<em><b>Extends Names</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Extends Names</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Extends Names</em>' attribute list.
	 * @see org.eventb.emf.core.context.ContextPackage#getContext_ExtendsNames()
	 * @model transient="true" volatile="true" derived="true"
	 * @generated
	 */
	EList<String> getExtendsNames();

	/**
	 * Returns the value of the '<em><b>Sets</b></em>' containment reference list.
	 * The list contents are of type {@link org.eventb.emf.core.context.CarrierSet}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sets</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sets</em>' containment reference list.
	 * @see org.eventb.emf.core.context.ContextPackage#getContext_Sets()
	 * @model containment="true"
	 * @generated
	 */
	EList<CarrierSet> getSets();

	/**
	 * Returns the value of the '<em><b>Constants</b></em>' containment reference list.
	 * The list contents are of type {@link org.eventb.emf.core.context.Constant}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Constants</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Constants</em>' containment reference list.
	 * @see org.eventb.emf.core.context.ContextPackage#getContext_Constants()
	 * @model containment="true"
	 * @generated
	 */
	EList<Constant> getConstants();

	/**
	 * Returns the value of the '<em><b>Axioms</b></em>' containment reference list.
	 * The list contents are of type {@link org.eventb.emf.core.context.Axiom}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Axioms</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Axioms</em>' containment reference list.
	 * @see org.eventb.emf.core.context.ContextPackage#getContext_Axioms()
	 * @model containment="true"
	 * @generated
	 */
	EList<Axiom> getAxioms();

} // Context
