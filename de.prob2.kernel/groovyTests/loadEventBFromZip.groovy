import java.nio.file.Paths

import de.prob.statespace.Trace

final factory = api.modelFactoryProvider.eventBFactory

final m = factory.extractModelFromZip(Paths.get(dir, "machines", "Farmer.zip").toString())
final s1 = m.load(m.MFarmer)
def t1 = s1 as Trace
t1.$setup_constants().$initialise_machine().randomAnimation(5)

final s2 = m.load(m.CFarmer)
def t2 = s2 as Trace
t2 = t2.$setup_constants()

"a model can be loaded from a zip file"
