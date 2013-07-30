function Session() {
	var extern = {}

	var current = 0;
	var poll_interval = 100;

	// client side template cache
	var templates = []

	// we need to load the error template in upfront
	get_template("/ui/common/server_disconnected.html");

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
				url : name,
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
		current = 0;
		$("body").replaceWith(
				get_template("/ui/common/server_disconnected.html"))
	}

	function listen(client) {
		var data = {
			'mode' : 'update',
			'lastinfo' : current,
			'client' : client
		};
		console.log("Requesting " + data.lastinfo)
		$
				.ajax({
					data : data,
					success : function(data) {
						if (data != "") {
							dx = JSON.parse(data);
							dobjs = dx.content
							current = dx.id
							if (!(Object.prototype.toString.call(dobjs) === "[object Array]")) {
								dobjs = [ dobjs ]
							}
							for (i in dobjs) {
								var dobj = dobjs[i]
								eval(dobj.cmd)(dobj)
							}
						}
						listen(client);
					},
					error : function(e, s, r) {
						disconnect()
					}
				});
	}

	extern.init = function(client) {
		listen(client);
		console.log("init")
	};

	extern.sendCmd = sendCmd;

	return extern;
}