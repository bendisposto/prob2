StateInspector = (function() {
	var extern = {}
	var session = Session();
	var cm = null;
	;
	history = [];
	hp = null;

	var editorkeys = function() {
		return {
			'Shift-Enter' : function(cm) {
				cm.replaceSelection("\n", "end", "+input");
				cm.indentLine(cm.getCursor().line, null, true);
				return true;
			},
			'Enter' : function(cm) {
				this.hp = null;
				var code = cm.getValue();
//				console.log("submit: '" + code + "'")
				session.sendCmd("evaluate", {
					"code" : code
				})
				this.history.push(code)
				cm.setValue("")
				return false;
			},
			'Up' : function(cm) {
				if (cm.getCursor().line == 0) {
//					console.log("History up")
					if (this.hp == null) {
						this.hp = this.history.length;
					}
					if (this.hp > 0) {
						this.hp--
						cm.setValue(this.history[this.hp])
					}
				} else
					this.hp = null;
				return CodeMirror.Pass;
				;
			},
			'Down' : function(cm) {
				var cnt = cm.doc.lineCount();
				if (cm.getCursor().line == cnt - 1) {
//					console.log("History down")
					if (this.hp != null) {
						if (this.hp < this.history.length - 1) {
							this.hp++
							cm.setValue(this.history[this.hp])
						} else {
							cm.setValue("")
						}

					}
				} else
					return CodeMirror.Pass;
				;
			}
		}
	};

	$(document).ready(function() {
	});

	function clearInput() {
		$("#content").replaceWith("<table id='content' class='table table-bordered'></table>");
	}

	function setModel(model) {
		$("#content").replaceWith(
				session.render("/ui/stateInspector/model_format.html", model))
        $("#content").colResizable()
	}

	function updateValues(values) {
		for ( var i = 0; i < values.length; i++) {
			$("#" + values[i].id).replaceWith(
					session.render("/ui/stateInspector/entry_format.html",
							values[i]));
			if (values[i].current !== values[i].previous) {
				$("#" + values[i].id).addClass("changed");
			}
		}
	}

	function updateHistory(history) {
		this.history = history
		this.hp = this.history.length
	}

	function showresult(data) {
		var output = session.render("/ui/stateInspector/shell_answer.html", data)
		$(".outbox").append(output)
	}

	function init() {
		cm = CodeMirror.fromTextArea($('#input')[0], {
			mode : 'b',
			lineNumbers : false,
			lineWrapping : true,
			theme : "default",
			viewportMargin : Infinity
		});

		cm.addKeyMap(editorkeys());

		$(".CodeMirror-hscrollbar").remove(); // Hack! no horizontal scrolling
		$(".CodeMirror-vscrollbar").remove(); // Hack! no vertical scrolling
		$(".CodeMirror-scrollbar-filler").remove(); // Hack! no funny white
		// square in bottom right
		// corner
	}

	function disable() {
        $("body").append("<div class='modal-backdrop disabled'></div>")
    }

    function enable() {
        $(".disabled").remove()
    }

    function clearResults() {
    	$(".outbox").empty()
    	cm.getDoc().setValue('')
    }

	extern.setModel = function(data) {
		setModel(JSON.parse(data.components))
		updateValues(JSON.parse(data.values))
		clearResults()
		updateHistory(JSON.parse(data.history))
	}
	extern.updateValues = function(data) {
		updateValues(JSON.parse(data.values))
	}
	extern.clearInput = clearInput;

	extern.client = ""
	extern.init = function() {
		session.init()
		init()
	}
	extern.result = showresult
	extern.cm = function() {
		return cm;
	}
	extern.disable = disable
    extern.enable = enable

	return extern;
}())