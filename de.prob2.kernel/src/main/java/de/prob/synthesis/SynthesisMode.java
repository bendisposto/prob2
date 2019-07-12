package de.prob.synthesis;

public enum SynthesisMode {
  INTERACTIVE, FIRST_SOLUTION;

  public boolean isInteractive() {
    return this.equals(INTERACTIVE);
  }
}
