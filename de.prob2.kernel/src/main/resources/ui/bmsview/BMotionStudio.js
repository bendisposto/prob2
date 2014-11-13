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

	extern.applyTransformers = function(data) {
		var d1 = JSON.parse(data.transformers)
		var i1 = 0
		for (; i1 < d1.length; i1++) {
			var t = d1[i1]
			if(t.selector) {
                var selector = $(t.selector)
                var content = t.content
                if(content != undefined) selector.html(content)
                selector.attr(t.attributes)
                selector.css(t.styles)
			}
		}
	}

    extern.callMethod = function(data) {
        var fErrorFn = function(data,status,er) { console.error("BMotion Studio: "+data+" status: "+status+" msg: "+er); }
        if(data.error === "undefined") {
            fErrorFn = data.error
        }
        var fSuccessFn = function(result) {}
        if(data.success !== "undefined") {
           fSuccessFn = data.success
        }
        delete data.success
        delete data.error
        data.mode = 'command'
        data.cmd = 'callGroovyMethod'
        $.ajax({
            async : true,
            cache: false,
            data : data,
            success : fSuccessFn,
            error : fErrorFn
        });
   	}
	
	return extern;

}())
