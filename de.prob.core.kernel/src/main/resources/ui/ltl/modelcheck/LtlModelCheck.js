LtlModelCheck = (function() {
	var extern = {};
	extern.formulas = null;
	
	/* Create and destroy */
	extern.destroy = function() {
		$(window).unbind('resize');
	}
	
	/* Restore state */
	extern.saveState = function() {
	
	}
	
	extern.restore = function() {		
		// Register buttons
		registerAddFormula();
		registerRemoveFormula();
		registerRemoveSelectedFormulas();
		
		// Register toggle of formula list
		registerFormulaListToggle();	

		// Register CodeMirror
		registerCodeMirror();
		
		// Get or restore formula list
		if (extern.formulas == null) {
			Util.getFormulaList();
		} else {
			setFormulaList(extern.formulas);
		}
		
		registerResize();
	}
	
	/* Register buttons */
	function registerAddFormula() {
		$("#mc-add-formula").click(function() {
			addFormula();
		});
	}
	
	function registerRemoveFormula() {
		$("#mc-remove-current").click(function() {
			removeCurrent();
		});
	}
	
	function registerRemoveSelectedFormulas() {
		$("#mc-remove-selected").click(function() {
			removeSelected();
		});
	}
	
	function registerFormulaListToggle() {
		$("#last-formulas-panel").click(function(event) {
			if ($(event.target).is("div") || $(event.target).is("#last-formulas-icon")) {
				var icon = $('#last-formulas-icon');
				if (icon.hasClass('glyphicon-chevron-right')) {
					icon.removeClass('glyphicon-chevron-right');
					icon.addClass('glyphicon-chevron-down');
				} else {
					icon.addClass('glyphicon-chevron-right');
					icon.removeClass('glyphicon-chevron-down');
				}
				
				$(".last-formulas").toggle();
				resizeCodeMirror();
			}
		});
	}
	
	/* Formula list */
	extern.setFormulaList = function(data) {
		setFormulaList(JSON.parse(data.formulas));
		resizeCodeMirror();
	}
	
	function setFormulaList(formulas) {
		var list = $('.last-formulas-list');
		list.empty();
		
		extern.formulas = formulas;
		// Fill if empty list
		if (extern.formulas == null) {
			extern.formulas = [];
		}
		if (extern.formulas.length == 0) {
			extern.formulas.push("// Enter a formula");
		}
		
		// Add list items
		for (var i = 0; i < extern.formulas.length; i++) {
			addFormulaListItem(extern.formulas[i])
		}
		
		// Register selection listener
		registerSelectionListener();
		
		// Select last element
		var index = extern.formulas.length - 1;
		selectItem(index);
		
		checkboxSelectionChanged();
	}
	
	function addFormulaListItem(formula) {
		$('.last-formulas-list').append($("<li><input type=\"checkbox\"><span>" + formula + "</span></li>"));
	}
	
	function saveFormulaList() {
		if (extern.formulas != null) {
			Util.saveFormulaList(extern.formulas);
		}
	}
	
	extern.saveFormulaListSuccess = function(data) {
	
	}
	
	/* List selection */
	function selectItem(index, elt = null) {
		var element = elt || $(".last-formulas-list li")[index];
		$(element).addClass("ui-selected").siblings().removeClass("ui-selected");
		
		LtlEditor.cm.setValue(extern.formulas[index]);
	}
	
	function registerSelectionListener() {
		// Register selection listener to pattern list
		$(".last-formulas-list li").click(function(event) {
			if (!$(event.target).is("input") && !$(this).hasClass("ui-selected")) {
				var index = $(".last-formulas-list li").index($(this));
				selectItem(index, $(this));
			}
		});
		
		// Register selection listener for checkboxes
		$(".last-formulas-list input:checkbox").change(function(event) {
			checkboxSelectionChanged();
		});
	}
	
	function checkboxSelectionChanged() {
		var selectedCount = $(".last-formulas-list input:checked").size();
		var count = 0;
		if (extern.formulas != null) {
			count = extern.formulas.length;
		}
		$("#mc-selected-badge").text(selectedCount + "/" + count);
		if (selectedCount == 0) {
			$("#mc-remove-selected-button").hide();
		} else {
			$("#mc-remove-selected-button").show();		
		}
	}
	
	function getSelectedItems() {
		var selectedItems = [];
		var list = $(".last-formulas-list li");
		$(".last-formulas-list input:checked").each(function(i, element) {
			selectedItems.push({
				element: element.parentNode,
				index: list.index(element.parentNode)
			});
		});
		return selectedItems;
	}
	
	function updateItem(formula) {
		var element = $(".last-formulas-list .ui-selected");
		var index = element.index();
		if (index >= 0) {
			extern.formulas[index] = formula;
			$(".last-formulas-list .ui-selected > span").text(formula);
		}
	}
	
	/* Add and remove formula */
	function addFormula() {
		// Add new formula
		var formula = "// Enter a formula";
		if (extern.formulas == null) {
			extern.formulas = [];
		}
		extern.formulas.push(formula);
		addFormulaListItem(formula);
		
		// Register selection listener
		registerSelectionListener();
		
		// Select last element
		var index = extern.formulas.length - 1;
		selectItem(index);
		
		// Save new item
		saveFormulaList();
	}
	
	function removeCurrent() {
		var element = $(".last-formulas-list .ui-selected");
		var index = element.index();
		
		// Remove from formula list
		extern.formulas.splice(index, 1); 
		element.remove();
		
		// Save list
		if (extern.formulas.length == 0) {
			// Empty list, so add default formula
			addFormula();
		} else {
			saveFormulaList();
			
			// Select new item at removed index or the last index
			selectItem(Math.min(index, extern.formulas.length -1));
		}
	}
	
	function removeSelected() {
		var selectedItems = getSelectedItems();
		if (selectedItems.length > 0) {
			var index = selectedItems[0].index;
		
			for (var i = 0; i < selectedItems.length; i++) {
				var item = selectedItems[i];
				
				// Remove from formula list
				extern.formulas.splice(item.index, 1); 
				$(item.element).remove();	
			}
				
			// Save list
			if (extern.formulas.length == 0) {
				// Empty list, so add default formula
				addFormula();
			} else {
				saveFormulaList();
				
				if ($(".last-formulas-list .ui-selected").size() == 0) {
					// Select new item at removed index or the last index
					selectItem(Math.min(index, extern.formulas.length -1));
				}
			}	
		}
		$("#mc-remove-selected-button").hide();
	}
	
	/* Resize */
	function registerResize() {
		// Register resize callback
		$(window).unbind('resize').resize(resizeCodeMirror);		
		
		// Call resize function
		resizeCodeMirror();
	}
	
	/* CodeMirror */
	function registerCodeMirror() {
		var options = {
			parseOnChange : true,
			showPatternMarkers : true,
			highlightOperands : true,
			showHints : true
		};
		LtlEditor.setCodeMirror(document.getElementById("mc-formula-code"), options);
		LtlEditor.parseListeners = [parseListener];
	}
	
	function parseListener() {
		$('#mc-code-error').css({display: (LtlEditor.lastParseOk ? "none" : "inline")});
		var value = LtlEditor.cm.getValue();
		updateItem(value);
	}
	
	function resizeCodeMirror() {
		var height = $('.mc-content').height() - $("#current-formulas-panel").height();
		var offset = $("#current-formulas-panel").offset();
		if (offset) {
			height -= (offset.top - 15);
			
			LtlEditor.cm.setSize(null, Math.max(150, height));	
			LtlEditor.cm.refresh();
		}
	}
	
	return extern;
}())