package de.prob.animator.domainobjects;

import java.util.List;
import java.util.stream.Collectors;

import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class TableData {

	private List<String> header;
	private List<List<String>> rows;

	private TableData(List<String> header, List<List<String>> table) {
		this.header = header;
		rows = table;
	}

	private TableData() {
	}

	public List<String> getHeader() {
		return header;
	}

	public List<List<String>> getRows() {
		return rows;
	}

	public static TableData fromProlog(ListPrologTerm tableTerm) {
		List<List<String>> table = ((ListPrologTerm) tableTerm.getArgument(1).getArgument(1))
				.stream()
				.map(term -> (ListPrologTerm)term.getArgument(1))
				.map(TableData::makeTuple)
				.collect(Collectors.toList());
		List<String> header = table.get(0);
		table.remove(0);
		return new TableData(header, table);
	}

	private static List<String> makeTuple(ListPrologTerm term) {
		return term.stream()
				.map(PrologTerm::getFunctor)
				.collect(Collectors.toList());
	}

}
