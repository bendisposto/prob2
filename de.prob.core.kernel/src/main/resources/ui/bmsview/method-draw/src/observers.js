var svgAllElementAttributes = [
	{value: "style", text: 'style'},
	{value: "class", text: 'class'}
];

var svgAllShapesAttributes = [
	{value: 'clip-path', text: 'clip-path'},
	{value: 'fill', text: 'fill'},
	{value: 'fill-opacity', text: 'fill-opacity'},
	{value: 'fill-rule', text: 'fill-rule'},
	{value: 'marker-end', text: 'marker-end'},
	{value: 'marker-mid', text: 'marker-mid'},
	{value: 'marker-start', text: 'marker-start'},
	{value: 'pattern', text: 'pattern'},
	{value: 'stroke', text: 'stroke'},
	{value: 'stroke-dasharray', text: 'stroke-dasharray'},
	{value: 'stroke-dashoffset', text: 'stroke-dashoffset'},
	{value: 'stroke-linecap', text: 'stroke-linecap'},
	{value: 'stroke-linejoin', text: 'stroke-linejoin'},
	{value: 'stroke-miterlimit', text: 'stroke-miterlimit'},
	{value: 'stroke-opacity', text: 'stroke-opacity'},
	{value: 'stroke-width', text: 'stroke-width'},
	{value: 'transform', text: 'transform'}
];

var svgCoordinatesAttributes = [
	{value: 'x', text: 'x'},
	{value: 'y', text: 'y'}
];

var svgRoundedCoordinatesAttributes = [
	{value: 'rx', text: 'rx'},
	{value: 'ry', text: 'ry'}
];

var svgRadiusCoordinatesAttributes = [
	{value: 'cx', text: 'cx'},
	{value: 'cy', text: 'cy'}
];

var svgDimensionAttributes = [
   	{value: 'width', text: 'width'},
  	{value: 'height', text: 'height'}
];

var svgTextAttributes = [
	{value: 'font-size', text: 'font-size'},
	{value: 'font-family', text: 'font-family'},
	{value: 'font-weight', text: 'font-weight'},
	{value: 'font-family', text: 'font-family'}
];

var svgLineCoordinatesAttributes = [
	{value: 'x1', text: 'x1'},
	{value: 'x2', text: 'x2'},
	{value: 'y1', text: 'y1'},
	{value: 'y2', text: 'y2'}
];

var svgAttributes = {
	"rect" : mergeAttr([svgAllElementAttributes, svgAllShapesAttributes, svgCoordinatesAttributes, svgRoundedCoordinatesAttributes, svgDimensionAttributes]),
	"ellipse" : mergeAttr([svgAllElementAttributes, svgAllShapesAttributes, svgRadiusCoordinatesAttributes, svgRoundedCoordinatesAttributes, svgDimensionAttributes]),
	"text" : mergeAttr([svgAllElementAttributes, svgCoordinatesAttributes, svgTextAttributes]),
	"line" : mergeAttr([svgAllElementAttributes, svgLineCoordinatesAttributes]),
	"path" : mergeAttr([svgAllElementAttributes, svgCoordinatesAttributes])
}

function mergeAttr(list) {
	var l = []
	$.each(list, function(i, v) {
		l = $.merge(l, v);
	});
	return l;
}

function addCspEventObserver(group) {
	var bms = window.top.bms
	bms.addObserver("CspEventObserver", group)
}

function addEvalObserver(group) {
	var bms = window.top.bms
	bms.addObserver("EvalObserver", group)
}

function changeEvalObserverData(obj,key,value) {
	var index = obj.parent().parent().parent().parent().index()
	var group = obj.attr("group")
	var bms = window.top.bms
	var data = {
		"type" : "EvalObserver",
		"group" : group,
		"index" : index,
		"key" : key,
		"value" : value
	}
	bms.changeObserverData(data)
}

function changeCspEventObserverData(obj,key,value) {
	var index = obj.parent().parent().parent().parent().index()
	var group = obj.attr("group")
	var bms = window.top.bms
	var data = {
		"type" : "CspEventObserver",
		"group" : group,
		"index" : index-1,
		"key" : key,
		"value" : value
	}
	bms.changeObserverData(data)
}

function initCspEventObserver() {
	
	var container = $("#observers").find("#accordion")
	
	container.find(".csp_events").editable({
		type: 'textarea',
		title: 'Events',
		success: function(response, newValue) {		
			changeCspEventObserverData($(this),"events",newValue)
	    }
	});
	
	container.find(".csp_selector").editable({
		type: 'textarea',
		title: 'Selector',
		success: function(response, newValue) {
			$(this).parent().parent().parent().parent().attr("selector",newValue)
			changeCspEventObserverData($(this),"selector",newValue)
	    }
	});
	
	container.find(".csp_attr").editable({
		type: 'select',
	    title: 'Attribute',
	    source: function() {
	    	var selector = $(this).parent().parent().parent().parent().attr("selector")
			try {
	    		var elementName = $(selector).prop('tagName')
	    		return svgAttributes[elementName];
			} catch(error) {
				return {}
			}
	    },
	    success: function(response, newValue) {
	    	changeCspEventObserverData($(this),"attr",newValue)
	    }
	});		  

	container.find(".csp_value").editable({
		type: 'textarea',
		title: 'Value',
		success: function(response, newValue) {
			changeCspEventObserverData($(this),"value",newValue)
	    }
	});
	
}

function initEvalObserver() {
	
	var container = $("#observers").find("#accordion")
	
	container.find(".oitem_predicate").editable({
		type: 'textarea',
		title: 'Predicate',
		success: function(response, newValue) {		
			changeEvalObserverData($(this),"predicate",newValue)
	    }
	});
	
	container.find(".oitem_attr").editable({
		type: 'select',
	    title: 'Attribute',
	    source: function() {
	    	var selector = $(this).attr("group")
	    	var elementName = $(selector).prop('tagName')
	    	return svgAttributes[elementName];
	    },
	    success: function(response, newValue) {
	    	changeEvalObserverData($(this),"attr",newValue)
	    }
	});		  

	container.find(".oitem_value").editable({
		type: 'textarea',
		title: 'Value',
		success: function(response, newValue) {
			changeEvalObserverData($(this),"value",newValue)
	    }
	});
	
}