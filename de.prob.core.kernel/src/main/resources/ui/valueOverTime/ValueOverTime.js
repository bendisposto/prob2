ValueOverTime = (function() {

    var extern = {}
    var session = Session();
    var svg = VizUtils.createCanvas("#visualization",
            $("#left-col")[0].clientWidth, VizUtils.calculateHeight());

    $(document).ready(function() {
        $(window).keydown(function(event){
            if(event.keyCode == 13) {
                event.preventDefault();
                return false;
            }
        });
    });

    $(".form-control").keyup(function(e) {
        session.sendCmd("parse", {
            "formula" : e.target.value,
            "id" : e.target.id
        })
    });

    $(".add_expr").click(function(e) {
        e.preventDefault();
        session.sendCmd("addFormula", {
            "time" : $("#inputTime").val(),
            "timeId" : "inputTime",
            "formula" : $("#inputFormula1").val(),
            "formulaId" : "inputFormula1",
            "client" : extern.client
        })
    })

    function draw(whatever) {
        // TO IMPLEMENT
    }

    function clearCanvas() {
        svg.selectAll(".axis").remove();
        svg.selectAll(".connection").remove();
        svg.selectAll(".key").remove();
        svg.selectAll(".button").remove();
    }

    function parseOk(id) {
        $("#" + id).parent().removeClass("has-error")
    }

    function parseError(id) {
        $("#" + id).parent().addClass("has-error")
    }

    function formulasAdded() {
        $(".alert").remove();
        $(".form-group").removeClass("has-error");
    }

    function hasFormulaErrors(ids) {
        $(".alert").remove();
        $("form").prepend(session.render("/ui/valueOverTime/error_msg.html",{}));
        for (var i = 0; i < ids.length; i++) {
            $("#" + ids[i]).parent().addClass("has-error");
        };
    }

    extern.draw = function(data) {
        draw(data.whatever);
    }
    extern.client = ""
    extern.init = session.init
    extern.parseError = function(data) {
        parseError(data.id);           
    }
    extern.parseOk = function(data) {
        parseOk(data.id);
    }
    extern.formulasAdded = formulasAdded;
    extern.hasFormulaErrors = function(data) {
        hasFormulaErrors(JSON.parse(data.ids));
    }
    extern.session = session;

    return extern;
}())