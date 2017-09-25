------------------------------ MODULE Club ------------------------------- 
EXTENDS Naturals, FiniteSets
CONSTANTS capacity, NAME, total
ASSUME capacity \in Nat /\ capacity = 2
ASSUME Cardinality(NAME) > capacity
ASSUME total \in Nat /\ total > 2

VARIABLES member, waiting
----------------------------------------------------------------------------
Inv == member \subseteq NAME /\ waiting \subseteq NAME
	/\ member \cup waiting = {}
	/\ Cardinality(member) \leq 4096
	/\ Cardinality(waiting) \leq total

Init == member = {} /\ waiting = {}
----------------------------------------------------------------------------

join_queue(nn) == nn \notin member /\ nn \notin waiting 
	/\ Cardinality(waiting) < total /\ waiting' = waiting \cup {nn}
	/\ UNCHANGED member

join(nn) == nn \in waiting /\ Cardinality(member) < capacity
	/\ member' = member \cup {nn} /\ waiting' = waiting\{nn} 

remove(nn) == nn \in member /\ member' = member\{nn} /\ UNCHANGED waiting


Next == \/ (\E nn \in NAME: join_queue(nn))
	\/ (\E nn \in NAME: join(nn))
	\/ (\E nn \in NAME: remove(nn))
=============================================================================
