package de.prob.synthesis.library;

/**
 * NONE:     do not consider if-statements
 * EXPLICIT: use explicit if-then-else expressions as supported by ProB (probably slow)
 * IMPLICIT: do not use explicit if-statements but possibly synthesize several operations with
 *           appropriate preconditions instead (semantically equivalent to using explicit
 *           if-statements)
 */
public enum ConsiderIfType {
  NONE, EXPLICIT, IMPLICIT;

  public boolean isNone() {
    return this.equals(NONE);
  }

  public boolean isExplicit() {
    return this.equals(EXPLICIT);
  }

  public boolean isImplicit() {
    return this.equals(IMPLICIT);
  }
}
