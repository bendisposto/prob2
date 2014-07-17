import de.prob.animator.command.EvalstoreCreateByStateCommand;
import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
m = api.b_load(dir+"/machines/scheduler.mch")
s = m as StateSpace

EvalstoreCreateByStateCommand c = new EvalstoreCreateByStateCommand(
	"root");
s.execute(c);
long store = c.getEvalstoreId();
assert store == 1

s.animator.cli.shutdown();
"Eval store MAY be working correctly, but NOBODY knows because who knows how the eval store works actually???"