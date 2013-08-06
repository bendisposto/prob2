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

	function set_headings(b) {
		var panels = $(".panel");
		var headings = $(".panel-heading")

		if (!b) {
			panels.removeClass("panel-compact")
			headings.fadeIn();
		} else {
			panels.addClass("panel-compact")
			headings.fadeOut();
		}

	}

	function render_box(number, type, content) {
		var co = {
			"box-number" : number,
			"box-type" : type,
			"type-selector" : function() {
				return function() {
					return session.render("/ui/worksheet/type-selector.html",
							{})
				}
			},
			"box-content" : content
		}
		return session.render("/ui/worksheet/box.html", co)
	}

	$(document).ready(
			function() {
				$(function() {
					$("#boxes").sortable({
						placeholder : "ui-sortable-placeholder",
						update : reorder,
						handle : ".panel-heading",
						forcePlaceholderSize : true
					});
				});

				$("#replace-with-type-selector").replaceWith(
						session.render("/ui/worksheet/type-selector.html", {}))

				$("#show-header").click(function(e) {
					set_headings(e.target.checked)
				})

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
	extern.render_box = render_box

	return extern;
}())