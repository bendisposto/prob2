import de.prob.animator.domainobjects.*
import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.AlgorithmTranslator
import de.prob.model.eventb.translate.*
import de.prob.statespace.*


mm = new ModelModifier().make {
	
	context(name: "limits") {
		constants "maxInt", "myNAT", "myINT"

		axioms "maxInt : NAT",
		 "myNAT = 0..maxInt",
		"myINT = (-maxInt)..maxInt"
    }
	
	context(name: "csts", extends: "limits") {
		constants "a", "aSize"
		
		axioms "aSize : myNAT",
			"a : 0..aSize-1 --> myINT",
			"!j,k.j : dom(a) & k : dom(a) & j < k => a(j) < a(k)"
			
		theorem "aSize <= maxInt"
		theorem	"!x.x : dom(a) => (!j.j : x+1..aSize-1  => a(x) < a(j))"
		theorem	"!x.x : dom(a) => (!j.j : 0..x-1 => a(x) > a(j))"
		theorem	"!x,k.x : dom(a) & k : myINT & a(x) < k => (!j.j : 0..x => a(j) /= k)"
		theorem	"!x,k.x : dom(a) & k : myINT & a(x) > k => (!j.j : x..aSize-1 => a(j) /= k)"
		theorem "!l,h. l <= h => l <= l + (h - l)/2"
		theorem "!l,h. l = h => (h - l)/2 <= h/2 - l/2"
		//theorem "!l,h.l : myNAT & h : myNAT & l < h & (h - l) mod 2 = 0 => (h - l)/2 = h/2 - l/2"
		//theorem "!l,h.l : myNAT & h : myNAT & l < h & (h - l) mod 2 = 1 & h mod 2 = 0 & l mod 2 = 1 => (h - l)/2 = h/2 - l/2"
		//theorem "!l,h.l : myNAT & h : myNAT & l < h & (h - l) mod 2 = 1 & h mod 2 = 1 & l mod 2 = 0 => (h - l)/2 = h/2 - l/2"
		//theorem "!l,h.l : myNAT & h : myNAT & l < h => (h - l)/2 <= h/2 - l/2"
		theorem "!l,h. l = h => l + (h - l)/2 <= h"
		theorem "!l,h. l < h => l + (h - l)/2 <= h"
		theorem "!l,h. l <= h => l + (h - l)/2 <= h"
		theorem "!l,h. l : dom(a) & h : dom(a) & l <= h => l + (h - l)/2 : dom(a)"
		theorem "!l,h. l : 0..aSize & h : -1..aSize-1 & l<=h => l : dom(a) & h : dom(a)"
		theorem "!l,h. l : 0..aSize & h : -1..aSize-1 & l<=h => l + (h - l)/2 : dom(a)"
		theorem "!x,y. x : myNAT & y : myNAT & x <= y => y - x <= maxInt"
		
	}

	procedure(name: "binarySearch", seen: "csts") {
		argument "key","myINT"
		result "pos","{-1}\\/dom(a)"
        
		precondition "TRUE=TRUE"
		postcondition "(pos = -1 => not(key : ran(a))) & (not(pos = -1) => pos : dom(a) & a(pos) = key)"
		
		implementation {
			var "low", "low : 0..aSize", "low := 0"
			var "high", "high : -1..aSize-1", "high := aSize - 1"
			var "mid", "mid : myNAT", "mid := 0"
			
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
				Assert("mid : 0..aSize-1")
				While("low <= high", invariant: "mid : 0..aSize-1") {
					Assign(" mid := low + (high - low) / 2")
					Assert("mid : dom(a)")
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
									Assert("a[{mid}] = {key}")
									Return("mid")							
								}
							}		
						}
					}
				}
				Assign("high := -1")
				Assert("high = -1")
				Assert("low > high") 
				Return("high")
			}
		}
	}
}

m = mm.getModel()
m = new AlgorithmTranslator(m, new AlgorithmGenerationOptions().DEFAULT).run()

mtx = new ModelToXML()
//d = mtx.writeToRodin(m, "GroovyBS", "/tmp")
//d.deleteDir()

"generating a model from an algorithm"