bms = (function() {

	
	var extern = {}
	var session = Session();

	$(document).ready(function() {
		
	    $('#navigation a').stop().animate({'marginLeft':'-85px'},1000);

	    $('#navigation > li').hover(
	        function () {
	            $('a',$(this)).stop().animate({'marginLeft':'-2px'},200);
	        },
	        function () {
	            $('a',$(this)).stop().animate({'marginLeft':'-85px'},200);
	        }
	    );
		
	});

	$('.template').click(function() {

		$("#sourceModal").on('shown', function() {
			// editorHtml.refresh()
			// editorJavascript.refresh()
		}).on('hidden', function() {
			// renderEdit()
		});
		// Show Modal
		$("#sourceModal").modal('show');

		$(".template").find("a").stop().animate({
			'marginLeft' : '-85px'
		}, 200);

	})

	// --------------------------------------------
	// Helper functions
	// --------------------------------------------
	jQuery.fn.toHtmlString = function() {
		return $('<div></div>').html($(this).clone()).html();
	};

	jQuery.expr[':'].parents = function(a, i, m) {
		return jQuery(a).parents(m[3]).length < 1;
	};

	var readHTMLFile = function(url) {
		var toReturn;
		$.ajax({
			url : url,
			async : false
		}).done(function(data) {
			toReturn = data;
		});
		return toReturn;
	};
	// --------------------------------------------

	// --------------------------------------------
	// Rendering
	// --------------------------------------------

	function renderVisualization(observer,events) {
		if (templateFile) {
			var src = "http://localhost:8080/bms/" + templateFile;
			// Prevents flickering ...
			$("#vis_container")
					.append(
							'<iframe src="" width="100%" frameborder="0" scrolling="no" name="tempiframe" id="tempiframe" style="visibility:hidden"></iframe>')
			$('#tempiframe').attr("src", src)
			$('#tempiframe').on('load', function() {
				$('#tempiframe').css("visibility", "visible")
				$('#iframeVisualization').remove()
				$('#tempiframe').attr("id", "iframeVisualization")
				resizeIframe()
				checkObserver(observer)
				setupEvents(events)
			});
		}
	}
	
	function checkObserver(observer) {
//		console.log("Start checking observer")
//		console.log(observer)
		for (var i = 0; i < observer.length; i++) {
			var o = observer[i];
//			console.log("Calling Observer")
//			console.log(o.config[0])
			B[o.cmd](o.config[0]);
		}
	}
	
	function setupEvents(events) {
//		console.log("Start setting up events")
//		console.log(events)
		for (var i = 0; i < events.length; i++) {
			var e = events[i];
//			console.log("Setup up event")
//			console.log(e.config[0])
			B[e.cmd](e.config[0]);
		}		
	}
	
	// --------------------------------------------

	extern.client = ""
	extern.observer = null;
	extern.workspace = "";
	extern.init = session.init
	extern.session = session

	extern.renderVisualization = function(data) {
		renderVisualization(JSON.parse(data.lang).observer, JSON
				.parse(data.lang).events)
	}

	function browse(dir_dom) {
		$('#filedialog').off('hidden.bs.modal')
		$('#filedialog').on('hidden.bs.modal', set_ok_button_state(dir_dom))
		$("#filedialog").modal('show')
		browse2(dir_dom)
	}

	function set_ok_button_state(dir_dom) {
		return function() {
			var file = $(dir_dom)[0].value
			var valid = check_file(file);
			if (valid) {
				$("#fb_okbtn").removeAttr("disabled")
			} else {
				$("#fb_okbtn").attr("disabled", "disabled")
			}
		}
	}

	function browse2(dir_dom) {
		var dir = $(dir_dom)[0].value
		// prepare dialog
		var data = request_files(dir)
		$(dir_dom).val(data.path)
		filldialog(data.dirs, data.files, dir_dom)
	}

	function request_files(d) {
		var s;
		$.ajax({
			url : "/files?path=" + d + "&extensions=bms&workspace="
					+ extern.workspace,
			success : function(result) {
				if (result.isOk === false) {
					alert(result.message);
				} else {
					s = JSON.parse(result);
				}
			},
			async : false
		});
		return s;
	}

	function check_file(d) {
		var s;
		$.ajax({
			url : "/files?check=true&path=" + d + "&extensions=bms&workspace="
					+ extern.workspace,
			success : function(result) {
				if (result.isOk === false) {
					alert(result.message);
				} else {
					s = JSON.parse(result);
				}
			},
			async : false
		});
		return s;
	}

	function filldialog(dirs, files, dir_dom) {
		$(".filedialog_item").remove()
		$(".filedialog_br").remove()
		var hook = $("#filedialog_content")
		var s
		for (s in dirs) {
			var file = dirs[s]
			if (!file.hidden) {
				hook.append(session.render("/ui/bmsview/fb_dir_entry.html", {
					"name" : file.name,
					"path" : file.path,
					"dom" : dir_dom
				}))
			}
		}
		for (s in files) {
			var file = files[s]
			if (!file.hidden) {
				hook.append(session.render("/ui/bmsview/fb_file_entry.html", {
					"name" : file.name,
					"path" : file.path,
					"dom" : dir_dom
				}))
			}
		}
	}

	function fb_select_dir(dir_dom, path) {
		$(dir_dom).val(path)
		browse2(dir_dom)
	}
	function fb_select_file(dir_dom, path) {
		$(dir_dom).val(path)
		$("#filedialog").modal('hide')
	}

	function resizeIframe() {
		var newIframeHeight = $("#iframeVisualization").contents().find("html")
				.height()
				+ 'px';
		$('#iframeVisualization').css("height", newIframeHeight);
	}

	extern.browse = browse
	extern.fb_select_dir = fb_select_dir
	extern.fb_select_file = fb_select_file
	extern.fb_load_file = function(dom_dir) {
		templateFile = $(dom_dir)[0].value
		session.sendCmd("setTemplate", {
			"path" : templateFile
		})
		$("#sourceModal").modal('hide')
		$("#chooseTemplateBox").css("display", "none");
	}

	extern.reloadTemplate = function(data) {
		templateFile = data.template
		renderVisualization(JSON.parse(data.lang).observer, JSON
				.parse(data.lang).events)
	}
	
	// --------------------------------------------
	// External API Calls
	// --------------------------------------------
	extern.executeOperation = function(op, predicate) {
		session.sendCmd("executeOperation", {
			"op" : op,
			"predicate" : predicate,
			"client" : extern.client
		})
	}
	// --------------------------------------------

	return extern;

}())
