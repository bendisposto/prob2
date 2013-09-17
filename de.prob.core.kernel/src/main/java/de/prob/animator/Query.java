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


/*
 * TODO:
 * composed commands
 */

public class Query implements IPrologTermOutput {


    static {
    	String osString = System.getProperty("os.name");
		String os = osString.toLowerCase();
		String extension;
		if (os.indexOf("win") >= 0) {
			extension = "dll";
			System.load(Main.PROB_HOME + "bin\\sprt4-2-1.dll");
		} else if (os.indexOf("mac") >= 0) {
			extension = "bundle";
		}  else {
			extension = "so";
		}
        System.load(Main.PROB_HOME + "/libquery." + extension);
        init();
    }
    
    private final int instance;
    
    public Query() {
    	instance = init_instance(Main.PROB_HOME + "/probcli.sav");
    	init_prob(instance); // calls set_search_pathes, init_eclipse_preferences and set_prefs
    }
    
    public Query(String pathToSav) {
    	instance = init_instance(pathToSav);
    }
    
    private final Stack<List<Integer>> terms = new Stack<List<Integer>>();
    private final Stack<String> functors = new Stack<String>();


    // save query
    private long predicate = 0;
    private int[] queryArgs = null;

    private native static void init();
    private native static int init_instance(String savLocation);
    private native static void init_prob(int instance);
    private native static long put_predicate(int instance, String functor, int arity);
    private native static int put_inti(int instance, byte[] bytes, int length);
    private native static int put_variable(int instance);
    private native static int build_compound(int instance, String functor, int arity, int[] args);
    private native static int build_list(int instance, int length, int[] args);
    private native static int put_atom(int instance, String name);
    private native static int put_string(int instance, String name);
    private native static long execute(int instance, long predicate, int[] args);
    private native static void close(int instance, long qid);
    private native static PrologTerm toPrologTerm(int instance, int ref);
    private native static int read_string(int instance, String goal, int[] varRefs);
    private native static void interrupt(int instance);

    private StringBuffer sb = new StringBuffer();

    private Map<String, Integer> variables = new HashMap<String, Integer>();
    private Map<String, PrologTerm> binding = new HashMap<String, PrologTerm>();

    public void clear() {
    	variables = new HashMap<String, Integer>();
    	binding = new HashMap<String, PrologTerm>();
    	sb = new StringBuffer();
    }
    
	public IPrologTermOutput openTerm(String functor) {
        final List<Integer> l = new ArrayList<Integer>();
        terms.push(l);
        functors.push(functor);
        
        sb.append(functor + "(");
        
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
            predicate = put_predicate(instance, functor, queryArgs.length);
        } else {
            int compRef = build_compound(instance, functor, arr.length, arr);
            terms.peek().add(compRef);
        }
        sb.append("), ");
        
		return this;
	}

	public IPrologTermOutput printAtom(String content) {
		if (terms.isEmpty()) {
			sb.append(content + ".");
			predicate = put_predicate(instance, content, 0);
			queryArgs = new int[] {};
			
			return this;
		}
		
        int atomRef = put_atom(instance, content);
        terms.peek().add(atomRef);
        sb.append(content + ", ");
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
        int stringRef = put_string(instance, content);

        terms.peek().add(stringRef);
        
        sb.append(content + ", ");
		return this;
	}

	public IPrologTermOutput printNumber(long number) {
        BigInteger bi = new BigInteger(String.valueOf(number));
		return printNumber(bi);
	}

	public IPrologTermOutput printNumber(BigInteger number) {
        byte[] arr = number.toByteArray();
        
        ArrayUtils.reverse(arr);
        
        int intRef = put_inti(instance, arr, arr.length);

        terms.peek().add(intRef);
        sb.append(number.toString() + ", ");
		return this;
	}

	public IPrologTermOutput openList() {
        ArrayList<Integer> l = new ArrayList<Integer>();
        terms.push(l);
        sb.append("[");

		return this;
	}

	public IPrologTermOutput closeList() {
        List<Integer> l = terms.pop();
        int[] list = toIntArray(l);
        int listRef = build_list(instance, list.length, list);

        terms.peek().add(listRef);
        sb.append("], ");
		return this;
	}

	public IPrologTermOutput emptyList() {
        int emptyRef = build_list(instance, 0, (int[]) null);

        terms.peek().add(emptyRef);
        sb.append("[], ");
		return this;
	}

	public IPrologTermOutput printVariable(String var) {
		//System.out.println(var);
        int varRef = put_variable(instance);
        variables.put(var, varRef);

        terms.peek().add(varRef);
        sb.append(var + ", ");
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
            throw new RuntimeException("Unknown prolog term");
        }
		return this;
	}

	public IPrologTermOutput flush() {
		return this;
	}

	public IPrologTermOutput fullstop() {
		sb.append(".");
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
    	if (predicate == 0 || queryArgs == null) {
    		throw new IllegalArgumentException("Tried to execute a query that was not initialized.");
    	}
    	System.out.println(sb.toString());
        
    	long qid = execute(instance, predicate, queryArgs);
        
        for (String varName : variables.keySet()) {
        	System.out.println(varName);
			int ref = variables.get(varName);
			PrologTerm pt = toPrologTerm(instance, ref);
			System.out.println(pt.toString());
			binding.put(varName, pt);
		}
        close(instance, qid);
    }
    
    public Map<String, PrologTerm> getBinding() {
    	return binding;
    }
    
	public int printRaw(String command, String[] my_variables) {
		sb.append(command);
		predicate = put_predicate(instance, "call", 1);
		int varRefs[] = new int[my_variables.length + 1];
		
		varRefs[my_variables.length] = 0;
		
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < my_variables.length; i++) {
			varRefs[i] = put_variable(instance);
	        variables.put(my_variables[i], varRefs[i]);
			sb.append(my_variables[i] + "=" + my_variables[i] + ", ");
		}
		
		
		sb.append(command);
		sb.append(".");
		
		int goal = read_string(instance, sb.toString(), varRefs);
		queryArgs = new int[] {goal};
		
		return 1; // for testing in groovy shell
	}
	
	public void sendInterrupt() {
		interrupt(instance);
	}

}
