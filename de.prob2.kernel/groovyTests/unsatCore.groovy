import de.prob.animator.command.UnsatCoreCommand
import de.prob.animator.domainobjects.ClassicalB

final s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch") // machine is not needed...

final pred = new ClassicalB("x > 7 & 1=4")
final c = new UnsatCoreCommand(pred,[])

s.execute(c)
assert("1=4".equals(c.getCore().toString()))

" The UnsatCoreCommand and MinimumUnsatCoreCommand work as expected."
