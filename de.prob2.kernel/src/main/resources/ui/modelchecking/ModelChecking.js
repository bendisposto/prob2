ModelChecking = (function() {
    var extern = {}
    var session = Session()
    var mode = ""

    $(document).ready(function() {
        $(window).keydown(function(event){
            if(event.keyCode == 13) {
                event.preventDefault();
                return false;
            }
        })

        $(".op-btn").click(function(e) {
            var id = "#" + e.target.id + "-mode"
            $("#open-new").modal('hide')
            $(".mc-mode").addClass("invisible")
            $(id).removeClass("invisible")
            $("#submit-job").unbind()
            var mode = e.currentTarget.id
            $("#submit-job").prop("disabled", $(id).hasClass("parse-incorrect"))
            $("#submit-job").click(function(e) {
                session.sendCmd("startJob", {
                    "check-mode": mode
                })
            })
            $("#open-new").modal('show')
        })

        $(".option").click(function(e) {
            toggleOption(e.target.id)
        })

        $(".form-control").keyup(function(e) {
            session.sendCmd("parse", {
                "formula" : e.target.value,
                "id" : e.target.parentNode.id
            })
        })
    })


    function toggleOption(option) {
        session.sendCmd(option, {"set": $("#"+option).prop('checked')})
    }

    function jobStarted(id, data) {
        $("#content").append(session.render("/ui/modelchecking/job.html", data))
        $("#results").append(session.render("/ui/modelchecking/queued.html", data))
        selectJob(id)
        $("#"+id+"-cancel").click(function(e) {
            session.sendCmd("cancel", {
                "jobId" : id
            })
        })
        $("#"+id).click(function(e) {
            selectJob(id)
        })
    }

    function selectJob(id) {
        $(".job").removeClass("selected")
        $("#"+id).addClass("selected")
        $(".result").addClass("invisible")
        $("#"+id+"-out").removeClass("invisible")
    }

    function cancelJob(id) {
        $("#"+id+"-in").replaceWith(session.render("/ui/modelchecking/cancelled.html", {"id": id}))
    }

    function updateJob(id,data) {
        data.stats = data.stats === "true"
        $("#"+id+"-in").replaceWith(session.render("/ui/modelchecking/working.html", data))
        $("#"+id+"-cancel").click(function(e) {
            session.sendCmd(session.sendCmd("cancel", {
                "jobId" : id
            }))
        })
    }

    function finishJob(id, data) {
        data.hasTrace = data.hasTrace === "true"
        data.stats = data.stats === "true"
        $("#"+id+"-in").replaceWith(session.render("/ui/modelchecking/finished.html", data))
        if(data.hasTrace) {
            $("#"+id+"-opentrace").click(function(e) {
                session.sendCmd("openTrace", {
                    "jobId": id
                })
            })
        }
        if(data.result === "success") {
            $("#"+id+"-status").replaceWith("<span id='"+id+"-status' class='success glyphicon glyphicon-ok-circle'></span>")
        }
        if(data.result === "danger") {
            $("#"+id+"-status").replaceWith("<span id='"+id+"-status' class='failure glyphicon glyphicon-remove-circle'></span>")
        }
        if(data.result === "warning") {
            $("#"+id+"-status").replaceWith("<span id='"+id+"-status' class='not-complete glyphicon glyphicon-minus'></span>")
        }
    }
    
    function changeStateSpaces(ssId, events, bType) {
        $(".job").addClass("invisible")
        $(".result").addClass("invisible")
        $("."+ssId).removeClass("invisible")
        if(!bType) {
            $(".b-type").addClass("invisible")
        }
        $("#cbc-inv-event-list").replaceWith(session.render("/ui/modelchecking/cbc-inv-list.html", {"events": events}))
        $(".cbc-inv-event").click(function(e) {
            if($(e.currentTarget).hasClass("event-selected")) {
                $(e.currentTarget).removeClass("event-selected")
                session.sendCmd("removeEvent", {
                    "event": $(e.currentTarget).text()
                })
            } else {
                $(e.currentTarget).addClass("event-selected")
                session.sendCmd("selectEvent", {
                    "event": $(e.currentTarget).text()
                })
            }
        })
    }

    function parseOk(id) {
        $("#"+id).removeClass("has-error")
        $("#submit-job").prop("disabled", false)
        $("#"+id).parent().removeClass("parse-incorrect")
    }

    function parseError(id) {
        $("#"+id).addClass("has-error")
        $("#submit-job").prop("disabled",true)
        $("#"+id).parent().addClass("parse-incorrect")
    }

    extern.client = ""
    extern.init = session.init
    extern.setDefaultOptions = function(data) {
        setDefaultOptions(data.options)
    }
    extern.changeStateSpaces = function(data) {
        var events = JSON.parse(data.events)
        changeStateSpaces(data.ssId, JSON.parse(data.events), data.bType === "true")
    }
    extern.updateJob = function(data) {
        updateJob(data.id, data)
    }
    extern.finishJob = function(data) {
        finishJob(data.id, data)
    }
    extern.jobStarted = function(data) {
        jobStarted(data.id, data)
    }
    extern.cancelJob = function(data) {
        cancelJob(data.id)
    }
    extern.toggleOption = toggleOption
    extern.parseOk = function(data) {
        parseOk(data.id)
    }
    extern.parseError = function(data) {
        parseError(data.id)
    }

    return extern;
}())