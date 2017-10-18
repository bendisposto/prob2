import de.prob.animator.domainobjects.*
import de.prob.statespace.*

// You can change the model you are testing here.
factory = api.modelFactoryProvider.eventBFactory

m = factory.extractModelFromZip(dir + File.separator + "machines" + File.separator + "Farmer.zip")
s = m.load(m.MFarmer)
t = s as Trace
t.$setup_constants().$initialise_machine().randomAnimation(5)

s = m.load(m.CFarmer)
t = s as Trace
t = t.$setup_constants()

"a model can be loaded from a zip file"