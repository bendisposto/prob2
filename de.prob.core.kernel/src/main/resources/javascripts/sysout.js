var outputline = 0;

function setLogLevel(level) {
 $("#loglevel")[0].innerHTML = level;
}

function scrollDown(){
  window.scrollTo(0,document.body.scrollHeight);
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
				   $("#system_out").get(0).innerHTML += data[prop].content.replace("\n","<br />");
				   outputline = data[prop].nr
                }
             }
				}
	})
	

	}, 300);
	
}
