function get_template(url) {
    var html;
    $.ajax({
        url : url,
        success : function(result) {
            if (result.isOk === false) {
                alert(result.message);
            } else {
                html = result;
            }
        },
        async : false
    });
    return html;
}

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
    $.ajax({
        url : "exec",
        data : msg
    })
}