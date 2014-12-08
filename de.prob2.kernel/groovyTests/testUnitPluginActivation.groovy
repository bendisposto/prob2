import de.prob.statespace.*
import de.prob.animator.command.*
import de.prob.parser.*
import de.prob.prolog.term.*


s = api.b_load(dir+File.separator+"machines"+File.separator+"Empty.mch") as StateSpace
thrown = false
try {
	cmd = new GetPluginResultCommand("Grounded Result State")
	s.execute(cmd)
	assert cmd.getResult() == ""
} catch(ResultParserException e) {
	thrown = true
}
assert thrown

s.execute(new ActivateUnitPluginCommand())

cmd = new GetPluginResultCommand("Grounded Result State")
s.execute(cmd)
res = cmd.getResult()
assert res.functor == "state"
arg1 = res.getArgument(1)
assert arg1 instanceof ListPrologTerm
assert arg1.isEmpty()

s.animator.cli.shutdown();
"Activating and accessing unit plugin successful"