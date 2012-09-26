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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Convergence</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.eventb.emf.core.machine.MachinePackage#getConvergence()
 * @model
 * @generated
 */
public enum Convergence implements Enumerator {
	/**
	 * The '<em><b>Ordinary</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ORDINARY_VALUE
	 * @generated
	 * @ordered
	 */
	ORDINARY(0, "ordinary", "ordinary"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Convergent</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CONVERGENT_VALUE
	 * @generated
	 * @ordered
	 */
	CONVERGENT(1, "convergent", "convergent"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Anticipated</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ANTICIPATED_VALUE
	 * @generated
	 * @ordered
	 */
	ANTICIPATED(2, "anticipated", "anticipated"); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * The '<em><b>Ordinary</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Ordinary</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ORDINARY
	 * @model name="ordinary"
	 * @generated
	 * @ordered
	 */
	public static final int ORDINARY_VALUE = 0;

	/**
	 * The '<em><b>Convergent</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Convergent</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CONVERGENT
	 * @model name="convergent"
	 * @generated
	 * @ordered
	 */
	public static final int CONVERGENT_VALUE = 1;

	/**
	 * The '<em><b>Anticipated</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Anticipated</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ANTICIPATED
	 * @model name="anticipated"
	 * @generated
	 * @ordered
	 */
	public static final int ANTICIPATED_VALUE = 2;

	/**
	 * An array of all the '<em><b>Convergence</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final Convergence[] VALUES_ARRAY =
		new Convergence[] {
			ORDINARY,
			CONVERGENT,
			ANTICIPATED,
		};

	/**
	 * A public read-only list of all the '<em><b>Convergence</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<Convergence> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Convergence</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static Convergence get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			Convergence result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Convergence</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static Convergence getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			Convergence result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Convergence</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static Convergence get(int value) {
		switch (value) {
			case ORDINARY_VALUE: return ORDINARY;
			case CONVERGENT_VALUE: return CONVERGENT;
			case ANTICIPATED_VALUE: return ANTICIPATED;
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final int value;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String name;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String literal;

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private Convergence(int value, String name, String literal) {
		this.value = value;
		this.name = name;
		this.literal = literal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getValue() {
	  return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
	  return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLiteral() {
	  return literal;
	}

	/**
	 * Returns the literal value of the enumerator, which is its string representation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		return literal;
	}
	
} //Convergence
