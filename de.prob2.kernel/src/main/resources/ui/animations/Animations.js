Animations = (function() {
	var extern = {}
	var session = Session();

	$(document).ready(function() {
	});

	function setContent(animations) {
		ops = JSON.parse(animations)
		$(".animation").remove()
		for (op in ops) {
			var co = ops[op]
			co.id = op;
			co.protected = co.protected === "true"
			if (co.isCurrent === "true") {
				co.selected = "selected"
			} else {
				co.selected = "notSelected"
			}
			// co = {id: 5, model:"scheduler", steps: 17, lastOp: "5=[2,7]", selected: notselected}
			
			$("#content").append(
					session.render("/ui/animations/animations_table.html", co))
		}
		$(".animation-click").click(function(e) {
			e.preventDefault()
			selectTrace(e.delegateTarget.id)
		})
		$(".close-button").click(function(e) {
			e.preventDefault()
			removeTrace(e.target.id)
		})
		$(".protect").click(function(e) {
			protectTrace(e.target.parentElement.id, $(e.target).is(":checked"))
		})
	}

	function selectTrace(id) {
		session.sendCmd("selectTrace", {
			"pos" : id,
			"client" : extern.client
		})
	}

	function removeTrace(id) {
		session.sendCmd("removeTrace", {
			"pos" : id,
			"client" : extern.client
		})
	}

	function protectTrace(id, protect) {
		session.sendCmd("protectTrace", {
			"pos" : id,
			"protect" : protect,
			"client" : extern.client
		})
	}

	extern.client = ""
	extern.init = session.init
	extern.setContent = function(data) {
		setContent(data.animations)
	}

	return extern;
}())