//jFIXME Error with serverSynchronization and setDirty when evaluating a document (js listeners are not set correctly)
var debug=window.location.href.match("\\?debug=true","g")!=null;
var wsid=-1;
$(document).ready(function(){
	if(typeof domReady == 'function'){
		domReady();
	}else{
		newDocument(wsid);
	}
	if(debug){
		$("BODY").bind("worksheetdirtystatechange",function(event,data){window.console.debug("dirty=",data)});
		$("BODY").bind("blocksyncstart",function(event,data){window.console.debug("sync of block: ",data)});
		
	}
});

function loadDocument(content,id){
	wsid=id;
	var params=addURIParameter("", "worksheetSessionId", wsid);
	params=addURIParameter(params, "documentXML", content);
	$.ajax("loadDocument",{
		type : "POST",
		data : params
	}).done($.proxy(function(data, status, xhr) {
		if(typeof editorSetSessionId=="function")
			editorSetSessionId(document.cookie.match(/JSESSIONID=(\w*)/)[1]);
		data = jQuery.parseJSON(xhr.responseText);
		data = $.recursiveFunctionTest(data);
		data.sessionId=wsid;
		$('#ws-id-1').one("worksheetinitialized",function(){
			$("#loadingBar").hide();
			$("#loadingSheet").hide();
		});
		$('#ws-id-1').worksheet(data);
		if(typeof setWorksheetLoaded=="function")
			setWorksheetLoaded(true);
		
	},this));	
}
function newDocument(id){
	wsid=id;
	//DEBUG alert("new Document");
	$.ajax("newDocument",{
		type : "POST",
		data : "worksheetSessionId="+wsid
	}).done($.proxy(function(data, status, xhr) {
		//DEBUG alert("new Document Result");
		if(typeof editorSetSessionId=="function")
			editorSetSessionId(document.cookie.match(/JSESSIONID=(\w*)/)[1]);
		data = jQuery.parseJSON(xhr.responseText);
		data = $.recursiveFunctionTest(data);
		data.sessionId=wsid;
		$('#ws-id-1').bind("",function(){
			//DEBUG alert("new Document worksheetInitialized");
			$("#loadingBar").hide();
			$("#loadingSheet").hide();
		});
		$('#ws-id-1').bind("worksheetevalstart",function(){
			$("#loadingBar").show();
			$("#loadingSheet").show();
		});
		$('#ws-id-1').bind("worksheetinitialized worksheetevalend ",function(){
			$("#loadingBar").hide();
			$("#loadingSheet").hide();
		});
		$('#ws-id-1').worksheet(data);
		if(typeof setWorksheetLoaded=="function")
			setWorksheetLoaded(true);
		
	},this));
	
}

function refreshDocument(id){
	$(".ui-worksheet").worksheet("destroy");
	setWorksheetLoaded(false);
	wsid=id;
	//jTODO replace by an refresh Document Function
	$.ajax("newDocument",{
		type : "POST",
		data : "worksheetSessionId="+wsid
	}).done($.proxy(function(data, status, xhr) {
		data = jQuery.parseJSON(xhr.responseText);
		data = $.recursiveFunctionTest(data);
		data.sessionId=wsid;
		$('#ws-id-1').worksheet(data);
		if(typeof setWorksheetLoaded=="function")
			setWorksheetLoaded(true);
		$('#ws-id-1').one("worksheetinitialized",function(){
			$("#loadingBar").hide();
			$("#loadingSheet").hide();
		});
	},this));
}


function addURIParameter (to, key, value) {
	if (to != "")
		to += "&";
	to += encodeURIComponent(key) + "="
			+ encodeURIComponent(value);
	return to;
}
