## Logical predicates:
 P & Q       conjunction
 P or Q      disjunction
 P => Q      implication
 P <=> Q     equivalence
 not P       negation
 !(x).(P=>Q) universal quantification
 #(x).(P&Q)  existential quantification

## Equality:
 E = F      equality
 E /= F     disequality

## Booleans:
 TRUE
 FALSE
 BOOL        set of boolean values ({TRUE,FALSE})
 bool(P)     convert predicate into BOOL value

Sets:
-----
 {}          empty set
 {E}         singleton set
 {E,F}       set enumeration
 {x|P}       comprehension set
 POW(S)      power set
 POW1(S)     set of non-empty subsets
 FIN(S)      set of all finite subsets
 FIN1(S)     set of all non-empty finite subsets
 card(S)     cardinality
 S*T         cartesian product
 S\/T        set union
 S/\T        set intersection
 S-T         set difference
 E:S         element of
 E/:S        not element of
 S<:T        subset of
 S/<:T       not subset of
 S<<:T       strict subset of
 S/<<:T      not strict subset of
 union(S)        generalised union over sets of sets
 inter(S)         generalised intersection over sets of sets
 UNION(z).(P|E)  generalised union with predicate
 INTER(z).(P|E)  generalised intersection with predicate

Numbers:
--------
 INTEGER     set of integers
 NATURAL     set of natural numbers
 NATURAL1    set of non-zero natural numbers
 INT         set of implementable integers (MININT..MAXINT)
 NAT         set of implementable natural numbers
 NAT1        set of non-zero implementable natural numbers
 n..m        set of numbers from n to m
 MININT      the minimum implementable integer
 MAXINT      the maximum implementable integer
 m>n         greater than
 m<n         less than
 m>=n        greater than or equal
 m<=n        less than or equal
 max(S)      maximum of a set of numbers
 min(S)      minimum of a set of numbers
 m+n         addition
 m-n         difference
 m*n         multiplication
 m/n         division
 m**n        power
 m mod n     remainder of division
 PI(z).(P|E)    Set product
 SIGMA(z).(P|E) Set summation
 succ(n)     successor (n+1)
 pred(n)     predecessor (n-1)


Relations:
----------
 S<->T     relation
 E|->F     maplet
 dom(r)    domain of relation
 ran(r)    range of relation
 id(S)     identity relation
 S<|r      domain restriction
 S<<|r     domain subtraction
 r|>S      range restriction
 r|>>S     range subtraction
 r~        inverse of relation
 r[S]      relational image
 r1<+r2    relational overriding (r2 overrides r1)
 r1><r2    direct product {x,(y,z) | x,y:r1 & x,z:r2}
 (r1;r2)     relational composition {x,y| x|->z:r1 & z|->y:r2}
 (r1||r2)    parallel product {((x,v),(y,w)) | x,y:r1 & v,w:r2}
 prj1(S,T)     projection function (usage prj1(Dom,Ran)(Pair))
 prj2(S,T)     projection function (usage prj2(Dom,Ran)(Pair))
 closure1(r)   transitive closure
 closure(r)    reflexive & transitive closure
               (non-standard version: closure({}) = {}; see iterate(r,0) below)
 iterate(r,n)  iteration of r with n>=0
               (Note: iterate(r,0) = id(s) where s = dom(r)\/ran(r))
 fnc(r)    translate relation A<->B into function A+->POW(B)
 rel(r)    translate relation A<->POW(B) into relation A<->B

Functions:
----------
  S+->T      partial function
  S-->T      total function
  S+->>T     partial surjection
  S-->>T     total surjection
  S>+>T      partial injection
  S>->T      total injection
  S>+>>T     partial bijection
  S>->>T     total bijection
  %x.(P|E)   lambda abstraction
  f(E)       function application
  f(E1,...,En)   is now supported (as well as f(E1|->E2))


