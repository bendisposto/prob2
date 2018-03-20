package de.prob.animator.domainobjects;

import java.util.List;
import java.util.stream.Collectors;

import de.prob.parser.BindingGenerator;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public final class TableData {
	private final List<String> header;
	private final List<List<String>> rows;

	private TableData(List<String> header, List<List<String>> table) {
		this.header = header;
		rows = table;
	}

	public List<String> getHeader() {
		return header;
	}

	public List<List<String>> getRows() {
		return rows;
	}

	private static ListPrologTerm getListTermContents(final PrologTerm term) {
		final CompoundPrologTerm listCompoundTerm = BindingGenerator.getCompoundTerm(term, "list", 1);
		return BindingGenerator.getList(listCompoundTerm.getArgument(1));
	}

	public static TableData fromProlog(PrologTerm tableTerm) {
		final List<List<String>> table = getListTermContents(tableTerm).stream()
			.map(term -> tableRowFromList(getListTermContents(term)))
			.collect(Collectors.toList());
		final List<String> header = table.remove(0);
		return new TableData(header, table);
	}

	private static List<String> tableRowFromList(ListPrologTerm term) {
		return term.stream()
				.map(PrologTerm::getFunctor)
				.collect(Collectors.toList());
	}
}
