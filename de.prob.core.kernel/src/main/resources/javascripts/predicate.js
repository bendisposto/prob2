function buildTree(vis, tree, treeData, height) {
    var root, labels, values, i, diagonal;
    vis.selectAll(".link").remove();
    vis.selectAll(".node").remove();

    root = treeData
    root.x0 = height / 2;
    root.y0 = 0;
    i = 0;
    root.calculated = false;
    diagonal = d3.svg.diagonal()
                        .projection(function(d) { return [d.y, d.x]; });

    labels = {};
    values = {};

    update(vis, tree, root, root, i, diagonal);
}

function update(vis, tree, root, source, i, diagonal) {

    var duration = d3.event && d3.event.altKey ? 5000 : 500;

    // Compute the new tree layout.
    var nodes = tree.nodes(root).reverse();

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
    

    // Update the nodes…
    var node = vis.selectAll("g.node")
        .data(nodes, function(d) { return d.id || (d.id = ++i); });

    // Enter any new nodes at the parent's previous position.
    var nodeEnter = node.enter().append("svg:g")
        .attr("class", "node")
        .attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; })
        .on("click", function(d) { toggle(d); update(vis, tree, root, d, i, diagonal); })
        .attr("id", function(d) { return "node" + d.fId; });

    nodeEnter.append("svg:rect")
                .attr("height",60)
                .attr("y",-30)
                .attr("rx",10)
                .attr("id",function(d) { return "r"+d.fId})
                .style("fill", function(d) { return calcColor(d); })
                .style("stroke", function(d) { return colorMain(d); })
                .style("stroke-width", 1e-6);

    nodeEnter.append("svg:text")
        .attr("class","label")
        .attr("x", function(d) { return hasChildren(d) ? -10 : 10; })
        .attr("dy", "-1em")
        .attr("text-anchor", function(d) { return hasChildren(d) ? "end" : "start"; })
        .text(function(d) { return d.name; })
        .style("fill-opacity", 1e-6)
        .attr("id", function(d) {
            var textW = this.getBBox().width;
            var newX = hasChildren(d) ? -(textW+20) : 0;
            d3.select("#r"+d.fId)
                .attr("width",textW+20)
                .attr("x",newX);
            d["tW"] = textW;
            return "t"+d.fId;
        });

    nodeEnter.append("svg:text")
        .attr("class","value")
        .attr("x", function(d) { return hasChildren(d) ? -10 : 10; })
        .attr("dy", "1em")
        .attr("text-anchor", function(d) { return hasChildren(d) ? "end" : "start"; })
        .text(function(d) { return d.value; })
        .style("fill-opacity", 1e-6)
        .attr("id",function(d) {
            var textW = this.getBBox().width;
            if(textW > d.tW) {
              var newX = hasChildren(d) ? -(textW+20) : 0;
              d3.select("#r"+d.fId)
                .attr("width",textW+20)
                .attr("x",newX);
              d["tW"] = textW;
            }
            return "v"+d.fId;
        });

    var main = root.name;
    if(main.indexOf("&") !== -1 && !root.calculated) {
        var rootName = main.split("&");
        for( i = 0 ; i < rootName.length - 1; i = i + 1 ) {
            rootName[i] = rootName[i] + "&";
        }
        var boxH = 30 + rootName.length * 15;
        var upperLimit = -(boxH / 2);
        var rootNode = vis.select("#node" + root.fId);

        var rootRect = rootNode.select("#r"+root.fId)
            .attr("height", 30 + rootName.length * 15)
            .attr("y", upperLimit);

        rootNode.selectAll(".label").remove();
        rootNode.select(".value").remove();

        var textW = 0;
        rootNode.selectAll("text")
                .data(rootName)
                .enter()
               .append("text")
                .attr("class","label")
                .attr("x", -10)
                .attr("dy", function(d) { return upperLimit + 15 + rootName.indexOf(d) * 15; })
                .attr("text-anchor", "end" )
                .text(function(d) { return d; })
                .style("fill-opacity", 1e-6)
                .attr("id", function(d) {
                    var tW = this.getBBox().width;
                    if( tW > textW ) {
                        textW = tW;
                    }
                    return "t"+ root.fId + rootName.indexOf(d) ;
                });
        console.log(textW);

        rootNode.append("svg:text")
            .attr("class","value")
            .attr("x", -10)
            .attr("dy", boxH + upperLimit - 8)
            .attr("text-anchor","end")
            .text(root.value)
            .style("fill-opacity", 1e-6)
            .attr("id",function(d) {
                var tW = this.getBBox().width;
                if(tW > textW) {
                  textW = tW;
                }
                return "v" + root.fId;
            });

        root.tW = textW;
        rootRect.attr("width", textW + 20)
                .attr("x", -(textW + 20));
        root.calculated = true;
    }

    nodes.forEach(function(d) { d.y = d.depth * 180 + 40 + root.tW; });

    // Transition nodes to their new position.
    var nodeUpdate = node.transition()
        .duration(duration)
        .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });

    nodeUpdate.select("rect")
        .style("fill-opacity", 0.7)
        .style("stroke-width", "2px");

    nodeUpdate.selectAll("text.label")
        .style("fill-opacity", 1);

    nodeUpdate.selectAll("text.value")
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

    vis.selectAll("node");
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

function initialize(id) {
    var dim = calculateDimensions();
    createFormulaViz(id,"body", dim.width, dim.height);
};

function createFormulaViz(id, positionId, width, height) {
    var functionCtr, vis, tree;
    functionCtr = 0;
    vis = createCanvas("#" + positionId, width, height);
    tree = d3.layout.tree()
            .size([height, width]);

    var refreshId = setInterval(function() {

        $.getJSON("predicate", {
            sessionId : id,
            getFormula : false
        }, function(res) {
            if(res.count !== functionCtr) {
              refresh(id, vis, tree, height);
              functionCtr = res.count;
            };
        });

    }, 300);
}

function refresh(id, vis, tree, height) {

    $.getJSON("predicate", {
        sessionId : id,
        getFormula : true
    }, function(res) {
        if(res.data !== "") {
            data = res.data;
            buildTree(vis, tree, res.data, height);  
            applyStyling(res.styling);   
        };
    });
}