Sequences:
----------
  <> or []   empty sequence
  [E]        singleton sequence
  [E,F]      constructed sequence
  seq(S)     set of sequences over Sequence
  seq1(S)    set of non-empty sequences over S
  iseq(S)    set of injective sequences
  iseq1(S)   set of non-empty injective sequences
  perm(S)    set of bijective sequences (permutations)
  size(s)    size of sequence
  s^t        concatenation
  E->s       prepend element
  s<-E       append element
  rev(s)     reverse of sequence
  first(s)   first element
  last(s)    last element
  front(s)   front of sequence (all but last element)
  tail(s)    tail of sequence (all but first element)
  conc(S)    concatenation of sequence of sequences
  s/|\n     take first n elements of sequence
  s\|/n     drop first n elements from sequence

Records:
--------
  struct(ID:S,...,ID:S)   set of records with given fields and field types
  rec(ID:E,...,ID:E)      construct a record with given field names and values
  E'ID                    get value of field with name ID

Strings:
--------
  "astring"    a specific string value
  STRING       the set of all strings
               Note: for the moment enumeration of strings is limited (if a variable
               of type STRING is not given a value by the machine, then ProB assumes
               STRING = { "STR1", "STR2" })
Trees:
------
  left, right, tree, btree, ... are recognised by the parser but not yet
               supported by ProB itself
Statements:
-----------
  skip         no operation
  x := E       assignment
  f(x) := E    functional override
  x :: S       choice from set
  x : (P)      choice by predicate P (constraining x)
  x <-- OP(x)  call operation and assign return value
  G||H         parallel substitution**
  G;H          sequential composition**
  ANY x,... WHERE P THEN G END   non deterministic choice
  LET x,... BE x=E & ... IN G END
  VAR x,... IN G END             generate local variables
  PRE P THEN G END
  ASSERT P THEN G END
  CHOICE G OR H END
  IF P THEN G END
  IF P THEN G ELSE H END
  IF P1 THEN G1 ELSIF P2 THEN G2 ... END
  IF P1 THEN G1 ELSIF P2 THEN G2 ... ELSE Gn END
  SELECT P THEN G WHEN ... WHEN Q THEN H END
  SELECT P THEN G WHEN ... WHEN Q THEN H ELSE I END
  CASE E OF EITHER m THEN G OR n THEN H ... END END
  CASE E OF EITHER m THEN G OR n THEN H ... ELSE I END END
  WHILE P1 DO INVARIANT P2 VARIANT E END

  WHEN P THEN G END  is a synonym for SELECT P THEN G END

**: cannot be used at the top-level of an operation, but needs to
  be wrapped inside a BEGIN END or another statement (to avoid
  problems with the operators ; and ||).

Machine header:
---------------
  MACHINE or REFINEMENT or IMPLEMENTATION

  Note: machine parameters can either be SETS (if identifier is all upper-case)
        or scalars (i.e., integer, boolean or SET element; if identifier is not
        all upper-case; typing must be provided be CONSTRAINTS)
  You can also use MODEL or SYSTEM as a synonym for MACHINE, as well
  as EVENTS as a synonym for OPERATIONS.

Machine sections:
-----------------
  CONSTRAINTS         P      (logical predicate)
  SETS                S;T={e1,e2,...};...
  CONSTANTS           x,y,...
  CONCRETE_CONSTANTS cx,cy,...
  PROPERTIES         P       (logical predicate)
  DEFINITIONS        m(x,...) == BODY;....
  VARIABLES          x,y,...  
  CONCRETE_VARIABLES cv,cw,...
  INVARIANT          P       (logical predicate)
  ASSERTIONS         P;...;P (list of logical predicates separated by ;)
  INITIALISATION
  OPERATIONS

Machine inclusion:
------------------
  USES list of machines
  INCLUDES list of machines
  SEES list of machines
  EXTENDS list of machines
  PROMOTES list of operations
  REFINES machine

  CSP_CONTROLLER controller  will use controller.csp to guide machine (currently disabled in 1.3)

  Note:
  Refinement machines should express the operation preconditions in terms
  of their own variables.

