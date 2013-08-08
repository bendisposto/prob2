package de.prob.animator;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import org.apache.commons.lang.ArrayUtils;

import de.prob.Main;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;



public class Query implements IPrologTermOutput {


    static {
        System.loadLibrary("query");
        init(Main.PROB_HOME + "/probcli.sav");
    }
	
	
    private final Stack<List<Integer>> terms = new Stack<List<Integer>>();
    private final Stack<String> functors = new Stack<String>();


    // save query
    private long predicate;
    private int[] queryArgs;

    private native static void init(String savLocation);
    private native static long put_predicate(String functor, int arity);
    private native static int put_inti(byte[] bytes, int length);
    private native static int put_variable();
    private native static int build_compound(String functor, int arity, int[] args);
    private native static int build_list(int length, int[] args);
    private native static int put_atom(String name);
    private native static int put_string(String name);
    private native static void execute(long predicate, int[] args);
    private native static void close();
    private native static PrologTerm toPrologTerm(int ref);


    private final Map<String, Integer> variables = new HashMap<String, Integer>();
    private final Map<String, PrologTerm> binding = new HashMap<String, PrologTerm>();

	public IPrologTermOutput openTerm(String functor) {
        final List<Integer> l = new ArrayList<Integer>();
        terms.push(l);
        functors.push(functor);
        
        return this;
	}

	public IPrologTermOutput openTerm(String functor, boolean ignoreIndention) {
        return openTerm(functor);
	}

	public IPrologTermOutput closeTerm() {
        final List<Integer> l = terms.pop();
        int[] arr = toIntArray(l);
        String functor = functors.pop();

        if (functors.isEmpty()) {
        	queryArgs = arr;
            predicate = put_predicate(functor, queryArgs.length);
        } else {
            int compRef = build_compound(functor, arr.length, arr);
            terms.peek().add(compRef);
        }
        
		return this;
	}

	public IPrologTermOutput printAtom(String content) {
		if (terms.isEmpty()) {
			predicate = put_predicate(content, 0);
			queryArgs = new int[] {};
			
			return this;
		}
		
        int atomRef = put_atom(content);
        terms.peek().add(atomRef);
		return this;
	}

	public IPrologTermOutput printAtomOrNumber(String content) {
        try {
            printNumber(Long.parseLong(content));
        } catch (NumberFormatException e) {
            printAtom(content);
        }

		return this;
	}

	public IPrologTermOutput printString(String content) {
        int stringRef = put_string(content);

        terms.peek().add(stringRef);
		return this;
	}

	public IPrologTermOutput printNumber(long number) {
        BigInteger bi = new BigInteger(String.valueOf(number));
		return printNumber(bi);
	}

	public IPrologTermOutput printNumber(BigInteger number) {
        byte[] arr = number.toByteArray();
        
        ArrayUtils.reverse(arr);
        
        int intRef = put_inti(arr, arr.length);

        terms.peek().add(intRef);
		return this;
	}

	public IPrologTermOutput openList() {
        ArrayList<Integer> l = new ArrayList<Integer>();
        terms.push(l);

		return this;
	}

	public IPrologTermOutput closeList() {
        List<Integer> l = terms.pop();
        int[] list = toIntArray(l);
        int listRef = build_list(list.length, list);

        terms.peek().add(listRef);
		return this;
	}

	public IPrologTermOutput emptyList() {
        int emptyRef = build_list(0, (int[]) null);

        terms.peek().add(emptyRef);
		return this;
	}

	public IPrologTermOutput printVariable(String var) {
        int varRef = put_variable();
        variables.put(var, varRef);

        terms.peek().add(varRef);
		return this;
	}

	public IPrologTermOutput printTerm(PrologTerm term) {
        if (term.isAtom()) {
            printAtom(term.getFunctor());
        } else if (term.isNumber()) {
            IntegerPrologTerm it = (IntegerPrologTerm) term;
            printNumber(it.getValue());
        } else if (term.isVariable()) {
            // TODO
        } else if (term.isTerm()) {
            openTerm(term.getFunctor());
            int arity = term.getArity();
            for (int i = 1; i <= arity; i++) {
                printTerm(term.getArgument(i));
            }
            closeTerm();
        } else if (term.isList()) {
            openList();
            ListPrologTerm lt = (ListPrologTerm) term;
            
            for (int i = 0; i < lt.size(); i++) {
                printTerm(lt.get(i));
            }
            closeList();
        } else {
            throw new RuntimeException("mimimi");
        }
		return this;
	}

	public IPrologTermOutput flush() {
		return this;
	}

	public IPrologTermOutput fullstop() {
		return this;
	}

    private int[] toIntArray(List<Integer> l) {
        int[] arr = new int[l.size()];
        for (int i = 0; i < l.size(); i++) {
            arr[i] = l.get(i);
        }
        return arr;
    }

    public void execute() {
        execute(predicate, queryArgs);
        for (String varName : variables.keySet()) {
			int ref = variables.get(varName);
			PrologTerm pt = toPrologTerm(ref);
			binding.put(varName, pt);
		}
        close();
    }
    
    public Map<String, PrologTerm> getBinding() {
    	return binding;
    }

}
