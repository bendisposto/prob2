package de.prob.animator.command;

public class OpInfo {
	public final String id;
	public final String name;
	public final String src;
	public final String dest;
	public final String params;

	public OpInfo(final String id, final String name, final String src,
			final String dest, final String params) {
		this.id = id;
		this.name = name;
		this.src = src;
		this.dest = dest;
		this.params = params;
	}
	
//	FIXME: Implement this for OpInfo. Should it be a static method?
//	public static Operation fromPrologTerm(final PrologTerm rawOpTerm) {
//		final CompoundPrologTerm opTerm = (CompoundPrologTerm) rawOpTerm;
//
//		final IntegerPrologTerm pInt = (IntegerPrologTerm) opTerm
//				.getArgument(ID);
//		final long id = pInt.getValue().longValue();
//		final String name = PrologTerm.atomicString(opTerm.getArgument(NAME));
//		final EventType type = SPECIAL_EVENTS.get(name);
//		final String destId = getIdFromPrologTerm(opTerm.getArgument(DST));
//		final String srcId = getIdFromPrologTerm(opTerm.getArgument(SRC));
//		final List<PrologTerm> args = (ListPrologTerm) opTerm.getArgument(ARGS);
//		final List<String> pargs = create_pretty_arguments((ListPrologTerm) opTerm
//				.getArgument(ARGS_PRETTY));
//		final List<EventStackElement> eventStack = createEventStack((ListPrologTerm) opTerm
//				.getArgument(INFOS));
//
//		return new Operation(id, type, name, destId, srcId, args, pargs,
//				eventStack);
//	}
}
