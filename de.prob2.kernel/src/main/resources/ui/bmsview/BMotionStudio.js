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
	
	extern.client = ""
	extern.observer = null;
	extern.init = session.init
	extern.session = session

	// The port and host fields are accessed from BMSStandalone.js
	extern.port = null
	extern.host = null
	extern.lang = null;

	extern.applyJavaScript = function(data) {
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
				var selector = $(action.selector)
				var changeFunc = attrObj.change
				if(changeFunc !== undefined) {
					changeFunc(selector,action.value)
				} else {
					selector.attr(action.attr,action.value)
				}	
			} else {
				selector.attr(action.attr,action.value)
			}
		});
	}

	extern.applyTransformers = function(data) {
		var d1 = JSON.parse(data.transformers)
		var i1 = 0
		//var process = function() {
		for (; i1 < d1.length; i1++) {
			var t = d1[i1]
			var selector = $(t.selector)
			var attrs = {}
			var d2 = t.attributes
			var i2 = 0
			for (; i2 < d2.length; i2++) {
				var a = d2[i2]
				var attrObj = svgAttributeList[a.name]
				if (attrObj !== undefined) {
					var changeFunc = attrObj.change
					if (changeFunc !== undefined) {
						changeFunc(selector, a.value)
					} else {
						attrs[a.name] = a.value;
					}
				} else {
					attrs[a.name] = a.value;
				}
			}
			selector.attr(attrs)
			//if (i1 + 1 < length && i1 % 100 == 0) {
			//	setTimeout(process, 5);
			//}	
		}
		//};
		//process();
	}
	
	return extern;

}())
