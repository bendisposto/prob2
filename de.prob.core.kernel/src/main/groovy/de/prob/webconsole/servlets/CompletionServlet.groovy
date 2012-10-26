package de.prob.webconsole.servlets;

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.Executors
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.codehaus.groovy.runtime.InvokerHelper

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import com.google.gson.Gson
import com.google.inject.Inject
import com.google.inject.Singleton

import de.prob.webconsole.GroovyExecution
import de.prob.webconsole.ShellCommands;

@Singleton
public class CompletionServlet extends HttpServlet {

	private final GroovyExecution executor;
	private ShellCommands shellCommands;
	
	private Multimap<String, String> clazzes = new ArrayListMultimap<String, String>()

	@Inject
	public CompletionServlet(GroovyExecution executor, ShellCommands shellCommands) {
		this.shellCommands = shellCommands;
		this.executor = executor;
		initClassNames()
	}
	
	def initClassNames() {
		Properties p = new Properties()
		def rs = this.getClass().getClassLoader()
		    .getResourceAsStream("classmap.properties");
		p.load(rs)

		for (String fqn : p.keySet()) {
			def pack = p.get(fqn)
			def cn = fqn.substring(pack.length() + 1)
			clazzes.put(cn, fqn)
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		String fulltext = req.getParameter("input");
		String col = req.getParameter("col");

		List<String> completions = getCompletions(col, fulltext)
		Gson g = new Gson();		
		
		String json = g.toJson(completions);
		out.println(json);
		out.close();
	}
	
	private String[] splitInput(String fulltext, int c) {
		String rest = fulltext.substring(c + 1)
		String front = fulltext.substring(0, c + 1)
		int split = front.lastIndexOf(" ") + 1
		String input = front.substring(split)
		String begin = front.substring(0, split)
			
		String[] arr = [begin, input, rest]
	}

	private List getCompletions(String col, String fulltext) {
		int c = Integer.parseInt(col)-1;

		String[] arr = splitInput(fulltext, c);
		def begin = arr[0]
		def input = arr[1]
		def rest  = arr[2]

		List<String> completions = new ArrayList<String>();
		
		def m = shellCommands.getMagic(fulltext)
		if (!m.isEmpty()) {
			return shellCommands.complete(m, c);
		}
		
		String[] parSplit = resplit(input);
		begin += parSplit[0]
		input = parSplit[1]

		String sub = ""
		String other = input // for matching
		
		// get Bindings
		completions.addAll(findMatchingVariables(input));
		
		if (input.contains(".")) {
			int pos = input.lastIndexOf(".")
			sub = input.substring(0, pos + 1)
			other = input.substring(pos + 1, input.length())
			addMethodsAndFields(completions, sub)
		} else {
			completions.addAll(clazzes.keySet())
		}
		

		if (begin.isEmpty() && other == input) {
			addMagicCommands(completions, shellCommands)
		}
		
		completions = camelMatch(completions, other)

		String pre = getCommonPrefix(completions);
		if (pre.length() >= input.length() && pre != input && pre != other && !pre.isEmpty()) {
			return [begin + sub + pre + rest]
		}
		
		if (clazzes.containsKey(other)) {
			def valueList = clazzes.get(other)
			if (valueList.size() > 1) {
				completions.addAll(valueList)
			}
		}
					
		if (completions.size() == 1) {
				completions = completions.collect {begin + sub + it + rest}
		}
		
		return completions
	}
	
	private String[] resplit(String input) {
		int unmatchedClosingParCount = 0;
		int i = input.length() - 1
		for (; i >= 0 && unmatchedClosingParCount >= 0; i--) {
			if (input.charAt(i) == '(')
				unmatchedClosingParCount--
			else if (input.charAt(i) == ')')
				unmatchedClosingParCount++
		}
		if (i == -1)
			return ["", input] as String[]
		def first = input.substring(0, i+2)
		def second = input.substring(i+2, input.length())
		return [first, second] as String[]
		/*def firstOpenPar = input.indexOf('(')
		def closingPar = input.indexOf(")", firstOpenPar+1)
		while (closingPar > firstOpenPar) {
			firstOpenPar = input.indexOf('(', firstOpenPar+1)
			closingPar = input.indexOf(")", closingPar+1)
		}
		return [input[0..firstOpenPar], input.substring(firstOpenPar+1)] as String[]*/
	}

	private List<String> camelMatch(final List<String> completions, final String match) {
		if (match.isEmpty())
			return completions
		def nopar = match.findAll {  Character.isJavaIdentifierPart(it.charAt(0))  }.join("")			
		def split = camelSplit(nopar);
		def  regex = split.join("[a-z]*") + ".*";
	    completions.findAll { it ==~ regex  }
		
	}
			
	private List<String> camelSplit(final String input) {
		if (input.isEmpty()) return [];	
		input.split("(?=[A-Z])").findAll {it != ""}
	}
	
	private String getCommonPrefix(ArrayList<String> input) {
		if (input.isEmpty())
			return ""
		input.collect { it as List }.transpose().takeWhile { (it as Set).size() == 1 }.collect {it[0]}.join() 
	}

	private ArrayList<String> computeCompletions(String input) {
		ArrayList<String> candidates = new ArrayList<String>();

		String inputWithoutDot = input.substring(0, input.length() - 1)
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		
		def future = executorService.submit({
				executor.tryevaluate(inputWithoutDot)
			} as Callable)
		
		def instance;
		try {
			instance = future.get(3, TimeUnit.SECONDS)
		} catch (Exception e) {
			return []
		}						
		
		return getPublicFieldsAndMethods(instance)
	}
	
	List findMatchingVariables(String prefix) {
		def vars = executor.getBindings().getVariables().keySet() as List;
		vars.findAll {String s -> s.startsWith(prefix) && !s.startsWith("__") && !s.startsWith('this$') }
	}

	List getPublicFieldsAndMethods(Object instance) {
		def rv = []
		def instanceClass = instance.getClass()
		instanceClass.declaredFields.each {
			rv << it.name
		}
		instanceClass.methods.each {
			rv << it.name + (it.parameterTypes.length == 0 ? "()" : "(")
		}
		InvokerHelper.getMetaClass(instance).metaMethods.each {
			rv << it.name + (it.parameterTypes.length == 0 ? "()" : "(")
		}
		return rv.sort().unique()
	}
	
	List<String> addMagicCommands(List<String> completions, ShellCommands cmds) {
		def magicCommands = cmds.getSpecialCommands()
		for (String cmd : magicCommands) {
				completions << cmd + " "
		}
	}
	
	List<String> addMethodsAndFields(List<String> completions, String sub) {
		def list = computeCompletions(sub)
		list.each { completions << it }
	}

}