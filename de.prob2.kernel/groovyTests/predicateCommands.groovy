import de.prob.animator.command.BeforeAfterPredicateCommand
import de.prob.animator.command.PrimePredicateCommand
import de.prob.animator.domainobjects.EventB
import de.prob.model.classicalb.ClassicalBMachine

final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")

final pred = "x = 5 & y = 7" as EventB
final cmd1 = new PrimePredicateCommand(pred)
s.execute(cmd1)

assert cmd1.getPrimedPredicate() != null
assert cmd1.getPrimedPredicate().getCode() == "#(x\',y\').(x\' = 5 & y\' = 7)"

final model = s.getMainComponent() as ClassicalBMachine

final cmd2 = new BeforeAfterPredicateCommand("swap")
s.execute(cmd2)

assert cmd2.getBeforeAfterPredicate() != null
assert cmd2.getBeforeAfterPredicate().getCode() == "active /= {} & (waiting\' = waiting \\/ active & ((ready = {} => active\' = {} & ready\' = ready) & (not(ready = {}) => #pp.(pp : ready & (active\' = {pp} & ready\' = ready - {pp})))))"

"predicate commands work"
