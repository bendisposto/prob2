LtlFormula = (function() {
	var extern = {}
	var session = Session();

	$(document).ready(function() {
	});

	function setContent(formulargs) {
		formulas = JSON.parse(formulargs)
		$(".formula").remove()
		for (formula in formulas) {
			var co = formulas[formula]
			console.log("foo")
			console.log(co)
						
			$("#content").append(
					session.render("/ui/ltlFormula/formula_table.html", co))
		}
		$(".formula-click").click(function(e) {
			clickFormula(e.target.id)
		})
		$(".close-button").click(function(e) {
			removeFormula(e.target.id)
		})
	}

	function clickFormula(id) {
		session.sendCmd("checkNthFormula", {
			"pos" : id,
			"client" : extern.client
		})
	}
	
	function removeFormula(id) {
		session.sendCmd("removeFormula", {
			"pos" : id,
			"client" : extern.client
		})
	}

	extern.setFormulas = function(data) {
		setContent(data.formulas)
	}
	extern.client = ""
	extern.init = session.init

	return extern;
}())