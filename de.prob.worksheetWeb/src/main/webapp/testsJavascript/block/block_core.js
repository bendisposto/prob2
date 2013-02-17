(function( $ ) {

module( "block: core" );

test( "defaults", function() {
	expect( 3 );
	var element = $( "#block" ).block();
	ok( element.hasClass( "ui-block" ), "main element is .ui-block" );
	ok( element.attr("id")!="undefined", "main element has an id");
	ok( element.children().length==0, "main element is empty");
});

test( "markup structure (empty block)", function() {
	expect( 3 );
	var options={
			hasMenu : false,
			editor : null,
			isOutput : true,
			outputBlockIds : [],
			menu : null
	};
	var element = $( "#block" ).block(options);
	ok( element.hasClass( "ui-block" ), "main element is .ui-block" );
	ok( element.attr("id")!="undefined","main element has an id");
	ok( element.children().length==0, "main element is empty");
});


test( "markup structure (block with visible menu )", function() {
	expect( 7 );
	var options={
			hasMenu : true,
			editor : null,
			isOutput : true,
			outputBlockIds : [],
			menu : [{
		       	children: [],
				click: "function() {alert('Open')}",
				iconClass: "ui-icon-disk",
				itemClass: "",
				text: "Open"}]
	};
	var element = $( "#block" ).block(options);
	ok( element.hasClass( "ui-block" ), "main element is .ui-block" );
	ok( element.attr("id")!="undefined","main element has an id");
	ok( element.children().length==1, "main element just contains one element");
	ok( element.children().first().hasClass( "ui-block-menu" ), "menu element is .ui-block-menu" );
	ok( element.children().first().children().length==1, "menu element contains one menupoint" );
	ok(	element.children().first().css("display")=="none");
	element.focusin();
	ok(	element.children().first().css("display")=="undefined" || element.children().first().css("display")=="block");
});

test( "markup structure (block with hidden menu )", function() {
	expect( 7 );
	var options={
			hasMenu : false,
			editor : null,
			isOutput : true,
			outputBlockIds : [],
			menu : [{
		       	children: [],
				click: "function() {alert('Open')}",
				iconClass: "ui-icon-disk",
				itemClass: "",
				text: "Open"}]
	};
	var element = $( "#block" ).block(options);
	ok( element.hasClass( "ui-block" ), "main element is .ui-block" );
	ok( element.attr("id")!="undefined","main element has an id");
	ok( element.children().length==1, "main element just contains one element");
	ok( element.children().first().hasClass( "ui-block-menu" ), "menu element is .ui-block-menu" );
	ok( element.children().first().children().length==1, "menu element contains one menupoint" );
	ok(	element.children().first().css("display")=="none");
	element.focusin();
	ok(	element.children().first().css("display")=="none");
});

test( "markup structure (sheet with body and default block)", function() {
	expect(1);
	var options={
			editor:{
				content:"click STRG+ENTER"
			}
	};
	var element=$("<div id='block2'></div>");
	element.bind("blockevaluate",function(){
		ok(true);
		element.remove();
		start();
	});
	element.attr("style","position:absolute;left:100px;top:100px;");
	$("BODY").append(element)
	stop();
	element.block(options);
});
test( "markup structure (sheet with body and default block)", function() {
	expect(1);
	var options={
			editor:{
			      objType : "javascript",
			      id : null,
			      getContent : "function(){return $(\"#\"+this.id+\"\").editor(\"getEditorObject\").getValue();}",
			      destroy : "function(){if(typeof $(\"#\"+this.id+\"\").editor(\"getEditorObject\").toTextArea=='function' ||typeof $(\"#\"+this.id+\"\").editor(\"getEditorObject\").toTextArea=='object')$(\"#\"+this.id+\"\").editor(\"getEditorObject\").toTextArea();}",
			      setContent : "function(content){$(\"#\"+this.id+\"\").editor(\"getEditorObject\").setValue(content);}",
			      cssURLs : [ "../../javascripts/libs/codemirror-2.36/lib/codemirror.css", "../../javascripts/libs/codemirror-2.36/theme/eclipse.css" ],
			      content : "click STRG+ENTER",
			      init : "function(){var cm = CodeMirror.fromTextArea($(\"#\"+this.id+\" .ui-editor-javascript\")[0],{lineNumbers: true,onChange:$.proxy($(\"#\"+this.id+\"\").editor().data().editor._editorChanged,$(\"#\"+this.id+\"\").editor().data().editor)}); return cm;}",
			      html : "<textarea class=\"ui-editor-javascript\"></textarea>",
			      jsURLs : [ "../../javascripts/libs/codemirror-2.36/lib/codemirror.js", "../../javascripts/libs/codemirror-2.36/mode/javascript/javascript.js" ]
			}
	};
	var element=$("<div id='block2'></div>");
	element.bind("blockevaluate",function(){
		ok(true);
		element.remove();
		start();
	});
	element.attr("style","position:absolute;left:100px;top:100px");
	$("BODY").append(element)
	stop();
	element.block(options);
});

}( jQuery ) );
