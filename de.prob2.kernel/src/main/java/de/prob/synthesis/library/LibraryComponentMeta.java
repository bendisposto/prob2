package de.prob.synthesis.library;

import java.util.HashMap;

class LibraryComponentMeta {

  static final HashMap<LibraryComponentName, String> libraryComponentDescrs =
      new HashMap<>();
  static final HashMap<LibraryComponentName, String> libraryComponentInternalNames =
      new HashMap<>();
  static final HashMap<LibraryComponentName, LibraryComponentType> libraryComponentTypes =
      new HashMap<>();
  static final HashMap<LibraryComponentName, String> libraryComponentSyntax =
      new HashMap<>();

  static {
    initializeDescriptions();
    initializeInternalNames();
    initializeTypes();
    initializeSyntax();
  }

  private static void initializeDescriptions() {
    // numbers
    libraryComponentDescrs.put(LibraryComponentName.NATURAL, "Natural Numbers");
    libraryComponentDescrs.put(LibraryComponentName.NATURAL1, "Non-Zero Natural Numbers");
    libraryComponentDescrs.put(LibraryComponentName.NAT, "Implementable Natural Numbers");
    libraryComponentDescrs.put(LibraryComponentName.NAT1, "Implementable Non-Zero Natural Numbers");
    libraryComponentDescrs.put(LibraryComponentName.INTEGER, "Set of Integers");
    libraryComponentDescrs.put(LibraryComponentName.INT, "Integers in MININT..MAXINT");
    libraryComponentDescrs.put(LibraryComponentName.MIN, "Minimum of Set of Integer");
    libraryComponentDescrs.put(LibraryComponentName.MAX, "Maximum of Set of Integer");
    libraryComponentDescrs.put(LibraryComponentName.ADD, "Addition");
    libraryComponentDescrs.put(LibraryComponentName.MINUS, "Subtraction");
    libraryComponentDescrs.put(LibraryComponentName.MULT, "Multiplication");
    libraryComponentDescrs.put(LibraryComponentName.DIV, "Division");
    libraryComponentDescrs.put(LibraryComponentName.MOD, "Modulo");
    libraryComponentDescrs.put(LibraryComponentName.GREATER, "Greater than");
    libraryComponentDescrs.put(LibraryComponentName.GEQ, "Greater or Equal than");
    libraryComponentDescrs.put(LibraryComponentName.LESS, "Less than");
    libraryComponentDescrs.put(LibraryComponentName.LEQ, "Less or Equal than");
    libraryComponentDescrs.put(LibraryComponentName.INTERVAL, "Interval");
    libraryComponentDescrs.put(LibraryComponentName.POWER_OF, "Power of");
    // predicates
    libraryComponentDescrs.put(LibraryComponentName.CONJUNCT, "Conjunction");
    libraryComponentDescrs.put(LibraryComponentName.DISJUNCT, "Disjunction");
    libraryComponentDescrs.put(LibraryComponentName.IMPLICATION, "Implication");
    libraryComponentDescrs.put(LibraryComponentName.EQUIVALENCE, "Equivalence");
    libraryComponentDescrs.put(LibraryComponentName.NEGATION, "Negation");
    libraryComponentDescrs.put(LibraryComponentName.EQUALITY, "Equality (arbitrary types)");
    libraryComponentDescrs.put(LibraryComponentName.INEQUALITY, "Inequality (arbitrary types)");
    // relations
    libraryComponentDescrs.put(LibraryComponentName.DOMAIN, "Domain");
    libraryComponentDescrs.put(LibraryComponentName.RANGE, "Range");
    libraryComponentDescrs.put(LibraryComponentName.DOMAIN_RESTRICTION, "Domain Restriction");
    libraryComponentDescrs.put(LibraryComponentName.DOMAIN_SUBTRACTION, "Domain Subtraction");
    libraryComponentDescrs.put(LibraryComponentName.RANGE_RESTRICTION, "Range Restriction");
    libraryComponentDescrs.put(LibraryComponentName.RANGE_SUBTRACTION, "Range Subtraction");
    libraryComponentDescrs.put(LibraryComponentName.CLOSURE, "Reflexive Closure");
    libraryComponentDescrs.put(LibraryComponentName.CLOSURE1, "Transitive Closure");
    // sequences
    libraryComponentDescrs.put(LibraryComponentName.SEQ, "Set of Finite Sequences");
    libraryComponentDescrs.put(LibraryComponentName.SEQ1, "Set of Finite and Non-Empty Sequences");
    libraryComponentDescrs.put(LibraryComponentName.ISEQ, "Set of Injective Sequences");
    libraryComponentDescrs.put(LibraryComponentName.PERMUTATIONS, "Permutations");
    libraryComponentDescrs.put(LibraryComponentName.CONCAT, "Sequence Concatenation");
    libraryComponentDescrs.put(LibraryComponentName.SIZE, "Sequence Size");
    libraryComponentDescrs.put(LibraryComponentName.REVERSE, "Sequence Reverse");
    libraryComponentDescrs.put(LibraryComponentName.FIRST, "First of Sequence");
    libraryComponentDescrs.put(LibraryComponentName.LAST, "Last of Sequence");
    libraryComponentDescrs.put(LibraryComponentName.TAIL, "All Elements without First");
    libraryComponentDescrs.put(LibraryComponentName.FRONT, "All Elements without Last");
    libraryComponentDescrs.put(LibraryComponentName.PREPEND, "Prepend Element");
    libraryComponentDescrs.put(LibraryComponentName.APPEND, "Append Element");
    libraryComponentDescrs.put(LibraryComponentName.TAKE, "Take First n Elements");
    libraryComponentDescrs.put(LibraryComponentName.DROP, "Drop First n Elements");
    libraryComponentDescrs.put(LibraryComponentName.OVERWRITE, "Right Overriding");
    // sets
    libraryComponentDescrs.put(LibraryComponentName.MEMBER, "Element of");
    libraryComponentDescrs.put(LibraryComponentName.NOT_MEMBER, "Not Element of");
    libraryComponentDescrs.put(LibraryComponentName.UNION, "Set Union");
    libraryComponentDescrs.put(LibraryComponentName.INTERSECTION, "Set Intersection");
    libraryComponentDescrs.put(LibraryComponentName.DIFFERENCE, "Set Difference");
    libraryComponentDescrs.put(LibraryComponentName.CARTESIAN, "Cartesian Product");
    libraryComponentDescrs.put(LibraryComponentName.CARD, "Cardinality");
    libraryComponentDescrs.put(LibraryComponentName.SUBSET, "Subset of");
    libraryComponentDescrs.put(LibraryComponentName.NOT_SUBSET, "Not Subset of");
    libraryComponentDescrs.put(LibraryComponentName.SUBSET_STRICT, "Strict Subset of");
    libraryComponentDescrs.put(LibraryComponentName.NOT_SUBSET_STRICT, "Not Strict Subset of");
    libraryComponentDescrs.put(LibraryComponentName.POWERSET, "Powerset of");
    libraryComponentDescrs.put(LibraryComponentName.POWERSET1, "Non-Empty Subsets");
    libraryComponentDescrs.put(LibraryComponentName.FINITE_SUBSET, "Finite Subsets");
    libraryComponentDescrs.put(LibraryComponentName.FINITE_SUBSET1, "Finite and Non-Empty Subsets");
    libraryComponentDescrs.put(LibraryComponentName.GENERAL_UNION, "Generalized Union");
    libraryComponentDescrs.put(LibraryComponentName.GENERAL_INTERSECTION, "Generalized Intersection");
    // substitutions
    libraryComponentDescrs.put(LibraryComponentName.CONVERT_BOOL, "Convert Predicate to Boolean");
  }

