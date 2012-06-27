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
package org.eventb.emf.core;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eventb.emf.core.CoreFactory
 * @model kind="package"
 * @generated
 */
public interface CorePackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "core"; //$NON-NLS-1$

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://emf.eventb.org/models/core"; //$NON-NLS-1$

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "core"; //$NON-NLS-1$

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	CorePackage eINSTANCE = org.eventb.emf.core.impl.CorePackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.impl.EventBObjectImpl <em>Event BObject</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.impl.EventBObjectImpl
	 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBObject()
	 * @generated
	 */
	int EVENT_BOBJECT = 0;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BOBJECT__ANNOTATIONS = EcorePackage.EOBJECT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Event BObject</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BOBJECT_FEATURE_COUNT = EcorePackage.EOBJECT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.impl.EventBElementImpl <em>Event BElement</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.impl.EventBElementImpl
	 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBElement()
	 * @generated
	 */
	int EVENT_BELEMENT = 1;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BELEMENT__ANNOTATIONS = EVENT_BOBJECT__ANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BELEMENT__EXTENSIONS = EVENT_BOBJECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BELEMENT__ATTRIBUTES = EVENT_BOBJECT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BELEMENT__REFERENCE = EVENT_BOBJECT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BELEMENT__GENERATED = EVENT_BOBJECT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Local Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BELEMENT__LOCAL_GENERATED = EVENT_BOBJECT_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Event BElement</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BELEMENT_FEATURE_COUNT = EVENT_BOBJECT_FEATURE_COUNT + 5;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.impl.EventBCommentedImpl <em>Event BCommented</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.impl.EventBCommentedImpl
	 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBCommented()
	 * @generated
	 */
	int EVENT_BCOMMENTED = 2;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BCOMMENTED__COMMENT = 0;

	/**
	 * The number of structural features of the '<em>Event BCommented</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BCOMMENTED_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.impl.EventBCommentedElementImpl <em>Event BCommented Element</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.impl.EventBCommentedElementImpl
	 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBCommentedElement()
	 * @generated
	 */
	int EVENT_BCOMMENTED_ELEMENT = 3;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BCOMMENTED_ELEMENT__ANNOTATIONS = EVENT_BELEMENT__ANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BCOMMENTED_ELEMENT__EXTENSIONS = EVENT_BELEMENT__EXTENSIONS;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BCOMMENTED_ELEMENT__ATTRIBUTES = EVENT_BELEMENT__ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BCOMMENTED_ELEMENT__REFERENCE = EVENT_BELEMENT__REFERENCE;

	/**
	 * The feature id for the '<em><b>Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BCOMMENTED_ELEMENT__GENERATED = EVENT_BELEMENT__GENERATED;

	/**
	 * The feature id for the '<em><b>Local Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BCOMMENTED_ELEMENT__LOCAL_GENERATED = EVENT_BELEMENT__LOCAL_GENERATED;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BCOMMENTED_ELEMENT__COMMENT = EVENT_BELEMENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Event BCommented Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BCOMMENTED_ELEMENT_FEATURE_COUNT = EVENT_BELEMENT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.impl.EventBExpressionImpl <em>Event BExpression</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.impl.EventBExpressionImpl
	 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBExpression()
	 * @generated
	 */
	int EVENT_BEXPRESSION = 4;

	/**
	 * The feature id for the '<em><b>Expression</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BEXPRESSION__EXPRESSION = 0;

	/**
	 * The number of structural features of the '<em>Event BExpression</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BEXPRESSION_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.impl.EventBCommentedExpressionElementImpl <em>Event BCommented Expression Element</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.impl.EventBCommentedExpressionElementImpl
	 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBCommentedExpressionElement()
	 * @generated
	 */
	int EVENT_BCOMMENTED_EXPRESSION_ELEMENT = 5;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BCOMMENTED_EXPRESSION_ELEMENT__ANNOTATIONS = EVENT_BCOMMENTED_ELEMENT__ANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BCOMMENTED_EXPRESSION_ELEMENT__EXTENSIONS = EVENT_BCOMMENTED_ELEMENT__EXTENSIONS;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BCOMMENTED_EXPRESSION_ELEMENT__ATTRIBUTES = EVENT_BCOMMENTED_ELEMENT__ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BCOMMENTED_EXPRESSION_ELEMENT__REFERENCE = EVENT_BCOMMENTED_ELEMENT__REFERENCE;

	/**
	 * The feature id for the '<em><b>Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BCOMMENTED_EXPRESSION_ELEMENT__GENERATED = EVENT_BCOMMENTED_ELEMENT__GENERATED;

	/**
	 * The feature id for the '<em><b>Local Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BCOMMENTED_EXPRESSION_ELEMENT__LOCAL_GENERATED = EVENT_BCOMMENTED_ELEMENT__LOCAL_GENERATED;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BCOMMENTED_EXPRESSION_ELEMENT__COMMENT = EVENT_BCOMMENTED_ELEMENT__COMMENT;

	/**
	 * The feature id for the '<em><b>Expression</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BCOMMENTED_EXPRESSION_ELEMENT__EXPRESSION = EVENT_BCOMMENTED_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Event BCommented Expression Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BCOMMENTED_EXPRESSION_ELEMENT_FEATURE_COUNT = EVENT_BCOMMENTED_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.impl.EventBNamedImpl <em>Event BNamed</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.impl.EventBNamedImpl
	 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBNamed()
	 * @generated
	 */
	int EVENT_BNAMED = 6;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED__NAME = 0;

	/**
	 * The number of structural features of the '<em>Event BNamed</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.impl.EventBNamedCommentedElementImpl <em>Event BNamed Commented Element</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.impl.EventBNamedCommentedElementImpl
	 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBNamedCommentedElement()
	 * @generated
	 */
	int EVENT_BNAMED_COMMENTED_ELEMENT = 7;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_ELEMENT__ANNOTATIONS = EVENT_BCOMMENTED_ELEMENT__ANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_ELEMENT__EXTENSIONS = EVENT_BCOMMENTED_ELEMENT__EXTENSIONS;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_ELEMENT__ATTRIBUTES = EVENT_BCOMMENTED_ELEMENT__ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_ELEMENT__REFERENCE = EVENT_BCOMMENTED_ELEMENT__REFERENCE;

	/**
	 * The feature id for the '<em><b>Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_ELEMENT__GENERATED = EVENT_BCOMMENTED_ELEMENT__GENERATED;

	/**
	 * The feature id for the '<em><b>Local Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_ELEMENT__LOCAL_GENERATED = EVENT_BCOMMENTED_ELEMENT__LOCAL_GENERATED;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_ELEMENT__COMMENT = EVENT_BCOMMENTED_ELEMENT__COMMENT;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_ELEMENT__NAME = EVENT_BCOMMENTED_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Event BNamed Commented Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_ELEMENT_FEATURE_COUNT = EVENT_BCOMMENTED_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.impl.EventBPredicateImpl <em>Event BPredicate</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.impl.EventBPredicateImpl
	 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBPredicate()
	 * @generated
	 */
	int EVENT_BPREDICATE = 8;

	/**
	 * The feature id for the '<em><b>Predicate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BPREDICATE__PREDICATE = 0;

	/**
	 * The number of structural features of the '<em>Event BPredicate</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BPREDICATE_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.impl.EventBNamedCommentedPredicateElementImpl <em>Event BNamed Commented Predicate Element</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.impl.EventBNamedCommentedPredicateElementImpl
	 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBNamedCommentedPredicateElement()
	 * @generated
	 */
	int EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT = 9;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__ANNOTATIONS = EVENT_BNAMED_COMMENTED_ELEMENT__ANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__EXTENSIONS = EVENT_BNAMED_COMMENTED_ELEMENT__EXTENSIONS;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__ATTRIBUTES = EVENT_BNAMED_COMMENTED_ELEMENT__ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__REFERENCE = EVENT_BNAMED_COMMENTED_ELEMENT__REFERENCE;

	/**
	 * The feature id for the '<em><b>Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__GENERATED = EVENT_BNAMED_COMMENTED_ELEMENT__GENERATED;

	/**
	 * The feature id for the '<em><b>Local Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__LOCAL_GENERATED = EVENT_BNAMED_COMMENTED_ELEMENT__LOCAL_GENERATED;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__COMMENT = EVENT_BNAMED_COMMENTED_ELEMENT__COMMENT;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__NAME = EVENT_BNAMED_COMMENTED_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Predicate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__PREDICATE = EVENT_BNAMED_COMMENTED_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Event BNamed Commented Predicate Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT_FEATURE_COUNT = EVENT_BNAMED_COMMENTED_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.impl.EventBDerivedImpl <em>Event BDerived</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.impl.EventBDerivedImpl
	 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBDerived()
	 * @generated
	 */
	int EVENT_BDERIVED = 10;

	/**
	 * The feature id for the '<em><b>Theorem</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BDERIVED__THEOREM = 0;

	/**
	 * The number of structural features of the '<em>Event BDerived</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BDERIVED_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.impl.EventBNamedCommentedDerivedPredicateElementImpl <em>Event BNamed Commented Derived Predicate Element</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.impl.EventBNamedCommentedDerivedPredicateElementImpl
	 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBNamedCommentedDerivedPredicateElement()
	 * @generated
	 */
	int EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT = 11;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__ANNOTATIONS = EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__ANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__EXTENSIONS = EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__EXTENSIONS;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__ATTRIBUTES = EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__REFERENCE = EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__REFERENCE;

	/**
	 * The feature id for the '<em><b>Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__GENERATED = EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__GENERATED;

	/**
	 * The feature id for the '<em><b>Local Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__LOCAL_GENERATED = EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__LOCAL_GENERATED;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__COMMENT = EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__COMMENT;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__NAME = EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Predicate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__PREDICATE = EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__PREDICATE;

	/**
	 * The feature id for the '<em><b>Theorem</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__THEOREM = EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Event BNamed Commented Derived Predicate Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT_FEATURE_COUNT = EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.impl.EventBActionImpl <em>Event BAction</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.impl.EventBActionImpl
	 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBAction()
	 * @generated
	 */
	int EVENT_BACTION = 12;

	/**
	 * The feature id for the '<em><b>Action</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BACTION__ACTION = 0;

	/**
	 * The number of structural features of the '<em>Event BAction</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BACTION_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.impl.EventBNamedCommentedActionElementImpl <em>Event BNamed Commented Action Element</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.impl.EventBNamedCommentedActionElementImpl
	 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBNamedCommentedActionElement()
	 * @generated
	 */
	int EVENT_BNAMED_COMMENTED_ACTION_ELEMENT = 13;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_ACTION_ELEMENT__ANNOTATIONS = EVENT_BNAMED_COMMENTED_ELEMENT__ANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_ACTION_ELEMENT__EXTENSIONS = EVENT_BNAMED_COMMENTED_ELEMENT__EXTENSIONS;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_ACTION_ELEMENT__ATTRIBUTES = EVENT_BNAMED_COMMENTED_ELEMENT__ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_ACTION_ELEMENT__REFERENCE = EVENT_BNAMED_COMMENTED_ELEMENT__REFERENCE;

	/**
	 * The feature id for the '<em><b>Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_ACTION_ELEMENT__GENERATED = EVENT_BNAMED_COMMENTED_ELEMENT__GENERATED;

	/**
	 * The feature id for the '<em><b>Local Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_ACTION_ELEMENT__LOCAL_GENERATED = EVENT_BNAMED_COMMENTED_ELEMENT__LOCAL_GENERATED;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_ACTION_ELEMENT__COMMENT = EVENT_BNAMED_COMMENTED_ELEMENT__COMMENT;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_ACTION_ELEMENT__NAME = EVENT_BNAMED_COMMENTED_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Action</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_ACTION_ELEMENT__ACTION = EVENT_BNAMED_COMMENTED_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Event BNamed Commented Action Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_ACTION_ELEMENT_FEATURE_COUNT = EVENT_BNAMED_COMMENTED_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.impl.EventBNamedCommentedComponentElementImpl <em>Event BNamed Commented Component Element</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.impl.EventBNamedCommentedComponentElementImpl
	 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBNamedCommentedComponentElement()
	 * @generated
	 */
	int EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT = 14;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT__ANNOTATIONS = EVENT_BNAMED_COMMENTED_ELEMENT__ANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT__EXTENSIONS = EVENT_BNAMED_COMMENTED_ELEMENT__EXTENSIONS;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT__ATTRIBUTES = EVENT_BNAMED_COMMENTED_ELEMENT__ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT__REFERENCE = EVENT_BNAMED_COMMENTED_ELEMENT__REFERENCE;

	/**
	 * The feature id for the '<em><b>Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT__GENERATED = EVENT_BNAMED_COMMENTED_ELEMENT__GENERATED;

	/**
	 * The feature id for the '<em><b>Local Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT__LOCAL_GENERATED = EVENT_BNAMED_COMMENTED_ELEMENT__LOCAL_GENERATED;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT__COMMENT = EVENT_BNAMED_COMMENTED_ELEMENT__COMMENT;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT__NAME = EVENT_BNAMED_COMMENTED_ELEMENT__NAME;

	/**
	 * The number of structural features of the '<em>Event BNamed Commented Component Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT_FEATURE_COUNT = EVENT_BNAMED_COMMENTED_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.impl.ProjectImpl <em>Project</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.impl.ProjectImpl
	 * @see org.eventb.emf.core.impl.CorePackageImpl#getProject()
	 * @generated
	 */
	int PROJECT = 15;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__ANNOTATIONS = EVENT_BNAMED_COMMENTED_ELEMENT__ANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__EXTENSIONS = EVENT_BNAMED_COMMENTED_ELEMENT__EXTENSIONS;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__ATTRIBUTES = EVENT_BNAMED_COMMENTED_ELEMENT__ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__REFERENCE = EVENT_BNAMED_COMMENTED_ELEMENT__REFERENCE;

	/**
	 * The feature id for the '<em><b>Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__GENERATED = EVENT_BNAMED_COMMENTED_ELEMENT__GENERATED;

	/**
	 * The feature id for the '<em><b>Local Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__LOCAL_GENERATED = EVENT_BNAMED_COMMENTED_ELEMENT__LOCAL_GENERATED;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__COMMENT = EVENT_BNAMED_COMMENTED_ELEMENT__COMMENT;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__NAME = EVENT_BNAMED_COMMENTED_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Components</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__COMPONENTS = EVENT_BNAMED_COMMENTED_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Project</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT_FEATURE_COUNT = EVENT_BNAMED_COMMENTED_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.impl.AbstractExtensionImpl <em>Abstract Extension</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.impl.AbstractExtensionImpl
	 * @see org.eventb.emf.core.impl.CorePackageImpl#getAbstractExtension()
	 * @generated
	 */
	int ABSTRACT_EXTENSION = 19;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_EXTENSION__ANNOTATIONS = EVENT_BELEMENT__ANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_EXTENSION__EXTENSIONS = EVENT_BELEMENT__EXTENSIONS;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_EXTENSION__ATTRIBUTES = EVENT_BELEMENT__ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_EXTENSION__REFERENCE = EVENT_BELEMENT__REFERENCE;

	/**
	 * The feature id for the '<em><b>Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_EXTENSION__GENERATED = EVENT_BELEMENT__GENERATED;

	/**
	 * The feature id for the '<em><b>Local Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_EXTENSION__LOCAL_GENERATED = EVENT_BELEMENT__LOCAL_GENERATED;

	/**
	 * The feature id for the '<em><b>Extension Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_EXTENSION__EXTENSION_ID = EVENT_BELEMENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Abstract Extension</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_EXTENSION_FEATURE_COUNT = EVENT_BELEMENT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.impl.ExtensionImpl <em>Extension</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.impl.ExtensionImpl
	 * @see org.eventb.emf.core.impl.CorePackageImpl#getExtension()
	 * @generated
	 */
	int EXTENSION = 16;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTENSION__ANNOTATIONS = ABSTRACT_EXTENSION__ANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTENSION__EXTENSIONS = ABSTRACT_EXTENSION__EXTENSIONS;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTENSION__ATTRIBUTES = ABSTRACT_EXTENSION__ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTENSION__REFERENCE = ABSTRACT_EXTENSION__REFERENCE;

	/**
	 * The feature id for the '<em><b>Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTENSION__GENERATED = ABSTRACT_EXTENSION__GENERATED;

	/**
	 * The feature id for the '<em><b>Local Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTENSION__LOCAL_GENERATED = ABSTRACT_EXTENSION__LOCAL_GENERATED;

	/**
	 * The feature id for the '<em><b>Extension Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTENSION__EXTENSION_ID = ABSTRACT_EXTENSION__EXTENSION_ID;

	/**
	 * The number of structural features of the '<em>Extension</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTENSION_FEATURE_COUNT = ABSTRACT_EXTENSION_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.impl.StringToAttributeMapEntryImpl <em>String To Attribute Map Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.impl.StringToAttributeMapEntryImpl
	 * @see org.eventb.emf.core.impl.CorePackageImpl#getStringToAttributeMapEntry()
	 * @generated
	 */
	int STRING_TO_ATTRIBUTE_MAP_ENTRY = 17;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_TO_ATTRIBUTE_MAP_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_TO_ATTRIBUTE_MAP_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>String To Attribute Map Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_TO_ATTRIBUTE_MAP_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.impl.AttributeImpl <em>Attribute</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.impl.AttributeImpl
	 * @see org.eventb.emf.core.impl.CorePackageImpl#getAttribute()
	 * @generated
	 */
	int ATTRIBUTE = 18;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE__ANNOTATIONS = EVENT_BOBJECT__ANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE__TYPE = EVENT_BOBJECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE__VALUE = EVENT_BOBJECT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Attribute</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_FEATURE_COUNT = EVENT_BOBJECT_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.impl.StringToStringMapEntryImpl <em>String To String Map Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.impl.StringToStringMapEntryImpl
	 * @see org.eventb.emf.core.impl.CorePackageImpl#getStringToStringMapEntry()
	 * @generated
	 */
	int STRING_TO_STRING_MAP_ENTRY = 20;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_TO_STRING_MAP_ENTRY__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_TO_STRING_MAP_ENTRY__VALUE = 1;

	/**
	 * The number of structural features of the '<em>String To String Map Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_TO_STRING_MAP_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.impl.AnnotationImpl <em>Annotation</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.impl.AnnotationImpl
	 * @see org.eventb.emf.core.impl.CorePackageImpl#getAnnotation()
	 * @generated
	 */
	int ANNOTATION = 21;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANNOTATION__ANNOTATIONS = EVENT_BOBJECT__ANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Source</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANNOTATION__SOURCE = EVENT_BOBJECT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Details</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANNOTATION__DETAILS = EVENT_BOBJECT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Event BObject</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANNOTATION__EVENT_BOBJECT = EVENT_BOBJECT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Contents</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANNOTATION__CONTENTS = EVENT_BOBJECT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>References</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANNOTATION__REFERENCES = EVENT_BOBJECT_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Annotation</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ANNOTATION_FEATURE_COUNT = EVENT_BOBJECT_FEATURE_COUNT + 5;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.AttributeType <em>Attribute Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.AttributeType
	 * @see org.eventb.emf.core.impl.CorePackageImpl#getAttributeType()
	 * @generated
	 */
	int ATTRIBUTE_TYPE = 22;


	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.EventBObject <em>Event BObject</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Event BObject</em>'.
	 * @see org.eventb.emf.core.EventBObject
	 * @generated
	 */
	EClass getEventBObject();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eventb.emf.core.EventBObject#getAnnotations <em>Annotations</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Annotations</em>'.
	 * @see org.eventb.emf.core.EventBObject#getAnnotations()
	 * @see #getEventBObject()
	 * @generated
	 */
	EReference getEventBObject_Annotations();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.EventBElement <em>Event BElement</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Event BElement</em>'.
	 * @see org.eventb.emf.core.EventBElement
	 * @generated
	 */
	EClass getEventBElement();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eventb.emf.core.EventBElement#getExtensions <em>Extensions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Extensions</em>'.
	 * @see org.eventb.emf.core.EventBElement#getExtensions()
	 * @see #getEventBElement()
	 * @generated
	 */
	EReference getEventBElement_Extensions();

	/**
	 * Returns the meta object for the map '{@link org.eventb.emf.core.EventBElement#getAttributes <em>Attributes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Attributes</em>'.
	 * @see org.eventb.emf.core.EventBElement#getAttributes()
	 * @see #getEventBElement()
	 * @generated
	 */
	EReference getEventBElement_Attributes();

	/**
	 * Returns the meta object for the attribute '{@link org.eventb.emf.core.EventBElement#getReference <em>Reference</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Reference</em>'.
	 * @see org.eventb.emf.core.EventBElement#getReference()
	 * @see #getEventBElement()
	 * @generated
	 */
	EAttribute getEventBElement_Reference();

	/**
	 * Returns the meta object for the attribute '{@link org.eventb.emf.core.EventBElement#isGenerated <em>Generated</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Generated</em>'.
	 * @see org.eventb.emf.core.EventBElement#isGenerated()
	 * @see #getEventBElement()
	 * @generated
	 */
	EAttribute getEventBElement_Generated();

	/**
	 * Returns the meta object for the attribute '{@link org.eventb.emf.core.EventBElement#isLocalGenerated <em>Local Generated</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Local Generated</em>'.
	 * @see org.eventb.emf.core.EventBElement#isLocalGenerated()
	 * @see #getEventBElement()
	 * @generated
	 */
	EAttribute getEventBElement_LocalGenerated();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.EventBCommented <em>Event BCommented</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Event BCommented</em>'.
	 * @see org.eventb.emf.core.EventBCommented
	 * @generated
	 */
	EClass getEventBCommented();

	/**
	 * Returns the meta object for the attribute '{@link org.eventb.emf.core.EventBCommented#getComment <em>Comment</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Comment</em>'.
	 * @see org.eventb.emf.core.EventBCommented#getComment()
	 * @see #getEventBCommented()
	 * @generated
	 */
	EAttribute getEventBCommented_Comment();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.EventBCommentedElement <em>Event BCommented Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Event BCommented Element</em>'.
	 * @see org.eventb.emf.core.EventBCommentedElement
	 * @generated
	 */
	EClass getEventBCommentedElement();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.EventBExpression <em>Event BExpression</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Event BExpression</em>'.
	 * @see org.eventb.emf.core.EventBExpression
	 * @generated
	 */
	EClass getEventBExpression();

	/**
	 * Returns the meta object for the attribute '{@link org.eventb.emf.core.EventBExpression#getExpression <em>Expression</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Expression</em>'.
	 * @see org.eventb.emf.core.EventBExpression#getExpression()
	 * @see #getEventBExpression()
	 * @generated
	 */
	EAttribute getEventBExpression_Expression();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.EventBCommentedExpressionElement <em>Event BCommented Expression Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Event BCommented Expression Element</em>'.
	 * @see org.eventb.emf.core.EventBCommentedExpressionElement
	 * @generated
	 */
	EClass getEventBCommentedExpressionElement();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.EventBNamed <em>Event BNamed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Event BNamed</em>'.
	 * @see org.eventb.emf.core.EventBNamed
	 * @generated
	 */
	EClass getEventBNamed();

	/**
	 * Returns the meta object for the attribute '{@link org.eventb.emf.core.EventBNamed#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eventb.emf.core.EventBNamed#getName()
	 * @see #getEventBNamed()
	 * @generated
	 */
	EAttribute getEventBNamed_Name();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.EventBNamedCommentedElement <em>Event BNamed Commented Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Event BNamed Commented Element</em>'.
	 * @see org.eventb.emf.core.EventBNamedCommentedElement
	 * @generated
	 */
	EClass getEventBNamedCommentedElement();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.EventBPredicate <em>Event BPredicate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Event BPredicate</em>'.
	 * @see org.eventb.emf.core.EventBPredicate
	 * @generated
	 */
	EClass getEventBPredicate();

	/**
	 * Returns the meta object for the attribute '{@link org.eventb.emf.core.EventBPredicate#getPredicate <em>Predicate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Predicate</em>'.
	 * @see org.eventb.emf.core.EventBPredicate#getPredicate()
	 * @see #getEventBPredicate()
	 * @generated
	 */
	EAttribute getEventBPredicate_Predicate();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.EventBNamedCommentedPredicateElement <em>Event BNamed Commented Predicate Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Event BNamed Commented Predicate Element</em>'.
	 * @see org.eventb.emf.core.EventBNamedCommentedPredicateElement
	 * @generated
	 */
	EClass getEventBNamedCommentedPredicateElement();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.EventBDerived <em>Event BDerived</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Event BDerived</em>'.
	 * @see org.eventb.emf.core.EventBDerived
	 * @generated
	 */
	EClass getEventBDerived();

	/**
	 * Returns the meta object for the attribute '{@link org.eventb.emf.core.EventBDerived#isTheorem <em>Theorem</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Theorem</em>'.
	 * @see org.eventb.emf.core.EventBDerived#isTheorem()
	 * @see #getEventBDerived()
	 * @generated
	 */
	EAttribute getEventBDerived_Theorem();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.EventBNamedCommentedDerivedPredicateElement <em>Event BNamed Commented Derived Predicate Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Event BNamed Commented Derived Predicate Element</em>'.
	 * @see org.eventb.emf.core.EventBNamedCommentedDerivedPredicateElement
	 * @generated
	 */
	EClass getEventBNamedCommentedDerivedPredicateElement();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.EventBAction <em>Event BAction</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Event BAction</em>'.
	 * @see org.eventb.emf.core.EventBAction
	 * @generated
	 */
	EClass getEventBAction();

	/**
	 * Returns the meta object for the attribute '{@link org.eventb.emf.core.EventBAction#getAction <em>Action</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Action</em>'.
	 * @see org.eventb.emf.core.EventBAction#getAction()
	 * @see #getEventBAction()
	 * @generated
	 */
	EAttribute getEventBAction_Action();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.EventBNamedCommentedActionElement <em>Event BNamed Commented Action Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Event BNamed Commented Action Element</em>'.
	 * @see org.eventb.emf.core.EventBNamedCommentedActionElement
	 * @generated
	 */
	EClass getEventBNamedCommentedActionElement();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.EventBNamedCommentedComponentElement <em>Event BNamed Commented Component Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Event BNamed Commented Component Element</em>'.
	 * @see org.eventb.emf.core.EventBNamedCommentedComponentElement
	 * @generated
	 */
	EClass getEventBNamedCommentedComponentElement();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.Project <em>Project</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Project</em>'.
	 * @see org.eventb.emf.core.Project
	 * @generated
	 */
	EClass getProject();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eventb.emf.core.Project#getComponents <em>Components</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Components</em>'.
	 * @see org.eventb.emf.core.Project#getComponents()
	 * @see #getProject()
	 * @generated
	 */
	EReference getProject_Components();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.Extension <em>Extension</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Extension</em>'.
	 * @see org.eventb.emf.core.Extension
	 * @generated
	 */
	EClass getExtension();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>String To Attribute Map Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>String To Attribute Map Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString" keyRequired="true"
	 *        valueType="org.eventb.emf.core.Attribute" valueContainment="true" valueResolveProxies="true"
	 * @generated
	 */
	EClass getStringToAttributeMapEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getStringToAttributeMapEntry()
	 * @generated
	 */
	EAttribute getStringToAttributeMapEntry_Key();

	/**
	 * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getStringToAttributeMapEntry()
	 * @generated
	 */
	EReference getStringToAttributeMapEntry_Value();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.Attribute <em>Attribute</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Attribute</em>'.
	 * @see org.eventb.emf.core.Attribute
	 * @generated
	 */
	EClass getAttribute();

	/**
	 * Returns the meta object for the attribute '{@link org.eventb.emf.core.Attribute#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see org.eventb.emf.core.Attribute#getType()
	 * @see #getAttribute()
	 * @generated
	 */
	EAttribute getAttribute_Type();

	/**
	 * Returns the meta object for the attribute '{@link org.eventb.emf.core.Attribute#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eventb.emf.core.Attribute#getValue()
	 * @see #getAttribute()
	 * @generated
	 */
	EAttribute getAttribute_Value();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.AbstractExtension <em>Abstract Extension</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Abstract Extension</em>'.
	 * @see org.eventb.emf.core.AbstractExtension
	 * @generated
	 */
	EClass getAbstractExtension();

	/**
	 * Returns the meta object for the attribute '{@link org.eventb.emf.core.AbstractExtension#getExtensionId <em>Extension Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Extension Id</em>'.
	 * @see org.eventb.emf.core.AbstractExtension#getExtensionId()
	 * @see #getAbstractExtension()
	 * @generated
	 */
	EAttribute getAbstractExtension_ExtensionId();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>String To String Map Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>String To String Map Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueDataType="org.eclipse.emf.ecore.EString"
	 * @generated
	 */
	EClass getStringToStringMapEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getStringToStringMapEntry()
	 * @generated
	 */
	EAttribute getStringToStringMapEntry_Key();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getStringToStringMapEntry()
	 * @generated
	 */
	EAttribute getStringToStringMapEntry_Value();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.Annotation <em>Annotation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Annotation</em>'.
	 * @see org.eventb.emf.core.Annotation
	 * @generated
	 */
	EClass getAnnotation();

	/**
	 * Returns the meta object for the attribute '{@link org.eventb.emf.core.Annotation#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Source</em>'.
	 * @see org.eventb.emf.core.Annotation#getSource()
	 * @see #getAnnotation()
	 * @generated
	 */
	EAttribute getAnnotation_Source();

	/**
	 * Returns the meta object for the map '{@link org.eventb.emf.core.Annotation#getDetails <em>Details</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Details</em>'.
	 * @see org.eventb.emf.core.Annotation#getDetails()
	 * @see #getAnnotation()
	 * @generated
	 */
	EReference getAnnotation_Details();

	/**
	 * Returns the meta object for the container reference '{@link org.eventb.emf.core.Annotation#getEventBObject <em>Event BObject</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Event BObject</em>'.
	 * @see org.eventb.emf.core.Annotation#getEventBObject()
	 * @see #getAnnotation()
	 * @generated
	 */
	EReference getAnnotation_EventBObject();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eventb.emf.core.Annotation#getContents <em>Contents</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Contents</em>'.
	 * @see org.eventb.emf.core.Annotation#getContents()
	 * @see #getAnnotation()
	 * @generated
	 */
	EReference getAnnotation_Contents();

	/**
	 * Returns the meta object for the reference list '{@link org.eventb.emf.core.Annotation#getReferences <em>References</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>References</em>'.
	 * @see org.eventb.emf.core.Annotation#getReferences()
	 * @see #getAnnotation()
	 * @generated
	 */
	EReference getAnnotation_References();

	/**
	 * Returns the meta object for enum '{@link org.eventb.emf.core.AttributeType <em>Attribute Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Attribute Type</em>'.
	 * @see org.eventb.emf.core.AttributeType
	 * @generated
	 */
	EEnum getAttributeType();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	CoreFactory getCoreFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.impl.EventBObjectImpl <em>Event BObject</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.impl.EventBObjectImpl
		 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBObject()
		 * @generated
		 */
		EClass EVENT_BOBJECT = eINSTANCE.getEventBObject();

		/**
		 * The meta object literal for the '<em><b>Annotations</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EVENT_BOBJECT__ANNOTATIONS = eINSTANCE.getEventBObject_Annotations();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.impl.EventBElementImpl <em>Event BElement</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.impl.EventBElementImpl
		 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBElement()
		 * @generated
		 */
		EClass EVENT_BELEMENT = eINSTANCE.getEventBElement();

		/**
		 * The meta object literal for the '<em><b>Extensions</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EVENT_BELEMENT__EXTENSIONS = eINSTANCE.getEventBElement_Extensions();

		/**
		 * The meta object literal for the '<em><b>Attributes</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EVENT_BELEMENT__ATTRIBUTES = eINSTANCE.getEventBElement_Attributes();

		/**
		 * The meta object literal for the '<em><b>Reference</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EVENT_BELEMENT__REFERENCE = eINSTANCE.getEventBElement_Reference();

		/**
		 * The meta object literal for the '<em><b>Generated</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EVENT_BELEMENT__GENERATED = eINSTANCE.getEventBElement_Generated();

		/**
		 * The meta object literal for the '<em><b>Local Generated</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EVENT_BELEMENT__LOCAL_GENERATED = eINSTANCE.getEventBElement_LocalGenerated();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.impl.EventBCommentedImpl <em>Event BCommented</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.impl.EventBCommentedImpl
		 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBCommented()
		 * @generated
		 */
		EClass EVENT_BCOMMENTED = eINSTANCE.getEventBCommented();

		/**
		 * The meta object literal for the '<em><b>Comment</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EVENT_BCOMMENTED__COMMENT = eINSTANCE.getEventBCommented_Comment();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.impl.EventBCommentedElementImpl <em>Event BCommented Element</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.impl.EventBCommentedElementImpl
		 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBCommentedElement()
		 * @generated
		 */
		EClass EVENT_BCOMMENTED_ELEMENT = eINSTANCE.getEventBCommentedElement();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.impl.EventBExpressionImpl <em>Event BExpression</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.impl.EventBExpressionImpl
		 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBExpression()
		 * @generated
		 */
		EClass EVENT_BEXPRESSION = eINSTANCE.getEventBExpression();

		/**
		 * The meta object literal for the '<em><b>Expression</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EVENT_BEXPRESSION__EXPRESSION = eINSTANCE.getEventBExpression_Expression();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.impl.EventBCommentedExpressionElementImpl <em>Event BCommented Expression Element</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.impl.EventBCommentedExpressionElementImpl
		 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBCommentedExpressionElement()
		 * @generated
		 */
		EClass EVENT_BCOMMENTED_EXPRESSION_ELEMENT = eINSTANCE.getEventBCommentedExpressionElement();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.impl.EventBNamedImpl <em>Event BNamed</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.impl.EventBNamedImpl
		 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBNamed()
		 * @generated
		 */
		EClass EVENT_BNAMED = eINSTANCE.getEventBNamed();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EVENT_BNAMED__NAME = eINSTANCE.getEventBNamed_Name();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.impl.EventBNamedCommentedElementImpl <em>Event BNamed Commented Element</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.impl.EventBNamedCommentedElementImpl
		 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBNamedCommentedElement()
		 * @generated
		 */
		EClass EVENT_BNAMED_COMMENTED_ELEMENT = eINSTANCE.getEventBNamedCommentedElement();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.impl.EventBPredicateImpl <em>Event BPredicate</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.impl.EventBPredicateImpl
		 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBPredicate()
		 * @generated
		 */
		EClass EVENT_BPREDICATE = eINSTANCE.getEventBPredicate();

		/**
		 * The meta object literal for the '<em><b>Predicate</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EVENT_BPREDICATE__PREDICATE = eINSTANCE.getEventBPredicate_Predicate();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.impl.EventBNamedCommentedPredicateElementImpl <em>Event BNamed Commented Predicate Element</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.impl.EventBNamedCommentedPredicateElementImpl
		 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBNamedCommentedPredicateElement()
		 * @generated
		 */
		EClass EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT = eINSTANCE.getEventBNamedCommentedPredicateElement();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.impl.EventBDerivedImpl <em>Event BDerived</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.impl.EventBDerivedImpl
		 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBDerived()
		 * @generated
		 */
		EClass EVENT_BDERIVED = eINSTANCE.getEventBDerived();

		/**
		 * The meta object literal for the '<em><b>Theorem</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EVENT_BDERIVED__THEOREM = eINSTANCE.getEventBDerived_Theorem();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.impl.EventBNamedCommentedDerivedPredicateElementImpl <em>Event BNamed Commented Derived Predicate Element</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.impl.EventBNamedCommentedDerivedPredicateElementImpl
		 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBNamedCommentedDerivedPredicateElement()
		 * @generated
		 */
		EClass EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT = eINSTANCE.getEventBNamedCommentedDerivedPredicateElement();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.impl.EventBActionImpl <em>Event BAction</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.impl.EventBActionImpl
		 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBAction()
		 * @generated
		 */
		EClass EVENT_BACTION = eINSTANCE.getEventBAction();

		/**
		 * The meta object literal for the '<em><b>Action</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EVENT_BACTION__ACTION = eINSTANCE.getEventBAction_Action();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.impl.EventBNamedCommentedActionElementImpl <em>Event BNamed Commented Action Element</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.impl.EventBNamedCommentedActionElementImpl
		 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBNamedCommentedActionElement()
		 * @generated
		 */
		EClass EVENT_BNAMED_COMMENTED_ACTION_ELEMENT = eINSTANCE.getEventBNamedCommentedActionElement();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.impl.EventBNamedCommentedComponentElementImpl <em>Event BNamed Commented Component Element</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.impl.EventBNamedCommentedComponentElementImpl
		 * @see org.eventb.emf.core.impl.CorePackageImpl#getEventBNamedCommentedComponentElement()
		 * @generated
		 */
		EClass EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT = eINSTANCE.getEventBNamedCommentedComponentElement();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.impl.ProjectImpl <em>Project</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.impl.ProjectImpl
		 * @see org.eventb.emf.core.impl.CorePackageImpl#getProject()
		 * @generated
		 */
		EClass PROJECT = eINSTANCE.getProject();

		/**
		 * The meta object literal for the '<em><b>Components</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROJECT__COMPONENTS = eINSTANCE.getProject_Components();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.impl.ExtensionImpl <em>Extension</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.impl.ExtensionImpl
		 * @see org.eventb.emf.core.impl.CorePackageImpl#getExtension()
		 * @generated
		 */
		EClass EXTENSION = eINSTANCE.getExtension();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.impl.StringToAttributeMapEntryImpl <em>String To Attribute Map Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.impl.StringToAttributeMapEntryImpl
		 * @see org.eventb.emf.core.impl.CorePackageImpl#getStringToAttributeMapEntry()
		 * @generated
		 */
		EClass STRING_TO_ATTRIBUTE_MAP_ENTRY = eINSTANCE.getStringToAttributeMapEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_TO_ATTRIBUTE_MAP_ENTRY__KEY = eINSTANCE.getStringToAttributeMapEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference STRING_TO_ATTRIBUTE_MAP_ENTRY__VALUE = eINSTANCE.getStringToAttributeMapEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.impl.AttributeImpl <em>Attribute</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.impl.AttributeImpl
		 * @see org.eventb.emf.core.impl.CorePackageImpl#getAttribute()
		 * @generated
		 */
		EClass ATTRIBUTE = eINSTANCE.getAttribute();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTRIBUTE__TYPE = eINSTANCE.getAttribute_Type();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTRIBUTE__VALUE = eINSTANCE.getAttribute_Value();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.impl.AbstractExtensionImpl <em>Abstract Extension</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.impl.AbstractExtensionImpl
		 * @see org.eventb.emf.core.impl.CorePackageImpl#getAbstractExtension()
		 * @generated
		 */
		EClass ABSTRACT_EXTENSION = eINSTANCE.getAbstractExtension();

		/**
		 * The meta object literal for the '<em><b>Extension Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ABSTRACT_EXTENSION__EXTENSION_ID = eINSTANCE.getAbstractExtension_ExtensionId();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.impl.StringToStringMapEntryImpl <em>String To String Map Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.impl.StringToStringMapEntryImpl
		 * @see org.eventb.emf.core.impl.CorePackageImpl#getStringToStringMapEntry()
		 * @generated
		 */
		EClass STRING_TO_STRING_MAP_ENTRY = eINSTANCE.getStringToStringMapEntry();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_TO_STRING_MAP_ENTRY__KEY = eINSTANCE.getStringToStringMapEntry_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_TO_STRING_MAP_ENTRY__VALUE = eINSTANCE.getStringToStringMapEntry_Value();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.impl.AnnotationImpl <em>Annotation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.impl.AnnotationImpl
		 * @see org.eventb.emf.core.impl.CorePackageImpl#getAnnotation()
		 * @generated
		 */
		EClass ANNOTATION = eINSTANCE.getAnnotation();

		/**
		 * The meta object literal for the '<em><b>Source</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ANNOTATION__SOURCE = eINSTANCE.getAnnotation_Source();

		/**
		 * The meta object literal for the '<em><b>Details</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ANNOTATION__DETAILS = eINSTANCE.getAnnotation_Details();

		/**
		 * The meta object literal for the '<em><b>Event BObject</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ANNOTATION__EVENT_BOBJECT = eINSTANCE.getAnnotation_EventBObject();

		/**
		 * The meta object literal for the '<em><b>Contents</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ANNOTATION__CONTENTS = eINSTANCE.getAnnotation_Contents();

		/**
		 * The meta object literal for the '<em><b>References</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ANNOTATION__REFERENCES = eINSTANCE.getAnnotation_References();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.AttributeType <em>Attribute Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.AttributeType
		 * @see org.eventb.emf.core.impl.CorePackageImpl#getAttributeType()
		 * @generated
		 */
		EEnum ATTRIBUTE_TYPE = eINSTANCE.getAttributeType();

	}

} //CorePackage
