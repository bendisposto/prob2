Events = (function() {
	var extern = {}
	var session = Session();

	$(document).ready(function() {
	});

	function setContent(ops_string) {
		var ops = JSON.parse(ops_string);
		var e = $("#events")
		e.children().remove()
		for (el in ops) {
			e.append('<li operation="' + el + '">' + ops[el] + '</li>')
		}
		$("li").click(function(e) {
			var id = e.target.getAttribute("operation")
			session.sendCmd("execute", {
				"id" : id,
				"client" : extern.client
			})
		})
	}

	extern.client = ""
	extern.init = session.init
	extern.setContent = function(data) {
		setContent(data.ops)
	}

	return extern;
}())