Events = (function() {
    var extern = {}
    var session = Session()
    var sortMode = "normal"
    var hidden = false

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

        $("#random1").click(function(e) {random(1)})
        $("#random5").click(function(e) {random(5)})
        $("#random10").click(function(e) {random(10)})
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

    function setContent(ops_string) {
        var ops = JSON.parse(ops_string);
        var e = $("#events")
        e.children().remove()
        for (el in ops) {
            var v = ops[el]
            v.params = v.params.join(", ")
            e.append(session.render("/ui/eventview/operation.html", v))
        }
        $(".enabled").click(function(e) {
            var id = e.currentTarget.id
            id = id.substring(2,id.length)
            console.log(id)
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

    extern.client = ""
    extern.init = session.init
    extern.setContent = function(data) {
        setContent(data.ops)
    }
    extern.setView = function(data) {
        setHide(data.hide)
        setContent(data.ops)
        setBackEnabled(data.canGoBack)
        setForwardEnabled(data.canGoForward)
        setSortMode(data.sortMode)
    }
    extern.newTrace = function(data) {
        setContent(data.ops)
        setBackEnabled(data.canGoBack)
        setForwardEnabled(data.canGoForward)
    }

    return extern;
}())