LtlEditor = (function() {
	var extern = {};
	var session = Session();
	var delay;
	var highlightDelay;

	$(document).ready(function() {
		parseInput();
	});
	
	function parseInput() {
		session.sendCmd("parseInput", {
			"input" : extern.codeMirror.getValue(),
			"client" : extern.client
		});
	}
	
	function getExpressionAtCursorPosition() {
		var cursor = extern.codeMirror.getCursor();
		var pos = (cursor.line + 1) + "-" + cursor.ch;
		session.sendCmd("getExpressionAtPosition", {
			"pos" : pos,
			"client" : extern.client
		});
	}
	
	function addMarkers(markers) {
		for (var i = 0; i < markers.length; i++) {
			var marker = markers[i];
			var mark = marker.mark;
			
			var line = mark.line - 1;
			var lineInfo = extern.codeMirror.lineInfo(line);
			// Gutter marker
			extern.codeMirror.setGutterMarker(line, "markers", makeGutterMarker(lineInfo, marker.type, marker.msg));
			// Text marker
			var options = {
				className: marker.type + '-underline', 
				title: marker.msg
			};
			setTextMarker(mark, options);
		}
	}
	
	function makeGutterMarker(lineInfo, type, msg) {
		var marker;
		if (typeof lineInfo.gutterMarkers === 'undefined' || lineInfo.gutterMarkers === null) {
			marker = document.createElement("div");
			marker.title = msg;
			marker.className = type;
		} else {
			marker = lineInfo.gutterMarkers['markers'];
			marker.title += '\n' + msg;
		}
		return marker;
	}
	
	function clearMarkers() {
		extern.codeMirror.clearGutter("markers");	
		removeTextMarkers(["error-underline", "warning-underline"]);
	}
	
	function removeTextMarkers(classes) {
		var marks = extern.codeMirror.getAllMarks();
		for (var i = 0; i < marks.length; i++) {
			var mark = marks[i];
			if ($.inArray(mark.className, classes) != -1) {
				mark.clear();
			}
		}
	}
	
	function setTextMarker(mark, options) {
		var line = mark.line - 1;
		
		var from = {
			line: line, 
			ch: mark.pos
		};
		var to = {
			line: line, 
			ch: mark.pos + mark.length
		};	
		extern.codeMirror.markText(from, to, options);
	}
	
	function highlightOperand(expression) {		
		setTextMarker(expression.operator, { className: "operator" });	
		var operands = expression.operands;
		for (var i = 0; i < operands.length; i++) {
			setTextMarker(operands[i], { className: "operand" });
		}
	}
	
	function refreshOperandHighlighting(ms) {
		clearTimeout(highlightDelay);
		highlightDelay = setTimeout(getExpressionAtCursorPosition, ms);	
	}
	
	// Extern 	
	extern.registerChangeHandlers = function() {
		extern.codeMirror.on("change", function(cm, obj) {
			clearTimeout(delay);
			delay = setTimeout(parseInput, 500);
		});
	}
	
	extern.enableOperandHighlighting = function() {
		extern.codeMirror.on("cursorActivity", function(cm) {
			refreshOperandHighlighting(500);
		});
	}
	
	extern.parseOk = function(data) {
		clearMarkers();
		addMarkers(JSON.parse(data.warnings));
		refreshOperandHighlighting(250);
	}
	
	extern.parseFailed = function(data) {
		clearMarkers();
		addMarkers(JSON.parse(data.errors));
		addMarkers(JSON.parse(data.warnings));
		refreshOperandHighlighting(250);
	}
	
	extern.expressionFound = function(data) {
		removeTextMarkers(["operator", "operand"]);
		highlightOperand(JSON.parse(data.expression));
	}
	
	extern.noExpressionFound = function(data) {
		removeTextMarkers(["operator", "operand"]);
	}
	
	extern.client = "";
	extern.codeMirror = null;
	extern.init = session.init;
	extern.parseInput = parseInput	

	return extern;
}())