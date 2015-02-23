Events = (function() {
    var extern = {}
    var session = Session()
    var sortMode = "normal"
    var hidden = false
    var cm = null
    var history = [];

    var editorkeys = function() {
        return {
            'Enter' : function(cm) {
                hp = null;
                var code = cm.getValue();
//                console.log("submit: '" + code + "'")
                session.sendCmd("executeEvent", {
                    "event" : code
                })
                history.push(code);
                cm.setValue("")
                return false;
            },
            'Up' : function(cm) {
                if (cm.getCursor().line == 0) {
//                    console.log("History up")
                    if (hp == null) {
                        hp = history.length;
                    }
                    if (hp >= 0) {
                        if (hp > 0) {
                            hp--
                        }
                        cm.setValue(history[hp])
                        cm.se
                    }

                } else
                    hp = null;
                return CodeMirror.Pass;
                ;
            },
            'Down' : function(cm) {
                var cnt = cm.doc.lineCount();
                if (cm.getCursor().line == cnt - 1) {
//                    console.log("History down")
                    if (hp != null) {
                        if (hp < history.length - 1) {
                            hp++
                            cm.setValue(history[hp])
                        } else {
                            cm.setValue("")
                        }

                    }
                } else
                    return CodeMirror.Pass;
                ;
            },'Shift-Enter' : function(cm) {
                cm.replaceSelection("\n", "end", "+input");
                cm.indentLine(cm.getCursor().line, null, true);
                return true;
            }
        }
    };

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
    }

    function debounce(fn, delay) {
        var timer = null;
        return function () {
            var context = this, args = arguments;
            clearTimeout(timer);
            timer = setTimeout(function () {
                fn.apply(context, args);
            }, delay);
        };
    }

    $(document).ready(function() {
        $(window).keydown(function(event){
            if(event.keyCode == 13) {
                event.preventDefault();
                if(event.target.id === "numRand") {
                	if(!$("#randomX").prop("disabled")) {
                		random($("#numRand").val())
            		}
                }
                return false;
            }
        })

        $('.dropdown-toggle').dropdown()

        $('.dropdown-menu input').click(function(e) {
            e.stopPropagation()
        })

        $("#numRand").keyup(debounce(function(e) {
            var isInt = /^([0-9]+)$/.exec(e.target.value)!=null

            if(!isInt && !$("#randomInput").hasClass('has-error')) {
                $("#randomInput").addClass('has-error')
                $("#randomX").prop("disabled",true)
            } else {
                $("#randomInput").removeClass('has-error')
                $("#randomX").prop("disabled",false)
            }
        }, 250));

        $("#random1").click(function(e) {e.preventDefault();random(1)})
        $("#random5").click(function(e) {e.preventDefault();random(5)})
        $("#random10").click(function(e) {e.preventDefault();random(10)})
        $("#randomX").click(function(e) {
            e.preventDefault();
            if(!$("#randomX").prop("disabled")) {
                random($("#numRand").val())
            }
        })

        $("#back").click(function(e) {
            e.preventDefault()
            session.sendCmd("back", {
                "client" : extern.client
            })
        })

        $("#forward").click(function(e) {
            e.preventDefault()
            session.sendCmd("forward", {
                "client" : extern.client
            })
        })

        $("#sort").click(function(e) {
            changeSortMode()
            session.sendCmd("sort", {
                "sortMode" : sortMode,
                "client" : extern.client
            })
        })

        $("#hide").click(function(e) {
            hidden = !hidden
            if(hidden) {
                $(".notEnabled").css("display","none")
            } else {
                $(".notEnabled").css("display","list-item")
            }
            session.sendCmd("hide", {
                "hidden" : hidden,
                "client" : extern.client
            })
        })

        $("#search").keyup(debounce(function(e) {
            session.sendCmd("filter", {
                "filter" : e.target.value,
                "client" : extern.client
            })
        },250))

        $("#notification-button").click(function(e) {
            $("#notification-screen").modal('show')
        })

    })

    function changeSortMode() {
        if( sortMode === "normal" ) {
            sortMode = "aToZ"
        } else if( sortMode === "aToZ" ) {
            sortMode = "zToA"
        } else if( sortMode === "zToA" ) {
            sortMode = "normal"
        }
    }

    function setContent(ops_string, errors) {
        $(".alert").remove()
        var ops = JSON.parse(ops_string)
        if (ops.length === 0) {
            $(".input-wrap").addClass("hide-input")
        } else {
            $(".input-wrap").removeClass("hide-input")
        }
        var e = $("#events")
        e.children().remove()
        for (el in ops) {
            var v = ops[el]
            v.params = v.params.join(", ")
            e.append(session.render("/ui/eventview/operation.html", v))
        }
        $(".enabled").click(function(e) {
        	e.preventDefault();
            var id = e.currentTarget.id
            id = id.substring(2,id.length)
//            console.log(id)
            session.sendCmd("execute", {
                "id" : id,
                "client" : extern.client
            })
        })
        if(hidden) {
            $(".notEnabled").css("display","none")
        } else {
            $(".notEnabled").css("display","list-item")
        }

        // Errors
        var errs = JSON.parse(errors)
        if (errs.length === 1) {
            $("#alert-bar").html(session.render("/ui/eventview/erroralert.html",errs[0]))
            $("#notification-button").click(function(e) {
                $("#notification-screen").modal('show')
            })
        } else if(errs.length > 1) {
            $("#alert-bar").html(session.render("/ui/eventview/erroralert.html",{shortMsg: "Errors Occurred"}))
            $("#notification-button").click(function(e) {
                $("#notification-screen").modal('show')
            })
        }

        $("#notification").html(session.render("/ui/eventview/errorlist.html",{errors: errs}))
    }

    function setBackEnabled(enabled) {
        $("#back").prop("disabled",!(enabled === "true"))
    }

    function setForwardEnabled(enabled) {
        $("#forward").prop("disabled",!(enabled === "true"))

    }

    function random(num) {
        session.sendCmd("random", {
            "num" : num,
            "client" : extern.client
        })
    }

    function setSortMode(mode) {
        this.sortMode = mode
    }

    function setHide(isHidden) {
        hidden = isHidden === "true"
    }

    function disable() {
        $("body").append("<div class='modal-backdrop transparent'></div>")
    }

    function enable() {
        $(".transparent").remove()
    }

    extern.client = ""
    extern.init = function() {
        session.init()
        init()
    }
    extern.setContent = function(data) {
        setContent(data.ops, "[]")
    }
    extern.setView = function(data) {
        setHide(data.hide)
        setContent(data.ops,data.errors)
        setBackEnabled(data.canGoBack)
        setForwardEnabled(data.canGoForward)
        setSortMode(data.sortMode)
    }
    extern.newTrace = function(data) {
        setContent(data.ops,data.errors)
        setBackEnabled(data.canGoBack)
        setForwardEnabled(data.canGoForward)
    }
    extern.disable = disable
    extern.enable = enable

    return extern;
}())