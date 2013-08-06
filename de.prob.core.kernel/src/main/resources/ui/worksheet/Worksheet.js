Worksheet = (function() {
	var extern = {}
	var session = Session();

	function reorder(evt, ui) {
		var boxes = $(".panel")
		var p = $.inArray(ui.item.context, boxes)
		var box = ui.item.context.getAttribute("box")
		session.sendCmd("reorder", {
			"box" : box,
			"newpos" : p,
			"client" : extern.client
		})
	}
	
	function toggle_headings() {
		var panels = $(".panel");
		var headings = $(".panel-heading")
		
		if (panels.hasClass("panel-compact")) {
			panels.removeClass("panel-compact")
			headings.fadeIn();
		}
		else {
			panels.addClass("panel-compact")
			headings.fadeOut();
		}
		
	}

	

	$(document).ready(function() {
		$(function() {
			$("#boxes").sortable({
				placeholder : "ui-sortable-placeholder",
				update : reorder,
				handle : ".panel-heading",
				forcePlaceholderSize : true
			});
		});

	});

	// function setTrace(data) {
	// ops = JSON.parse(data.trace)
	// $(".op").remove()
	// for (op in ops) {
	// $("#content").prepend('<li id="'+op+'" class="op">' + ops[op] + '</li>')
	// }
	// $(".op").click(function(e) {
	// clickTrace(e.target.id)
	// })
	// }
	//
	// function clickTrace(id) {
	// session.sendCmd("gotoPos", {
	// "pos" : id,
	// "client" : extern.client
	// })
	// }

	// extern.setTrace = setTrace

	extern.client = ""
	extern.init = session.init
	extern.toggle_headings = toggle_headings

	return extern;
}())