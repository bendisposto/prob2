package de.prob.webconsole.servlets;

import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.codehaus.groovy.runtime.InvokerHelper

import com.google.gson.Gson
import com.google.inject.Inject
import com.google.inject.Singleton

import de.prob.webconsole.GroovyExecution

@Singleton
public class CompletionServlet extends HttpServlet {

	private final GroovyExecution executor;

	@Inject
	public CompletionServlet(GroovyExecution executor) {
		this.executor = executor;
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		String fulltext = req.getParameter("input");
		String col = req.getParameter("col");

		int c = Integer.parseInt(col)-1;
		int b = c > -1 ? fulltext.lastIndexOf(" ", c) : - 1;

				String begin = b > -1 ? fulltext[0..b] : ""
		String input;
		if (c == -1 || c == b)
		input = ""
		else
		input = b > 0 ? fulltext[b+1..c] : fulltext[0..c]
		String rest = c >= fulltext.length() - 1 ? "" : fulltext[(c+1)..-1]

		ArrayList<String> completions = new ArrayList<String>();
		if (input.contains(".")) {
			int pos = input.lastIndexOf(".")
			String sub = input.substring(0, pos + 1)
			String other = input.substring(pos + 1, input.length())
			def computed = computeCompletions(sub)
			completions = camelMatch(computed, other)
		} else if (!input.isEmpty()) {
			def computed = computeCompletions(new String(input.charAt(0)))
			completions = camelMatch(computed, input);
		}
		
		String pre = getCommonPrefix(completions);
		if (!pre.isEmpty() && pre != input && !input.contains("."))
		completions = [begin + pre + rest]
		else if (completions.size() == 1) {
			if (input.contains('.')) {
				String sub = input.substring(0, input.lastIndexOf(".") + 1)
				begin = begin + sub;
			}
			completions = completions.collect {begin + it + rest}
		}
		else if (input.contains(".") && !pre.isEmpty()) {
			// situation: "xyz.abc"
			int pos = input.lastIndexOf(".")
			String sub = input.substring(0, pos + 1)
			String other = input.substring(pos + 1, input.length())
			if (pos < input.length() && other != pre)
			completions = [begin + sub + pre + rest]
		}

		Gson g = new Gson();
		String json = g.toJson(completions);
		out.println(json);
		out.close();
	}

	private List<String> camelMatch(final List<String> completions, final String match) {
		if (match.isEmpty())
			return completions
		String nopar = match.charAt(match.length() - 1) == '(' ?
							match.substring(0, match.length() - 1) : match;
		List<String> split = camelSplit(nopar);
		StringBuffer sb = new StringBuffer()
		for (String str : split) {
			sb.append(str + "[a-z]*")
		}
		sb.append(".*")
		String regex = sb.toString()
		
		ArrayList<String> matches = new ArrayList()
		for (String str : completions) {
			if (str ==~ regex) {
				matches.add(str)
			}
		}
		return matches
	}
			
	private List<String> camelSplit(final String input) {
		final ArrayList<String> camel = new ArrayList<String>();
		if (input.isEmpty())
			return camel;
		int last = 0
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i).isUpperCase()) {
				camel.add(input.substring(last, i))
				last = i
			}
		}
		camel.add(input.substring(last, input.length()))
		return camel;
	}
			
	private String getCommonPrefix(ArrayList<String> input) {
		if (input.isEmpty())
			return ""
		def min = input.get(0).length()
		def first = input.get(0)
		for (String str : input) {
			for (int i = min; i >= 0; i--) {
				if (i > str.length())
					i = str.length();
				def pre = str.substring(0, i)
				if (first.startsWith(pre)) {
					min = i;
					break;
				}
			}
		}
		
		return first.substring(0, min)
	}

	private ArrayList<String> computeCompletions(String input) {
		ArrayList<String> candidates = new ArrayList<String>();
		def cursor = input.length();
		int identifierStart = findIdentifierStart(input, cursor)
		String identifierPrefix = identifierStart != -1 ? input.substring(identifierStart, cursor) : ""
		int lastDot = input.lastIndexOf('.')

		if (lastDot == -1 || noDotsBeforeParentheses(input, cursor) ) {
			if (identifierStart != -1) {
				List myCandidates = findMatchingVariables(identifierPrefix)
				if (myCandidates.size() > 0) {
				    def prefix = input.substring(0, identifierStart);
					candidates.addAll(myCandidates.collect {
						prefix+it
					})
					return candidates
				}
			}
			return []
		}
		
		else {
			if (lastDot == cursor-1 || identifierStart != -1){
				int predecessorStart=findIdentifierStart(input,lastDot)

				if(predecessorStart!=-1){
					String instanceRefExpression = input.substring(predecessorStart, lastDot)
					if(executor.getBindings().getVariables().keySet().contains(instanceRefExpression)) {
						def instance = executor.tryevaluate(instanceRefExpression);
						if (instance != null) {
							// look for public methods/fields that match the prefix
							List myCandidates = getPublicFieldsAndMethods(instance, identifierPrefix)
							if (myCandidates.size() > 1) {
								candidates.addAll(myCandidates)
							}
							else if (myCandidates.size() == 1) {
								def prefix = input.substring(0, identifierStart);
								candidates.add(prefix+myCandidates[0])
							}
							return candidates;
						}
					}
				}
			}

			return []
		}
		return candidates;
	}
	
	List findMatchingVariables(String prefix) {
		def matches = []
		def vars = executor.getBindings().getVariables().keySet();
		for (String varName in vars)
			if (varName.startsWith(prefix))
				matches << varName
		return matches
	}
	
	
	Boolean noDotsBeforeParentheses(String buffer, int endingAt) {
		int lastDotIndex = buffer.lastIndexOf('.')
		int lastParanIndex = buffer.lastIndexOf('(')

		if(lastDotIndex==-1&&lastParanIndex==-1)
			return true;
		return lastDotIndex < lastParanIndex
	}

	int findIdentifierStart(String buffer, int endingAt) {
		// if the string is empty then there is no expression
		if (endingAt == 0)
			return -1
		// if the last character is not valid then there is no expression
		char lastChar = buffer.charAt(endingAt-1)
		if (!Character.isJavaIdentifierPart(lastChar) )
			return -1
		// scan backwards until the beginning of the expression is found
		int startIndex = endingAt-1
		while (startIndex > 0 && Character.isJavaIdentifierPart(buffer.charAt(startIndex-1)))
			--startIndex
		return startIndex
	}

	List getPublicFieldsAndMethods(Object instance, String prefix) {
		def rv = []
		def instanceClass = instance.getClass()
		instanceClass.fields.each {
			if (it.name.startsWith(prefix))
				rv << it.name
		}
		instanceClass.methods.each {
			if (it.name.startsWith(prefix))
				rv << it.name + (it.parameterTypes.length == 0 ? "()" : "(")
		}
		InvokerHelper.getMetaClass(instance).metaMethods.each {
			if (it.name.startsWith(prefix))
				rv << it.name + (it.parameterTypes.length == 0 ? "()" : "(")
		}
		return rv.sort().unique()
	}

}