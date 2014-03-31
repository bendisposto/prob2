
	$(function() {
		
		initDialog($("#history_view"),$("#history_iframe"),$("#bt_open_history_view"),"http://localhost:"+bms.port+"/sessions/CurrentTrace",true);
		initDialog($("#events_view"),$("#events_iframe"),$("#bt_open_events_view"),"http://localhost:"+bms.port+"/sessions/Events",true);
		initDialog($("#animation_view"),$("#animation_iframe"),$("#bt_open_animation_view"),"http://localhost:"+bms.port+"/sessions/CurrentAnimations",false);
		
		$("#bt_open_template").click(function() {
			$("#modal_open_template").modal('show')
		});
		
	});
	

	function fixSizeDialog(dialog,iframe) {
		var newwidth = dialog.parent().width()
		var newheight = dialog.parent().height()
		iframe.attr("style","width:"+(newwidth)+"px;height:"+(newheight-50)+"px");  
	}
  
	function initDialog(dialog,iframe,bt,url,autoopen) {

		dialog.dialog({
			  
			dragStart: function() {
				iframe.hide();
			},
			dragStop: function() { 
				iframe.show();
			},
			resize: function() { 
				iframe.hide(); 
			}, 
			resizeStart: function() { 
				iframe.hide(); 
			},
			resizeStop: function(ev, ui){
				iframe.show();
				fixSizeDialog(dialog,iframe);
			},
			open: function(ev, ui){
				iframe.attr("src",url);
				fixSizeDialog(dialog,iframe);
				dialog.css('overflow', 'hidden'); //this line does the actual hiding
			},
			autoOpen: autoopen,
			width: 350,
			height: 400
	
		});
  
		bt.click(function() {
			dialog.dialog( "open" );
		});
	  
	}
	
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
				hook.append(bms.session.render("/ui/bmsview/fb_dir_entry.html", {
					"name" : file.name,
					"path" : file.path,
					"dom" : dir_dom
				}))
			}
		}
		for (s in files) {
			var file = files[s]
			if (!file.hidden) {
				hook.append(bms.session.render("/ui/bmsview/fb_file_entry.html", {
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
	
	bms.browse = browse
	bms.fb_select_dir = fb_select_dir
	bms.fb_select_file = fb_select_file
	bms.fb_load_file = function(dom_dir) {
		templateFile = $(dom_dir)[0].value
		window.location = "/bms/?template=" + templateFile + "&lang=" + bms.lang
	}
