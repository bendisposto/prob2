import de.prob.statespace.*
import de.prob.animator.domainobjects.*;

/*
  this tests ensures that a .buc file can be loaded
  and it does not result in an empty machine
*/

s = api.eventb_load(dir+File.separator+"Lift"+File.separator+"levels.buc") 
c = s as Trace
assert c.getCurrentState() == s.root
assert c.getCurrentState().toString() == "root"
c = c.$setup_constants()
st = c.getCurrentState()
assert st != s.root
c = c.$initialise_machine()
assert c.getCurrentState() != st

s.animator.cli.shutdown();
"A .buc file can be loaded"