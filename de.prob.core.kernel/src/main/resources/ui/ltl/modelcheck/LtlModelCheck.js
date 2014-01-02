LtlModelCheck = (function() {
	var extern = {};
	extern.formulas = null;
	extern.mode = "init";
	
	/* Create and destroy */
	extern.destroy = function() {
		$(window).unbind('resize');
		
		$("#mc-add-formula").unbind('click');
		$("#mc-remove-current").unbind('click');
		$("#mc-remove-selected").unbind('click');
		$("#settings-selected").unbind('click');
		$("#last-formulas-panel").unbind('click');
	}
	
	/* Restore state */
	extern.saveState = function() {
		saveFormulaList();
		
		extern.destroy();
	}
	
	extern.restore = function() {		
		// Register buttons
		registerCheckCurrent();
		registerCheckSelected();
		registerAddFormula();
		registerRemoveFormula();
		registerRemoveSelectedFormulas();
		registerSettingSelected();
		
		// Register toggle of formula list
		registerFormulaListToggle();	

		// Register CodeMirror
		registerCodeMirror();
		
		// Get or restore formula list
		if (extern.formulas == null) {
			setFormulaList(["// The saved formula list was not loaded properly.\n"]);
			Util.getFormulaList();
		} else {
			setFormulaList(extern.formulas);
		}
		
		registerResize();
	}
	
	/* Register buttons */
	function registerCheckCurrent() {
		$("#mc-start-current").click(function() {
			checkFormula(getSelectedIndex());
		});
	}
	
	function registerCheckSelected() {
		$("#mc-start-all").click(function() {
			checkFormulaList();
		});
	}
	
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
					
					$(".last-formulas").show();
				} else {
					icon.addClass('glyphicon-chevron-right');
					icon.removeClass('glyphicon-chevron-down');
					
					$(".last-formulas").hide();
				}
				resizeCodeMirror();
			}
		});
	}
	
	function registerSettingSelected() {
		$("#settings-selected").click(function() {
			extern.mode = $("#startingPoint").val();
		});
	}
	
	/* Formula list */
	extern.setFormulaList = function(data) {
		var formulas = null;
		if (data.formulas != "") {
			formulas = JSON.parse(data.formulas);
		}
		setFormulaList(formulas);
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
		$('.last-formulas-list').append($("<li><input type=\"checkbox\">"
		+ "<div id=\"mc-check-failed\" class=\"glyphicon glyphicon-minus-sign\"></div>"
		+ "<div id=\"mc-check-passed\" class=\"glyphicon glyphicon-ok-sign\"></div>"
		+ "<div id=\"mc-check-unchecked\" class=\"glyphicon glyphicon-question-sign\"></div>"
		+ "<div id=\"mc-check-parse-failed\" class=\"badge mc-badge-error\">Code-Error</div>"
		+ "<span>" + formula + "</span></li>"));
	}
	
	function saveFormulaList() {
		if (extern.formulas != null) {
			Util.saveFormulaList(extern.formulas);
		}
	}
	
	extern.saveFormulaListSuccess = function(data) {
	
	}
	
	/* List selection */
	function selectItem(index, elt) {
		elt = typeof elt === 'undefined' ? null : elt;
		var element = elt || $(".last-formulas-list li")[index];
		$(element).addClass("ui-selected").siblings().removeClass("ui-selected");
		
		$('#mc-code-error').hide();
		LtlEditor.cm.setValue(extern.formulas[index]);
		
		// Scroll to let the element be visible
		scrollVisible(index, element);
	}
	
	function scrollVisible(index, element) {
		var list = $(".last-formulas-list");
		var visibleFrom = list.scrollTop();
		var visibleTo = visibleFrom + list.height();
		
		var itemHeight = $(element).height() + 1;
		var heightToBeVisible = index * itemHeight;
		if (visibleFrom > heightToBeVisible) {
			// scroll up
			var diff = heightToBeVisible - visibleFrom;
			list.scrollTop(visibleFrom + diff);
		} else if (visibleTo < heightToBeVisible + itemHeight) {
			// scroll down
			var diff = (heightToBeVisible + itemHeight) - visibleTo;
			list.scrollTop(visibleFrom + diff);
		}
	}
	
	function registerSelectionListener() {
		// Register selection listener to pattern list
		$(".last-formulas-list li").click(function(event) {
			if (!$(event.target).is("input") && !$(this).hasClass("ui-selected")) {
				var index = $(".last-formulas-list li").index($(this));
				selectItem(index, $(this));
				saveFormulaList();
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
			$("#mc-selected").hide();
		} else {
			$("#mc-selected").show();		
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
	
	function getSelectedIndex() {
		var element = $(".last-formulas-list .ui-selected");
		return element.index();
	}
	
	function updateItem(formula, parseOk) {
		var element = $(".last-formulas-list .ui-selected");
		var index = element.index();
		if (index >= 0) {
			extern.formulas[index] = formula;
			$(".last-formulas-list .ui-selected > span").text(formula);
			//$(".last-formulas-list .ui-selected > #mc-formula-error").css({display: (parseOk ? "none" : "inline")});
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
		checkboxSelectionChanged();
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
		checkboxSelectionChanged();
	}
	
	function removeSelected() {
		var selectedItems = getSelectedItems();
		if (selectedItems.length > 0) {
			var index = selectedItems[0].index;
		
			for (var i = selectedItems.length - 1; i >= 0; i--) {
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
		checkboxSelectionChanged();
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
			showPatternMarkers : moveToPatternManager,
			highlightOperands : true,
			showHints : true
		};
		LtlEditor.setCodeMirror(document.getElementById("mc-formula-code"), options);
		LtlEditor.parseListeners = [parseListener];
	}
	
	function moveToPatternManager(name, start, stop) {
		var from = {
			line: start.line - 1, 
			ch: start.pos
		};
		var to = {
			line: stop.line - 1, 
			ch: stop.pos + stop.length
		};	
		
		var code = LtlEditor.cm.getRange(from, to);
		LtlEditor.cm.replaceRange("", from, to);
		updateItem(LtlEditor.cm.getValue(), true);
		
		extern.movePattern({name: name, description: "", code: code});
		
	}
	
	function parseListener() {
		$('#mc-code-error').css({display: (LtlEditor.lastParseOk ? "none" : "inline")});
		var value = LtlEditor.cm.getValue();
		updateItem(value, LtlEditor.lastParseOk);
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
	
	/* Model checking */
	function checkFormula(index) {
		var element = $(".last-formulas-list li")[index];
		$(element).children("#mc-check-failed").hide();
		$(element).children("#mc-check-passed").hide();
		$(element).children("#mc-check-parse-failed").hide();
		$(element).children("#mc-check-unchecked").css({display: "inline"});
		Util.checkFormula(index, extern.mode, extern.formulas[index]);
	}
	
	function checkFormulaList() {
		var list = $(".last-formulas-list li");
		$(list).each(function(index, element) {
			$(element).children("#mc-check-failed").hide();
			$(element).children("#mc-check-passed").hide();
			$(element).children("#mc-check-parse-failed").hide();
			$(element).children("#mc-check-unchecked").css({display: "inline"});
		});
		
		var indizes = [];
		var formulas = [];
		var items = getSelectedItems();
		for (var i = 0; i < items.length; i++) {
			var index = items[i].index;
			indizes.push(index);
			formulas.push(extern.formulas[i]);
		}
		
		Util.checkFormulaList(indizes, extern.mode, formulas);
	}
	
	extern.checkFormulaFailed = function(data) {
		var index = data.index;
		var error = parseInt(data.error);
		
		var element = $(".last-formulas-list li")[index];
		$(element).children("#mc-check-failed").css({display: "inline"});
		$(element).children("#mc-check-passed").hide();
		$(element).children("#mc-check-unchecked").hide();
		if (error == 1 || error == 3) {
			$(element).children("#mc-check-parse-failed").css({display: "inline"});
		}
	}
	
	extern.checkFormulaPassed = function(data) {
		var index = data.index;	
		
		var element = $(".last-formulas-list li")[index];
		$(element).children("#mc-check-failed").hide();
		$(element).children("#mc-check-parse-failed").hide();
		$(element).children("#mc-check-passed").css({display: "inline"});	
		$(element).children("#mc-check-unchecked").hide();
	}
	
	extern.checkFormulaListFinished = function(data) {
		$("#selected-check-ready").css('display', 'inline').hide().fadeIn("fast", function() {
			$("#selected-check-ready").delay(2000).fadeOut("slow");
		});
	}
	
	extern.checkFormulaFinished = function(data) {
		$("#current-check-ready").css('display', 'inline').hide().fadeIn("fast", function() {
			$("#current-check-ready").delay(2000).fadeOut("slow");
		});
	}
	
	return extern;
}())