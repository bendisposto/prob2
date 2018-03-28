import de.prob.animator.command.ActivateUnitPluginCommand
import de.prob.animator.command.GetPluginResultCommand
import de.prob.exception.ProBError
import de.prob.prolog.term.ListPrologTerm

final s = api.b_load(dir+File.separator+"machines"+File.separator+"Empty.mch") 
def thrown = false
try {
	final cmd1 = new GetPluginResultCommand("Grounded Result State")
	s.execute(cmd1)
	assert cmd1.getResult() == ""
} catch (ProBError e) {
	thrown = true
}
assert thrown

s.execute(new ActivateUnitPluginCommand())

final cmd2 = new GetPluginResultCommand("Grounded Result State")
s.execute(cmd2)
final res = cmd2.getResult()
assert res.functor == "state"
final arg1 = res.getArgument(1)
assert arg1 instanceof ListPrologTerm
assert arg1.isEmpty()

"Activating and accessing unit plugin successful"
