package de.prob.bmotion;

import com.google.gson.Gson
import de.prob.bmotion.BMotionObserver

class Transform extends BMotionObserver {

	def String selector
	def List<Attribute> attributes = []
		
	def Transform(selector) {
		this(selector,[])
	}
	
	def Transform(selector,attributes) {
		this.selector = selector
		this.attributes = attributes
	}
		
	def Transform set(String name,  String value) {
		attributes << new Attribute(name, value);
		this
	}

	def Transform attr(String name,  String value) {
		attributes << new Attribute(name, value);
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