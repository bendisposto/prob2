ModelChecking = (function() {
    var extern = {}
    var session = Session()

    $(document).ready(function() {
        $("#start-new").click(function(e) {
           $("#open-new").modal('show')
        })

        $("#results").append(session.render("/ui/modelchecking/queued.html", {id: "job1"}))
        $("#results").append(session.render("/ui/modelchecking/queued.html", {id: "job11"}))
        $("#results").append(session.render("/ui/modelchecking/queued.html", {id: "job2"}))
        $("#results").append(session.render("/ui/modelchecking/queued.html", {id: "job3"}))

        $("#job1-in").replaceWith(session.render("/ui/modelchecking/finished.html", {id: "job1", message: "Model Checking sucessful.", hasTrace: false, result: "success", processedNodes: 500, totalNodes: 500, totalTransitions: 1000}))
        $("#job11-in").replaceWith(session.render("/ui/modelchecking/working.html", {id: "job11", processedNodes: 120, totalNodes: 240, totalTransitions: 1000, percent: 50}))
        $("#job3-in").replaceWith(session.render("/ui/modelchecking/finished.html", {id: "job3", message: "Invariant violation found.", hasTrace: true, result: "danger", processedNodes: 500, totalNodes: 500, totalTransitions: 1000}))

        $(".job").click(function(e) {
            $(".job").removeClass("selected")
            $(e.currentTarget).addClass("selected")
            $(".result").addClass("invisible")
            $("#"+e.currentTarget.id+"-out").removeClass("invisible")
        })
    })


    function toggleOption(method,id) {
        session.sendCmd(method, {"set": $("#"+id).prop('checked')})
    }

    function jobStarted(data) {
        $("#content").append(session.render("/ui/modelchecking/job.html", data))
        $("#results").append(session.render("/ui/modelchecking/queued.html", data))
        $("#"+data.id).click(function(e) {
            $(".job").removeClass("selected")
            $(e.currentTarget).addClass("selected")
            $(".result").addClass("invisible")
            $("#"+e.currentTarget.id+"-out").removeClass("invisible")
        })
    }

    function updateJob(id,data) {
        $("#"+id+"-in").replaceWith(session.render("/ui/modelchecking/working.html", data))
    }

    function finishJob(id,data) {
        $("#"+id+"-in").replaceWith(session.render("/ui/modelchecking/finished.html", data))
        if(data.result === "success") {
            $("#"+id+"-jobdesc").before("<span class='success glyphicon glyphicon-ok-circle'></span>")
        }
        if(data.result === "danger") {
            $("#"+id+"-jobdesc").before("<span class='failure glyphicon glyphicon-remove-circle'></span>")
        }
        if(data.result === "warning") {
            $("#"+id+"-jobdesc").before("<span class='not-complete glyphicon glyphicon-minus'></span>")
        }
    }
    
    function changeStateSpaces(ssId) {
        $(".job").addClass("invisible")
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
    extern.toggleOption = toggleOption


    return extern;
}())