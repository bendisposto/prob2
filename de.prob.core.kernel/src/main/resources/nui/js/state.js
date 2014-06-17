var lastinfo = -1;

function receive() {

        var data = {
			'state' : lastinfo,
		};


		$.ajax({    url: "/data/",
					data : data,
					async : false,
					cache: false,
					success : function(data) {
						dx = data.split('\n')
						ninfo = JSON.parse(dx[0]);
						if (lastinfo < ninfo) { lastinfo = ninfo; $("#updates").append("<li>"+dx[0]+": "+dx[1]+"</li>") }
					},
					error : function(e, s, r) {
						alert('error')
					}
				});


}

setInterval(receive, 500);