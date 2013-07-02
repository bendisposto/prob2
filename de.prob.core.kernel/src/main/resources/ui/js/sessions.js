// client side template cache
var templates = []

// we need to load the error template in upfront
get_template("server_disconnected.html");

function sendCmd(s, data) {
	data.mode = 'command'
	data.cmd = s
	$.ajax({
		async : false,
		data : data
	});
}

function get_template(name) {
	var html = templates[name];
	if (html == undefined) {
		$.ajax({
			url : "/ui/templates/parts/" + name,
			success : function(result) {
				if (result.isOk === false) {
					alert(result.message);
				} else {
					html = result;
				}
			},
			async : false
		});
		templates[name] = html;
	}
	return html;
}

function disconnect() {
	$("body").replaceWith(get_template("server_disconnected.html"))
}

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
			disconnect()
		}
	});
}

listen();
