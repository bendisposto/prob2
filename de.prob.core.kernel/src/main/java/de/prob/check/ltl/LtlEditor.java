package de.prob.check.ltl;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import de.prob.ltl.parser.LtlBaseListener;
import de.prob.ltl.parser.LtlParser;
import de.prob.ltl.parser.LtlParser.AndExprContext;
import de.prob.ltl.parser.LtlParser.ExprContext;
import de.prob.ltl.parser.LtlParser.FinallyExprContext;
import de.prob.ltl.parser.LtlParser.GloballyExprContext;
import de.prob.ltl.parser.LtlParser.HistoricallyExprContext;
import de.prob.ltl.parser.LtlParser.ImpliesExprContext;
import de.prob.ltl.parser.LtlParser.NextExprContext;
import de.prob.ltl.parser.LtlParser.NotExprContext;
import de.prob.ltl.parser.LtlParser.OnceExprContext;
import de.prob.ltl.parser.LtlParser.OrExprContext;
import de.prob.ltl.parser.LtlParser.ReleaseExprContext;
import de.prob.ltl.parser.LtlParser.SinceExprContext;
import de.prob.ltl.parser.LtlParser.TriggerExprContext;
import de.prob.ltl.parser.LtlParser.UnaryCombinedExprContext;
import de.prob.ltl.parser.LtlParser.UntilExprContext;
import de.prob.ltl.parser.LtlParser.WeakuntilExprContext;
import de.prob.ltl.parser.LtlParser.YesterdayExprContext;
import de.prob.ltl.parser.pattern.PatternManager;
import de.prob.ltl.parser.semantic.PatternDefinition;
import de.prob.web.AbstractSession;
import de.prob.web.WebUtils;

@Singleton
public class LtlEditor extends AbstractSession {

	public final String USER_PATTERNS_FILE = "modelcheck/user_patterns.ltlp";

	private final Logger logger = LoggerFactory.getLogger(LtlEditor.class);
	private List<Expression> expressions = new LinkedList<Expression>();
	private Map<String, Expression> expressionMap = new HashMap<String, Expression>();
	protected final PatternManager patternManager;

	private final String[] KEYWORDS = {
			"def", "var", "seq", "num",
			"count", "up", "down", "to", "end",
			"without",
	};

	private final String[] SCOPES = {
			"before", "after", "between", "after_until"
	};

	private final String[] BOOLEAN = {
			"not", "and", "or", "=>"
	};

	private final String[] LTL_ATOMS = {
			"true", "false", "sink", "deadlock", "current"
	};

	private final String[] LTL_OPERATORS = {
			"G", "F", "X", "H", "O", "Y",
			"U", "R", "W", "S", "T"
	};

	public LtlEditor() {
		this(UUID.randomUUID());
	}

	public LtlEditor(UUID id) {
		super(id);

		patternManager = new PatternManager();
		try {
			patternManager.loadPatternsFromFile(USER_PATTERNS_FILE);
		} catch (IOException e) {
		}
	}

	@Override
	public String html(String clientid, Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/ltl/editor/editor.html");
	}

	public Object parseFormula(Map<String, String[]> params) {
		logger.trace("Parse ltl formula");
		String input = get(params, "input");
		String callback = get(params, "callbackObj");

		ParseListener listener = new ParseListener();
		LtlParser parser = parse(input, listener);
		List<Marker> markers = getPatternMarkers(parser.getSymbolTableManager().getPatternDefinitions());

		Map<String, String> result = null;
		if (listener.getErrorMarkers().size() == 0) {
			logger.trace("Parse ok (errors: 0, warnings: {}). Submitting parse results", listener.getWarningMarkers().size());
			result = WebUtils.wrap(
					"cmd", callback + ".parseOk",
					"warnings", WebUtils.toJson(listener.getWarningMarkers()),
					"markers", WebUtils.toJson(markers));
		} else {
			logger.trace("Parse failed (errors: {}, warnings: {}). Submitting parse results", listener.getErrorMarkers().size(), listener.getWarningMarkers().size());
			result = WebUtils.wrap(
					"cmd", callback + ".parseFailed",
					"warnings", WebUtils.toJson(listener.getWarningMarkers()),
					"errors", WebUtils.toJson(listener.getErrorMarkers()),
					"markers", WebUtils.toJson(markers));
		}

		return result;
	}