  private static void initializeInternalNames() {
    // numbers
    libraryComponentInternalNames.put(LibraryComponentName.NATURAL, "integer_set_natural");
    libraryComponentInternalNames.put(LibraryComponentName.NATURAL1, "integer_set_natural1");
    libraryComponentInternalNames.put(LibraryComponentName.NAT, "implementable_natural");
    libraryComponentInternalNames.put(LibraryComponentName.NAT1, "implementable_natural1");
    libraryComponentInternalNames.put(LibraryComponentName.INTEGER, "integer_set");
    libraryComponentInternalNames.put(LibraryComponentName.INT, "integer_set_min_max");
    libraryComponentInternalNames.put(LibraryComponentName.MIN, "min");
    libraryComponentInternalNames.put(LibraryComponentName.MAX, "max");
    libraryComponentInternalNames.put(LibraryComponentName.ADD, "add");
    libraryComponentInternalNames.put(LibraryComponentName.MINUS, "minus");
    libraryComponentInternalNames.put(LibraryComponentName.MULT, "multiplication");
    libraryComponentInternalNames.put(LibraryComponentName.DIV, "div");
    libraryComponentInternalNames.put(LibraryComponentName.MOD, "modulo");
    libraryComponentInternalNames.put(LibraryComponentName.GREATER, "greater");
    libraryComponentInternalNames.put(LibraryComponentName.GEQ, "greater_equal");
    libraryComponentInternalNames.put(LibraryComponentName.LESS, "less");
    libraryComponentInternalNames.put(LibraryComponentName.LEQ, "less_equal");
    libraryComponentInternalNames.put(LibraryComponentName.INTERVAL, "interval");
    libraryComponentInternalNames.put(LibraryComponentName.POWER_OF, "power_of");
    // predicates
    libraryComponentInternalNames.put(LibraryComponentName.CONJUNCT, "conjunct");
    libraryComponentInternalNames.put(LibraryComponentName.DISJUNCT, "disjunct");
    libraryComponentInternalNames.put(LibraryComponentName.IMPLICATION, "implication");
    libraryComponentInternalNames.put(LibraryComponentName.EQUIVALENCE, "equivalence");
    libraryComponentInternalNames.put(LibraryComponentName.NEGATION, "negation");
    libraryComponentInternalNames.put(LibraryComponentName.EQUALITY, "equal");
    libraryComponentInternalNames.put(LibraryComponentName.INEQUALITY, "not_equal");
    // relations
    libraryComponentInternalNames.put(LibraryComponentName.DOMAIN, "dom");
    libraryComponentInternalNames.put(LibraryComponentName.RANGE, "ran");
    libraryComponentInternalNames.put(LibraryComponentName.DOMAIN_RESTRICTION, "domain_restriction");
    libraryComponentInternalNames.put(LibraryComponentName.DOMAIN_SUBTRACTION, "domain_subtraction");
    libraryComponentInternalNames.put(LibraryComponentName.RANGE_RESTRICTION, "range_restriction");
    libraryComponentInternalNames.put(LibraryComponentName.RANGE_SUBTRACTION, "range_subtraction");
    libraryComponentInternalNames.put(LibraryComponentName.CLOSURE, "reflexive_closure");
    libraryComponentInternalNames.put(LibraryComponentName.CLOSURE1, "closure");
    // sequences
    libraryComponentInternalNames.put(LibraryComponentName.SEQ, "seq");
    libraryComponentInternalNames.put(LibraryComponentName.SEQ1, "seq1");
    libraryComponentInternalNames.put(LibraryComponentName.ISEQ, "iseq1");
    libraryComponentInternalNames.put(LibraryComponentName.PERMUTATIONS, "perm");
    libraryComponentInternalNames.put(LibraryComponentName.CONCAT, "concat");
    libraryComponentInternalNames.put(LibraryComponentName.SIZE, "size");
    libraryComponentInternalNames.put(LibraryComponentName.REVERSE, "reverse");
    libraryComponentInternalNames.put(LibraryComponentName.FIRST, "first");
    libraryComponentInternalNames.put(LibraryComponentName.LAST, "last");
    libraryComponentInternalNames.put(LibraryComponentName.TAIL, "tail");
    libraryComponentInternalNames.put(LibraryComponentName.FRONT, "front");
    libraryComponentInternalNames.put(LibraryComponentName.PREPEND, "insert_front");
    libraryComponentInternalNames.put(LibraryComponentName.APPEND, "insert_tail");
    libraryComponentInternalNames.put(LibraryComponentName.TAKE, "restrict_front");
    libraryComponentInternalNames.put(LibraryComponentName.DROP, "restrict_tail");
    libraryComponentInternalNames.put(LibraryComponentName.OVERWRITE, "overwrite");
    // sets
    libraryComponentInternalNames.put(LibraryComponentName.MEMBER, "member");
    libraryComponentInternalNames.put(LibraryComponentName.NOT_MEMBER, "not_member");
    libraryComponentInternalNames.put(LibraryComponentName.UNION, "union");
    libraryComponentInternalNames.put(LibraryComponentName.INTERSECTION, "intersection");
    libraryComponentInternalNames.put(LibraryComponentName.DIFFERENCE, "set_subtraction");
    libraryComponentInternalNames.put(LibraryComponentName.CARTESIAN, "cartesian_product");
    libraryComponentInternalNames.put(LibraryComponentName.CARD, "card");
    libraryComponentInternalNames.put(LibraryComponentName.SUBSET, "subset");
    libraryComponentInternalNames.put(LibraryComponentName.NOT_SUBSET, "not_subset");
    libraryComponentInternalNames.put(LibraryComponentName.SUBSET_STRICT, "subset_strict");
    libraryComponentInternalNames.put(LibraryComponentName.NOT_SUBSET_STRICT, "not_subset_strict");
    libraryComponentInternalNames.put(LibraryComponentName.POWERSET, "pow_subset");
    libraryComponentInternalNames.put(LibraryComponentName.POWERSET1, "pow_subset1");
    libraryComponentInternalNames.put(LibraryComponentName.FINITE_SUBSET, "fin_subset");
    libraryComponentInternalNames.put(LibraryComponentName.FINITE_SUBSET1, "fin_subset1");
    libraryComponentInternalNames.put(LibraryComponentName.GENERAL_UNION, "general_union");
    libraryComponentInternalNames.put(LibraryComponentName.GENERAL_INTERSECTION, "general_intersection");
    // substitutions
    libraryComponentInternalNames.put(LibraryComponentName.CONVERT_BOOL, "convert_bool");
  }

