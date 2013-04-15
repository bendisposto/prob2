
var w = 600;
var h = 400;
d3.select("#body").append("svg:svg");
var svg = d3.select("svg")
      .attr("width",w)
      .attr("height",h);

var padding = 10;

function doIt(dataset) {

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

var functionCtr = 0;

function initialize(id) {

  setInterval(function() {
    $.getJSON("formula", {
      sessionId : id,
      getFormula : false
    }, function(res) {
      if(res.count != functionCtr) {
        refresh(id);
      };
    });
  }, 300);

}


function refresh(id) {
  svg.selectAll(".axis").remove();
  svg.selectAll(".connection").remove();
  svg.selectAll(".point").remove();

	$.getJSON("formula", {
		sessionId : id,
    getFormula : true
	}, function(res) {
    functionCtr = res.count;
    if(res.data !== "") {
      doIt(res.data);
    };
  });
}
