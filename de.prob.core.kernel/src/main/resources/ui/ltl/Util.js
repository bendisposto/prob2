Util = (function() {
	var extern = {};
	
	/* Init */
	extern.init = function(client) {
		extern.client = client;
		extern.session = Session();
		extern.session.init(client);
	}
	
	/* Parsing */
	extern.parseFormula = function(input, callbackObj = "LtlEditor") {
		extern.session.sendCmd("parseFormula", {
			"input" : input,
			"callbackObj" : callbackObj,
			"client" : extern.client
		});
	}
	
	extern.parsePattern = function(input, ignorePatternName, callbackObj = "LtlEditor") {
		extern.session.sendCmd("parsePattern", {
			"input" : input,
			"ignorePatternName": ignorePatternName,
			"callbackObj" : callbackObj,
			"client" : extern.client
		});
	}
	
	/* Search operator */
	extern.getOperatorAtPosition = function(pos, callbackObj = "LtlEditor") {
		extern.session.sendCmd("getOperatorAtPosition", {
			"pos" : pos,
			"callbackObj" : callbackObj,
			"client" : extern.client
		});
	}
	
	/* Autocomplete list */
	extern.getAutoCompleteList = function(line, ch, startsWith, input, callbackObj = "LtlEditor") {
		extern.session.sendCmd("getAutoCompleteList", {
			"line" : line,
			"ch" : ch,
			"startsWith" : startsWith,
			"input" : input,
			"callbackObj" : callbackObj,
			"client" : extern.client
		});
	}
	
	/* Get pattern list */
	extern.getPatternList = function(callbackObj = "LtlPatternManager") {
		extern.session.sendCmd("getPatternList", {
			"callbackObj" : callbackObj,
			"client" : extern.client
		});
	}
	
	/* Save, update, remove user defined pattern */
	extern.savePattern = function(pattern, callbackObj = "LtlPatternManager") {
		extern.session.sendCmd("savePattern", {
			"name" : pattern.name,
			"description" : pattern.description,
			"code" : pattern.code,
			"callbackObj" : callbackObj,
			"client" : extern.client
		});
	}
	
	extern.updatePattern = function(oldPatternName, pattern, callbackObj = "LtlPatternManager") {
		extern.session.sendCmd("updatePattern", {
			"oldPatternName" : oldPatternName,
			"name" : pattern.name,
			"description" : pattern.description,
			"code" : pattern.code,
			"callbackObj" : callbackObj,
			"client" : extern.client
		});
	}
	
	extern.removePatterns = function(patternNames, callbackObj = "LtlPatternManager") {
		extern.session.sendCmd("removePatterns", {
			"names" : JSON.stringify(patternNames),
			"callbackObj" : callbackObj,
			"client" : extern.client
		});
	}
	
	/* Rendering */
	extern.replaceContent = function(container, template_name, context = {}) {
		var content = extern.session.render(template_name, context);
		$(container).empty().append(content);	
	}	
	
	return extern;
}())