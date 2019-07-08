package de.prob.synthesis;

public class BSynthesisException extends Exception {

  private final String msg;

  public BSynthesisException(final String msg) {
    this.msg = msg;
  }

  public String getMsg() {
    return msg;
  }
}
