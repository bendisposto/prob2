import de.prob.statespace.Trace

/*
  this tests ensures that a .buc file can be loaded
  and it does not result in an empty machine
*/

final s = api.eventb_load(dir+File.separator+"Lift"+File.separator+"levels.buc") 
def c = s as Trace
assert c.getCurrentState() == s.root
assert c.getCurrentState().toString() == "root"
c = c.$setup_constants()
final st = c.getCurrentState()
assert st != s.root
c = c.$initialise_machine()
assert c.getCurrentState() != st

"A .buc file can be loaded"
