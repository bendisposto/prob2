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
		"Trace" : {
			codemirror : {
				mode : 'b',
				lineNumbers : false,
				lineWrapping : true,
				theme : "default",
				viewportMargin : Infinity,
			},
			construct : function(id,editor_data){
				if(editor_data.args!=null){
					
					var templateArgs={
						"withDropdown":(editor_data.args.traces!=null && editor_data.args.traces.length>1),
						"items":editor_data.args.traces,
						"type":"trace"
					};
					var menu=$(Worksheet.getSession().render("/ui/worksheet/box-menu.html",templateArgs));
					$("#box"+id+" .heading-bar").append(menu);
					menu.find(".selectee").click(function(e){
							$(e.target).closest(".box-menu").find(".selectee").removeClass("selected");
							$(e.target).addClass("selected");
							$(e.target).closest(".box-menu").find(".trace-selected").html(e.target.id);
						});
					if(editor_data.args.traces!=null && editor_data.args.traces.length>0){ 
						menu.find(".trace-selected").html(editor_data.args.traces[0]);
					}else{
						menu.find(".trace-selected").html("No Traces in Worksheet");
					}
				}				
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