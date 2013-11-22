var session = parent.bms.session;

$(document).ready(function() {
		
  {{#data.observer}}
    {{#predicateObserver}}
      
      {{#trigger}}
	triggerPredicateObserver("{{predicate}}","{{selector}}","{{attribute}}","{{value}}")
      {{/trigger}}
      
    {{/predicateObserver}}
    {{#executeOperation}}
      setupExecuteOperation("{{selector}}","{{operation}}","{{predicate}}")
    {{/executeOperation}}
  {{/data.observer}}
		
});

function triggerPredicateObserver(pred,selector,attr,val) {
  obj = $(selector)
  if (pred === "true") {
	  if(val === "true") {
		  val = true;
	  } else if(val === "false") {
		  val = false;
	  }
	  if(!(typeof obj.prop(attr) === 'undefined')) {
		  obj.prop(attr, val)
	  } else if(!(typeof obj.attr(attr) === 'undefined')) {
		  
		  obj.attr(attr, val)				
	  } else if(!(typeof obj.css(attr) === 'undefined')) {
		  obj.css(attr, val)
	  }
  }
}

function setupExecuteOperation(selector,operation,predicate) {
  obj = $(selector)
  obj.click(function() {
    session.sendCmd("executeOperation", {
      "op" : operation,
      "predicate" : predicate,
      "client" : parent.bms.client
    })
  });
}