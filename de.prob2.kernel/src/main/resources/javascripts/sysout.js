var outputline = 0;
var sysout = $("#system_out").get(0);


function scrollDown(){
  window.scrollTo(0,document.body.scrollHeight);
}

function toggle(element_id) {
 var elem = $(element_id).get(0)
 if(elem.style.display == "none"){
  elem.style.display = "block";
}else{
  elem.style.display = "none";
}
}

function display_trace(trace) {
  var id ="trace"+outputline;
  var m1 = $('<a href="javascript:toggle(\'#'+id+'\')">stacktrace</a>)<br />');
  var m2 = $('<span class="groovy_trace" style="display: none;" id="'+id+'"></span><br />');
  m1.appendTo(sysout);
  m2.appendTo(sysout);
  for (var i = 0; i < trace.length; i++) {
    var line = trace[i].replace(/\n/g,"<br />");
    m2.append(line+"<br />");
  } 
}

function initialize() {
// setup output polling
setInterval(function() {
	
	$.getJSON("outputs", {
		since : outputline
	}, function(data) {
   if (data != "") {
    for (var prop in data) {
      if (data.hasOwnProperty(prop)) { 
       var content = data[prop].content.replace(/ /g,"&nbsp;").replace(/\n/g,"<br />");
       outputline = data[prop].nr
       if (data[prop].msgtype == "output") {
        var m = $('<span class=groovy_output>'+content+'</span>'); 
        m.appendTo(sysout);
      }
      if (data[prop].msgtype == "error" || data[prop].msgtype == "trace") { 
        var m = $('<span class=groovy_error>'+content+' </span><span class=groovy_output>(</span>'); 
          m.appendTo(sysout);
        }
        if (data[prop].msgtype == "trace") { display_trace(data[prop].extra); }

      }
    }
    scrollDown();
  }
})
	

}, 300);



}
