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
		new File(pathToDir).eachFile(FileType.FILES, {
			tests << it.getText()
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
