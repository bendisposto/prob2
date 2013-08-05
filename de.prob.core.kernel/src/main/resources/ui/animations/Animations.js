Animations = (function() {
	var extern = {}
	var session = Session();

	$(document).ready(function() {
	});

	function setContent(data) {
		ops = JSON.parse(data.animations)
		$(".op").remove()
		for (op in ops) {
			var co = ops[op]
			$("#content").append(
					session.render("/ui/animations/animations_table.html", co))
		}
		$(".op").click(function(e) {
			// clickTrace(e.target.id)
		})
	}

	function clickTrace(id) {
		session.sendCmd("gotoPos", {
			"pos" : id,
			"client" : extern.client
		})
	}

	extern.client = ""
	extern.init = session.init
	extern.setContent = setContent

	return extern;
}())