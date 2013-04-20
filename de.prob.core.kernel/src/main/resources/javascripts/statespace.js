var width, height;

function calculateDimensions() {
  if( typeof( window.innerWidth ) == 'number' ) { width = window.innerWidth; height = window.innerHeight; } // NORMAL BROWSERS 
  else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) { 
    width = document.documentElement.clientWidth; height = document.documentElement.clientHeight; } // IE6+ 
  else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) { 
    width = document.body.clientWidth; height = document.body.clientHeight; }
}

calculateDimensions();
var force = d3.layout.force()
    .charge(-250)
    .linkDistance(160)
    .size([width, height]);

var svg = d3.select("#body").append("svg:svg")
    .attr("width",width)
    .attr("height",height)
    .attr("pointer-events","all")
  .append("svg:g")
    .call(d3.behavior.zoom().on("zoom", redraw))
  .append("svg:g");

svg.append("svg:rect")
    .attr("class","canvas")
    .attr("width",width)
    .attr("height",height)
    .style("fill-opacity",1e-6);

function redraw() {
  svg.attr("transform","translate("+d3.event.translate+")"+ " scale("+d3.event.scale+")");
}

var nodes = [];
var links = [];

function buildGraph(attrs) {
    force
        .nodes(nodes)
        .links(links)
        .start();

    // build the arrow
    svg.append("svg:defs").selectAll("marker")
          .data(["end"])
        .enter().append("svg:marker")
          .attr("id", String)
          .attr("viewBox", "0 -5 10 10")
          .attr("refX", 32)
          .attr("refY", -1.5)
          .attr("markerWidth", 6)
          .attr("markerHeight", 6)
          .attr("orient", "auto")
        .append("svg:path")
          .attr("d", "M0,-5L10,0L0,5");


    var path = svg.append("svg:g").selectAll("path")
          .data(links)
        .enter().append("svg:path")
          .attr("class", "link")
          .style("marker-end", "url(#end)");

    var node = svg.selectAll(".node")
          .data(nodes)
        .enter().append("g")
          .attr("class", "node")
          .attr("id",function(d) {return "n"+d.id})
          .call(force.drag);

    node.append("circle")
      .attr("r",20)
      .attr("id",function(d) { return "c"+d.id});

    node.append("text")
      .attr("dy", ".35em")
      .attr("text-anchor","middle")
      .attr("id", function(d) { return "t"+d.id})
      .text(function(d) {return d.name; });

    node.append("title")
      .text(function(d) { return d.name; });

    for (var i = 0; i < attrs.length; i++) {
      var selected = svg.selectAll(attrs[i].selector);
      var attributes = attrs[i].attributes;
      for (var j = 0; j < attributes.length; j++) {
        selected.attr(attributes[j].name,attributes[j].attr);
      };
    };

    if(nodes.length > 50) {
      var safety = 0;
      while(force.alpha() > 0) {
        force.tick();
        if(safety++ > 5000) {
          break;
        }
      };      
    };


    force.resume();
    force.on("tick", function() {
      path.attr("d", function(d) {
        var dx = d.target.x - d.source.x,
            dy = d.target.y - d.source.y,
            dr = Math.sqrt(dx * dx + dy * dy);
        return "M" + 
            d.source.x + "," + 
            d.source.y + "A" + 
            dr + "," + dr + " 0 0,1 " + 
            d.target.x + "," + 
            d.target.y;
    });

      node.attr("transform", function(d) { return "translate("+d.x+","+d.y+")"});
    });


}


var ssCtr = 0;

function initialize(id) {
// setup output polling
setInterval(function() {
	
  	$.getJSON("statespace_servlet", {
  		sessionId : id,
      getSS : false,
      getAll : false
  	}, function(res) {
      if(res.count !== ssCtr) {
        refresh(id, ssCtr === 0);
      };
    });
	

  }, 300);
};

function refresh(id,getAllStates) {
  svg.selectAll(".link").remove();
  svg.selectAll(".node").remove();


  $.getJSON("statespace_servlet", {
    sessionId : id,
    getSS : true,
    getAll : getAllStates
  }, function(res) {
    ssCtr = res.count;
    if(res.data !== "") {
      var n = res.data.nodes
      var l = res.data.links

      for (var i = 0; i < n.length; i++) {
        var node = n[i];
        var index = node.parentIndex;
        if(index !== -1 && index < nodes.length) {
          var tN = nodes[index];
          if(typeof(tN.x) !== 'undefined') {
            node["x"] = tN.x;
            node["y"] = tN.y;
          };
        };
        nodes.push(n[i]);
      };
      for (var i = 0; i < l.length; i++) {
        links.push(l[i]);
      };
      buildGraph(res.attrs);
    };
  });
}
