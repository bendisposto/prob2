Log = (function() {
	var extern = {}
	var session = Session()
	var template = "<li class=\"logged {{type}}\"><strong>{{level}}</strong> {{from}} - {{msg}}</li>"

	$(document).ready(function() {
	});

	function scrollDown() {
  		window.scrollTo(0,document.body.scrollHeight);
	}

	function setTrace(trace) {
		ops = JSON.parse(trace)
		$(".op").remove()
		for (op in ops) {
			$("#content").prepend(
					'<li id="' + op + '" class="op">' + ops[op] + '</li>')
		}
	}

	extern.setTrace = function(data) {
		setTrace(data.trace)
	}
	extern.client = ""
	extern.init = session.init

	return extern;
}())