LtlPatternManager = (function() {
	var extern = {};
	var session = Session();

	$(document).ready(function() {
		extern.start();
	});	
	
	/* Pattern selection */
	function selected(event, ui) {
		var count = $('.ui-selected').size();
		if (count == 1) {
			var index = $("#selectable .pattern-list-item").index($('.ui-selected'));
			extern.showPattern(index);
		} else if (count == 0) {
			extern.showDefaultPage();
		} else {
			extern.showPatternList();
		}
	}
	
	extern.showPattern = function(index) {
		var pattern = extern.patterns[index];
		var content = session.render("/ui/ltl/manager/show_pattern.html", pattern);
		$(".content").empty().append(content);	
		
		// Register edit button
		$('#edit-pattern').click(function() {
			showEditView(pattern);
		});
		// Register remove button
		registerRemoveButton([pattern]);
	}
	
	extern.showDefaultPage = function(index) {
		$(".content").empty();	
	}
	
	extern.showPatternList = function() {
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
		
		var content = session.render("/ui/ltl/manager/show_pattern_list.html", { patterns: patterns, builtins: builtins });
		$(".content").empty().append(content);	
		
		// Register remove button
		registerRemoveButton(patterns);
	}
	
	/* Show create and edit*/	
	function showEditView(pattern) {
		var content = session.render("/ui/ltl/manager/edit_pattern.html", pattern);
		$(".content").empty().append(content);
		
		// Register save button
		$('#save-pattern').click(function() {
			if (pattern.name) {
				// Edit
				extern.updatePattern(pattern.name, collectInputData());
			} else {
				// Create
				extern.savePattern(collectInputData());
			}
		});
		
		// Register input checks
		$('#name-input').keyup(function() {
			checkInput(pattern.name || null, collectInputData());
		});
		LtlEditor.parseListeners = [function() {
			checkInput(pattern.name || null, collectInputData());
		}];
		
		if (pattern.name) {
			// Register remove button for edit page
			registerRemoveButton([pattern]);
		}
	}
	
	function registerRemoveButton(patterns) {
		// Register remove button
		$('#remove-pattern').click(function() {
			var names = [];
			for (var i = 0; i < patterns.length; i++) {
				names.push(patterns[i].name);
			}
			extern.removePatterns(names);
		});
	}
	
	/* Pattern list */
	extern.getPatternList = function() {
		session.sendCmd("getPatternList", {
			"client" : extern.client
		});
	}
	
	extern.setPatternList = function(data) {
		extern.patterns = JSON.parse(data.patterns);
		
		var user = $('#user-patterns');
		var builtins = $('#builtin-patterns');
		user.empty();
		builtins.empty();
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
	
	/* Code mirror */
	extern.changeCodeElement = function(codeElement, mode, ignorePattern) {
		LtlEditor.changeCM(codeElement, mode, ignorePattern);		
		extern.registerResize(LtlEditor.cm);
	}
	
	extern.registerResize = function(cm) {
		// Register resize callback
		$(window).unbind('resize').resize(function() {
			var height = $('.content').height();
			var offset = $('#code-panel').offset();
			height -= (offset.top + 35);
			
			cm.setSize(null, Math.max(200, height));	
			cm.refresh();
		});		
		
		// Call resize function
		$(window).trigger('resize');
	}
	
	/* Saving */
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
		var uniqueName = true;
		if (pattern.name != oldName) {
			for (var i = 0; i < extern.patterns.length && uniqueName; i++) {
				if (pattern.name == extern.patterns[i].name) {
					uniqueName = false;
				}
			}		
		}
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
	
	extern.savePattern = function(pattern) {
		if (checkInput(null, pattern)) {
			session.sendCmd("savePattern", {
				"name": pattern.name,
				"description": pattern.description,
				"code": pattern.code,
				"client" : extern.client
			});
		} else {
			$("#error-alert").fadeIn("fast", function() {
				$("#error-alert").delay(2000).fadeOut("slow");
			});
		}
	}
	
	extern.updatePattern = function(oldName, pattern) {
		if (checkInput(oldName, pattern)) {
			session.sendCmd("updatePattern", {
				"oldName": oldName,
				"name": pattern.name,
				"description": pattern.description,
				"code": pattern.code,
				"client" : extern.client
			});
		} else {
			$("#error-alert").fadeIn("fast", function() {
				$("#error-alert").delay(2000).fadeOut("slow");
			});
		}
	}
	
	extern.removePatterns = function(names) {
		session.sendCmd("removePatterns", {
			"names": JSON.stringify(names),
			"client" : extern.client
		});
	}
	
	extern.saveSuccess = function(data) {
		var pattern = JSON.parse(data.pattern);
		extern.patterns.push(pattern);
		
		showEditView(pattern);
		$('.ui-selected').each(function(i, element) {
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
			var content = session.render("/ui/ltl/manager/default.html", { multiple: (names.length > 1) });
			$(".content").empty().append(content);	
		
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
		$('.ui-selected').each(function(i, element) {
			$(element).removeClass('ui-selected');
		});
	}
	
	extern.start = function() {
		// Get pattern list 
		extern.getPatternList();
		
		// Register selection listener to pattern list
		$("#selectable").selectable({
			filter: ".pattern-list-item",
			unselected: selected,
			selected: selected			
		});
		
		// Register create button
		$('#add-pattern').click(function() {
			showEditView({});
		});
	}
	
	/* Init */
	extern.init = function(client) {
		extern.client = client;
		session.init(client);	
		extern.session = session;
		
		var options = {
			parseOnChange : true,
			showPatternMarkers : true,
			highlightOperands : true,
			showHints : true
		};
		LtlEditor.init(client, session, options);
	}
	extern.session = null;
	extern.client = null;
	extern.patterns = [];
	extern.builtins = [];
	
	return extern;
}())