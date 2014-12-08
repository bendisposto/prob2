import de.prob.animator.domainobjects.*
import de.prob.statespace.*

m = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
s = m as StateSpace
x = s.root
f = new LTL("{card(active)>0}")
c = new ExecuteUntilCommand(s,x,f)
s.execute(c)
assert cmd.isSuccess()
y = c.getFinalState()
t = c.getTrace(s)
assert t != null

x = s.root.anyEvent().anyEvent()
f = new LTL("F Y [swap]")
c = new ExecuteUntilCommand(s,x,f)
s.execute(c)
assert cmd.isSuccess()
t = c.getTrace(s)
assert t != null
opList = t.getTransitionList()
assert opList[opList.size() - 1].getRep() == "swap()"

x = s.root
f = new LTL("F e(swap)")
c = new ExecuteUntilCommand(s,x,f)
s.execute(c)
assert cmd.isSuccess()
t = c.getTrace(s)
assert t != null
assert t.canExecuteEvent("swap",[])

s.animator.cli.shutdown()
" The ExecuteUntilCommand was executed successfully."