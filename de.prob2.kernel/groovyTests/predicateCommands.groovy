import de.prob.statespace.*

s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")

pred = "x = 5 & y = 7" as EventB
cmd = new PrimePredicateCommand(pred)
s.execute(cmd)

assert cmd.getPrimedPredicate() != null
assert cmd.getPrimedPredicate().getCode() == "#(x\',y\').(x\' = 5 & y\' = 7)"

model = s.getMainComponent() as ClassicalBMachine

cmd = new BeforeAfterPredicateCommand("swap")
s.execute(cmd)

assert cmd.getBeforeAfterPredicate() != null
assert cmd.getBeforeAfterPredicate().getCode() == "active /= {} & (waiting\' = waiting \\/ active & ((ready = {} => active\' = {} & ready\' = ready) & (not(ready = {}) => #pp.(pp : ready & (active\' = {pp} & ready\' = ready - {pp})))))"

"predicate commands work"