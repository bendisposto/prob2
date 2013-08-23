LtlPatternManager = (function() {
	var extern = {};
	var session = Session();

	$(document).ready(function() {
		extern.getPatternList();
	});	
	
	extern.getPatternList = function() {
		session.sendCmd("getPatternList", {
			"client" : extern.client
		});
	}
	
	extern.setPatternList = function(data) {
		var patterns = JSON.parse(data.patterns);
		var builtins = JSON.parse(data.builtins);
		
		var list = $('#user-patterns');
		list.empty();
		for (var i = 0; i < patterns.length; i++) {
			var pattern = patterns[i];
			var element = document.createElement("li");
			element.className = "pattern-list-item";
			element.appendChild(document.createTextNode(pattern.name));
			list.appendChild(element);
		}
		list = $('#builtin-patterns');
		list.empty();
		for (var i = 0; i < builtins.length; i++) {
			var pattern = builtins[i];
			var element = document.createElement("li");
			element.className = "pattern-list-item";
			element.appendChild(document.createTextNode(pattern.name));
			list.append(element);
		}
	}
	
	extern.init = session.init;
	extern.client = null;
	
	return extern;
}())