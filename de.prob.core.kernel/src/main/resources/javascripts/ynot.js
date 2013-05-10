"use strict";

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

var session = null, globalcount = 0, currentMode = null, editors = [], currentEditor = null, boxtemplate = get_template('templates/worksheet_box.html');

function reEval(rep) {
    console.log('Reevaluate Worksheet, starting with pos ', rep);
}

function reorder(e, x) {
    boxes = $(".worksheetbox");
    neditors = [];
    op = findEditor(x.item[0].id.substr(5));
    for ( var i = 0; i < boxes.length; i++) {
        id = boxes[i].id.substr(5);
        e = findEditor(id);
        neditors.push(editors[e]);
    }
    editors = neditors;
    np = findEditor(x.item[0].id.substr(5));
    rep = Math.min(op, np);
    reEval(rep)
    updateServlet();
}

function init() {
    currentMode = settings.b;
    createEditor(currentMode);
    $(function() {
        $("#boxes").sortable({
            placeholder : "ui-sortable-placeholder",
            update : reorder,
            handle : ".renderer",
            forcePlaceholderSize : true
        });
    });
    updateServlet();
}
// <div class="span1 topmenu">{{lang}}</div> \

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

function freshId() {
    return globalcount++;
}

function setDefaultType(mode) {
    currentMode = mode;
    $("#ts_button").html(function() {
        return mode.lang + " <span class='caret'></span>"
    });
}

function createEditor(options) {
    var id = freshId();

    var view = {
        'id' : id,
        'lang' : options.lang,

    };
    var output = Mustache.render(boxtemplate, view);
    $(output).appendTo("#boxes");
    var box = $("#wsbox" + id)
    var textarea = $("#code" + id)[0];
    var editor = CodeMirror.fromTextArea(textarea, options.codemirror);
    var renderer = $("#renderer" + id);
    // $("#wsbox" + id + " .CodeMirror").addClass('span11')

    $(".CodeMirror-hscrollbar").remove(); // Hack! no horizontal
    // scrollbars
    var editorinfo = {
        'codemirror' : editor,
        'renderer' : renderer,
        'options' : options,
        'box' : box[0],
        'id' : id
    };

    configureCodemirror(editorinfo);
    editors.push(editorinfo);
    editor.focus();
}

function nextEditor() {
    currentEditor++;
    if (currentEditor === editors.length) {
        createEditor(currentMode);
    } else {
        var info = editors[currentEditor];
        disableRenderer(info);
        enableEditor(info);
        info.codemirror.focus();
    }
}

function previousEditor() {
    if (currentEditor > 0) {
        currentEditor--;
        var info = editors[currentEditor];
        disableRenderer(info);
        enableEditor(info);
        info.codemirror.focus();
    }
}

function configureCodemirror(info) {

    // Evaluate on unfocus

    // if (info.id % 2 === 0) { // used for debugging stylesheets

    info.codemirror.on("blur", function() {
        info.codemirror.save();
        disableEditor(info);
        enableRenderer(info);
        var pos = findEditor(info.id)
        reEval(pos);
    });
    // }

    // prevent newline if Shift +Enter is pressed
    info.codemirror.getWrapperElement().onkeypress = function(e) {
        if (event.shiftKey && event.keyCode === 13)
            e.preventDefault();
    };

    info.codemirror.addKeyMap({
        'Shift-Enter' : function(cm) {
            nextEditor();
            return true;
        },
        'Up' : function(cm) {
            var pos = cm.getCursor().line;
            if (pos === 0) {
                previousEditor();
            } else
                return CodeMirror.Pass;
        },
        'Down' : function(cm) {
            var cnt = cm.doc.lineCount();
            var pos = cm.getCursor().line;
            if (pos === cnt - 1) {
                nextEditor();
            } else
                return CodeMirror.Pass;
        }
    });

    info.renderer.dblclick(function(evt) {
        disableRenderer(info);
        enableEditor(info);
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

function evalGroovy(text, info) {
    var r = info.renderer;
    r.removeClass("renderer");
    info.renderer.html('<img src="images/loading.gif" class="preload"  />');
    $.getJSON("exec", {
        'command' : 'eval',
        'text' : text,
        'lang' : "groovy",
        'session' : session
    }, function(data) {
        session = data.session;
        r.addClass("renderer");
        printRenderer(r, data.result)
        $("#wsbox" + info.id).height(r.height() + 8);

    });
}
function evalB(text, info) {
    var r = info.renderer;
    r.removeClass("renderer");
    info.renderer.html('<img src="images/loading.gif" class="preload"  />');
    $.getJSON("exec", {
        'command' : 'eval',
        'text' : text,
        'lang' : "b",
        'session' : session
    }, function(data) {
        session = data.session;
        r.addClass("renderer");
        printRenderer(r, data.result)
        $("#wsbox" + info.id).height(r.height() + 8);

    });
}

function evalMarkdown(text, info) {
    var t = markdown.toHTML(text);
    printRenderer(info.renderer, t);
}
function evalJavaScript(text, info) {
    var printResult = true;
    var r = null;
    try {
        r = eval(text);
    } catch (e) {
        r = '<div class="jserror">' + e + '</div>';
    }
    if (r == undefined)
        r = "undefined";
    if (printResult)
        printRenderer(info.renderer, r);
}

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
        evalfkt : evalMarkdown,
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
        evalfkt : evalGroovy,
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
        evalfkt : evalJavaScript,
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
        evalfkt : evalB,
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
    var d = [];
    editors.forEach(function(editor) {
        d.push({
            'id' : editor.id,
            'lang' : editor.options.lang,
            'text' : editor.codemirror.getValue()
        })
    });

    var request = $.ajax({
        url : "exec",
        type : "GET",
        data : {
            'session' : session,
            'command' : 'update',
            'data' : d,
            'count' : editors.length
        },
        dataType : "html"
    });

}
