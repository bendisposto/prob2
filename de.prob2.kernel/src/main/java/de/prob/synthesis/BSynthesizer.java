package de.prob.synthesis;

import de.prob.exception.ProBError;
import de.prob.statespace.StateSpace;
import de.prob.synthesis.library.BLibrary;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Interface for the program synthesis backend to generate B predicates or operations from examples.
 */
@SuppressWarnings("unused")
public class BSynthesizer {

  private final StateSpace stateSpace;

  private long solverTimeOut = 2500;
  private SynthesisMode synthesisMode = SynthesisMode.FIRST_SOLUTION;

  public BSynthesizer(final StateSpace stateSpace) {
    this.stateSpace = stateSpace;
  }

  /**
   * Synthesize a B predicate from explicit state positive and negative examples. Use predefined
   * component library configurations (default).
   *
   * @param positiveExamples Set of examples (machine states) that satisfy the predicate.
   * @param negativeExamples Set of examples (machine states) the predicate should be false for.
   * @return Returns a predicate or a distinguishing example depending on the synthesis mode.
   * @throws BSynthesisException Internal error in Prolog backend.
   */
  public BSynthesisResult synthesizePredicate(final Set<Example> positiveExamples,
                                              final Set<Example> negativeExamples)
      throws BSynthesisException {
    return synthesizePredicate(positiveExamples, negativeExamples, new BLibrary());
  }

  /**
   * Synthesize a B predicate from explicit state positive and negative examples. Use predefined
   * component library configurations (default).
   *
   * @param positiveExamples Set of examples (machine states) that satisfy the predicate.
   * @param negativeExamples Set of examples (machine states) the predicate should be false for.
   * @param componentLibrary A component library to be used during synthesis.
   * @return Returns a predicate or a distinguishing example depending on the synthesis mode.
   * @throws BSynthesisException Internal error in Prolog backend.
   */
  @SuppressWarnings("WeakerAccess")
  public BSynthesisResult synthesizePredicate(final Set<Example> positiveExamples,
                                              final Set<Example> negativeExamples,
                                              final BLibrary componentLibrary)
      throws BSynthesisException {
    // dummy output state
    final Set<IOExample> positiveIOExamples = positiveExamples.stream()
        .map(example -> new IOExample(example, example))
        .collect(Collectors.toSet());
    final Set<IOExample> negativeIOExamples = negativeExamples.stream()
        .map(example -> new IOExample(example, example))
        .collect(Collectors.toSet());

    final BSynthesisCommand synthesisCommand = new BSynthesisCommand(
        SynthesisType.PREDICATE, positiveIOExamples, negativeIOExamples, componentLibrary);
    synthesisCommand.setSolverTimeOut(solverTimeOut);
    synthesisCommand.setSynthesisMode(synthesisMode);
    try {
      stateSpace.execute(synthesisCommand);
      return processResult(synthesisCommand);
    } catch (final ProBError proBError) {
      throw new BSynthesisException(proBError.getOriginalMessage());
    }
  }

  /**
   * Synthesize a B operation from explicit state positive and negative input-output examples.
   * Use predefined component library configurations (default).
   *
   * @param positiveExamples Set of I/O examples that describe the behavior of the
   *                         operation's substitution. The input states are used as positive
   *                         examples to synthesize a precondition.
   * @param negativeExamples Set of I/O examples the operation should be disabled for, i.e., the
   *                         precondition is false on the input state. It is sufficient to provide
   *                         empty strings for the output examples' values as they are neither used
   *                         to synthesize an operation's substitution nor precondition.
   * @return Returns an operation or a distinguishing I/O example depending on the synthesis mode.
   * @throws BSynthesisException Internal error in Prolog backend.
   */
  public BSynthesisResult synthesizeOperation(final Set<IOExample> positiveExamples,
                                              final Set<IOExample> negativeExamples)
      throws BSynthesisException {
    return synthesizeOperation(positiveExamples, negativeExamples, new BLibrary());
  }

  @SuppressWarnings("WeakerAccess")
  public BSynthesisResult synthesizeOperation(final Set<IOExample> positiveExamples,
                                              final Set<IOExample> negativeExamples,
                                              final BLibrary componentLibrary)
      throws BSynthesisException {
    final BSynthesisCommand synthesisCommand = new BSynthesisCommand(
        SynthesisType.OPERATION, positiveExamples, negativeExamples, componentLibrary);
    synthesisCommand.setSolverTimeOut(solverTimeOut);
    synthesisCommand.setSynthesisMode(synthesisMode);
    try {
      stateSpace.execute(synthesisCommand);
      return processResult(synthesisCommand);
    } catch (final ProBError proBError) {
      throw new BSynthesisException(proBError.getOriginalMessage());
    }
  }

  private BSynthesisResult processResult(final BSynthesisCommand synthesisCommand)
      throws BSynthesisException {
    if (synthesisCommand.getSynthesizedProgram() != null) {
      return synthesisCommand.getSynthesizedProgram();
    }
    if (synthesisCommand.getDistinguishingExample() != null) {
      return synthesisCommand.getDistinguishingExample();
    }
    throw new BSynthesisException("BSynthesis: No Solution Found.");
  }

  public long getSolverTimeOut() {
    return solverTimeOut;
  }

  /**
   * The timeout used for the ProB constraint solver in milliseconds.
   */
  public void setSolverTimeOut(long solverTimeOut) {
    this.solverTimeOut = solverTimeOut;
  }

  public SynthesisMode getSynthesisMode() {
    return synthesisMode;
  }

  /**
   * Synthesis mode can be set to either return the first solution or to search for a unique
   * solution (interactive mode) considering that the examples possibly describe ambiguous behavior.
   * In the latter case, synthesis searches for further solutions possibly providing a
   * non-equivalent program resulting in a distinguishing example which can be validated by the
   * user. The uniqueness depends on the selected solver timeout and is not complete in practice.
   */
  public void setSynthesisMode(SynthesisMode synthesisMode) {
    this.synthesisMode = synthesisMode;
  }
}
