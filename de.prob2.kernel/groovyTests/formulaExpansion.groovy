import com.sun.org.apache.bcel.internal.generic.F2D;

import de.prob.animator.command.InsertFormulaForVisualizationCommand;
import de.prob.animator.domainobjects.*
import de.prob.statespace.*
import de.prob.unicode.UnicodeTranslator;

toUnicode = { str -> UnicodeTranslator.toUnicode(str) }

// You can change the model you are testing here.
m = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
s = m as StateSpace
t = new Trace(s)
t = t.$initialise_machine()

f = "(ready /\\ waiting) = {} & card(active) <= 1" as ClassicalB
cmd = new InsertFormulaForVisualizationCommand(f)
s.execute(cmd)
assert cmd.getFormulaId() != null

cmd = new ExpandFormulaCommand(cmd.getFormulaId(), t.getCurrentState())
s.execute(cmd)
assert cmd.getResult() != null && cmd.getResult() instanceof ExpandedFormula
formula = cmd.getResult()
assert formula.getLabel() == "ready \u2229 waiting = \u2205 \u2227 card(active) \u2264 1"
assert formula.getValue() == true
assert formula.children.size() == 2

f1 = formula.getChildren()[0]
assert f1.getLabel() == "ready \u2229 waiting = \u2205"
assert f1.getValue() == true
assert f1.children.size() == 1

f11 = f1.getChildren()[0]
assert f11.getLabel() == 'ready \u2229 waiting'
assert f11.getValue() == toUnicode("{}")
assert f11.getChildren().size() == 2

f111 = f11.getChildren()[0]
assert f111.getLabel() == "ready"
assert f111.getValue() == toUnicode("{}")
assert f111.getChildren() == null

f112 = f11.getChildren()[1]
assert f112.getLabel() == "waiting"
assert f112.getValue() == toUnicode("{}")
assert f112.getChildren() == null

f2 = formula.getChildren()[1]
assert f2.getLabel() == 'card(active) \u2264 1'
assert f2.getValue() == true
assert f2.getChildren().size() == 1

f21 = f2.getChildren()[0]
assert f21.getLabel() == 'card(active)'
assert f21.getValue() == '0'
assert f21.getChildren().size() == 1

f211 = f21.getChildren()[0]
assert f211.getLabel() == 'active'
assert f211.getValue() == toUnicode('{}')
assert f211.getChildren() == null

s.animator.cli.shutdown();
"expanding a B formula works"