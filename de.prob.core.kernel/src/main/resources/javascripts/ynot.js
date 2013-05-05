var globalcount = 0;

var currentMode = null;

var editors = [];

var currentEditor = null; // position in the editors-array

var boxtemplate = ' \
  <div class="worksheetbox row" id="wsbox{{id}}"> \
	<div class="span10 editorbox"> \
       <textarea id="code{{id}}" name="code{{id}}" rows="3"></textarea> \
       <div id="renderer{{id}}" style="display: none" class="renderer"></div> \
   </div> \
   <div class="controlfield"> \
	<button class="close"><a href="javascript:deleteEditorById({{id}})" class="close"> &times; </a></button> \
	<div class="btn-group"> \
     <button id="lang_button{{id}}" class="btn btn-mini btn-primary dropdown-toggle" data-toggle="dropdown">{{lang}} <span class="caret"></span></button> \
	  <ul id="ts_menu" class="dropdown-menu"> \
		<li><a href="#"	onclick="javascript:setBoxType({{id}},settings.groovy)">Groovy</a></li> \
		<li><a href="#" onclick="javascript:setBoxType({{id}},settings.b)">B</a></li> \
	    <li><a href="#" onclick="javascript:setBoxType({{id}},settings.markdown)">Markdown</a></li> \
	    <li><a href="#"	onclick="javascript:setBoxType({{id}},settings.javascript)">JavaScript	(Browser)</a></li> \
	  </ul> \
     </button> \
    </div> \
  </div> \
</div> \
';

function setBoxType(box, options) {
	createEditor(options);
	var on = findEditor(box)
	var oldE = editors[on];
	var text = oldE.codemirror.getValue();
	var newE = editors.splice(-1)[0];
	newE.codemirror.setValue(text);
	editors[on] = newE;
	$("#wsbox" + oldE.id).replaceWith($("#wsbox" + newE.id))
	$("#lang_button" + newE.id).html(function() {
		return newE.options.lang + '  <span class="caret"></span>'
	});

}

function reEval(rep) {
	console.log('Reevaluate Worksheet, starting with pos ', rep);
}

function reorder(e, x) {
	boxes = $(".worksheetbox");
	neditors = [];
	op = findEditor(x.item[0].id.substr(5));
	for ( var i = 0; i < boxes.length; i++) {
		id = boxes[i].id.substr(5);
		e = findEditor(id);
		neditors.push(editors[e]);
	}
	editors = neditors;
	np = findEditor(x.item[0].id.substr(5));
	rep = Math.min(op, np);
	reEval(rep)
}

function init() {
	currentMode = settings.markdown;
	createEditor(currentMode);
	$(function() {
		$("#boxes").sortable({
			placeholder : "ui-sortable-placeholder",
			update : reorder,
			handle : ".renderer",
			forcePlaceholderSize : true
		});
	});
}
// <div class="span1 topmenu">{{lang}}</div> \

function deleteEditorById(id) {
	nr = findEditor(id);
	deleteEditor(nr);
}

function deleteEditor(nr) {
	if (currentEditor > nr) {
		currentEditor--;
	}

	var rme = editors.splice(nr, 1)[0];
	$("#wsbox" + rme.id).remove();

	if (nr === currentEditor) {
		var info = editors[currentEditor];
		disableRenderer(info);
		enableEditor(info);
		info.codemirror.focus();
	} else if (currentEditor === editors.length) {
		createEditor(currentMode);
	}

	return rme;
}

function freshId() {
	return globalcount++;
}

function setDefaultType(mode) {
	currentMode = mode;
	$("#ts_button").html(function() {
		return mode.lang + " <span class='caret'></span>"
	});
}

function createEditor(options) {
	var id = freshId();

	var view = {
		'id' : id,
		'lang' : options.lang,

	};
	var output = Mustache.render(boxtemplate, view);
	$(output).appendTo("#boxes");
	var box = $("#wsbox" + id)
	var textarea = $("#code" + id)[0];
	var editor = CodeMirror.fromTextArea(textarea, options.codemirror);
	var renderer = $("#renderer" + id);
	// $("#wsbox" + id + " .CodeMirror").addClass('span11')

	$(".CodeMirror-hscrollbar").remove(); // Hack! no horizontal scrollbars
	var editorinfo = {
		'codemirror' : editor,
		'renderer' : renderer,
		'options' : options,
		'box' : box[0],
		'id' : id
	};

	configureCodemirror(editorinfo);
	editors.push(editorinfo);
	editor.focus();
}

function nextEditor() {
	currentEditor++;
	if (currentEditor === editors.length) {
		createEditor(currentMode);
	} else {
		var info = editors[currentEditor];
		disableRenderer(info);
		enableEditor(info);
		info.codemirror.focus();
	}
}

