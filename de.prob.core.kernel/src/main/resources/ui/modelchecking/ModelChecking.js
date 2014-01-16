ModelChecking = (function() {
    var extern = {}
    var session = Session()

    $(document).ready(function() {
        $("#start-new").click(function(e) {
           $("#open-new").modal('show')
        })

        $("#submit-job").click(function(e) {
            session.sendCmd("startJob", {})
        })
    })


    function toggleOption(method,id) {
        session.sendCmd(method, {"set": $("#"+id).prop('checked')})
    }

    function jobStarted(id, data) {
        $("#content").append(session.render("/ui/modelchecking/job.html", data))
        $("#results").append(session.render("/ui/modelchecking/queued.html", data))
        selectJob(id)
        $("#"+id+"-cancel").click(function(e) {
            session.sendCmd(session.sendCmd("cancel", {
                "jobId" : id
            }))
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

    function updateJob(id,data) {
        $("#"+id+"-in").replaceWith(session.render("/ui/modelchecking/working.html", data))
        $("#"+id+"-cancel").click(function(e) {
            session.sendCmd(session.sendCmd("cancel", {
                "jobId" : id
            }))
        })
    }

    function finishJob(id, data) {
        var hasTrace = data.hasTrace === "true"
        data.hasTrace = hasTrace
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
    
    function changeStateSpaces(ssId) {
        $(".job").addClass("invisible")
        $(".result").addClass("invisible")
        $("."+ssId).removeClass("invisible")
    }

    extern.client = ""
    extern.init = session.init
    extern.setDefaultOptions = function(data) {
        setDefaultOptions(data.options)
    }
    extern.changeStateSpaces = function(data) {
        changeStateSpaces(data.ssId)
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
    extern.toggleOption = toggleOption


    return extern;
}())