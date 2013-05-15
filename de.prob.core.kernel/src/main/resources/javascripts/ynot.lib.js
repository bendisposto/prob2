function get_session() {
    var s;
    $.ajax({
        url : "exec?cmd=session",
        success : function(result) {
            if (result.isOk === false) {
                alert(result.message);
            } else {
                s = JSON.parse(result).session;
            }
        },
        async : false
    });
    return s;
}

function async_query(session, msg) {
    msg.session = session;
    msg.type = "sending";
    if (msg.cmd != "updates")
        console.log(msg)
    $.ajax({
        url : "exec",
        data : msg
    })

}