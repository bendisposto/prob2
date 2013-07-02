function listen() {
	var data = {
		'mode' : 'listen'
	};
	$.ajax({
		data : data,
		success : function(data) {
			if (data != "") {
				dobj = JSON.parse(data);
				console.log(dobj);
				window[dobj.cmd](dobj);
			}
			listen();
		},
		error : function(e, s, r) {
			console.log(s);
			console.log(r);
		}
	});
}

function sendCmd(s, data) {
	data.mode = 'command'
	data.cmd = s
	$.ajax({
		async : false,
		data : data
	});
}

function Ok(data) {
	console.log("OK Function")
}
function fail(data) {
	console.log("Fail Function")
}