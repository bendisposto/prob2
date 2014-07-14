	infuser.defaults.templateSuffix = ".tpl.html"
	
	function initObservers(observers) {
		
		var ObserverJsonModel = function(observers) {
			
			 var self = this;
			 self.observers = ko.observableArray(ko.utils.arrayMap(observers, function(observer) { 
		         return { type: observer.type, objs: ko.observableArray(initObserverables(observer.objs)) }
		     }));
		     self.addItem = function(items, item, refresh) {
		    	 items.unshift(convertToObservable(item))
		    	 if(refresh) {
		    		refreshAccordion($(".observer_objs"));
					updateObserverObjMenu();
					updateObserverBindingMenu();
		    	 }
		     };
		     self.deleteItem = function(item,list) {
		    	 if (confirm("Delete?")==true) {
		    		 list.remove(item)
		    	 }
		     }
		     self.postRender = function(elements) {
				if(!$(elements).is(".infuser-loading")) {
					$.each(elements, function (i, obj) {
						if($(obj).hasClass("observer_objs")) {
							initAccordion($(obj));
						}
					});
					updateObserverObjMenu();
					updateObserverBindingMenu();
				}
		     }
		     
		}
		
		var observerModel = new ObserverJsonModel(observers);
		methodDraw.setObserverModel(observerModel);

		 ko.bindingHandlers.sortableList = {
			      init: function(element, valueAccessor) {
			          var list = valueAccessor();
			          $(element).sortable({
			              update: function(event, ui) {
			                  //retrieve our actual data item
			                  var item = ko.dataFor(ui.item.get(0));
			                  //figure out its new position
			                  var position = ko.utils.arrayIndexOf(ui.item.parent().children(), ui.item[0]);
			                  ui.item.get(0).remove()
			                  //remove the item and add it back in the right spot
			                  if (position >= 0) {	                     
			                	 list.remove(item);
			                     list.splice(position, 0, item);
			                  }
			                  refreshAccordion($(".observer_objs"));			                  
			              }
			          });
			      }
			  };

		ko.applyBindings(observerModel);
		
		$( ".observer_bindings" ).sortable();
		$( ".observer_bindings" ).disableSelection();
		
		var container = $(".observer_list")
		container.find('.truncate').textOverflow();
		
	}
	
	function refreshAccordion(container) {
		var isAccordion = !!container.data("ui-accordion");
		if(isAccordion) {
			container.accordion( "refresh" );
		}
	}
	
	function initAccordion(container) {
		var isAccordion = !!container.data("ui-accordion");
		if(container.attr("role") !== undefined && isAccordion) {
			container.accordion("destroy");
		}
		container.accordion({
			header: "> div > h3",
			collapsible: true
		}).sortable({
	        axis: "y",
	        handle: "h3",
	        stop: function( event, ui ) {
	          // IE doesn't register the blur when sorting
	          // so trigger focusout handlers to remove .ui-state-focus
	          ui.item.children( "h3" ).triggerHandler( "focusout" );
	        }
	      });
	}
	
	function updateObserverContainer() {
		var height = $("#observers").height()
		$(".observer_loop").css("max-height",height-60+"px");
	}
	
	function duplicateItem(listel, el) {
		var	obj = ko.dataFor(el.get(0));
		var clone = ko.toJS(obj)
		var list = ko.dataFor(listel)
		var listname = $(listel).attr("data-list")
		var newPosition = ko.utils.arrayIndexOf($(listel).children(), el[0]);
		var newel = $(listel).children().get(newPosition);		
		list[listname].splice(newPosition,0,convertToObservable(clone));
		return newPosition;
	}
	
	function deleteItem(listel,objel) {
		if (confirm("Delete?")==true) {
			var list = ko.dataFor(listel)
			var obj = ko.dataFor(objel)
			var listname = $(listel).attr("data-list")
			list[listname].remove(obj)
		}
	}
	
	function addBinding(el,jsonpattern) {
		var parentel = el.parent()
		var observerlistel = parentel.find(".observer_bindings");
		var listname= $(observerlistel).attr("data-list")
		var observerlist = ko.dataFor(observerlistel.get(0))
		var list = observerlist[listname]
		list.unshift(convertToObservable(jQuery.parseJSON(jsonpattern)))
		updateObserverBindingMenu()
	}
	
	function addAction(el,jsonpattern) {
		var actionListElement = el.find(".observer_actions");
		var actionList = ko.dataFor(actionListElement.get(0))
		var list = actionList["actions"]
		list.unshift(convertToObservable(jQuery.parseJSON(jsonpattern)))
	}
	
	function updateObserverObjMenu() {
		$(".observer_head").contextMenu({
			inSpeed: 0
		},
		function(action, el, menuitem, pos) {
			
			switch ( action ) {
				case 'duplicate':
					var newPosition =  duplicateItem(el.parent().parent().get(0), el.parent())
					refreshAccordion($(".observer_objs"));
				    $( ".observer_objs" ).accordion( "option", "active", newPosition );
					$('.observer_loop').animate({
				        scrollTop: $(el.parent()).offset().top - 240
				    }, 0);
					updateObserverObjMenu();
					updateObserverBindingMenu();
					break;
				case 'delete':
					deleteItem(el.parent().parent().get(0), el.parent().get(0))
					break;
				case 'addBinding':
					addBinding(el,menuitem.attr('data-json'))
					break;
				default:
					break;
			}
			
		});
	
	}
	
	function updateObserverBindingMenu() {
		$(".observer_binding").contextMenu({
			inSpeed: 0
		},
		function(action, el, menuitem, pos) {
			switch ( action ) {
				case 'duplicate':
					var newPosition =  duplicateItem(el.parent().get(0), el)
					updateObserverBindingMenu();
					break;
				case 'delete':
					deleteItem(el.parent().get(0), el.get(0))
					break;
				case 'addAction':
					addAction(el,menuitem.attr('data-json'))
					break;						
				default:
					break;
			}
			
		});
	}
	
	function initObserverables(list) {
	    var newList = [];
	    if(list !== undefined) {
	    	$.each(list, function (i, obj) {
	        	var newObj = {}; 
	        	Object.keys(obj).forEach(function (key) {
	        		newObj[key] = convertToObservable(obj[key]);
	        	}); 
	        	newList.push(newObj); 
	    	});
	    }
	    return newList; 
	}
	
	function convertToObservable(observable) { 
		
    	if (isArray(observable)) {

		    var newList = []; 
		    $.each(observable, function (i, obj) {
		        var newObj = {}; 
		        Object.keys(obj).forEach(function (key) { 
						newObj[key] = convertToObservable(obj[key]);
		        }); 
		        newList.push(newObj); 
		    });
		    return ko.observableArray(newList);
    		
    	} else if(isObject(observable)) {
	        var newObj = {}; 
	        Object.keys(observable).forEach(function (key) { 
					newObj[key] = convertToObservable(observable[key]);
	        }); 
	        return newObj;
    	} else {
    		return ko.observable(observable);
    	}
	    
	}	
	
	function isArray(what) {
	    return Object.prototype.toString.call(what) === '[object Array]';
	}
	
	function isObject(what) {
	    return Object.prototype.toString.call(what) === '[object Object]';
	}