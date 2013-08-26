Languages = (function() {
	var extern = {
		"Groovy" : {
			codemirror : {
				mode : 'groovy',
				lineNumbers : true,
				lineWrapping : true,
				theme : "default",
				viewportMargin : Infinity,
			},
		},
		"B" : {
			codemirror : {
				mode : 'b',
				lineNumbers : false,
				lineWrapping : true,
				theme : "default",
				viewportMargin : Infinity,
			},
		},
		"Markdown" : {
			codemirror : {
				mode : 'markdown',
				lineNumbers : false,
				lineWrapping : true,
				theme : "default",
				viewportMargin : Infinity,
			}
		},
		"LoadModel" : {
			getter : function() {
				return $("#openfile" + this.id)[0].value
			},
			construct : function(id, editor_data) {
				var dom_dir = "#openfile" + id;
				$(dom_dir).keyup(Worksheet.set_ok_button_state(dom_dir, id))
				Worksheet.set_ok_button_state(dom_dir, id)();
			},
			focusFkt : function(id) {
				return function() {
					var dom_dir = "#openfile" + id;
					$(dom_dir).focus()
				}
			}

		}
	}
	return extern;
}())