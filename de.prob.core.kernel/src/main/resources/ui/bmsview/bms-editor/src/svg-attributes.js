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

var svgImageAttributes = [
	{value: 'image', text: 'image'},
]

var svgAttributes = {
	"rect" : mergeAttr([svgAllElementAttributes, svgAllShapesAttributes, svgCoordinatesAttributes, svgRoundedCoordinatesAttributes, svgDimensionAttributes]),
	"ellipse" : mergeAttr([svgAllElementAttributes, svgAllShapesAttributes, svgRadiusCoordinatesAttributes, svgRoundedCoordinatesAttributes, svgDimensionAttributes]),
	"text" : mergeAttr([svgAllElementAttributes, svgCoordinatesAttributes, svgAllShapesAttributes, svgTextAttributes]),
	"line" : mergeAttr([svgAllElementAttributes, svgLineCoordinatesAttributes]),
	"path" : mergeAttr([svgAllElementAttributes, svgCoordinatesAttributes]),
	"image" : mergeAttr([svgAllElementAttributes, svgCoordinatesAttributes, svgDimensionAttributes, svgImageAttributes])
}

function mergeAttr(list) {
	var l = []
	$.each(list, function(i, v) {
		l = $.merge(l, v);
	});
	return l;
}
