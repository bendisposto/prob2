package de.prob.testing

import groovy.io.FileType

import com.google.inject.Singleton

@Singleton
class TestRegistry {
	def tests = []
	def ArrayList<ITestsAddedListener> listeners = []

	def refresh() {
		tests = []
	}

	def loadTests(pathToDir) {
		tests = []
		new File(pathToDir).eachFile(FileType.FILES, {
			if (it.getName().endsWith(".groovy")) tests << it.getText()
		})
		listeners.each {
			it.newTests(tests)
		}
		tests
	}

	def registerListener(listener) {
		listeners << listener
	}
}
