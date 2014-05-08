StateInspector = (function() {
    var extern = {}
    var session = Session();
    var cm = null;
    ;
    history = [];
    hp = null;
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
                this.hp = null;
                var code = cm.getValue();
//                console.log("submit: '" + code + "'")
                session.sendCmd("evaluate", {
                    "code" : code
                })
                this.history.push(code)
                cm.setValue("")
                return false;
            },
            'Up' : function(cm) {
                if (cm.getCursor().line == 0) {
//                    console.log("History up")
                    if (this.hp == null) {
                        this.hp = this.history.length;
                    }
                    if (this.hp > 0) {
                        this.hp--
                        cm.setValue(this.history[this.hp])
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
                        if (this.hp < this.history.length - 1) {
                            this.hp++
                            cm.setValue(this.history[this.hp])
                        } else {
                            cm.setValue("")
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
    }

    function clickFunction(e) {
        var path = []
        var componentS, headingS, formulaS
        for (var i = 0; i < this.classList.length; i++) {
            path.push(this.classList[i])
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
        for ( var i = 0; i < values.length; i++) {
            $("#" + values[i].id + "_current").html(values[i].current);
            $("#" + values[i].id + "_previous").html(values[i].previous);
            if (values[i].current !== values[i].previous) {
                $("#" + values[i].id).addClass("changed");
            }
        }
    }

    function updateHistory(history) {
        this.history = history
        this.hp = this.history.length
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
    }

    function disable() {
        $("body").append("<div class='modal-backdrop disabled'></div>")
    }

    function enable() {
        $(".disabled").remove()
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