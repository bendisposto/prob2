CodeMirror
		.defineMode(
				"b",
				function() {

					var type = /[0-9]*\s*\.\.\s*[0-9]*|(-->)|(-->>?)|(>->>?)|(\+->>?)|(>\+>>?)|<->/
					var type_w = /(POW(1?)|(scope_\w*)|(SET_PREF_\w*)|(FORCE_SYMMETRY_\w*)|FIN(1?)|perm|i?seq1?|BOOL|struct|STRING|INT(EGER)?|NAT(URAL)?1?)/

					var logical = /&|<?=>|!|#/
					var logical_w = /(not|or|TRUE|FALSE|bool|GOAL|ANIMATION_IMG\w*|ANIMATION_STR\w*|ANIMATION_FUNCTION_DEFAULT|ANIMATION_FUNCTION|HEURISTIC_FUNCTION|ASSERT_(LTL|CTL)\w*)/

					var assignment = /<--|:=|==?(?!>)|\|\||::/

					var operator_x = /\|->|>=|<=(?!>)|%|\*\*|~|\/?<<?:|\/(:|=)|<\+|><|\^|<-(?!-|>)|<<?\||\|>>?|{}|{|}|\[|\]|\|\[\]|<>|(\/\|?\\)|(\\\|?\/)|\'/
					var operator_w = /(SIGMA|PI|MININT|MAXINT|pred|succ|id|INTER|UNION|card|dom|ran|max|min|union|inter|size|mod|fnc|rel|rev|conc|front|tail|first|last|rec|closure1|closure|iterate|prj(1|2))/

					var keyword_w = /MACHINE(\s*\w\w*)?|END\s*\Z|(OPERATIONS|EVENTS|ASSERTIONS|INITIALI(S|Z)ATION|SEES|PROMOTES|USES|INCLUDES|IMPORTS|REFINES|EXTENDS|REFINEMENT|CSP_CONTROLLER|SYSTEM|MODEL|IMPLEMENTATION|INVARIANT|CONCRETE_VARIABLES|ABSTRACT_VARIABLES|VARIABLES|PROPERTIES|CONSTANTS|ABSTRACT_CONSTANTS|CONCRETE_CONSTANTS|CONSTRAINTS|SETS|DEFINITIONS)/

					var control_keyword_w = /(skip|LET|BE|VAR|IN|ANY|WHILE|DO|VARIANT|ELSIF|IF|THEN|ELSE|EITHER|CASE|SELECT|ASSERT|WHEN|PRE|BEGIN|END|CHOICE|WHERE|OR|OF)/

					var unsupported_w = /left|right|infix|arity|subtree|son|father|rank|mirror|sizet|postfix|prefix|sons|top|const|btree|tree/

					return {
						startState : function(basecolumn) {
							return {
								"comment" : false
							};
						},

						token : function(stream, state) {
							stream.eatSpace();

							if (stream.match(/\/\*/, true)) {
								state.comment = true
								return 'b-comment'
							}

							if (stream.match(/\*\//, true)) {
								state.comment = false
								return 'b-comment'
							}

							if (state.comment == false) {
								if (stream.match(type, true)) {
									return 'b-type'
								}
								if (stream.match(type_w, true)
										&& (stream.eol() || stream.peek()
												.match(/\s/))) {
									return 'b-type'
								}

								if (stream.match(logical, true)) {
									return 'b-logical'
								}
								if (stream.match(logical_w, true)
										&& (stream.eol() || stream.peek()
												.match(/\s/))) {
									return 'b-logical'
								}

								if (stream.match(assignment, true)) {
									return 'b-assignment'
								}

								if (stream.match(operator_x, true)) {
									return 'b-operator'
								}
								if (stream.match(operator_w, true)
										&& (stream.eol() || stream.peek()
												.match(/\s|\(/))) {
									return 'b-operator'
								}

								if (stream.match(keyword_w, true)
										&& (stream.eol() || stream.peek()
												.match(/\s/))) {
									return 'b-keyword'
								}

								if (stream.match(control_keyword_w, true)
										&& (stream.eol() || stream.peek()
												.match(/\s/))) {
									return 'b-controlkeyword'
								}

								if (stream.match(unsupported_w, true)
										&& (stream.eol() || stream.peek()
												.match(/\s/))) {
									return 'b-unsupported'
								}
								stream.next();
								return 'b-nothing';
							} else {
								stream.next();
								return 'b-comment';
							}
						}
					};
				});

CodeMirror.defineMIME("text/b", "b");