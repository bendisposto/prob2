BConsole = (function() {
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
        session.sendCmd("eval", {
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
            welcomeMessage : 'ProB 2.0 B console',
            promptLabel : 'Eval> ',
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

    function result(result) {
        controller.commandResult([ {
            msg : result,
            className : "result"
        }]);
    }

    function error(error) {
        controller.commandResult([ {
            msg : error,
            className : "error"
        }]);
    }

    function disable() {
        $("body").append("<div class='modal-backdrop disabled'></div>")
    }

    function enable() {
        $(".disabled").remove()
    }

    function modelChange(loaded, name) {
        $(".model").remove()
        if (loaded) {
            $(".jquery-console-welcome").after("<div class='model'><span class='bold'>Model Loaded: </span>"+name+"</div>")
        } 
    }

    extern.result = function(data) {
        result(data.result)
        scrollDown()
    }
    extern.error = function(data) {
        error(data.error)
        scrollDown()
    }
    extern.client = ""
    extern.init = session.init
    extern.setPromptHistory = setPromptHistory
    extern.disable = disable;
    extern.enable = enable;
    extern.modelChange = function(data) {
        modelChange(data.modelloaded === "true", data.name)
    }

    return extern;
}())