import de.prob.animator.domainobjects.CSP
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.EvalResult
import de.prob.animator.domainobjects.EventB
import de.prob.statespace.TraceDecorator

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

ArrayList.metaClass.to {Class<?> type ->
	return delegate.collect {it.asType(type)}
}

def appendToTrace(t, c) {
	c.resolveStrategy = Closure.DELEGATE_FIRST
	c.delegate = new TraceDecorator(t)
	c()
}

// Redirect print and println to our own buffered console
inConsole = true
void print(s) {
	__console.print(s)
	if (!inConsole) {
		System.out.print(s)
	}
}
void println(s) {
	__console.println(s)
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
