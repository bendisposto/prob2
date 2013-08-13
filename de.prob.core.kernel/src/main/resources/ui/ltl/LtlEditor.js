LtlEditor = (function() {
	var extern = {};
	var session = Session();
	var delay;

	$(document).ready(function() {
	});
	
	function parseInput() {
		session.sendCmd("parseInput", {
			"input" : extern.codeMirror.getValue(),
			"client" : extern.client
		});
	}
	
	function addMarkers(markers) {
		for (var key in markers) {
			var marker = markers[key]
			
			var line = marker.line - 1;
			var lineInfo = extern.codeMirror.lineInfo(line);
			extern.codeMirror.setGutterMarker(line, "markers", makeMarker(lineInfo, marker.type, marker.msg));
			setTextMarker(marker);
		}
	}
	
	function clearMarkers() {
		extern.codeMirror.clearGutter("markers");	
		removeTextMarkers();
	}
	
	function makeMarker(lineInfo, type, msg) {
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
	
	function setTextMarker(marker) {
		var line = marker.line - 1;
		
		var from = {
			line: line, 
			ch: marker.pos
		};
		
		var to = {
			line: line, 
			ch: marker.pos + marker.length
		};
		
		var options = {
			className: marker.type + '-underline', 
			title: marker.msg
		};
		
		extern.codeMirror.markText(from, to, options);
	}
	
	function removeTextMarkers() {
		var marks = extern.codeMirror.getAllMarks();
		for (var key in marks) {
			var mark = marks[key];
			mark.clear();
		}
	}
	
	// Extern 	
	extern.registerChangeHandlers = function(id) {
		extern.codeMirror.on("change", function(cm, obj) {
			clearTimeout(delay);
			delay = setTimeout(parseInput, 500);
		});
	}
	
	extern.parseOk = function(data) {
		clearMarkers();
		addMarkers(JSON.parse(data.warnings));
	}
	
	extern.parseFailed = function(data) {
		clearMarkers();
		addMarkers(JSON.parse(data.errors));
		addMarkers(JSON.parse(data.warnings));
	}
	
	extern.client = "";
	extern.codeMirror = null;
	extern.init = session.init;
	extern.parseInput = parseInput	

	return extern;
}())