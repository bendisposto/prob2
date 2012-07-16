var controller;
var loglevel = "ERROR";
var bindingTable;

$.fn.dataTableExt.oApi.fnReloadAjax = function(oSettings, sNewSource) {
	oSettings.sAjaxSource = sNewSource;
	this.fnClearTable(this);
	this.oApi._fnProcessingDisplay(oSettings, true);
	var that = this;

	$.getJSON("bindings", null, function(json) {
		/* Got the data - add it to the table */
		for ( var i = 0; i < json.aaData.length; i++) {
			that.oApi._fnAddData(oSettings, json.aaData[i]);
		}

		oSettings.aiDisplay = oSettings.aiDisplayMaster.slice();
		that.fnDraw(that);
		that.oApi._fnProcessingDisplay(oSettings, false);
		reattach_clickhandlers();
	});
}

$('#activityCodeDataTable').dataTable();

function reattach_clickhandlers() {
	$("#bindings tbody tr").click(function(e) {
		// alert(e.srcElement.innerText);
	});
}

function onValidate(line) {
	return true;
};

function onComplete(line, perform) {
	var result;
	$.getJSON("complete", {
		input : line
	}, function(json) {
		perform(json);
	});
};

function onHandle(line, report) {
	$.getJSON("evaluate", {
		input : line
	}, function(data) {
		bindingTable.fnReloadAjax()
		if (!data.continued) {
			controller.lePrompt = false;
			report([ {
				msg : data.output,
				className : "jquery-console-message-value"
			} ]);

			var impdiv = $("#imports")[0];
			impdiv.value = data.imports;

		} else {
			controller.lePrompt = true;
			report();
		}
	});
};

function switchLogLevel() {
	var level = "ERROR";
	var levela = $("#loglevel")[0];
	if (loglevel == "ERROR")
		level = "TRACE";
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

	bindingTable = $('#bindings').dataTable({
		"bPaginate" : false,
		"bLengthChange" : true,
		"bFilter" : true,
		"bSort" : true,
		"aaSorting" : [ [ 0, "asc" ] ],
		"aoColumns" : [ {
			sWidth : '50px'
		}, {
			sWidth : '100px'
		}, {
			sWidth : '120px'
		} ]
	});

	bindingTable.fnReloadAjax()
	$('#bindings_filter label input').prop('type', 'search')

	controller = $("#console").console({
		welcomeMessage : 'ProB 2.0 console',
		promptLabel : 'ProB> ',
		continuedPromptLabel : '----| ',
		commandValidate : onValidate,
		commandHandle : onHandle,
		completionHandle : onComplete,
		autofocus : true,
		animateScroll : true,
		promptHistory : true
	});
}
