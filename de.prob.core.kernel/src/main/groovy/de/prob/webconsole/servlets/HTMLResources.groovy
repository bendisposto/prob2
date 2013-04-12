package de.prob.webconsole.servlets

class HTMLResources {

	def static String getPredicateHTML(String sessionId) {
		return '''<!DOCTYPE html>
<!--[if lt IE 7 ]><html class="ie ie6" lang="en"> <![endif]-->
<!--[if IE 7 ]><html class="ie ie7" lang="en"> <![endif]-->
<!--[if IE 8 ]><html class="ie ie8" lang="en"> <![endif]-->
<!--[if (gte IE 9)|!(IE)]><!-->
<html lang="en">
<!--<![endif]-->
<head>
<!-- Basic Page Needs
    ================================================== -->
<meta charset="utf-8">
<title>Predicate Visualization</title>
<meta name="description" content="">
<meta name="author" content="Joy Clark">

<!-- Mobile Specific Metas
    ================================================== -->
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1">

<!-- CSS
    ================================================== -->
<link rel="stylesheet" href="../stylesheets/skeleton.css">
<link rel="stylesheet" href="../stylesheets/layout.css">
<link rel="stylesheet" href="../stylesheets/evalb.css">
<link rel="stylesheet" href="../stylesheets/pepper.css">

<style type="text/css">
 .node circle {
    cursor: pointer;
    fill: #fff;
    stroke: steelblue;
    stroke-width: 1.5px;
  }

  .node text {
    font-size: 11px;
  }

  path.link {
    fill: none;
    stroke: #ccc;
    stroke-width: 1.5px;
  }


</style>

</head>
<body onload="initialize('''+"'"+sessionId+"'"+''');">

	<!-- Primary Page Layout
      ================================================== -->

	<div id="body"></div>

	<!-- JS
      ================================================== -->
	<!-- <script src="http://code.jquery.com/jquery-1.7.1.min.js"></script> -->
	<script src="../javascripts/jquery-1.7.2.min.js"></script>
	<script src="../javascripts/d3.v2.min.js"></script>
	<script src="../javascripts/predicate.js"></script>



	<!-- End Document
  ================================================== -->
</body>
</html>'''
	}

	def static String getValueVsTimeHTML(String sessionId) {
		return '''<!DOCTYPE html>
<!--[if lt IE 7 ]><html class="ie ie6" lang="en"> <![endif]-->
<!--[if IE 7 ]><html class="ie ie7" lang="en"> <![endif]-->
<!--[if IE 8 ]><html class="ie ie8" lang="en"> <![endif]-->
<!--[if (gte IE 9)|!(IE)]><!-->
<html lang="en">
<!--<![endif]-->
<head>
<!-- Basic Page Needs
    ================================================== -->
<meta charset="utf-8">
<title>Value vs Time</title>
<meta name="description" content="">
<meta name="author" content="Joy Clark">

<!-- Mobile Specific Metas
    ================================================== -->
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1">

<!-- CSS
    ================================================== -->
<link rel="stylesheet" href="../stylesheets/skeleton.css">
<link rel="stylesheet" href="../stylesheets/layout.css">
<link rel="stylesheet" href="../stylesheets/evalb.css">
<link rel="stylesheet" href="../stylesheets/pepper.css">

<style type="text/css">
 .axis path,
 .axis line {
    fill: none;
    stroke: #000;
    shape-rendering: crispEdges;
  }

  .axis text {
        font-family: sans-serif;
        font-size: 11px;
   }

   .x.axis path {
   	  display: none;
   }

   .line {
   	  fill: none;
   	  stroke: #4682B4;
   	  stroke-width: 1.5px;
   }

.connection {
 fill:none;
  stroke:black;
}


</style>

</head>
<body onload="initialize('''+"'"+sessionId+"'"+''')">

	<!-- Primary Page Layout
      ================================================== -->

	<!-- Delete everything in this .container and get started on your own site! -->

	<svg></svg>

	<!-- container -->

	<!-- JS
      ================================================== -->
	<!-- <script src="http://code.jquery.com/jquery-1.7.1.min.js"></script> -->
	<script src="../javascripts/jquery-1.7.2.min.js"></script>
	<script src="../javascripts/d3.v2.min.js"></script>
	<script src="../javascripts/oszilloscope.js"></script>



	<!-- End Document
  ================================================== -->
</body>
</html>'''
	}
}
