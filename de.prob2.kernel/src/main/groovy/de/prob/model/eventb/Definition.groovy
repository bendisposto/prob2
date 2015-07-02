package de.prob.model.eventb

class Definition {
	static {
		def oldMapAsType = LinkedHashMap.metaClass.getMetaMethod("asType", [Class] as Class[])
		
		LinkedHashMap.metaClass.asType = { Class type ->
			if (type == Definition) return new Definition(delegate)
			oldMapAsType.invoke(delegate, [type] as Class[])
		}
	}
	
	String label
	String formula
	
	def Definition(LinkedHashMap map) {
		if (map.size() != 1) {
			throw new IllegalArgumentException("Definitions must define only one property")
		}
		def prop = map.collect { k,v -> [k, v]}[0]
		if (!(prop[0] instanceof String && prop[1] instanceof String)) {
			throw new IllegalArgumentException("Labels and formulas for definitions must be strings")
		}
		this.label = prop[0]
		this.formula = prop[1]
	}
}
