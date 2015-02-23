FormulaView = (function() {

    var extern = {}
    var session = Session();
    var vizUtils = VizUtils();
    var width = vizUtils.calculateWidth();
    var height =  vizUtils.calculateHeight() - $("#header")[0].clientHeight;
    var vis = vizUtils.createCanvas("#visualization", width, height);
    var tree = d3.layout.tree()
            .size([height, width]);
    var mode;
    var lastData;

    $(document).ready(function() {
        $(window).keydown(function(event){
            if(event.keyCode == 13) {
                event.preventDefault();
                return false;
            }
        });

        $(window).resize(function() {
            width = vizUtils.calculateWidth();
            h = vizUtils.calculateHeight() - $("#header")[0].clientHeight - 20;
            if(h != height) {
                height = h;
                $("#visualization").empty();
                vis = vizUtils.createCanvas("#visualization", width, height);
                tree = d3.layout.tree().size([height,width]);
                if(lastData != undefined) {
                    draw(lastData)
                }                
            }
        })
    });

    // UI Interaction
    $(".add-formula").click(function(e) {
        e.preventDefault();
        var id = e.target.parentNode.parentNode.id;
        session.sendCmd("setFormula", {});
    });

    $(".form-control").keyup(function(e) {
        session.sendCmd("parse", {
            "formula" : e.target.value
        })
    });

    function error(errormsg) {
        $(".alert").remove();
        $("#enter-formula").prepend(session.render("/ui/valueOverTime/error_msg.html",errormsg));
        $(".input-group").addClass("has-error");
    }

    function formulaSet(formula, unicode) {
        $(".alert").remove();
        $(".input-group").removeClass("has-error");
        
        $("#input-field").replaceWith(session.render("/ui/formulaView/formula_entered.html",{formula: unicode}));
        $("#edit-formula").click(function(e) {
            e.preventDefault();
            editFormula(formula);
        });
        $("#remove-formula").click(function(e) {
            e.preventDefault();
            session.sendCmd("removeFormula",{});
        });
    }

    function editFormula(formula) {
        $("#input-field").replaceWith(session.render("/ui/formulaView/input_field.html",{value: formula}));
        $("#input-button").click(function(e) {
            e.preventDefault();
            session.sendCmd("setFormula", {})
        });
        $(".form-control").keyup(function(e) {
            session.sendCmd("parse", {
                "formula" : e.target.value
            })
        });
    }

    function parseOk() {
        $(".input-group").removeClass("has-error");
        $("#input-button").prop("disabled",false);
    }

    function parseError() {
        $(".input-group").addClass("has-error");
        $("#input-button").prop("disabled",true);
    }

    function clear() {
        vis.selectAll(".link").remove();
        vis.selectAll(".node").remove();       
    }

    function draw(data) {
        lastData = data;
        var root, labels, values, i, diagonal;
        clear();

        if(data !== null) {
            root = data
            root.x0 = height / 2;
            root.y0 = 0;
            i = 0;
            root.calculated = false;
            diagonal = d3.svg.diagonal()
                                .projection(function(d) { return [d.y, d.x]; });

            labels = {};
            values = {};

            update(root, root, i, diagonal);            
        }
    }

    function update(root, source, i, diagonal) {
        var duration = d3.event && d3.event.altKey ? 5000 : 500;

        // Compute the new tree layout.
        var nodes = tree.nodes(root).reverse();

        var hasChildren = function(d) {
            return d.children || d._children;
        };

        var calcColor = function(d) {
            if (d.hasError === true) {
                return "#FFC1A8";
            } else if(d.value === true) {
                return "#A6F1A6";
            } else if(d.value === false) {
                return "#F0B6B6";
            } else {
                return "#fff";
            };
        };

        var colorMain = function(d) {
            if (d.hasError === true) {
                return "#FF8C00";
            } else if(d.value === true) {
                return "#73BE73";
            } else if(d.value === false) {
                return "#C06666";
            } else {
                return "#CCC";
            };
        };

        // Update the nodes…
        var node = vis.selectAll("g.node")
            .data(nodes, function(d) { return d.id || (d.id = ++i); });

        // Enter any new nodes at the parent's previous position.
        var nodeEnter = node.enter().append("svg:g")
            .attr("class", "node")
            .attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; })
            .on("click", function(d) { toggle(d); update(root, d, i, diagonal); })
            .attr("id", function(d) { return "node" + d.id; });

        nodeEnter.each(function(d) { d.width = 0 })

        var rects = nodeEnter.append("svg:rect")
            
        rects.attr("height",60)
            .attr("y",-30)
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
            .style("fill-opacity", 1e-6)
            .attr("id", function(d) {
                var textW = this.getBBox().width;
                if((textW + 20) > d.width) {
                    d.width = textW + 20
                }
                return "t"+d.id;
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
                if((textW + 20) > d.width) {
                    d.width = textW + 20
                }
                return "v"+d.id;
            });

        rects.attr("x", function(d) { return hasChildren(d) ? -(d.width) : 0})
            .attr("width", function(d) { return d.width })
            .attr("id",function(d) { return "r"+d.id})



        var maxWidths = []
        nodes.forEach(function(d) { if(maxWidths[d.depth] === undefined) {
            maxWidths[d.depth] = d.width + 25
        } else {
            maxWidths[d.depth] = (d.width > maxWidths[d.depth]) ? d.width : maxWidths[d.depth]
        }})
        var depths = (maxWidths.length > 0) ? [maxWidths[0]] : []
        for(var i = 1 ; i < maxWidths.length ; i++) {
            depths[i] = maxWidths[i] + depths[(i-1)] + 40
        }
        nodes.forEach(function(d) { d.y = depths[d.depth] });

        // Transition nodes to their new position.
        var nodeUpdate = node.transition()
            .duration(duration)
            .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });

        nodeUpdate.select("rect")
            .style("fill-opacity", 1)
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

    function toggle(d) {
        if (d.children) {
            d._children = d.children;
            d.children = null;
            session.sendCmd("collapseNode",{
                "formulaId" : d.id
            });
        } else {
            d.children = d._children;
            d._children = null;
            session.sendCmd("expandNode", {
                "formulaId" : d.id
            });
        }
    }
    
    function applyStyling(styling) {
        vizUtils.applyStyling(styling);
    }

    function disable() {
        $("body").append("<div class='modal-backdrop disabled'></div>")
    }

    function enable() {
        $(".disabled").remove()
    }

    extern.client = ""
    extern.init = session.init
    extern.error = function(data) {
        error(data);
    }
    extern.formulaSet = function(data) {
        formulaSet(data.formula, data.unicode);
        draw(JSON.parse(data.data));
    }
    extern.parseOk = parseOk;
    extern.parseError = parseError;
    extern.formulaRemoved = function() {
        editFormula("");
        draw(null);
    };
    extern.draw = function(data) {
        draw(JSON.parse(data.data));
    }
    extern.disable = disable
    extern.enable = enable

    return extern;
}())