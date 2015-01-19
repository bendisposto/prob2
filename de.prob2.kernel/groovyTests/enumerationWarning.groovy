import de.prob.animator.domainobjects.*
import de.prob.statespace.*

m = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
s = m as StateSpace
t = new Trace(s)
t = t.$initialise_machine()

x = "{w | w : NATURAL & w mod 2 = 0} /\\ {v | v : NATURAL & v mod 2 = 1} = {}" as ClassicalB
res = t.evalCurrent(x)
assert res instanceof EnumerationWarning

tf = new TranslateFormula(x)
res = t.evalCurrent(tf)
assert res instanceof EnumerationWarning 

s.animator.cli.shutdown();
"enumeration warnings are handled"