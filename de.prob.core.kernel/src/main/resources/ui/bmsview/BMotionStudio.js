bms = (function() {

	var extern = {}
	var session = Session();
	var svgCanvas = null;
	var svgElement = null;
	var isInit = false;	
	
	$(document).ready(function() {
		
		hookInputFieldListener(true)

		$("#tabs").tabs({

			activate : function(event, ui) {
				// Visualization
				if ($(ui.oldPanel).attr("id") == "tabs-1") {
					$("#result").html("")
					forceRendering()
				} else {
					$("#render").html("")
					renderEdit()
				}
			}

		});
			
	});
	
	// --------------------------------------------
	// Helper functions
	// --------------------------------------------
	jQuery.fn.toHtmlString = function() {
		return $('<div></div>').html($(this).clone()).html();
	};
	
	jQuery.expr[':'].parents = function(a,i,m){
	    return jQuery(a).parents(m[3]).length < 1;
	};
	// --------------------------------------------

	// --------------------------------------------
	// Code Mirror Editor
	// Define an extended mixed-mode that understands vbscript and
	// leaves mustache/handlebars embedded templates in html mode
	// --------------------------------------------
	var mixedMode = {
		name : "htmlmixed",
		scriptTypes : [ {
			matches : /\/x-handlebars-template|\/x-mustache/i
		} ]
	};

	var javascriptMode = {
		name : "javascript"
	};

	var editorHtml = CodeMirror.fromTextArea(document
			.getElementById("template_content"), {
		mode : mixedMode,
		tabMode : "indent",
		lineWrapping : true,
		lineNumbers : true,
		onKeyEvent : function(e, s) {
			if (s.type == "keyup") {
				forceSaveTemplate()
				renderEdit()
			}
		}
	});

	var editorJavascript = CodeMirror.fromTextArea(document
			.getElementById("template_scripting"), {
		mode : javascriptMode,
		tabMode : "indent",
		lineWrapping : true,
		lineNumbers : true,
		matchBrackets : true,
		onKeyEvent : function(e, s) {
			if (s.type == "keyup") {
				forceSaveTemplate()
				renderEdit()
			}
		}
	});

	$("#show-codeblock").click(function(e) {
		var template1 = $(".code-block");
		if (!e.target.checked) {
			template1.fadeOut();
		} else {
			template1.fadeIn();
		}
	})

	// $('#template-code-block').find('.CodeMirror').resizable({
	// resize : function() {
	// editorHtml.setSize($(this).width(), $(this).height());
	// }
	// });
	//
	// $('#scripting-code-block').find('.CodeMirror').resizable({
	// resize : function() {
	// editorJavascript.setSize($(this).width(), $(this).height());
	// }
	// });
	// --------------------------------------------

	// --------------------------------------------
	// Context Menu Configuration
	// --------------------------------------------
	$.contextMenu({
		selector : 'svg',
		items : {
			"edit" : {
				name : "Edit SVG",
				icon: "edit",
				callback : function(key, options) {
					openSvgEditor(this)
				}
			}
		}
	});
	
	function openSvgEditor(e) {
		svgElement = $(e);
		var svgstring = $(e).toHtmlString()
		$("#dialog").dialog("open");
   		svgCanvas.setSvgString(svgstring);
	}

	function saveSvg() {
		svgCanvas.getSvgString()(handleSvgData);
	}

	function handleSvgData(data, error) {
		if (error) {
			alert(error);
		} else {
			if (svgElement) {
				svgElement.replaceWith($(data))
				editorHtml.setValue($("#result").children().toHtmlString())
				forceSaveTemplate()
				renderEdit()
			}
		}
	}

	$("#dialog").dialog({
		bgiframe : true,
		autoOpen : true,
		height : 775,
		width : 1055,
		modal : true
	});

	$("#svgedit").load(function() {
		var frame = document.getElementById('svgedit');
		svgCanvas = new embedded_svg_edit(frame);
		$("#dialog").dialog("close");
	});
	
	$('#dialog').bind('dialogclose', function(event) {
		saveSvg();
	});	
	
	// --------------------------------------------

	// --------------------------------------------
	// Calls to Server
	// --------------------------------------------
	function forceRendering() {
		session.sendCmd("forcerendering", {
			"client" : extern.client
		})
	}

	function forceSaveTemplate() {
		session.sendCmd("saveTemplate", {
			"template_content" : editorHtml.getValue(),
			"template_scripting" : editorJavascript.getValue(),
			"client" : extern.client
		})
	}
	// --------------------------------------------

	// --------------------------------------------
	// Rendering
	// --------------------------------------------
	function renderEdit() {
		$("#result").html("");
		$("#result").html(editorHtml.getValue());
		initDnd()
	}

	function renderVisualization(data) {
		
		$("#render").html("");
		var template_text = editorHtml.getValue();
		
		var observer = JSON.parse(data.observer)

		jQuery.each(observer, function(i, o) {
			template_text = template_text + session.render(o.template, JSON.parse(o.data));
		});

		try {
			var output = Mustache.render(template_text + "<script>"
					+ editorJavascript.getValue() + "</script>", data);
		} catch (e) {
			template_error(e)
		}
		$("#render").html(output);
		$("*.draggable").removeClass('draggable').addClass('draggable-vis');
		
	}

	function initDnd() {
		$('.draggable').draggable({
			stop : function(e, ui) {
				editorHtml.setValue($("#result").children().toHtmlString())
			}
		});
	}
	// --------------------------------------------

	function restoreTemplate(template_content, template_scripting) {
		if (template_content != null)
			editorHtml.setValue(template_content);
		if (template_scripting != null)
			editorJavascript.setValue(template_scripting);
	}

	function restoreFormulas(formulas) {
		var id, formula, idNum;
		for ( var i = formulas.length - 1; i >= 0; i--) {
			id = formulas[i].id;
			formula = formulas[i].formula;
			$("#formulas").prepend(
					session.render("/ui/bmsview/formula_entered.html", {
						id : id,
						formula : formula
					}));
		}
		hookEnteredFieldListener()
	}
	
	function restoreObserver(observer) {
		var observerList = $('#svgedit').contents().find('#observer_list')
		for ( var i = 0; i <= observer.length - 1; i++) {
			var o = observer[i];
			var odata = JSON.parse(o.data)
			observerList.append(
			"<h3>New Observer</h3><div>" + 
			session.render("/ui/bmsview/observer/predicateObserverForm.html", odata) +
			"</div>")
		}
	}

	extern.client = ""
	extern.init = session.init
	extern.session = session

	extern.renderVisualization = function(data) {
		renderVisualization(JSON.parse(data.data))
	}

	extern.restorePage = function(data) {
		restoreFormulas(JSON.parse(data.formulas))
		restoreTemplate(data.template_content, data.template_scripting)
		renderEdit()
	}

	extern.register = function(observer, expression) {
		session.sendCmd("register", {
			"observer" : observer,
			"expression" : expression,
			"client" : extern.client
		})
	}

	extern.formulaRemoved = function(data) {
		formulaRemoved(data.id);
	}

	extern.openSvgEditor = function(e) {
		openSvgEditor(e)
	}

	// --------------------------------------------
	// Observer / Formulas
	// --------------------------------------------
	function appendNewInputField(nextId) {
		$("#formulas").append(session.render("/ui/bmsview/input_field.html", {
			id : nextId,
			value : "",
			text : "Add"
		}));
	}

	function replaceWithEnteredField(id, formula) {
		$("#" + id).removeClass("has-error");
		$("#input-" + id).replaceWith(
				session.render("/ui/bmsview/formula_entered.html", {
					id : id,
					formula : formula
				}));
	}

	function replaceWithEditField(id, formula) {
		var parentId = "#input-" + id;
		$(parentId).replaceWith(
				session.render("/ui/bmsview/input_field.html", {
					id : id,
					value : formula,
					text : "Ok"
				}));
	}

	function formulaAdded(id, formula, nextId) {

		// Append a new input field ...
		appendNewInputField(nextId);
		// ... and hook corresponding listeners
		hookInputFieldListener(true);

		// Replace formula field and ...
		replaceWithEnteredField(id, formula);
		// ... hook listener for edit and remove buttons
		hookEnteredFieldListener();

	}

	function formulaRemoved(id) {
		$("#input-" + id).remove();
	}

	function hookEnteredFieldListener() {

		$("[id^=edit-]").unbind('click');
		$("[id^=remove-]").unbind('click');

		$("[id^=edit-]").click(function(e) {
			e.preventDefault();
			var id = e.target.parentElement.id;
			var formula = $("#formula-" + id)[0].textContent;
			editFormula(id, formula);
		});
		$("[id^=remove-]").click(function(e) {
			e.preventDefault();
			var id = e.target.parentElement.id;
			session.sendCmd("removeFormula", {
				"id" : id
			});
		})

	}

	function hookInputFieldListener(newformula) {

		$(".add-formula").unbind('click');
		$(".form-control").unbind('keyup');

		$(".add-formula").click(function(e) {
			e.preventDefault();
			var id = e.target.parentNode.parentNode.id;
			session.sendCmd("addFormula", {
				"id" : id,
				"newFormula" : newformula
			});
		});

		$(".form-control").keyup(function(e) {
			session.sendCmd("parse", {
				"formula" : e.target.value,
				"id" : e.target.parentNode.id
			})
		});

	}

	function editFormula(id, formula) {
		replaceWithEditField(id, formula);
		hookInputFieldListener(false);
	}

	function formulaRestored(id, formula) {
		replaceWithEnteredField(id, formula);
		hookEnteredFieldListener();
		renderEdit()
	}
	
	function parseOk(id) {
		$("#" + id).removeClass("has-error")
		$("#btn-" + id).prop("disabled", false);
	}

	function parseError(id) {
		$("#" + id).addClass("has-error")
		$("#btn-" + id).prop("disabled", true);
	}
	
	extern.parseError = function(data) {
		parseError(data.id);
	}
	extern.parseOk = function(data) {
		parseOk(data.id);
	}

	extern.formulaAdded = function(data) {
		formulaAdded(data.id, data.formula, data.nextId);
	}
	
	extern.formulaRestored = function(data) {
		formulaRestored(data.id, data.formula);
	}
	
	extern.restoreObserver = function(observer) {
		restoreObserver(JSON.parse(observer.data))
	}	
	
	// --------------------------------------------
	
	// --------------------------------------------
	// Error Handling
	// --------------------------------------------
	extern.error = function(data) {
		error(data.id, data);
	}

	function error(id, errormsg) {
		$("#right-col").prepend(
				session.render("/ui/bmsview/error_msg.html", errormsg));
		$("#" + id).addClass("has-error");
	}
	// --------------------------------------------
	
	// --------------------------------------------
	// External API Calls
	// --------------------------------------------
	extern.executeOperation = function(op, predicate) {
		session.sendCmd("executeOperation", {
			"op" : op,
			"predicate" : predicate,
			"client" : extern.client
		})
	}
	// --------------------------------------------
		
	return extern;

}())
