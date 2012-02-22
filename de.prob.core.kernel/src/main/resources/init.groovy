println "Hello from Groovy!!!" 

def classLoader = ClassLoader.systemClassLoader

while (classLoader.parent) {

       classLoader = classLoader.parent

}

console_reader.addCompletor(new org.codehaus.groovy.tools.shell.util.ClassNameCompletor(new GroovyClassLoader()));



//api.b_def()

def s = api.b_def()

println "Goodbye from Groovy!!! allalaxxxx"

