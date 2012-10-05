function initialize() {
	// setup output polling
	setInterval(function() {
		$.ajax({
			url : "outputs",
			success : function(data) {
				if (data != "") {
					$("#system_out").get(0).innerHTML += data
				}
			},
			dataType : "json"
		});
	}, 100);
}
