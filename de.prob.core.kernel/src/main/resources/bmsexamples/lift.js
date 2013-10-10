$("#door").click(function() {
  
  parent.bms.executeOperation("close_door")
  parent.bms.executeOperation("open_door")
    
});

parent.bms.observer = {
	doorpos: function () {
		return function(curfloor,render) {
			switch (render(curfloor)) {
			  case "1":
			    return 60;
			    break;
			  case "0":
			    return 175;
			    break;
			  case "-1":
			    return 275;
			    break;
			  default:
			    return 0;
			    break;
			}
		}
	  }
}
