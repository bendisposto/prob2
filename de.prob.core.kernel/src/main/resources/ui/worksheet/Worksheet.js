Worksheet = (function() {
	var extern = {}
	var session = Session();

	var editors = {};

	var focused = null;

	var editorkeys = function(number) {
		return {
			'Shift-Enter' : function(cm) {
				return true;
			},
			'Up' : function(cm) {
				var pos = cm.getCursor().line;
				if (pos === 0) {
					session.sendCmd("leaveEditor", {
						"box" : number,
						"direction" : "up",
						"client" : extern.client,
						"text" : cm.getValue()
					})
					focused = null; // prevent blur event
				} else
					return CodeMirror.Pass;
			},
			'Down' : function(cm) {
				var cnt = cm.doc.lineCount();
				var pos = cm.getCursor().line;
				if (pos === cnt - 1) {
					session.sendCmd("leaveEditor", {
						"box" : number,
						"direction" : "down",
						"client" : extern.client,
						"text" : cm.getValue()
					})
					focused = null; // prevent blur event
				} else
					return CodeMirror.Pass;
			}
		}
	};

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

	function focus(number, direction) {
		var editor = $("#editor" + number)
		editor.removeClass("invisible");
		var renderer = $("#render" + number)
		renderer.addClass("invisible");
		var currentEditor = editors[number];
		if (currentEditor != null) {
			if (direction === "up") {
				var ll_pos = currentEditor.lineCount() - 1
				var ll_length = currentEditor.getLine(ll_pos).length
				currentEditor.setCursor(ll_pos, ll_length);
			} else {
				currentEditor.setCursor(0, 0);
			}
			currentEditor.focus()
		}
		focused = number;
	}

	function unfocus(number) {
		var editor = $("#editor" + number)
		editor.addClass("invisible");
		var renderer = $("#render" + number)
		renderer.removeClass("invisible");
		focused = null;
	}

	function gen_box_html(number, type, content, renderedhtml, template) {
		var co = {
			"box-number" : number,
			"box-type" : type,
			"type-selector" : session.render(
					"/ui/worksheet/type-selector.html", {
						"id" : number,
						"local" : "localselector" + number
					}),
			"rendered" : renderedhtml,
			"box-content" : content
		}
		return session.render(template, co)
	}

	function gen_codemirror(number, type) {
		var edi = CodeMirror.fromTextArea($('#textarea' + number)[0],
				eval("Languages." + type));

		edi.addKeyMap(editorkeys(number));

		edi.getWrapperElement().onkeypress = function(e) {
			if (e.shiftKey && e.keyCode === 13)
				e.preventDefault();
			if (e.keyCode === 13)
				console.log("enter")
		};
		editors[number] = edi;
		$(".CodeMirror-hscrollbar").remove(); // Hack! no horizontal scrolling
		$(".CodeMirror-vscrollbar").remove(); // Hack! no vertical scrolling
		$(".CodeMirror-scrollbar-filler").remove(); // Hack! no funny white
		// square in bottom right
		// corner
	}

	function replace_box(number, type, content, renderedhtml, template, cm) {
		var box_html = gen_box_html(number, type, content, renderedhtml,
				template)
		$("#box" + number).replaceWith(box_html)
		configure_box(number, type, cm)
	}

	function configure_box(number, type, cm) {
		$(".localselector" + number).click(function(e) {
			noFocus();
			session.sendCmd("switchType", {
				"box" : number,
				"client" : extern.client,
				"type" : e.target.id
			})
		})
		$("#remove" + number).click(function(e) {
			console.log("Remove " + number)
			session.sendCmd("deleteBox", {
				"number" : number
			})
		})
		$("#box" + number).dblclick(function(e) {
			if (!(number === focused)) {
				unfocus(focused);
				focus(number, "none")
			}
		})
		if (cm === "true") {
			gen_codemirror(number, type)
		}
		focus(number, "down")
	}

	function render_box(number, type, content, renderedhtml, template, cm) {
		var box_html = gen_box_html(number, type, content, renderedhtml,
				template)
		$("#boxes").append(box_html)
		configure_box(number, type, cm)
	}

	function save() {
		console.log("Save document")
	}
	function format() {
		console.log("Format " + focused)
	}

	function render(id, html) {
		$("#render" + id).replaceWith(
				'<div class="renderbox" id="render' + id + '">' + html
						+ '</div>')
	}
	function delete_box(id) {
		$("#box" + id).remove()
	}

	function key_handler(evt) {
		// console.log("key:", evt)
		if (evt.which == 83 && (evt.metaKey || evt.ctrlKey)) { // CMD+S
			save();
			return false;
		}
		if (evt.which == 70 && evt.shiftKey && (evt.metaKey || evt.ctrlKey)) { // SHIFT-CMD-F
			format();
			return false;
		}
		if (evt.which == 27) {
			noFocus();
		}
		return true;
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
						session.render("/ui/worksheet/type-selector.html", {
							"id" : "default"
						}))

				$("#show-header").click(function(e) {
					set_headings(e.target.checked)
				})

				jQuery(document).keydown(key_handler);

				$(".type-select").click(function(e) {
					session.sendCmd("setDefaultType", {
						"type" : e.target.id
					})
				})

				$("body").click(function(e) {
					if (e.target.nodeName === "BODY") {
						noFocus();
					}
				})

			});

	function request_files(d) {
		var s;
		$.ajax({
			url : "/files?path=" + d,
			success : function(result) {
				if (result.isOk === false) {
					alert(result.message);
				} else {
					if (result === "file") {
						s = []
					} else {
						s = JSON.parse(result);
					}
				}
			},
			async : false
		});
		return s;
	}

	function fb_select_dir(dir_dom, path) {
		$(dir_dom).val(path)
		browse2(dir_dom)
	}
	function fb_select_file(dir_dom, path) {
		$(dir_dom).val(path)
		$("#filedialog").modal('hide')
	}

	function filldialog(dirs, files, dir_dom) {
		$(".filedialog_item").remove()
		$(".filedialog_br").remove()
		var hook = $("#filedialog_content")

		var s
		for (s in dirs) {
			var file = dirs[s]
			if (!file.hidden) {
				hook.append(session.render("/ui/worksheet/fb_dir_entry.html", {
					"name" : file.name,
					"path" : file.path,
					"dom" : dir_dom
				}))
			}
		}
		// $(".filedialog_item").click(function(e) {
		// e.preventDefault();
		// var newdir = e.target.text;
		// $(dir_dom).attr("value", newdir)
		// browse2(dir_dom)
		// })
		for (s in files) {
			var file = files[s]
			if (!file.hidden) {
				hook.append(session.render("/ui/worksheet/fb_file_entry.html",
						{
							"name" : file.name,
							"path" : file.path,
							"dom" : dir_dom
						}))
			}
		}
		// $(".filedialog_item").click(function(e) {
		// e.preventDefault();
		// var newdir = e.target.text;
		// $(dir_dom).attr("value", newdir)
		// browse2(dir_dom)
		// })
	}

	function browse2(dir_dom) {
		var dir = $(dir_dom)[0].value
		// prepare dialog
		var data = request_files(dir)
		$(dir_dom).val(data.path)
		filldialog(data.dirs, data.files, dir_dom)
	}

	function browse(dir_dom) {
		$("#filedialog").modal('show')
		browse2(dir_dom)
	}

	function noFocus() {
		if (focused === null) {
			return;
		} else {
			session.sendCmd("leaveEditor", {
				"box" : focused,
				"direction" : "none",
				"client" : extern.client,
				"text" : editors[focused].getValue()
			})
			unfocus(focused)
		}
	}

	extern.client = ""
	extern.init = session.init

	extern.setDefaultType = function(data) {
		$("#defaultBoxType").text(data.type)
	}

	extern.renderMath = function(data) {
		MathJax.Hub.Queue([ "Typeset", MathJax.Hub, "box" + data.box ])();
	}

	extern.render_box = function(data) {
		render_box(data.number, data.type, data.content, data.renderedhtml,
				data.template, data.codemirror)
	}
	extern.replace_box = function(data) {
		replace_box(data.number, data.type, data.content, data.renderedhtml,
				data.template, data.codemirror)
	}

	extern.focus = function(data) {
		focus(data.number, data.direction)
	}
	extern.unfocus = function(data) {
		unfocus(data.number)
	}
	extern.render = function(data) {
		render(data.box, data.html)
	}

	extern.browse = browse
	extern.fb_select_dir = fb_select_dir
	extern.fb_select_file = fb_select_file
	extern.fb_load_file = function(dom_dir) {
		session.sendCmd("leaveEditor", {
			"box" : focused,
			"direction" : "down",
			"client" : extern.client,
			"text" : $(dom_dir)[0].value
		})
		focused = null; // prevent blur event
	}

	extern.deleteBox = function(data) {
		delete_box(data.id)
	}

	return extern;
}())