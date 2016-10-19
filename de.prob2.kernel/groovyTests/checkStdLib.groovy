import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.google.inject.Injector;

import de.prob.Main;
import de.prob.animator.domainobjects.*
import de.prob.statespace.*

assertNotNull(System.getProperty("prob.stdlib")); // default folder has been set

s = api.b_load(dir+File.separator+"machines"+File.separator+"LoadStdLibTest.mch")
t = new Trace(s)

"the standard library is present"