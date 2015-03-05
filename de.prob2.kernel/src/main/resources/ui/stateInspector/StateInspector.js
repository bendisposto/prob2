StateInspector = (function() {
    var extern = {}
    var session = Session();
    var cm = null;
    ;
    var hist = [];
    var hp = null;
    var ctr = 0;

    var nrOfInterest = {}

    var editorkeys = function() {
        return {
            'Shift-Enter' : function(cm) {
                cm.replaceSelection("\n", "end", "+input");
                cm.indentLine(cm.getCursor().line, null, true);
                return true;
            },
            'Enter' : function(cm) {
                var code = cm.getValue();
//                console.log("submit: '" + code + "'")
                session.sendCmd("evaluate", {
                    "code" : code
                })
                this.hist.push(code)
                this.hp = this.hist.length - 1;
                cm.setValue("")
                return false;
            },
            'Up' : function(cm) {
                if (cm.getCursor().line == 0) {
//                    console.log("History up")
                    if (this.hp == null) {
                        this.hp = this.hist.length;
                    }
                    if (this.hp > 0) {
                        this.hp--
                        cm.setValue(this.hist[this.hp])  
                    }
                } else
                    this.hp = null;
                return CodeMirror.Pass;
                ;
            },
            'Down' : function(cm) {
                var cnt = cm.doc.lineCount();
                if (cm.getCursor().line == cnt - 1) {
//                    console.log("History down")
                    if (this.hp != null) {
                        if (this.hp < this.hist.length - 1) {
                            this.hp++
                            cm.setValue(this.hist[this.hp])
                        } else {
                            cm.setValue("")
                            this.hp = null
                        }

                    }
                } else
                    return CodeMirror.Pass;
                ;
            }
        }
    };

    $(document).ready(function() {
    });

    function clearInput() {
        $("#content").replaceWith("<tbody id='content'></tbody>");
        clearResults()
        $(".CodeMirror").addClass("_empty")
    }

    function extractOfInterest(data) {
        if(data.formulaId) {
            return
        }
        nrOfInterest[data.path.join("_")+"_"] = data.nrOfInterest
        for (var i = 0; i < data.children.length; i++) {
            extractOfInterest(data.children[i])
        };
    }

    function setModel(model) {
        $("#table").colResizable({disable: true})
        $("#content").html(
                session.render("/ui/stateInspector/model_format.html", {components: model}))
        $("#model-select").replaceWith(
                session.render("/ui/stateInspector/model_select.html", {components: model}))
        $("#table").colResizable()
        for (var i = 0; i < model.length; i++) {
            extractOfInterest(model[i])
        };
        $("input").click(clickFunction)
        $(".CodeMirror").removeClass("_empty")
    }

    function clickFunction(e) {
        var path = []
        var componentS, headingS, formulaS
        var classList = $(e.target).attr('class').split(' ').filter(function(e) { return e !== ""})
        for (var i = 0; i < classList.length; i++) {
            path.push(classList[i])
            if(i === 0) {
                componentS = path[0] + "_"
            } else if(i === 1) {
                headingS = path.join("_") + "_"
            }
        };
        if(path.length === 5) {
            headingS = path[0] + "_guards_"
        }
        formulaS = path.join(".")

        if($(this).is(":checked")) {
            $("tr#"+componentS).removeClass("_empty")
            $("tr#"+headingS).removeClass("_empty")
            $("tr."+formulaS).removeClass("_empty")
            nrOfInterest[componentS]++
            nrOfInterest[headingS]++
            session.sendCmd("registerFormula", {"path": path})
        } else {
            nrOfInterest[componentS]--
            nrOfInterest[headingS]--
            if(nrOfInterest[componentS] === 0) {
                $("tr#"+componentS).addClass("_empty")
            }
            if(nrOfInterest[headingS] === 0) {
                $("tr#"+headingS).addClass("_empty")
            }
            $("tr."+formulaS).addClass("_empty")
            session.sendCmd("deregisterFormula", {"path": path})
        }
    }

    function updateValues(values) {
        var id, curr_id, prev_id;
        for ( var i = 0; i < values.length; i++) {
            id = "#" + values[i].id;
            $(id + "_current").html(values[i].current);
            $(id + "_previous").html(values[i].previous);
            $(id).removeClass("changed")
            if (values[i].current !== values[i].previous) {
                $(id).addClass("changed")
            }
            updateClasses(id + "_current", values[i].current.toLowerCase())
            updateClasses(id + "_previous", values[i].previous.toLowerCase())
        }
    }

    function updateClasses(id, value) {
        $(id).removeClass("false")
        $(id).removeClass("true")
        $(id).removeClass("not-well-defined")
        $(id).removeClass("enum-warning")

        value = value === "?(&infin;)" ? "enum-warning" : value
        if (value === "false" || value === "true" || 
            value === "not-well-defined" || value === "enum-warning") {
            $(id).addClass(value)            
        }
    }

    function updateHistory(history) {
        this.hist = history
        this.hp = this.hist.length
    }

    function showresult(data) {
        var id = "_answer_" + ctr++
        data.id = id
        var output = session.render("/ui/stateInspector/shell_answer.html", data)
        $(".outbox").append(output)
        $("#"+id).click(function(e) {
            $(this).remove()
        })
    }

    function init() {
        cm = CodeMirror.fromTextArea($('#input')[0], {
            mode : 'b',
            lineNumbers : false,
            lineWrapping : true,
            theme : "default",
            viewportMargin : Infinity
        });

        cm.addKeyMap(editorkeys());

        $(".CodeMirror-hscrollbar").remove(); // Hack! no horizontal scrolling
        $(".CodeMirror-vscrollbar").remove(); // Hack! no vertical scrolling
        $(".CodeMirror-scrollbar-filler").remove(); // Hack! no funny white
        // square in bottom right
        // corner

        $("#edit").click(function(e) {
            $("#edit-screen").modal('show')
        })

        $(".CodeMirror").addClass("_empty")
    }

    function disable() {
        $("body").append("<div class='modal-backdrop transparent'></div>")
    }

    function enable() {
        $(".transparent").remove()
    }

    function clearResults() {
        $(".outbox").empty()
        cm.getDoc().setValue('')
    }

    extern.setModel = function(data) {
        setModel(JSON.parse(data.components))
        updateValues(JSON.parse(data.values))
        clearResults()
        updateHistory(JSON.parse(data.history))
    }
    extern.updateValues = function(data) {
        updateValues(JSON.parse(data.values))
    }
    extern.clearInput = clearInput;

    extern.client = ""
    extern.init = function() {
        session.init()
        init()
    }
    extern.result = showresult
    extern.cm = function() {
        return cm;
    }
    extern.disable = disable
    extern.enable = enable

    return extern;
}())