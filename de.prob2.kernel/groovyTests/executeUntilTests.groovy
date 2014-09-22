import de.prob.animator.domainobjects.*
import de.prob.statespace.*

m = api.b_load(dir+"/machines/scheduler.mch")
s = m as StateSpace
x = s.root
f = new LTL("{card(active)>0}")
c = new ExecuteUntilCommand(s,x,f)
s.execute(c)
y = c.getFinalState()
" The ExecuteUntilCommand was executed successfully."