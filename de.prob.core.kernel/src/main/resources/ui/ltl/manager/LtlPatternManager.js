LtlPatternManager = (function() {
	var extern = {};
	var pages = {
		"default": 		"/ui/ltl/manager/default.html",
		"show": 		"/ui/ltl/manager/show_pattern.html",
		"show_list": 	"/ui/ltl/manager/show_pattern_list.html",
		"create": 		"/ui/ltl/manager/edit_pattern.html",
		"edit": 		"/ui/ltl/manager/edit_pattern.html"
	};
	extern.patterns = [];
	extern.ignorePatternName = null;
	extern.page = "default";
	extern.parseListeners = null;
	extern.movePattern = null;
	
	/* Create and destroy pattern manager */
	extern.create = function() {
		// Get pattern list 
		Util.getPatternList();
		
		// Register selection listener to pattern list
		$("#selectable").selectable({
			filter: ".pattern-list-item",
			unselected: selected,
			selected: selected			
		});
		
		registerCreateButton($('#add-pattern'));
	}
	
	extern.destroy = function() {
		$(window).unbind('resize');
	}
	
	/* Restore state */	
	extern.saveState = function() {
		extern.parseListeners = LtlEditor.parseListeners;
	}
	
	extern.restore = function() {		
		if (extern.movePattern != null) {
			showEditView();
			
			$('#name-input').val(extern.movePattern.name);
			$('#description-input').val(extern.movePattern.description);
			LtlEditor.cm.setValue(extern.movePattern.code);
			
			extern.movePattern = null;
		} else if (extern.page == "create" || extern.page == "edit") {
			extern.setCodeMirror(document.getElementById("code"), extern.ignorePatternName);
			
			LtlEditor.parseListeners = extern.parseListeners;
		}
	}
	
	/* Register buttons */
	function registerCreateButton(element) {
		element.click(function() {
			showEditView();
		});
	}
	
	function registerEditButton(element, pattern) {
		element.click(function() {
			showEditView(pattern);
		});
	}
	
	function registerRemoveButton(element, patterns) {
		element.click(function() {
			var names = [];
			for (var i = 0; i < patterns.length; i++) {
				names.push(patterns[i].name);
			}
			Util.removePatterns(names);
		});
	}
	
	function registerSaveButton(element, oldPatternName) {
		if (oldPatternName) {
			element.click(function() {
				updatePattern(oldPatternName, collectInputData());
			});
		} else {
			element.click(function() {
				savePattern(collectInputData());
			});
		}
	}
	
	/* Show content pages */	
	function showPage(page, context) {
		context = typeof context === 'undefined' ? {} : context;
		extern.page = page;
		Util.replaceContent(".manager-content", pages[page], context);
	}
	
	function showPattern(index) {
		var pattern = extern.patterns[index];
		
		$(window).unbind('resize');
		showPage("show", pattern);
		
		registerEditButton($('#edit-pattern'), pattern);
		registerRemoveButton($('#remove-pattern'), [pattern]);
	}
	
	function showPatternList() {
		var patterns = [];
		$('#user-patterns .ui-selected').each(function(i, element) {
			var index = $("#selectable .pattern-list-item").index(element);
			patterns.push(extern.patterns[index]);
		});
		
		var builtins = [];
		$('#builtin-patterns .ui-selected').each(function(i, element) {
			var index = $("#selectable .pattern-list-item").index(element);
			builtins.push(extern.patterns[index]);
		});
		
		$(window).unbind('resize');
		showPage("show_list", { patterns: patterns, builtins: builtins });
		
		registerRemoveButton($('#remove-pattern'), patterns);
	}
		
	function showEditView(pattern) {
		pattern = typeof pattern === 'undefined' ? {} : pattern;
		$(window).unbind('resize');		
		showPage((pattern.name ? "edit" : "create"), pattern);
		
		registerSaveButton($('#save-pattern'), pattern.name);
		registerRemoveButton($('#remove-pattern'), [pattern]);
		
		// Register input checks
		$('#name-input').keyup(function() {
			checkInput(pattern.name || null, collectInputData());
		});
		LtlEditor.parseListeners = [function() {
			checkInput(pattern.name || null, collectInputData());
		}];
	}	
	
	/* Pattern list */	
	extern.setPatternList = function(data) {		
		setPatternList(JSON.parse(data.patterns));
	}
	
	function setPatternList(patterns) {
		var user = $('#user-patterns');
		var builtins = $('#builtin-patterns');
		user.empty();
		builtins.empty();
		
		extern.patterns = patterns;
		for (var i = 0; i < extern.patterns.length; i++) {
			var pattern = extern.patterns[i];
			var element = document.createElement("li");
			element.className = "pattern-list-item";
			element.appendChild(document.createTextNode(pattern.name));
			if (pattern.builtin) {
				builtins.append(element);
			} else {
				user.append(element);
			}
		}
	}
	
	function selected(event, ui) {
		var count = $('#selectable .ui-selected').size();
		if (count == 1) {
			var index = $("#selectable .pattern-list-item").index($('#selectable .ui-selected'));
			showPattern(index);
		} else if (count == 0) {
			if ($('#selectable .ui-selecting').size() == 0) {
				$(window).unbind('resize');
				$(".manager-content").empty();
			}
		} else {
			showPatternList();
		}
	}
	
	/* Input validation */
	function collectInputData() {
		var pattern = {
			name: $('#name-input').val().trim(),
			description: $('#description-input').val().trim(),
			code: $.trim(LtlEditor.cm.getValue())
		};		
		
		return pattern;
	}
	
	function checkInput(oldName, pattern) {
		// Check empty name
		var emptyName = !pattern.name;
		// Check unique name
		var uniqueName = (pattern.name == oldName || findPattern(pattern.name) == -1);
		// Check errors in code
		var codeErrors = !LtlEditor.lastParseOk;
		
		// Show/hide badges
		$('#name-empty-error').css({display: (emptyName ? "inline" : "none")});
		$('#name-unique-error').css({display: (uniqueName ? "none" : "inline")});
		$('#code-error').css({display: (codeErrors ? "inline" : "none")});
		
		return !emptyName && uniqueName && !codeErrors;
	}
	
	function findPattern(name) {
		for (var i = 0; i < extern.patterns.length; i++) {
			if (extern.patterns[i].name == name) {
				return i;
			}
		}
		return -1;
	}
	
	/* Saving, updating and removing */
	function savePattern(pattern) {
		if (checkInput(null, pattern)) {
			Util.savePattern(pattern);
		} else {
			$("#error-alert").fadeIn("fast", function() {
				$("#error-alert").delay(2000).fadeOut("slow");
			});
		}
	}
	
	function updatePattern(oldName, pattern) {
		if (checkInput(oldName, pattern)) {
			Util.updatePattern(oldName, pattern);
		} else {
			$("#error-alert").fadeIn("fast", function() {
				$("#error-alert").delay(2000).fadeOut("slow");
			});
		}
	}
	
	extern.saveSuccess = function(data) {
		var pattern = JSON.parse(data.pattern);
		extern.patterns.push(pattern);
		
		showEditView(pattern);
		$('#selectable .ui-selected').each(function(i, element) {
			$(element).removeClass('ui-selected');
		});
		
		var element = document.createElement("li");
		element.className = "pattern-list-item ui-selected";
		element.appendChild(document.createTextNode(pattern.name));
		
		var user = $('#user-patterns');
		user.append(element);
		
		$("#success-alert").fadeIn("fast", function() {
			$("#success-alert").delay(2000).fadeOut("slow");
		});
	}
	
	extern.updateSuccess = function(data) {
		var pattern = JSON.parse(data.pattern);
		
		var oldPatternName = data.oldPatternName;
		var index = findPattern(oldPatternName);
		extern.patterns[index] = pattern;
		
		var element = $("#selectable .pattern-list-item")[index];
		element.innerHTML = pattern.name;
		
		showEditView(pattern);
	
		$("#success-alert").fadeIn("fast", function() {
			$("#success-alert").delay(2000).fadeOut("slow");
		});
	}
	
	extern.removeSuccess = function(data) {
		var names = JSON.parse(data.names);
		
		$('#removeModal').on('hidden.bs.modal', function () {
			$(window).unbind('resize');
			showPage("default", { multiple: (names.length > 1) });	
		
			$("#success-alert").fadeIn("fast", function() {
				$("#success-alert").delay(2000).fadeOut("slow");
			});
		});

		$('#removeModal').modal('hide');
		
		for (var i = 0; i < names.length; i++) {
			var index = findPattern(names[i]);
			extern.patterns.splice(index, 1);
		
			var element = $("#selectable .pattern-list-item")[index];
			element.remove();
		}
		$('#selectable .ui-selected').each(function(i, element) {
			$(element).removeClass('ui-selected');
		});
	}
	
	/* Code mirror */
	extern.setCodeMirror = function(codeElement, ignorePatternName) {
		extern.ignorePatternName = ignorePatternName;
		var options = {
			parseOnChange : true,
			showPatternMarkers : false,
			highlightOperands : true,
			showHints : true,
			mode : "parsePattern",
			ignorePatternName : ignorePatternName
		};
		LtlEditor.setCodeMirror(codeElement, options);	
		extern.registerResize(LtlEditor.cm);
	}
	
	extern.registerResize = function(cm) {
		// Register resize callback
		$(window).unbind('resize').resize(function() {
			var height = $('.manager-content').height();
			var offset = $('#code-panel').offset();
			if (offset) {
				height -= (offset.top - 15);
				
				cm.setSize(null, Math.max(200, height));	
				cm.refresh();
			}
		});		
		
		// Call resize function
		$(window).trigger('resize');
	}
	
	return extern;
}())