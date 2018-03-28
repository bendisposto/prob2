import de.prob.statespace.Trace

// You can change the model you are testing here.
final factory = api.modelFactoryProvider.eventBFactory

final m = factory.extractModelFromZip(dir + File.separator + "machines" + File.separator + "Farmer.zip")
final s1 = m.load(m.MFarmer)
def t = s1 as Trace
t.$setup_constants().$initialise_machine().randomAnimation(5)

final s2 = m.load(m.CFarmer)
t = s2 as Trace
t = t.$setup_constants()

"a model can be loaded from a zip file"
