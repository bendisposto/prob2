import de.prob.animator.command.GetVersionCommand

// You can change the model you are testing here.
final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch")

final cmd = new GetVersionCommand()
s.execute(cmd)

assert cmd.getMajor() != ""
assert cmd.getMinor() != ""
assert cmd.getService() != ""
assert cmd.getQualifier() != ""
assert cmd.getSvnrevision() != ""
assert cmd.getProloginfo() != ""
assert cmd.getVersion() != null

"version accessed successfully"
