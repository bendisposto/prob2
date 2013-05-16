function calculateDimensions() {
    var dimensions = {};
    if (typeof (window.innerWidth) === 'number') {
        dimensions.width = window.innerWidth;
        dimensions.height = window.innerHeight; //NORMAL BROWSERS
    } else if (document.documentElement && (document.documentElement.clientWidth || document.documentElement.clientHeight)) {
        dimensions.width = document.documentElement.clientWidth;
        dimensions.height = document.documentElement.clientHeight; // IE6+
    } else if (document.body && (document.body.clientWidth || document.body.clientHeight)) {
        dimensions.width = document.body.clientWidth;
        dimensions.height = document.body.clientHeight;
    }
    return dimensions;
}

function applyStyling(styling) {
    var i, j, selector, selected, attributes, styles;
    for (i = 0; i < styling.length; i = i + 1) {
        selector = styling[i].selector;
        if (selector !== "") {
            selected = d3.selectAll(selector);
            attributes = styling[i].attributes;
            for (j = 0; j < attributes.length; j = j + 1) {
                selected.attr(attributes[j].name, attributes[j].value);
            }
            styles = styling[i].styles;
            for (j = 0; j < styles.length; j = j + 1) {
                selected.style(styles[j].name, styles[j].value);
            }
        }
    }
}

function createCanvas(positionId,width,height) {
    var svg, redrawSvg;
    svg = d3.select(positionId).append("svg:svg")
        .attr("class", "background")
        .attr("viewBox", "0 0 " + width + " " + height)
        .attr("pointer-events", "all")
      .append("svg:g")
        .call(d3.behavior.zoom().on("zoom", function() {
            redrawCanvas(svg);
        }))
      .append("svg:g");

    svg.append("svg:rect")
        .attr("class","canvas")
        .attr("width",width)
        .attr("height",height)
        .style("fill-opacity",1e-6);

    redrawCanvas = function(canvas) {
        canvas.attr("transform", "translate(" + d3.event.translate + ")" + " scale(" + d3.event.scale + ")");
    }

    return svg;
}