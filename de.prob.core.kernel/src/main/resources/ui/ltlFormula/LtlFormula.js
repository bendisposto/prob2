LtlFormula = (function() {
	var extern = {}
	var session = Session();

	$(document).ready(function() {
	});

	function setFormulas(formulargs, stati) {
		formulas = JSON.parse(formulargs)
		$(".formula").remove()
		for (formula in formulas) {
			$("#content").prepend(
					'<li id="' + formula + '" class="formula">' + formulas[formula] + '</li>')
		}
		$(".formula").click(function(e) {
			clickFormula(e.target.id)
		})
	}

	function clickFormula(id) {
		session.sendCmd("gotoPos", {
			"pos" : id,
			"client" : extern.client
		})
	}

	extern.setFormulas = function(data) {
		setFormulas(data.formulas, data.stati)
	}
	extern.client = ""
	extern.init = session.init

	return extern;
}())