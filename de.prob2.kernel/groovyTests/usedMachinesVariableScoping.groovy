import java.nio.file.Paths

import de.prob.model.classicalb.ClassicalBModel

final s = api.b_load(Paths.get(dir, "machines", "references", "Foo.mch").toString())
final m = s as ClassicalBModel

assert m.B.variables[0].formula.code == "B.foo"
assert m.B.invariants[0].formula.code == "B.foo:NAT"
"variables from used machines are correctly prefixed"
