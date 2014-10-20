Dotty = (function() {
	var extern = {}
	var session = Session()

	function drawDotty(content) {
        var m = Viz(content, "svg");
        try {	
            $("#viz").replaceWith(m);
			$("svg").attr("id","viz");
        } catch(e) {
            alert("Dotty graph not rendered. " + e.message)
        }
    }

	extern.draw = function(data) {
		drawDotty(data.content)
	}
	extern.client = ""
	extern.init = session.init

	return extern;
}())
