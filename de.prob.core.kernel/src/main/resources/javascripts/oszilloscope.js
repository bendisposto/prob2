function drawEach(svg, dataset, color, w, h, xLabel) {
    var i;
    var padding = 10;
    var xMax = 0;
    var xMin = 99999999;
    var temp = 0;

    for( i = 0 ; i < dataset.length ; i = i + 1 ) {
        temp = d3.max(dataset[i].dataset, function(d) { return d.t; });
        if( temp > xMax ) {
            xMax = temp;
        }
        temp = d3.min(dataset[i].dataset, function(d) { return d.t; });
        if( temp < xMin ) {
            xMin = temp;
        }
    }

    var xScale = d3.scale.linear().domain([xMin, xMax]).range([4*padding, w-padding]);

    var xAxis = d3.svg.axis()
                    .scale(xScale)
                    .orient("bottom");

    svg.append("g")
            .attr("class", "axis")
            .attr("transform", "translate("+0+"," + (h - 2*padding) + ")")
            .call(xAxis)
          .append("text")
            .attr("class", "label")
            .attr("x", w)
            .attr("y", -6)
            .style("text-anchor", "end")
            .text(xLabel);

    var line;
    var yHeight = h / dataset.length;
    var yScale;
    var yAxis;
    for( i = 0 ; i < dataset.length ; i++ ) {

        yScale = d3.scale.linear().domain([0, d3.max(dataset[i].dataset, function(d) { return d.value; })]).range([h-4*padding-yHeight*i, padding+h - yHeight * (i + 1)]);

        line = d3.svg.line().x(function(d){return xScale(d.t)}).y(function(d){ return yScale(d.value) });
       
        yAxis = d3.svg.axis()
                    .scale(yScale)
                    .orient("left").ticks(2);

        svg.append("g")
            .attr("class", "axis")
            .attr("transform", "translate(" + 3*padding + ",0)")
            .call(yAxis);   

        svg.append("path")
            .attr("class","connection")
            .attr("d", line(dataset[i].dataset))
            .attr("stroke", color(dataset[i].name));
    }

}

function drawOver(svg, dataset, color, w, h, xLabel) {
        var i,j;
    var padding = 10;

    var xMax = 0;
    var yMax = 0;
    var xMin = 99999999;
    var temp = 0;


    for( i = 0 ; i < dataset.length ; i = i + 1 ) {
        temp = d3.max(dataset[i].dataset, function(d) { return d.t; });
        if( temp > xMax ) {
            xMax = temp;
        }
        temp = d3.min(dataset[i].dataset, function(d) { return d.t; });
        if( temp < xMin ) {
            xMin = temp;
        }
        temp = d3.max(dataset[i].dataset, function(d) { return d.value; });
        if( temp > yMax ) {
            yMax = temp;
        }
    }

    for( i = 0 ; i < dataset.length ; i = i + 1 ) {
        var data = dataset[i].dataset;
        for( j = 0 ; j < data.length ; j = j + 1 ) {
            if( data[j].scaleV === undefined) {
                if( data[j].type === "BOOL" ) {
                    data[j].scaleV = data[j].value * yMax;
                } else {
                    data[j].scaleV = data[j].value;
                }
            }
        }
    }

    var xScale = d3.scale.linear().domain([xMin, xMax]).range([4*padding, w-padding]);
    var yScale = d3.scale.linear().domain([0, yMax]).range([h-4*padding, padding]);

    var xAxis = d3.svg.axis()
                    .scale(xScale)
                    .orient("bottom");
                    
    var yAxis = d3.svg.axis()
                    .scale(yScale)
                    .orient("left").ticks(2);

    var line = d3.svg.line().x(function(d){return xScale(d.t)}).y(function(d){ return yScale(d.scaleV) });

    svg.append("g")
        .attr("class", "axis")
        .attr("transform", "translate("+0+"," + (h - 2*padding) + ")")
        .call(xAxis)
      .append("text")
        .attr("class", "label")
        .attr("x", w)
        .attr("y", -6)
        .style("text-anchor", "end")
        .text(xLabel);
   
    svg.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(" + 3*padding + ",0)")
        .call(yAxis);   

    svg.selectAll("connection")
                    .data(dataset)
                    .enter()
                   .append("path")
                    .attr("class","connection")
                    .attr("d", function(d) { return line(d.dataset); })
                    .attr("stroke", function(d) { return color(d.name); });
}

