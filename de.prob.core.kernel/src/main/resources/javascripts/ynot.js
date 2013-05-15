"use strict";

var poll_interval = 100;
var session = get_session();
var currentMode = null;
var currentEditor = null;
var lastcommand = -1;
var terminated = false;

function get_template(name) {
    var html = templates[name];
    if (html == undefined) {
        $.ajax({
            url : "templates/" + name,
            success : function(result) {
                if (result.isOk === false) {
                    alert(result.message);
                } else {
                    html = result;
                }
            },
            async : false
        });
        templates[name] = html;
    }
    return html;
}

var editorkeys = function(id) {
    return {
        'Shift-Enter' : function(cm) {
            return true;
        },
        'Up' : function(cm) {
            var pos = cm.getCursor().line;
            if (pos === 0) {
                async_query(session, {
                    "cmd" : "leave",
                    "box" : id,
                    "text" : currentEditor.getValue(),
                    "direction" : "up"
                })
            } else
                return CodeMirror.Pass;
        },
        'Down' : function(cm) {
            var cnt = cm.doc.lineCount();
            var pos = cm.getCursor().line;
            if (pos === cnt - 1) {
                async_query(session, {
                    "cmd" : "leave",
                    "box" : id,
                    "text" : currentEditor.getValue(),
                    "direction" : "down"
                })
            } else
                return CodeMirror.Pass;
        }
    }
};

var templates = {};
get_template("server_disconnected.html"); // won't be able to get it later ;-)
get_template('worksheet_renderer.html');
get_template('worksheet_editor.html');
get_template('worksheet_topbar.html')

function reEval(rep) {
    console.log('Reevaluate Worksheet, starting with pos ', rep);
}

function reorder() {
    var boxes = $(".worksheetbox");
    var neditors = [];
    for ( var i = 0; i < boxes.length; i++) {
        var id = boxes[i].id.substr(5);
        neditors.push(id);
    }
    var msg = {
        'cmd' : 'reorder',
        'order' : neditors
    };
    async_query(session, msg);
}

function dispatch_setTop(lang) {
    var l = eval("settings." + lang).lang
    var view = {
        'mode' : l
    }
    var output = Mustache.render(get_template("worksheet_topbar.html"), view);
    $(output).appendTo(".top-bar")
}

function init() {

    $(function() {
        $("#boxes").sortable({
            placeholder : "ui-sortable-placeholder",
            update : reorder,
            handle : ".renderer",
            forcePlaceholderSize : true
        });
    });

    var msg = {
        'cmd' : 'init'
    };
    async_query(session, msg);

    setInterval(poller, poll_interval);
}

function poller() {
    if (!terminated) {
        var data = {
            'cmd' : 'updates',
            'since' : lastcommand,
            'session' : session
        };
        $.ajax({
            url : "exec",
            dataType : 'json',
            data : data,
            success : function(data) {
                if (data != null && data != "" && data != []) {
                    for ( var i = 0; i < data.length; i++) {
                        lastcommand = data[i].id;
                        dispatch(JSON.parse(data[i]));
                    }
                }
            },
            error : function() {
                terminated = true;
                $("body").replaceWith(get_template("server_disconnected.html"))
            }
        });
    }
}

function dispatch(data) {
    var cmd = data.cmd;
    switch (cmd) {
    case 'set_top':
        dispatch_setTop(data.lang);
        break;
    case 'append_box':
        var options = eval("settings." + data.lang);
        appendEditor(options, data.id);
        break;
    case 'delete':
        deleteBoxFromDom(data.id);
        break;
    case 'eval':
        deleteBoxFromDom(data.id);
        break;
    case 'focus':
        currentEditor.focus();
        if (data.direction === "from_below") {
            var e = currentEditor;
            var lastlinepos = e.lineCount() - 1
            var lastline = e.getLine(lastlinepos)
            var lastcol = lastline.length
            currentEditor.setCursor(lastlinepos, lastcol);
        }
        break;
    case 'activate':
        var options = eval("settings." + data.lang);
        $("#wsbox" + data.id).replaceWith(
                Mustache.render('<div id="tmprenderer{{id}}">', {
                    'id' : data.id
                }))
        activateEditor(options, data.id, data.text);
        break;
    case 'render':
        var view = JSON.parse(data.args);
        view["lang"] = eval("settings." + data.lang).lang;
        var rend = null;
        if (data.template != "none") {
            rend = Mustache.render(get_template(data.template), view);
            $("#wsbox" + data.id).replaceWith(rend);
        } else {
            rend = Mustache.render(get_template("plain_renderer.html"), view);
            $("#wsbox" + data.id).replaceWith(rend);
            $("#renderer" + data.id).append(view.text)
        }
        $("#renderer" + data.id).dblclick(function(evt) {
            async_query(session, {
                "cmd" : "renderer_dblclick",
                "box" : data.id,
                "text" : currentEditor.getValue()
            });
        });
        break;

    default:
        break;
    }
    console.log(data);
}

function deleteBox(id) {
    var msg = {
        'cmd' : 'delete',
        'box' : id
    };
    async_query(session, msg);
}

function deleteBoxFromDom(id) {
    $("#wsbox" + id).remove();
}

function setDefaultType(mode) {
    $("#ts_button").html(function() {
        return mode.lang + " <span class='caret'></span>"
    });
    async_query(session, {
        'cmd' : 'default_lang',
        'lang' : mode.key
    })
}

function appendEditor(options, id) {
    $(Mustache.render('<div id="tmprenderer{{id}}">', {
        'id' : id
    })).appendTo("#boxes");
    activateEditor(options, id);
}

