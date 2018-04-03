import java.nio.file.Paths

import de.prob.animator.command.BeforeAfterPredicateCommand
import de.prob.animator.command.PrimePredicateCommand
import de.prob.animator.domainobjects.EventB
import de.prob.model.classicalb.ClassicalBMachine

final s = api.b_load(Paths.get(dir, "machines", "scheduler.mch").toString())

final pred = "x = 5 & y = 7" as EventB
final cmd1 = new PrimePredicateCommand(pred)
s.execute(cmd1)

assert cmd1.primedPredicate != null
assert cmd1.primedPredicate.code == "#(x',y').(x' = 5 & y' = 7)"

final model = s.mainComponent as ClassicalBMachine

final cmd2 = new BeforeAfterPredicateCommand("swap")
s.execute(cmd2)

assert cmd2.beforeAfterPredicate != null
assert cmd2.beforeAfterPredicate.code == "active /= {} & (waiting' = waiting \\/ active & ((ready = {} => active' = {} & ready' = ready) & (not(ready = {}) => #pp.(pp : ready & (active' = {pp} & ready' = ready - {pp})))))"

"predicate commands work"
