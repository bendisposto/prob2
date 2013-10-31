CurrentTrace = (function() {
	var extern = {}
	var session = Session()
	var pattern = '<li id="{{id}}" class="{{group}}">{{rep}}</li>'

	$(document).ready(function() {
	});

	function setTrace(trace) {
		ops = JSON.parse(trace)
		$("li").remove()
		for (var i = 0; i < ops.length; i++) {
			$("#content").append(Mustache.render(pattern,ops[i]));
		}
		$("li").click(function(e) {
			clickTrace(e.target.id)
		})
	}

	function clickTrace(id) {
		session.sendCmd("gotoPos", {
			"id" : id,
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