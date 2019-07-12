package de.prob.synthesis;

import de.prob.parser.BindingGenerator;
import de.prob.prolog.term.CompoundPrologTerm;

public class SynthesizedProgram implements BSynthesisResult {

  private final CompoundPrologTerm abstractSyntaxTree;
  private final String prettyPrint;

  public SynthesizedProgram(final CompoundPrologTerm prologTerm) {
    this.abstractSyntaxTree = BindingGenerator.getCompoundTerm(prologTerm.getArgument(1), 3);
    this.prettyPrint =
        BindingGenerator.getCompoundTerm(prologTerm.getArgument(2), 0).toString();
  }

  public CompoundPrologTerm getAbstractSyntaxTree() {
    return abstractSyntaxTree;
  }

  @Override
  public String toString() {
    return prettyPrint;
  }

  @Override
  public boolean isProgram() {
    return true;
  }

  @Override
  public boolean isDistinguishingExample() {
    return false;
  }
}
