package de.prob.synthesis;

import de.prob.prolog.output.IPrologTermOutput;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Example extends HashSet<VariableExample> implements BSynthesisResult {

  @Override
  public boolean add(final VariableExample variableExample) {
    Set<VariableExample> existing =
        this.stream().filter(e -> e.getName().equals(variableExample.getName()))
            .collect(Collectors.toSet());
    this.removeAll(existing);
    super.add(variableExample);
    return true;
  }

  @Override
  public boolean addAll(final Collection<? extends VariableExample> variableExamples) {
    variableExamples.forEach(this::add);
    return true;
  }

  public Example addf(final VariableExample variableExample) {
    this.add(variableExample);
    return this;
  }

  public Example addAllf(final Set<VariableExample> variableExamples) {
    this.addAll(variableExamples);
    return this;
  }

  /**
   * An example is represented as a list of {@link VariableExample} in Prolog.
   */
  void appendToPrologTerm(final IPrologTermOutput prologTerm) {
    prologTerm.openList();
    this.forEach(variableExample -> variableExample.appendToPrologTerm(prologTerm));
    prologTerm.closeList();
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
