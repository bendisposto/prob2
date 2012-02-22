println "Hello from Groovy!!!" 

classLoader = ClassLoader.systemClassLoader

while (classLoader.parent) {  classLoader = classLoader.parent }

console_reader.addCompletor(new org.codehaus.groovy.tools.shell.util.ClassNameCompletor(new GroovyClassLoader()));

s = api.b_def()

explore = s.&explore

