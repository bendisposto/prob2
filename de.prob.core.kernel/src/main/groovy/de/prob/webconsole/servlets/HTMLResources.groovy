package de.prob.webconsole.servlets

class HTMLResources {

	def static pred_head = '''<!DOCTYPE html>
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
<title>ProB Webshell</title>
<meta name="description" content="">
<meta name="author" content="Joy Clark">

<!-- Mobile Specific Metas
    ================================================== -->
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1">

<!-- CSS
    ================================================== -->
<link rel="stylesheet" href="stylesheets/skeleton.css">
<link rel="stylesheet" href="stylesheets/layout.css">
<link rel="stylesheet" href="stylesheets/evalb.css">
<link rel="stylesheet" href="stylesheets/pepper.css">

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
<body onload="initialize('''

	def static pred_tail = ''');">

	<!-- Primary Page Layout
      ================================================== -->

	<div id="body"></div>

	<!-- JS
      ================================================== -->
	<!-- <script src="http://code.jquery.com/jquery-1.7.1.min.js"></script> -->
	<script src="javascripts/jquery-1.7.2.min.js"></script>
	<script src="javascripts/d3.v2.min.js"></script>
	<script src="javascripts/predicate.js"></script>



	<!-- End Document
  ================================================== -->
</body>
</html>'''
}
