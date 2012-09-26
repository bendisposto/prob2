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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Event BNamed</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eventb.emf.core.EventBNamed#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eventb.emf.core.CorePackage#getEventBNamed()
 * @model abstract="true"
 * @generated
 */
public interface EventBNamed extends EObject {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eventb.emf.core.CorePackage#getEventBNamed_Name()
	 * @model default="" required="true" transient="true" volatile="true" derived="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eventb.emf.core.EventBNamed#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <getName() should be changed to call this method>
	 * 
	 * Assumes this is a subclass of EventBElement.
	 * Returns this element's name or, if it is a proxy, the name being referenced.
	 * The name is derived from the element's 'reference' attribute which
	 *  includes the element type to ensure that references are unique within a resource.
	 * (Calling this method will not resolve any unresolved proxies).
	 * <!-- end-model-doc -->
	 * @model annotation="http://www.eclipse.org/emf/2002/GenModel body='assert (this instanceof <%org.eventb.emf.core.EventBElement%>);\nString reference = ((EventBElement)this).getReferenceWithoutResolving();\rreturn reference.length() > this.eStaticClass().getInstanceClassName().length() ?\n\treference.substring(this.eStaticClass().getInstanceClassName().length()+1)\n\t: \"\";'"
	 * @generated
	 */
	String doGetName();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <setName() should be changed to call this method>
	 * 
	 * If this is not a subclass of EventBElement this method does nothing.
	 * Sets the name of this element or, if it is a proxy, the name being referenced. 
	 * The name is stored in the 'reference' attribute which also contains the element's 
	 * type to ensure that references are unique winthin a resource.
	 * (Calling this method will not resolve any unresolved proxies).
	 * 
	 * 
	 * <!-- end-model-doc -->
	 * @model annotation="http://www.eclipse.org/emf/2002/GenModel body='((EventBElement)this).setReference(this.eStaticClass().getInstanceClassName()+\".\"+newName);'"
	 * @generated
	 */
	void doSetName(String newName);

} // EventBNamed
