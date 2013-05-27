function doIt(svg, dataset, xLabel, w, h) {
    var i,j;
    var padding = 10;

    var xMax = 0;
    var yMax = 0;
    var xMin = 99999999;
    var temp = 0;
    var color = d3.scale.category20();

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
            if( data[j].type === "BOOL" ) {
                data[j].value = data[j].value * yMax;
            }
        }
    }

    var elementNames = [];
    for( i = 0 ; i < dataset.length ; i = i + 1 ) {
        elementNames.push(dataset[i].name);
    }

    color.domain(elementNames);

    var xScale = d3.scale.linear().domain([xMin, xMax]).range([4*padding, w-padding]);
    var yScale = d3.scale.linear().domain([0, yMax]).range([h-4*padding, padding]);

    var xAxis = d3.svg.axis()
                    .scale(xScale)
                    .orient("bottom");
                    
    var yAxis = d3.svg.axis()
                    .scale(yScale)
                    .orient("left").ticks(2);

    var line = d3.svg.line().x(function(d){return xScale(d.t)}).y(function(d){ return yScale(d.value) });

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
        

 /*   paths.append("text")
        .datum(function(d) { return {name: d.name, value: d.dataset[d.dataset.length - 1]}; })
        .attr("transform", function(d) { return "translate(" + xScale(d.value.t) + "," + yScale(d.value.value) + ")"; })
        .attr("dy","5px")
        .attr("dx","5px")
        .text(function(d) { return d.name; });*/

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


  /*  svg.selectAll("circle").data(dataset).enter()
        .append("circle")
        .attr("class","point")
        .attr("r",2)
        .attr("cx",function(d) { return xScale(d.t);  })
        .attr("cy",function(d) { return yScale(d.value);});*/
  

}

function initialize(id) {
    var dim = calculateDimensions();
    init(id, "body", dim.width, dim.height, 600, 400);
}

function init(id, positionId, width, height, diagramWidth, diagramHeight) {
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
  //  svg.selectAll(".point").remove();

  	$.getJSON("formula", {
        sessionId : id,
        getFormula : true
  	}, function(res) {
        functionCtr = res.count;
        if(res.data !== "") {
            doIt(svg, res.data, res.xLabel, w, h);
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
