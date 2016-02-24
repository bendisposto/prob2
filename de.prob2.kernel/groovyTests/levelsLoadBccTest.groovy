import de.prob.statespace.*
import de.prob.animator.domainobjects.*;

/*
  this tests ensures that a .bcc file can be loaded
  and it does not result in an empty machine
*/

s = api.eventb_load(dir+File.separator+"Lift"+File.separator+"levels.bcc")
c = s as Trace
assert c.getCurrentState() == s.root
assert c.getCurrentState().toString() == "root"
c = c.anyEvent()
st = c.getCurrentState()
assert st != s.root
c = c.anyEvent()
assert c.getCurrentState() != st

"A .bcc file can be loaded"