	public Object parsePattern(Map<String, String[]> params) {
		logger.trace("Parse ltl pattern");
		String input = get(params, "input");
		String ignorePatternName = get(params, "ignorePatternName");
		String callback = get(params, "callbackObj");

		ParseListener listener = new ParseListener();
		patternManager.addIgnorePattern(ignorePatternName);
		parsePatternDefinition(input, listener);
		patternManager.clearIgnorePatterns();

		Map<String, String> result = null;
		if (listener.getErrorMarkers().size() == 0) {
			logger.trace("Parse ok (errors: 0, warnings: {}). Submitting parse results", listener.getWarningMarkers().size());
			result = WebUtils.wrap(
					"cmd", callback + ".parseOk",
					"warnings", WebUtils.toJson(listener.getWarningMarkers()),
					"markers", "");
		} else {
			logger.trace("Parse failed (errors: {}, warnings: {}). Submitting parse results", listener.getErrorMarkers().size(), listener.getWarningMarkers().size());
			result = WebUtils.wrap(
					"cmd", callback + ".parseFailed",
					"warnings", WebUtils.toJson(listener.getWarningMarkers()),
					"errors", WebUtils.toJson(listener.getErrorMarkers()),
					"markers", "");
		}

		return result;
	}

	public Object getOperatorAtPosition(Map<String, String[]> params) {
		logger.trace("Get operator at the passed position");

		String key = get(params, "pos");
		String callback = get(params, "callbackObj");
		Expression ex = expressionMap.get(key);

		Map<String, String> result = null;
		if (ex == null) {
			result = WebUtils.wrap(
					"cmd", callback + ".noExpressionFound");
		}else {
			result = WebUtils.wrap(
					"cmd", callback + ".expressionFound",
					"expression", WebUtils.toJson(ex));
		}
		return result;
	}

	public Object getAutoCompleteList(Map<String, String[]> params) {
		logger.trace("Get auto complete list at the passed position");

		//String line = get(params, "line");
		//String ch = get(params, "ch");
		String startsWith = get(params, "startsWith");
		String input = get(params, "input");
		String callback = get(params, "callbackObj");

		return WebUtils.wrap(
				"cmd", callback + ".showHint",
				"hints", WebUtils.toJson(getCompletionList(input, startsWith)));
	}

	public List<Marker> getPatternMarkers(List<PatternDefinition> patterns) {
		List<Marker> markers = new LinkedList<Marker>();
		for (PatternDefinition pattern : patterns) {
			String msg = String.format("Move pattern '%s' to pattern manager.", pattern.getName());
			markers.add(new Marker("pattern", pattern.getContext().start, pattern.getContext().stop, pattern.getSimpleName(), msg));
		}
		return markers;
	}

	private List<Hint> getCompletionList(String input, String startsWith) {
		// Keywords
		List<Hint> hints = new LinkedList<Hint>();

		addHint(hints, "keyword", KEYWORDS);
		addHint(hints, "scope", SCOPES);
		addHint(hints, "boolean", BOOLEAN);
		addHint(hints, "atom", LTL_ATOMS);
		addHint(hints, "operator", LTL_OPERATORS);

		LtlParser parser = new LtlParser(input);
		parser.removeErrorListeners();
		parser.setPatternManager(patternManager);
		parser.parse();

		// Add patterns
		List<PatternDefinition> patterns = parser.getSymbolTableManager().getAllPatternDefinitions();
		for (PatternDefinition pattern : patterns) {
			String name = pattern.getSimpleName();
			Hint hint = new Hint(name, "pattern");
			if (!hints.contains(hint)) {
				hints.add(hint);
			}
		}
		// TODO add vars

		// Remove words that do not start with 'startsWith'
		Iterator<Hint> it = hints.iterator();
		while (it.hasNext()) {
			if (!it.next().getText().startsWith(startsWith)) {
				it.remove();
			}
		}

		Collections.sort(hints);

		return hints;
	}

	private void addHint(List<Hint> hints, String type, String[] words) {
		for (String word : words) {
			Hint hint = new Hint(word, type);
			if (!hints.contains(hint)) {
				hints.add(hint);
			}
		}
	}

	protected LtlParser parse(String input, ParseListener listener) {
		LtlParser parser = new LtlParser(input);
		parser.removeErrorListeners();
		parser.addErrorListener(listener);
		parser.addWarningListener(listener);
		parser.setPatternManager(patternManager);

		parser.parse();

		astChanged(parser.getAst());
		return parser;
	}

	protected LtlParser parsePatternDefinition(String input, ParseListener listener) {
		LtlParser parser = new LtlParser(input);
		parser.removeErrorListeners();
		parser.addErrorListener(listener);
		parser.addWarningListener(listener);
		parser.setPatternManager(patternManager);

		parser.parsePatternDefinition();

		astChanged(parser.getAst());
		return parser;
	}

