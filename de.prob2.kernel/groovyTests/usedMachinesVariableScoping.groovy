import de.prob.animator.domainobjects.*
import de.prob.statespace.*


sep = File.separator
m = api.b_load(dir+sep+"machines"+sep+"references"+sep+"Foo.mch")

assert m.B.variables[0].getFormula().getCode() == "B.foo"
assert m.B.invariants[0].getFormula().getCode() == "B.foo:NAT"
"variables from used machines are correctly prefixed"