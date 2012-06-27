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

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Event BObject</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eventb.emf.core.EventBObject#getAnnotations <em>Annotations</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eventb.emf.core.CorePackage#getEventBObject()
 * @model abstract="true"
 * @generated
 */
public interface EventBObject extends EObject {
	/**
	 * Returns the value of the '<em><b>Annotations</b></em>' containment reference list.
	 * The list contents are of type {@link org.eventb.emf.core.Annotation}.
	 * It is bidirectional and its opposite is '{@link org.eventb.emf.core.Annotation#getEventBObject <em>Event BObject</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Annotations</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Annotations</em>' containment reference list.
	 * @see org.eventb.emf.core.CorePackage#getEventBObject_Annotations()
	 * @see org.eventb.emf.core.Annotation#getEventBObject
	 * @model opposite="eventBObject" containment="true"
	 * @generated
	 */
	EList<Annotation> getAnnotations();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * returns the nearest container of this element that is a sub-type eClass
	 * or null if no container of that type
	 * @param 	the EClass that is the super-type of the returned elements
	 * @return 	containing EventBObject that is a sub-type of eClass
	 * 
	 * <!-- end-model-doc -->
	 * @model required="true" eClassRequired="true"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='EventBObject bObject = this;\nwhile (!eClass.isSuperTypeOf(bObject.eClass()))\n\tif (bObject.eContainer() instanceof EventBObject) bObject=(EventBObject)bObject.eContainer();\n\telse return null;\nreturn bObject;'"
	 * @generated
	 */
	EventBObject getContaining(EClass eClass);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * returns a list of elements that sub-type eClass and
	 * that are contained (directly or indirectly) by this element
	 * @param  eClass - 	the EClass that is the super-type of the returned elements
	 * @param resolve - 	whether to resolve proxies
	 * 
	 * <!-- end-model-doc -->
	 * @model many="false" eClassRequired="true" resolveRequired="true"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='EList<EObject> typeObjects = new BasicEList<EObject>();\ntypeObjects.add(null);\t//include the null object\nfor (TreeIterator<EObject>trit = EcoreUtil.getAllContents(this, resolve); trit.hasNext();){\n\tEObject o = trit.next();\n\tif (eClass.isSuperTypeOf(o.eClass())) typeObjects.add(o);\n}\nreturn typeObjects;'"
	 * @generated
	 */
	EList<EObject> getAllContained(EClass eClass, boolean resolve);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * returns the URI of this element.
	 * (If the element is not loaded, the URI is obtained from the proxy without loading the element)
	 * 
	 * [AFTER RE-GENERATING REMOVE THE TYPE PARAMETER <URI>]
	 * <!-- end-model-doc -->
	 * @model kind="operation" required="true"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel body='if (eIsProxy()){\n\treturn ((InternalEObject)this).eProxyURI();\n}else{\n\treturn eResource().getURI();\n}'"
	 * @generated
	 */
	<URI> URI getURI();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * returns the annotation with the specified source or null if no such annotation is contained by this element
	 * @param  source - 	the source (string) of the Annotation to be retrieved
	 * 
	 * <!-- end-model-doc -->
	 * @model annotation="http://www.eclipse.org/emf/2002/GenModel body='for (Annotation annotation : getAnnotations()){\n\tif (source.equals(annotation.getSource())) return annotation;\n}\nreturn null;'"
	 * @generated
	 */
	Annotation getAnnotation(String source);

} // EventBObject
