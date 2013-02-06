//jFIXME Error with serverSynchronization and setDirty which results in unsaved Content (not moving out of the editor)
//jFIXME Error with serverSynchronization and setDirty when evaluating a document (js listeners are not set correctly)

wsid=-1;
$(document).ready(function(){
	if(typeof domReady == 'function'){
		domReady();
	}else{
		newDocument(wsid);
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
		$('#ui-id-1').worksheet(data);
		if(typeof setWorksheetLoaded=="function")
			setWorksheetLoaded(true);
		$('#ui-id-1').one("worksheetinitialized",function(){
			$("#loadingBar").hide();
			$("#loadingSheet").hide();
		});
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
		$('#ui-id-1').worksheet(data);
		if(typeof setWorksheetLoaded=="function")
			setWorksheetLoaded(true);
		$('#ui-id-1').one("worksheetinitialized",function(){
			//DEBUG alert("new Document worksheetInitialized");
			$("#loadingBar").hide();
			$("#loadingSheet").hide();
		});
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
		$('#ui-id-1').worksheet(data);
		if(typeof setWorksheetLoaded=="function")
			setWorksheetLoaded(true);
		$('#ui-id-1').one("worksheetinitialized",function(){
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
