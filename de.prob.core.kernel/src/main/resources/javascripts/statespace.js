var width = 1500,
    height = 1000;

var force = d3.layout.force()
    .charge(-250)
    .linkDistance(120)
    .size([width, height]);

var svg = d3.select("#body").append("svg")
    .attr("width",width)
    .attr("height", height);

function buildGraph(data) {
    force
        .nodes(data.nodes)
        .links(data.links)
        .start();

    var link = svg.selectAll(".link")
          .data(data.links)
        .enter().append("line")
          .attr("class", "link")
          .style("stroke-width", function(d) { return 2; });

    var node = svg.selectAll(".node")
          .data(data.nodes)
        .enter().append("g")
          .attr("class", "node")
          .call(force.drag);

    node.append("circle")
      .attr("r",20)
      .attr("id",function(d) { return "n"+d.id});

    node.append("text")
      .attr("dy", ".35em")
      .attr("text-anchor","middle")
      .text(function(d) {return d.name; });

    node.append("title")
      .text(function(d) { return d.name; });

    var safety = 0;
    while(force.alpha() > 0) {
      force.tick();
      if(safety++ > 5000) {
        break;
      }
    };

    force.resume();
    force.on("tick", function() {
      link.attr("x1", function(d) { return d.source.x; })
          .attr("y1", function(d) { return d.source.y; })
          .attr("x2", function(d) { return d.target.x; })
          .attr("y2", function(d) { return d.target.y; });

      node.attr("transform", function(d) { return "translate("+d.x+","+d.y+")"});
    });

}


var ssCtr = 0;

function initialize(id) {
// setup output polling
setInterval(function() {
	
  	$.getJSON("statespace_servlet", {
  		sessionId : id,
      getSS : false
  	}, function(res) {
      if(res.count !== ssCtr) {
        refresh(id);
      };
    });
	

  }, 300);
};

function refresh(id) {
  svg.selectAll(".link").remove();
  svg.selectAll(".node").remove();


  $.getJSON("statespace_servlet", {
    sessionId : id,
    getSS : true
  }, function(res) {
    ssCtr = res.count;
    if(res.data !== "") {
      buildGraph(res.data);
    };
  });
}
