

$(document).ready(function(){
	$.ajax("newDocument",{
		type : "POST",
		data : "worksheetSessionId=1"
	}).done(function(data, status, xhr) {
		data = (new Function( "return " + xhr.responseText))();
		$("body > div").first().worksheet(data);
		
	});
	
});


function parseJSDocument(js){
	$("BODY").empty();
	var documentContainer = $("<div id=\"documentContainer\" class=\"documentContainer\"></div>");
	var documentMenu = $("<div id=\"documentMenu\" class=\"menubar\"></div>");
	var documentBody = $("<div id=\"documentBody\" class=\"worksheet\"></div>");
	$("BODY").append(documentContainer);
	documentContainer.append(documentMenu);
	documentContainer.append(documentBody);
	
	$.each(js.blocks,function(key,value){
		appendJSBlock(documentBody,value);
	});
}

function appendJSBlock(js){
	var block = $("<div class=\"block collapsable-container\">\n<div class=\"block-menu menubar\"></div>\n<div class=\"block-content collapsable \">\n<div class=\"subblock block-documentation collapsable-container\">\n<div class=\"menubar\"></div>\n<div class=\"content collapsable\"></div>\n</div>\n<div class=\"subblock block-input collapsable-container javascript \">\n<div class=\"menubar\"></div>\n<div class=\"content collapsable\"></div>\n</div>\n<div class=\"subblock block-output collapsable-container\">\n<div class=\"menubar\"></div>\n<div class=\"content collapsable\"></div>\n</div>\n</div>\n</div>");	
	$("#documentBody").children(".block").last().after(block);
	
}

/*
			<div class="block collapsable-container">
				<div class="block-menu menubar"></div>
				<div class="block-content collapsable ">
					<div class="subblock block-documentation collapsable-container">
						<div class="menubar"></div>
						<div class="content collapsable"></div>
					</div>
					<div class="subblock block-input collapsable-container javascript ">
						<div class="menubar"></div>
						<div class="content collapsable"></div>
					</div>
					<div class="subblock block-output collapsable-container">
						<div class="menubar"></div>
						<div class="content collapsable"></div>
					</div>
				</div>
			</div>
*/
/*
<ul>
<li><a href="#">Action</a>
	<ul>
		<li><a href="#">Evaluate (this)</a></li>
		<li><a href="#">Evaluate (all after)</a></li>
	</ul></li>
<li><a href="#">ProB</a>
	<ul>
		<li><a href="#">Javascript</a></li>
		<li><a href="#">Event-B</a></li>
		<li><a href="#">Groovy</a></li>
		<li><a href="#">Classical-B</a></li>
	</ul></li>
<li class="menu-last"><a class="min-button button"></a></li>
</ul>
*/

/*
<div class="title">Dokumentation</div>
<div class="buttons">
	<div class="min-button button"></div>
</div>
<br style="clear: both" />
*/