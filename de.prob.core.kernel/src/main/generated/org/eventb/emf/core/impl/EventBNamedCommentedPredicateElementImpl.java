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
package org.eventb.emf.core.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eventb.emf.core.CorePackage;
import org.eventb.emf.core.EventBNamedCommentedPredicateElement;
import org.eventb.emf.core.EventBPredicate;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Event BNamed Commented Predicate Element</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eventb.emf.core.impl.EventBNamedCommentedPredicateElementImpl#getPredicate <em>Predicate</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class EventBNamedCommentedPredicateElementImpl extends EventBNamedCommentedElementImpl implements EventBNamedCommentedPredicateElement {
	/**
	 * The default value of the '{@link #getPredicate() <em>Predicate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPredicate()
	 * @generated
	 * @ordered
	 */
	protected static final String PREDICATE_EDEFAULT = "true"; //$NON-NLS-1$

	/**
	 * The cached value of the '{@link #getPredicate() <em>Predicate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPredicate()
	 * @generated
	 * @ordered
	 */
	protected String predicate = PREDICATE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EventBNamedCommentedPredicateElementImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CorePackage.Literals.EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getPredicate() {
		return predicate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPredicate(String newPredicate) {
		String oldPredicate = predicate;
		predicate = newPredicate;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CorePackage.EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__PREDICATE, oldPredicate, predicate));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CorePackage.EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__PREDICATE:
				return getPredicate();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case CorePackage.EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__PREDICATE:
				setPredicate((String)newValue);
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
			case CorePackage.EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__PREDICATE:
				setPredicate(PREDICATE_EDEFAULT);
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
			case CorePackage.EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__PREDICATE:
				return PREDICATE_EDEFAULT == null ? predicate != null : !PREDICATE_EDEFAULT.equals(predicate);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
		if (baseClass == EventBPredicate.class) {
			switch (derivedFeatureID) {
				case CorePackage.EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__PREDICATE: return CorePackage.EVENT_BPREDICATE__PREDICATE;
				default: return -1;
			}
		}
		return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
		if (baseClass == EventBPredicate.class) {
			switch (baseFeatureID) {
				case CorePackage.EVENT_BPREDICATE__PREDICATE: return CorePackage.EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__PREDICATE;
				default: return -1;
			}
		}
		return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (predicate: "); //$NON-NLS-1$
		result.append(predicate);
		result.append(')');
		return result.toString();
	}

} //EventBNamedCommentedPredicateElementImpl
