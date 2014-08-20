package de.prob.statespace

class TraceDecorator {
	private delegate
	private mch
	def _ = null

	TraceDecorator(delegate) {
		this.delegate = delegate
		this.mch = delegate.getModel().getMainComponent()
	}
	def invokeMethod(String name, args) {
		def pred = ""
		if (args.size() == 1 && args[0] instanceof Closure) {
			pred =  args[0]()
		}
		else {
			pred = make_predicate(mch.getOperations().find {it.name == name}.getParameters(),args)
		}

		this.delegate = delegate.invokeMethod(name, [pred])
	}

	def make_predicate(formal_params,actual_params) {
		def  p = [formal_params, actual_params]
		.transpose()
		.findAll { a,b -> b != null }
		.collect { a,b -> a.toString() + "=" + b.toString() }
		.join(" && ")
		p.isEmpty() ? "TRUE = TRUE" : p
	}
}