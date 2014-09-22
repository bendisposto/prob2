import de.prob.animator.domainobjects.*
import de.prob.statespace.*

m = api.b_load(dir+"/machines/scheduler.mch")
s = m as StateSpace
x = s.root
f = new LTL("{card(active)>0}")
c = new ExecuteUntilCommand(s,x,f)
s.execute(c)
y = c.getFinalState()

x = s.root.anyEvent().anyEvent()
f = new LTL("[swap]")
c = new ExecuteUntilCommand(s,x,f)
s.execute(c)
t = c.getTrace(s)
assert t != null
" The ExecuteUntilCommand was executed successfully."