  private static void initializeTypes() {
    // numbers
    libraryComponentTypes.put(LibraryComponentName.NATURAL, LibraryComponentType.NUMBERS);
    libraryComponentTypes.put(LibraryComponentName.NATURAL1, LibraryComponentType.NUMBERS);
    libraryComponentTypes.put(LibraryComponentName.NAT, LibraryComponentType.NUMBERS);
    libraryComponentTypes.put(LibraryComponentName.NAT1, LibraryComponentType.NUMBERS);
    libraryComponentTypes.put(LibraryComponentName.INTEGER, LibraryComponentType.NUMBERS);
    libraryComponentTypes.put(LibraryComponentName.INT, LibraryComponentType.NUMBERS);
    libraryComponentTypes.put(LibraryComponentName.MIN, LibraryComponentType.NUMBERS);
    libraryComponentTypes.put(LibraryComponentName.MAX, LibraryComponentType.NUMBERS);
    libraryComponentTypes.put(LibraryComponentName.ADD, LibraryComponentType.NUMBERS);
    libraryComponentTypes.put(LibraryComponentName.MINUS, LibraryComponentType.NUMBERS);
    libraryComponentTypes.put(LibraryComponentName.MULT, LibraryComponentType.NUMBERS);
    libraryComponentTypes.put(LibraryComponentName.DIV, LibraryComponentType.NUMBERS);
    libraryComponentTypes.put(LibraryComponentName.MOD, LibraryComponentType.NUMBERS);
    libraryComponentTypes.put(LibraryComponentName.GREATER, LibraryComponentType.NUMBERS);
    libraryComponentTypes.put(LibraryComponentName.GEQ, LibraryComponentType.NUMBERS);
    libraryComponentTypes.put(LibraryComponentName.LESS, LibraryComponentType.NUMBERS);
    libraryComponentTypes.put(LibraryComponentName.LEQ, LibraryComponentType.NUMBERS);
    libraryComponentTypes.put(LibraryComponentName.INTERVAL, LibraryComponentType.NUMBERS);
    libraryComponentTypes.put(LibraryComponentName.POWER_OF, LibraryComponentType.NUMBERS);
    // predicates
    libraryComponentTypes.put(LibraryComponentName.CONJUNCT, LibraryComponentType.PREDICATES);
    libraryComponentTypes.put(LibraryComponentName.DISJUNCT, LibraryComponentType.PREDICATES);
    libraryComponentTypes.put(LibraryComponentName.IMPLICATION, LibraryComponentType.PREDICATES);
    libraryComponentTypes.put(LibraryComponentName.EQUIVALENCE, LibraryComponentType.PREDICATES);
    libraryComponentTypes.put(LibraryComponentName.NEGATION, LibraryComponentType.PREDICATES);
    libraryComponentTypes.put(LibraryComponentName.EQUALITY, LibraryComponentType.PREDICATES);
    libraryComponentTypes.put(LibraryComponentName.INEQUALITY, LibraryComponentType.PREDICATES);
    // relations
    libraryComponentTypes.put(LibraryComponentName.DOMAIN, LibraryComponentType.RELATIONS);
    libraryComponentTypes.put(LibraryComponentName.RANGE, LibraryComponentType.RELATIONS);
    libraryComponentTypes.put(LibraryComponentName.DOMAIN_RESTRICTION, LibraryComponentType.RELATIONS);
    libraryComponentTypes.put(LibraryComponentName.DOMAIN_SUBTRACTION, LibraryComponentType.RELATIONS);
    libraryComponentTypes.put(LibraryComponentName.RANGE_RESTRICTION, LibraryComponentType.RELATIONS);
    libraryComponentTypes.put(LibraryComponentName.RANGE_SUBTRACTION, LibraryComponentType.RELATIONS);
    libraryComponentTypes.put(LibraryComponentName.CLOSURE, LibraryComponentType.RELATIONS);
    libraryComponentTypes.put(LibraryComponentName.CLOSURE1, LibraryComponentType.RELATIONS);
    // sequences
    libraryComponentTypes.put(LibraryComponentName.SEQ, LibraryComponentType.SEQUENCES);
    libraryComponentTypes.put(LibraryComponentName.SEQ1, LibraryComponentType.SEQUENCES);
    libraryComponentTypes.put(LibraryComponentName.ISEQ, LibraryComponentType.SEQUENCES);
    libraryComponentTypes.put(LibraryComponentName.PERMUTATIONS, LibraryComponentType.SEQUENCES);
    libraryComponentTypes.put(LibraryComponentName.CONCAT, LibraryComponentType.SEQUENCES);
    libraryComponentTypes.put(LibraryComponentName.SIZE, LibraryComponentType.SEQUENCES);
    libraryComponentTypes.put(LibraryComponentName.REVERSE, LibraryComponentType.SEQUENCES);
    libraryComponentTypes.put(LibraryComponentName.FIRST, LibraryComponentType.SEQUENCES);
    libraryComponentTypes.put(LibraryComponentName.LAST, LibraryComponentType.SEQUENCES);
    libraryComponentTypes.put(LibraryComponentName.TAIL, LibraryComponentType.SEQUENCES);
    libraryComponentTypes.put(LibraryComponentName.FRONT, LibraryComponentType.SEQUENCES);
    libraryComponentTypes.put(LibraryComponentName.PREPEND, LibraryComponentType.SEQUENCES);
    libraryComponentTypes.put(LibraryComponentName.APPEND, LibraryComponentType.SEQUENCES);
    libraryComponentTypes.put(LibraryComponentName.TAKE, LibraryComponentType.SEQUENCES);
    libraryComponentTypes.put(LibraryComponentName.DROP, LibraryComponentType.SEQUENCES);
    libraryComponentTypes.put(LibraryComponentName.OVERWRITE, LibraryComponentType.SEQUENCES);
    // sets
    libraryComponentTypes.put(LibraryComponentName.MEMBER, LibraryComponentType.SETS);
    libraryComponentTypes.put(LibraryComponentName.NOT_MEMBER, LibraryComponentType.SETS);
    libraryComponentTypes.put(LibraryComponentName.UNION, LibraryComponentType.SETS);
    libraryComponentTypes.put(LibraryComponentName.INTERSECTION, LibraryComponentType.SETS);
    libraryComponentTypes.put(LibraryComponentName.DIFFERENCE, LibraryComponentType.SETS);
    libraryComponentTypes.put(LibraryComponentName.CARTESIAN, LibraryComponentType.SETS);
    libraryComponentTypes.put(LibraryComponentName.CARD, LibraryComponentType.SETS);
    libraryComponentTypes.put(LibraryComponentName.SUBSET, LibraryComponentType.SETS);
    libraryComponentTypes.put(LibraryComponentName.NOT_SUBSET, LibraryComponentType.SETS);
    libraryComponentTypes.put(LibraryComponentName.SUBSET_STRICT, LibraryComponentType.SETS);
    libraryComponentTypes.put(LibraryComponentName.NOT_SUBSET_STRICT, LibraryComponentType.SETS);
    libraryComponentTypes.put(LibraryComponentName.POWERSET, LibraryComponentType.SETS);
    libraryComponentTypes.put(LibraryComponentName.POWERSET1, LibraryComponentType.SETS);
    libraryComponentTypes.put(LibraryComponentName.FINITE_SUBSET, LibraryComponentType.SETS);
    libraryComponentTypes.put(LibraryComponentName.FINITE_SUBSET1, LibraryComponentType.SETS);
    libraryComponentTypes.put(LibraryComponentName.GENERAL_UNION, LibraryComponentType.SETS);
    libraryComponentTypes.put(LibraryComponentName.GENERAL_INTERSECTION, LibraryComponentType.SETS);
    // substitutions
    libraryComponentTypes.put(LibraryComponentName.CONVERT_BOOL, LibraryComponentType.SUBSTITUTIONS);
  }


