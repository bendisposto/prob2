Events = (function() {
    var extern = {}
    var session = Session();

    $(document).ready(function() {
        $('.dropdown-toggle').dropdown()

        $('.dropdown-menu input').click(function(e) {
            e.stopPropagation()
        })

        $("#numRand").keyup(function(e) {
            var isInt = /^([0-9]+)$/.exec(e.target.value)!=null

            if(!isInt && !$("#randomInput").hasClass('has-error')) {
                $("#randomInput").addClass('has-error')
                $("#randomX").prop("disabled",true)
            } else {
                $("#randomInput").removeClass('has-error')
                $("#randomX").prop("disabled",false)
            }
        });

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

    })

    function setContent(ops_string) {
        var ops = JSON.parse(ops_string);
        var e = $("#events")
        e.children().remove()
        for (el in ops) {
            var v = ops[el]
            v.params = v.params.join(", ")
            e.append(session.render("/ui/eventview/operation.html", v))
        }
        $(".operation").click(function(e) {
            var id = e.target.getAttribute("operation")
            console.log(id)
            session.sendCmd("execute", {
                "id" : id,
                "client" : extern.client
            })
        })
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

    extern.client = ""
    extern.init = session.init
    extern.setContent = function(data) {
        setContent(data.ops)
        setBackEnabled(data.canGoBack)
        setForwardEnabled(data.canGoForward)
    }

    return extern;
}())