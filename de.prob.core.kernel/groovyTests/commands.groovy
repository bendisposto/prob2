import de.prob.statespace.*

m = api.b_load(dir+"/machines/scheduler.mch")
s = m as StateSpace
h = s as Trace

h = h.add(0)

cmd = new de.prob.animator.command.EvaluationExpandCommand("inv")
s.execute(cmd)
cmd.getChildrenIds()

cmd2 = new de.prob.animator.command.EvaluationGetValuesCommand(cmd.getChildrenIds(),"1")
s.execute(cmd2)
values = cmd2.getValues()
assert values["1"]
assert values["2"]
assert values["3"]
assert values["4"]
