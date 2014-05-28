var svgAttributeList = {
	"x" : {value: 'x', text: 'x'},
	"y" : {value: 'y', text: 'y'},
	"rx" : {value: 'rx', text: 'rx'},
	"ry" : {value: 'ry', text: 'ry'},
	"cx" : {value: 'cx', text: 'cx'},
	"cy" : {value: 'cy', text: 'cy'},
	"x1" : {value: 'x1', text: 'x1'},
	"x2" : {value: 'x2', text: 'x2'},
	"y1" : {value: 'y1', text: 'y1'},
	"y2" : {value: 'y2', text: 'y2'},	
   	"width" : {value: 'width', text: 'width'},
  	"height" : {value: 'height', text: 'height'},
	"style" : {value: "style", text: 'style'},
	"class" : {value: "class", text: 'class'},
	"image" : {value: 'image', text: 'image', change: function(selector, value) {
		$(selector).attr('xlink:href', value)
	}},
	"font-size" : {value: 'font-size', text: 'font-size'},
	"font-family" : {value: 'font-family', text: 'font-family'},
	"font-weight" : {value: 'font-weight', text: 'font-weight'},
	"font-family" : {value: 'font-family', text: 'font-family'},	
	"text" :  {value: 'text', text: 'text', change: function(selector, value) {
		$(selector).html(value)
	}},
	"clip-path" : {value: 'clip-path', text: 'clip-path'},
	"fill" : {value: 'fill', text: 'fill'},
	"fill-opacity" : {value: 'fill-opacity', text: 'fill-opacity'},
	"fill-rule" : {value: 'fill-rule', text: 'fill-rule'},
	"marker-end" : {value: 'marker-end', text: 'marker-end'},
	"marker-mid" : {value: 'marker-mid', text: 'marker-mid'},
	"marker-start" : {value: 'marker-start', text: 'marker-start'},
	"pattern" : {value: 'pattern', text: 'pattern'},
	"stroke" : {value: 'stroke', text: 'stroke'},
	"stroke-dasharray" : {value: 'stroke-dasharray', text: 'stroke-dasharray'},
	"stroke-dashoffset" : {value: 'stroke-dashoffset', text: 'stroke-dashoffset'},
	"stroke-linecap" : {value: 'stroke-linecap', text: 'stroke-linecap'},
	"stroke-linejoin" : {value: 'stroke-linejoin', text: 'stroke-linejoin'},
	"stroke-miterlimit" : {value: 'stroke-miterlimit', text: 'stroke-miterlimit'},
	"stroke-opacity" : {value: 'stroke-opacity', text: 'stroke-opacity'},
	"stroke-width" : {value: 'stroke-width', text: 'stroke-width'},
	"transform" : {value: 'transform', text: 'transform'}
};

var svgAllElementAttributes = [
    svgAttributeList["style"], 
    svgAttributeList["class"]
];

var svgAllShapesAttributes = [
	svgAttributeList['clip-path'],
	svgAttributeList['fill'],
	svgAttributeList['fill-opacity'],
	svgAttributeList['fill-rule'],
	svgAttributeList['marker-end'],
	svgAttributeList['marker-mid'],
	svgAttributeList['marker-start'],
	svgAttributeList['pattern'],
	svgAttributeList['stroke'],
	svgAttributeList['stroke-dasharray'],
	svgAttributeList['stroke-dashoffset'],
	svgAttributeList['stroke-linecap'],
	svgAttributeList['stroke-linejoin'],
	svgAttributeList['stroke-miterlimit'],
	svgAttributeList['stroke-opacity'],
	svgAttributeList['stroke-width'],
	svgAttributeList['transform']
];

var svgCoordinatesAttributes = [
	svgAttributeList['x'],
	svgAttributeList['y']
];

var svgRoundedCoordinatesAttributes = [
	svgAttributeList['rx'],
	svgAttributeList['ry']
];

var svgRadiusCoordinatesAttributes = [
	svgAttributeList['cx'],
	svgAttributeList['cy']
];

var svgDimensionAttributes = [
   	svgAttributeList['width'],
  	svgAttributeList['height']
];

var svgTextAttributes = [
    svgAttributeList['text'],
    svgAttributeList['font-size'],
	svgAttributeList['font-family'],
	svgAttributeList['font-weight'],
	svgAttributeList['font-family']
];

var svgLineCoordinatesAttributes = [
	svgAttributeList['x1'],
	svgAttributeList['x2'],
	svgAttributeList['y1'],
	svgAttributeList['y2']
];

var svgImageAttributes = [
	svgAttributeList['image']
]

var svgAttributes = {
	"rect" : mergeAttr([svgAllElementAttributes, svgAllShapesAttributes, svgCoordinatesAttributes, svgRoundedCoordinatesAttributes, svgDimensionAttributes]),
	"ellipse" : mergeAttr([svgAllElementAttributes, svgAllShapesAttributes, svgRadiusCoordinatesAttributes, svgRoundedCoordinatesAttributes, svgDimensionAttributes]),
	"text" : mergeAttr([svgAllElementAttributes, svgCoordinatesAttributes, svgAllShapesAttributes, svgTextAttributes]),
	"line" : mergeAttr([svgAllElementAttributes, svgLineCoordinatesAttributes]),
	"path" : mergeAttr([svgAllElementAttributes, svgCoordinatesAttributes]),
	"image" : mergeAttr([svgAllElementAttributes, svgCoordinatesAttributes, svgDimensionAttributes, svgImageAttributes]),
	"g" : mergeAttr([svgAllElementAttributes, svgAllShapesAttributes])
}

function mergeAttr(list) {
	var l = []
	$.each(list, function(i, v) {
		l = $.merge(l, v);
	});
	return l;
}
