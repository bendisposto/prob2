ModelChecking = (function() {
    var extern = {}
    var session = Session()

    $(document).ready(function() {
        if( $(".container").width() < 720) {
            $("#sidebar").css("height","")
        } else {
            $("#sidebar").height($(window).height() - 2)
        }
        $(window).resize(function (e) {
            if( $(".container").width() < 720) {
                $("#sidebar").css("height","")
            } else {
                $("#sidebar").height($(window).height() - 2)
            }
        })

        $("#results").append(session.render("/ui/modelchecking/queued.html", {id: "job1"}))
        $("#results").append(session.render("/ui/modelchecking/queued.html", {id: "job2"}))
        $("#results").append(session.render("/ui/modelchecking/queued.html", {id: "job3"}))

        $("#job2-in").replaceWith(session.render("/ui/modelchecking/working.html", {id: "job2", percent: 60, processedNodes: 120, totalNodes: 200, totalTransitions: 350}))
        $("#job3-in").replaceWith(session.render("/ui/modelchecking/finished.html", {id: "job3", message: "Model Checking sucessful.", result: "success", processedNodes: 500, totalNodes: 500, totalTransitions: 1000}))

        $(".job").click(function(e) {
            $(".job").removeClass("selected")
            $(e.currentTarget).addClass("selected")
            $(".result").addClass("invisible")
            $("#"+e.currentTarget.id+"-out").removeClass("invisible")
        })
    })

    
    extern.client = ""
    extern.init = session.init


    return extern;
}())