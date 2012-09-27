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

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eventb.emf.core.AbstractExtension;
import org.eventb.emf.core.Attribute;
import org.eventb.emf.core.CorePackage;
import org.eventb.emf.core.EventBElement;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Event BElement</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eventb.emf.core.impl.EventBElementImpl#getExtensions <em>Extensions</em>}</li>
 *   <li>{@link org.eventb.emf.core.impl.EventBElementImpl#getAttributes <em>Attributes</em>}</li>
 *   <li>{@link org.eventb.emf.core.impl.EventBElementImpl#getReference <em>Reference</em>}</li>
 *   <li>{@link org.eventb.emf.core.impl.EventBElementImpl#isGenerated <em>Generated</em>}</li>
 *   <li>{@link org.eventb.emf.core.impl.EventBElementImpl#isLocalGenerated <em>Local Generated</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class EventBElementImpl extends EventBObjectImpl implements EventBElement {
	/**
	 * The cached value of the '{@link #getExtensions() <em>Extensions</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExtensions()
	 * @generated
	 * @ordered
	 */
	protected EList<AbstractExtension> extensions;

	/**
	 * The cached value of the '{@link #getAttributes() <em>Attributes</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAttributes()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, Attribute> attributes;

	/**
	 * The default value of the '{@link #getReference() <em>Reference</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReference()
	 * @generated
	 * @ordered
	 */
	protected static final String REFERENCE_EDEFAULT = ""; //$NON-NLS-1$

	/**
	 * The cached value of the '{@link #getReference() <em>Reference</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReference()
	 * @generated
	 * @ordered
	 */
	protected String reference = REFERENCE_EDEFAULT;

	/**
	 * The default value of the '{@link #isGenerated() <em>Generated</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isGenerated()
	 * @generated
	 * @ordered
	 */
	protected static final boolean GENERATED_EDEFAULT = false;

	/**
	 * The default value of the '{@link #isLocalGenerated() <em>Local Generated</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isLocalGenerated()
	 * @generated
	 * @ordered
	 */
	protected static final boolean LOCAL_GENERATED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isLocalGenerated() <em>Local Generated</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isLocalGenerated()
	 * @generated
	 * @ordered
	 */
	protected boolean localGenerated = LOCAL_GENERATED_EDEFAULT;

	/**
	 * This is true if the Local Generated attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean localGeneratedESet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EventBElementImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CorePackage.Literals.EVENT_BELEMENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<AbstractExtension> getExtensions() {
		if (extensions == null) {
			extensions = new EObjectContainmentEList.Resolving<AbstractExtension>(AbstractExtension.class, this, CorePackage.EVENT_BELEMENT__EXTENSIONS);
		}
		return extensions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EMap<String, Attribute> getAttributes() {
		if (attributes == null) {
			attributes = new EcoreEMap<String,Attribute>(CorePackage.Literals.STRING_TO_ATTRIBUTE_MAP_ENTRY, StringToAttributeMapEntryImpl.class, this, CorePackage.EVENT_BELEMENT__ATTRIBUTES);
		}
		return attributes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setReference(String newReference) {
		String oldReference = reference;
		reference = newReference;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CorePackage.EVENT_BELEMENT__REFERENCE, oldReference, reference));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isGenerated() {
		// TODO: implement this method to return the 'Generated' attribute
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGenerated(boolean newGenerated) {
		// TODO: implement this method to set the 'Generated' attribute
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isLocalGenerated() {
		return localGenerated;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLocalGenerated(boolean newLocalGenerated) {
		boolean oldLocalGenerated = localGenerated;
		localGenerated = newLocalGenerated;
		boolean oldLocalGeneratedESet = localGeneratedESet;
		localGeneratedESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CorePackage.EVENT_BELEMENT__LOCAL_GENERATED, oldLocalGenerated, localGenerated, !oldLocalGeneratedESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetLocalGenerated() {
		boolean oldLocalGenerated = localGenerated;
		boolean oldLocalGeneratedESet = localGeneratedESet;
		localGenerated = LOCAL_GENERATED_EDEFAULT;
		localGeneratedESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, CorePackage.EVENT_BELEMENT__LOCAL_GENERATED, oldLocalGenerated, LOCAL_GENERATED_EDEFAULT, oldLocalGeneratedESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetLocalGenerated() {
		return localGeneratedESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getReferenceWithoutResolving() {
		if (this.eIsProxy()){
			return ((InternalEObject)this).eProxyURI().fragment();
		}else{
			return reference;
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void doSetReference(String newReference) {
		if (this.eIsProxy()){
			((InternalEObject)this).eProxyURI().appendFragment(newReference);
		}else{
			reference = newReference;
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case CorePackage.EVENT_BELEMENT__EXTENSIONS:
				return ((InternalEList<?>)getExtensions()).basicRemove(otherEnd, msgs);
			case CorePackage.EVENT_BELEMENT__ATTRIBUTES:
				return ((InternalEList<?>)getAttributes()).basicRemove(otherEnd, msgs);
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
			case CorePackage.EVENT_BELEMENT__EXTENSIONS:
				return getExtensions();
			case CorePackage.EVENT_BELEMENT__ATTRIBUTES:
				if (coreType) return getAttributes();
				else return getAttributes().map();
			case CorePackage.EVENT_BELEMENT__REFERENCE:
				return getReference();
			case CorePackage.EVENT_BELEMENT__GENERATED:
				return isGenerated();
			case CorePackage.EVENT_BELEMENT__LOCAL_GENERATED:
				return isLocalGenerated();
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
			case CorePackage.EVENT_BELEMENT__EXTENSIONS:
				getExtensions().clear();
				getExtensions().addAll((Collection<? extends AbstractExtension>)newValue);
				return;
			case CorePackage.EVENT_BELEMENT__ATTRIBUTES:
				((EStructuralFeature.Setting)getAttributes()).set(newValue);
				return;
			case CorePackage.EVENT_BELEMENT__REFERENCE:
				setReference((String)newValue);
				return;
			case CorePackage.EVENT_BELEMENT__GENERATED:
				setGenerated((Boolean)newValue);
				return;
			case CorePackage.EVENT_BELEMENT__LOCAL_GENERATED:
				setLocalGenerated((Boolean)newValue);
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
			case CorePackage.EVENT_BELEMENT__EXTENSIONS:
				getExtensions().clear();
				return;
			case CorePackage.EVENT_BELEMENT__ATTRIBUTES:
				getAttributes().clear();
				return;
			case CorePackage.EVENT_BELEMENT__REFERENCE:
				setReference(REFERENCE_EDEFAULT);
				return;
			case CorePackage.EVENT_BELEMENT__GENERATED:
				setGenerated(GENERATED_EDEFAULT);
				return;
			case CorePackage.EVENT_BELEMENT__LOCAL_GENERATED:
				unsetLocalGenerated();
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
			case CorePackage.EVENT_BELEMENT__EXTENSIONS:
				return extensions != null && !extensions.isEmpty();
			case CorePackage.EVENT_BELEMENT__ATTRIBUTES:
				return attributes != null && !attributes.isEmpty();
			case CorePackage.EVENT_BELEMENT__REFERENCE:
				return REFERENCE_EDEFAULT == null ? reference != null : !REFERENCE_EDEFAULT.equals(reference);
			case CorePackage.EVENT_BELEMENT__GENERATED:
				return isGenerated() != GENERATED_EDEFAULT;
			case CorePackage.EVENT_BELEMENT__LOCAL_GENERATED:
				return isSetLocalGenerated();
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
		result.append(" (reference: "); //$NON-NLS-1$
		result.append(reference);
		result.append(", localGenerated: "); //$NON-NLS-1$
		if (localGeneratedESet) result.append(localGenerated); else result.append("<unset>"); //$NON-NLS-1$
		result.append(')');
		return result.toString();
	}

} //EventBElementImpl
