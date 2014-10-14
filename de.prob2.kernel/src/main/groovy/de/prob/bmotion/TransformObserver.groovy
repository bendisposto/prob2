package de.prob.bmotion

import groovy.transform.TupleConstructor;

@TupleConstructor
class TransformObserver implements BMotionObserver {

	def String selector
	def attributes = [:]
	def styles = [:]
	def content

	def TransformObserver set(String name, Object value) {
		(name == "content" || name == "text") ? content = value : attributes.put(name,value)
		this
	}

	def TransformObserver attr(String name, Object value) {
		set(name, value)
	}
	
	def TransformObserver style(String name, Object value) {
		styles.put(name, value)
		this
	}
	
	def List<Transform> update(BMotion bms) {
        def t = new Transform((selector instanceof Closure)? selector() : selector)
        t.attributes = attributes.collectEntries { kv ->
            (kv.value instanceof Closure)? [kv.key, kv.value()] : [kv.key, kv.value
            ]}
        t.styles = styles.collectEntries { kv ->
            (kv.value instanceof Closure)? [kv.key, kv.value()] : [kv.key, kv.value
            ]}
        t.content = (content instanceof Closure)? content() : content
        [t]
	}

}