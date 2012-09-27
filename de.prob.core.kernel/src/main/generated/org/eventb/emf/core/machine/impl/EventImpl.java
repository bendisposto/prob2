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
package org.eventb.emf.core.machine.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eventb.emf.core.impl.EventBNamedCommentedElementImpl;

import org.eventb.emf.core.machine.Action;
import org.eventb.emf.core.machine.Convergence;
import org.eventb.emf.core.machine.Event;
import org.eventb.emf.core.machine.Guard;
import org.eventb.emf.core.machine.MachinePackage;
import org.eventb.emf.core.machine.Parameter;
import org.eventb.emf.core.machine.Witness;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Event</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eventb.emf.core.machine.impl.EventImpl#getConvergence <em>Convergence</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.impl.EventImpl#isExtended <em>Extended</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.impl.EventImpl#getRefines <em>Refines</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.impl.EventImpl#getRefinesNames <em>Refines Names</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.impl.EventImpl#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.impl.EventImpl#getGuards <em>Guards</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.impl.EventImpl#getWitnesses <em>Witnesses</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.impl.EventImpl#getActions <em>Actions</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EventImpl extends EventBNamedCommentedElementImpl implements Event {
	/**
	 * The default value of the '{@link #getConvergence() <em>Convergence</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConvergence()
	 * @generated
	 * @ordered
	 */
	protected static final Convergence CONVERGENCE_EDEFAULT = Convergence.ORDINARY;

	/**
	 * The cached value of the '{@link #getConvergence() <em>Convergence</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConvergence()
	 * @generated
	 * @ordered
	 */
	protected Convergence convergence = CONVERGENCE_EDEFAULT;

	/**
	 * The default value of the '{@link #isExtended() <em>Extended</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isExtended()
	 * @generated
	 * @ordered
	 */
	protected static final boolean EXTENDED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isExtended() <em>Extended</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isExtended()
	 * @generated
	 * @ordered
	 */
	protected boolean extended = EXTENDED_EDEFAULT;

	/**
	 * This is true if the Extended attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean extendedESet;

	/**
	 * The cached value of the '{@link #getRefines() <em>Refines</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRefines()
	 * @generated
	 * @ordered
	 */
	protected EList<Event> refines;

	/**
	 * The cached value of the '{@link #getParameters() <em>Parameters</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParameters()
	 * @generated
	 * @ordered
	 */
	protected EList<Parameter> parameters;

	/**
	 * The cached value of the '{@link #getGuards() <em>Guards</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGuards()
	 * @generated
	 * @ordered
	 */
	protected EList<Guard> guards;

	/**
	 * The cached value of the '{@link #getWitnesses() <em>Witnesses</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWitnesses()
	 * @generated
	 * @ordered
	 */
	protected EList<Witness> witnesses;

	/**
	 * The cached value of the '{@link #getActions() <em>Actions</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getActions()
	 * @generated
	 * @ordered
	 */
	protected EList<Action> actions;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EventImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MachinePackage.Literals.EVENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Convergence getConvergence() {
		return convergence;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setConvergence(Convergence newConvergence) {
		Convergence oldConvergence = convergence;
		convergence = newConvergence == null ? CONVERGENCE_EDEFAULT : newConvergence;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MachinePackage.EVENT__CONVERGENCE, oldConvergence, convergence));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isExtended() {
		return extended;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setExtended(boolean newExtended) {
		boolean oldExtended = extended;
		extended = newExtended;
		boolean oldExtendedESet = extendedESet;
		extendedESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MachinePackage.EVENT__EXTENDED, oldExtended, extended, !oldExtendedESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetExtended() {
		boolean oldExtended = extended;
		boolean oldExtendedESet = extendedESet;
		extended = EXTENDED_EDEFAULT;
		extendedESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, MachinePackage.EVENT__EXTENDED, oldExtended, EXTENDED_EDEFAULT, oldExtendedESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetExtended() {
		return extendedESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Event> getRefines() {
		if (refines == null) {
			refines = new EObjectResolvingEList<Event>(Event.class, this, MachinePackage.EVENT__REFINES);
		}
		return refines;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<String> getRefinesNames() {
		// TODO: implement this method to return the 'Refines Names' attribute list
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
	public EList<Parameter> getParameters() {
		if (parameters == null) {
			parameters = new EObjectContainmentEList<Parameter>(Parameter.class, this, MachinePackage.EVENT__PARAMETERS);
		}
		return parameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Guard> getGuards() {
		if (guards == null) {
			guards = new EObjectContainmentEList<Guard>(Guard.class, this, MachinePackage.EVENT__GUARDS);
		}
		return guards;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Witness> getWitnesses() {
		if (witnesses == null) {
			witnesses = new EObjectContainmentEList<Witness>(Witness.class, this, MachinePackage.EVENT__WITNESSES);
		}
		return witnesses;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Action> getActions() {
		if (actions == null) {
			actions = new EObjectContainmentEList<Action>(Action.class, this, MachinePackage.EVENT__ACTIONS);
		}
		return actions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MachinePackage.EVENT__PARAMETERS:
				return ((InternalEList<?>)getParameters()).basicRemove(otherEnd, msgs);
			case MachinePackage.EVENT__GUARDS:
				return ((InternalEList<?>)getGuards()).basicRemove(otherEnd, msgs);
			case MachinePackage.EVENT__WITNESSES:
				return ((InternalEList<?>)getWitnesses()).basicRemove(otherEnd, msgs);
			case MachinePackage.EVENT__ACTIONS:
				return ((InternalEList<?>)getActions()).basicRemove(otherEnd, msgs);
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
			case MachinePackage.EVENT__CONVERGENCE:
				return getConvergence();
			case MachinePackage.EVENT__EXTENDED:
				return isExtended();
			case MachinePackage.EVENT__REFINES:
				return getRefines();
			case MachinePackage.EVENT__REFINES_NAMES:
				return getRefinesNames();
			case MachinePackage.EVENT__PARAMETERS:
				return getParameters();
			case MachinePackage.EVENT__GUARDS:
				return getGuards();
			case MachinePackage.EVENT__WITNESSES:
				return getWitnesses();
			case MachinePackage.EVENT__ACTIONS:
				return getActions();
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
			case MachinePackage.EVENT__CONVERGENCE:
				setConvergence((Convergence)newValue);
				return;
			case MachinePackage.EVENT__EXTENDED:
				setExtended((Boolean)newValue);
				return;
			case MachinePackage.EVENT__REFINES:
				getRefines().clear();
				getRefines().addAll((Collection<? extends Event>)newValue);
				return;
			case MachinePackage.EVENT__REFINES_NAMES:
				getRefinesNames().clear();
				getRefinesNames().addAll((Collection<? extends String>)newValue);
				return;
			case MachinePackage.EVENT__PARAMETERS:
				getParameters().clear();
				getParameters().addAll((Collection<? extends Parameter>)newValue);
				return;
			case MachinePackage.EVENT__GUARDS:
				getGuards().clear();
				getGuards().addAll((Collection<? extends Guard>)newValue);
				return;
			case MachinePackage.EVENT__WITNESSES:
				getWitnesses().clear();
				getWitnesses().addAll((Collection<? extends Witness>)newValue);
				return;
			case MachinePackage.EVENT__ACTIONS:
				getActions().clear();
				getActions().addAll((Collection<? extends Action>)newValue);
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
			case MachinePackage.EVENT__CONVERGENCE:
				setConvergence(CONVERGENCE_EDEFAULT);
				return;
			case MachinePackage.EVENT__EXTENDED:
				unsetExtended();
				return;
			case MachinePackage.EVENT__REFINES:
				getRefines().clear();
				return;
			case MachinePackage.EVENT__REFINES_NAMES:
				getRefinesNames().clear();
				return;
			case MachinePackage.EVENT__PARAMETERS:
				getParameters().clear();
				return;
			case MachinePackage.EVENT__GUARDS:
				getGuards().clear();
				return;
			case MachinePackage.EVENT__WITNESSES:
				getWitnesses().clear();
				return;
			case MachinePackage.EVENT__ACTIONS:
				getActions().clear();
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
			case MachinePackage.EVENT__CONVERGENCE:
				return convergence != CONVERGENCE_EDEFAULT;
			case MachinePackage.EVENT__EXTENDED:
				return isSetExtended();
			case MachinePackage.EVENT__REFINES:
				return refines != null && !refines.isEmpty();
			case MachinePackage.EVENT__REFINES_NAMES:
				return !getRefinesNames().isEmpty();
			case MachinePackage.EVENT__PARAMETERS:
				return parameters != null && !parameters.isEmpty();
			case MachinePackage.EVENT__GUARDS:
				return guards != null && !guards.isEmpty();
			case MachinePackage.EVENT__WITNESSES:
				return witnesses != null && !witnesses.isEmpty();
			case MachinePackage.EVENT__ACTIONS:
				return actions != null && !actions.isEmpty();
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
		result.append(" (convergence: "); //$NON-NLS-1$
		result.append(convergence);
		result.append(", extended: "); //$NON-NLS-1$
		if (extendedESet) result.append(extended); else result.append("<unset>"); //$NON-NLS-1$
		result.append(')');
		return result.toString();
	}

} //EventImpl
