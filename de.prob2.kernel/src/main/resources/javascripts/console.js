var controller;
var loglevel = "ERROR";
var bindingTable;

function onValidate(line) {
	return true;
};


function scrollDown(){
  window.scrollTo(0,document.body.scrollHeight);
}


function onComplete(line,column, perform) {
	var result;
	$.getJSON("complete", {
		input : line,
		col: column
	}, function(json) {
		perform(json);
		scrollDown();
	});
};

function updateImports() {
	var impdiv = $("#imports")[0];
	$.getJSON("imports", {}, function(data) {
		impdiv.value = data;
	});
}



function onHandle(line, report) {
	$.getJSON("evaluate", {
		input : line
	}, function(data) {
		if (!data.continued) {
			controller.lePrompt = false;
			report([ {
				msg : data.output,
				className : "jquery-console-message-value"
			} ]);

		} else {
			controller.lePrompt = true;
			report();
		}
			scrollDown();
	});
};

function switchLogLevel() {
	var level = "ERROR";
	var levela = $("#loglevel")[0];
	if (loglevel == "ERROR") {
		level = "TRACE";
	}
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

	 $.getJSON("scrollback", {}, function(data) {
		for(var i=0; i<data.length; i++) {
		   controller.addToHistory(data[i]);
		}
	});
	
   $.getJSON("evaluate", {input: '', reset: true}, function(data) {});

   $(window).resize(function() {
      $("#console").height($(window).height())
   });
   $("#console").height($(window).height())
   
   $(window).focus(function() {
     scrollDown();
   });
   
}
