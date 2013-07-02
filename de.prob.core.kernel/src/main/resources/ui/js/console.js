var controller;

function onValidate(line) {
	return true;
};

function onHandle(line) {
	sendCmd("exec", {
		"line" : line
	})
}

function onComplete(line_text, column_nr, perform_fn) {
	// console.log(line_text)
	// console.log(column_nr)
	// console.log(perform_fn)
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

function groovyResult(data) {
	var msg = data.result;
	controller.commandResult(msg);
}
function groovyError(data) {
	var msg = "EXCEPTION: " + data.message;
	controller.commandResult(msg);
}