LtlModelCheck = (function() {
	var extern = {};
	
	/* Restore state */
	extern.saveState = function() {
	
	}
	
	extern.restore = function() {
		var options = {
			parseOnChange : true,
			showPatternMarkers : true,
			highlightOperands : true,
			showHints : true
		};
		LtlEditor.setCodeMirror(document.getElementById("mc-formula-code"), options);
	}
	
	return extern;
}())