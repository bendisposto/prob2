bms = (function() {

	var extern = {}
	var session = Session();

	// --------------------------------------------
	// Helper functions
	// --------------------------------------------
	jQuery.fn.toHtmlString = function() {
		return $('<div></div>').html($(this).clone()).html();
	};

	jQuery.expr[':'].parents = function(a, i, m) {
		return jQuery(a).parents(m[3]).length < 1;
	};
			
	// --------------------------------------------
		
	function initExecuteOperationObserver(objs) {
		$.each(objs, function(i,o)
		{	
			var selector = $(o.group);
			var events = $._data( selector[0], 'events' )
			if (events === undefined || (events !== undefined && events.click === undefined)) {
			    selector.attr("class","mouse_hand")
			    var items = o.items
			    selector.click(function() {
				 	 $.each(items, function(i,item) {
						  var parameter = item.parameter;
						  if(parameter === undefined)
							  parameter = "1=1"
						  var operation = item.operation
					 		  session.sendCmd("executeOperation", {
								"op" : operation,
								"predicate" : parameter,
								"client" : bms.client
							})
					 });
			    });
			}			
		});		
	}
	
	var clones = {};	
	function resetCSP(selectors) {
		// Revert objects ...
		//$.each(clones, function(i, v) {
		//	$(i).replaceWith(v)
		//});
		// Clone objects
		//$.each(selectors, function(i, v) {
		//	clones[v] = $(v).clone(true,true)
		//});
	}
	
	extern.client = ""
	extern.observer = null;
	extern.init = session.init
	extern.session = session

	// The port and host fields are accessed from BMSStandalone.js
	extern.port = null
	extern.host = null
	extern.lang = null;

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
	
	extern.update_visualization = function(data) {
		vs = eval(data.values);
		for (e in vs) {
			v = vs[e];
			eval(v)
		}
	}
	
	extern.triggerObserverActions = function(data) {
		$.each(data.actions, function(i,action) {
			var attrObj = svgAttributeList[action.attr]
			if(attrObj !== undefined) {
				var changeFunc = attrObj.change
				if(changeFunc !== undefined) {
					changeFunc(action.selector,action.value)
				} else {
					$(action.selector).attr(action.attr,action.value)
				}	
			} else {
				$(action.selector).attr(action.attr,action.value)
			}
		});
	}
	
	return extern;

}())
