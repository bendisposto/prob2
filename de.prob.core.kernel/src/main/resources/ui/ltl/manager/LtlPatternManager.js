LtlPatternManager = (function() {
	var extern = {};
	var session = Session();

	$(document).ready(function() {
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
	}
	
	extern.showDefaultPage = function(index) {
		$(".content").empty();	
	}
	
	extern.showPatternList = function() {
		var patterns = [];
		
		$('.ui-selected').each(function(i, element) {
			var index = $("#selectable .pattern-list-item").index(element);
			patterns.push(extern.patterns[index]);
		});
		
		var content = session.render("/ui/ltl/manager/show_pattern_list.html", { patterns: patterns });
		$(".content").empty().append(content);	
	}
	
	/* Show create and edit*/	
	function showEditView(pattern) {
		var content = session.render("/ui/ltl/manager/edit_pattern.html", pattern);
		$(".content").empty().append(content);
		
		// Register save button
		$('#save-pattern').click(function() {
			// TODO save
			$("#success-alert").fadeIn("fast", function() {
				$("#success-alert").delay(2000).fadeOut("slow");
			});
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
	extern.changeCodeElement = function(codeElement) {
		LtlEditor.changeCM(codeElement);		
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
	
	/* Init */
	extern.init = function(client) {
		extern.client = client;
		session.init(client);	
		
		var options = {
			parseOnChange : true,
			showPatternMarkers : true,
			highlightOperands : true,
			showHints : true
		};
		LtlEditor.init(client, options);		
	}
	extern.client = null;
	extern.patterns = [];
	extern.builtins = [];
	
	return extern;
}())