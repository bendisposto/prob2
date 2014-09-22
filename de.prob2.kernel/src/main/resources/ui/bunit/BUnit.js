BUnit = (function() {
    var extern = {}
    var session = Session()
    
    $(document).ready(function() {
    });

    function clearInput() {
        $("#content").replaceWith(session.render("/ui/bunit/content.html", {}))
    }

    function setStats(data) {
        $("#stats").replaceWith(session.render("/ui/bunit/stats.html", data))
    }

    function addSuite(data) {
        $("#content").append(
                session.render("/ui/bunit/suite.html", data))
    }

    function addTest(suite, data) {
        $("#"+suite+"-list").append(session.render("/ui/bunit/test.html", data))
        $("#"+data.id).click(function(e) {
            $("#errors").addClass("invisible")
        })
    }

    function suiteFail(suite) {
    	$("#"+suite).addClass("test-suite-fail")
    }

    function suiteError(suite) {
    	$("#"+suite).addClass("test-suite-error")
    }

    function testIgnore(id) {
        $("#"+id).addClass("test-ignored")
        $("#"+id).click(function(e) {
            $("#errors").addClass("invisible")
        })
    }

    function testFail(id, reason) {
        $("#"+id).addClass("test-fail")
        $("#"+id).click(function(e) {
            $("#errors").removeClass("invisible")
            $("#errors").empty()
            $("#errors").html(reason)           
        }) 
    }

    function testError(id, reason) {
        $("#"+id).addClass("test-error")
        $("#"+id).click(function(e) {
            $("#errors").removeClass("invisible")
            $("#errors").empty()
            $("#errors").html(reason)           
        })        
    }



    extern.clearInput = clearInput

    extern.client = ""
    extern.init = session.init
    extern.addSuite = addSuite
    extern.addTest = function(data) {
        addTest(data.suite, data)
    }
    extern.testIgnore = function(data) {
        addTest(data.suite, data)
        testIgnore(data.id)
    }
    extern.testFail = function(data) {
    	suiteFail(data.suite)
        testFail(data.test, data.reason)
    }
    extern.testError = function(data) {
    	suiteError(data.suite)
        testError(data.test, data.reason)
    }
    extern.setStats = setStats

    return extern;
}())