LtlPatternManager = (function() {
	var extern = {};
	var session = Session();

	$(document).ready(function() {
		// Get pattern list 
		extern.getPatternList();
		
		// Register selection listener to pattern list
		$("#selectable").selectable({
			filter: ".pattern-list-item",
			selected: function( event, ui ) {
				var count = countSelection();
				if (count == 1) {
					var index = $( "#selectable .pattern-list-item" ).index( ui.selected );
					extern.showPattern(index);
				} else {
					// TODO
				}
			}	,
			stop: function( event, ui ) {
				var count = countSelection();
				if (count > 1) {
					alert("remove");
					// TODO
				}
			}			
		});
	});	
	
	/* Pattern selection */
	extern.showPattern = function(index) {
		var pattern = extern.patterns[index];
		$('#name').text(pattern.name);
		$('.description').val(pattern.description);
	}
		
	function countSelection() {
		var count = $('.ui-selected').size();
		return count + $('.ui-selecting').size();
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
	
	/* Init */
	extern.init = function(client, codeElement) {
		extern.client = client;
		session.init(client);
		
		// ltl editor
		var settings = {
			lineNumbers: true,
			matchBrackets: true,
			autoCloseBrackets: true,
			extraKeys: {"Ctrl-Space": "autocomplete"},
			gutters: ["CodeMirror-linenumbers", "markers"]
		};
		extern.cm = CodeMirror.fromTextArea(codeElement, settings);
		LtlEditor.init(client, extern.cm);
		
		// Register resize callback
		$(window).resize(function() {
			var height = $('.content').height();
			var offset = $('#code-panel').offset();
			height -= (offset.top + 35);
			
			extern.cm.setSize(null, Math.max(200, height));	
		});		
	}
	extern.client = null;
	extern.cm = null;
	extern.patterns = [];
	extern.builtins = [];
	
	return extern;
}())