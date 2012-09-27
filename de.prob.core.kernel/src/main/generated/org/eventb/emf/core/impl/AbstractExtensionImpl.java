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

import org.eventb.emf.core.AbstractExtension;
import org.eventb.emf.core.CorePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Abstract Extension</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eventb.emf.core.impl.AbstractExtensionImpl#getExtensionId <em>Extension Id</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class AbstractExtensionImpl extends EventBElementImpl implements AbstractExtension {
	/**
	 * The default value of the '{@link #getExtensionId() <em>Extension Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExtensionId()
	 * @generated
	 * @ordered
	 */
	protected static final String EXTENSION_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getExtensionId() <em>Extension Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExtensionId()
	 * @generated
	 * @ordered
	 */
	protected String extensionId = EXTENSION_ID_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AbstractExtensionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CorePackage.Literals.ABSTRACT_EXTENSION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getExtensionId() {
		return extensionId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setExtensionId(String newExtensionId) {
		String oldExtensionId = extensionId;
		extensionId = newExtensionId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CorePackage.ABSTRACT_EXTENSION__EXTENSION_ID, oldExtensionId, extensionId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CorePackage.ABSTRACT_EXTENSION__EXTENSION_ID:
				return getExtensionId();
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
			case CorePackage.ABSTRACT_EXTENSION__EXTENSION_ID:
				setExtensionId((String)newValue);
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
			case CorePackage.ABSTRACT_EXTENSION__EXTENSION_ID:
				setExtensionId(EXTENSION_ID_EDEFAULT);
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
			case CorePackage.ABSTRACT_EXTENSION__EXTENSION_ID:
				return EXTENSION_ID_EDEFAULT == null ? extensionId != null : !EXTENSION_ID_EDEFAULT.equals(extensionId);
		}
		return super.eIsSet(featureID);
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
		result.append(" (extensionId: "); //$NON-NLS-1$
		result.append(extensionId);
		result.append(')');
		return result.toString();
	}

} //AbstractExtensionImpl
