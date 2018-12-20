package de.prob.animator.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.prob.animator.domainobjects.AnimationMatrixEntry;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.State;

public final class GetAnimationMatrixForStateCommand extends AbstractCommand {
	private static final String PROLOG_COMMAND_NAME = "get_animation_image_matrix_for_state";
	
	private static final String MATRIX_VAR = "Matrix";
	private static final String MIN_ROW_VAR = "MinRow";
	private static final String MAX_ROW_VAR = "MaxRow";
	private static final String MIN_COL_VAR = "MinCol";
	private static final String MAX_COL_VAR = "MaxCol";
	
	private final State state;
	private List<List<AnimationMatrixEntry>> matrix;
	
	public GetAnimationMatrixForStateCommand(final State state) {
		super();
		
		this.state = state;
		this.matrix = null;
	}
	
	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.printAtomOrNumber(this.state.getId());
		pto.printVariable(MATRIX_VAR);
		pto.printVariable(MIN_ROW_VAR);
		pto.printVariable(MAX_ROW_VAR);
		pto.printVariable(MIN_COL_VAR);
		pto.printVariable(MAX_COL_VAR);
		pto.closeTerm();
	}
	
	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		final int minRow = BindingGenerator.getInteger(bindings.get(MIN_ROW_VAR)).getValue().intValueExact();
		final int maxRow = BindingGenerator.getInteger(bindings.get(MAX_ROW_VAR)).getValue().intValueExact();
		final int minColumn = BindingGenerator.getInteger(bindings.get(MIN_COL_VAR)).getValue().intValueExact();
		final int maxColumn = BindingGenerator.getInteger(bindings.get(MAX_COL_VAR)).getValue().intValueExact();
		
		final int rows = maxRow - minRow + 1;
		final int columns = maxColumn - minColumn + 1;
		if (rows <= 0 || columns <= 0) {
			// No animation function defined
			return;
		}
		
		// Create a rows*columns 2D list filled with nulls.
		// https://stackoverflow.com/a/5600690
		this.matrix = Stream.generate(() -> new ArrayList<>(Collections.nCopies(columns, (AnimationMatrixEntry)null)))
			.limit(rows)
			.collect(Collectors.toList());
		
		BindingGenerator.getList(bindings.get(MATRIX_VAR))
			.stream()
			.map(term -> BindingGenerator.getCompoundTerm(term, "entry", 3))
			.forEach(term -> {
				final int row = BindingGenerator.getInteger(term.getArgument(1)).getValue().intValueExact();
				final int column = BindingGenerator.getInteger(term.getArgument(2)).getValue().intValueExact();
				final PrologTerm valueTerm = term.getArgument(3);
				final AnimationMatrixEntry value;
				if ("image".equals(valueTerm.getFunctor())) {
					// Animation image number
					BindingGenerator.getCompoundTerm(valueTerm, 1);
					final int imageNumber = ((IntegerPrologTerm)valueTerm.getArgument(1)).getValue().intValueExact();
					value = new AnimationMatrixEntry.Image(row, column, imageNumber);
				} else if ("text".equals(valueTerm.getFunctor())) {
					// Literal string label
					BindingGenerator.getCompoundTerm(valueTerm, 1);
					final String text = PrologTerm.atomicString(valueTerm.getArgument(1));
					value = new AnimationMatrixEntry.Text(row, column, text);
				} else {
					throw new IllegalArgumentException("Unknown animation matrix entry value (expected image/1 or text/1): " + valueTerm);
				}
				this.matrix.get(row - minRow).set(column - minColumn, value);
			});
	}
	
	public List<List<AnimationMatrixEntry>> getMatrix() {
		return this.matrix;
	}
}
