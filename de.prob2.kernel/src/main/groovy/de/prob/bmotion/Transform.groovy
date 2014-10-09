package de.prob.bmotion;

import java.util.List;

import com.google.gson.Gson

import de.prob.bmotion.BMotionObserver

class Transform extends BMotionObserver {

	def String selector
	def attributes = [:]
	def styles = [:]
	def content

	def Transform() {
	}
	
	def Transform(selector) {
		this.selector = selector
	}

	def Transform(selector, attributes) {
		this.selector = selector
		this.attributes = attributes
	}

	def Transform set(String name, Object value) {
		(name == "content" || name == "text") ? content = value : attributes.put(name,value)
		this
	}

	def Transform attr(String name, Object value) {
		set(name, value)
	}
	
	def Transform style(String name, Object value) {
		styles.put(name, value)
		this
	}
	
	def List<Transform> update(BMotion bms) {
		[this]
	}

}