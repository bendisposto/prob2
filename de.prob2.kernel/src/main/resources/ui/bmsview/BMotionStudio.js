bms = (function() {

	var extern = {}
	var session = Session();

	extern.client = ""
	extern.observer = null;
	extern.init = session.init
	extern.session = session

	// The port and host fields are accessed from BMSStandalone.js
	extern.port = null
	extern.host = null
	extern.lang = null;

	extern.applyJavaScript = function(data) {
		vs = eval(data.values);
		for (e in vs) {
			v = vs[e];
			eval(v)
		}
	}
	
	extern.setObservers = function(data) {
		bmotion_om.core.setObservers(JSON.parse(data.observers))
	}
	
	extern.setHtml = function(data) {
		bmotion_om.core.setHtml(data.html)
	}
	
	return extern;

}())
