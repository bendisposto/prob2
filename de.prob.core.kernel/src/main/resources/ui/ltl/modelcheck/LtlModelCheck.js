LtlModelCheck = (function() {
	var extern = {};
	extern.formulas = [];
	
	/* Create and destroy */
	extern.destroy = function() {
		$(window).unbind('resize');
	}
	
	/* Restore state */
	extern.saveState = function() {
	
	}
	
	extern.restore = function() {
		registerCodeMirror();
		registerResize();
		
		// Register toggle of formula list
		$("#last-formulas-panel").click(function(event) {
			if ($(event.target).is("div")) {
				var icon = $('#last-formulas-icon');
				if (icon.hasClass('glyphicon-chevron-right')) {
					icon.removeClass('glyphicon-chevron-right');
					icon.addClass('glyphicon-chevron-down');
				} else {
					icon.addClass('glyphicon-chevron-right');
					icon.removeClass('glyphicon-chevron-down');
				}
				$("#mc-remove-selected").toggle();
				LtlModelCheck.toggleSavedFormulasList();
			}
		});
		
		// Register add formula
		$("#mc-add-formula").click(function() {
			addFormula();
		});
		
		// Register remove formula
		$("#mc-remove-current").click(function() {
			removeFormula();
		});
		$("#mc-remove-selected").click(function() {
			removeSelected();
		});
		
		if (extern.formulas == null) {
			Util.getFormulaList();
		}
		setFormulaList(extern.formulas);
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
		if (extern.formulas.length == 0) {
			extern.formulas.push("// Enter a formula");
		}
		for (var i = 0; i < extern.formulas.length; i++) {
			var formula = extern.formulas[i];
			list.append($("<li><input type=\"checkbox\">" + formula + "</li>"));
		}
		
		registerSelection();
		// Select last element
		var index = extern.formulas.length - 1;
		var element = $(".last-formulas-list li")[index];
		selectionChanged($(element), index);
	}
	
	function selectionChanged(element, index) {
		$(element).addClass("ui-selected").siblings().removeClass("ui-selected");
		var formula = extern.formulas[index];
		
		LtlEditor.cm.setValue(formula);
	}
	
	function registerSelection() {
		// Register selection listener to pattern list
		$(".last-formulas-list li").click(function(event) {
			if ($(event.target).is("li") && !$(this).hasClass("ui-selected")) {
				var index = $(".last-formulas-list li").index($(this));
				selectionChanged($(this), index);
			}
		});
	}
	
	/* Add and remove formula */
	function addFormula() {
		var formula = "// Enter a formula";
		extern.formulas.push(formula);
		$('.last-formulas-list').append($("<li><input type=\"checkbox\">" + formula + "</li>"));
		
		registerSelection();
		// Select last element
		var index = extern.formulas.length - 1;
		var element = $(".last-formulas-list li")[index];
		selectionChanged($(element), index);
	}
	
	function removeFormula() {
		var element = $(".last-formulas-list li ui-selected");
		var index = element.index()
		extern.formulas.splice(index, 1); 
		element.remove();
	}
	
	function removeSelected() {
		
	}
	
	/* Resize */
	function registerResize() {
		// Register resize callback
		$(window).unbind('resize').resize(resizeCodeMirror);		
		
		// Call resize function
		$(window).trigger('resize');
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
	
	/* CodeMirror */
	function registerCodeMirror() {
		var options = {
			parseOnChange : true,
			showPatternMarkers : true,
			highlightOperands : true,
			showHints : true
		};
		LtlEditor.setCodeMirror(document.getElementById("mc-formula-code"), options);
	}
	
	extern.toggleSavedFormulasList = function() {
		$(".last-formulas").toggle();
		resizeCodeMirror();
	}
	
	return extern;
}())