function changeMode(id, svg, dataset) {
    svg.selectAll(".axis").remove();
    svg.selectAll(".connection").remove();
    svg.selectAll(".key").remove();
    svg.selectAll(".button").remove();

    var mode;
    if( dataset.mode === "over") {
        mode = "each";
    } else if( dataset.mode === "each") {
        mode = "over";
    }

    dataset.mode = mode;

    $.getJSON("formula", {
        sessionId : id,
        cmd : "mode_change",
        param : mode
    });


}


function doIt(id, svg, dataset, xLabel, w, h) {

    var color = d3.scale.category20();

    var elementNames = [];
    for( i = 0 ; i < dataset.length ; i = i + 1 ) {
        elementNames.push(dataset[i].name);
    }

    color.domain(elementNames);

    if( dataset.mode === "over") {
        drawOver(svg, dataset, color, w, h, xLabel);
    } else if( dataset.mode === "each") {
        drawEach(svg, dataset, color, w, h, xLabel);
    }

    var button = svg.append("g")
        .attr("transform","translate(" + (w + 10) + "," + (h - 20) +")")
        .attr("class","button")
        .on("click",function() { changeMode(id, svg, dataset); doIt(id, svg, dataset, xLabel, w, h); });

    var rect =button.append("rect")
        .attr("height","20px")
        .attr("fill","#B0B0B0")
        .attr("stroke","#888")
        .attr("rx","2")
        .attr("ry","2");

    var text = button.append("text")
        .text("Toggle mode")
        .attr("font-size","11px")
        .attr("dx","5px")
        .attr("dy","12px");

    rect.attr("width",text[0][0].getBBox().width+10);

    var keys = svg.selectAll("key")
            .data(elementNames)
            .enter()
           .append("g")
            .attr("class","key")
            .attr("transform",function(d) {
                var height = elementNames.indexOf(d) * 20;
                return "translate(" + w + "," + height + ")";
            });

    keys.append("rect")
        .attr("width","10px")
        .attr("height","10px")
        .attr("fill",function(d) { return color(d); });

    keys.append("text")
        .text(function(d) { return d; })
        .attr("dx","20px")
        .attr("dy","10px");  

}

function initialize(id) {
    var dim = calculateDimensions();
    createValueOverTimeViz(id, "body", dim.width, dim.height, 600, 400);
}

function createValueOverTimeViz(id, positionId, width, height, diagramWidth, diagramHeight) {
    var functionCtr, svg, menu;

    functionCtr = 0;
    menu = d3.select("#"+positionId)
        .append("div")
        .attr("id",positionId+"-menu");
    d3.select("#"+positionId)
        .append("div")
        .attr("id", positionId + "-viz" );

    createMenu(menu, id);
    svg = createCanvas("#" + positionId + "-viz", width, height);

    setInterval(function() {
        $.getJSON("formula", {
            sessionId : id,
            getFormula : false
        }, function(res) {
            if(res.count !== functionCtr) {
                refresh(svg, id, diagramWidth, diagramHeight);
                functionCtr = res.count;
            };
        });
    }, 300);
}

function refresh(svg, id, w, h) {
    svg.selectAll(".axis").remove();
    svg.selectAll(".connection").remove();
    svg.selectAll(".key").remove();
    svg.selectAll(".button").remove();

  	$.getJSON("formula", {
        sessionId : id,
        getFormula : true
  	}, function(res) {
        functionCtr = res.count;
        if(res.data !== "") {
            res.data.mode = res.mode;
            doIt(id, svg, res.data, res.xLabel, w, h);
            applyStyling(res.styling);
        };
    });
}

function createMenu(menu, id) {
    menu.append("label")
        .text("Add Expression: ");

    menu.append("input")
        .attr("type","text")
        .attr("id","new_expr");
        
    menu.append("input")
        .attr("type","button")
        .attr("value","Submit")
        .on("click", function() {
                $.getJSON("formula", {
                    sessionId : id,
                    cmd : "add_formula",
                    param : $("#new_expr").val()
                });
            });
}
