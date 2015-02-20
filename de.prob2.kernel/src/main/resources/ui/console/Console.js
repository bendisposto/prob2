Console = (function() {
    var extern = {}
    var controller
    var session = Session()

    function scrollDown(){
          window.scrollTo(0,document.body.scrollHeight);
    }

    $(document).ready(function() {
        $(window).focus(function() {
            scrollDown()
        })
    })

    function onValidate(line) {
        return true;
    }

    function onHandle(line) {
        session.sendCmd("exec", {
            "line" : line,
            "client" : extern.client
        })
        scrollDown()
    }

    function onComplete(line_text, column_nr, perform_fn) {
        // console.log(line_text)
        // console.log(column_nr)
        // console.log(perform_fn)
    }

    $(document).ready(function() {
        controller = $("#console").console({
            welcomeMessage : 'ProB 2.0 Groovy console',
            promptLabel : 'ProB> ',
            continuedPromptLabel : '----| ',
            commandValidate : onValidate,
            commandHandle : onHandle,
            completionHandle : onComplete,
            autofocus : true,
            animateScroll : true,
            lineWrapping : true,
            promptHistory : true
        });
    });

    function setPromptHistory(history) {
        for (var i = 0; i < history.length; i++) {
            controller.addToHistory(history[i])
        };
    }

    function groovyResult(output, result) {
        controller.commandResult([ {
            msg : output,
            className : "groovy_output"
        }, {
            msg : result,
            className : "groovy_result"
        } ]);
    }

    function groovyError(message, trace) {
        controller.commandResult([ {
            msg : message,
            className : "groovy_error"
        }, {
            msg : trace,
            className : "groovy_trace"
        } ]);

    }

    extern.groovyResult = function(data) {
        groovyResult(data.output, data.result)
        scrollDown()
    }
    extern.groovyError = function(data) {
        groovyError(data.message, data.trace)
        scrollDown()
    }
    extern.client = ""
    extern.init = session.init
    extern.setPromptHistory = setPromptHistory

    return extern;
}())