(function( $ ) {

module( "lazyLoader: core" );

test( "defaults", function() {
	expect( 5 );
	var element = $( "#lazyLoader" ).lazyLoader();
	ok( typeof element.data("lazyLoader")!="undefined", "data is stored in element" );
	ok( element.data("lazyLoader").jsUrls.length==0, "no js in store" );
	ok( element.data("lazyLoader").jsUrlQueue.length==0, "no js in queue" );
	ok( element.data("lazyLoader").cssUrls.length==0, "no css in store" );
	ok( element.data("lazyLoader").cssUrlQueue.length==0, "no css in queue" );
});

test( "initialize with initial loaded libs", function() {
	expect( 5 );
	var options={
			jsUrls:["test.js"],
			cssUrls:["test.css"]
	};
	var element = $( "#lazyLoader" ).lazyLoader(options);
	ok( typeof element.data("lazyLoader")!="undefined", "data is stored in element" );
	ok( element.data("lazyLoader").jsUrls.length==1, "one js in store" );
	ok( element.data("lazyLoader").jsUrlQueue.length==0, "no js in queue" );
	ok( element.data("lazyLoader").cssUrls.length==1, "one css in store" );
	ok( element.data("lazyLoader").cssUrlQueue.length==0, "no css in queue" );
});

test( "initialize with initial loading libs", function() {
	expect( 9 );
	var options={
			jsUrlQueue:["./test.js"],
			cssUrlQueue:["./test.css"]
	};
	var element = $( "#lazyLoader" ).lazyLoader(options);
	ok( typeof element.data("lazyLoader")!="undefined", "data is stored in element" );
	ok( element.data("lazyLoader").jsUrls.length==0, "no js in store" );
	ok( element.data("lazyLoader").jsUrlQueue.length==1, "one js in queue" );
	ok( element.data("lazyLoader").cssUrls.length==0, "no css in store" );
	ok( element.data("lazyLoader").cssUrlQueue.length==1, "one css in queue" );
	element.one("scriptsLoaded",$.proxy(function(){
		ok( element.data("lazyLoader").jsUrls.length==1, "one js in store" );
		ok( element.data("lazyLoader").jsUrlQueue.length==0, "no js in queue" );
		ok( element.data("lazyLoader").cssUrls.length==1, "one css in store" );
		ok( element.data("lazyLoader").cssUrlQueue.length==0, "no css in queue" );	
	},this));
	element.lazyLoader("load");
});


}( jQuery ) );
