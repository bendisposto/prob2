function doIt(svg, dataset, w, h) {
    var padding = 10;

    var xScale = d3.scale.linear().domain([0, d3.max(dataset, function(d) { return d.t; })]).range([4*padding, w-padding]);
    var yScale = d3.scale.linear().domain([0, d3.max(dataset, function(d) { return d.value; })]).range([h-4*padding, padding]);

    var xAxis = d3.svg.axis()
                    .scale(xScale)
                    .orient("bottom");
                    
    var yAxis = d3.svg.axis()
                    .scale(yScale)
                    .orient("left").ticks(2);
                    
  

    var line = d3.svg.line().x(function(d){return xScale(d.t)}) .y(function(d){ return yScale(d.value) });

    svg.append("g")
        .attr("class", "axis")
        .attr("transform", "translate("+0+"," + (h - 2*padding) + ")")
        .call(xAxis)
      .append("text")
        .attr("class", "label")
        .attr("x", w)
        .attr("y", -6)
        .style("text-anchor", "end")
        .text("Number of Animation Steps");
   
    svg.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(" + 3*padding + ",0)")
        .call(yAxis);   

    svg.append("path")
        .attr("d", line(dataset))
        .attr("class","connection");

    svg.selectAll("circle").data(dataset).enter()
        .append("circle")
        .attr("class","point")
        .attr("r",2)
        .attr("cx",function(d) { return xScale(d.t);  })
        .attr("cy",function(d) { return yScale(d.value);});
  

}

function initialize(id) {
    var dim = calculateDimensions();
    init(id, "body", dim.width, dim.height, 600, 400);
}

function init(id, positionId, width, height, diagramWidth, diagramHeight) {
    var functionCtr, svg;

    var functionCtr = 0;
    var svg = createCanvas("#"+positionId,width,height);

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
    svg.selectAll(".point").remove();

  	$.getJSON("formula", {
        sessionId : id,
        getFormula : true
  	}, function(res) {
        functionCtr = res.count;
        if(res.data !== "") {
            doIt(svg, res.data, w, h);
            applyStyling(res.attrs);
        };
    });
}
