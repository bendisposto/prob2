import de.prob.animator.domainobjects.*
import de.prob.statespace.*

m = api.b_load(dir+"/machines/scheduler.mch")
s = m as StateSpace
x = s.root
f = new LTL("{card(active)>0}")
c = new ExecuteUntilCommand(s,x,f)
s.execute(c)
y = c.getFinalState()
t = c.getTrace(s)
assert t != null

x = s.root.anyEvent().anyEvent()
f = new LTL("F [swap]")
c = new ExecuteUntilCommand(s,x,f)
s.execute(c)
t = c.getTrace(s)
assert t != null
opList = t.current.opList
assert s.isExplored(t.getCurrentState())
assert opList[opList.size() - 1].getRep() == "swap()"

x = s.root
f = new LTL("F e(swap)")
c = new ExecuteUntilCommand(s,x,f)
s.execute(c)
t = c.getTrace(s)
assert t != null
assert t.canExecuteEvent("swap",[])

s.animator.cli.shutdown()
" The ExecuteUntilCommand was executed successfully."