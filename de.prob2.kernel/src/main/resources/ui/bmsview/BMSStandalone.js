
	$(function() {
		
		// Initialise dialogs ...
		initDialog($("#animation_view"),$("#animation_iframe"),$("#bt_open_animation_view"),"http://"+bms.host+":"+bms.port+"/sessions/CurrentAnimations",false);
		initDialog($("#state_view"),$("#state_iframe"),$("#bt_open_state_view"),"http://"+bms.host+":"+bms.port+"/sessions/StateInspector",false);
		initDialog($("#log_view"),$("#log_iframe"),$("#bt_open_log_view"),"http://"+bms.host+":"+bms.port+"/sessions/Log",false);
		initDialog($("#history_view"),$("#history_iframe"),$("#bt_open_history_view"),"http://"+bms.host+":"+bms.port+"/sessions/CurrentTrace",false);
		initDialog($("#events_view"),$("#events_iframe"),$("#bt_open_events_view"),"http://"+bms.host+":"+bms.port+"/sessions/Events",true);
			
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
	