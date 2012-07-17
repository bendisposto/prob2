

<!DOCTYPE html>
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
<meta name="author" content="Jens Bendisposto">

<!-- Mobile Specific Metas
    ================================================== -->
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1">

<!-- CSS
    ================================================== -->
<link rel="stylesheet" href="stylesheets/base.css">
<link rel="stylesheet" href="stylesheets/skeleton.css">
<link rel="stylesheet" href="stylesheets/layout.css">
<link rel="stylesheet" href="stylesheets/evalb.css">
<link rel="stylesheet" href="stylesheets/pepper.css">
<link rel="stylesheet" href="stylesheets/table_jui.css">


<!--[if lt IE 9]>
          <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
      <![endif]-->

<!-- Favicons
      ================================================== -->
<link rel="shortcut icon" href="images/favicon.ico">
<link rel="apple-touch-icon" href="images/apple-touch-icon.png">
<link rel="apple-touch-icon" sizes="72x72"
	href="images/apple-touch-icon-72x72.png">
<link rel="apple-touch-icon" sizes="114x114"
	href="images/apple-touch-icon-114x114.png">
</head>
<body onLoad="initialize()">

	<!-- Primary Page Layout
      ================================================== -->

	<!-- Delete everything in this .container and get started on your own site! -->

	<div class="container">
		<h1 class="capital remove-bottom" style="margin-top: 40px">ProB
			2.0</h1>
		<h2 style="margin-top: 5px">The Model Checker and Animator</h2>
		<hr />
		<div class="sixteen columns">
			Current Log-Level: <a id="loglevel"
				href="javascript:switchLogLevel()">Trace</a>
		</div>
		<div class="sixteen columns">
			<div id="console" class="console"></div>
		</div>
		<div class="sixteen columns">
			<hr style="margin-top: 20px;" />
			<h3>System.out</h3>
			<textarea id="system_out" style="width: 99%; height: 300px; font-family: monospace;"></textarea>
		</div>

		<div class="sixteen columns">
			<hr style="margin-top: 20px;" />
			<h3>Global variables</h3>
			<div>
				<table id="bindings">
					<thead>
						<tr>
							<th>Variable-Name</th>
							<th>Type</th>
							<th>Value</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td></td>
							<td></td>
							<td></td>
						</tr>

					</tbody>
				</table>
			</div>
		</div>

		<div class="sixteen columns">
		<h3 style="margin-top: 20px;">Imported</h3>
			<textarea id="imports" style="width: 99%; font-family: monospace;"></textarea>
		</div>


		<div class="sixteen columns">
			<hr style="margin-top: 20px;" />
			<h3>About ProB</h3>
			<p>Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed
				diam nonumy eirmod tempor invidunt ut labore et dolore magna
				aliquyam erat, sed diam voluptua. At vero eos et accusam et justo
				duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata
				sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet,
				consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt
				ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero
				eos et accusam et justo duo dolores et ea rebum. Stet clita kasd
				gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.</p>
		</div>

		<div id="syntax"></div>

		<div class="sixteen columns">
			<hr style="margin-top: 20px;" />
			<h3>Troubleshooting</h3>
			<p>
				If you face any problem please submit a report to our <a
					href="http://jira.cobra.cs.uni-duesseldorf.de/">bug tracking
					system</a>.
			</p>
		</div>
		<div class="sixteen columns space">
			<hr style="margin-top: 0px; margin-bottom: 0px;" />
			<div id="footer-logo">
				(C) 2012, <a href="http://www.stups.uni-duesseldorf.de">STUPS
					Group</a>, HHU Duesseldorf
			</div>
		</div>

		<div id="selectioneval"
			style="display: none; position: absolute; border: 1px solid #999999; background-color: #FFFFEE; padding: 4px;"></div>


	</div>
	<!-- container -->

	<!-- JS
      ================================================== -->
	<!-- <script src="http://code.jquery.com/jquery-1.7.1.min.js"></script> -->
	<script src="javascripts/jquery-1.7.2.min.js"></script>
	<script src="javascripts/jquery.console.prob.js"></script>

	<script src="javascripts/jquery.dataTables.min.js"></script>
	<script src="javascripts/prob.js"></script>



	<!-- End Document
  ================================================== -->
</body>
</html>