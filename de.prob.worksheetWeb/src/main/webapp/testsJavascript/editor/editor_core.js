(function( $ ) {

module( "editor: core" );


test("initialize Event",function(){
	expect(2);
	var phase=0;
	var done=false;
	var element = $( "#editor" );
	element.bind("editoroptionschanged",function(){
		ok(phase==0 && !done) ;
		phase=1;
	});
	element.bind("editorinitialized",function(){
		ok(phase==1 && !done);
		done=true;
		start();
	});
	element.bind("editorcontentchanged",function(){
		ok(false);
	});
	stop();
	element.editor();
	
});

test("Content Changed",function(){
	expect(4);
	
	var phase=0;
	var done=false;
	var element = $( "#editor" );
	element.editor();
	
	var phase=0;
	var done=false;
	element.bind("editorcontentchanged",function(){
		ok(phase==0 &&  !done);
		phase=1;
	});
	element.bind("editoroptionschanged",function(){
		ok(phase==1 && !done) ;
		done=true;
		start();
	});
	element.bind("editorinitialized",function(){
		ok(false);
		start();
	});
	stop();
	$("#editor").editor("setContent","test");
	element.unbind();
	
	
	
	
	var phase=0;
	var done=false;
	element.bind("editorcontentchanged",function(){
		ok(phase==0 &&  !done);
		phase=1;
	});
	element.bind("editoroptionschanged",function(){
		ok(phase==1 && !done) ;
		done=true;
		start();
	});
	element.bind("editorinitialized",function(){
		ok(false);
		start();
	});
	stop();
	
	
	var event = $.Event( "keydown" );
	event.keyCode = 9;
	$(document).trigger( event );
	
	element.editor("setContent","test");
	element.unbind();
	
	
	
});

test( "defaults", function() {
	expect( 6 );
	var element = $( "#editor" ).editor();
	ok( element.hasClass( "ui-editor" ), "main element is .ui-editor" );
	ok( element.attr("id")!="undefined", "main element has an id");
	ok( element.children().length==1, "main element has one child");
	ok( $(element.children().first()).hasClass("ui-editor-content"), "editor-content is .ui-editor-content");
	ok( $(element.children().first()).children().length==1, "editor-content element has one child");
	ok( $($(element.children().first()).children().first()).hasClass("editor-object"), "editor-object is .editor-object");
});


test( "markup structure without inner Editor", function() {
	expect(5);
	var options={
			content: "",
			destroy: "function(){}",
			getContent: "function(){}",
			init: "function(){}",
			html: ""
	};
	var element =$( "#editor" ).editor(options);
	ok( element.hasClass( "ui-editor" ), "main element is .ui-editor" );
	ok( element.attr("id")!="undefined", "main element has an id");
	ok( element.children().length==1, "main element has one child");
	ok( $(element.children().first()).hasClass("ui-editor-content"), "editor-content is .ui-editor-content");
	ok( $(element.children().first()).children().length==0, "editor-content element has no child");
});

test( "getContent",function(){
	expect(1);
	// textarea Editor
	var element=$("#editor").editor({content:"test2"});	
	equal(element.data("editor").getContent(),"test2","content for default editor seems to be correct");
		
});

test( "setContent",function(){
	expect(1);
	// textarea Editor
	var element=$("#editor").editor();	
	
	element.editor("setContent","test");
	
	equal(element.editor("getContent"),"test","content for default editor seems to be correct");
		
});

test( "getEditorObject",function(){
	expect(2);
	// textarea Editor
	var element=$("#editor").editor();	
	ok(element.editor("getEditorObject")[0]==$(".editor-object").first()[0]);
	equal(element.editor("getEditorObject")[0],$(".editor-object").first()[0],"content for default editor seems to be correct");
});

test( "destroy",function(){
	expect(1);
	var clean=$($("#editor").first()).clone();
	equal(clean[0],$("#editor").editor().editor("destroy")[0]);
});


}( jQuery ) );
