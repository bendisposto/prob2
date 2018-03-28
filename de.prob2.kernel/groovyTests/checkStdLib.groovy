import de.prob.statespace.Trace

import static org.junit.Assert.assertNotNull

assertNotNull(System.getProperty("prob.stdlib")); // default folder has been set

final s = api.b_load(dir+File.separator+"machines"+File.separator+"LoadStdLibTest.mch")
final t = new Trace(s)

"the standard library is present"
