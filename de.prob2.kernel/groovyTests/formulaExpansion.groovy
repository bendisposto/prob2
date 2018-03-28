import de.prob.animator.command.ExpandFormulaCommand
import de.prob.animator.command.InsertFormulaForVisualizationCommand
import de.prob.animator.domainobjects.ClassicalB
import de.prob.animator.domainobjects.ExpandedFormula
import de.prob.statespace.Trace
import de.prob.unicode.UnicodeTranslator

final toUnicode = { str -> UnicodeTranslator.toUnicode(str) }

// You can change the model you are testing here.
final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
def t = new Trace(s)
t = t.$initialise_machine()

final f = "(ready /\\ waiting) = {} & card(active) <= 1" as ClassicalB
final cmd1 = new InsertFormulaForVisualizationCommand(f)
s.execute(cmd1)
assert cmd1.getFormulaId() != null

final cmd2 = new ExpandFormulaCommand(cmd1.getFormulaId(), t.getCurrentState())
s.execute(cmd2)
assert cmd2.getResult() != null && cmd2.getResult() instanceof ExpandedFormula
final formula = cmd2.getResult()
assert formula.getLabel() == "ready \u2229 waiting = \u2205 \u2227 card(active) \u2264 1"
assert formula.getValue() == true
assert formula.children.size() == 2

final f1 = formula.getChildren()[0]
assert f1.getLabel() == "ready \u2229 waiting = \u2205"
assert f1.getValue() == true
assert f1.children.size() == 1

final f11 = f1.getChildren()[0]
assert f11.getLabel() == 'ready \u2229 waiting'
assert f11.getValue() == toUnicode("{}")
assert f11.getChildren().size() == 2

final f111 = f11.getChildren()[0]
assert f111.getLabel() == "ready"
assert f111.getValue() == toUnicode("{}")
assert f111.getChildren() == null

final f112 = f11.getChildren()[1]
assert f112.getLabel() == "waiting"
assert f112.getValue() == toUnicode("{}")
assert f112.getChildren() == null

final f2 = formula.getChildren()[1]
assert f2.getLabel() == 'card(active) \u2264 1'
assert f2.getValue() == true
assert f2.getChildren().size() == 1

final f21 = f2.getChildren()[0]
assert f21.getLabel() == 'card(active)'
assert f21.getValue() == '0'
assert f21.getChildren().size() == 1

final f211 = f21.getChildren()[0]
assert f211.getLabel() == 'active'
assert f211.getValue() == toUnicode('{}')
assert f211.getChildren() == null

"expanding a B formula works"
