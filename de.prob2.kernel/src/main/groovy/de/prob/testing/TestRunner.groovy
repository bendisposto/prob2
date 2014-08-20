package de.prob.testing

import groovy.io.FileType

import javax.script.ScriptEngine

import org.junit.runner.JUnitCore
import org.junit.runner.Result
import org.junit.runner.notification.RunListener

import com.google.inject.Inject
import com.google.inject.Singleton

@Singleton
class TestRunner {
	def ScriptEngine executor
	def RunListener listener

	@Inject
	def TestRunner(ProBTestListener listener) {
		this.listener = listener
	}

	def Result runTests(pathToDir) {
		def tests = []
		new File(pathToDir).eachFile(FileType.FILES, {
			if (it.getName().endsWith(".groovy")) tests << it.getText()
		})
		def classes = []
		tests.each {
			def clazz = getTestClass(it)
			if(clazz) {
				classes << clazz.getClass()
			}
		}
		doRun(classes as Class<?>[])
	}


	def Result doRun(classes) {
		JUnitCore jUnitCore = new JUnitCore();
		jUnitCore.addListener(listener);
		Result result = jUnitCore.run(classes);
		return result;
	}

	def getTestClass(final String test) {
		def clazz = ""
		try {
			clazz = executor.eval(test)
		} catch (Throwable e) {
			e.printStackTrace() // won't happen in regular mode
		}
		return clazz
	}
}
