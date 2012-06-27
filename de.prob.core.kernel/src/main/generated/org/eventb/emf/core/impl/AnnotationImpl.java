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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eventb.emf.core.Annotation;
import org.eventb.emf.core.CorePackage;
import org.eventb.emf.core.EventBObject;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Annotation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eventb.emf.core.impl.AnnotationImpl#getSource <em>Source</em>}</li>
 *   <li>{@link org.eventb.emf.core.impl.AnnotationImpl#getDetails <em>Details</em>}</li>
 *   <li>{@link org.eventb.emf.core.impl.AnnotationImpl#getEventBObject <em>Event BObject</em>}</li>
 *   <li>{@link org.eventb.emf.core.impl.AnnotationImpl#getContents <em>Contents</em>}</li>
 *   <li>{@link org.eventb.emf.core.impl.AnnotationImpl#getReferences <em>References</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AnnotationImpl extends EventBObjectImpl implements Annotation {
	/**
	 * The default value of the '{@link #getSource() <em>Source</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSource()
	 * @generated
	 * @ordered
	 */
	protected static final String SOURCE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSource() <em>Source</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSource()
	 * @generated
	 * @ordered
	 */
	protected String source = SOURCE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getDetails() <em>Details</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDetails()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, String> details;

	/**
	 * The cached value of the '{@link #getContents() <em>Contents</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContents()
	 * @generated
	 * @ordered
	 */
	protected EList<EObject> contents;

	/**
	 * The cached value of the '{@link #getReferences() <em>References</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReferences()
	 * @generated
	 * @ordered
	 */
	protected EList<EObject> references;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AnnotationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CorePackage.Literals.ANNOTATION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getSource() {
		return source;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSource(String newSource) {
		String oldSource = source;
		source = newSource;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CorePackage.ANNOTATION__SOURCE, oldSource, source));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EMap<String, String> getDetails() {
		if (details == null) {
			details = new EcoreEMap<String,String>(CorePackage.Literals.STRING_TO_STRING_MAP_ENTRY, StringToStringMapEntryImpl.class, this, CorePackage.ANNOTATION__DETAILS);
		}
		return details;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EventBObject getEventBObject() {
		if (eContainerFeatureID() != CorePackage.ANNOTATION__EVENT_BOBJECT) return null;
		return (EventBObject)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetEventBObject(EventBObject newEventBObject, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newEventBObject, CorePackage.ANNOTATION__EVENT_BOBJECT, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setEventBObject(EventBObject newEventBObject) {
		if (newEventBObject != eInternalContainer() || (eContainerFeatureID() != CorePackage.ANNOTATION__EVENT_BOBJECT && newEventBObject != null)) {
			if (EcoreUtil.isAncestor(this, newEventBObject))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newEventBObject != null)
				msgs = ((InternalEObject)newEventBObject).eInverseAdd(this, CorePackage.EVENT_BOBJECT__ANNOTATIONS, EventBObject.class, msgs);
			msgs = basicSetEventBObject(newEventBObject, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CorePackage.ANNOTATION__EVENT_BOBJECT, newEventBObject, newEventBObject));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<EObject> getContents() {
		if (contents == null) {
			contents = new EObjectContainmentEList<EObject>(EObject.class, this, CorePackage.ANNOTATION__CONTENTS);
		}
		return contents;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<EObject> getReferences() {
		if (references == null) {
			references = new EObjectResolvingEList<EObject>(EObject.class, this, CorePackage.ANNOTATION__REFERENCES);
		}
		return references;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case CorePackage.ANNOTATION__EVENT_BOBJECT:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetEventBObject((EventBObject)otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case CorePackage.ANNOTATION__DETAILS:
				return ((InternalEList<?>)getDetails()).basicRemove(otherEnd, msgs);
			case CorePackage.ANNOTATION__EVENT_BOBJECT:
				return basicSetEventBObject(null, msgs);
			case CorePackage.ANNOTATION__CONTENTS:
				return ((InternalEList<?>)getContents()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID()) {
			case CorePackage.ANNOTATION__EVENT_BOBJECT:
				return eInternalContainer().eInverseRemove(this, CorePackage.EVENT_BOBJECT__ANNOTATIONS, EventBObject.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CorePackage.ANNOTATION__SOURCE:
				return getSource();
			case CorePackage.ANNOTATION__DETAILS:
				if (coreType) return getDetails();
				else return getDetails().map();
			case CorePackage.ANNOTATION__EVENT_BOBJECT:
				return getEventBObject();
			case CorePackage.ANNOTATION__CONTENTS:
				return getContents();
			case CorePackage.ANNOTATION__REFERENCES:
				return getReferences();
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
			case CorePackage.ANNOTATION__SOURCE:
				setSource((String)newValue);
				return;
			case CorePackage.ANNOTATION__DETAILS:
				((EStructuralFeature.Setting)getDetails()).set(newValue);
				return;
			case CorePackage.ANNOTATION__EVENT_BOBJECT:
				setEventBObject((EventBObject)newValue);
				return;
			case CorePackage.ANNOTATION__CONTENTS:
				getContents().clear();
				getContents().addAll((Collection<? extends EObject>)newValue);
				return;
			case CorePackage.ANNOTATION__REFERENCES:
				getReferences().clear();
				getReferences().addAll((Collection<? extends EObject>)newValue);
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
			case CorePackage.ANNOTATION__SOURCE:
				setSource(SOURCE_EDEFAULT);
				return;
			case CorePackage.ANNOTATION__DETAILS:
				getDetails().clear();
				return;
			case CorePackage.ANNOTATION__EVENT_BOBJECT:
				setEventBObject((EventBObject)null);
				return;
			case CorePackage.ANNOTATION__CONTENTS:
				getContents().clear();
				return;
			case CorePackage.ANNOTATION__REFERENCES:
				getReferences().clear();
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
			case CorePackage.ANNOTATION__SOURCE:
				return SOURCE_EDEFAULT == null ? source != null : !SOURCE_EDEFAULT.equals(source);
			case CorePackage.ANNOTATION__DETAILS:
				return details != null && !details.isEmpty();
			case CorePackage.ANNOTATION__EVENT_BOBJECT:
				return getEventBObject() != null;
			case CorePackage.ANNOTATION__CONTENTS:
				return contents != null && !contents.isEmpty();
			case CorePackage.ANNOTATION__REFERENCES:
				return references != null && !references.isEmpty();
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
		result.append(" (source: "); //$NON-NLS-1$
		result.append(source);
		result.append(')');
		return result.toString();
	}

} //AnnotationImpl
