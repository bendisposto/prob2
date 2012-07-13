var controller;
var loglevel = "ERROR";

function onValidate(line) {
	return true;
};

function onHandle(line, report) {
	$.getJSON("evaluate", {
		input : line
	}, function(data) {
		if (!data.continued)
			{
			controller.lePrompt = false;
			report([ {
				msg : data.output,
				className : "jquery-console-message-value"
			}]);
			}
		else { controller.lePrompt = true; report(); }
	});
};


function switchLogLevel() {
	var level = "ERROR";
	var levela = $("#loglevel")[0];   
	if (loglevel == "ERROR") level = "TRACE"
	$.getJSON("loglevel", {
		input : level
	}, function(data) {
	   levela.innerHTML = data.output;
	   loglevel = data.output;
	});
	
}


function initialize() {
	lePrompt = "ProB> ";
	var levela = $("#loglevel")[0];   
	$.getJSON("loglevel", {
		input : loglevel
	}, function(data) {
	   levela.innerHTML = data.output;	
	});
	
	
	controller = $("#console").console({
		welcomeMessage : 'ProB 2.0 console',
		promptLabel : 'ProB> ',
		continuedPromptLabel : '----| ',
		commandValidate : onValidate,
		commandHandle : onHandle,
		autofocus : true,
		animateScroll : true,
		promptHistory : true
	});
}
