ValueOverTime = (function() {
	var extern = {}
	var session = Session();
	var svg = VizUtils.createCanvas("#visualization",
			$("#left-col")[0].clientWidth, VizUtils.calculateHeight());

	$(document).ready(function() {
	});

	$(".form-control").keyup(function(e) {
		session.sendCmd("parse", {
			"formula" : e.target.value,
			"id" : e.target.id
		})
	});

	$(".add_expr").click(function(e) {
		e.preventDefault();
		session.sendCmd("addFormula", {
			"time" : $("#inputTime").val(),
			"formula" : $("#inputFormula1").val(),
			"client" : extern.client
		})
	})

	function draw(whatever) {
		// TO IMPLEMENT
	}

	function parseOk(id) {
		$("#" + id).parent().removeClass("has-error")
	}

	function parseError(id) {
		$("#" + id).parent().addClass("has-error")
	}

	extern.draw = function(data) {
		draw(data.whatever);
	}
	extern.client = ""
	extern.init = session.init
	extern.parseError = function(data) {
		parseError(data.id);
	}
	extern.parseOk = function(data) {
		parseOk(data.id);
	}

	return extern;
}())