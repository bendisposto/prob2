import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.AlgorithmTranslator

final mm = new ModelModifier().make {
	context(name: "limits") {
		constants "MAXINT", "NATURAL", "INTEGER"
		
		axioms "MAXINT : NAT",
		 "NATURAL = 0..MAXINT",
		"INTEGER = (-MAXINT)..MAXINT"
	}
	
	context(name: "constants_correct", extends: "limits") {
		constants "a", "aSize"
		
		axioms "aSize : NATURAL",
			"a : 0..aSize-1 --> INTEGER",
			"!j,k.j : dom(a) & k : dom(a) & j < k => a(j) <= a(k)"
		
		theorem "aSize <= MAXINT"
		//theorem	"!x.x : dom(a) => (!j.j : x+1..aSize-1  => a(j)>a(x))"
		//theorem	"!x.x : dom(a) => (!j.j : 0..x-1 => a(x) > a(j))"
		theorem	"!x,k.x : dom(a) & k : INTEGER & a(x) < k => (!j.j : 0..x => j |-> k /: a)"
		theorem	"!x,k.x : dom(a) & k : INTEGER & a(x) > k => (!j.j : x..aSize-1 => j |-> k /: a)"
		theorem "!l,h. l <= h => l <= l + (h - l)/2"
		theorem "!l,h. l = h => l + (h - l)/2 <= h"
		theorem "!l,h. l < h => l + (h - l)/2 <= h"
		theorem "!l,h. l <= h => l + (h - l)/2 <= h"
		theorem "!l,h. l : dom(a) & h : dom(a) & l <= h => l + (h - l)/2 : dom(a)"
		theorem "!l,h. l : 0..aSize & h : -1..aSize-1 & l<=h => l : dom(a) & h : dom(a)"
		theorem "!l,h. l : 0..aSize & h : -1..aSize-1 & l<=h => l + (h - l)/2 : dom(a)"
		
		theorem "!x,y,z. x <= y & y <= z => x <= z"
		theorem "!x,y. 0 <= x & x <= y & y <= MAXINT => 0 <= y - x & y - x <= MAXINT"
		theorem "!x,y. 0 <= x & x <= y & y <= MAXINT => 0 <= (y - x) / 2 & (y - x) / 2 <= MAXINT"
		theorem "!x,y. 0 <= x & x <= y & y <= MAXINT => 0 <= x + (y - x) / 2 & x + (y - x) / 2 <= MAXINT"
		theorem "!x,y. x : 0..aSize & y : -1..aSize-1 & x <= y => y - x : NATURAL"
		theorem "!x,y. x : 0..aSize & y : -1..aSize-1 & x <= y => (y - x) / 2 : NATURAL"
		theorem "!x,y. x : 0..aSize & y : -1..aSize-1 & x <= y => x + (y - x) / 2 : NATURAL"
		
		
	}
	
	procedure(name: "binarySearch", seen: "constants_correct") {
		argument "key","INTEGER"
		result "pos","{-1}\\/dom(a)"
		
		precondition "TRUE=TRUE"
		postcondition "(pos = -1 => not(key : ran(a))) & (not(pos = -1) => pos : dom(a) & a(pos) = key)"
		
		implementation {
			var "low", "low : 0..aSize", "low := 0"
			var "high", "high : -1..aSize-1", "high := aSize - 1"
			var "mid", "mid : NATURAL", "mid := 0"
			var "midVar", "midVar : INTEGER", "midVar :: INTEGER"
			
			theorem  "low > high => 0..low-1 \\/ high+1..aSize-1 = 0..aSize-1"
			
			invariants "!x.x : 0..low-1 => x|->key /: a",
			  "!x.x : high+1..aSize-1 => x|->key /: a"
			
			algorithm {
				If("high < low") {
					Then {
						Assert("high = -1 & a = {}")
						Return("high")
					}
				}
				While("low <= high", variant: "high - low + 1") {
					Assert("low <= high")
					Assert("high - low : NATURAL")
					Assert("(high - low) / 2 : NATURAL")
					Assert("low + (high - low) / 2 : NATURAL")
					//Assert("mid : dom(a)")
					Assign("mid := low + (high - low) / 2")
					Assert("mid : dom(a)")
					Assign("midVar := a(mid)")
					//Assert("mid : dom(a)")
					If("a[{mid}]={midVar} & midVar < key") {
						Then {
							Assert("a[{mid}]={midVar} & midVar < key")
							Assign("low := mid + 1")
						}
						Else {
							If("a[{mid}]={midVar} & midVar > key") {
								Then {
									Assert("a[{mid}]={midVar} & midVar > key")
									Assign("high := mid - 1")
								} 
								Else {
									Assert("a[{mid}]={midVar} & midVar = key")
									Return("mid")
								}
							}
						}
					}
					//Assert("a[{mid}]={midVar}")
					Assert("high - low + 1 >= 0")
				}
				Assert("low > high")
				Assign("high := -1")
				Assert("high = -1")
				Assert("low > high") 
				Return("high")
			}
		}
	}
	
	/*context(name: "csts_fail", extends: "limits") {
		constants "a", "aSize"
		
		axioms "aSize : NATURAL",
			"a : 0..aSize-1 --> INTEGER",
			"!j.j : dom(a) & j < aSize-1 => a(j) <= a(j+1)",
			"MAXINT = 4",
			"aSize = MAXINT"
		
	}
	
	procedure(name: "binarySearchFail", seen: "csts_fail") {
		argument "key","INTEGER"
		result "pos","{-1}\\/dom(a)"
		
		precondition "!i.i : dom(a) => key > a(i)"
		postcondition "(pos = -1 => not(key : ran(a))) & (not(pos = -1) => pos : dom(a) & a(pos) = key)"
		
		implementation {
			var "low", "low : 0..aSize", "low := 0"
			var "high", "high : -1..aSize-1", "high := aSize - 1"
			var "mid", "mid : NATURAL", "mid := 0"
			
			algorithm {
				While("low <= high") {
					Assign("mid := low + high")
					Assign("mid := mid / 2")
					If("a(mid) < key") {
						Then {
							Assign("low := mid + 1")
						}
						Else {
							If("a(mid) > key") {
								Then {
									Assign("high := mid - 1")
								}
								Else {
									Return("mid")
								}
							}
						}
					}
				}
				Assign("high := -1")
				Return("high")
			}
		}
	}*/
}

final m = new AlgorithmTranslator(mm.model, new AlgorithmGenerationOptions().DEFAULT.terminationAnalysis(true)).run()

"generating a model of a binary search algorithm"
