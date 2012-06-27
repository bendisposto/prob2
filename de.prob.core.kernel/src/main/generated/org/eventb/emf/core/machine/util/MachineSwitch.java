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
package org.eventb.emf.core.machine.util;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eventb.emf.core.EventBAction;
import org.eventb.emf.core.EventBCommented;
import org.eventb.emf.core.EventBCommentedElement;
import org.eventb.emf.core.EventBCommentedExpressionElement;
import org.eventb.emf.core.EventBDerived;
import org.eventb.emf.core.EventBElement;
import org.eventb.emf.core.EventBExpression;
import org.eventb.emf.core.EventBNamed;
import org.eventb.emf.core.EventBNamedCommentedActionElement;
import org.eventb.emf.core.EventBNamedCommentedComponentElement;
import org.eventb.emf.core.EventBNamedCommentedDerivedPredicateElement;
import org.eventb.emf.core.EventBNamedCommentedElement;
import org.eventb.emf.core.EventBNamedCommentedPredicateElement;
import org.eventb.emf.core.EventBObject;
import org.eventb.emf.core.EventBPredicate;

import org.eventb.emf.core.machine.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eventb.emf.core.machine.MachinePackage
 * @generated
 */
public class MachineSwitch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static MachinePackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MachineSwitch() {
		if (modelPackage == null) {
			modelPackage = MachinePackage.eINSTANCE;
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	public T doSwitch(EObject theEObject) {
		return doSwitch(theEObject.eClass(), theEObject);
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T doSwitch(EClass theEClass, EObject theEObject) {
		if (theEClass.eContainer() == modelPackage) {
			return doSwitch(theEClass.getClassifierID(), theEObject);
		}
		else {
			List<EClass> eSuperTypes = theEClass.getESuperTypes();
			return
				eSuperTypes.isEmpty() ?
					defaultCase(theEObject) :
					doSwitch(eSuperTypes.get(0), theEObject);
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case MachinePackage.MACHINE: {
				Machine machine = (Machine)theEObject;
				T result = caseMachine(machine);
				if (result == null) result = caseEventBNamedCommentedComponentElement(machine);
				if (result == null) result = caseEventBNamedCommentedElement(machine);
				if (result == null) result = caseEventBCommentedElement(machine);
				if (result == null) result = caseEventBNamed(machine);
				if (result == null) result = caseEventBElement(machine);
				if (result == null) result = caseEventBCommented(machine);
				if (result == null) result = caseEventBObject(machine);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MachinePackage.VARIABLE: {
				Variable variable = (Variable)theEObject;
				T result = caseVariable(variable);
				if (result == null) result = caseEventBNamedCommentedElement(variable);
				if (result == null) result = caseEventBCommentedElement(variable);
				if (result == null) result = caseEventBNamed(variable);
				if (result == null) result = caseEventBElement(variable);
				if (result == null) result = caseEventBCommented(variable);
				if (result == null) result = caseEventBObject(variable);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MachinePackage.INVARIANT: {
				Invariant invariant = (Invariant)theEObject;
				T result = caseInvariant(invariant);
				if (result == null) result = caseEventBNamedCommentedDerivedPredicateElement(invariant);
				if (result == null) result = caseEventBNamedCommentedPredicateElement(invariant);
				if (result == null) result = caseEventBDerived(invariant);
				if (result == null) result = caseEventBNamedCommentedElement(invariant);
				if (result == null) result = caseEventBPredicate(invariant);
				if (result == null) result = caseEventBCommentedElement(invariant);
				if (result == null) result = caseEventBNamed(invariant);
				if (result == null) result = caseEventBElement(invariant);
				if (result == null) result = caseEventBCommented(invariant);
				if (result == null) result = caseEventBObject(invariant);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MachinePackage.VARIANT: {
				Variant variant = (Variant)theEObject;
				T result = caseVariant(variant);
				if (result == null) result = caseEventBCommentedExpressionElement(variant);
				if (result == null) result = caseEventBCommentedElement(variant);
				if (result == null) result = caseEventBExpression(variant);
				if (result == null) result = caseEventBElement(variant);
				if (result == null) result = caseEventBCommented(variant);
				if (result == null) result = caseEventBObject(variant);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MachinePackage.EVENT: {
				Event event = (Event)theEObject;
				T result = caseEvent(event);
				if (result == null) result = caseEventBNamedCommentedElement(event);
				if (result == null) result = caseEventBCommentedElement(event);
				if (result == null) result = caseEventBNamed(event);
				if (result == null) result = caseEventBElement(event);
				if (result == null) result = caseEventBCommented(event);
				if (result == null) result = caseEventBObject(event);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MachinePackage.PARAMETER: {
				Parameter parameter = (Parameter)theEObject;
				T result = caseParameter(parameter);
				if (result == null) result = caseEventBNamedCommentedElement(parameter);
				if (result == null) result = caseEventBCommentedElement(parameter);
				if (result == null) result = caseEventBNamed(parameter);
				if (result == null) result = caseEventBElement(parameter);
				if (result == null) result = caseEventBCommented(parameter);
				if (result == null) result = caseEventBObject(parameter);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MachinePackage.GUARD: {
				Guard guard = (Guard)theEObject;
				T result = caseGuard(guard);
				if (result == null) result = caseEventBNamedCommentedDerivedPredicateElement(guard);
				if (result == null) result = caseEventBNamedCommentedPredicateElement(guard);
				if (result == null) result = caseEventBDerived(guard);
				if (result == null) result = caseEventBNamedCommentedElement(guard);
				if (result == null) result = caseEventBPredicate(guard);
				if (result == null) result = caseEventBCommentedElement(guard);
				if (result == null) result = caseEventBNamed(guard);
				if (result == null) result = caseEventBElement(guard);
				if (result == null) result = caseEventBCommented(guard);
				if (result == null) result = caseEventBObject(guard);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MachinePackage.WITNESS: {
				Witness witness = (Witness)theEObject;
				T result = caseWitness(witness);
				if (result == null) result = caseEventBNamedCommentedPredicateElement(witness);
				if (result == null) result = caseEventBNamedCommentedElement(witness);
				if (result == null) result = caseEventBPredicate(witness);
				if (result == null) result = caseEventBCommentedElement(witness);
				if (result == null) result = caseEventBNamed(witness);
				if (result == null) result = caseEventBElement(witness);
				if (result == null) result = caseEventBCommented(witness);
				if (result == null) result = caseEventBObject(witness);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MachinePackage.ACTION: {
				Action action = (Action)theEObject;
				T result = caseAction(action);
				if (result == null) result = caseEventBNamedCommentedActionElement(action);
				if (result == null) result = caseEventBNamedCommentedElement(action);
				if (result == null) result = caseEventBAction(action);
				if (result == null) result = caseEventBCommentedElement(action);
				if (result == null) result = caseEventBNamed(action);
				if (result == null) result = caseEventBElement(action);
				if (result == null) result = caseEventBCommented(action);
				if (result == null) result = caseEventBObject(action);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Machine</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Machine</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMachine(Machine object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Variable</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Variable</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseVariable(Variable object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Invariant</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Invariant</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseInvariant(Invariant object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Variant</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Variant</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseVariant(Variant object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Event</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Event</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEvent(Event object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Parameter</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Parameter</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseParameter(Parameter object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Guard</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Guard</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseGuard(Guard object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Witness</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Witness</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseWitness(Witness object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Action</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Action</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAction(Action object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Event BObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Event BObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEventBObject(EventBObject object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Event BElement</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Event BElement</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEventBElement(EventBElement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Event BCommented</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Event BCommented</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEventBCommented(EventBCommented object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Event BCommented Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Event BCommented Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEventBCommentedElement(EventBCommentedElement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Event BNamed</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Event BNamed</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEventBNamed(EventBNamed object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Event BNamed Commented Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Event BNamed Commented Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEventBNamedCommentedElement(EventBNamedCommentedElement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Event BNamed Commented Component Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Event BNamed Commented Component Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEventBNamedCommentedComponentElement(EventBNamedCommentedComponentElement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Event BPredicate</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Event BPredicate</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEventBPredicate(EventBPredicate object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Event BNamed Commented Predicate Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Event BNamed Commented Predicate Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEventBNamedCommentedPredicateElement(EventBNamedCommentedPredicateElement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Event BDerived</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Event BDerived</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEventBDerived(EventBDerived object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Event BNamed Commented Derived Predicate Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Event BNamed Commented Derived Predicate Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEventBNamedCommentedDerivedPredicateElement(EventBNamedCommentedDerivedPredicateElement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Event BExpression</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Event BExpression</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEventBExpression(EventBExpression object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Event BCommented Expression Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Event BCommented Expression Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEventBCommentedExpressionElement(EventBCommentedExpressionElement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Event BAction</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Event BAction</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEventBAction(EventBAction object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Event BNamed Commented Action Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Event BNamed Commented Action Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEventBNamedCommentedActionElement(EventBNamedCommentedActionElement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	public T defaultCase(EObject object) {
		return null;
	}

} //MachineSwitch
