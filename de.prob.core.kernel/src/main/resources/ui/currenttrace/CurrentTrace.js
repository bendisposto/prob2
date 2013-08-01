CurrentTrace = (function() {
	var extern = {}
	var session = Session();

	$(document).ready(function() {
	});

	function setTrace(data) {
		ops = JSON.parse(data.trace)
		$(".op").remove()
		for (op in ops) {
			$("#content").prepend('<div class="op">' + ops[op] + '</div>')
		}
	}

	extern.setTrace = setTrace
	extern.client = ""
	extern.init = session.init

	return extern;
}())