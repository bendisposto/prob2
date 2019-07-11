package de.prob.synthesis;

import de.prob.animator.command.AbstractCommand;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.synthesis.library.BLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

class BSynthesisCommand extends AbstractCommand {

  private static final String PROLOG_COMMAND_NAME = "start_synthesis_from_ui_";
  private static final String DISTINGUISHING_EXAMPLE = "Distinguishing";
  private static final String SYNTHESIZED_CODE = "SynthesizedCode";

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private SynthesisMode synthesisMode = SynthesisMode.FIRST_SOLUTION;
  private SynthesisType synthesisType;

  private BLibrary componentLibrary;

  private Example distinguishingExample;
  private IOExample distinguishingIOExample;

  private Set<IOExample> positiveExamples;
  private Set<IOExample> negativeExamples;

  private SynthesizedProgram synthesizedProgram;

  private long solverTimeOut = 2500;

  BSynthesisCommand(final SynthesisType synthesisType,
                    final Set<IOExample> positiveExamples,
                    final Set<IOExample> negativeExamples) {
    this(synthesisType, positiveExamples, negativeExamples, new BLibrary());
  }

  BSynthesisCommand(final SynthesisType synthesisType,
                    final Set<IOExample> positiveExamples,
                    final Set<IOExample> negativeExamples,
                    final BLibrary componentLibrary) {
    this.synthesisType = synthesisType;
    this.positiveExamples = positiveExamples;
    this.negativeExamples = negativeExamples;
    this.componentLibrary = componentLibrary;
  }

  @Override
  public void writeCommand(final IPrologTermOutput pto) {
    pto.openTerm(PROLOG_COMMAND_NAME)
        .printAtom(synthesisMode.isInteractive() ? "interactive" : "first_solution")
        .printAtom("no")                                          // adapt machine code on success
        .printNumber(solverTimeOut);
    componentLibrary.printToPrologTerm(pto);
    pto.printAtom(componentLibrary.enumerateConstants() ? "no" : "yes") // consider constants enumerated by the solvers
        .printAtom("proB")                                        // constraint solver
        .openList().closeList()                                   // TODO: currently no if-statements by default
        .printAtom("synthesized")                                 // operation name
        .printAtom(synthesisType.toString().toLowerCase());
    printListToPrologTerm(pto, positiveExamples);
    printListToPrologTerm(pto, negativeExamples);
    pto.printVariable(SYNTHESIZED_CODE)
        .printVariable(DISTINGUISHING_EXAMPLE).closeTerm();
    logger.info("Start synthesis prolog backend by calling prob2_interface:{}", pto);
  }

  @Override
  public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
    distinguishingExample = null;
    distinguishingIOExample = null;
    final String newMachineCode = bindings.get(SYNTHESIZED_CODE).getFunctor();
    if (isDistinguishingExample(bindings.get(DISTINGUISHING_EXAMPLE))) {
      setDistinguishingExampleFromTerm(bindings.get(DISTINGUISHING_EXAMPLE));
      return;
    }
    if (newMachineCode.equals("none")) {
      logger.info("Synthesized two non equivalent programs. Distinguishing example: {}",
          bindings.get(DISTINGUISHING_EXAMPLE));
      distinguishingExample = null;
      distinguishingIOExample = null;
      synthesizedProgram = null;
      return;
    }
    // synthesis succeeded
    this.synthesizedProgram =
        new SynthesizedProgram(BindingGenerator.getCompoundTerm(bindings.get(SYNTHESIZED_CODE), 2));
  }

  private boolean isDistinguishingExample(final PrologTerm prologTerm) {
    return prologTerm.getFunctor().equals("transition") || prologTerm.getFunctor().equals("state");
  }

  private void printListToPrologTerm(final IPrologTermOutput pto,
                                     final Set<IOExample> examples) {
    pto.openList();
    examples.forEach(example -> example.appendToPrologTerm(pto));
    pto.closeList();
  }

  private void setDistinguishingExampleFromTerm(final PrologTerm prologTerm) {
    final String resultFunctor = prologTerm.getFunctor();
    switch (resultFunctor) {
      case "state":
        distinguishingExample = new Example().addAllf(BindingGenerator.getList(prologTerm.getArgument(1))
            .stream().map(VariableExample::fromPrologTerm).collect(Collectors.toSet()));
        distinguishingIOExample = null;
        break;
      case "transition":
        distinguishingExample = null;
        final Example input = new Example().addAllf(BindingGenerator.getList(prologTerm.getArgument(1))
            .stream().map(VariableExample::fromPrologTerm).collect(Collectors.toSet()));
        final Example output = new Example().addAllf(BindingGenerator.getList(prologTerm.getArgument(2))
            .stream().map(VariableExample::fromPrologTerm).collect(Collectors.toSet()));
        distinguishingIOExample = new IOExample(input, output);
        break;
      default:
        throw new AssertionError("Error: Unexpected result of synthesis command.");
    }
  }

  /**
   * Either returns an {@link Example} or an {@link IOExample} depending on the  current
   * command's {@link #synthesisType}.
   */
  public BSynthesisResult getDistinguishingExample() {
    if (synthesisType.isPredicate()) {
      return distinguishingExample;
    }
    return distinguishingIOExample;
  }

  public SynthesisMode getSynthesisMode() {
    return synthesisMode;
  }

  public void setSynthesisMode(final SynthesisMode synthesisMode) {
    this.synthesisMode = synthesisMode;
  }

  public SynthesisType getSynthesisType() {
    return synthesisType;
  }

  public Set<IOExample> getPositiveExamples() {
    return positiveExamples;
  }

  public void setPositiveExamples(final Set<IOExample> positiveExamples) {
    this.positiveExamples = positiveExamples;
  }

  public Set<IOExample> getNegativeExamples() {
    return negativeExamples;
  }

  public void setNegativeExamples(final Set<IOExample> negativeExamples) {
    this.negativeExamples = negativeExamples;
  }

  public long getSolverTimeOut() {
    return solverTimeOut;
  }

  public void setSolverTimeOut(final long solverTimeOut) {
    this.solverTimeOut = solverTimeOut;
  }

  public SynthesizedProgram getSynthesizedProgram() {
    return synthesizedProgram;
  }

  public BLibrary getComponentLibrary() {
    return componentLibrary;
  }

  public void setComponentLibrary(final BLibrary componentLibrary) {
    this.componentLibrary = componentLibrary;
  }
}
