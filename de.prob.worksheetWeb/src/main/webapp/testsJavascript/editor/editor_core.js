(function( $ ) {

module( "editor: core" );

test( "defaults", function() {
	expect( 3 );
	var element = $( "#block" ).block();
	ok( element.hasClass( "ui-block" ), "main element is .ui-block" );
	ok( element.attr("id")!="undefined", "main element has an id");
	ok( element.children().length==0, "main element is empty");
});

test( "markup structure (empty block)", function() {
	expect(3);
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
