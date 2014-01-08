LtlFormula = (function() {
	var extern = {}
	var session = Session();

	//$(document).ready(function() {
	//});
	
    $(document).ready(function() {
        $(window).keydown(function(event){
            if(event.keyCode == 13) {
                event.preventDefault();
                return false;
            }
        });
    });
    
    $(".add-formula").click(function(e) {
    	formula = $("input")[0].value
    	$("input")[0].value = ""
        session.sendCmd("addFormula", {
        	"val": formula
        });
    });

	function setContent(formulargs) {
		formulas = JSON.parse(formulargs)
		$(".formula").remove()
		for (formula in formulas) {
			var co = formulas[formula]
						
			$("#content").append(
					session.render("/ui/ltlFormula/formula_table.html", co))
		}

		$(".status-click").click(function(e) {
			clickFormula(e.target.id)
		})
		$(".close-button").click(function(e) {
			removeFormula(e.target.id)
		})
		$(".edit-button").click(function(e) {
			removeFormula(e.target.id)
			$("input")[0].value = e.target.parentNode.parentNode.childNodes[1].firstChild.wholeText
		})
	}

	function clickFormula(id) {
		session.sendCmd("checkNthFormula", {
			"pos" : id,
			"client" : extern.client
		})
	}
	
	function removeFormula(id) {
		session.sendCmd("removeFormula", {
			"pos" : id,
			"client" : extern.client
		})
	}

    function disable() {
        $("body").append("<div class='modal-backdrop disabled'></div>")
    }

    function enable() {
        $(".disabled").remove()
    }

	extern.setFormulas = function(data) {
		setContent(data.formulas)
	}
	extern.client = ""
	extern.init = session.init
    extern.disable = disable
    extern.enable = enable

	return extern;
}())