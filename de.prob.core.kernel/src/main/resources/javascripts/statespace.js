function initialize() {
// setup output polling
setInterval(function() {
	
	$.getJSON("statespace_servlet", {
		since : 1
	}, function(data) {
   if (data != "") {
    /* for (var prop in data) {
      if (data.hasOwnProperty(prop)) { 
       var outputline = data[prop].qualifier
      
        var m = $("#content"); 
        m.append(outputline);
      } 
    } //for
    */ 
     var m = $("#content"); 
      m.append(data.installed.qualifier );
    
  } //if 
})
	

}, 300);
}