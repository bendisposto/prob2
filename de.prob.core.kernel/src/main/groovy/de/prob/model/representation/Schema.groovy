package de.prob.model.representation



class Schema {

	def static String ALL = "\$ALL\$"

	def type
	def name
	def components = []

	def Schema() {
	}

	def Schema(type, name) {
		this.type = type
		this.name = name
	}

	/*
	 enum Constants {
	 ALL, NAMES, FORMULAS
	 }
	 def components = []
	 class Component {
	 def type
	 def filter
	 def subcomponents = []
	 def Component(type) {
	 this.type = type
	 }
	 def setFilter(filter) {
	 this.filter = f
	 }
	 def Variables(p=Constants.ALL) {
	 subcomponents << new Component(Variable.class).with { setFilter(p) }
	 }
	 }
	 def type(Class type, closure) {
	 new Component(type)
	 }
	 def Machine(closure) {
	 components << new Component().with(closure)
	 }
	 def Machines(closure) {
	 components << new Component(Machine.class).with(closure)
	 }*/

	def type(Class t) {
		type(t, ALL, {})
	}

	def type(Class t, closure) {
		type(t, ALL, closure)
	}

	def type(Class t, String name, closure) {
		def s = new Schema(t, name).with(closure)
	}
}
