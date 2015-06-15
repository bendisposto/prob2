import de.prob.animator.domainobjects.*
import de.prob.statespace.*


sep = File.separator
s = api.b_load(dir+sep+"machines"+sep+"references"+sep+"Foo.mch")
m = s as ClassicalBModel

assert m.B.variables[0].getFormula().getCode() == "B.foo"
assert m.B.invariants[0].getFormula().getCode() == "B.foo:NAT"
"variables from used machines are correctly prefixed"