function previousEditor() {
	if (currentEditor > 0) {
		currentEditor--;
		var info = editors[currentEditor];
		disableRenderer(info);
		enableEditor(info);
		info.codemirror.focus();
	}
}

function configureCodemirror(info) {

	// Evaluate on unfocus

	// if (info.id % 2 === 0) { // used for debugging stylesheets

	info.codemirror.on("blur", function() {
		info.codemirror.save();
		disableEditor(info);
		enableRenderer(info);
		pos = findEditor(info.id)
		reEval(pos);
	});
	// }

	// prevent newline if Shift +Enter is pressed
	info.codemirror.getWrapperElement().onkeypress = function(e) {
		if (event.shiftKey && event.keyCode === 13)
			e.preventDefault();
	};

	info.codemirror.addKeyMap({
		'Shift-Enter' : function(cm) {
			nextEditor();
			return true;
		},
		'Up' : function(cm) {
			var pos = cm.getCursor().line;
			if (pos === 0) {
				previousEditor();
			} else
				return CodeMirror.Pass;
		},
		'Down' : function(cm) {
			var cnt = cm.doc.lineCount();
			var pos = cm.getCursor().line;
			if (pos === cnt - 1) {
				nextEditor();
			} else
				return CodeMirror.Pass;
		}
	});

	info.renderer.dblclick(function(evt) {
		disableRenderer(info);
		enableEditor(info);
	});

}

function enableRenderer(info) {
	var editor = info.codemirror;
	var text = editor.getValue();
	var evalfkt = info.options.evalfkt;
	var renderer = info.renderer;
	var output = evalfkt(text, info);
	renderer[0].style.display = "block";
	MathJax.Hub.Queue([ "Typeset", MathJax.Hub, renderer[0] ]);
	$("#wsbox" + info.id).height(renderer.height() + 8);
}

function disableRenderer(info) {
	info.renderer[0].style.display = "none";
}

function enableEditor(info) {
	var editor = info.codemirror;
	var wrapper = editor.getWrapperElement();
	wrapper.style.display = "block";
	currentEditor = findEditor(info.id);
	editor.focus();
}

function findEditor(id) {
	for ( var i = 0; i < editors.length; i++) {
		if (editors[i].id == id)
			return i;
	}
}

function disableEditor(info) {
	var editor = info.codemirror;
	info.codemirror.getWrapperElement().style.display = "none";
}

function evalGroovy(text, info) {
	var r = info.renderer;
	r.removeClass("renderer");
	info.renderer.html('<img src="images/loading.gif" class="preload"  />');
	$.getJSON("exec", {
		input : text,
		lang : "groovy"
	}, function(data) {
		r.addClass("renderer");
		printRenderer(r, data.result)
	});

}

function evalMarkdown(text, info) {
	var t = markdown.toHTML(text);
	printRenderer(info.renderer, t);
}
function evalJavaScript(text, info) {
	var printResult = true;
	var r = null;
	try {
		r = eval(text);
	} catch (e) {
		r = '<div class="jserror">' + e + '</div>';
	}
	if (r == undefined)
		r = "undefined";
	if (printResult)
		printRenderer(info.renderer, r);
}

function printRenderer(renderer, out) {
	var output = String(out);
	if (output === "")
		renderer.html("<p>&nbsp;</p>");
	else
		renderer.html(output);
}

var settings = {
	markdown : {
		lang : "Markdown",
		evalfkt : evalMarkdown,
		codemirror : {
			mode : 'markdown',
			lineNumbers : false,
			theme : "default",
			viewportMargin : Infinity,
			lineWrapping : true,
			extraKeys : {
				"Enter" : "newlineAndIndentContinueMarkdownList"
			}
		}
	},
	groovy : {
		lang : "Groovy",
		evalfkt : evalGroovy,
		codemirror : {
			mode : 'groovy',
			lineNumbers : true,
			lineWrapping : true,
			theme : "default",
			viewportMargin : Infinity,
		}
	},
	javascript : {
		lang : "JavaScript",
		evalfkt : evalJavaScript,
		codemirror : {
			mode : 'javascript',
			lineNumbers : true,
			lineWrapping : true,
			theme : "default",
			viewportMargin : Infinity,
		}
	},
	b : {
		lang : "B",
		evalfkt : function(text, info) {
			printRenderer(info.renderer, "B said: I build my own " + text
					+ ". With Blackjack and Hookers.")
		},
		codemirror : {
			mode : 'javascript',
			lineNumbers : false,
			lineWrapping : true,
			theme : "default",
			viewportMargin : Infinity,
		}
	}
}
