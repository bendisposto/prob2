import de.prob.animator.domainobjects.*
import de.prob.statespace.*


m = api.b_load(dir+File.separator +"machines"+File.separator+"MultipleExample.mch", ["MAX_DISPLAY_SET": "1"])
s = m as StateSpace
t = (m as Trace).$initialise_machine()
t = t.Crazy2("p1 = 5", "p2 = {2,4,6,8}")
trans = t.getCurrentTransition()
assert trans.evaluate(FormulaExpand.truncate).getRep() == "4,{(5|->#4:{2,...,8})},6 <-- Crazy2(5,#4:{2,...,8})"
assert trans.evaluate(FormulaExpand.expand).getRep() == "4,{(5|->{2,4,6,8})},6 <-- Crazy2(5,{2,4,6,8})"
assert trans.evaluate(FormulaExpand.truncate).getRep() == "4,{(5|->{2,4,6,8})},6 <-- Crazy2(5,{2,4,6,8})"

truncated = new ClassicalB("{2,4,6,8,10}")
assert t.evalCurrent(truncated).getValue() == "#5:{2,...,10}"
expanded = new ClassicalB("{2,4,6,8,10}",FormulaExpand.expand)
assert t.evalCurrent(expanded).getValue() == "{2,4,6,8,10}"

s.animator.cli.shutdown();
"add a description of the test here"