ModelChecking = (function() {
    var extern = {}
    var session = Session()
    
    $(document).ready(function() {
        $(".job").click(function(e) {
            $(".job").removeClass("selected")
            $(e.currentTarget).addClass("selected")
        })
    })

    
    extern.client = ""
    extern.init = session.init


    return extern;
}())