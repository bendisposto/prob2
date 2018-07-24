package de.prob.animator.command;

import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * @deprecated This command does not support text entries. Use {@link GetAnimationMatrixForStateCommand} instead.
 */
@Deprecated
public class GetImagesForStateCommand extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "get_animation_image_matrix_for_state";
	private static final String MATRIX_VAR = "Matrix";
	private static final String MINROW_VAR = "Minrow";
	private static final String MAXROW_VAR = "Maxrow";
	private static final String MINCOL_VAR = "Mincol";
	private static final String MAXCOL_VAR = "Maxcol";

	private final String stateId;

	private Integer[][] matrix;
	private int rows;
	private int columns;

	public GetImagesForStateCommand(String stateId) {
		this.stateId = stateId;
	}

	@Override
	public void writeCommand(IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.printAtomOrNumber(stateId);
		pto.printVariable(MATRIX_VAR);
		pto.printVariable(MINROW_VAR);
		pto.printVariable(MAXROW_VAR);
		pto.printVariable(MINCOL_VAR);
		pto.printVariable(MAXCOL_VAR);
		pto.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		ListPrologTerm matrixterm = BindingGenerator.getList(bindings.get(MATRIX_VAR));
		int maxRow = BindingGenerator.getInteger(bindings.get(MAXROW_VAR)).getValue().intValue();
		int minRow = BindingGenerator.getInteger(bindings.get(MINROW_VAR)).getValue().intValue();
		int maxColumn = BindingGenerator.getInteger(bindings.get(MAXCOL_VAR)).getValue().intValue();
		int minColumn = BindingGenerator.getInteger(bindings.get(MINCOL_VAR)).getValue().intValue();
		rows = maxRow - minRow + 1;
		columns = maxColumn - minColumn + 1;
		matrix = new Integer[rows][columns];

		for (PrologTerm t : matrixterm) {
			int row = BindingGenerator.getInteger(t.getArgument(1)).getValue().intValue();
			int col = BindingGenerator.getInteger(t.getArgument(2)).getValue().intValue();
			int id = BindingGenerator.getInteger(t.getArgument(3).getArgument(1)).getValue().intValue();
			matrix[row - minRow][col - minColumn] = id;
		}

	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}

	public Integer[][] getMatrix() {
		return matrix;
	}

}
