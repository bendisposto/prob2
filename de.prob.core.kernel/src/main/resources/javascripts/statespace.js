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
    .charge(-150)
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

function buildGraph(attrs,n) {
    force
        .nodes(nodes)
        .links(links)
        .start();

    var l = svg.append("svg:g");

    var path = l.selectAll("path")
          .data(links)
        .enter().append("svg:path")
          .attr("class", "link")
          .attr("id",function(d) { return "arc"+d.id })
          .style("stroke",function(d) { return d.color });

    var text = l.selectAll("text")
            .data(links)
          .enter().append("svg:text")
            .attr("text-anchor","start")
            .attr("dx","30")
            .attr("font-size","3px")
            .attr("id",function(d) { return "lt"+d.id});

    text.append("textPath")
              .attr("xlink:href",function(d) { return "#arc"+d.id })
              .text(function(d) {return d.name});

    var node = svg.selectAll(".node")
          .data(nodes)
        .enter().append("g")
          .attr("class", "node")
          .attr("id",function(d) {return "n"+d.id})
          .call(force.drag);

    var boxH = n*5+5;

    node.append("rect")
      .attr("width","40")
      .attr("height",boxH+"")
      .attr("rx","5")
      .attr("ry","5")
      .attr("id",function(d) { return "r"+d.id});

    for(var i = 0 ; i < n ; i++) {
      node.append("text")
        .attr("stroke-width","0px")
        .attr("class","nText")
        .attr("text-anchor","middle")
        .attr("dy",function(d) { return 5*(i+1)+"" })
        .attr("font-size","3px")
        .attr("fill","white")
        .text(function(d) {return d.vars[i]; })
        .attr("id", function(d) {return "nt"+d.id});
    }

    d3.selectAll(".nText")
        .attr("dx",function(d) {
          var textW = this.getBBox().width;
          if(textW > 35) {
            d3.select("#r"+d.id).attr("width",textW+5+"");
            return Math.round((textW - 35)/2 + 20) + "";
          }
          return "20";
        });

    node.append("title")
      .text(function(d) { return d.name; });

    for (var i = 0; i < attrs.length; i++) {
      var selected = svg.selectAll(attrs[i].selector);
      var attributes = attrs[i].attributes;
      for (var j = 0; j < attributes.length; j++) {
        selected.attr(attributes[j].name,attributes[j].value);
      };
      var styles = attrs[i].styles;
      for (var j = 0; j < styles.length; j++) {
        selected.style(styles[j].name,styles[j].value);
      }
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

    var cycleGen =  d3.svg.line()
                          .x(function(d) {return d.x; })
                          .y(function(d) {return d.y; })
                          .interpolate("basis-closed");

    force.resume();
    force.on("tick", function() {
      path.attr("d", function(d) {
        if(d.loop) {
          d3.select("#lt"+d.id).attr("dx",80);
          var factor = 1+d.lNr*0.1;
          var linedata = [
              {x: d.source.x-10, y: d.source.y+10},
              {x: d.source.x+40*factor, y: d.source.y-20*factor},
              {x: d.source.x+50*factor, y: d.source.y+10*factor}
          ];
          return cycleGen(linedata);
        } else {
          var dx = d.target.x - d.source.x,
              dy = d.target.y - d.source.y,
              dr =  Math.sqrt(dx * dx + dy * dy)*(1-d.lNr*0.1);
          return "M" + 
            d.source.x + "," + 
            d.source.y + "A" + 
            dr + "," + dr + " 0 0,1 " + 
            d.target.x + "," + 
            d.target.y;
          };
    });

      node.attr("transform", function(d) {var nx = d.x-20, ny = d.y-boxH/2; return "translate("+nx+","+ny+")"});
    });


}

var linkMap = d3.map();
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
        var entry = l[i].source+"-"+l[i].target;
        var linkNr = 1;
        if(linkMap.has(entry)) {
          linkNr = linkMap.set(entry,linkMap.get(entry)+1);
        } else {
          linkMap.set(entry,1);
        };

        var loop = l[i].source === l[i].target;

        l[i]["lNr"] = linkNr;
        l[i]["loop"] = loop;
        links.push(l[i]);

      };
      buildGraph(res.data.styling,res.varCount);
    };
  });
}
