package de.prob.animator.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.prob.animator.IAnimator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.output.PrologTermDelegate;
import de.prob.prolog.term.PrologTerm;

/**
 * A ComposedCommand contains several other commands and writes their query
 * strings in one pass to the ProB process. It ensures that there are no name
 * clashes in the variables of the different commands.
 * 
 * @author plagge
 * 
 */
public class ComposedCommand extends AbstractCommand {
	private static final char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

	private final AbstractCommand[] cmds;

	public ComposedCommand(final AbstractCommand... cmds) {
		this.cmds = cmds;
	}

	public ComposedCommand(final List<? extends AbstractCommand> cmds) {
		this.cmds = cmds.toArray(new AbstractCommand[0]);
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		final PrefixMap<PrologTerm> prefixMap = new PrefixMap<>(bindings);
		for (int i = 0; i < cmds.length; i++) {
			processPrefixedCommand(prefixMap, i);
		}
	}

	private void processPrefixedCommand(final PrefixMap<PrologTerm> prefixMap, final int i) {
		prefixMap.prefix = createPrefix(i);
		cmds[i].processResult(prefixMap);
	}

	@Override
	public void writeCommand(final IPrologTermOutput orig) {
		PrologPrefixVarOutput pto = new PrologPrefixVarOutput(orig);
		for (int i = 0; i < cmds.length; i++) {
			writePrefixedCommand(pto, i);
		}
	}

	private void writePrefixedCommand(final PrologPrefixVarOutput pto, final int i) {
		pto.prefix = createPrefix(i);
		cmds[i].writeCommand(pto);
	}

	public String createPrefix(final int i) {
		if (i < LETTERS.length) {
			return String.valueOf(LETTERS[i]);
		} else {
			final int letternum = i % LETTERS.length;
			final int number = i / LETTERS.length;
			return String.valueOf(LETTERS[letternum]) + number;
		}
	}

	public void getResultForCommand(final AbstractCommand command,
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		final int index = indexOf(command);
		// added second condition in case command is not included in cmds
		if (index >= 0 && index != cmds.length) {
			final PrefixMap<PrologTerm> prefixMap = new PrefixMap<>(bindings);
			processPrefixedCommand(prefixMap, index);
		} else {
			throw new IllegalArgumentException("cannot reprocess command, command unknown");
		}
	}

	private int indexOf(final AbstractCommand command) {
		int index;
		for (index = 0; index < cmds.length; index++) {
			if (cmds[index].equals(command)) {
				break;
			}
		}
		return index;
	}

	/**
	 * This PrologTermDelegate prefixes every variable with a given string.
	 */
	private static final class PrologPrefixVarOutput extends PrologTermDelegate {
		private String prefix;

		public PrologPrefixVarOutput(final IPrologTermOutput pto) {
			super(pto);
		}

		@Override
		public IPrologTermOutput printVariable(final String var) {
			pto.printVariable(prefix == null ? var : prefix + var);
			return this;
		}

		@Override
		public IPrologTermOutput fullstop() {
			// ignore the fullstop
			return this;
		}
	}

	/**
	 * This simplified map prefixes every query to the map with a given string.
	 */
	private static final class PrefixMap<V> implements ISimplifiedROMap<String, V> {
		private final ISimplifiedROMap<String, V> map;
		private String prefix;

		public PrefixMap(final ISimplifiedROMap<String, V> map) {
			this.map = map;
		}

		@Override
		public V get(final String key) {
			return map.get(prefix == null ? key : prefix + key);
		}

		@Override
		public String toString() {
			return map.toString();
		}

	}

	public AbstractCommand[] runInDebugMode(final IAnimator animator) {
		for (AbstractCommand cmd : cmds) {
			animator.execute(cmd);
		}
		return cmds;
	}

	@Override
	public List<AbstractCommand> getSubcommands() {
		return new ArrayList<>(Arrays.asList(cmds));
	}
}
