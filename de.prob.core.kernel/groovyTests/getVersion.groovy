import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
m = api.b_load(dir+"/machines/scheduler.mch")
s = m as StateSpace

cmd = new GetVersionCommand()
s.execute(cmd)

assert cmd.getMajor() != ""
assert cmd.getMinor() != ""
assert cmd.getService() != ""
assert cmd.getQualifier() != ""
assert cmd.getSvnrevision() != ""
assert cmd.getProloginfo() != ""
assert cmd.getVersion() != null

s.animator.cli.shutdown();
"version accessed successfully"