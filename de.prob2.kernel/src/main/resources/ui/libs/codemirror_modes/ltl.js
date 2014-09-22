CodeMirror.defineMode("ltl", function() {
	
	var keywordGroups = { 
		"keyword": words("def count up down to end without seq num var"),
		"scope": words("before after between after_until"),
		"atom": words("true false sink deadlock current"),  
		"ltl": words("G F X N H O Y U W R S T"),  
		"boolean": words("not and or")
	};
	
	function words(str) {
		var obj = {}, words = str.split(" ");
		for (var i = 0; i < words.length; ++i) obj[words[i]] = true;
		return obj;
	}

	function keyword(stream, state) {
		if (!stream.eatWhile(/\w/)) {
			// Skip
			stream.next();
			return null;
		}
		var word = stream.current();
		for (var token in keywordGroups) {
			var words = keywordGroups[token];
			if (words.propertyIsEnumerable(word)) {
				return token;
			}
		}
		return null;
	}

	function multiLineComment(stream, state) {
		while (stream.skipTo('*')) {
			stream.next();
			if (stream.eat('/')) {
				// End of multiline comment found
				state.inMultiLineComment = false;
				return "comment";
			}
		}
		// Rest of line must also be part of the comment
		stream.skipToEnd();
		return "comment";
	}


	return {
		startState: function() {
			return {
				inMultiLineComment: false
			};
		},
		token: function(stream, state) {
			if (state.inMultiLineComment) {
				return multiLineComment(stream, state);
			} else {
				if (stream.eat('/')) {
					// Comments
					if (stream.eat('/')) {
						// Single line comment
						stream.skipToEnd();
						return "comment";
					} else if (stream.eat('*')) {
						// Multi line comment
						state.inMultiLineComment = true;
						return "comment";
					}
				} else if (stream.eatWhile(/\s/)) {
					// Whitespaces
					return null;
				} else if (stream.eatWhile(/\d/)) {
					// Number
					if (stream.eat(/\w/)) {
						// [a-zA-Z] are not allowed directly after a number
						return null;
					}
					return "number";
				}  else if (/[!&|=]/.test(stream.peek())) {
					// Boolean operator symbols
					if (stream.next() == '=' && !stream.eat('>')) {
						return null;
					}
					return "boolean";
				} else if (stream.eatWhile(/[GFXHOY]+/)) {
					// Combined unary ltl operator
					return "ltl";
				} else {
					// Keywords
					return keyword(stream, state);
				}
			}
		}
	};
});