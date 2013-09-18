CurrentTrace = (function() {
	var extern = {}
	var session = Session();

	$(document).ready(function() {
	});

	function setTrace(trace) {
		ops = JSON.parse(trace)
		$(".op").remove()
		for (op in ops) {
			$("#content").prepend(
					'<li id="' + op + '" class="op">' + ops[op] + '</li>')
		}
		$(".op").click(function(e) {
			clickTrace(e.target.id)
		})
	}

	function clickTrace(id) {
		session.sendCmd("gotoPos", {
			"pos" : id,
			"client" : extern.client
		})
	}

	extern.setTrace = function(data) {
		setTrace(data.trace)
	}
	extern.client = ""
	extern.init = session.init

	return extern;
}())