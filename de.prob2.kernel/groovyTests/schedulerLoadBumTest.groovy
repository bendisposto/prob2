import de.prob.statespace.*
import de.prob.animator.domainobjects.*;

/*
  this tests ensures that a .bum file can be loaded
  and it does not result in an empty machine
*/

s = api.eventb_load(dir+File.separator+"Scheduler"+File.separator+"Scheduler0.bum") 
c = s as Trace
assert c.getCurrentState() == s.root
assert c.getCurrentState().toString() == "root"
c = c.anyEvent()
st = c.getCurrentState()
assert st != s.root
c = c.anyEvent()
assert c.getCurrentState() != st

"A .bum file (Scheduler0.bum) was loaded and some steps were made"