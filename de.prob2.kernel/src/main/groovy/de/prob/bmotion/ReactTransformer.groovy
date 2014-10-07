package de.prob.bmotion;

import java.util.List;

import com.google.gson.Gson

import de.prob.bmotion.BMotionObserver

class ReactTransformer {

	def String bmsid
	def attributes = [:]
	def styles = [:]
	def String content

	def ReactTransformer(bmsid) {
		this.bmsid = bmsid
	}
			
}