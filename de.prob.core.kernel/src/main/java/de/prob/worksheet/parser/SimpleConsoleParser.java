package de.prob.worksheet.parser;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.worksheet.api.evalStore.EvalStoreAPI;

@SuppressWarnings("rawtypes")
public class SimpleConsoleParser {
	private static final Class[] apis = new Class[] { EvalStoreAPI.class };

	public TreeMap<String, Class> apiMethodNamesMap = new TreeMap<String, Class>();

	private final Logger logger = LoggerFactory
			.getLogger(SimpleConsoleParser.class);

	// public String[] worksheetAPImethodNames;
	// public String[] animatorCommands;

	public SimpleConsoleParser() {
		// worksheetAPImethodNames = ClassicalBWorksheetParser
		// .initialGetWorksheetApiMethodNames();
		// animatorCommands = new String[] { "GetErrorsCommand" };
		init();
	}

	public static String[] initialGetWorksheetApiMethodNames() {
		final Method[] methods = EvalStoreAPI.class.getMethods();
		final String[] methodNames = new String[methods.length];

		for (int x = 0; x < methods.length; x++) {
			methodNames[x] = methods[x].getName();
		}
		Arrays.sort(methodNames);
		return methodNames;
	}

	public void init() {
		for (final Class api : SimpleConsoleParser.apis) {
			apiMethodNamesMap.putAll(SimpleConsoleParser
					.getPublicMethodNamesMap(api));
		}
	}

	public static Map<String, Class> getPublicMethodNamesMap(final Class api) {
		final Method[] methods = api.getMethods();
		final TreeMap<String, Class> methodNames = new TreeMap<String, Class>();
		// String[] methodNames = new String[methods.length];

		for (final Method method : methods) {
			if (Modifier.isPublic(method.getModifiers())) {
				methodNames.put(method.getName(), api);
			}
		}

		return methodNames;
	}

	public static List<String> getPublicMethodNames(final Class api) {
		final Method[] methods = api.getMethods();
		final ArrayList<String> methodNames = new ArrayList<String>();
		// String[] methodNames = new String[methods.length];

		for (final Method method : methods) {
			if (Modifier.isPublic(method.getModifiers())) {
				methodNames.add(method.getName());
			}
		}

		return methodNames;
	}

	public class EvalObject {
		public Object methodInstance;
		public String[] method;

		@Override
		public String toString() {
			String res = "";
			if (methodInstance != null)
				res += methodInstance.toString() + " ";
			res += "params:" + Arrays.toString(method);
			return res;
		}
	}

	public EvalObject[] parse(final String code) {
		final String[] expressions = splitToExpressions(code);
		final String[][] methods = expressionsToMethods(expressions);
		final EvalObject[] evalObjects = methodsToEvalObjects(methods);
		return evalObjects;
	}

	public String[] splitToExpressions(final String code) {
		if (code == null)
			return new String[] {};
		final String[] expressions = code.split("\\n|\\r|\\r\\n");
		for (int x = 0; x < expressions.length; x++) {
			expressions[x] = expressions[x].trim();
		}
		return expressions;
	}

	@SuppressWarnings("unchecked")
	public EvalObject[] methodsToEvalObjects(final String[][] methods) {
		final List<EvalObject> evalObjects = new ArrayList<EvalObject>();
		for (final String[] method : methods) {
			Method methodInstance = null;
			try {
				if (method.length > 0
						&& apiMethodNamesMap.containsKey(method[0])) {
					final Class api = apiMethodNamesMap.get(method[0]);
					methodInstance = api.getMethod(method[0],
							toTypeArrayMinusFirst(method));
				} else {
					// TODO check if something needs to be done when
					// method.length=0
				}
			} catch (final NoSuchMethodException e) {
				logger.error(
						"String seems to be an WorksheetApi method but isn't found with this parameters!",
						e);
			} catch (final SecurityException e) {
				logger.error(e.getMessage(), e);
			}
			if (methodInstance != null) {
				final EvalObject newEval = new EvalObject();
				newEval.method = method;
				newEval.methodInstance = methodInstance;
				evalObjects.add(newEval);
			} else {
				final EvalObject newEval = new EvalObject();
				newEval.method = method;
				newEval.methodInstance = null;
				evalObjects.add(newEval);
			}
		}
		return evalObjects.toArray(new EvalObject[evalObjects.size()]);
	}

