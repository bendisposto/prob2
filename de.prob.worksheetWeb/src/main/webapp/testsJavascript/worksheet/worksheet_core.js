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


}( jQuery ) );
