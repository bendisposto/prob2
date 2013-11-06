var session = parent.bms.session;

$(document).ready(function() {
		
  {{#data.observer}}
    {{#cspEventObserver}}
      {{#model.traceAsList}}
	triggerCspEventObserver("{{.}}","{{events}}","{{selector}}","{{attribute}}","{{value}}")
      {{/model.traceAsList}}
    {{/cspEventObserver}}
  {{/data.observer}}
		
});

function triggerCspEventObserver(lastop,events,selector,attr,val) {
  obj = $(selector)
  if(events.indexOf(lastop) !== -1) {
    if(val === "true") {
	    val = true;
    } else if(val === "false") {
	    val = false;
    }
    if(!(typeof obj.attr(attr) === 'undefined')) {
	    obj.attr(attr, val)
    } else if(!(typeof obj.prop(attr) === 'undefined')) {
	    obj.prop(attr, val)				
    } else if(!(typeof obj.css(attr) === 'undefined')) {
	    obj.css(attr, val)
    } else {
	    obj.attr(attr, val)
    }
  }
}