
function setLogLevel(level) {
 $("#loglevel")[0].innerHTML = level;
}

function scrollDown(){
  window.scrollTo(0,document.body.scrollHeight);
}

function initialize() {
	// setup output polling
	setInterval(function() {
		$.ajax({
			url : "outputs",
			success : function(data) {
				if (data != "") {
					$("#system_out").get(0).innerHTML += data;
					scrollDown();
				}
			},
			dataType : "json"
		});
	}, 100);
}
