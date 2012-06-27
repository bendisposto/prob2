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

import org.eventb.emf.core.context.Context;

import org.eventb.emf.core.impl.EventBNamedCommentedComponentElementImpl;

import org.eventb.emf.core.machine.Event;
import org.eventb.emf.core.machine.Invariant;
import org.eventb.emf.core.machine.Machine;
import org.eventb.emf.core.machine.MachinePackage;
import org.eventb.emf.core.machine.Variable;
import org.eventb.emf.core.machine.Variant;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Machine</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eventb.emf.core.machine.impl.MachineImpl#getRefines <em>Refines</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.impl.MachineImpl#getRefinesNames <em>Refines Names</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.impl.MachineImpl#getSees <em>Sees</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.impl.MachineImpl#getSeesNames <em>Sees Names</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.impl.MachineImpl#getVariables <em>Variables</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.impl.MachineImpl#getInvariants <em>Invariants</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.impl.MachineImpl#getVariant <em>Variant</em>}</li>
 *   <li>{@link org.eventb.emf.core.machine.impl.MachineImpl#getEvents <em>Events</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MachineImpl extends EventBNamedCommentedComponentElementImpl implements Machine {
	/**
	 * The cached value of the '{@link #getRefines() <em>Refines</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRefines()
	 * @generated
	 * @ordered
	 */
	protected EList<Machine> refines;

	/**
	 * The cached value of the '{@link #getSees() <em>Sees</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSees()
	 * @generated
	 * @ordered
	 */
	protected EList<Context> sees;

	/**
	 * The cached value of the '{@link #getVariables() <em>Variables</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVariables()
	 * @generated
	 * @ordered
	 */
	protected EList<Variable> variables;

	/**
	 * The cached value of the '{@link #getInvariants() <em>Invariants</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInvariants()
	 * @generated
	 * @ordered
	 */
	protected EList<Invariant> invariants;

	/**
	 * The cached value of the '{@link #getVariant() <em>Variant</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVariant()
	 * @generated
	 * @ordered
	 */
	protected Variant variant;

	/**
	 * The cached value of the '{@link #getEvents() <em>Events</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEvents()
	 * @generated
	 * @ordered
	 */
	protected EList<Event> events;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MachineImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MachinePackage.Literals.MACHINE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Machine> getRefines() {
		if (refines == null) {
			refines = new EObjectResolvingEList<Machine>(Machine.class, this, MachinePackage.MACHINE__REFINES);
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
	public EList<Context> getSees() {
		if (sees == null) {
			sees = new EObjectResolvingEList<Context>(Context.class, this, MachinePackage.MACHINE__SEES);
		}
		return sees;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<String> getSeesNames() {
		// TODO: implement this method to return the 'Sees Names' attribute list
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
	public EList<Variable> getVariables() {
		if (variables == null) {
			variables = new EObjectContainmentEList<Variable>(Variable.class, this, MachinePackage.MACHINE__VARIABLES);
		}
		return variables;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Invariant> getInvariants() {
		if (invariants == null) {
			invariants = new EObjectContainmentEList<Invariant>(Invariant.class, this, MachinePackage.MACHINE__INVARIANTS);
		}
		return invariants;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Variant getVariant() {
		return variant;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetVariant(Variant newVariant, NotificationChain msgs) {
		Variant oldVariant = variant;
		variant = newVariant;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MachinePackage.MACHINE__VARIANT, oldVariant, newVariant);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setVariant(Variant newVariant) {
		if (newVariant != variant) {
			NotificationChain msgs = null;
			if (variant != null)
				msgs = ((InternalEObject)variant).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MachinePackage.MACHINE__VARIANT, null, msgs);
			if (newVariant != null)
				msgs = ((InternalEObject)newVariant).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MachinePackage.MACHINE__VARIANT, null, msgs);
			msgs = basicSetVariant(newVariant, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MachinePackage.MACHINE__VARIANT, newVariant, newVariant));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Event> getEvents() {
		if (events == null) {
			events = new EObjectContainmentEList<Event>(Event.class, this, MachinePackage.MACHINE__EVENTS);
		}
		return events;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MachinePackage.MACHINE__VARIABLES:
				return ((InternalEList<?>)getVariables()).basicRemove(otherEnd, msgs);
			case MachinePackage.MACHINE__INVARIANTS:
				return ((InternalEList<?>)getInvariants()).basicRemove(otherEnd, msgs);
			case MachinePackage.MACHINE__VARIANT:
				return basicSetVariant(null, msgs);
			case MachinePackage.MACHINE__EVENTS:
				return ((InternalEList<?>)getEvents()).basicRemove(otherEnd, msgs);
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
			case MachinePackage.MACHINE__REFINES:
				return getRefines();
			case MachinePackage.MACHINE__REFINES_NAMES:
				return getRefinesNames();
			case MachinePackage.MACHINE__SEES:
				return getSees();
			case MachinePackage.MACHINE__SEES_NAMES:
				return getSeesNames();
			case MachinePackage.MACHINE__VARIABLES:
				return getVariables();
			case MachinePackage.MACHINE__INVARIANTS:
				return getInvariants();
			case MachinePackage.MACHINE__VARIANT:
				return getVariant();
			case MachinePackage.MACHINE__EVENTS:
				return getEvents();
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
			case MachinePackage.MACHINE__REFINES:
				getRefines().clear();
				getRefines().addAll((Collection<? extends Machine>)newValue);
				return;
			case MachinePackage.MACHINE__REFINES_NAMES:
				getRefinesNames().clear();
				getRefinesNames().addAll((Collection<? extends String>)newValue);
				return;
			case MachinePackage.MACHINE__SEES:
				getSees().clear();
				getSees().addAll((Collection<? extends Context>)newValue);
				return;
			case MachinePackage.MACHINE__SEES_NAMES:
				getSeesNames().clear();
				getSeesNames().addAll((Collection<? extends String>)newValue);
				return;
			case MachinePackage.MACHINE__VARIABLES:
				getVariables().clear();
				getVariables().addAll((Collection<? extends Variable>)newValue);
				return;
			case MachinePackage.MACHINE__INVARIANTS:
				getInvariants().clear();
				getInvariants().addAll((Collection<? extends Invariant>)newValue);
				return;
			case MachinePackage.MACHINE__VARIANT:
				setVariant((Variant)newValue);
				return;
			case MachinePackage.MACHINE__EVENTS:
				getEvents().clear();
				getEvents().addAll((Collection<? extends Event>)newValue);
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
			case MachinePackage.MACHINE__REFINES:
				getRefines().clear();
				return;
			case MachinePackage.MACHINE__REFINES_NAMES:
				getRefinesNames().clear();
				return;
			case MachinePackage.MACHINE__SEES:
				getSees().clear();
				return;
			case MachinePackage.MACHINE__SEES_NAMES:
				getSeesNames().clear();
				return;
			case MachinePackage.MACHINE__VARIABLES:
				getVariables().clear();
				return;
			case MachinePackage.MACHINE__INVARIANTS:
				getInvariants().clear();
				return;
			case MachinePackage.MACHINE__VARIANT:
				setVariant((Variant)null);
				return;
			case MachinePackage.MACHINE__EVENTS:
				getEvents().clear();
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
			case MachinePackage.MACHINE__REFINES:
				return refines != null && !refines.isEmpty();
			case MachinePackage.MACHINE__REFINES_NAMES:
				return !getRefinesNames().isEmpty();
			case MachinePackage.MACHINE__SEES:
				return sees != null && !sees.isEmpty();
			case MachinePackage.MACHINE__SEES_NAMES:
				return !getSeesNames().isEmpty();
			case MachinePackage.MACHINE__VARIABLES:
				return variables != null && !variables.isEmpty();
			case MachinePackage.MACHINE__INVARIANTS:
				return invariants != null && !invariants.isEmpty();
			case MachinePackage.MACHINE__VARIANT:
				return variant != null;
			case MachinePackage.MACHINE__EVENTS:
				return events != null && !events.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //MachineImpl
