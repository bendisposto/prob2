(function($, undefined) {

	$.widget("ui.editor", {
		version : "0.1.0",
		options : {
			isInitialized:false,
			worksheetId : null,
			html : "<textarea class=\"editor-object\"></textarea>",
			content : "",
			jsUrls:[],
			cssUrls:[],
			init : function() {
				//DEBUG window.console.debug("Trace: jsInit");
				var obj = $($("#"+this.id).find(".editor-object").first());
				obj.change($.proxy($("#"+this.id).data("editor")._editorChanged,$("#"+this.id).data("editor")));
				return obj;
			},
			destroy : null,
			setContent : function(content) {
				//DEBUG window.console.debug("Trace: js:setContent");
				var obj = $($("#"+this.id).find(".editor-object").first());
				obj.val(content);
			},
			getContent : function() {
				//DEBUG window.console.debug("Trace: js:getContent");
				var obj = $($("#"+this.id).find(".editor-object").first());
				return obj.val();
			},
			setFocus:function(){
				$($("#"+this.id).find(".editor-object")).focus();
			},
			contentChanged : function(event, content) {
				//$.proxy($("#"+this.id).editor().data().editor._editorChanged(content),$("#"+this.id+).editor().data().editor)});
				
				if(!$.browser.msie)
					window.console.debug("Event: contentChanged from Editor");
			},
			initialized : function(event, data) {},
			optionsChanged : function(event, options) {
				if(!$.browser.msie)
					window.console.debug("Event: optionsChanged from editor: " + options.id);
			}
		},
		backupId:null,
		_create : function() {
			//DEBUG window.console.debug("Trace: _create");
			this.element.empty();
			if(typeof this.element.attr("id")=="string")	
				this.backupId=this.element.attr("id");
			this.element.addClass("ui-editor ui-widget");
			//TODO find better workaround including id from server for uniqueid Jquery Bug
			this.element.uniqueId();
			this._setOption("id",this.element.attr("id"));
			this._setOption("isInitialized",false);
			
			
			var editorContentContainer = $("<div></div>").addClass("ui-editor-content");
			this.element.append(editorContentContainer);
			
			this.options=$.recursiveFunctionTest(this.options);

			$("body").lazyLoader();
			$("body").lazyLoader("loadStyles", this.options.cssURLs);
			$("body").one("scriptsLoaded", $.proxy(this._create2, this));
			$("body").lazyLoader("loadScripts", this.options.jsURLs);
		},

		_create2 : function(event, data) {
			//DEBUG window.console.debug("Trace: _create2");
			this.setEditorObject(this.initEditor());
			this.initcontent=this.options.content;
			this.setContent(this.options.content);

			this._triggerInitialized();
		},
		
		

		getEditorObject : function() {
			//DEBUG window.console.debug("Trace: getEditorObject");
			return this.editorObject;
		},

		setEditorObject : function(newEditorObject) {
			//DEBUG window.console.debug("Trace: setEditorObject");
			this.editorObject = newEditorObject;
		},
		insertEditorHTML : function() {
			//DEBUG window.console.debug("Trace: _insertEditorHTML");
			if (this.options.html != "" && this.options.html != null) {
				// TODO decide wheter to allow more content then this html
				// (maybe empty ui-worksheet-block-editor-content and destroy
				// already existing editor)
				if(this.options.isInitialized){
				//TODO seems to be dead code
					this.destroyEditor();
					$(".ui-editor-content", this.element).empty();
				}
				var content=$(this.options.html);
				content.uniqueId();
				$(".ui-editor-content", this.element).append(content);
			}
		},
		initEditor : function() {
			//DEBUG window.console.debug("Trace: _initEditor");
			if ($.isFunction(this.options.init)){
				this.insertEditorHTML();
				return this.options.init();		
			}
			return null;
		},
		destroyEditor : function() {
			//DEBUG window.console.debug("Trace: _destroyEditor");
			if ($.isFunction(this.options.destroy) && this.options.isInitialized)
				return this.options.destroy();
			return null;
		},

		setContent : function(content) {
			//DEBUG window.console.debug("Trace: setContent");
			if($.isFunction(this.options.getContent) && this.getContent()!=content){
				if($.isFunction(this.options.setContent)){
					if(this.options.escapeHtml)
						content=$("<div/>").text(content).html();
					if(this.options.newlineToHtml){
						var reg=RegExp("\\r\\n","g");
						content=content.replace(reg,"<br />");
						var reg=RegExp("\\n","g");
						content=content.replace(reg,"<br />");
						var reg=RegExp("\\r","g");
						content=content.replace(reg,"<br />");
					}
					this.options.setContent(content);
				}
			}
		},

		getContent : function() {
			//DEBUG window.console.debug("Trace: getContent");
			if ($.isFunction(this.options.getContent) && this.options.isInitialized){
				var content=this.options.getContent();
				if(this.options.newlineToHtml){
					var reg=RegExp("<br\\s*/?>","g");
					content=content.replace(reg,"\n");
				}
				if(this.options.escapeHtml)
					content=$("<div/>").html(content).text();
				return content;	
			}
			return "";
		},
		setFocus:function(){
			if ($.isFunction(this.options.setFocus) && this.options.isInitialized)
				return this.options.setFocus();
			return null;
		},
		_setOption :function(key,val){
			//DEBUG window.console.debug("Trace: _setOption");
			switch (key) {
			case "id":
				break;
			case "content":				
				this.setContent(val);
				this.options.content = val;
				if(this.initcontent!=val && !this.converting )
					this._trigger("contentChanged", 0, [val]);
				
				break;
			case "cssURLs":
				this._setCSSUrls(val);
				break;
			case "jsURLs":
				this._setJSUrls(val);
				break;
			case "getContent":
				value=$.recursiveFunctionTest(val);
				break;
			case "setContent":
				value=$.recursiveFunctionTest(val);
				break;
			case "html":
				break;
			case "init":
				value=$.recursiveFunctionTest(val);
				break;
			case "objType":
				break;
			case "destroy":
				value=$.recursiveFunctionTest(val);
				break;


			default:
				break;
			}
			this._super( key, val );
			this._trigger("optionsChanged",0,this.options);		
		},
		_setCSSUrls:function(urls){
			//DEBUG window.console.debug("Trace: _setCSSUrls");
			$("body").lazyLoader("loadStyles", urls);	
		},
		_setJSUrls:function(urls){
			//DEBUG window.console.debug("Trace: _setJsUrls");
			$("body").lazyLoader("loadScripts", urls);	
		},
		_setOptionContent : function(content) {
			//DEBUG window.console.debug("Trace: _setOptionContent");
			
		},

		_getOptionContent : function() {
			//DEBUG window.console.debug("Trace: _getOptionContent");
			return this.options.content;
		},
		_triggerInitialized:function(){
			//DEBUG window.console.debug("Trace: _triggerInitialized");
			
			this._setOption("isInitialized",true);
			if(this.element.closest(".ui-block").block("option","toUnicode"))
				this.toUnicode();
			if(!$.browser.msie)
				window.console.debug("Event: initialized from Editor");
			this._trigger("initialized", 0, [ this ]);
		},
		_destroy : function() {
			//DEBUG window.console.debug("Trace: _destroy");
			
			// TODO maybe remove scriptsLoaded Listener from lazyLaoder
			this.destroyEditor();
			this.element.empty();
			this.element.removeClass("ui-editor ui-widget");
			if(this.element.attr("class")=="")
				this.element.removeAttr("class");
			if(this.backupId!=null){
				this.element.attr("id",this.backupId);		
			}else{
				this.element.removeAttr("id");
			}
		},
		initcontent:"",
		_editorChanged : function() {
			//jTODO rework this shit
		
			//DEBUG window.console.debug("Trace: _editorChanged");
			if(!this.options.isInitialized)
				return;
			
			var content = this.getContent();
			this._setOption("content",content);
		},
		_toUnicodeEventB:function(ascii){
			var reg=RegExp("<:","g");	ascii=ascii.replace(reg,"\u2286");//,8838],
			reg=RegExp(":\\|","g");		ascii=ascii.replace(reg,"\u2223");//,8739],
			reg=RegExp(":=","g");		ascii=ascii.replace(reg,"\u2254");//,8788],
			reg=RegExp("::","g");		ascii=ascii.replace(reg,"\u2208")//,8712],
			reg=RegExp("/<:","g");		ascii=ascii.replace(reg,"\u2288");//,8840],
			reg=RegExp("/<<:","g");		ascii=ascii.replace(reg,"\u2284");//,8836],
			reg=RegExp("/:","g");		ascii=ascii.replace(reg,"\u2209");//,8713],
			reg=RegExp("<<:","g");		ascii=ascii.replace(reg,"\u2282");//,8834],
			reg=RegExp(":","g");		ascii=ascii.replace(reg,"\u2208");//,8712],
			reg=RegExp("\\\\","g");		ascii=ascii.replace(reg,"\u2216");//,8726],
			reg=RegExp("\\.\\.","g");	ascii=ascii.replace(reg,"\u2025");//,8229],
			reg=RegExp("NAT","g");		ascii=ascii.replace(reg,"\u2115");//,8469],
			reg=RegExp("{}","g");		ascii=ascii.replace(reg,"\u2205");//,8709],
			reg=RegExp("false","g");	ascii=ascii.replace(reg,"\u22a5");//,8869],
			reg=RegExp("!","g");		ascii=ascii.replace(reg,"\u2200");//,8704],
			reg=RegExp("#","g");		ascii=ascii.replace(reg,"\u2203");//,8707],
			reg=RegExp("\\|->","g");	ascii=ascii.replace(reg,"\u21a6");//,8614],
			reg=RegExp("true","g");		ascii=ascii.replace(reg,"\u22a4");//,8868],
			reg=RegExp("\\\\/","g");	ascii=ascii.replace(reg,"\u222a");//,8746],
			reg=RegExp("/\\\\","g");	ascii=ascii.replace(reg,"\u2229");//,8745],
			reg=RegExp("<\\|","g");		ascii=ascii.replace(reg,"\u25c1");//,9665],
			reg=RegExp("\\|>","g");		ascii=ascii.replace(reg,"\u25b7");//,9655],
			reg=RegExp("<<\\|","g");		ascii=ascii.replace(reg,"\u2a64");//,10852],
			reg=RegExp("\\|>>","g");		ascii=ascii.replace(reg,"\u2a65");//,10853],
			reg=RegExp("%","g");		ascii=ascii.replace(reg,"\u03bb");//,955],
			reg=RegExp("oftype","g");	ascii=ascii.replace(reg,"\u2982");//,10626],
			reg=RegExp("\\*\\*","g");		ascii=ascii.replace(reg,"\u00d7");//,215],
			reg=RegExp("UNION","g");	ascii=ascii.replace(reg,"\u22c3");//,8899],
			reg=RegExp("INTER","g");	ascii=ascii.replace(reg,"\u22c2");//,8898],
			reg=RegExp(";","g");		ascii=ascii.replace(reg,"\u003b");//,59],
			reg=RegExp("circ","g");		ascii=ascii.replace(reg,"\u2218");//,8728],
			reg=RegExp("<<->>","g");	ascii=ascii.replace(reg,"\ue102");//,57602],
			reg=RegExp("><","g");		ascii=ascii.replace(reg,"\u2297");//,8855],
			reg=RegExp("\\|\\|","g");		ascii=ascii.replace(reg,"\u2225");//,8741],
			reg=RegExp("INT","g");		ascii=ascii.replace(reg,"\u2124");//,8484],
			reg=RegExp("&","g");		ascii=ascii.replace(reg,"\u2227");//,8743],
			reg=RegExp("=>","g");		ascii=ascii.replace(reg,"\u21d2");//,8658],
			reg=RegExp("<=>","g");		ascii=ascii.replace(reg,"\u21d4");//,8660],
			reg=RegExp("not","g");		ascii=ascii.replace(reg,"\u00ac");//);//,172],
			reg=RegExp("\\.","g");		ascii=ascii.replace(reg,"\u00b7");//);//,183],
			reg=RegExp("~","g");		ascii=ascii.replace(reg,"\u223c");//,8764],
			reg=RegExp("<<->","g");		ascii=ascii.replace(reg,"\ue100");//,57600],
			reg=RegExp("<->>","g");		ascii=ascii.replace(reg,"\ue101");//,57601],
			reg=RegExp("\\+->","g");		ascii=ascii.replace(reg,"\u21f8");//,8696],
			reg=RegExp("-->","g");		ascii=ascii.replace(reg,"\u2192");//,8594],
			reg=RegExp(">\\+>","g");		ascii=ascii.replace(reg,"\u2914");//,10516],
			reg=RegExp(">->","g");		ascii=ascii.replace(reg,"\u21a3");//,8611],
			reg=RegExp("\\+>>","g");		ascii=ascii.replace(reg,"\u2900");//,10496],
			reg=RegExp("->>","g");		ascii=ascii.replace(reg,"\u21a0");//,8608],
			reg=RegExp(">->>","g");		ascii=ascii.replace(reg,"\u2916");//,10518],
			reg=RegExp("\\^","g");		ascii=ascii.replace(reg,"\u005e");//,94],
			reg=RegExp("or","g");		ascii=ascii.replace(reg,"\u2228");//,8744],
			reg=RegExp("POW","g");		ascii=ascii.replace(reg,"\u2119");//,8473],
			reg=RegExp("\\|","g");		ascii=ascii.replace(reg,"\u2223");//,8739],
			reg=RegExp("/=","g");		ascii=ascii.replace(reg,"\u2260");//,8800],
			reg=RegExp("<->","g");		ascii=ascii.replace(reg,"\u2194");//,8596],
			reg=RegExp("<\\+","g");		ascii=ascii.replace(reg,"\ue103");//,57603],
			reg=RegExp("<=","g");		ascii=ascii.replace(reg,"\u2264");//,8804],
			reg=RegExp(">=","g");		ascii=ascii.replace(reg,"\u2265");//,8805],
			reg=RegExp("/","g");		ascii=ascii.replace(reg,"\u00f7");//,247],
			reg=RegExp("\\*","g");		ascii=ascii.replace(reg,"\u2217");//,8727],
			reg=RegExp("-","g");		ascii=ascii.replace(reg,"\u2212");//,8722],	
			return ascii;
		},
		_toUnicodeClassicalB:function(ascii){
			
		},
		_toAsciiEventB:function(unicode){
			var reg=RegExp("\\u2286","g");	unicode=unicode.replace(reg,"<:");//,8838],
			reg=RegExp("\\u2223","g");		unicode=unicode.replace(reg,":|");//,8739],,
			reg=RegExp("\\u2208","g");		unicode=unicode.replace(reg,":");//,8712],
			reg=RegExp("\\u2254","g");		unicode=unicode.replace(reg,":=");//,8788],
			reg=RegExp("\\u2208","g");		unicode=unicode.replace(reg,"::")//,8712],
			reg=RegExp("\\u2288","g");		unicode=unicode.replace(reg,"/<:");//,8840],
			reg=RegExp("\\u2284","g");		unicode=unicode.replace(reg,"/<<:");//,8836],
			reg=RegExp("\\u2209","g");		unicode=unicode.replace(reg,"/:");//,8713],
			reg=RegExp("\\u2282","g");		unicode=unicode.replace(reg,"<<:");//,8834]
			reg=RegExp("\\u2216","g");		unicode=unicode.replace(reg,"\\");//,8726],
			reg=RegExp("\\u2025","g");		unicode=unicode.replace(reg,"..");//,8229],
			reg=RegExp("\\u2115","g");		unicode=unicode.replace(reg,"NAT");//,8469],
			reg=RegExp("\\u2205","g");		unicode=unicode.replace(reg,"{}");//,8709],
			reg=RegExp("\\u22a5","g");		unicode=unicode.replace(reg,"false");//,8869],
			reg=RegExp("\\u2200","g");		unicode=unicode.replace(reg,"!");//,8704],
			reg=RegExp("\\u2203","g");		unicode=unicode.replace(reg,"#");//,8707],
			reg=RegExp("\\u21a6","g");		unicode=unicode.replace(reg,"|->");//,8614],
			reg=RegExp("\\u22a4","g");		unicode=unicode.replace(reg,"true");//,8868],
			reg=RegExp("\\u222a","g");		unicode=unicode.replace(reg,"\\/");//,8746],
			reg=RegExp("\\u2229","g");		unicode=unicode.replace(reg,"/\\");//,8745],
			reg=RegExp("\\u25c1","g");		unicode=unicode.replace(reg,"<|");//,9665],
			reg=RegExp("\\u25b7","g");		unicode=unicode.replace(reg,"|>");//,9655],
			reg=RegExp("\\u2a64","g");		unicode=unicode.replace(reg,"<<|");//,10852],
			reg=RegExp("\\u2a65","g");		unicode=unicode.replace(reg,"|>>");//,10853],
			reg=RegExp("\\u03bb","g");		unicode=unicode.replace(reg,"%");//,955],
			reg=RegExp("\\u2982","g");		unicode=unicode.replace(reg,"oftype");//,10626],
			reg=RegExp("\\u00d7","g");		unicode=unicode.replace(reg,"**");//,215],
			reg=RegExp("\\u22c3","g");		unicode=unicode.replace(reg,"UNION");//,8899],
			reg=RegExp("\\u22c2","g");		unicode=unicode.replace(reg,"INTER");//,8898],
			reg=RegExp("\\u003b","g");		unicode=unicode.replace(reg,";");//,59],
			reg=RegExp("\\u2218","g");		unicode=unicode.replace(reg,"circ");//,8728],
			reg=RegExp("\\ue102","g");		unicode=unicode.replace(reg,"<<->>");//,57602],
			reg=RegExp("\\u2297","g");		unicode=unicode.replace(reg,"><");//,8855],
			reg=RegExp("\\u2225","g");		unicode=unicode.replace(reg,"||");//,8741],
			reg=RegExp("\\u2124","g");		unicode=unicode.replace(reg,"INT");//,8484],
			reg=RegExp("\\u2227","g");		unicode=unicode.replace(reg,"&");//,8743],
			reg=RegExp("\\u21d2","g");		unicode=unicode.replace(reg,"=>");//,8658],
			reg=RegExp("\\u21d4","g");		unicode=unicode.replace(reg,"<=>");//,8660],
			reg=RegExp("\\u00ac","g");		unicode=unicode.replace(reg,"not");//);//,172],
			reg=RegExp("\\u00b7","g");		unicode=unicode.replace(reg,".");//);//,183],
			reg=RegExp("\\u223c","g");		unicode=unicode.replace(reg,"~");//,8764],
			reg=RegExp("\\ue100","g");		unicode=unicode.replace(reg,"<<->");//,57600],
			reg=RegExp("\\ue101","g");		unicode=unicode.replace(reg,"<->>");//,57601],
			reg=RegExp("\\u21f8","g");		unicode=unicode.replace(reg,"+->");//,8696],
			reg=RegExp("\\u2192","g");		unicode=unicode.replace(reg,"-->");//,8594],
			reg=RegExp("\\u2914","g");		unicode=unicode.replace(reg,">+>");//,10516],
			reg=RegExp("\\u21a3","g");		unicode=unicode.replace(reg,">->");//,8611],
			reg=RegExp("\\u2900","g");		unicode=unicode.replace(reg,"+>>");//,10496],
			reg=RegExp("\\u21a0","g");		unicode=unicode.replace(reg,"->>");//,8608],
			reg=RegExp("\\u2916","g");		unicode=unicode.replace(reg,">->>");//,10518],
			reg=RegExp("\\u005e","g");		unicode=unicode.replace(reg,"^");//,94],
			reg=RegExp("\\u2228","g");		unicode=unicode.replace(reg,"or");//,8744],
			reg=RegExp("\\u2119","g");		unicode=unicode.replace(reg,"POW");//,8473],
			reg=RegExp("\\u2223","g");		unicode=unicode.replace(reg,"|");//,8739],
			reg=RegExp("\\u2260","g");		unicode=unicode.replace(reg,"/=");//,8800],
			reg=RegExp("\\u2194","g");		unicode=unicode.replace(reg,"<->");//,8596],
			reg=RegExp("\\ue103","g");		unicode=unicode.replace(reg,"<+");//,57603],
			reg=RegExp("\\u2264","g");		unicode=unicode.replace(reg,"<=");//,8804],
			reg=RegExp("\\u2265","g");		unicode=unicode.replace(reg,">=");//,8805],
			reg=RegExp("\\u00f7","g");		unicode=unicode.replace(reg,"/");//,247],
			reg=RegExp("\\u2217","g");		unicode=unicode.replace(reg,"*");//,8727],
			reg=RegExp("\\u2212","g");		unicode=unicode.replace(reg,"-");//,8722],	
			return unicode;
		},
		_toAsciiClassicalB:function(unicode){
			
		},
		converting:false,
		toUnicode:function(){
			if(!this.element.closest(".ui-block").block("option","toUnicode"))
				return;
			var content=this.getContent();
			if(content!=null){
				content=$('<div/>').html(content).text();				
				var ascii=this._toAsciiEventB(content);
				var unicode=this._toUnicodeEventB(ascii);
				
				this.converting=true;
				this.setContent(unicode);
				this.converting=false;		
			
			}
		}
		
	});
}(jQuery));