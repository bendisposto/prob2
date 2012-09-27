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
import org.eventb.emf.core.EventBDerived;
import org.eventb.emf.core.EventBNamedCommentedDerivedPredicateElement;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Event BNamed Commented Derived Predicate Element</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eventb.emf.core.impl.EventBNamedCommentedDerivedPredicateElementImpl#isTheorem <em>Theorem</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class EventBNamedCommentedDerivedPredicateElementImpl extends EventBNamedCommentedPredicateElementImpl implements EventBNamedCommentedDerivedPredicateElement {
	/**
	 * The default value of the '{@link #isTheorem() <em>Theorem</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isTheorem()
	 * @generated
	 * @ordered
	 */
	protected static final boolean THEOREM_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isTheorem() <em>Theorem</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isTheorem()
	 * @generated
	 * @ordered
	 */
	protected boolean theorem = THEOREM_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EventBNamedCommentedDerivedPredicateElementImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CorePackage.Literals.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isTheorem() {
		return theorem;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTheorem(boolean newTheorem) {
		boolean oldTheorem = theorem;
		theorem = newTheorem;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__THEOREM, oldTheorem, theorem));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__THEOREM:
				return isTheorem();
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
			case CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__THEOREM:
				setTheorem((Boolean)newValue);
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
			case CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__THEOREM:
				setTheorem(THEOREM_EDEFAULT);
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
			case CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__THEOREM:
				return theorem != THEOREM_EDEFAULT;
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
		if (baseClass == EventBDerived.class) {
			switch (derivedFeatureID) {
				case CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__THEOREM: return CorePackage.EVENT_BDERIVED__THEOREM;
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
		if (baseClass == EventBDerived.class) {
			switch (baseFeatureID) {
				case CorePackage.EVENT_BDERIVED__THEOREM: return CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__THEOREM;
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
		result.append(" (theorem: "); //$NON-NLS-1$
		result.append(theorem);
		result.append(')');
		return result.toString();
	}

} //EventBNamedCommentedDerivedPredicateElementImpl
