function createSSGraph(id,positionId,m,events,width,height) {
    var links, nodes, linkMap, menu, mode, force, ssCtr, svg, stopped, elements;
    mode = m;
    ssCtr = 0;
    menu = d3.select("#"+positionId)
        .append("div")
        .attr("id",positionId+"-menu");
    d3.select("#"+positionId)
        .append("div")
        .attr("id",positionId+"-viz");

    stopped = {value: false};
    calculateHeader(menu, id, mode, stopped,events);
    force = d3.layout.force()
              .charge(-500)
              .linkDistance(160)
              .size([width, height]);
    svg = createCanvas("#"+positionId+"-viz",width,height);
    nodes = [];
    links = [];
    linkMap = d3.map();


    // setup output polling
    setInterval(function() {
        $.getJSON("statespace_servlet", {
            sessionId : id,
            getSS : false,
            getAll : false
        }, function(res) {
            if (res.mode !== mode) {
                mode = res.mode;
                calculateHeader(menu, id, res.mode, stopped, res.events);
            }
            if (res.count !== ssCtr) {
                refresh(svg, id, ssCtr === 0 || res.reset, force, stopped);
                ssCtr = res.count;
            }
        });
    }, 300);


    function buildGraph(svg, force, n, stopped) {
        force
            .nodes(nodes)
            .links(links)
            .start();

        var l, path, text, transLabels, node, boxH, cycleGen, i, safety;

        l = svg.append("svg:g");

        path = l.selectAll("path")
                .data(links)
              .enter().append("svg:path")
                .attr("class", "link")
                .attr("id", function(d) { return "arc" + d.id; })
                .style("stroke", function(d) { return d.color; });

        text = l.selectAll("text")
                .data(links)
              .enter().append("svg:text")
                .attr("class", "linkT")
                .attr("text-anchor", "start")
                .attr("dx", "30")
                .attr("font-size", "3px")
                .attr("id", function(d) { return "lt" + d.id; });

        transLabels = text.append("textPath")
                  .attr("xlink:href", function(d) { return "#arc" + d.id; })
                  .text(function(d) { return d.name; });

        node = svg.selectAll(".node")
              .data(nodes)
            .enter().append("g")
              .attr("class", "node")
              .attr("id", function(d) { return "n" + d.id; })
              .call(force.drag);

        boxH = n * 5 + 5;

        node.append("rect")
            .attr("width", "40")
            .attr("height", boxH.toString())
            .attr("rx", "5")
            .attr("ry", "5")
            .attr("id", function(d) { return "r" + d.id; });

        for (i = 0; i < n; i += 1) {
            node.append("text")
                .attr("stroke-width", "0px")
                .attr("class", "nText")
                .attr("text-anchor", "middle")
                .attr("dy", function() { return (5 * (i + 1)).toString(); })
                .attr("font-size", "3px")
                .attr("fill", "white")
                .text(function(d) { return d.vars[i]; })
                .attr("id", function(d) { return "nt" + d.id; });
        }

        d3.selectAll(".nText")
            .attr("dx", function(d) {
                var textW = this.getBBox().width;
                if (textW > 35) {
                    d3.select("#r" + d.id).attr("width", (textW + 5).toString());
                    return Math.round((textW - 35) / 2 + 20).toString();
                }
                return "20";
            });

        node.append("title")
            .text(function(d) { return d.name; });

        if (nodes.length > 50) {
            safety = 0;
            while (force.alpha() > 0) {
                force.tick();
                safety = safety + 1;
                if (safety > 5000) {
                    break;
                }
            }
        }

        cycleGen =  d3.svg.line()
                              .x(function(d) {return d.x; })
                              .y(function(d) {return d.y; })
                              .interpolate("basis-closed");

        force.resume();
        force.on("tick", function() {
            if (!stopped.value) {
                path.attr("d", function(d) {
                    var factor, linedata, dx, dy, dr;
                    if (d.loop) {
                        d3.select("#lt" + d.id).attr("dx", 80);
                        factor = 1 + d.lNr * 0.1;
                        linedata = [
                            {x: d.source.x - 10, y: d.source.y + 10},
                            {x: d.source.x + 40 * factor, y: d.source.y - 20 * factor},
                            {x: d.source.x + 50 * factor, y: d.source.y + 10 * factor}
                        ];
                        return cycleGen(linedata);
                    }
                    dx = d.target.x - d.source.x;
                    dy = d.target.y - d.source.y;
                    dr = Math.sqrt(dx * dx + dy * dy) * (1 - d.lNr * 0.1);
                    return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
                });

                transLabels.attr("xlink:href", function(d) { return "#arc" + d.id; });

                node.attr("transform", function(d) {
                    var nx = d.x - 20, ny = d.y - boxH / 2;
                    return "translate(" + nx + "," + ny + ")";
                });
            }
        });
    }

    function drawDotty(svg, content) {
        svg.append("svg:rect")
            .attr("class", "canvas")
            .attr("id", "toReplace")
            .attr("height", height)
            .attr("width", width)
            .style("fill-opacity", 1e-6);

        var m = Viz(content, "svg");
        $("#toReplace").replaceWith(m);
    }

    function forD3(svg, res, force, stopped) {
        var n, l, node, index, tN, entry, linkNr, loop, i, varCount;
        n = res.data.nodes;
        l = res.data.links;

        for (i = 0; i < n.length; i = i + 1) {
            node = n[i];
            index = node.parentIndex;
            if (index !== -1 && index < nodes.length) {
                tN = nodes[index];
                if (tN.x !== undefined) {
                    node.x = tN.x;
                    node.y = tN.y;
                }
            }
            nodes.push(n[i]);
        }
        for (i = 0; i < l.length; i = i + 1) {
            entry = l[i].source + "-" + l[i].target;
            linkNr = 1;
            if (linkMap.has(entry)) {
                linkNr = linkMap.set(entry, linkMap.get(entry) + 1);
            } else {
                linkMap.set(entry, 1);
            }

            loop = l[i].source === l[i].target;

            l[i].lNr = linkNr;
            l[i].loop = loop;
            links.push(l[i]);
        }
        varCount = res.varCount;
        stopped.value = false;
        buildGraph(svg, force, varCount, stopped);
    }

    function refresh(svg, id, getAllStates, force, stopped) {
        if (getAllStates) {
            nodes = [];
            links = [];
            linkMap = d3.map();
        }

        svg.selectAll(".link").remove();
        svg.selectAll(".node").remove();
        svg.selectAll(".linkT").remove();
        svg.selectAll("g svg").remove();


        $.getJSON("statespace_servlet", {
            sessionId : id,
            getSS : true,
            getAll : getAllStates
        }, function(res) {

            if (res.data !== "") {
                if (res.mode > 3) {
                    if (res.data.content !== "") {
                        drawDotty(svg, res.data.content);
                    }
                } else {
                    forD3(svg, res, force, stopped);
                }
                applyStyling(res.data.styling);
            }
        });
    }
}

