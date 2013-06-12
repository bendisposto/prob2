var template = "<div class=\"logged {{type}}\"><strong>{{level}}</strong> {{from}} - {{msg}}</div>";

var lastElement = 0;
var log = $("#log");

function scrollDown(){
  window.scrollTo(0,document.body.scrollHeight);
}

function addElements(elements) {
    var element;
    for (var i = 0; i < elements.length; i++) {
        element = Mustache.to_html(template,elements[i]);
        log.append(element);
    };
}

function initialize() {
    // setup output polling
    setInterval(function() {
	
        $.getJSON("get_log", {
            since : lastElement
        }, function(data) {
            if (data != "" && data.numLogged != lastElement) {
                lastElement = data.numLogged;
                addElements(data.elements);
                scrollDown();
            }
        });
    }, 300);
}