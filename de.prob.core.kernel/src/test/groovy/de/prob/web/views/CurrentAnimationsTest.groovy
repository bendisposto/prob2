package de.prob.web.views;


import com.google.inject.Guice;

import de.prob.scripting.Api
import de.prob.Main;
import de.prob.MainModule
import de.prob.statespace.AnimationSelector
import de.prob.statespace.Trace
import de.prob.webconsole.ServletContextListener;
import de.prob.webconsole.WebConsole
import geb.Page
import geb.spock.GebReportingSpec
import geb.spock.GebSpec
import spock.lang.Ignore


class CurrentAnimationsPage extends Page {
	static url = "http://localhost:8080/sessions/CurrentAnimations"
	static at = { title == "ProB Current Trace" }
}

class CurrentTracePage extends Page {
	static url = "http://localhost:8080/sessions/CurrentTrace"
	static at = { title == "ProB Current Trace" }
}

class CurrentAnimationsTest extends GebReportingSpec {
	
	def setupSpec() {
		Runnable r = new Runnable() {
			public void run() {
				WebConsole.run();
			}
		}
		Thread thread = new Thread(r);
		thread.start();
	}

	def "check the CurrentAnimations view with a single trace"() {
		setup:
		def injector = ServletContextListener.INJECTOR;
		Api api = injector.getInstance(Api.class)
		AnimationSelector animations = injector.getInstance(AnimationSelector.class)
		def m = api.b_load("bin/examples/scheduler.mch")
		def t = m as Trace
		t = t.add(0)
		t = t.add(3)
	
		when:
		animations.addNewAnimation(t)

		and:
		to CurrentAnimationsPage
		
		then:
		waitFor { at CurrentAnimationsPage }
		
		and:
		$("td").size() == 4
		$("td", 0).text() == "scheduler"
		$("td", 1).text() == "3=[0,2]"
		$("td", 2).text() == "2"
	}
	
	def "check the CurrentTrace view"() {
		setup:
		def injector = ServletContextListener.INJECTOR;
		Api api = injector.getInstance(Api.class)
		AnimationSelector animations = injector.getInstance(AnimationSelector.class)
		def m = api.b_load("bin/examples/scheduler.mch")
		def t = m as Trace
		t = t.add(0)
		t = t.add(3)
	
		when:
		animations.addNewAnimation(t)
		
		and:
		to CurrentTracePage
		
		then:
		waitFor { at CurrentTracePage }
		
		and:
		$("li").size() == 2
		$("li", 0).text() == "new(PID2)"
		$("li", 1).text() == "\$initialise_machine({},{},{})"
	}
	
//	def "check the CurrentAnimations view with two traces"() {
//		setup:
//		def injector = ServletContextListener.INJECTOR;
//		Api api = injector.getInstance(Api.class)
//		AnimationSelector animations = injector.getInstance(AnimationSelector.class)
//		def m = api.b_load("bin/examples/scheduler.mch")
//		def t = m as Trace
//		t = t.add(0)
//		t = t.add(3)
//		def n = api.b_load("bin/examples/scheduler.mch")
//		def u = n as Trace
//		u = u.add(0)
//		u = u.add(1)
//	
//		when:
//		animations.addNewAnimation(t)
//		animations.addNewAnimation(u)
//
//		and:
//		to CurrentAnimationsPage
//		
//		then:
//		waitFor { at CurrentAnimationsPage }
//		
//		and:
//		$("td").size() == 8
//		$("td", 0).text() == "scheduler"
//		$("td", 1).text() == "3=[0,2]"
//		$("td", 2).text() == "2"
//		$("td", 4).text() == "scheduler"
//		$("td", 5).text() == "1=[0,1]"
//		$("td", 6).text() == "1"
//	}
	
	
	
}
