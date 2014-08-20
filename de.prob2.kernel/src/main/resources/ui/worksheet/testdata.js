function add_test_data(session) {

	var t = Worksheet.render_box("11", "Groovy", "no soup for you! 1")
	$("#boxes").append(t)
	var t = Worksheet.render_box("12", "Groovy", "no soup for you! 2")
	$("#boxes").append(t)
	var t = Worksheet.render_box("13", "Groovy", "no soup for you! 3")
	$("#boxes").append(t)
	var t = Worksheet.render_box("14", "Groovy", "no soup for you! 4")
	$("#boxes").append(t)
	var t = Worksheet.render_box("15", "Groovy", "no soup for you! 5")
	$("#boxes").append(t)

}