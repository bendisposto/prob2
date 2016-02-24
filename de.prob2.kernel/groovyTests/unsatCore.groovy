import de.prob.animator.domainobjects.*
import de.prob.animator.command.*
import de.prob.statespace.*
import de.prob.animator.*

s = api.b_load(dir+File.separator+"machines"+File.separator+"scheduler.mch") // machine is not needed...

pred = new ClassicalB("x > 7 & 1=4")
c = new UnsatCoreCommand(pred,[])

s.execute(c)
assert("1=4".equals(c.getCore().toString()))

" The UnsatCoreCommand and MinimumUnsatCoreCommand work as expected."