function activateEditor(options, id) {
    var text = arguments[2];
    if (text == undefined)
        text = ""
    var view = {
        'id' : id,
        'lang' : options.lang,
        'content' : text
    };
    var output = Mustache.render(get_template("worksheet_editor.html"), view);
    $("#tmprenderer" + id).replaceWith(output);
    var textarea = $("#code" + id)[0];
    currentEditor = CodeMirror.fromTextArea(textarea, options.codemirror);
    currentEditor.addKeyMap(editorkeys(id));
    currentEditor.on("blur", function() {
        currentEditor.save();
        async_query(session, {
            "cmd" : "leave",
            "box" : id,
            "direction" : "none",
            "text" : currentEditor.getValue()
        });
    });
    $(".CodeMirror-hscrollbar").remove(); // Hack! no horizontal scrolling
    currentEditor.getWrapperElement().onkeypress = function(e) {
        if (e.shiftKey && e.keyCode === 13)
            e.preventDefault();
        if (e.keyCode === 13)
            console.log("enter")
    };

}

function enableRenderer(info) {
    var editor = info.codemirror;
    var text = editor.getValue();
    var evalfkt = info.options.evalfkt;
    var renderer = info.renderer;
    var output = evalfkt(text, info);
    renderer[0].style.display = "block";
    MathJax.Hub.Queue([ "Typeset", MathJax.Hub, renderer[0] ]);
    $("#wsbox" + info.id).height(renderer.height() + 8);
}

function disableRenderer(info) {
    info.renderer[0].style.display = "none";
}

function enableEditor(info) {
    var editor = info.codemirror;
    var wrapper = editor.getWrapperElement();
    wrapper.style.display = "block";
    currentEditor = findEditor(info.id);
    editor.focus();
}

function findEditor(id) {
    for ( var i = 0; i < editors.length; i++) {
        if (editors[i].id == id)
            return i;
    }
}

function disableEditor(info) {
    var editor = info.codemirror;
    info.codemirror.getWrapperElement().style.display = "none";
}

// function evalGroovy(text, info) {
// var r = info.renderer;
// r.removeClass("renderer");
// info.renderer.html('<img src="images/loading.gif" class="preload" />');
// $.getJSON("exec", {
// 'command' : 'eval',
// 'text' : text,
// 'lang' : "groovy",
// 'session' : session
// }, function(data) {
// session = data.session;
// r.addClass("renderer");
// printRenderer(r, data.result)
// $("#wsbox" + info.id).height(r.height() + 8);
//
// });
// }
// function evalB(text, info) {
// var r = info.renderer;
// r.removeClass("renderer");
// info.renderer.html('<img src="images/loading.gif" class="preload" />');
// $.getJSON("exec", {
// 'command' : 'eval',
// 'text' : text,
// 'lang' : "b",
// 'session' : session
// }, function(data) {
// session = data.session;
// r.addClass("renderer");
// printRenderer(r, data.result)
// $("#wsbox" + info.id).height(r.height() + 8);
//
// });
// }
//
// function evalMarkdown(text, info) {
// var t = markdown.toHTML(text);
// printRenderer(info.renderer, t);
// }

// function evalJavaScript(text, info) {
// var printResult = true;
// var r = null;
// try {
// r = eval(text);
// } catch (e) {
// r = '<div class="jserror">' + e + '</div>';
// }
// if (r == undefined)
// r = "undefined";
// if (printResult)
// printRenderer(info.renderer, r);
// }

function printRenderer(renderer, out) {
    var output = String(out);
    if (output === "")
        renderer.html("<p>&nbsp;</p>");
    else
        renderer.html(output);
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
    msg.type = "sending";
    if (msg.cmd != "updates")
        console.log(msg)
    $.ajax({
        url : "exec",
        data : msg
    })

}

var settings = {
    markdown : {
        lang : "Markdown",
        key : "markdown",
        codemirror : {
            mode : 'markdown',
            lineNumbers : false,
            theme : "default",
            viewportMargin : Infinity,
            lineWrapping : true,
            extraKeys : {
                "Enter" : "newlineAndIndentContinueMarkdownList"
            }
        }
    },
    groovy : {
        key : "groovy",
        lang : "Groovy",
        codemirror : {
            mode : 'groovy',
            lineNumbers : true,
            lineWrapping : true,
            theme : "default",
            viewportMargin : Infinity,
        }
    },
    javascript : {
        key : "javascript",
        lang : "JavaScript",
        codemirror : {
            mode : 'javascript',
            lineNumbers : true,
            lineWrapping : true,
            theme : "default",
            viewportMargin : Infinity,
        }
    },
    b : {
        key : "b",
        lang : "B",
        codemirror : {
            mode : 'javascript',
            lineNumbers : false,
            lineWrapping : true,
            theme : "default",
            viewportMargin : Infinity,
        }
    }
}

function setBoxType(box, options) {
    async_query(session, {
        'cmd' : 'switch_box_lang',
        'lang' : options.key,
        'box' : box
    })
}

function updateServlet() {
}
// var d = [];
// editors.forEach(function(editor) {
// d.push({
// 'id' : editor.id,
// 'lang' : editor.options.lang,
// 'text' : editor.codemirror.getValue()
// })
// });
//
// var request = $.ajax({
// url : "exec",
// type : "GET",
// data : {
// 'session' : session,
// 'command' : 'update',
// 'data' : d,
// 'count' : editors.length
// },
// dataType : "html"
// });
//
// }
