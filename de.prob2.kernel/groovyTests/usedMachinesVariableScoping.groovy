import de.prob.model.classicalb.ClassicalBModel

final sep = File.separator
final s = api.b_load(dir+sep+"machines"+sep+"references"+sep+"Foo.mch")
final m = s as ClassicalBModel

assert m.B.variables[0].getFormula().getCode() == "B.foo"
assert m.B.invariants[0].getFormula().getCode() == "B.foo:NAT"
"variables from used machines are correctly prefixed"
