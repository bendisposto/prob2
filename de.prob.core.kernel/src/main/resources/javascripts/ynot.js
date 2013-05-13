"use strict";

var session = get_session();
var currentMode = null;
var editors = {};
var currentEditor = null;
var lastcommand = -1;
var boxtemplate = get_template('templates/worksheet_box.html');
var topbartemplate = get_template('templates/worksheet_topbar.html')

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
    var view = {
        'mode' : lang
    }
    var output = Mustache.render(topbartemplate, view);
    $(output).appendTo(".top-bar")
}

function init() {

    // currentMode = settings.groovy;
    // createEditor(currentMode);

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

    setInterval(function() {
        $.getJSON("exec", {
            'cmd' : 'updates',
            'since' : lastcommand,
            'session' : session
        }, function(data) {
            if (data != null && data != "") {
                lastcommand = data.id;
                dispatch(data)
            }
        })
    }, 4000);
}

function dispatch(data) {
    var cmd = data.cmd;
    switch (cmd) {
    case 'set_top':
        dispatch_setTop(data.lang);
        break;

    default:
        break;
    }
    console.log(data);
}

function deleteEditorById(id) {
    nr = findEditor(id);
    deleteEditor(nr);
}

function deleteEditor(nr) {
    if (currentEditor > nr) {
        currentEditor--;
    }

    var rme = editors.splice(nr, 1)[0];
    $("#wsbox" + rme.id).remove();

    if (nr === currentEditor) {
        var info = editors[currentEditor];
        disableRenderer(info);
        enableEditor(info);
        info.codemirror.focus();
    } else if (currentEditor === editors.length) {
        createEditor(currentMode);
    }
    updateServlet();
    return rme;
}

function setDefaultType(mode) {
    currentMode = mode;
    $("#ts_button").html(function() {
        return mode.lang + " <span class='caret'></span>"
    });
}

function createEditor(options, id) {

    var view = {
        'id' : id,
        'lang' : options.lang,
    };

    var output = Mustache.render(boxtemplate, view);
    $(output).appendTo("#boxes");
    var textarea = $("#code" + id)[0];
    var editor = CodeMirror.fromTextArea(textarea, options.codemirror);
    // $("#wsbox" + id + " .CodeMirror").addClass('span11')

    $(".CodeMirror-hscrollbar").remove(); // Hack! no horizontal
    // scrollbars

    configureCodemirror(id, editor);
    editors[id] = editor;
}
//
// function nextEditor() {
// currentEditor++;
// if (currentEditor === editors.length) {
// createEditor(currentMode);
// } else {
// var info = editors[currentEditor];
// disableRenderer(info);
// enableEditor(info);
// info.codemirror.focus();
// }
// }
//
// function previousEditor() {
// if (currentEditor > 0) {
// currentEditor--;
// var info = editors[currentEditor];
// disableRenderer(info);
// enableEditor(info);
// info.codemirror.focus();
// }
// }

function configureCodemirror(id, codemirror) {

    codemirror.on("blur", function() {
        codemirror.save();
        async_query(session, {
            "cmd" : "leave",
            "box" : id,
            "direction" : "none"
        });
    });

    // prevent newline if Shift +Enter is pressed
    codemirror.getWrapperElement().onkeypress = function(e) {
        if (event.shiftKey && event.keyCode === 13)
            e.preventDefault();
    };

    codemirror.addKeyMap({
        'Shift-Enter' : function(cm) {
            nextEditor();
            return true;
        },
        'Up' : function(cm) {
            var pos = cm.getCursor().line;
            async_query(session, {
                "cmd" : "leave",
                "box" : id,
                "direction" : "up"
            })
            return CodeMirror.Pass;
        },
        'Down' : function(cm) {
            var cnt = cm.doc.lineCount();
            var pos = cm.getCursor().line;
            if (pos === cnt - 1) {
                async_query(session, {
                    "cmd" : "leave",
                    "box" : id,
                    "direction" : "down"
                })
            } else
                return CodeMirror.Pass;
        }
    });

    $("#renderer" + id).dblclick(function(evt) {
        console.log("click")
        // disableRenderer(id);
        // enableEditor(id);
    });

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

var settings = {
    markdown : {
        lang : "Markdown",
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
    createEditor(options);
    var on = findEditor(box)
    var oldE = editors[on];
    var text = oldE.codemirror.getValue();
    var newE = editors.splice(-1)[0];
    newE.codemirror.setValue(text);
    editors[on] = newE;
    $("#wsbox" + oldE.id).replaceWith($("#wsbox" + newE.id))
    $("#lang_button" + newE.id).html(function() {
        return newE.options.lang + '  <span class="caret"></span>'
    });
    updateServlet();
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
