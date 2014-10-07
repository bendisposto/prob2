MACHINE Simple
VARIABLES loop, loop2
INVARIANT loop : BOOL & loop2 : BOOL
INITIALISATION loop := FALSE || loop2 := FALSE
OPERATIONS
o <-- read(xx) = PRE
				loop = FALSE & loop2 = FALSE & xx : NATURAL
			 THEN
			 	o := xx ||
			 	loop := TRUE
			 END;

nothing = PRE
			loop = TRUE & loop2 = FALSE
		THEN
			loop2 := TRUE
		END;
end =	PRE
			loop = TRUE & loop2 = TRUE
		THEN
			loop := FALSE ||
			loop2 := FALSE
		END
END