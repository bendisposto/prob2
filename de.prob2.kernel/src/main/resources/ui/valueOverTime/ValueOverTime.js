ValueOverTime = (function() {

    var extern = {}
    var session = Session();
    var vizUtils = VizUtils();
    var w = 600
    var h = 450
    var svg = vizUtils.createCanvas("#visualization", w + 100, h);
    var mode;
    var lastData;
    var lastLabel;

    $(document).ready(function() {
        $(window).keydown(function(event){
            if(event.keyCode == 13) {
                event.preventDefault();
                return false;
            }
        });

       /* $(window).resize(function() {
            width = vizUtils.calculateWidth();
            h = vizUtils.calculateHeight() - $("#header")[0].clientHeight - 20;
            if(h != height) {
                height = h;
                $("#visualization").empty();
                vis = vizUtils.createCanvas("#visualization", $("#left-col")[0].clientWidth, height);
                if(lastData != undefined && lastLabel != undefined) {
                    draw(lastData, lastLabel)
                }                
            }
        })*/
    });

    $(".form-control").keyup(function(e) {
        session.sendCmd("parse", {
            "formula" : e.target.value,
            "id" : e.target.parentNode.id
        })
    });

    $(".add-formula").click(function(e) {
        e.preventDefault();
        var id = e.target.parentNode.parentNode.id;
        session.sendCmd("addFormula", {
            "id" : id,
            "newFormula" : true
        });
    });

    $("#btn-time").click(function(e) {
        e.preventDefault();
        session.sendCmd("addFormula", {
            "id" : "time",
            "newFormula" : true
        })
    });

    function timeSet(formula) {
        $("#input-time").replaceWith(session.render("/ui/valueOverTime/formula_entered.html",{id: "time", formula: formula}));
        $("#edit-time").click(function(e) {
            e.preventDefault();
            changeTime(formula);
        });
        $("#remove-time").click(function(e) {
            e.preventDefault();
            session.sendCmd("removeFormula",{
                id : "time"
            });
        });
    }

    function changeTime(formula) {
        $("#input-time").replaceWith(session.render("/ui/valueOverTime/input_field.html",{id: "time", value: formula, text: "Set"}));
        $("#btn-time").click(function(e) {
            e.preventDefault();
            session.sendCmd("addFormula", {
                "id" : "time",
                "newFormula" : true
            })
        });
        $("#formula-time").keyup(function(e) {
            session.sendCmd("parse", {
                "formula" : e.target.value,
                "id" : e.target.parentNode.id
            })
        });
    }

    function formulaAdded(id,formula,nextId) {
        $(".alert").remove();
        $("#"+id).removeClass("has-error");
        var parentId = "#input-"+id;

        $("#formulas").append(session.render("/ui/valueOverTime/input_field.html",{id: nextId, value: "", text:"Add"}));
        $("#btn-"+nextId).click(function(e) {
            e.preventDefault();
            var id = e.target.parentNode.parentNode.id;
            session.sendCmd("addFormula", {
                "id" : id,
                "newFormula" : true
            });
        });
        $("#formula-"+nextId).keyup(function(e) {
            session.sendCmd("parse", {
                "formula" : e.target.value,
                "id" : e.target.parentNode.id
            })
        });
        $(parentId).replaceWith(session.render("/ui/valueOverTime/formula_entered.html",{id: id, formula: formula}))
        $("#edit-"+id).click(function(e) {
            e.preventDefault();
            var id = e.target.parentElement.id;
            var formula = $("#formula-"+id)[0].textContent;
            editFormula(id,formula);
        });
        $("#remove-"+id).click(function(e) {
            e.preventDefault();
            var id = e.target.parentElement.id;
            session.sendCmd("removeFormula", {
                "id" : id
            });
        })
    }

    function restoreFormulas(formulas,time) {
        var id, formula, idNum;
        $(".rendered").remove();
        for (var i = formulas.length - 1; i >= 0; i--) {
            id = formulas[i].id;
            formula = formulas[i].formula;
            $("#formulas").prepend(session.render("/ui/valueOverTime/formula_entered.html",{id: id, formula: formula}));
            $("#edit-"+id).click(function(e) {
                e.preventDefault();
                var id = e.target.parentElement.id;
                var formula = $("#formula-"+id)[0].textContent;
                editFormula(id,formula);
            });
            $("#remove-"+id).click(function(e) {
                e.preventDefault();
                var id = e.target.parentElement.id;
                session.sendCmd("removeFormula", {
                    "id" : id
                });
            })         
        }
        if(time !== "") {
            timeSet(time);            
        }
    }

    function editFormula(id,formula) {
        var parentId = "#input-"+id;
        $(parentId).replaceWith(session.render("/ui/valueOverTime/input_field.html",{id: id, value: formula, text: "Ok"}));
        $("#btn-"+id).click(function(e) {
            e.preventDefault();
            var id = e.target.parentNode.parentNode.id;
            session.sendCmd("addFormula", {
                "id" : id,
                "newFormula" : false
            });
        });
        $("#formula-"+id).keyup(function(e) {
            session.sendCmd("parse", {
                "formula" : e.target.value,
                "id" : e.target.parentNode.id
            })
        });
    }

    function formulaRestored(id,formula) {
        $(".alert").remove();
        $("#"+id).removeClass("has-error");
        var parentId = "#input-"+id;
        $(parentId).replaceWith(session.render("/ui/valueOverTime/formula_entered.html",{id: id, formula: formula}));
        $("#edit-"+id).click(function(e) {
            e.preventDefault();
            editFormula(id,formula);
        });
        $("#remove-"+id).click(function(e) {
            e.preventDefault();
            session.sendCmd("removeFormula", {
                "id" : id
            });
        })
    }

    function formulaRemoved(id) {
        var parentId = "#input-"+id;
        $(parentId).remove();
    }

    function draw(dataset,xLabel) {
        lastData = dataset
        lastLabel = xLabel

        clearCanvas();
        var color = d3.scale.category20();

        var elementNames = [];
        for( i = 0 ; i < dataset.length ; i = i + 1 ) {
            elementNames.push(dataset[i].name);
        }

        color.domain(elementNames);

        if( mode === "over") {
            drawOver(dataset, color, xLabel);
        } else if( mode === "each") {
            drawEach(dataset, color, xLabel);
        }

        var button = svg.append("g")
            .attr("transform","translate(" + (w + 10) + "," + (h - 20) +")")
            .attr("class","button click")
            .on("click",function() { clearCanvas(); changeMode(); draw(dataset, xLabel); });

        var rect =button.append("rect")
            .attr("height","20px")
            .attr("fill","#B0B0B0")
            .attr("stroke","#888")
            .attr("rx","2")
            .attr("ry","2");

        var text = button.append("text")
            .text("Toggle mode")
            .attr("font-size","11px")
            .attr("dx","5px")
            .attr("dy","12px");

        rect.attr("width",text[0][0].getBBox().width+10);

        for( i = 0 ; i < dataset.length ; i = i + 1 ) {
            var id = "#box-" + dataset[i].id
            $(id).attr("style","background-color: "+color(dataset[i].name))
        }
    }

    function changeMode() {
        if( mode === "over") {
            mode = "each";
        } else if( mode === "each") {
            mode = "over";
        }

        session.sendCmd("changeMode", {
            "varMode" : mode
        });
    }

    function drawOver(dataset, color, xLabel) {
            var i,j;
        var padding = 10;

        var xMax = 0;
        var yMax = 0;
        var xMin = Number.MAX_VALUE;
        var yMin = Number.MAX_VALUE;
        var temp = 0;


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
            temp = d3.min(dataset[i].dataset, function(d) { return d.value; });
            if( temp < yMin ) {
                yMin = temp;
            }
        }


        for( i = 0 ; i < dataset.length ; i = i + 1 ) {
            var data = dataset[i].dataset;
            for( j = 0 ; j < data.length ; j = j + 1 ) {
                if( data[j].scaleV === undefined) {
                    if( data[j].type === "BOOL" ) {
                        data[j].scaleV = data[j].value * yMax;
                    } else {
                        data[j].scaleV = data[j].value;
                    }
                }
            }
        }

        var xScale = d3.scale.linear().domain([xMin, xMax]).range([4*padding, w-padding]);
        var yScale = d3.scale.linear().domain([yMin, yMax]).range([h-4*padding, padding]);

        var xAxis = d3.svg.axis()
                        .scale(xScale)
                        .orient("bottom");

        // Set the tick count so no decimal points are shown
        if(dataset.length > 0 && dataset[0].dataset.length < 14) {
            xAxis.ticks(Math.round(dataset[0].dataset.length/2));
        }
                        
        var yAxis = d3.svg.axis()
                        .scale(yScale)
                        .orient("left")
                        .tickFormat(d3.format("d"))
                        .tickSubdivide(0);
        // Set the tick count so no decimal points are shown
        if(yMax - yMin < 7) {
            yAxis.ticks(yMax - yMin);
        }

        var line = d3.svg.line().x(function(d){return xScale(d.t)}).y(function(d){ return yScale(d.scaleV) });

        svg.append("g")
            .attr("class", "axis")
            .attr("transform", function() { var y = yMin > 0 ? (h - 2*padding) : yScale(0); return "translate("+0+"," + y + ")"})
            .call(xAxis)
          .append("text")
            .attr("class", "label")
            .attr("x", w)
            .attr("y", -6)
            .style("text-anchor", "end")
            .text(xLabel);
       
        var axis = svg.append("g")
            .attr("class", "axis")
            .attr("transform", "translate(" + 3*padding + ",0)")
            .call(yAxis); 

        // If there is only one value in the dataset, artificially insert it 
        //   because it will not be drawn by default
        if(dataset.length > 0 && dataset[0].dataset.length > 0 && axis.selectAll("g")[0].length === 0) {
            axis.append("g")
                .attr("style","opacity: 1;")
                .attr("transform","translate(0," + (h - 4 * padding) + ")")
               .append("text")
                .attr("x",-9)
                .attr("y",0)
                .attr("dy",".32em")
                .attr("text-anchor","end")
                .text(dataset[0].dataset[0].value);
        }  

        svg.selectAll("connection")
                        .data(dataset)
                        .enter()
                       .append("path")
                        .attr("class","connection")
                        .attr("d", function(d) { return line(d.dataset); })
                        .attr("stroke", function(d) { return color(d.name); });
    }

    function drawEach(dataset, color, xLabel) {
        var i;
        var padding = 10;
        var xMax = 0;
        var xMin = 99999999;
        var temp = 0;

        for( i = 0 ; i < dataset.length ; i = i + 1 ) {
            temp = d3.max(dataset[i].dataset, function(d) { return d.t; });
            if( temp > xMax ) {
                xMax = temp;
            }
            temp = d3.min(dataset[i].dataset, function(d) { return d.t; });
            if( temp < xMin ) {
                xMin = temp;
            }
        }

        var xScale = d3.scale.linear().domain([xMin, xMax]).range([4*padding, w-padding]);

        var xAxis = d3.svg.axis()
                        .scale(xScale)
                        .orient("bottom");

        // Set the tick count so no decimal points are shown
        if(dataset.length > 0 && dataset[0].dataset.length < 14) {
            xAxis.ticks(Math.round(dataset[0].dataset.length/2));
        }

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

        var line;
        var yHeight = h / dataset.length;
        var yScale;
        var yAxis, axis;
        var yMax, yMin;
        var rangeMin, rangeMax;
        for( i = 0 ; i < dataset.length ; i++ ) {
            yMin = d3.min(dataset[i].dataset, function(d) { return d.value; });
            yMax = d3.max(dataset[i].dataset, function(d) { return d.value; });
            rangeMin = h-4*padding-yHeight*i;
            rangeMax = padding+h - yHeight * (i + 1);
            yScale = d3.scale.linear().domain([yMin, yMax]).range([rangeMin, rangeMax]);

            line = d3.svg.line().x(function(d){return xScale(d.t)}).y(function(d){ return yScale(d.value) });
           
            yAxis = d3.svg.axis()
                        .scale(yScale)
                        .orient("left")
                        .tickFormat(d3.format("d"))
                        .tickSubdivide(0);

            // Set the tick count so no decimal points are shown
            if(yMax - yMin < 7) {
                yAxis.ticks(yMax - yMin);
            }

            axis = svg.append("g")
                .attr("class", "axis")
                .attr("transform", "translate(" + 3*padding + ",0)")
                .call(yAxis);   

            // If there is only one value in the dataset, artificially insert it 
            //   because it will not be drawn by default
            if(dataset[i].dataset.length > 0 && axis.selectAll("g")[0].length === 0) {
                axis.append("g")
                    .attr("style","opacity: 1;")
                    .attr("transform","translate(0,"+rangeMin+")")
                   .append("text")
                    .attr("x",-9)
                    .attr("y",0)
                    .attr("dy",".32em")
                    .attr("text-anchor","end")
                    .text(dataset[i].dataset[0].value);
            }

            svg.append("path")
                .attr("class","connection")
                .attr("d", line(dataset[i].dataset))
                .attr("stroke", color(dataset[i].name));
        }
    }

    function applyStyling(styling) {
        vizUtils.applyStyling(styling);
    }

    function clearCanvas() {
        svg.selectAll(".axis").remove();
        svg.selectAll(".connection").remove();
        svg.selectAll(".key").remove();
        svg.selectAll(".button").remove();
    }

    function parseOk(id) {
        $("#" + id).removeClass("has-error")
        $("#btn-" + id).prop("disabled",false);
    }

    function parseError(id) {
        $("#" + id).addClass("has-error")
        $("#btn-" + id).prop("disabled",true);
    }

    function error(id, errormsg) {
        $(".alert").remove();
        $("#right-col").prepend(session.render("/ui/valueOverTime/error_msg.html",errormsg));
        $("#"+id).addClass("has-error");
    }

    function disable() {
        $("body").append("<div class='modal-backdrop disabled'></div>")
    }

    function enable() {
        $(".disabled").remove()
    }

    extern.draw = function(data) {
        mode = data.drawMode;
        draw(JSON.parse(data.data),data.xLabel);
    }
    extern.client = ""
    extern.init = session.init
    extern.parseError = function(data) {
        parseError(data.id);           
    }
    extern.parseOk = function(data) {
        parseOk(data.id);
    }
    extern.error = function(data) {
        error(data.id,data);
    }
    extern.formulaAdded = function(data) {
        formulaAdded(data.id,data.formula,data.nextId);
        mode = data.drawMode;
        draw(JSON.parse(data.data),data.xLabel);
    }
    extern.formulaRestored = function(data) {
        formulaRestored(data.id,data.formula);
        mode = data.drawMode;
        draw(JSON.parse(data.data),data.xLabel);
    }
    extern.formulaRemoved = function(data) {
        if(data.id === "time") {
            changeTime("");
        } else {
            formulaRemoved(data.id);
        }
        mode = data.drawMode;
        draw(JSON.parse(data.data),data.xLabel);
    }
    extern.hasFormulaErrors = function(data) {
        hasFormulaErrors(JSON.parse(data.ids));
    }
    extern.restorePage = function(data) {
        restoreFormulas(JSON.parse(data.formulas),data.time);
        mode = data.drawMode;
        draw(JSON.parse(data.data),data.xLabel);
    }
    extern.timeSet = function(data) {
        if(data.formula === "") {
            changeTime(data.formula);
        } else {
            timeSet(data.formula);            
        }
        draw(JSON.parse(data.data),data.xLabel);
    }
    extern.session = session;
    extern.disable = disable
    extern.enable = enable

    return extern;
}())