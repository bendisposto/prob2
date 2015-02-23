CurrentTrace = (function() {
	var extern = {}
	var session = Session()
	var pattern = '<li id="{{id}}" class="{{group}}">{{rep}}</li>'
	var sortDown = true

	$(document).ready(function() {
		$("#sort").click(function(e) {
			sortDown = !sortDown
			session.sendCmd("changeSort", {
				"sortDown" : sortDown
			})
		})
	});

	function setTrace(trace) {
		var ops = JSON.parse(trace)
		$("li").remove()
		for (var i = 0; i < ops.length; i++) {
			$("#content").append(Mustache.render(pattern,ops[i]))
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

    function disable() {
        $("body").append("<div class='modal-backdrop transparent'></div>")
    }

    function enable() {
        $(".transparent").remove()
    }

	extern.setTrace = function(data) {
		setTrace(data.trace)
	}
	extern.client = ""
	extern.init = session.init
	extern.disable = disable
    extern.enable = enable

	return extern;
}())