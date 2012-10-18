var outputline = 0;


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
				   $("#system_out").get(0).innerHTML += "<span class=\""+ data[prop].style+"\">"+data[prop].content.replace("\n","<br />"+"</span>");
				   outputline = data[prop].nr
                }
             }
   		     scrollDown();
				}
	})
	

	}, 300);
	

	
}
