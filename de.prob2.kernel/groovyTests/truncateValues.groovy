import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.FormulaExpand
import de.prob.statespace.Trace

final s = api.b_load(dir+File.separator +"machines"+File.separator+"MultipleExample.mch", ["MAX_DISPLAY_SET": "1"])
def t = (s as Trace).$initialise_machine()
t = t.Crazy2("p1 = 5", "p2 = {2,4,6,8}")
final trans = t.getCurrentTransition()
assert trans.evaluate(FormulaExpand.TRUNCATE).getRep() == "4,{(5|->#4:{2,...,8})},6 <-- Crazy2(5,#4:{2,...,8})"
assert trans.evaluate(FormulaExpand.EXPAND).getRep() == "4,{(5|->{2,4,6,8})},6 <-- Crazy2(5,{2,4,6,8})"
assert trans.evaluate(FormulaExpand.TRUNCATE).getRep() == "4,{(5|->{2,4,6,8})},6 <-- Crazy2(5,{2,4,6,8})"

final truncated = new ClassicalB("{2,4,6,8,10}", FormulaExpand.TRUNCATE)
assert t.evalCurrent(truncated).getValue() == "#5:{2,...,10}"
final expanded = new ClassicalB("{2,4,6,8,10}")
assert t.evalCurrent(expanded).getValue() == "{2,4,6,8,10}"

"Expanding and truncating a formila works correctly"
