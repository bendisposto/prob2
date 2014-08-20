
	$(function() {
		
		// Initialise dialogs ...
		initDialog($("#animation_view"),$("#animation_iframe"),$("#bt_open_animation_view"),"http://"+bms.host+":"+bms.port+"/sessions/CurrentAnimations",false);
		initDialog($("#state_view"),$("#state_iframe"),$("#bt_open_state_view"),"http://"+bms.host+":"+bms.port+"/sessions/StateInspector",false);
		initDialog($("#log_view"),$("#log_iframe"),$("#bt_open_log_view"),"http://"+bms.host+":"+bms.port+"/sessions/Log",false);
		initDialog($("#history_view"),$("#history_iframe"),$("#bt_open_history_view"),"http://"+bms.host+":"+bms.port+"/sessions/CurrentTrace",false);
		initDialog($("#events_view"),$("#events_iframe"),$("#bt_open_events_view"),"http://"+bms.host+":"+bms.port+"/sessions/Events",true);
		
		var groovyCodeMirror = CodeMirror(document.getElementById("script_view"), {
			lineNumbers: true,
			lineWrapping: true,
       		matchBrackets: true,
  			mode: "text/x-groovy"
		});
		
		$("#script_view").dialog({
			open: function(ev, ui){
				$.ajax({
    				type: 'POST',
    				data: {
  						task: 'init'
    				},
    				success: function (data) {
    					if(data.scriptstr.length > 0) {
    						groovyCodeMirror.getDoc().setValue(data.scriptstr)
    					}	
    				},
    				error:function(data,status,er) {
        				alert("error: "+data+" status: "+status+" er:"+er);
    				}
				});
				fixSizeDialog($("#script_view"),$(".CodeMirror"),0,31);
			},
			resize: function() { 
				fixSizeDialog($("#script_view"),$(".CodeMirror"),0,31);
			}, 
			autoOpen: false,
			width: 500,
			height: 400,
		    buttons: {
        		Save: function() {
					$.ajax({
	    				type: 'POST',
	    				data: {
	  						task: 'save',
	  						content: groovyCodeMirror.getDoc().getValue()
	    				},
	    				success: function (data) {		
	    				},
	    				error:function(data,status,er) {
	        				alert("error: "+data+" status: "+status+" er:"+er);
	    				}
					});
        		}
      		}
		});
		
		$("#bt_edit_script").click(function() {
			$("#script_view").dialog( "open" );
		});
		
		$("#bt_open_template").click(function() {
			$("#modal_open_template").modal('show')
		});
		$("#bt_create_template").click(function() {
			$("#modal_create_template").modal('show')
		});	
			
	});
	

	function fixSizeDialog(dialog,obj,ox,oy) {
		var newwidth = dialog.parent().width() - ox
		var newheight = dialog.parent().height() - oy
		obj.attr("style","width:"+(newwidth)+"px;height:"+(newheight-50)+"px");  
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
				fixSizeDialog(dialog,iframe,0,0);
			},
			open: function(ev, ui){
				iframe.attr("src",url);
				fixSizeDialog(dialog,iframe,0,0);
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
		window.location = "/bms/?template=" + templateFile
	}

	bms.createTemplateFile = function(dom_dir) {
		
		templateFile = $(dom_dir)[0].value
		
		$.ajax({
		    type: 'POST',
		    data: {
		    		task: 'save',
		    		newtemplate: templateFile
		    	},
		    success: function (data) {
		    	if(data === 'ok') {
		    		alert("Template saved")
		    		window.location = "/bms/?template=" + templateFile	
		    	} else if(data === 'notemplate') {
		    		alert("No template specified")
		    	}
		    },
		    error:function(data,status,er) {
		        alert("error: "+data+" status: "+status+" er:"+er);
		    }
		});
		
		//window.location = "/bms/?template=" + templateFile
		//console.log(templateFile)
	}