  private static void initializeSyntax() {
    // numbers
    libraryComponentSyntax.put(LibraryComponentName.NATURAL, "NATURAL");
    libraryComponentSyntax.put(LibraryComponentName.NATURAL1, "NATURAL1");
    libraryComponentSyntax.put(LibraryComponentName.NAT, "NAT");
    libraryComponentSyntax.put(LibraryComponentName.NAT1, "NAT1");
    libraryComponentSyntax.put(LibraryComponentName.INTEGER, "INTEGER");
    libraryComponentSyntax.put(LibraryComponentName.INT, "INT");
    libraryComponentSyntax.put(LibraryComponentName.MIN, "min(m)");
    libraryComponentSyntax.put(LibraryComponentName.MAX, "max(m)");
    libraryComponentSyntax.put(LibraryComponentName.ADD, "m + n");
    libraryComponentSyntax.put(LibraryComponentName.MINUS, "m - n");
    libraryComponentSyntax.put(LibraryComponentName.MULT, "m * n");
    libraryComponentSyntax.put(LibraryComponentName.DIV, "m / n");
    libraryComponentSyntax.put(LibraryComponentName.MOD, "m mod n");
    libraryComponentSyntax.put(LibraryComponentName.GREATER, "m > n");
    libraryComponentSyntax.put(LibraryComponentName.GEQ, "m >= n");
    libraryComponentSyntax.put(LibraryComponentName.LESS, "m < n");
    libraryComponentSyntax.put(LibraryComponentName.LEQ, "m <= n");
    libraryComponentSyntax.put(LibraryComponentName.INTERVAL, "m .. n");
    libraryComponentSyntax.put(LibraryComponentName.POWER_OF, "m ** n");
    // predicates
    libraryComponentSyntax.put(LibraryComponentName.CONJUNCT, "P & Q");
    libraryComponentSyntax.put(LibraryComponentName.DISJUNCT, "P or Q");
    libraryComponentSyntax.put(LibraryComponentName.IMPLICATION, "P => Q");
    libraryComponentSyntax.put(LibraryComponentName.EQUIVALENCE, "P <=> Q");
    libraryComponentSyntax.put(LibraryComponentName.NEGATION, "not P");
    libraryComponentSyntax.put(LibraryComponentName.EQUALITY, "E = F");
    libraryComponentSyntax.put(LibraryComponentName.INEQUALITY, "E /= F");
    // relations
    libraryComponentSyntax.put(LibraryComponentName.DOMAIN, "dom(r)");
    libraryComponentSyntax.put(LibraryComponentName.RANGE, "ran(r)");
    libraryComponentSyntax.put(LibraryComponentName.DOMAIN_RESTRICTION, "S <| r");
    libraryComponentSyntax.put(LibraryComponentName.DOMAIN_SUBTRACTION, "S <<| r");
    libraryComponentSyntax.put(LibraryComponentName.RANGE_RESTRICTION, "r |> S");
    libraryComponentSyntax.put(LibraryComponentName.RANGE_SUBTRACTION, "r |>> S");
    libraryComponentSyntax.put(LibraryComponentName.CLOSURE, "closure(r)");
    libraryComponentSyntax.put(LibraryComponentName.CLOSURE1, "closure1(r)");
    // sequences
    libraryComponentSyntax.put(LibraryComponentName.SEQ, "seq(s)");
    libraryComponentSyntax.put(LibraryComponentName.SEQ1, "seq1(s)");
    libraryComponentSyntax.put(LibraryComponentName.ISEQ, "iseq1(s)");
    libraryComponentSyntax.put(LibraryComponentName.PERMUTATIONS, "perm(s)");
    libraryComponentSyntax.put(LibraryComponentName.CONCAT, "s ^ t");
    libraryComponentSyntax.put(LibraryComponentName.SIZE, "size(s)");
    libraryComponentSyntax.put(LibraryComponentName.REVERSE, "rev(s)");
    libraryComponentSyntax.put(LibraryComponentName.FIRST, "first(s)");
    libraryComponentSyntax.put(LibraryComponentName.LAST, "last(s)");
    libraryComponentSyntax.put(LibraryComponentName.TAIL, "tail(s)");
    libraryComponentSyntax.put(LibraryComponentName.FRONT, "front(s)");
    libraryComponentSyntax.put(LibraryComponentName.PREPEND, "E → s");
    libraryComponentSyntax.put(LibraryComponentName.APPEND, "s ← E");
    libraryComponentSyntax.put(LibraryComponentName.TAKE, "s /|\\ n");
    libraryComponentSyntax.put(LibraryComponentName.DROP, "s \\|/ n");
    libraryComponentSyntax.put(LibraryComponentName.OVERWRITE, "r1 <+ r2");
    // sets
    libraryComponentSyntax.put(LibraryComponentName.MEMBER, "E : S");
    libraryComponentSyntax.put(LibraryComponentName.NOT_MEMBER, "E /: S");
    libraryComponentSyntax.put(LibraryComponentName.UNION, "S \\/ T");
    libraryComponentSyntax.put(LibraryComponentName.INTERSECTION, "S /\\ T");
    libraryComponentSyntax.put(LibraryComponentName.DIFFERENCE, "S - T");
    libraryComponentSyntax.put(LibraryComponentName.CARTESIAN, "S * T");
    libraryComponentSyntax.put(LibraryComponentName.CARD, "card(S)");
    libraryComponentSyntax.put(LibraryComponentName.SUBSET, "S <: T");
    libraryComponentSyntax.put(LibraryComponentName.NOT_SUBSET, "S /<: T");
    libraryComponentSyntax.put(LibraryComponentName.SUBSET_STRICT, "S <<: T");
    libraryComponentSyntax.put(LibraryComponentName.NOT_SUBSET_STRICT, "S /<<: T");
    libraryComponentSyntax.put(LibraryComponentName.POWERSET, "POW(S)");
    libraryComponentSyntax.put(LibraryComponentName.POWERSET1, "POW1(S)");
    libraryComponentSyntax.put(LibraryComponentName.FINITE_SUBSET, "FIN(S)");
    libraryComponentSyntax.put(LibraryComponentName.FINITE_SUBSET1, "FIN1(S)");
    libraryComponentSyntax.put(LibraryComponentName.GENERAL_UNION, "union(S)");
    libraryComponentSyntax.put(LibraryComponentName.GENERAL_INTERSECTION, "inter(S)");
    // substitutions
    libraryComponentSyntax.put(LibraryComponentName.CONVERT_BOOL, "bool(P)");
  }
}
