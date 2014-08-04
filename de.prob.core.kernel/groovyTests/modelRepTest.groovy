import java.security.cert.X509Certificate;

import de.prob.model.representation.*

m = api.b_load(dir+"/machines/scheduler.mch")
x = ModelRep.translate(m)
assert x.size() == 1
scheduler = x[0]
assert scheduler.label == "scheduler"
assert scheduler.children.size() == 3