svgEditor.setConfig({
	gridSnapping : true,
	initStroke: {
        width: 1
	}
});

//var menuItem = {
//	id : "menu-item-observer",
//	label : "Observer",
//	action : function() {
//		console.log("execute")
//	}
//}
//
//svgedit.contextmenu.add(menuItem);

svgEditor.addExtension("BMS", function() {
	return {
	};
});
