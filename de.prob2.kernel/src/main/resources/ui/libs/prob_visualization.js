function VizUtils(){
    var extern = {};

    function calculateHeight() {
        var height = 0;
        if (typeof (window.innerHeight) === 'number') {
            height = window.innerHeight;
        } else if (document.documentElement && (document.documentElement.clientHeight)) {
            height = document.documentElement.clientHeight;
        } else if (document.body && (document.body.clientHeight)) {
            height = document.body.clientHeight;
        }
        return height;
    }

    function calculateWidth() {
        var width = 0
        if (typeof (window.innerWidth) === 'number') {
            width = window.innerWidth;
        } else if (document.documentElement && (document.documentElement.clientWidth)) {
            width = document.documentElement.clientWidth;
        } else if (document.body && (document.body.clientWidth)) {
            width = document.body.clientWidth;
        }
        return width;
    }

    function applyStyling(styling) {
        var i, j, selector, selected, attributes, styles;
        for (i = 0; i < styling.length; i = i + 1) {
            try {
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
            } catch(e) {
                // Handle error. For now ignore, because there is not yet a good way to notify the user.
            }

        }
    }

    var zoom;

    function createCanvas(positionId,width,height) {
        var svg, redrawSvg;
        zoom = d3.behavior.zoom().on("zoom",function() {
            redrawCanvas(svg);
        });
        svg = d3.select(positionId).append("svg:svg")
            .attr("class", "background")
            .attr("viewBox", "0 0 " + width + " " + height)
            .attr("pointer-events", "all")
            .attr("id","svg-viz")
            .attr("height",height)
          .append("svg:g")
            .call(zoom)
          .append("svg:g")
            .attr("class","zoomed")
            .attr("transform", "translate(0,0)");

        svg.append("svg:rect")
            .attr("class","canvas")
            .attr("width",width)
            .attr("height",height)
            .style("fill-opacity",1e-6);

        function redrawCanvas(canvas) {
            canvas.attr("transform", "translate(" + d3.event.translate + ")" + " scale(" + d3.event.scale + ")");
        }

        resetZoom(svg);

        return svg;
    }

    function resetZoom(svg) {
        zoom.scale(1);
        zoom.translate([0, 0]);
        svg.transition().duration(500).attr("transform","translate("+zoom.translate()+") scale("+zoom.scale()+")");
    }

    extern.calculateHeight = calculateHeight;
    extern.calculateWidth = calculateWidth;
    extern.applyStyling = applyStyling;
    extern.createCanvas = createCanvas;

    return extern;
}