	public String[][] expressionsToMethods(final String[] expressions) {
		final List<String[]> methods = new ArrayList<String[]>();
		for (final String expression : expressions) {
			if (expression.equals("")) {
				continue;
			}
			final String command = getCommand(expression);
			if (apiMethodNamesMap.containsKey(command)) {
				// its an ApiCommand
				methods.add(parseApiMethod(expression));

			} else {
				// FIXME is unknown at the moment i say it is a eval expression
				methods.add(new String[] { "evaluate", expression });
			}
		}
		return methods.toArray(new String[methods.size()][]);
	}

	private Class[] toTypeArrayMinusFirst(final Object[] array) {
		return toTypeArray(Arrays.copyOfRange(array, 1, array.length));
	}

	private Class[] toTypeArray(final Object[] array) {
		final Class[] res = new Class[array.length];
		for (int x = 0; x < array.length; x++) {
			res[x] = array[x].getClass();
		}
		return res;
	}

	public String getCommand(String expression) {
		expression = expression.trim();
		final int end = expression.indexOf("(");
		if (end != -1)
			return expression.substring(0, end).trim();
		return expression.trim();
	}

	public String[] parseApiMethod(String expression) {
		expression = expression.trim();
		final Pattern reg = Pattern
				.compile("^([a-zA-Z_$][a-zA-Z_0-9$]*)\\s*(\\((.*)\\)$)?");
		final Matcher match = reg.matcher(expression);
		final ArrayList<String> parts = new ArrayList<String>();
		String[] args = null;
		if (match.matches()) {

			if (match.group(1) != null) {
				parts.add(match.group(1).trim());
			}
			if (match.group(3) != null) {
				args = splitArgs(match.group(3).trim());
				if (args != null) {
					parts.addAll(Arrays.asList(args));
				}
			}
		}
		return parts.toArray(new String[parts.size()]);
	}

	public String[] splitArgs(final String argString) {
		if (argString.length() > 0) {
			final ArrayList<String> args = new ArrayList<String>();
			int depth = 0;
			boolean inStr = false;
			boolean inStr2 = false;
			String arg = "";
			for (int x = 0; x < argString.length(); x++) {
				if (x == 0 || (x > 0 && argString.charAt(x - 1) != '\\')) {
					if (argString.charAt(x) == '(') {
						depth++;
					} else if (argString.charAt(x) == '{') {
						depth++;
					} else if (argString.charAt(x) == '[') {
						depth++;
					} else if (argString.charAt(x) == '"' && !inStr && !inStr2) {
						inStr = true;
					} else if (argString.charAt(x) == '\'' && !inStr && !inStr2) {
						inStr2 = true;
					} else if (argString.charAt(x) == ')') {
						depth--;
					} else if (argString.charAt(x) == '}') {
						depth--;
					} else if (argString.charAt(x) == ']') {
						depth--;
					} else if (argString.charAt(x) == '"' && inStr) {
						inStr = false;
					} else if (argString.charAt(x) == '\'' && inStr2) {
						inStr2 = false;
					}
				}

				if (argString.charAt(x) == ',' && !inStr && !inStr2
						&& depth == 0) {
					args.add(arg);
					arg = new String("");
				} else {
					arg += argString.charAt(x);
				}
			}
			args.add(arg);
			final String[] sArgs = args.toArray(new String[args.size()]);
			for (int x = 0; x < sArgs.length; x++) {
				sArgs[x] = sArgs[x].trim();
				if (sArgs[x].charAt(0) == '"'
						&& sArgs[x].charAt(sArgs[x].length() - 1) == '"') {
					sArgs[x] = sArgs[x].substring(1, sArgs[x].length() - 1);
				}

			}
			return sArgs;
		}
		return null;
	}
}
