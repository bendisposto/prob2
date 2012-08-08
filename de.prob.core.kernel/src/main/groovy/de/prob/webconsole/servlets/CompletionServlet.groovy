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
		String input = req.getParameter("input");

		ArrayList<String> completions = computeCompletions(input);
		String pre = getCommonPrefix(completions);
		if (!pre.isEmpty() && pre != input)
			completions = [pre]
		Gson g = new Gson();
		String json = g.toJson(completions);
		out.println(json);
		out.close();
	}
			
	private String getCommonPrefix(ArrayList<String> input) {
		if (input.isEmpty())
			return ""
		def min = input.get(0).length()
		def first = input.get(0)
		for (String str : input) {
			for (int i = min; i > 0; i--) {
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
		instance.class.fields.each {
			if (it.name.startsWith(prefix))
				rv << it.name
		}
		instance.class.methods.each {
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