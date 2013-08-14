(function() { 
	var WORD = /[\w$]+/;
	
	CodeMirror.registerHelper("hint", "ltl", function(cm, options) {
		var cursor = cm.getCursor();
		var lineInfo = cm.getLine(cursor.line);
		
		// Find characters before cursor that belong to the current word
		var start = cursor.ch; 
		var end = start;
		while (end < lineInfo.length && WORD.test(lineInfo.charAt(end))) ++end;
		while (start && WORD.test(lineInfo.charAt(start - 1))) --start;				
		
		return {list: options.words, from: CodeMirror.Pos(cursor.line, start), to: CodeMirror.Pos(cursor.line, end)};
  });
})();
