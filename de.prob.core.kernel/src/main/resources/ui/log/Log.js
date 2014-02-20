Log = (function() {
	var extern = {}
	var session = Session()

	$(document).ready(function() {
	});

	function scrollDown() {
  		window.scrollTo(0,document.body.scrollHeight);
	}

	function addEntry(entry) {
		$("#content").append(session.render("/ui/log/entry.html",entry));
		scrollDown()
	}

	function addEntries(entries) {
		for (var i = 0; i < entries.length; i++) {
			addEntry(entries[i])
		}
		scrollDown()
	}

	function setTrace(trace) {
		ops = JSON.parse(trace)
		$(".op").remove()
		for (op in ops) {
			$("#content").prepend(
					'<li id="' + op + '" class="op">' + ops[op] + '</li>')
		}
	}

	extern.addEntry = function(data) {
		addEntry(JSON.parse(data.entry))
	}
	extern.addEntries = function(data) {
		addEntries(JSON.parse(data.entries))
	}
	extern.client = ""
	extern.init = session.init

	return extern;
}())