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
package org.eventb.emf.core.util;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eventb.emf.core.*;

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
 * @see org.eventb.emf.core.CorePackage
 * @generated
 */
public class CoreSwitch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static CorePackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CoreSwitch() {
		if (modelPackage == null) {
			modelPackage = CorePackage.eINSTANCE;
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
			case CorePackage.EVENT_BOBJECT: {
				EventBObject eventBObject = (EventBObject)theEObject;
				T result = caseEventBObject(eventBObject);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CorePackage.EVENT_BELEMENT: {
				EventBElement eventBElement = (EventBElement)theEObject;
				T result = caseEventBElement(eventBElement);
				if (result == null) result = caseEventBObject(eventBElement);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CorePackage.EVENT_BCOMMENTED: {
				EventBCommented eventBCommented = (EventBCommented)theEObject;
				T result = caseEventBCommented(eventBCommented);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CorePackage.EVENT_BCOMMENTED_ELEMENT: {
				EventBCommentedElement eventBCommentedElement = (EventBCommentedElement)theEObject;
				T result = caseEventBCommentedElement(eventBCommentedElement);
				if (result == null) result = caseEventBElement(eventBCommentedElement);
				if (result == null) result = caseEventBCommented(eventBCommentedElement);
				if (result == null) result = caseEventBObject(eventBCommentedElement);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CorePackage.EVENT_BEXPRESSION: {
				EventBExpression eventBExpression = (EventBExpression)theEObject;
				T result = caseEventBExpression(eventBExpression);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CorePackage.EVENT_BCOMMENTED_EXPRESSION_ELEMENT: {
				EventBCommentedExpressionElement eventBCommentedExpressionElement = (EventBCommentedExpressionElement)theEObject;
				T result = caseEventBCommentedExpressionElement(eventBCommentedExpressionElement);
				if (result == null) result = caseEventBCommentedElement(eventBCommentedExpressionElement);
				if (result == null) result = caseEventBExpression(eventBCommentedExpressionElement);
				if (result == null) result = caseEventBElement(eventBCommentedExpressionElement);
				if (result == null) result = caseEventBCommented(eventBCommentedExpressionElement);
				if (result == null) result = caseEventBObject(eventBCommentedExpressionElement);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CorePackage.EVENT_BNAMED: {
				EventBNamed eventBNamed = (EventBNamed)theEObject;
				T result = caseEventBNamed(eventBNamed);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT: {
				EventBNamedCommentedElement eventBNamedCommentedElement = (EventBNamedCommentedElement)theEObject;
				T result = caseEventBNamedCommentedElement(eventBNamedCommentedElement);
				if (result == null) result = caseEventBCommentedElement(eventBNamedCommentedElement);
				if (result == null) result = caseEventBNamed(eventBNamedCommentedElement);
				if (result == null) result = caseEventBElement(eventBNamedCommentedElement);
				if (result == null) result = caseEventBCommented(eventBNamedCommentedElement);
				if (result == null) result = caseEventBObject(eventBNamedCommentedElement);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CorePackage.EVENT_BPREDICATE: {
				EventBPredicate eventBPredicate = (EventBPredicate)theEObject;
				T result = caseEventBPredicate(eventBPredicate);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CorePackage.EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT: {
				EventBNamedCommentedPredicateElement eventBNamedCommentedPredicateElement = (EventBNamedCommentedPredicateElement)theEObject;
				T result = caseEventBNamedCommentedPredicateElement(eventBNamedCommentedPredicateElement);
				if (result == null) result = caseEventBNamedCommentedElement(eventBNamedCommentedPredicateElement);
				if (result == null) result = caseEventBPredicate(eventBNamedCommentedPredicateElement);
				if (result == null) result = caseEventBCommentedElement(eventBNamedCommentedPredicateElement);
				if (result == null) result = caseEventBNamed(eventBNamedCommentedPredicateElement);
				if (result == null) result = caseEventBElement(eventBNamedCommentedPredicateElement);
				if (result == null) result = caseEventBCommented(eventBNamedCommentedPredicateElement);
				if (result == null) result = caseEventBObject(eventBNamedCommentedPredicateElement);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CorePackage.EVENT_BDERIVED: {
				EventBDerived eventBDerived = (EventBDerived)theEObject;
				T result = caseEventBDerived(eventBDerived);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT: {
				EventBNamedCommentedDerivedPredicateElement eventBNamedCommentedDerivedPredicateElement = (EventBNamedCommentedDerivedPredicateElement)theEObject;
				T result = caseEventBNamedCommentedDerivedPredicateElement(eventBNamedCommentedDerivedPredicateElement);
				if (result == null) result = caseEventBNamedCommentedPredicateElement(eventBNamedCommentedDerivedPredicateElement);
				if (result == null) result = caseEventBDerived(eventBNamedCommentedDerivedPredicateElement);
				if (result == null) result = caseEventBNamedCommentedElement(eventBNamedCommentedDerivedPredicateElement);
				if (result == null) result = caseEventBPredicate(eventBNamedCommentedDerivedPredicateElement);
				if (result == null) result = caseEventBCommentedElement(eventBNamedCommentedDerivedPredicateElement);
				if (result == null) result = caseEventBNamed(eventBNamedCommentedDerivedPredicateElement);
				if (result == null) result = caseEventBElement(eventBNamedCommentedDerivedPredicateElement);
				if (result == null) result = caseEventBCommented(eventBNamedCommentedDerivedPredicateElement);
				if (result == null) result = caseEventBObject(eventBNamedCommentedDerivedPredicateElement);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CorePackage.EVENT_BACTION: {
				EventBAction eventBAction = (EventBAction)theEObject;
				T result = caseEventBAction(eventBAction);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CorePackage.EVENT_BNAMED_COMMENTED_ACTION_ELEMENT: {
				EventBNamedCommentedActionElement eventBNamedCommentedActionElement = (EventBNamedCommentedActionElement)theEObject;
				T result = caseEventBNamedCommentedActionElement(eventBNamedCommentedActionElement);
				if (result == null) result = caseEventBNamedCommentedElement(eventBNamedCommentedActionElement);
				if (result == null) result = caseEventBAction(eventBNamedCommentedActionElement);
				if (result == null) result = caseEventBCommentedElement(eventBNamedCommentedActionElement);
				if (result == null) result = caseEventBNamed(eventBNamedCommentedActionElement);
				if (result == null) result = caseEventBElement(eventBNamedCommentedActionElement);
				if (result == null) result = caseEventBCommented(eventBNamedCommentedActionElement);
				if (result == null) result = caseEventBObject(eventBNamedCommentedActionElement);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CorePackage.EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT: {
				EventBNamedCommentedComponentElement eventBNamedCommentedComponentElement = (EventBNamedCommentedComponentElement)theEObject;
				T result = caseEventBNamedCommentedComponentElement(eventBNamedCommentedComponentElement);
				if (result == null) result = caseEventBNamedCommentedElement(eventBNamedCommentedComponentElement);
				if (result == null) result = caseEventBCommentedElement(eventBNamedCommentedComponentElement);
				if (result == null) result = caseEventBNamed(eventBNamedCommentedComponentElement);
				if (result == null) result = caseEventBElement(eventBNamedCommentedComponentElement);
				if (result == null) result = caseEventBCommented(eventBNamedCommentedComponentElement);
				if (result == null) result = caseEventBObject(eventBNamedCommentedComponentElement);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CorePackage.PROJECT: {
				Project project = (Project)theEObject;
				T result = caseProject(project);
				if (result == null) result = caseEventBNamedCommentedElement(project);
				if (result == null) result = caseEventBCommentedElement(project);
				if (result == null) result = caseEventBNamed(project);
				if (result == null) result = caseEventBElement(project);
				if (result == null) result = caseEventBCommented(project);
				if (result == null) result = caseEventBObject(project);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CorePackage.EXTENSION: {
				Extension extension = (Extension)theEObject;
				T result = caseExtension(extension);
				if (result == null) result = caseAbstractExtension(extension);
				if (result == null) result = caseEventBElement(extension);
				if (result == null) result = caseEventBObject(extension);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CorePackage.STRING_TO_ATTRIBUTE_MAP_ENTRY: {
				@SuppressWarnings("unchecked") Map.Entry<String, Attribute> stringToAttributeMapEntry = (Map.Entry<String, Attribute>)theEObject;
				T result = caseStringToAttributeMapEntry(stringToAttributeMapEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CorePackage.ATTRIBUTE: {
				Attribute attribute = (Attribute)theEObject;
				T result = caseAttribute(attribute);
				if (result == null) result = caseEventBObject(attribute);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CorePackage.ABSTRACT_EXTENSION: {
				AbstractExtension abstractExtension = (AbstractExtension)theEObject;
				T result = caseAbstractExtension(abstractExtension);
				if (result == null) result = caseEventBElement(abstractExtension);
				if (result == null) result = caseEventBObject(abstractExtension);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CorePackage.STRING_TO_STRING_MAP_ENTRY: {
				@SuppressWarnings("unchecked") Map.Entry<String, String> stringToStringMapEntry = (Map.Entry<String, String>)theEObject;
				T result = caseStringToStringMapEntry(stringToStringMapEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case CorePackage.ANNOTATION: {
				Annotation annotation = (Annotation)theEObject;
				T result = caseAnnotation(annotation);
				if (result == null) result = caseEventBObject(annotation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
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
	 * Returns the result of interpreting the object as an instance of '<em>Project</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Project</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseProject(Project object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Extension</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Extension</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseExtension(Extension object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>String To Attribute Map Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>String To Attribute Map Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseStringToAttributeMapEntry(Map.Entry<String, Attribute> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Attribute</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Attribute</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAttribute(Attribute object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Abstract Extension</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Abstract Extension</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAbstractExtension(AbstractExtension object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>String To String Map Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>String To String Map Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseStringToStringMapEntry(Map.Entry<String, String> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Annotation</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Annotation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAnnotation(Annotation object) {
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

} //CoreSwitch