Definitions:
------------
  NAME1 == Expression ; NAME2(ID,...,ID) == E2 ...
Note: we now support definitions with arguments.
There are a few Definitions which can be used to influence the animator:
  GOAL == P                to define a custom Goal predicate for Model Checking
                        (the Goal is also set by using "Advanced Find...")
  SCOPE == P               to limit the search space to "interesting" nodes
  scope_SETNAME == n..n    to define custom cardinality for set SETNAME
  scope_SETNAME == n       equivalent to 1..n
  SET_PREF_MININT == n
  SET_PREF_MAXINT == n
  SET_PREF_MAX_INITIALISATIONS == n  max. number of intialisations computed
  SET_PREF_MAX_OPERATIONS == n       max. number of enablings per operation computed
  SET_PREF_SYMBOLIC == TRUE/FALSE
  SET_PREF_TIME_OUT == n             time out for operation computation in ms
  ASSERT_LTL... == "LTL Formula"  	using X,F,G,U,R LTL operators +
                                   Y,O,H,S Past-LTL operators +
                                   atomic propositions: e(OpName), [OpName], {BPredicate}
  ANIMATION_FUNCTION == e            a function (INT*INT) +-> INT or an INT
  ANIMATION_FUNCTION_DEFAULT == e    a function (INT*INT) +-> INT or an INT
                    instead of any INT above you can also use BOOL or any SET
  ANIMATION_IMGn == "PATH to .gif"   a path to a gif file
  ANIMATION_STRn == "sometext"       a string without spaces

Differences with AtelierB/B4Free
--------------------------------
Basically, we try to be compatible with Atelier B and conform to the semantics
of Abrial's B-Book and of Atelier B's reference manual
(http://www.atelierb.eu/php/documents-en.php#manuel-reference).
Here are the main differences with Atelier B:
  - tuples without parantheses are not supported; write (a,b,c) instead of a,b,c
  - relational composition has to be wrapped into parentheses; write (f;g)
  - parallel product also has to be wrapped into parentheses; write (f||g)
  - trees are not yet supported
  - the VALUES clause is not yet supported
  - definitions have to be syntactically correct and be either an expression,
    predicate or substitution;
    the arguments to definitions have to be expressions;
    definitions which are predicates or substitutions must be declared before first use
  - definitions are local to a machine
 - for ProB the order of fields in a record is not relevant (internally the fields are
    sorted), Atelier-B reports a type error if the order of the name of the fields changes
  - well-definedness: for disjunctions and implications ProB uses the L-system
    of well-definedness (i.e., for P => Q, P should be well-defined and
    if P is true then Q should also be well-defined)
 (If you discover more differences, please let us know!)

See also our Wiki for documentation:
  http://www.stups.hhu.de/ProB/index.php5/Current_Limitations
  http://www.stups.hhu.de/ProB/index.php5/Using_ProB_with_Atelier_B

Also note that there are various differences between BToolkit and AtelierB/ProB:
 - AtelierB/ProB do not allow true as predicate;
   e.g., PRE true THEN ... END is not allowed (use BEGIN ... END instead)
 - AtelierB/ProB do not allow a machine parameter to be used in the PROPERTIES
 - AtelierB/ProB require a scalar machine parameter to be typed in the
   CONSTRAINTS clause
 - In AtelierB/ProB the BOOL type is pre-defined and cannot be redefined

Other notes
------------
 ProB is best at treating universally quantified formulas of the form
 !x.(x:SET => RHS), or
 !(x,y).(x|->y:SET =>RHS), !(x,y,z).(x|->y|->z:SET =>RHS), ...;
 otherwise the treatment of !(x1,...,xn).(LHS => RHS) may delay until all values
 treated by LHS are known.
 Similarly, expressions of the form SIGMA(x).(x:SET|Expr) and PI(x).(x:SET|Expr)
 lead to better constraint propagation.
 The construction S:FIN(S) is recognised by ProB as equivalent to the Event-B
 finite(S) operator.
