(function() { 
	var WORD = /[\w$]+/;
	
	function renderHint(element, self, data) {
		var text = document.createElement("span");
		text.className = "text";
		text.appendChild(document.createTextNode(data.text));
		text.hintId = element.hintId;
		
		var type = document.createElement("span");
		type.className = "type";
		type.appendChild(document.createTextNode(data.type));
		type.hintId = element.hintId;
		
		element.className = "CodeMirror-hint hint-" + data.type;
		if (data.active) {
			element.className += " CodeMirror-hint-active";
		}
		element.appendChild(text);
		element.appendChild(type);
	}
	
	CodeMirror.registerHelper("hint", "ltl", function(cm, options) {
		var cursor = cm.getCursor();
		var lineInfo = cm.getLine(cursor.line);
		
		// Find characters before cursor that belong to the current word
		var start = cursor.ch; 
		var end = start;
		while (end < lineInfo.length && WORD.test(lineInfo.charAt(end))) ++end;
		while (start && WORD.test(lineInfo.charAt(start - 1))) --start;		

		var hints = options.hints;
		var list = [];
		for (var i = 0; i < hints.length; i++) {
			var hint = hints[i];
			hint.render = renderHint;
			hint.active = (i == 0);
			list.push(hint);
		}
		
		return {list: list, from: CodeMirror.Pos(cursor.line, start), to: CodeMirror.Pos(cursor.line, end)};
  });
})();
