MACHINE
    M3
VARIABLES
    bb
INITIALISATION
    bb := FALSE
INVARIANT
    bb:BOOL
OPERATIONS
    set(xx) =
    PRE
        xx:BOOL
        & xx/=bb
    THEN
        bb:=xx
    END
END
