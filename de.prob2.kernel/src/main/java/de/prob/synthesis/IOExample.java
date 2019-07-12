package de.prob.synthesis;

import de.prob.prolog.output.IPrologTermOutput;

public class IOExample implements BSynthesisResult {

  // TODO: export to map for interface to neural networks

  private Example input;
  private Example output;

  public IOExample(final Example input,
                   final Example output) {
    this.input = input;
    this.output = output;
  }

  public Example getInput() {
    return input;
  }

  public Example getOutput() {
    return output;
  }

  /**
   * An I/O example is represented as a tuple of {@link Example} in Prolog.
   */
  void appendToPrologTerm(final IPrologTermOutput prologTerm) {
    prologTerm.openTerm(",");
    input.appendToPrologTerm(prologTerm);
    output.appendToPrologTerm(prologTerm);
    prologTerm.closeTerm();
  }

  @Override
  public boolean isProgram() {
    return false;
  }

  @Override
  public boolean isDistinguishingExample() {
    return true;
  }
}
