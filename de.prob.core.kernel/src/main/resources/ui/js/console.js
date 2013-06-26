var controller;

function onValidate(line) {
	return true;
};

function onHandle() {
}
function onComplete(line_text, column_nr, perform_fn) {
	console.log(line_text)
	console.log(column_nr)
	console.log(perform_fn)
}

$(document).ready(function() {

	controller = $("#console").console({
		welcomeMessage : 'ProB 2.0 console',
		promptLabel : 'ProB> ',
		continuedPromptLabel : '----| ',
		commandValidate : onValidate,
		commandHandle : onHandle,
		completionHandle : onComplete,
		autofocus : true,
		animateScroll : true,
		lineWrapping : true,
		promptHistory : true
	});

});