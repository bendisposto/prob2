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
package org.eventb.emf.core.context.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eventb.emf.core.context.Axiom;
import org.eventb.emf.core.context.CarrierSet;
import org.eventb.emf.core.context.Constant;
import org.eventb.emf.core.context.Context;
import org.eventb.emf.core.context.ContextPackage;

import org.eventb.emf.core.impl.EventBNamedCommentedComponentElementImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Context</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eventb.emf.core.context.impl.ContextImpl#getExtends <em>Extends</em>}</li>
 *   <li>{@link org.eventb.emf.core.context.impl.ContextImpl#getExtendsNames <em>Extends Names</em>}</li>
 *   <li>{@link org.eventb.emf.core.context.impl.ContextImpl#getSets <em>Sets</em>}</li>
 *   <li>{@link org.eventb.emf.core.context.impl.ContextImpl#getConstants <em>Constants</em>}</li>
 *   <li>{@link org.eventb.emf.core.context.impl.ContextImpl#getAxioms <em>Axioms</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ContextImpl extends EventBNamedCommentedComponentElementImpl implements Context {
	/**
	 * The cached value of the '{@link #getExtends() <em>Extends</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExtends()
	 * @generated
	 * @ordered
	 */
	protected EList<Context> extends_;

	/**
	 * The cached value of the '{@link #getSets() <em>Sets</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSets()
	 * @generated
	 * @ordered
	 */
	protected EList<CarrierSet> sets;

	/**
	 * The cached value of the '{@link #getConstants() <em>Constants</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConstants()
	 * @generated
	 * @ordered
	 */
	protected EList<Constant> constants;

	/**
	 * The cached value of the '{@link #getAxioms() <em>Axioms</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAxioms()
	 * @generated
	 * @ordered
	 */
	protected EList<Axiom> axioms;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ContextImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ContextPackage.Literals.CONTEXT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Context> getExtends() {
		if (extends_ == null) {
			extends_ = new EObjectResolvingEList<Context>(Context.class, this, ContextPackage.CONTEXT__EXTENDS);
		}
		return extends_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<String> getExtendsNames() {
		// TODO: implement this method to return the 'Extends Names' attribute list
		// Ensure that you remove @generated or mark it @generated NOT
		// The list is expected to implement org.eclipse.emf.ecore.util.InternalEList and org.eclipse.emf.ecore.EStructuralFeature.Setting
		// so it's likely that an appropriate subclass of org.eclipse.emf.ecore.util.EcoreEList should be used.
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<CarrierSet> getSets() {
		if (sets == null) {
			sets = new EObjectContainmentEList<CarrierSet>(CarrierSet.class, this, ContextPackage.CONTEXT__SETS);
		}
		return sets;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Constant> getConstants() {
		if (constants == null) {
			constants = new EObjectContainmentEList<Constant>(Constant.class, this, ContextPackage.CONTEXT__CONSTANTS);
		}
		return constants;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Axiom> getAxioms() {
		if (axioms == null) {
			axioms = new EObjectContainmentEList<Axiom>(Axiom.class, this, ContextPackage.CONTEXT__AXIOMS);
		}
		return axioms;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ContextPackage.CONTEXT__SETS:
				return ((InternalEList<?>)getSets()).basicRemove(otherEnd, msgs);
			case ContextPackage.CONTEXT__CONSTANTS:
				return ((InternalEList<?>)getConstants()).basicRemove(otherEnd, msgs);
			case ContextPackage.CONTEXT__AXIOMS:
				return ((InternalEList<?>)getAxioms()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ContextPackage.CONTEXT__EXTENDS:
				return getExtends();
			case ContextPackage.CONTEXT__EXTENDS_NAMES:
				return getExtendsNames();
			case ContextPackage.CONTEXT__SETS:
				return getSets();
			case ContextPackage.CONTEXT__CONSTANTS:
				return getConstants();
			case ContextPackage.CONTEXT__AXIOMS:
				return getAxioms();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ContextPackage.CONTEXT__EXTENDS:
				getExtends().clear();
				getExtends().addAll((Collection<? extends Context>)newValue);
				return;
			case ContextPackage.CONTEXT__EXTENDS_NAMES:
				getExtendsNames().clear();
				getExtendsNames().addAll((Collection<? extends String>)newValue);
				return;
			case ContextPackage.CONTEXT__SETS:
				getSets().clear();
				getSets().addAll((Collection<? extends CarrierSet>)newValue);
				return;
			case ContextPackage.CONTEXT__CONSTANTS:
				getConstants().clear();
				getConstants().addAll((Collection<? extends Constant>)newValue);
				return;
			case ContextPackage.CONTEXT__AXIOMS:
				getAxioms().clear();
				getAxioms().addAll((Collection<? extends Axiom>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case ContextPackage.CONTEXT__EXTENDS:
				getExtends().clear();
				return;
			case ContextPackage.CONTEXT__EXTENDS_NAMES:
				getExtendsNames().clear();
				return;
			case ContextPackage.CONTEXT__SETS:
				getSets().clear();
				return;
			case ContextPackage.CONTEXT__CONSTANTS:
				getConstants().clear();
				return;
			case ContextPackage.CONTEXT__AXIOMS:
				getAxioms().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case ContextPackage.CONTEXT__EXTENDS:
				return extends_ != null && !extends_.isEmpty();
			case ContextPackage.CONTEXT__EXTENDS_NAMES:
				return !getExtendsNames().isEmpty();
			case ContextPackage.CONTEXT__SETS:
				return sets != null && !sets.isEmpty();
			case ContextPackage.CONTEXT__CONSTANTS:
				return constants != null && !constants.isEmpty();
			case ContextPackage.CONTEXT__AXIOMS:
				return axioms != null && !axioms.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //ContextImpl
