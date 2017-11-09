package de.prob.visualization;

public class HTMLResources {
	public static String getPredicateHTML(final String sessionId, final String w, final String h) {
		if (w != null && h != null) {
			String loadcmd = "createFormulaViz(\'" + sessionId + "\', \'body\', " + w + ", " + h + ")";
			return getHTML(loadcmd, "Predicate Visualization", "predicate.css", "predicate.js");
		}

		String loadcmd = "initialize(\'" + sessionId + "\')";
		return getHTML(loadcmd, "Predicate Visualization", "predicate.css", "predicate.js");
	}

	public static String getValueVsTimeHTML(final String sessionId, final String w, final String h) {
		if (w != null && h != null) {
			String loadcmd = "createValueOverTimeViz(\'" + sessionId + "\', \'body\', " + w + ", " + h + ", 600, 400)";
			return getHTML(loadcmd, "Value vs Time", "valueOverTime.css", "oszilloscope.js");
		}

		String loadcmd = "initialize(\'" + sessionId + "\')";
		return getHTML(loadcmd, "Value vs Time", "valueOverTime.css", "oszilloscope.js");
	}

	public static String getSSVizHTML(final String sessionId, final String w, final String h) {
		if (w != null && h != null) {
			String loadcmd = "createSSGraph(\'" + sessionId + "\',\'body\',1,[]," + w + "," + h + ")";
			return getHTML(loadcmd, "State Space Visualization", "statespace.css", "statespace.js");
		}


		String loadcmd = "initialize(\'" + sessionId + "\')";
		return getHTML(loadcmd, "State Space Visualization", "statespace.css", "statespace.js");
	}

	/**
	 * @param stylesheet ( "MyStyleSheet.css" that is saved in folder stylesheets/visualizations )
	 * @param javascript ( "MyJavascript.js" that is saved in folder javascripts )
	 * @return String representation of the HTML for the page
	 */
	public static String getHTML(String loadcmd, String title, String stylesheet, String javascript) {
		return "<!DOCTYPE html>\r\n<!--[if lt IE 7 ]>" +
				"<html class=\"ie ie6\" lang=\"en\"> <![endif]-->\r\n<!--[if IE 7 ]> " +
				"<html class=\"ie ie7\" lang=\"en\"> <![endif]-->\r\n<!--[if IE 8 ]>" +
				"<html class=\"ie ie8\" lang=\"en\"> <![endif]-->\r\n<!--[if (gte IE 9)|!(IE)]><!-->\r\n" +
				"<html lang=\"en\">\r\n<!--<![endif]-->\r\n<head>\r\n" +
				"<!-- Basic Page Needs\r\n    ================================================== -->\r\n" +
				"<meta charset=\"utf-8\">\r\n" +
				"<title>'''+title+'''</title>\r\n" +
				"<meta name=\"description\" content=\"\">\r\n" +
				"<meta name=\"author\" content=\"Joy Clark\">\r\n\r\n" +
				"<!-- Mobile Specific Metas\r\n    ================================================== -->\r\n" +
				"<meta name=\"viewport\"\r\n\tcontent=\"width=device-width, initial-scale=1, maximum-scale=1\">\r\n\r\n" +
				"<!-- CSS\r\n    ================================================== -->\r\n" +
				"<link rel=\"stylesheet\" href=\"../stylesheets/skeleton.css\">\r\n" +
				"<link rel=\"stylesheet\" href=\"../stylesheets/layout.css\">\r\n" +
				"<link rel=\"stylesheet\" href=\"../stylesheets/evalb.css\">\r\n" +
				"<link rel=\"stylesheet\" href=\"../stylesheets/pepper.css\">\r\n" +
				"<link rel=\"stylesheet\" href=\"../stylesheets/visualizations/'''+stylesheet+'''\"\r\n" +
				"</head>\r\n" +
				"<body onload=\"'''+loadcmd+'''\">\r\n\r\n\t\t\t" +
				"<div id=\"body\">" +
				"</div>\r\n\r\n\t\t\t" +
				"<!-- JS\r\n\t\t\t  ================================================== -->\r\n\t\t\t" +
				"<!-- <script src=\"http://code.jquery.com/jquery-1.7.1.min.js\">" +
				"</script> -->\r\n\t\t\t" +
				"<script src=\"../javascripts/jquery-1.9.1.min.js\">" +
				"</script>\r\n\t\t\t" +
				"<script src=\"../javascripts/d3.v2.min.js\">" +
				"</script>\r\n\t\t\t" +
				"<script src=\"../javascripts/viz.js\">" +
				"</script>\r\n\t\t\t" +
				"<script src=\"../javascripts/prob_visualization.js\">" +
				"</script>\r\n\t\t\t" +
				"<script src=\"../javascripts/'''+javascript+'''\">" +
				"</script>\r\n\r\n\t\t\t" +
				"<!-- End Document\r\n\t\t  ================================================== -->\r\n\t\t" +
				"</body>\r\n\t\t" +
				"</html>";
		//code.jquery.com/jquery-1.7.1.min.js"></script> -->\n
		// <script src="../javascripts/jquery-1.9.1.min.js"></script>\n
		// <script src="../javascripts/d3.v2.min.js"></script>\n
		// <script src="../javascripts/viz.js"></script>\n
		// <script src="../javascripts/prob_visualization.js"></script>\n
		// <script src="../javascripts/" + javascript + ""></script>\n\n
		// <!-- End Document\n		  ================================================== -->\n
		// </body>\n
		// </html>";
	}

}
