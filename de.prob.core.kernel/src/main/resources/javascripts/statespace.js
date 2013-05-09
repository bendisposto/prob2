var width, height, force, svg;

function calculateDimensions() {
  if( typeof( window.innerWidth ) == 'number' ) { width = window.innerWidth; height = window.innerHeight; } // NORMAL BROWSERS 
  else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) { 
    width = document.documentElement.clientWidth; height = document.documentElement.clientHeight; } // IE6+ 
  else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) { 
    width = document.body.clientWidth; height = document.body.clientHeight; }
}

function init() {
  calculateDimensions();
  force = d3.layout.force()
    .charge(-500)
    .linkDistance(160)
    .size([width, height]);

  svg = d3.select("#body").append("svg:svg")
      .attr("class","background")
      .attr("viewBox","0 0 "+width+" "+height)
      .attr("pointer-events","all")
    .append("svg:g")
      .call(d3.behavior.zoom().on("zoom", redraw))
    .append("svg:g");


}

function redraw() {
  svg.attr("transform","translate("+d3.event.translate+")"+ " scale("+d3.event.scale+")");
}


var nodes = [];
var links = [];
var stopped = {value:false};

function buildGraph(attrs,n) {
    force
        .nodes(nodes)
        .links(links)
        .start();

    svg.append("svg:rect")
        .attr("class","canvas")
        .attr("height",height)
        .attr("width",width)
        .style("fill-opacity",1e-6);

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
            .attr("class","linkT")
            .attr("text-anchor","start")
            .attr("dx","30")
            .attr("font-size","3px")
            .attr("id",function(d) { return "lt"+d.id});

    var transLabels = text.append("textPath")
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
      if(selected !== "") {
        var attributes = attrs[i].attributes;
        for (var j = 0; j < attributes.length; j++) {
          selected.attr(attributes[j].name,attributes[j].value);
        };
        var styles = attrs[i].styles;
        for (var j = 0; j < styles.length; j++) {
          selected.style(styles[j].name,styles[j].value);
        }        
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
      if(!stopped.value) {
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

      transLabels.attr("xlink:href",function(d) { return "#arc"+d.id });

      node.attr("transform", function(d) {var nx = d.x-20, ny = d.y-boxH/2; return "translate("+nx+","+ny+")"});
    }});
}

function drawDotty(svg,content) {
  svg.append("svg:rect")
        .attr("class","canvas")
        .attr("id","toReplace")
        .attr("height",height)
        .attr("width",width)
        .style("fill-opacity",1e-6);

  var m = Viz(content, "svg");
  $("#toReplace").replaceWith(m);
}

var linkMap = d3.map();
var ssCtr = 0;
var mode = 1;
var sId;

function initialize(id) {
sId = id;
calculateHeader(id, mode);
init();
// setup output polling
setInterval(function() {
	
  	$.getJSON("statespace_servlet", {
  		sessionId : id,
      getSS : false,
      getAll : false
  	}, function(res) {
      if(res.mode !== mode) {
        calculateHeader(id,res.mode);
      }
      if(res.count !== ssCtr) {
        refresh(id, ssCtr === 0 || res.reset);
      };
    });
	

  }, 300);
};

function refresh(id,getAllStates) {
  if(getAllStates) {
    nodes = [];
    links = [];
    linkMap = d3.map();
  }

  svg.selectAll(".link").remove();
  svg.selectAll(".node").remove();
  svg.selectAll(".linkT").remove();
  svg.selectAll(".canvas").remove();


  $.getJSON("statespace_servlet", {
    sessionId : id,
    getSS : true,
    getAll : getAllStates
  }, function(res) {
    ssCtr = res.count;
    if(res.data !== "") {
      if(res.mode > 3) {
        if(res.data.content != "") {
          drawDotty(svg,res.data.content);
        };
      } else {
        forD3(res);
      }
    };
  });
}

function forD3(res) {
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
      styling = res.data.styling;
      varCount = res.varCount;
      stopped.value = false;
      buildGraph(styling,varCount);
}

function calculateHeader(id,m) {
  mode = m;
  d3.selectAll(".menuOps").remove();
  var cmds = [{name:"Original State Space",cmd:"org_ss",id:1},{name:"Signature Merge",cmd:"sig_merge",id:2},{name:"Transition Diagram",cmd:"trans_diag",id:3},{name:"Transition Diagram (dotty)",cmd:"d_trans_diag",id:4}];

  var menu = d3.select("#menu").append("ul").attr("class","menuOps");
  var pause = menu.append("li");

  pause.selectAll("push")
        .data([stopped])
        .enter()          
      .append("img")
        .attr("class","push")
        .attr("src",function(d) { return d.value ? "../images/play-icon.png" : "../images/pause-icon.png"})
        .attr("width","20")
        .attr("height","20")
        .attr("align","top")
        .on("click",function(d) { var x = d.value; d.value = !x; d3.select(".push").attr("src",function(d) { return d.value ? "../images/play-icon.png" : "../images/pause-icon.png"})});

  var list = menu.append("li")
                .attr("class","dropdown")
              .append("select")
                .on("change",function() {doCmd(id,this.options[this.selectedIndex].__data__.cmd)});

  list.selectAll("option")
      .data(cmds)
      .enter()
    .append("option")
      .attr("id",function(d) {return "op"+d.id})
      .text(function(d) {return d.name});

  list.select("#op"+m)
      .attr("selected",true);

}

function doCmd(id,cmd) {
  var p = "";
  if(cmd === "trans_diag" || cmd === "d_trans_diag") {
    p = prompt("Input a B Expression","Input the expression here");
  }

  $.getJSON("statespace_servlet", {
      sessionId : id,
      cmd : cmd,
      param : p
  });
}

function resize() {
  calculateDimensions();
  d3.select("#background")
    .attr("width",width)
    .attr("height",height);
  refresh(sId,false,mode);
}
