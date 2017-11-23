MACHINE ExampleMachine
VARIABLES
  x
INVARIANT
  x : 1..100
INITIALISATION
  x := 1
OPERATIONS
  out1, out2 <-- Foo(p1,p2) = SELECT p1 : 1..10 & p2 : BOOL THEN out1:= p1 || out2 := p2 || x := x + 1 END
END
