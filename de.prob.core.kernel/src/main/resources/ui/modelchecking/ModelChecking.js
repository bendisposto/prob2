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

        $("#options-body").append(session.render("/ui/modelchecking/options.html", {
                                                                                        spaces: [{id: "s1", rep: "Space 1"},{id: "s2", rep: "Space 2"}], 
                                                                                        "breadth_first_search": true, 
                                                                                        "find_deadlocks": false,
                                                                                        "find_assertion_violations": true,
                                                                                        "find_invariant_violations": false,
                                                                                        "inspect_existing_nodes": false,
                                                                                        "stop_at_full_coverage": false
                                                                                    }))
    })

    function updateOptions(options) {
        $("#options-body").empty()
        $("#options-body").append(session.render("/ui/modelchecking/options.html"),options)
    }

    function toggleOption(id) {
        session.sendCmd(id, {"set": $("#"+id).prop('checked')})
    }
    
    extern.client = ""
    extern.init = session.init
    extern.updateOptions = updateOptions
    extern.toggleOption = toggleOption


    return extern;
}())