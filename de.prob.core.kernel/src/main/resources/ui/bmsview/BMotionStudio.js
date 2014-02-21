bms = (function() {

	var extern = {}
	var session = Session();
	var svgCanvas = null;
	var svgId = null;

	$(document).ready(function() {
		
	    $('#navigation a').stop().animate({'marginLeft':'-60px'},1000);

	    $('#navigation > li').hover(
	        function () {
	            $('a',$(this)).stop().animate({'marginLeft':'-2px'},200);
	        },
	        function () {
	            $('a',$(this)).stop().animate({'marginLeft':'-60px'},200);
	        }
	    );

	    $('.nav_open_template').click(function() {
			$("#modal_open_template").modal('show');
	    });
	    
		$('.nav_edit_template').click(function() {

			// TODO: what is if we have more than one svg element in template?
			var svgElement = $('svg', window.parent.document)
			svgId = svgElement.attr("id")
			session.sendCmd("openSvgEditor", {
				"id" : svgId,
				"client" : parent.bms.client
			})
			/*$( "#svgeditor" ).on('shown.bs.modal', function (e) {				
			}).on('hidden.bs.modal', function (e) {
			});*/

			$(".template").find("a").stop().animate({
				'marginLeft' : '-60px'
			}, 200);

		})

		$('#svgedit').load(function() {
			svgCanvas = new embedded_svg_edit(this);
		});
		
		$('#bt_svgSave').click(function() {
			svgCanvas.getSvgString()(handleSvgData);
		});
		
	});
	$(window).bind("resize", rescale);	
	
	function handleSvgData(data, error) {
		if (error) {
			alert('error ' + error);
		} else {
		 	session.sendCmd("saveSvg", {
				"svg" : data,
				"id" : svgId,
				"client" : parent.bms.client
			})
		}			
	}
	
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
	
	function rescale() {
	    var size = {width: $(window).width() , height: $(window).height() }
	    var offset = 25;
	    var offsetBody = 200;
	    $('#modal_svgeditor').css('height', size.height - offset );
	    $('#modal_svgeditor .modal-body').css('height', size.height - (offset + offsetBody));
	    $('#modal_svgeditor').css('top', 0);
	}
	// --------------------------------------------

	// --------------------------------------------
	// Rendering
	// --------------------------------------------

	function renderVisualization(observer,data) {
		checkObserver(observer,data)
		extern.stateChange(data)
	}
	
	function checkObserver(observer,data) {
		var observerList = observer.observer;
		if (observerList !== undefined) {
			for ( var i = 0; i < observerList.length; i++) {
				var observer = observerList[i];
				bms[observer.cmd](observer,data);
			}
		}
	}
	
	// --------------------------------------------

	extern.client = ""
	extern.observer = null;
	extern.init = session.init
	extern.session = session

	function browse(dir_dom) {
		$('#modal_filedialog').off('hidden.bs.modal')
		$('#modal_filedialog').on('hidden.bs.modal', set_ok_button_state(dir_dom))
		$("#modal_filedialog").modal('show')
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
			url : "/files?path=" + d + "&extensions=bms",
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
			url : "/files?check=true&path=" + d + "&extensions=bms",
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
		$("#modal_filedialog").modal('hide')
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

	extern.setTemplate = function(data) {
		window.location = "/bms/?template=" + data.request;
	}
	
	extern.reloadTemplate = function(data) {
		renderVisualization(JSON.parse(data.observer).wrapper, JSON.parse(data.data))
	}

	extern.renderVisualization = function(data) {
		renderVisualization(JSON.parse(data.observer).wrapper, JSON.parse(data.data))
	}
	
	extern.stateChange = function(data) {
	}
	
	extern.openSvgEditor = function(data) {
		
		// Init observers
		var oContainer = $("#svgedit").contents().find("#observers").find(
				"#accordion")
		oContainer.empty()
		var json = JSON.parse(data.json)[0]
		if (json !== undefined) {
			var observer = json.observer
			$.each(observer, function(i, v) {
				var oName = v.cmd
				$.each(v.objects, function(i, v) {
					oContainer.append(session.render("/ui/bmsview/ui_" + oName
							+ ".html", v))
				});
			});
			svgCanvas.initObservers();
		}
		
		// Init editor
		var svg = data.svg
		svgCanvas.setSvgString(svg)
		$("#modal_svgeditor").modal('show');
		rescale();
		
	}
	
	extern.promptReload = function(data) {
		window.location.reload();	
	}
	
	extern.translateValue = function(val) {
		if (val === "true") {
			return true;
		} else if (val === "false") {
			return false;
		} else if(!isNaN(val)) {
			val = parseInt(val)
		}
		return val;
	}
	
	executeOperation = function(observer, formulas) {
		
		  var objects = observer.objects
		  
		  $.each(objects, function(i,v)
		  {
			  var o = v;
			  var selector = $(o.selector);
			  
			  var events = $._data( selector[0], 'events' )
			  if (events === undefined || (events !== undefined && events.click === undefined)) {
				    selector.attr("class","mouse_hand")
				    var ops = o.ops
				    selector.click(function() {
					 	 $.each(ops, function(i,v) {
							  var predicate = v.predicate;
							  if(predicate === undefined)
								  predicate = "1=1"
							  var operation = v.operation
  					 		  session.sendCmd("executeOperation", {
									"op" : operation,
									"predicate" : predicate,
									"client" : parent.bms.client
								})
						 });
				   
				    });
			  
			  }
			  
		  });
		
	}
	
	var bodyClone;
	
	resetCSP = function() {
		// Revert objects ...
		if(bodyClone) {
			$("body").replaceWith(bodyClone)
		}
		bodyClone = $("body").clone(true,true)	
	}
	
	extern.update_visualization = function(data) {
		vs = eval(data.values);
		for (e in vs) {
			v = vs[e];
			eval(v)
		}
	}

	return extern;

}())
