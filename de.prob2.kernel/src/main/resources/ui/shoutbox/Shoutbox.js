Shoutbox = (function() {
	var extern = {}
	var pattern = '<li class="{{group}}">{{text}} <em>({{time}})</em></li>'
	var session = Session()

	function setTexts(texts) {
		var lines = JSON.parse(texts)
		$("li").remove()
		for ( var i = 0; i < lines.length; i++) {
			$("#content").prepend(Mustache.render(pattern, lines[i]))
		}
	}

	$(document).ready(function() {
		$("#send").click(function(e) {
			var text = $("#shout").val()
			session.sendCmd("addText", {
				"text" : text
			})
		})
	});

	function append(text) {
		var line = JSON.parse(text)
		$("#content").prepend(Mustache.render(pattern, line))
	}

	extern.setText = function(data) {
		setTexts(data.texts)
	}
	extern.append = function(data) {
		append(data.line)
	}
	extern.send = send
	extern.client = ""
	extern.init = session.init
	return extern;
}())