Util = (function() {
	var extern = {};
	
	/* Init */
	extern.init = function(client) {
		extern.client = client;
		extern.session = Session();
		extern.session.init(client);
	}
	
	/* Parsing */
	extern.parseFormula = function(input, callbackObj) {
		callbackObj = typeof callbackObj === 'undefined' ? "LtlEditor" : callbackObj;
		extern.session.sendCmd("parseFormula", {
			"input" : input,
			"callbackObj" : callbackObj,
			"client" : extern.client
		});
	}
	
	extern.parsePattern = function(input, ignorePatternName, callbackObj) {
		callbackObj = typeof callbackObj === 'undefined' ? "LtlEditor" : callbackObj;
		extern.session.sendCmd("parsePattern", {
			"input" : input,
			"ignorePatternName": ignorePatternName,
			"callbackObj" : callbackObj,
			"client" : extern.client
		});
	}
	
	/* Search operator */
	extern.getOperatorAtPosition = function(pos, callbackObj) {
		callbackObj = typeof callbackObj === 'undefined' ? "LtlEditor" : callbackObj;
		extern.session.sendCmd("getOperatorAtPosition", {
			"pos" : pos,
			"callbackObj" : callbackObj,
			"client" : extern.client
		});
	}
	
	/* Autocomplete list */
	extern.getAutoCompleteList = function(line, ch, startsWith, input, callbackObj) {
		callbackObj = typeof callbackObj === 'undefined' ? "LtlEditor" : callbackObj;
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
	extern.getPatternList = function(callbackObj) {
		callbackObj = typeof callbackObj === 'undefined' ? "LtlEditor" : callbackObj;
		extern.session.sendCmd("getPatternList", {
			"callbackObj" : callbackObj,
			"client" : extern.client
		});
	}
	
	/* Save, update, remove user defined pattern */
	extern.savePattern = function(pattern, callbackObj) {
		callbackObj = typeof callbackObj === 'undefined' ? "LtlPatternManager" : callbackObj;
		extern.session.sendCmd("savePattern", {
			"name" : pattern.name,
			"description" : pattern.description,
			"code" : pattern.code,
			"callbackObj" : callbackObj,
			"client" : extern.client
		});
	}
	
	extern.updatePattern = function(oldPatternName, pattern, callbackObj) {
		callbackObj = typeof callbackObj === 'undefined' ? "LtlPatternManager" : callbackObj;
		extern.session.sendCmd("updatePattern", {
			"oldPatternName" : oldPatternName,
			"name" : pattern.name,
			"description" : pattern.description,
			"code" : pattern.code,
			"callbackObj" : callbackObj,
			"client" : extern.client
		});
	}
	
	extern.removePatterns = function(patternNames, callbackObj) {
		callbackObj = typeof callbackObj === 'undefined' ? "LtlPatternManager" : callbackObj;
		extern.session.sendCmd("removePatterns", {
			"names" : JSON.stringify(patternNames),
			"callbackObj" : callbackObj,
			"client" : extern.client
		});
	}
	
	/* Get and save formula list*/
	extern.getFormulaList = function(callbackObj) {
		callbackObj = typeof callbackObj === 'undefined' ? "LtlModelCheck" : callbackObj;
		extern.session.sendCmd("getFormulaList", {
			"callbackObj" : callbackObj,
			"client" : extern.client
		});
	}
	
	extern.saveFormulaList = function(formulas, callbackObj) {
		callbackObj = typeof callbackObj === 'undefined' ? "LtlModelCheck" : callbackObj;
		extern.session.sendCmd("saveFormulaList", {
			"formulas" : JSON.stringify(formulas),
			"callbackObj" : callbackObj,
			"client" : extern.client
		});
	}
	
	/* Model checking */
	extern.checkFormula = function(index, mode, formula, callbackObj) {
		callbackObj = typeof callbackObj === 'undefined' ? "LtlModelCheck" : callbackObj;
		extern.session.sendCmd("checkFormula", {
			"formula" : formula,
			"startMode" : mode,
			"index" : index,
			"callbackObj" : callbackObj,
			"client" : extern.client
		});
	}
	
	extern.checkFormulaList = function(indizes, mode, formulas, callbackObj) {
		callbackObj = typeof callbackObj === 'undefined' ? "LtlModelCheck" : callbackObj;
		extern.session.sendCmd("checkFormulaList", {
			"formulas" : JSON.stringify(formulas),
			"indizes" : JSON.stringify(indizes),
			"startMode" : mode,
			"callbackObj" : callbackObj,
			"client" : extern.client
		});
	}
	
	/* Rendering */
	extern.replaceContent = function(container, template_name, context) {
		context = typeof context === 'undefined' ? {} : context;
		var content = extern.session.render(template_name, context);
		$(container).empty().append(content);	
	}	
	
	return extern;
}())