(function( $ ) {

module( "worksheet: core" );

test( "defaults", function() {
	expect( 3 );
	var element = $( "#worksheet" ).worksheet();
	ok( element.hasClass( "ui-worksheet" ), "main element is .ui-worksheet" );
	ok( element.attr("id")=="ui-id-1", "main element has id #ui-id-1");
	ok( element.children().length==0, "main element is empty");
});

test( "markup structure (empty sheet)", function() {
	expect( 3 );
	var options={
			hasBody: false,
			hasMenu: false,			
			id: "ui-id-1",
			blocks: [],
			menu: []
	};
	var element = $( "#worksheet" ).worksheet(options);
	ok( element.hasClass( "ui-worksheet" ), "main element is .ui-worksheet" );
	ok( element.attr("id")=="ui-id-1","main element has id #ui-id-1");
	ok( element.children().length==0, "main element is empty");
});


test( "markup structure (sheet with empty body)", function() {
	expect( 5 );
	var options={
			hasBody: true,
			hasMenu: false,			
			id: "ui-id-1",
			blocks: [],
			menu: []
	};
	var element = $( "#worksheet" ).worksheet(options);
	ok( element.hasClass( "ui-worksheet" ), "main element is .ui-worksheet" );
	ok( element.attr("id")=="ui-id-1","main element has id #ui-id-1");
	ok( element.children().length==1, "main element just contains one element");
	ok( element.children().first().hasClass( "ui-worksheet-body" ), "body element is .ui-worksheet-body" );
	ok( element.children().first().children().length==0, "body element is empty" );
	
});

test( "markup structure (sheet with empty menu)", function() {
	expect( 5 );
	var options={
			hasBody: false,
			hasMenu: true,			
			id: "ui-id-1",
			blocks: [],
			menu: []
	};
	var element = $( "#worksheet" ).worksheet(options);
	ok( element.hasClass( "ui-worksheet" ), "main element is .ui-worksheet" );
	ok( element.attr("id")=="ui-id-1","main element has id #ui-id-1");
	ok( element.children().length==1, "main element just contains one element");
	ok( element.children().first().hasClass( "ui-worksheet-menu" ), "menu element is .ui-worksheet-menu" );
	ok( element.children().first().children().length==0, "menu element is empty" );
});
test( "markup structure (sheet with empty menu and body)", function() {
	expect( 7 );
	var options={
			hasBody: true,
			hasMenu: true,			
			id: "ui-id-1",
			blocks: [],
			menu: []
	};
	var element = $( "#worksheet" ).worksheet(options);
	ok( element.hasClass( "ui-worksheet" ), "main element is .ui-worksheet" );
	ok( element.attr("id")=="ui-id-1","main element has id #ui-id-1");
	ok( element.children().length==2, "main element just contains two elements");
	ok( $(element.children()[0]).hasClass( "ui-worksheet-menu" ), "menu element is .ui-worksheet-menu" );
	ok( $(element.children()[0]).children().length==0, "menu element is empty" );
	ok( $(element.children()[1]).hasClass( "ui-worksheet-body" ), "body element is .ui-worksheet-body" );
	ok( $(element.children()[1]).children().length==0, "body element is empty" );

});

test( "markup structure (sheet with body and default block)", function() {
	expect( 7 );
	var options={
			hasBody: true,
			hasMenu: true,			
			id: "ui-id-1",
			blocks: [],
			menu: []
	};
	var element = $( "#worksheet" ).worksheet(options);
	ok( element.hasClass( "ui-worksheet" ), "main element is .ui-worksheet" );
	ok( element.attr("id")=="ui-id-1","main element has id #ui-id-1");
	ok( element.children().length==2, "main element just contains two elements");
	ok( $(element.children()[0]).hasClass( "ui-worksheet-menu" ), "menu element is .ui-worksheet-menu" );
	ok( $(element.children()[0]).children().length==0, "menu element is empty" );
	ok( $(element.children()[1]).hasClass( "ui-worksheet-body" ), "body element is .ui-worksheet-body" );
	ok( $(element.children()[1]).children().length==0, "body element is empty" );

});
test("worksheet dirty handling (programmatic)",function(){
	expect(2);
	var options={
			hasBody:true,
			blocks:[{
				editor:{}
			}]
	};
	var element = $( "#worksheet" ).worksheet(options);
	ok(!element.worksheet("isDirty"));
	element.find(".ui-editor").editor("setContent","test");
	ok(element.worksheet("isDirty"));
	element.worksheet("destroy");
});

test("worksheet dirty handling (editorChanged)",function(){
	expect(2);
	var options={
			hasBody:true,
			blocks:[{
				editor:{}
			}]
	};
	var element = $( "#worksheet" ).worksheet(options);
	ok(!element.worksheet("isDirty"));
	//first event is not fired because of initialization
	element.find(".ui-editor").editor("setContent","test");
	element.worksheet("setDirty",false);	
	$(".ui-editor").first().data("editor")._editorChanged();
	//$(".ui-editor").first().data("editor")._editorChanged();
	ok(element.worksheet("isDirty"));
	element.worksheet("destroy");
	//jTODO add test for initializing with empty content
});


test( "eval Event", function() {
	expect(1);
	var options={
			hasBody:true,
			blocks:[{
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
			}]
	};
	var element=$("<div id='worksheet2'></div>");
	element.bind("worksheetevalstart",function(){
		ok(true);
		element.remove();
		start();
	});
	element.attr("style","position:absolute;left:100px;top:100px");
	$("BODY").append(element)
	stop();
	element.worksheet(options);
});

}( jQuery ) );
