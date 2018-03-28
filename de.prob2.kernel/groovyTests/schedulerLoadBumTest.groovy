import de.prob.statespace.Trace;

/*
  this tests ensures that a .bum file can be loaded
  and it does not result in an empty machine
*/

final s = api.eventb_load(dir+File.separator+"Scheduler"+File.separator+"Scheduler0.bum") 
def c = s as Trace
assert c.getCurrentState() == s.root
assert c.getCurrentState().toString() == "root"
c = c.anyEvent()
st = c.getCurrentState()
assert st != s.root
c = c.anyEvent()
assert c.getCurrentState() != st

"A .bum file (Scheduler0.bum) was loaded and some steps were made"
