MACHINE MultipleExample
OPERATIONS
  a,b,c  <-- Set = BEGIN a :=1 || b := 2 || c := 3 END;

  a,b <-- Crazy1(p) =
  	SELECT
  		p : INTEGER
  	THEN
  		a := p ||
  		b := p + 4
  	END;

  a,b,c <-- Crazy2(p1, p2) =
  	SELECT
  		p1 : INTEGER &
  		p2 : POW(INTEGER)
  	THEN
  		a := card(p2) ||
  		b := {(p1,p2)} ||
  		c := p1 + 1
  	END
END