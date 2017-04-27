import de.prob.animator.prologast.*
import de.prob.animator.command.*
import java.util.List

s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")
assert s != null

c = new GetMachineStructureCommand()
s.execute(c);

List<PrologASTNode> toTest = c.getPrologASTList()

assert "SETS" == toTest.get(0).getName()

assert "VARIABLES" == toTest.get(1).getName()
assert true == toTest.get(1).isExpanded()
assert false == toTest.get(1).getSubnodes().isEmpty()
List<PrologASTNode> subnodesVar = toTest.get(1).getSubnodes()
assert "\n[Formula] : formula(b(identifier(active),set(global('PID')),[nodeid(10)]),active)" == subnodesVar.get(0).toString()

assert "INVARIANTS" == toTest.get(2).getName()
assert false == toTest.get(2).isExpanded()
assert false == toTest.get(2).getSubnodes().isEmpty()

assert "OPERATIONS" == toTest.get(3).getName()
assert false == toTest.get(3).isExpanded()
List<PrologASTNode> subnodesOp = toTest.get(3).getSubnodes()
assert true == subnodesOp.get(0).isPropagated()
assert "nr_ready" == subnodesOp.get(0).getName()
assert true == subnodesOp.get(subnodesOp.size()-1).isPropagated()
assert "swap" == subnodesOp.get(subnodesOp.size()-1).getName()

"PrologAST is built properly"