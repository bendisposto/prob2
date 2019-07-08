package de.prob.synthesis;

public enum SynthesisType {
  OPERATION, PREDICATE;

  @Override
  public String toString() {
    switch (this) {
      case OPERATION:
        return "action";
      case PREDICATE:
        return "guard";
      default:
        return "";
    }
  }

  public boolean isPredicate() {
    return this.equals(PREDICATE);
  }
}
