Worksheet = (function() {
	var extern = {}
	var session = Session();

	var editors = {};
	var contentGetters = {}

	var aside_classes = {}

	var focused = null;

	var compacted = false;
	var sourceview = false;

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

	function set_headings() {
		var panels = $(".panel");
		var headings = $(".panel-heading")
		var variables = $(".vars")

		b = $("#compactbtn")
		if (compacted) {
			panels.removeClass("panel-compact")
			headings.fadeIn();
			variables.fadeIn();
			b.removeClass("btn-selected")
		} else {
			panels.addClass("panel-compact")
			headings.fadeOut();
			variables.fadeOut();
			b.addClass("btn-selected")
		}
		compacted = !compacted

	}

	function focus(number, direction) {
		var editor = $("#editor" + number)
		editor.removeClass("invisible");
		var renderer = $("#render" + number)
		renderer.addClass("invisible");
		var currentEditor = editors[number];

		if (currentEditor != null) {
			if (currentEditor.codemirror != null) {
				var cm = currentEditor.codemirror
				if (direction === "up") {
					var ll_pos = cm.lineCount() - 1
					var ll_length = cm.getLine(ll_pos).length
					cm.setCursor(ll_pos, ll_length);
				} else {
					cm.setCursor(0, 0);
				}
				cm.focus()
			}
			if (currentEditor.focusFkt != null) {
				currentEditor.focusFkt();
			}
			focused = number;
		} else {
			focused = null;
		}

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
		if (eval("Languages." + type + ".no_vars") == null) {
			co.size = "col-lg-10"
		} else {
			co.size = "col-lg-12"
		}

		co.editor = session.render(template, co)
		return session.render("/ui/worksheet/box.html", co)
	}

	function gen_codemirror(number, type) {
		var edi = CodeMirror.fromTextArea($('#textarea' + number)[0],
				eval("Languages." + type + ".codemirror"));

		edi.addKeyMap(editorkeys(number));

		edi.getWrapperElement().onkeypress = function(e) {
			if (e.shiftKey && e.keyCode === 13)
				e.preventDefault();
//			if (e.keyCode === 13)
//				console.log("enter")
		};

		$(".CodeMirror-hscrollbar").remove(); // Hack! no horizontal scrolling
		$(".CodeMirror-vscrollbar").remove(); // Hack! no vertical scrolling
		$(".CodeMirror-scrollbar-filler").remove(); // Hack! no funny white
		// square in bottom right
		// corner
		return edi;
	}

	function gen_editor_data(number, type) {
		var lang_data = eval("Languages." + type)
		var editor_data = {
			id : number,
			type : type,
			getValue : lang_data.getter,
			has_source : lang_data.has_source,
		}
		if (lang_data.codemirror != null) {
			var edi = gen_codemirror(number, type);
			editor_data.codemirror = edi
			editor_data.getValue = function() {
				return this.codemirror.getValue()
			}
		}
		if (lang_data.no_vars != null) {
			editor_data.no_vars = true
		}

		var focusFkt = lang_data.focusFkt;
		if (focusFkt != null) {
			editor_data.focusFkt = lang_data.focusFkt(number)
		}

		var konstrukt = lang_data.construct
		if (konstrukt != null) {
			konstrukt(number, editor_data)
		}
		editors[number] = editor_data;
	}

	function replace_box(number, type, content, renderedhtml, template) {
		var box_html = gen_box_html(number, type, content, renderedhtml,
				template)
		$("#box" + number).replaceWith(box_html)
		configure_box(number, type)
	}

	function configure_box(number, type) {
		$(".localselector" + number).click(function(e) {

			var data = {
				"box" : number,
				"client" : extern.client,
				"type" : e.target.id,

			}
			data.text = editors[number].getValue();

			session.sendCmd("switchType", data)
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
		gen_editor_data(number, type)
		focus(number, "down")
	}

	function render_box(number, type, content, renderedhtml, template) {
		var box_html = gen_box_html(number, type, content, renderedhtml,
				template)
		$("#boxes").append(box_html)
		configure_box(number, type)
	}

	function save() {
//		console.log("Save document")
	}
	function format() {
//		console.log("Format " + focused)
	}

	function render(id, html) {
		$("#render" + id + " *").remove()
		$("#render" + id).removeClass("invalidated")
		$("#render" + id).append(html)

	}

	function invalidate(id) {
		$("#render" + id).addClass("invalidated")
	}

	function delete_box(id) {
		$("#box" + id).remove()
		$("#aside" + id).remove()
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
						placeholder : "col-lg-12 ui-sortable-placeholder",
						update : reorder,
						handle : ".panel-heading",
						refreshPositions : true,
						forcePlaceholderSize : true
					});
				});

				$("#replace-with-type-selector").replaceWith(
						session.render("/ui/worksheet/main-type-selector.html",
								{
									"id" : "default"
								}))

				jQuery(document).keydown(key_handler);

				$(".type-select").click(function(e) {
					session.sendCmd("setDefaultType", {
						"type" : e.target.id
					})
				})

				$("body").click(
						function(e) {
							if (e.target.nodeName === "BODY"
									|| e.target.nodeName === "HTML") {
								noFocus();
							}
						})

			});

	function request_files(d) {
		var s;
		$.ajax({
			url : "/files?path=" + d + "&extensions=prob",
			success : function(result) {
				if (result.isOk === false) {
					alert(result.message);
				} else {
					s = JSON.parse(result);
				}
			},
			async : false
		});
		return s;
	}
	function check_file(d) {
		var s;
		$.ajax({
			url : "/files?check=true&extensions=prob&path=" + d,
			success : function(result) {
				if (result.isOk === false) {
					alert(result.message);
				} else {
					s = JSON.parse(result);
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

	function set_ok_button_state(dir_dom, box) {
		return function() {
			var file = $(dir_dom)[0].value
			var valid = check_file(file);
			if (valid) {
				$("#fb_okbtn" + box).removeAttr("disabled")
			} else {
				$("#fb_okbtn" + box).attr("disabled", "disabled")
			}
		}
	}

	function make_listener(box, name) {
		return function() {
			$("#value_" + name + "_" + box).modal("show")
		}
	}

	function make_class_info(v) {
		var clz = {}
		clz.name = v.clazz
		clz.supertype = v.supertype
		try {
			clz.attributes = JSON.parse(v.attributes)
		} catch (e) {
			clz.attributes = []
		}
		try {
			clz.methods = JSON.parse(v.methods)
		} catch (e) {
			clz.methods = []
		}
		return clz;
	}

	function aside(boxnr, asidestr) {
		if (editors[boxnr].no_vars == true)
			return;
		var aside = JSON.parse(asidestr)

		$("#aside" + boxnr).children().remove()
		for (e in aside) {
			var v = aside[e]
			var clz = null;
			if (aside_classes[v.clazz] == null) {
				clz = make_class_info(v);
//				console.log("Classinfo: ", clz)
				aside_classes[v.clazz] = clz
			} else {
				clz = aside_classes[v.clazz]
			}
			var el = $("<div class='aside-label label "
					+ (v.fresh === "true" ? "label-primary" : "label-default")
					+ "' >" + v.name + "</div>")
			el = el.appendTo("#aside" + boxnr)
			$("#value_" + v.name + "_" + boxnr).remove();
			var co = {
				'box_number' : boxnr,
				'variable_name' : v.name,
				'variable_value' : v.value,
				'class_name' : clz.name,
				'class_super' : clz.supertype,
				'class_attributes' : clz.attributes,
				'class_methods' : clz.methods
			}
			$("body").append(
					session.render("/ui/worksheet/variable_info.html", co))
			el.click(make_listener(boxnr, v.name))
		}
	}

	function browse(dir_dom, box) {
		$('#filedialog').off('hidden.bs.modal')
		$('#filedialog').on('hidden.bs.modal',
				set_ok_button_state(dir_dom, box))
		$("#filedialog").modal('show')
		browse2(dir_dom)
	}

	function source() {
		b = $("#sourcebtn")
		if (sourceview) {
			$(".renderbox").removeClass("col-lg-5").removeClass("pull-right")

			for (e in editors) {
				var edi = editors[e]
				if (edi.has_source) {
					$("#render" + edi.id).removeClass("col-lg-5").removeClass(
							"pull-right")
					$("#edit" + edi.id).removeClass("col-lg-6").removeClass(
							"pull-left")
					$("#editor" + edi.id).addClass("invisible")

				}
			}

			b.removeClass("btn-selected")
		} else {
			for (e in editors) {
				var edi = editors[e]
				if (edi.has_source) {
					$("#render" + edi.id).addClass("col-lg-5").addClass(
							"pull-right")
					$("#edit" + edi.id).addClass("col-lg-6").addClass(
							"pull-left")
					$("#editor" + edi.id).removeClass("invisible")

				}
			}
			b.addClass("btn-selected")
		}
		sourceview = !sourceview
//		console.log("Source")
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
				data.template)
	}
	extern.replace_box = function(data) {
		replace_box(data.number, data.type, data.content, data.renderedhtml,
				data.template)
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
	extern.set_ok_button_state = set_ok_button_state

	extern.aside = function(data) {
		aside(data.number, data.aside)
	}

	extern.compact = set_headings;

	extern.refreshAll = function() {

		var data = {
			"box" : focused,
			"direction" : "none",
			"client" : extern.client,
		}

		if (focused != null) {
			data.text = editors[focused].getValue()
		}
		session.sendCmd("refreshAll", data)
		unfocus(focused)
	}

	extern.invalidate = function(data) {
		invalidate(data.number)
	}

	extern.source = source

	// Debugging
	extern.editors = editors
	extern.focused = focused

	return extern;
}())