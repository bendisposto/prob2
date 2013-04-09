var m = [20, 120, 20, $(window).width()],
    w = 1280 - m[1] - m[3],
    h = 800 - m[0] - m[2],
    i = 0,
    root;

var tree = d3.layout.tree()
    .size([h, w]);

var diagonal = d3.svg.diagonal()
    .projection(function(d) { return [d.y, d.x]; });

var vis = d3.select("#body").append("svg:svg")
    .attr("width", w + m[1] + m[3])
    .attr("height", h + m[0] + m[2])
  .append("svg:g")
    .attr("transform", "translate(" + 25 + "," + m[0] + ")");  

var nodeLength = {};
var valueLength = {};

function buildTree(treeData)
{
    root = treeData
    root.x0 = h / 2;
    root.y0 = 0;

    var labels = {};
    var values = {};

    calculateSize(root);

    update(root);
}

// static calculation of size of labels
function calculateSize(data) {
  var toCalc = vis.selectAll("g.node")
      .data(tree.nodes(root).reverse());

  var l = toCalc.enter().append("svg:text")
        .attr("class","l")
        .attr("font-size","11px")
        .text(function(d) {return d.name});

  var v = toCalc.enter().append("svg:text")
        .attr("class","v")
        .attr("font-size","11px")
        .text(function(d) {return d.value});

  var toDel = $(".l");

  for (var i = 0 ; i < toDel.length; i++) {
    nodeLength[toDel[i].textContent] = toDel[i].getBBox().width;
  };

  toDel = $(".v");
    for (var i = 0 ; i < toDel.length; i++) {
    valueLength[toDel[i].textContent] = toDel[i].getBBox().width;
  };

  l.remove();
  v.remove();
}

function update(source) {
  var duration = d3.event && d3.event.altKey ? 5000 : 500;

  // Compute the new tree layout.
  var nodes = tree.nodes(root).reverse();

  var calcWidth = function(key) {
    var labelL = nodeLength[key.name];
    var valueL = valueLength[key.value];
    if( labelL >= valueL) {
      return labelL;
    } 
    return valueL;
  };

  var hasChildren = function(d) {
    return d.children || d._children;
  };

  var calcColor = function(d) {
    if(hasChildren(d)) {
      return colorMain(d);
    } else {
      if(d.value === true) {
        return "#A6F1A6";
      } else if(d.value === false) {
        return "#F39999";
      } else {
        return "#fff";
      };
    };
  };

  var colorMain = function(d) {
      if(d.value === true) {
        return "#73BE73";
      } else if(d.value === false) {
        return "#C06666";
      } else {
        return "#CCC";
      };
  };

  // Normalize for fixed-depth.
  nodes.forEach(function(d) { d.y = d.depth * 180 + calcWidth(root); });

  // Update the nodes…
  var node = vis.selectAll("g.node")
      .data(nodes, function(d) { return d.id || (d.id = ++i); });

  // Enter any new nodes at the parent's previous position.
  var nodeEnter = node.enter().append("svg:g")
      .attr("class", "node")
      .attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; })
      .on("click", function(d) { toggle(d); update(d); });

  nodeEnter.append("svg:rect")
                .attr("width",function(d) { return calcWidth(d)+20; })
                .attr("height",60)
                .attr("y",-30)
                .attr("x", function(d) { return hasChildren(d) ? -(calcWidth(d)+20) : 0; })
                .attr("rx",10)
                .style("fill", function(d) { return calcColor(d); })
                .style("stroke", function(d) { return colorMain(d); })
                .style("stroke-width", 1e-6);

  nodeEnter.append("svg:text")
      .attr("class","label")
      .attr("x", function(d) { return hasChildren(d) ? -10 : 10; })
      .attr("dy", "-1em")
      .attr("text-anchor", function(d) { return hasChildren(d) ? "end" : "start"; })
      .text(function(d) { return d.name; })
      .style("fill-opacity", 1e-6);

  nodeEnter.append("svg:text")
      .attr("class","value")
      .attr("x", function(d) { return hasChildren(d) ? -10 : 10; })
      .attr("dy", "1em")
      .attr("text-anchor", function(d) { return hasChildren(d) ? "end" : "start"; })
      .text(function(d) { return d.value; })
      .style("fill-opacity", 1e-6)

  // Resize Rectangles to fit text



 // $(".rL").attr("width",$(".tL").width()+20);
 // $(".rL").attr("height",$(".tL").height()*2);
 // $(".rL").attr("y",-($(".rL").attr("height")/2));
 // $(".rL").attr("x",-($(".rL").attr("width")));

  // Transition nodes to their new position.
  var nodeUpdate = node.transition()
      .duration(duration)
      .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });

  nodeUpdate.select("rect")
      .style("fill-opacity", 0.7)
      .style("stroke-width", "2px");

  nodeUpdate.select("text.label")
      .style("fill-opacity", 1);

  nodeUpdate.select("text.value")
      .style("fill-opacity", 1);

  // Transition exiting nodes to the parent's new position.
  var nodeExit = node.exit().transition()
      .duration(duration)
      .attr("transform", function(d) { return "translate(" + source.y + "," + source.x + ")"; })
      .remove();

  nodeExit.select("rect")
      .attr("fill-opacity", 1e-6)
      .style("stroke-width", 1e-6);

  nodeExit.select("text.label")
      .style("fill-opacity", 1e-6);

  nodeExit.select("text.value")
      .style("fill-opacity", 1e-6);

  // Update the links…
  var link = vis.selectAll("path.link")
      .data(tree.links(nodes), function(d) { return d.target.id; });

  // Enter any new links at the parent's previous position.
  link.enter().insert("svg:path", "g")
      .attr("class", "link")
      .attr("d", function(d) {
        var o = {x: source.x0, y: source.y0};
        return diagonal({source: o, target: o});
      })
    .transition()
      .duration(duration)
      .attr("d", diagonal);

  // Transition links to their new position.
  link.transition()
      .duration(duration)
      .attr("d", diagonal);

  // Transition exiting nodes to the parent's new position.
  link.exit().transition()
      .duration(duration)
      .attr("d", function(d) {
        var o = {x: source.x, y: source.y};
        return diagonal({source: o, target: o});
      })
      .remove();

  // Stash the old positions for transition.
  nodes.forEach(function(d) {
    d.x0 = d.x;
    d.y0 = d.y;
  });

  vis.selectAll("node")
}

// Toggle children.
function toggle(d) {
  if (d.children) {
    d._children = d.children;
    d.children = null;
  } else {
    d.children = d._children;
    d._children = null;
  }
}

function displayFormula() {
    var line = $("#formula")[0].value;
	$.getJSON("predicate_exp", {
		formula : line
	}, buildTree);
}
