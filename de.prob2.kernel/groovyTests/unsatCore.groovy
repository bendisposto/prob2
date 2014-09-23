import de.prob.animator.domainobjects.*
import de.prob.animator.command.*
import de.prob.statespace.*
import de.prob.animator.*

IAnimator anim = Main.getInjector().getInstance(
	IAnimator.class);

pred = new ClassicalB("x > 7 & 1=4")
c = new UnsatCoreCommand(pred,[])

anim.execute(c)
assert("1=4".equals(c.getCore().toString()))

" The UnsatCoreCommand and MinimumUnsatCoreCommand work as expected."