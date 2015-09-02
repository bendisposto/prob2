MACHINE
    M2

INCLUDES
    MC.M3,
    MD.M3

PROMOTES
    MC.set,
    MD.set

VARIABLES
    vv
INITIALISATION
    vv := FALSE
INVARIANT
    vv:BOOL
OPERATIONS
    set(xx) =
    PRE
        xx:BOOL
        & xx/=vv
    THEN
        vv:=xx
    END
END
