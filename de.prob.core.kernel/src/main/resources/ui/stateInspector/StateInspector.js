StateInspector = (function() {
    var extern = {}
    var session = Session();
    var testFormula = {
        components : [
            {
                label: "Scheduler",
                children: [
                    {
                        label: "Variables",
                        children: [
                            { code: "active", id: 1 },
                            { code: "waiting", id: 2 },
                            { code: "ready", id: 3 }
                        ]
                    },{
                        label: "Invariants",
                        children: [
                            { code: "active <: PID", id: 4 },
                            { code: "ready <: PID", id: 5 },
                            { code: "waiting <: PID", id: 6 },
                            { code: "(ready /\\ waiting) = {}", id: 7 },
                            { code: "active /\\ (ready \\/ waiting) = {}", id: 8 },
                            { code: "card(active) <= 1", id: 9 },
                            { code: "((active = {}) => (ready = {}))", id: 10 }
                        ]
                    }
                ]
            }
        ]
    }

    $(document).ready(function() {
    });

    function clearInput() {
        $("#content").replaceWith("<ul id='content'></ul>");
    }

    function setModel(model) {
    	$("#content").replaceWith(session.render("/ui/stateInspector/model_format.html",model))
    }

    function updateValues(values) {
        for (var i = 0; i < values.length; i++) {
            $("#"+values[i].id).replaceWith(session.render("/ui/stateInspector/entry_format.html",values[i]));
        };
    }

    extern.setModel = function(data) {
        setModel(JSON.parse(data.components));
        updateValues(JSON.parse(data.values));
    }
    extern.updateValues = function(data) {
        updateValues(JSON.parse(data.values));
    }
    extern.clearInput = clearInput;

    extern.client = ""
    extern.init = session.init

    return extern;
}())