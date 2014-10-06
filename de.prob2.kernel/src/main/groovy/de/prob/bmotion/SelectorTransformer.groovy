package de.prob.bmotion;

import java.util.List;

import com.google.gson.Gson

import de.prob.bmotion.BMotionObserver

class SelectorTransformer extends BMotionObserver {

	def String selector
	def attributes = [:]
	def String content

	def SelectorTransformer(selector) {
		this.selector = selector
	}

	def SelectorTransformer(selector,attributes) {
		this.selector = selector
		this.attributes = attributes
	}

	def SelectorTransformer set(String name,  String value) {
		(name == "content" || name == "text") ? content = value : attributes.put(name,value)
		this
	}

	def SelectorTransformer attr(String name,  String value) {
		set(name, value)
	}
	
	def SelectorTransformer content(String content) {
		this.content = content
		this
	}
	
	def List<SelectorTransformer> update(BMotion bms) {
		[this]
	}
	
	@Override
	public String toString() {
		Gson g = new Gson();
		return g.toJson(this);
	}

}