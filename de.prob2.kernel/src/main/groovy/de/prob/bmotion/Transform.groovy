package de.prob.bmotion;

import com.google.gson.Gson
import de.prob.bmotion.BMotionObserver

class Transform extends BMotionObserver {

	def String selector
	def attributes = [:]
	def String content
	
	def Transform() {
		
	}
	
	def Transform(selector) {
		this(selector,[:])
	}
	
	def Transform(selector,attributes) {
		this.selector = selector
		this.attributes = attributes
	}

	def Transform set(String name,  String value) {
		(name == "content" || name == "text") ? content = value : attributes.put(name,value)
		this
	}

	def Transform attr(String name,  String value) {
		set(name, value)
	}
	
	def Transform content(String content) {
		this.content = content
		this
	}
	
	def List<Transform> update(BMotionStudioSession bms) {
		[this]
	}
	
	@Override
	public String toString() {
		Gson g = new Gson();
		return g.toJson(this);
	}

}