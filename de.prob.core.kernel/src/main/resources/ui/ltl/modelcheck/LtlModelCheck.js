LtlModelCheck = (function() {
	var extern = {};
	var session = null;

	$(document).ready(function() {
		
	});	
	
	/* Init */
	extern.init = function(client) {
		extern.client = client;
		LtlPatternManager.init(client);	
		session = LtlPatternManager.session;
		extern.session = session;
		
	}
	extern.client = null;
	extern.session = null;
	
	return extern;
}())