function doCmd(id, cmd, enabled) {
    var p = "";
    if (cmd === "trans_diag" || cmd === "d_trans_diag") {
        p = prompt("Input a B Expression", "Input the expression here");
    }
    if (cmd === "sig_merge" || cmd === "d_sig_merge") {
        // may need to import json2.js to support IE6/7
        p = JSON.stringify(enabled);
    }

    $.getJSON("statespace_servlet", {
        sessionId : id,
        cmd : cmd,
        param : p
    });
}

function calculateHeader(menu, id, m, stopped, checks) {
    var cmds, pause, list, menu2, settings;

    d3.selectAll(".menuOps").remove();
    cmds = [
        {name: "Original State Space", cmd: "org_ss", id: 1},
        {name: "Signature Merge", cmd: "sig_merge", id: 2},
        {name: "Transition Diagram", cmd: "trans_diag", id: 3},
        {name: "Signature Merge (dotty)", cmd: "d_sig_merge", id: 4},
        {name: "Transition Diagram (dotty)", cmd: "d_trans_diag", id: 5}
    ];

    menu2 = menu.append("ul").attr("class", "menuOps");
    pause = menu2.append("li");

    pause.selectAll("push")
          .data([stopped])
          .enter()
        .append("img")
          .attr("class", "push")
          .attr("src", function(d) { return d.value ? "../images/play-icon.png" : "../images/pause-icon.png"; })
          .attr("width", "20")
          .attr("height", "20")
          .attr("align", "top")
          .on("click", function(d) {
            var x = d.value;
            d.value = !x;
            d3.select(".push").attr("src", function(d) { return d.value ? "../images/play-icon.png" : "../images/pause-icon.png"; });
        });

    list = menu2.append("li")
                  .attr("class", "dropdown")
                .append("select")
                  .on("change", function() {doCmd(id, this.options[this.selectedIndex].__data__.cmd, checks); });

    list.selectAll("option")
        .data(cmds)
        .enter()
      .append("option")
        .attr("id", function(d) {return "op" + d.id; })
        .text(function(d) {return d.name; });

    list.select("#op"+m)
        .attr("selected",true);

    var createChecks = function() {
        var settingBox = menu2.append("li")
            .attr("class","settingBox")
            .append("fieldset");
        settingBox.append("legend")
            .text("Enabled Events:");
        var form = settingBox.append("form").append("ul");
        var element = form.selectAll("setting")
            .data(checks)
            .enter()
           .append("li");

        element.attr("class",function(d) {return d.checked ? "checked" : "unchecked"});

        element.append("input")
            .attr("type","checkbox")
            .attr("name","setting")
            .attr("id",function(d) { return d.name; })
            .attr("value",function(d) { return d.name; })
            .on("click",function(d) {
                if(d.checked) {
                    d.checked = false;
                } else {
                    d.checked = true;
                }
            });

        form.selectAll(".checked")
            .select("input")
            .attr("checked","true");

        element.append("label")
            .attr("for",function(d) { return d.name; })
            .text(function(d) {return d.name; });
        
        form.append("li").append("input")
            .attr("type","button")
            .attr("value","Submit")
            .on("click",function() {
                var command = m === 2 ? "sig_merge" : "d_sig_merge";
                doCmd(id, command, checks);
            });
    }

    var settingsOpen = {value: false};
    if(m === 2 || m === 4) {
        settings = menu2.append("li");
        settings.append("img")
            .attr("src","../images/settings.png")
            .attr("width", "20")
            .attr("height", "20")
            .on("click", function() {
                if(!settingsOpen.value) {
                    createChecks();
                    settingsOpen.value = true;
                } else {
                    menu2.select(".settingBox").remove();
                    settingsOpen.value = false;
                }
            });
    }
}

function initialize(id) {
    var dim = calculateDimensions();
    
    createSSGraph(id,"body",1,[],dim.width,dim.height);
}


