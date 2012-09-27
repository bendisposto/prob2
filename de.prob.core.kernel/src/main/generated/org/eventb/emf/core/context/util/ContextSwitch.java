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
package org.eventb.emf.core.context.util;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eventb.emf.core.EventBCommented;
import org.eventb.emf.core.EventBCommentedElement;
import org.eventb.emf.core.EventBDerived;
import org.eventb.emf.core.EventBElement;
import org.eventb.emf.core.EventBNamed;
import org.eventb.emf.core.EventBNamedCommentedComponentElement;
import org.eventb.emf.core.EventBNamedCommentedDerivedPredicateElement;
import org.eventb.emf.core.EventBNamedCommentedElement;
import org.eventb.emf.core.EventBNamedCommentedPredicateElement;
import org.eventb.emf.core.EventBObject;
import org.eventb.emf.core.EventBPredicate;

import org.eventb.emf.core.context.*;

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
 * @see org.eventb.emf.core.context.ContextPackage
 * @generated
 */
public class ContextSwitch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static ContextPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ContextSwitch() {
		if (modelPackage == null) {
			modelPackage = ContextPackage.eINSTANCE;
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
			case ContextPackage.CONTEXT: {
				Context context = (Context)theEObject;
				T result = caseContext(context);
				if (result == null) result = caseEventBNamedCommentedComponentElement(context);
				if (result == null) result = caseEventBNamedCommentedElement(context);
				if (result == null) result = caseEventBCommentedElement(context);
				if (result == null) result = caseEventBNamed(context);
				if (result == null) result = caseEventBElement(context);
				if (result == null) result = caseEventBCommented(context);
				if (result == null) result = caseEventBObject(context);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ContextPackage.CONSTANT: {
				Constant constant = (Constant)theEObject;
				T result = caseConstant(constant);
				if (result == null) result = caseEventBNamedCommentedElement(constant);
				if (result == null) result = caseEventBCommentedElement(constant);
				if (result == null) result = caseEventBNamed(constant);
				if (result == null) result = caseEventBElement(constant);
				if (result == null) result = caseEventBCommented(constant);
				if (result == null) result = caseEventBObject(constant);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ContextPackage.CARRIER_SET: {
				CarrierSet carrierSet = (CarrierSet)theEObject;
				T result = caseCarrierSet(carrierSet);
				if (result == null) result = caseEventBNamedCommentedElement(carrierSet);
				if (result == null) result = caseEventBCommentedElement(carrierSet);
				if (result == null) result = caseEventBNamed(carrierSet);
				if (result == null) result = caseEventBElement(carrierSet);
				if (result == null) result = caseEventBCommented(carrierSet);
				if (result == null) result = caseEventBObject(carrierSet);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ContextPackage.AXIOM: {
				Axiom axiom = (Axiom)theEObject;
				T result = caseAxiom(axiom);
				if (result == null) result = caseEventBNamedCommentedDerivedPredicateElement(axiom);
				if (result == null) result = caseEventBNamedCommentedPredicateElement(axiom);
				if (result == null) result = caseEventBDerived(axiom);
				if (result == null) result = caseEventBNamedCommentedElement(axiom);
				if (result == null) result = caseEventBPredicate(axiom);
				if (result == null) result = caseEventBCommentedElement(axiom);
				if (result == null) result = caseEventBNamed(axiom);
				if (result == null) result = caseEventBElement(axiom);
				if (result == null) result = caseEventBCommented(axiom);
				if (result == null) result = caseEventBObject(axiom);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Context</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Context</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseContext(Context object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Constant</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Constant</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseConstant(Constant object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Carrier Set</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Carrier Set</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCarrierSet(CarrierSet object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Axiom</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Axiom</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAxiom(Axiom object) {
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

} //ContextSwitch
