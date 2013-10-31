B = (function() {

	var extern = {}

	$(document).ready(function() {
	
	});
	
	extern.predicateObserver = function(config) {
		var selector = config.selector
		var attr = config.attribute
		var val = config.value
		var predicate = config.predicate
		var obj = $("#iframeVisualization").contents().find(selector)
		if (predicate == "true") {
			var prop = obj.prop("disabled")
			if(val == "true") {
				val = true;
			} else if(val == "false") {
				val = false;
			}
			if(typeof prop === 'undefined') {
				obj.attr(attr, val)
			} else {
				obj.prop(attr, val)
			}
		}
	}
	
	extern.executeOperation = function(config) {
		var selector = config.selector
		var operation = config.operation
		var predicate = config.predicate
		var obj = $("#iframeVisualization").contents().find(selector)
		obj.click(function() {
			  parent.bms.executeOperation(operation,predicate)
		});
	}
	
	return extern;

}())