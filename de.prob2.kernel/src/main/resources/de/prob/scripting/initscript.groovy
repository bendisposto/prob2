import de.prob.animator.domainobjects.CSP
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.EvalResult
import de.prob.animator.domainobjects.EventB
import de.prob.statespace.TraceDecorator
import de.prob.util.Tuple2

// This extends String's as operator to allow creating EvalElements from strings.
// For example, "x" as ClassicalB creates a ClassicalB object by parsing the formula "x".
// The same works for the EventB and CSP types.
final oldStringAsType = String.metaClass.getMetaMethod("asType", [Class] as Class<?>[])
String.metaClass.asType = {Class<?> type -> 
	if (type == ClassicalB) {
		new ClassicalB(delegate)
	} else if (type == EventB) {
		new EventB(delegate)
	} else if (type == CSP) {
		new CSP(delegate)
	} else {
		oldStringAsType.invoke(delegate, [type] as Object[])
	}
}

final oldTuple2Equals = Tuple2.metaClass.getMetaMethod("equals", [Object] as Class<?>[])
Tuple2.metaClass.equals = {Object other ->
	if (other instanceof ArrayList<?>) {
		return other.size() == 2 && delegate.first == other[0] && delegate.second == other[1]
	} else {
		return oldTuple2Equals.invoke(delegate, [other] as Object[])
	}
}

final oldArrayListEquals = ArrayList.metaClass.getMetaMethod("equals", [Object] as Class<?>[])
ArrayList.metaClass.equals = {Object other ->
	if (other instanceof Tuple2<?, ?>) {
		return delegate.size() == 2 && delegate[0] == other.first && delegate[1] == other.second
	} else {
		return oldArrayListEquals.invoke(delegate, [other] as Object[])
	}
}

def appendToTrace(t, c) {
	c.resolveStrategy = Closure.DELEGATE_FIRST
	c.delegate = new TraceDecorator(t)
	c()
}

// Redirect print and println to our own buffered console
inConsole = true
Script.metaClass.print = {s ->
	__console.append(s)
	if (!inConsole) {
		System.out.print(s)
	}
}
Script.metaClass.println = {s ->
	__console.append(s + "\n")
	if (!inConsole) {
		System.out.println(s)
	}
}

def execTrace(t, c) {
	final proxy = new TraceDecorator(t)
	c.resolveStrategy = Closure.DELEGATE_FIRST
	c.delegate = proxy
	c()
}

final oldEvalResultAsType = EvalResult.metaClass.getMetaMethod("asType", [Class] as Class<?>[])
EvalResult.getMetaClass().asType = {Class<?> clazz ->
	if (clazz == Integer) {
		Integer.valueOf(delegate.value)
	} else if (clazz == Double) {
		Double.valueOf(delegate.value)
	} else if (clazz == String) {
		delegate.value
	} else {
		oldEvalResultAsType.invoke(delegate, [clazz] as Object[])
	}
}

def eval(script) {
	engine.eval(script)
}
