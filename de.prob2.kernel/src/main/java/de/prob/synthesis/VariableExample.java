package de.prob.synthesis;

import de.be4.classicalb.core.parser.ClassicalBParser;
import de.prob.parser.BindingGenerator;
import de.prob.parserbase.ProBParseException;
import de.prob.parserbase.ProBParserBaseAdapter;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VariableExample {

  private final Logger logger = LoggerFactory.getLogger(VariableExample.class);

  private final ProBParserBaseAdapter bParser =
      new ProBParserBaseAdapter(new ClassicalBParser());

  private final String name;
  private final String value;

  public VariableExample(final String name,
                         final String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  /**
   * A single variable example is represented as a tuple of variable name and parsed value in
   * Prolog. The value will be typechecked before running synthesis.
   */
  void appendToPrologTerm(final IPrologTermOutput prologTerm) {
    try {
      prologTerm.openTerm(",")
          .printAtom(name)
          .printTerm(bParser.parseExpression(value, false)).closeTerm();
    } catch (final ProBParseException parseException) {
      logger.error("Error when parsing synthesis example value.", parseException);
    }
  }

  static VariableExample fromPrologTerm(final PrologTerm prologTerm) {
    final CompoundPrologTerm compoundPrologTerm =
        BindingGenerator.getCompoundTerm(prologTerm, 2);
    return new VariableExample(compoundPrologTerm.getArgument(1).toString(),
        compoundPrologTerm.getArgument(2).toString());
  }
}