	private void astChanged(ParseTree ast) {
		logger.trace("AST has changed. Determine expressions");

		expressions.clear();
		expressionMap.clear();

		ParseTreeWalker.DEFAULT.walk(new LtlBaseListener() {

			@Override
			public void enterAndExpr(AndExprContext ctx) {
				addExpression(new Expression(createMark(ctx.AND()), createMark(ctx.expr())));
			}

			@Override
			public void enterFinallyExpr(FinallyExprContext ctx) {
				addExpression(new Expression(createMark(ctx.FINALLY()), createMark(ctx.expr())));
			}

			@Override
			public void enterGloballyExpr(GloballyExprContext ctx) {
				addExpression(new Expression(createMark(ctx.GLOBALLY()), createMark(ctx.expr())));
			}

			@Override
			public void enterHistoricallyExpr(HistoricallyExprContext ctx) {
				addExpression(new Expression(createMark(ctx.HISTORICALLY()), createMark(ctx.expr())));
			}

			@Override
			public void enterImpliesExpr(ImpliesExprContext ctx) {
				addExpression(new Expression(createMark(ctx.IMPLIES()), createMark(ctx.expr())));
			}

			@Override
			public void enterNextExpr(NextExprContext ctx) {
				addExpression(new Expression(createMark(ctx.NEXT()), createMark(ctx.expr())));
			}

			@Override
			public void enterNotExpr(NotExprContext ctx) {
				addExpression(new Expression(createMark(ctx.NOT()), createMark(ctx.expr())));
			}

			@Override
			public void enterOnceExpr(OnceExprContext ctx) {
				addExpression(new Expression(createMark(ctx.ONCE()), createMark(ctx.expr())));
			}

			@Override
			public void enterOrExpr(OrExprContext ctx) {
				addExpression(new Expression(createMark(ctx.OR()), createMark(ctx.expr())));
			}

			@Override
			public void enterReleaseExpr(ReleaseExprContext ctx) {
				addExpression(new Expression(createMark(ctx.RELEASE()), createMark(ctx.expr())));
			}

			@Override
			public void enterSinceExpr(SinceExprContext ctx) {
				addExpression(new Expression(createMark(ctx.SINCE()), createMark(ctx.expr())));
			}

			@Override
			public void enterTriggerExpr(TriggerExprContext ctx) {
				addExpression(new Expression(createMark(ctx.TRIGGER()), createMark(ctx.expr())));
			}

			@Override
			public void enterUntilExpr(UntilExprContext ctx) {
				addExpression(new Expression(createMark(ctx.UNTIL()), createMark(ctx.expr())));
			}

			@Override
			public void enterWeakuntilExpr(WeakuntilExprContext ctx) {
				addExpression(new Expression(createMark(ctx.WEAKUNTIL()), createMark(ctx.expr())));
			}

			@Override
			public void enterYesterdayExpr(YesterdayExprContext ctx) {
				addExpression(new Expression(createMark(ctx.YESTERDAY()), createMark(ctx.expr())));
			}

			@Override
			public void enterUnaryCombinedExpr(UnaryCombinedExprContext ctx) {
				addExpression(new Expression(createMark(ctx.UNARY_COMBINED()), createMark(ctx.expr())));
			}

		}, ast);
	}

	private void addExpression(Expression ex) {
		expressions.add(ex);

		int line = ex.getOperator().getLine();
		int pos = ex.getOperator().getPos();
		for (int i = 0; i <= ex.getOperator().getLength(); i++) {
			String key = String.format("%d-%d", line, pos + i);
			expressionMap.put(key, ex);
		}
	}

	private Mark createMark(TerminalNode node) {
		Token token = node.getSymbol();
		int length = token.getStopIndex() - token.getStartIndex() + 1;
		return new Mark(token.getLine(), token.getCharPositionInLine(), length);
	}

	private List<Mark> createMark(List<ExprContext> ctxs) {
		List<Mark> marks = new LinkedList<Mark>();
		for (ExprContext ctx : ctxs) {
			marks.add(createMark(ctx));
		}
		return marks;
	}

	private Mark createMark(ExprContext ctx) {
		int length = ctx.stop.getStopIndex() - ctx.start.getStartIndex() + 1;
		return new Mark(ctx.start.getLine(), ctx.start.getCharPositionInLine(), length);
	}

}
