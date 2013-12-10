package de.prob.check.ltl;

import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.ltl.parser.WarningListener;

public class ParseListener extends BaseErrorListener implements WarningListener {

	private final Logger logger = LoggerFactory.getLogger(ParseListener.class);

	private List<Marker> warningMarkers = new LinkedList<Marker>();
	private List<Marker> errorMarkers = new LinkedList<Marker>();

	@Override
	public void warning(Token token, String msg) {
		int length = token.getStopIndex() - token.getStartIndex() + 1;
		warningMarkers.add(new Marker("warning", token.getLine(), token.getCharPositionInLine(), length, msg));
	}

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer,
			Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException e) {
		int length = 1;
		if (offendingSymbol != null && offendingSymbol instanceof Token) {
			Token token = (Token) offendingSymbol;
			length = token.getStopIndex() - token.getStartIndex() + 1;
		}
		errorMarkers.add(new Marker("error", line, charPositionInLine, length, msg));
		logger.trace("Parse error {}", offendingSymbol);
	}

	public List<Marker> getWarningMarkers() {
		return warningMarkers;
	}

	public List<Marker> getErrorMarkers() {
		return errorMarkers;
	}

}