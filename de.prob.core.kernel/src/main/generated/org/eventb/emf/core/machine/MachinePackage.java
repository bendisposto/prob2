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
package org.eventb.emf.core.machine;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eventb.emf.core.CorePackage;

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
 * @see org.eventb.emf.core.machine.MachineFactory
 * @model kind="package"
 * @generated
 */
public interface MachinePackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "machine"; //$NON-NLS-1$

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://emf.eventb.org/models/core/machine"; //$NON-NLS-1$

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "machine"; //$NON-NLS-1$

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	MachinePackage eINSTANCE = org.eventb.emf.core.machine.impl.MachinePackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.machine.impl.MachineImpl <em>Machine</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.machine.impl.MachineImpl
	 * @see org.eventb.emf.core.machine.impl.MachinePackageImpl#getMachine()
	 * @generated
	 */
	int MACHINE = 0;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MACHINE__ANNOTATIONS = CorePackage.EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT__ANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MACHINE__EXTENSIONS = CorePackage.EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT__EXTENSIONS;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MACHINE__ATTRIBUTES = CorePackage.EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT__ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MACHINE__REFERENCE = CorePackage.EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT__REFERENCE;

	/**
	 * The feature id for the '<em><b>Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MACHINE__GENERATED = CorePackage.EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT__GENERATED;

	/**
	 * The feature id for the '<em><b>Local Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MACHINE__LOCAL_GENERATED = CorePackage.EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT__LOCAL_GENERATED;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MACHINE__COMMENT = CorePackage.EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT__COMMENT;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MACHINE__NAME = CorePackage.EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Refines</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MACHINE__REFINES = CorePackage.EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Refines Names</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MACHINE__REFINES_NAMES = CorePackage.EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Sees</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MACHINE__SEES = CorePackage.EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Sees Names</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MACHINE__SEES_NAMES = CorePackage.EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Variables</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MACHINE__VARIABLES = CorePackage.EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Invariants</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MACHINE__INVARIANTS = CorePackage.EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Variant</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MACHINE__VARIANT = CorePackage.EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Events</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MACHINE__EVENTS = CorePackage.EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT_FEATURE_COUNT + 7;

	/**
	 * The number of structural features of the '<em>Machine</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MACHINE_FEATURE_COUNT = CorePackage.EVENT_BNAMED_COMMENTED_COMPONENT_ELEMENT_FEATURE_COUNT + 8;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.machine.impl.VariableImpl <em>Variable</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.machine.impl.VariableImpl
	 * @see org.eventb.emf.core.machine.impl.MachinePackageImpl#getVariable()
	 * @generated
	 */
	int VARIABLE = 1;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIABLE__ANNOTATIONS = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT__ANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIABLE__EXTENSIONS = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT__EXTENSIONS;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIABLE__ATTRIBUTES = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT__ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIABLE__REFERENCE = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT__REFERENCE;

	/**
	 * The feature id for the '<em><b>Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIABLE__GENERATED = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT__GENERATED;

	/**
	 * The feature id for the '<em><b>Local Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIABLE__LOCAL_GENERATED = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT__LOCAL_GENERATED;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIABLE__COMMENT = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT__COMMENT;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIABLE__NAME = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT__NAME;

	/**
	 * The number of structural features of the '<em>Variable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIABLE_FEATURE_COUNT = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.machine.impl.InvariantImpl <em>Invariant</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.machine.impl.InvariantImpl
	 * @see org.eventb.emf.core.machine.impl.MachinePackageImpl#getInvariant()
	 * @generated
	 */
	int INVARIANT = 2;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INVARIANT__ANNOTATIONS = CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__ANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INVARIANT__EXTENSIONS = CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__EXTENSIONS;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INVARIANT__ATTRIBUTES = CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INVARIANT__REFERENCE = CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__REFERENCE;

	/**
	 * The feature id for the '<em><b>Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INVARIANT__GENERATED = CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__GENERATED;

	/**
	 * The feature id for the '<em><b>Local Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INVARIANT__LOCAL_GENERATED = CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__LOCAL_GENERATED;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INVARIANT__COMMENT = CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__COMMENT;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INVARIANT__NAME = CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Predicate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INVARIANT__PREDICATE = CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__PREDICATE;

	/**
	 * The feature id for the '<em><b>Theorem</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INVARIANT__THEOREM = CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__THEOREM;

	/**
	 * The number of structural features of the '<em>Invariant</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INVARIANT_FEATURE_COUNT = CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.machine.impl.VariantImpl <em>Variant</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.machine.impl.VariantImpl
	 * @see org.eventb.emf.core.machine.impl.MachinePackageImpl#getVariant()
	 * @generated
	 */
	int VARIANT = 3;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIANT__ANNOTATIONS = CorePackage.EVENT_BCOMMENTED_EXPRESSION_ELEMENT__ANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIANT__EXTENSIONS = CorePackage.EVENT_BCOMMENTED_EXPRESSION_ELEMENT__EXTENSIONS;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIANT__ATTRIBUTES = CorePackage.EVENT_BCOMMENTED_EXPRESSION_ELEMENT__ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIANT__REFERENCE = CorePackage.EVENT_BCOMMENTED_EXPRESSION_ELEMENT__REFERENCE;

	/**
	 * The feature id for the '<em><b>Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIANT__GENERATED = CorePackage.EVENT_BCOMMENTED_EXPRESSION_ELEMENT__GENERATED;

	/**
	 * The feature id for the '<em><b>Local Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIANT__LOCAL_GENERATED = CorePackage.EVENT_BCOMMENTED_EXPRESSION_ELEMENT__LOCAL_GENERATED;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIANT__COMMENT = CorePackage.EVENT_BCOMMENTED_EXPRESSION_ELEMENT__COMMENT;

	/**
	 * The feature id for the '<em><b>Expression</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIANT__EXPRESSION = CorePackage.EVENT_BCOMMENTED_EXPRESSION_ELEMENT__EXPRESSION;

	/**
	 * The number of structural features of the '<em>Variant</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIANT_FEATURE_COUNT = CorePackage.EVENT_BCOMMENTED_EXPRESSION_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.machine.impl.EventImpl <em>Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.machine.impl.EventImpl
	 * @see org.eventb.emf.core.machine.impl.MachinePackageImpl#getEvent()
	 * @generated
	 */
	int EVENT = 4;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT__ANNOTATIONS = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT__ANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT__EXTENSIONS = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT__EXTENSIONS;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT__ATTRIBUTES = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT__ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT__REFERENCE = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT__REFERENCE;

	/**
	 * The feature id for the '<em><b>Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT__GENERATED = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT__GENERATED;

	/**
	 * The feature id for the '<em><b>Local Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT__LOCAL_GENERATED = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT__LOCAL_GENERATED;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT__COMMENT = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT__COMMENT;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT__NAME = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Convergence</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT__CONVERGENCE = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Extended</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT__EXTENDED = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Refines</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT__REFINES = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Refines Names</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT__REFINES_NAMES = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT__PARAMETERS = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Guards</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT__GUARDS = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Witnesses</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT__WITNESSES = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Actions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT__ACTIONS = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT_FEATURE_COUNT + 7;

	/**
	 * The number of structural features of the '<em>Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EVENT_FEATURE_COUNT = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT_FEATURE_COUNT + 8;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.machine.impl.ParameterImpl <em>Parameter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.machine.impl.ParameterImpl
	 * @see org.eventb.emf.core.machine.impl.MachinePackageImpl#getParameter()
	 * @generated
	 */
	int PARAMETER = 5;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__ANNOTATIONS = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT__ANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__EXTENSIONS = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT__EXTENSIONS;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__ATTRIBUTES = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT__ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__REFERENCE = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT__REFERENCE;

	/**
	 * The feature id for the '<em><b>Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__GENERATED = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT__GENERATED;

	/**
	 * The feature id for the '<em><b>Local Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__LOCAL_GENERATED = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT__LOCAL_GENERATED;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__COMMENT = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT__COMMENT;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__NAME = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT__NAME;

	/**
	 * The number of structural features of the '<em>Parameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_FEATURE_COUNT = CorePackage.EVENT_BNAMED_COMMENTED_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.machine.impl.GuardImpl <em>Guard</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.machine.impl.GuardImpl
	 * @see org.eventb.emf.core.machine.impl.MachinePackageImpl#getGuard()
	 * @generated
	 */
	int GUARD = 6;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GUARD__ANNOTATIONS = CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__ANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GUARD__EXTENSIONS = CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__EXTENSIONS;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GUARD__ATTRIBUTES = CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GUARD__REFERENCE = CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__REFERENCE;

	/**
	 * The feature id for the '<em><b>Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GUARD__GENERATED = CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__GENERATED;

	/**
	 * The feature id for the '<em><b>Local Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GUARD__LOCAL_GENERATED = CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__LOCAL_GENERATED;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GUARD__COMMENT = CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__COMMENT;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GUARD__NAME = CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Predicate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GUARD__PREDICATE = CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__PREDICATE;

	/**
	 * The feature id for the '<em><b>Theorem</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GUARD__THEOREM = CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT__THEOREM;

	/**
	 * The number of structural features of the '<em>Guard</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GUARD_FEATURE_COUNT = CorePackage.EVENT_BNAMED_COMMENTED_DERIVED_PREDICATE_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.machine.impl.WitnessImpl <em>Witness</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.machine.impl.WitnessImpl
	 * @see org.eventb.emf.core.machine.impl.MachinePackageImpl#getWitness()
	 * @generated
	 */
	int WITNESS = 7;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WITNESS__ANNOTATIONS = CorePackage.EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__ANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WITNESS__EXTENSIONS = CorePackage.EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__EXTENSIONS;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WITNESS__ATTRIBUTES = CorePackage.EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WITNESS__REFERENCE = CorePackage.EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__REFERENCE;

	/**
	 * The feature id for the '<em><b>Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WITNESS__GENERATED = CorePackage.EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__GENERATED;

	/**
	 * The feature id for the '<em><b>Local Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WITNESS__LOCAL_GENERATED = CorePackage.EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__LOCAL_GENERATED;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WITNESS__COMMENT = CorePackage.EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__COMMENT;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WITNESS__NAME = CorePackage.EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Predicate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WITNESS__PREDICATE = CorePackage.EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT__PREDICATE;

	/**
	 * The number of structural features of the '<em>Witness</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WITNESS_FEATURE_COUNT = CorePackage.EVENT_BNAMED_COMMENTED_PREDICATE_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.machine.impl.ActionImpl <em>Action</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.machine.impl.ActionImpl
	 * @see org.eventb.emf.core.machine.impl.MachinePackageImpl#getAction()
	 * @generated
	 */
	int ACTION = 8;

	/**
	 * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION__ANNOTATIONS = CorePackage.EVENT_BNAMED_COMMENTED_ACTION_ELEMENT__ANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION__EXTENSIONS = CorePackage.EVENT_BNAMED_COMMENTED_ACTION_ELEMENT__EXTENSIONS;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION__ATTRIBUTES = CorePackage.EVENT_BNAMED_COMMENTED_ACTION_ELEMENT__ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION__REFERENCE = CorePackage.EVENT_BNAMED_COMMENTED_ACTION_ELEMENT__REFERENCE;

	/**
	 * The feature id for the '<em><b>Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION__GENERATED = CorePackage.EVENT_BNAMED_COMMENTED_ACTION_ELEMENT__GENERATED;

	/**
	 * The feature id for the '<em><b>Local Generated</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION__LOCAL_GENERATED = CorePackage.EVENT_BNAMED_COMMENTED_ACTION_ELEMENT__LOCAL_GENERATED;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION__COMMENT = CorePackage.EVENT_BNAMED_COMMENTED_ACTION_ELEMENT__COMMENT;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION__NAME = CorePackage.EVENT_BNAMED_COMMENTED_ACTION_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Action</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION__ACTION = CorePackage.EVENT_BNAMED_COMMENTED_ACTION_ELEMENT__ACTION;

	/**
	 * The number of structural features of the '<em>Action</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTION_FEATURE_COUNT = CorePackage.EVENT_BNAMED_COMMENTED_ACTION_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eventb.emf.core.machine.Convergence <em>Convergence</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eventb.emf.core.machine.Convergence
	 * @see org.eventb.emf.core.machine.impl.MachinePackageImpl#getConvergence()
	 * @generated
	 */
	int CONVERGENCE = 9;


	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.machine.Machine <em>Machine</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Machine</em>'.
	 * @see org.eventb.emf.core.machine.Machine
	 * @generated
	 */
	EClass getMachine();

	/**
	 * Returns the meta object for the reference list '{@link org.eventb.emf.core.machine.Machine#getRefines <em>Refines</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Refines</em>'.
	 * @see org.eventb.emf.core.machine.Machine#getRefines()
	 * @see #getMachine()
	 * @generated
	 */
	EReference getMachine_Refines();

	/**
	 * Returns the meta object for the attribute list '{@link org.eventb.emf.core.machine.Machine#getRefinesNames <em>Refines Names</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Refines Names</em>'.
	 * @see org.eventb.emf.core.machine.Machine#getRefinesNames()
	 * @see #getMachine()
	 * @generated
	 */
	EAttribute getMachine_RefinesNames();

	/**
	 * Returns the meta object for the reference list '{@link org.eventb.emf.core.machine.Machine#getSees <em>Sees</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Sees</em>'.
	 * @see org.eventb.emf.core.machine.Machine#getSees()
	 * @see #getMachine()
	 * @generated
	 */
	EReference getMachine_Sees();

	/**
	 * Returns the meta object for the attribute list '{@link org.eventb.emf.core.machine.Machine#getSeesNames <em>Sees Names</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Sees Names</em>'.
	 * @see org.eventb.emf.core.machine.Machine#getSeesNames()
	 * @see #getMachine()
	 * @generated
	 */
	EAttribute getMachine_SeesNames();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eventb.emf.core.machine.Machine#getVariables <em>Variables</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Variables</em>'.
	 * @see org.eventb.emf.core.machine.Machine#getVariables()
	 * @see #getMachine()
	 * @generated
	 */
	EReference getMachine_Variables();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eventb.emf.core.machine.Machine#getInvariants <em>Invariants</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Invariants</em>'.
	 * @see org.eventb.emf.core.machine.Machine#getInvariants()
	 * @see #getMachine()
	 * @generated
	 */
	EReference getMachine_Invariants();

	/**
	 * Returns the meta object for the containment reference '{@link org.eventb.emf.core.machine.Machine#getVariant <em>Variant</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Variant</em>'.
	 * @see org.eventb.emf.core.machine.Machine#getVariant()
	 * @see #getMachine()
	 * @generated
	 */
	EReference getMachine_Variant();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eventb.emf.core.machine.Machine#getEvents <em>Events</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Events</em>'.
	 * @see org.eventb.emf.core.machine.Machine#getEvents()
	 * @see #getMachine()
	 * @generated
	 */
	EReference getMachine_Events();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.machine.Variable <em>Variable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Variable</em>'.
	 * @see org.eventb.emf.core.machine.Variable
	 * @generated
	 */
	EClass getVariable();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.machine.Invariant <em>Invariant</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Invariant</em>'.
	 * @see org.eventb.emf.core.machine.Invariant
	 * @generated
	 */
	EClass getInvariant();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.machine.Variant <em>Variant</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Variant</em>'.
	 * @see org.eventb.emf.core.machine.Variant
	 * @generated
	 */
	EClass getVariant();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.machine.Event <em>Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Event</em>'.
	 * @see org.eventb.emf.core.machine.Event
	 * @generated
	 */
	EClass getEvent();

	/**
	 * Returns the meta object for the attribute '{@link org.eventb.emf.core.machine.Event#getConvergence <em>Convergence</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Convergence</em>'.
	 * @see org.eventb.emf.core.machine.Event#getConvergence()
	 * @see #getEvent()
	 * @generated
	 */
	EAttribute getEvent_Convergence();

	/**
	 * Returns the meta object for the attribute '{@link org.eventb.emf.core.machine.Event#isExtended <em>Extended</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Extended</em>'.
	 * @see org.eventb.emf.core.machine.Event#isExtended()
	 * @see #getEvent()
	 * @generated
	 */
	EAttribute getEvent_Extended();

	/**
	 * Returns the meta object for the reference list '{@link org.eventb.emf.core.machine.Event#getRefines <em>Refines</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Refines</em>'.
	 * @see org.eventb.emf.core.machine.Event#getRefines()
	 * @see #getEvent()
	 * @generated
	 */
	EReference getEvent_Refines();

	/**
	 * Returns the meta object for the attribute list '{@link org.eventb.emf.core.machine.Event#getRefinesNames <em>Refines Names</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Refines Names</em>'.
	 * @see org.eventb.emf.core.machine.Event#getRefinesNames()
	 * @see #getEvent()
	 * @generated
	 */
	EAttribute getEvent_RefinesNames();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eventb.emf.core.machine.Event#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameters</em>'.
	 * @see org.eventb.emf.core.machine.Event#getParameters()
	 * @see #getEvent()
	 * @generated
	 */
	EReference getEvent_Parameters();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eventb.emf.core.machine.Event#getGuards <em>Guards</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Guards</em>'.
	 * @see org.eventb.emf.core.machine.Event#getGuards()
	 * @see #getEvent()
	 * @generated
	 */
	EReference getEvent_Guards();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eventb.emf.core.machine.Event#getWitnesses <em>Witnesses</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Witnesses</em>'.
	 * @see org.eventb.emf.core.machine.Event#getWitnesses()
	 * @see #getEvent()
	 * @generated
	 */
	EReference getEvent_Witnesses();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eventb.emf.core.machine.Event#getActions <em>Actions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Actions</em>'.
	 * @see org.eventb.emf.core.machine.Event#getActions()
	 * @see #getEvent()
	 * @generated
	 */
	EReference getEvent_Actions();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.machine.Parameter <em>Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameter</em>'.
	 * @see org.eventb.emf.core.machine.Parameter
	 * @generated
	 */
	EClass getParameter();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.machine.Guard <em>Guard</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Guard</em>'.
	 * @see org.eventb.emf.core.machine.Guard
	 * @generated
	 */
	EClass getGuard();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.machine.Witness <em>Witness</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Witness</em>'.
	 * @see org.eventb.emf.core.machine.Witness
	 * @generated
	 */
	EClass getWitness();

	/**
	 * Returns the meta object for class '{@link org.eventb.emf.core.machine.Action <em>Action</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Action</em>'.
	 * @see org.eventb.emf.core.machine.Action
	 * @generated
	 */
	EClass getAction();

	/**
	 * Returns the meta object for enum '{@link org.eventb.emf.core.machine.Convergence <em>Convergence</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Convergence</em>'.
	 * @see org.eventb.emf.core.machine.Convergence
	 * @generated
	 */
	EEnum getConvergence();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	MachineFactory getMachineFactory();

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
		 * The meta object literal for the '{@link org.eventb.emf.core.machine.impl.MachineImpl <em>Machine</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.machine.impl.MachineImpl
		 * @see org.eventb.emf.core.machine.impl.MachinePackageImpl#getMachine()
		 * @generated
		 */
		EClass MACHINE = eINSTANCE.getMachine();

		/**
		 * The meta object literal for the '<em><b>Refines</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MACHINE__REFINES = eINSTANCE.getMachine_Refines();

		/**
		 * The meta object literal for the '<em><b>Refines Names</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MACHINE__REFINES_NAMES = eINSTANCE.getMachine_RefinesNames();

		/**
		 * The meta object literal for the '<em><b>Sees</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MACHINE__SEES = eINSTANCE.getMachine_Sees();

		/**
		 * The meta object literal for the '<em><b>Sees Names</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MACHINE__SEES_NAMES = eINSTANCE.getMachine_SeesNames();

		/**
		 * The meta object literal for the '<em><b>Variables</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MACHINE__VARIABLES = eINSTANCE.getMachine_Variables();

		/**
		 * The meta object literal for the '<em><b>Invariants</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MACHINE__INVARIANTS = eINSTANCE.getMachine_Invariants();

		/**
		 * The meta object literal for the '<em><b>Variant</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MACHINE__VARIANT = eINSTANCE.getMachine_Variant();

		/**
		 * The meta object literal for the '<em><b>Events</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MACHINE__EVENTS = eINSTANCE.getMachine_Events();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.machine.impl.VariableImpl <em>Variable</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.machine.impl.VariableImpl
		 * @see org.eventb.emf.core.machine.impl.MachinePackageImpl#getVariable()
		 * @generated
		 */
		EClass VARIABLE = eINSTANCE.getVariable();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.machine.impl.InvariantImpl <em>Invariant</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.machine.impl.InvariantImpl
		 * @see org.eventb.emf.core.machine.impl.MachinePackageImpl#getInvariant()
		 * @generated
		 */
		EClass INVARIANT = eINSTANCE.getInvariant();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.machine.impl.VariantImpl <em>Variant</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.machine.impl.VariantImpl
		 * @see org.eventb.emf.core.machine.impl.MachinePackageImpl#getVariant()
		 * @generated
		 */
		EClass VARIANT = eINSTANCE.getVariant();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.machine.impl.EventImpl <em>Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.machine.impl.EventImpl
		 * @see org.eventb.emf.core.machine.impl.MachinePackageImpl#getEvent()
		 * @generated
		 */
		EClass EVENT = eINSTANCE.getEvent();

		/**
		 * The meta object literal for the '<em><b>Convergence</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EVENT__CONVERGENCE = eINSTANCE.getEvent_Convergence();

		/**
		 * The meta object literal for the '<em><b>Extended</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EVENT__EXTENDED = eINSTANCE.getEvent_Extended();

		/**
		 * The meta object literal for the '<em><b>Refines</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EVENT__REFINES = eINSTANCE.getEvent_Refines();

		/**
		 * The meta object literal for the '<em><b>Refines Names</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EVENT__REFINES_NAMES = eINSTANCE.getEvent_RefinesNames();

		/**
		 * The meta object literal for the '<em><b>Parameters</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EVENT__PARAMETERS = eINSTANCE.getEvent_Parameters();

		/**
		 * The meta object literal for the '<em><b>Guards</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EVENT__GUARDS = eINSTANCE.getEvent_Guards();

		/**
		 * The meta object literal for the '<em><b>Witnesses</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EVENT__WITNESSES = eINSTANCE.getEvent_Witnesses();

		/**
		 * The meta object literal for the '<em><b>Actions</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EVENT__ACTIONS = eINSTANCE.getEvent_Actions();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.machine.impl.ParameterImpl <em>Parameter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.machine.impl.ParameterImpl
		 * @see org.eventb.emf.core.machine.impl.MachinePackageImpl#getParameter()
		 * @generated
		 */
		EClass PARAMETER = eINSTANCE.getParameter();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.machine.impl.GuardImpl <em>Guard</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.machine.impl.GuardImpl
		 * @see org.eventb.emf.core.machine.impl.MachinePackageImpl#getGuard()
		 * @generated
		 */
		EClass GUARD = eINSTANCE.getGuard();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.machine.impl.WitnessImpl <em>Witness</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.machine.impl.WitnessImpl
		 * @see org.eventb.emf.core.machine.impl.MachinePackageImpl#getWitness()
		 * @generated
		 */
		EClass WITNESS = eINSTANCE.getWitness();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.machine.impl.ActionImpl <em>Action</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.machine.impl.ActionImpl
		 * @see org.eventb.emf.core.machine.impl.MachinePackageImpl#getAction()
		 * @generated
		 */
		EClass ACTION = eINSTANCE.getAction();

		/**
		 * The meta object literal for the '{@link org.eventb.emf.core.machine.Convergence <em>Convergence</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eventb.emf.core.machine.Convergence
		 * @see org.eventb.emf.core.machine.impl.MachinePackageImpl#getConvergence()
		 * @generated
		 */
		EEnum CONVERGENCE = eINSTANCE.getConvergence();

	}

} //MachinePackage
