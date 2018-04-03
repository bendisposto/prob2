import java.nio.file.Paths

import de.prob.animator.command.GetMachineStructureCommand

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())
assert s != null

final c = new GetMachineStructureCommand()
s.execute(c)

final toTest = c.prologASTList

assert toTest[0].name == "SETS"

assert toTest[1].name == "VARIABLES"
assert toTest[1].expanded
assert !toTest[1].subnodes.empty

assert toTest[2].name == "INVARIANTS"
assert !toTest[2].expanded
assert !toTest[2].subnodes.empty

assert toTest[3].name == "OPERATIONS"
assert !toTest[3].expanded
final subnodesOp = toTest[3].subnodes
assert subnodesOp[0].propagated
assert subnodesOp[0].name == "nr_ready"
assert subnodesOp[-1].propagated
assert subnodesOp[-1].name == "swap"

"PrologAST is built properly"
