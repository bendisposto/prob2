import de.prob.animator.domainobjects.*
import de.prob.statespace.*
import de.prob.animator.command.GetEnableMatrixCommand;

// You can change the model you are testing here.
s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")

p1 = new GetEnableMatrixCommand.EventPair("new","del");
p2 = new GetEnableMatrixCommand.EventPair("del","del");

c = new GetEnableMatrixCommand(p1,p2);

s.execute(c);

x = c.getEnableInfo(p1);
y = c.getEnableInfo(p2);

assert x.enable == "ok"
assert y.enable == "false"
assert y.disable == "ok"

"EnableMatrixTest passed"