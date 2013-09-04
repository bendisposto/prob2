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
			has_source : true
		},
		"B" : {
			codemirror : {
				mode : 'b',
				lineNumbers : false,
				lineWrapping : true,
				theme : "default",
				viewportMargin : Infinity,
			},
			has_source : true
		},
		"Markdown" : {
			codemirror : {
				mode : 'markdown',
				lineNumbers : false,
				lineWrapping : true,
				theme : "default",
				viewportMargin : Infinity,
			},
			no_vars : true,
			has_source : true
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
			},
			has_source : false
		}
	}
	return extern;
}())