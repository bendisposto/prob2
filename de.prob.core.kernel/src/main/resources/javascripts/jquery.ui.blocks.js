(function($, undefined) {

	$
			.widget(
					"ui.block",
					{
						version : "0.1.0",
						options : {
							type : "Javascript",
							container : "body",
							role : "block",

							hasMenu : false,
							editor : null,
							isOutput : true,
							outputBlockIds : [],

							isInitialized : false,
							// callbacks
							initialized : function(event, data) {
								
							},
							optionsChanged : function(event, options) {
								if (!$.browser.msie)
									window.console
											.debug("Event: optionsChanged from block: "
													+ options.id);
							}
						},
						_create : function() {
							this.options.isInitialized = false;
							this.options = $.recursiveFunctionTest(this.options);
							this.element.addClass("ui-block ui-widget ui-widget-content ui-corner-none"); //
							if (this.options.id) {
								this.element.attr("id", this.options.id);
							} else {
								this.element.uniqueId();
								this.options.id = this.element.attr("id");
							}
							this._setNeitherInNorOutput(this.options.neitherInNorOutput);
							this._initMenu(this.options.menu);
							this._setHasMenu(this.options.hasMenu);
							this._setIsOutput(this.options.isOutput);
							
							// this.element.append("<div class=\"ui-sort-handle
							// ui-icon ui-icon-arrow-4 ui-widget-content
							// ui-corner-all\"></div>");
							if (this.options.editor != null) {
								this._setEditor(this.options.editor);
							} else {
								this._triggerInitialized();
								this._trigger("optionsChanged", 0,
										[ this.options ]);
							}
							this._setMarked(this.options.mark);
							this.element.focusin($.proxy(function() {
								if (this.options.hasMenu) {
									this.element.find(".ui-visible-on-focus")
											.show();
								}
								this.element.addClass("focusin");
							}, this));
							this.element.focusout($.proxy(function() {
								this.element.removeClass("focusin");
								this.element.find(".ui-visible-on-focus")
										.hide();
								this.element.find(".ui-editor").editor("toUnicode");
								this.syncToServer();
							}, this));

							// TODO give this Block the Focus;
							this.lastOptions=$.extend(true,{},this.options);
							this.element.focusout();
							this.element.on("keypress",$.proxy(function(event){
								if(event.ctrlKey && (event.which==13 || event.which==10)){	
									this.element.find(".ui-editor").editor("toUnicode");
									this._trigger("evaluate",1,[this.options.id]);
								}
							},this));
								
							this.element.on("keydown",$.proxy(function(event){
								if(event.ctrlKey && event.altKey){
									this.menuactivate=true;
									if(event.which!=17 && event.which!=18)
										this.menuactivate=false;
									this.menuKey(event);
								}else if(!(event.ctrlKey && this.menuactivate) && !(event.altKey && this.menuactivate)){
									this.menuactivate=false;	
								}
							},this));
							this.element.on("keyup",$.proxy(function(event){
								if(this.menuactivate){
									this.activateMenu();
								}
							},this));
							this.element.on("keyup",$.proxy(function(event){
								if(this.menuactivate){
									this.activateMenu();
								}
							},this));
							this.element.contextMenu([
							                         {'Add documentation before': $.proxy(function(menuItem,menu){$('#ws-id-1').worksheet("addNewBlock",['Documentation',this.attr('id'),true]);},this.element)},
							                         {'Add documentation after': $.proxy(function(menuItem,menu){$('#ws-id-1').worksheet("addNewBlock",['Documentation',this.attr('id'),false]);},this.element)},
							                         $.contextMenu.separator,
							                         {'Add block before': $.proxy(function(menuItem,menu){$('#ws-id-1').worksheet("addNewBlock",['Event-B',this.attr('id'),true]);},this.element)},
							                         {'Add block after': $.proxy(function(menuItem,menu){$('#ws-id-1').worksheet("addNewBlock",['Event-B',this.attr('id'),false]);},this.element)},
							                         $.contextMenu.separator,
							                         {'Zoom in': function(){$("BODY").css("font-size",parseInt($("BODY").css("font-size"))+4+"px")}},
							                         {'Zoom out': function(){$("BODY").css("font-size",parseInt($("BODY").css("font-size"))-4+"px")}},
							                         $.contextMenu.separator,
							                         {'Print worksheet': function(){window.print();}}
							                         ],
							                         {theme:'vista'});
							this.element.find(".ui-block-menu").bind("menuselect",$.proxy(
									function(event,ui){ui.item.trigger("menuitemselect",ui)},
									this.element.find(".ui-block-menu")));
						},
						menuactivate:false,
						lastOptions:null,
						needsSync:function(){
							if(this.options.editor.content!=this.lastOptions.editor.content)
								return true;
							return false;
						},
						syncToServer : function() {
							if(!this.needsSync())
								return;
							if(this.syncBlocked)
								return;
							this.lastOptions = jQuery.extend(true, {}, this.options);
							
							if($("#loadingBar").css("display")=="hide")
								return;
							if(typeof wsid=="undefined")
								return
							var msg = jQuery.extend(true, {}, this.options);
							delete msg.menu;

							// FIXME syncToServer is called unnecessary some
							// times (eg. when clicking the menu)
							var content = this._addParameter("", "block", $
									.toJSON(msg));
							content = this._addParameter(content,
									"worksheetSessionId", wsid);
							this._trigger("syncStart",1,this.options.id);
							$.ajax(
											"setBlock",
											{
												type : "POST",
												contentType : "application/x-www-form-urlencoded;charset=UTF-8",
												data : content
											}).done($.proxy(function(){
												this._trigger("syncEnd",1,this.options.id);
											},this)).fail($.proxy(function(jqXhr,textStatus,error){
												$("#ws-id-1").data("worksheet")._alert(jqXhr.responseText);
											},this));
						},
						
						createULFromNodeArrayRecursive : function(nodes) {
							var menu = $("<ul></ul>");
							for ( var x = 0; x < nodes.length; x++) {
								var menuItem = this.nodeToUL(nodes[x]);
								if (nodes[x].children.length > 0) {
									menuItem
											.append(this
													.createULFromNodeArrayRecursive(nodes[x].children));
								}
								menu.append(menuItem);
							}
							return menu;
						},
						nodeToUL : function(node) {
							var nodeItem = $("<li></li>");
							var item = $("<a></a>");

							if (node.itemClass != "") {
								nodeItem.addClass(node.itemClass);
							}
							if (node.iconClass != "") {
								var icon = $("<span></span>").addClass(
										"ui-icon " + node.iconClass);
								item.append(icon);
							}
							if (node.title) {
								if (node.text != "") {
									item.append(node.text);
								}
								nodeItem.append(item);
							} else {
								if (node.text != "") {
									item.append(node.text);
									if(node.char!= ""){
										var hint=$("<span class=\"menu-key-hint\" />");
										hint.append("[CTRL+ALT+"+node.char+"]");
										item.append(hint);
										this.menuKeys[node.char]=node.click;
									}
								}
								item.click($.noop());
								//nodeItem.bind("menuitemselect",$.proxy(function(event,ui){window.console.debug(ui);},this.element.find(".ui-block-menu")))
								nodeItem.bind("menuitemselect",$.proxy(node.click,this.element));
								nodeItem.append(item);
							}
							
							return nodeItem;
						},
						toggleBlockContent : function() {
							var content = this.element
									.find(".ui-block-content");
							var icon = this.element
									.find(".ui-block-menu .ui-menubutton-toggle .ui-icon");
							content.toggle("blind").toggleClass(
									"ui-content-blind");
							if (content.hasClass("ui-content-blind")) {
								icon.switchClass("ui-icon-triangle-1-s",
										"ui-icon-triangle-1-w", 400,
										"easeInOutQuad");
							} else {
								icon.switchClass("ui-icon-triangle-1-w",
										"ui-icon-triangle-1-s", 400,
										"easeInOutQuad");
							}
						},
						insertEditor : function(editorOptions, index) {

							// this.options.editor=newEditor.editor("updateOptions");
						},
						getOutputBlockIds : function() {
							return this.options.outputBlockIds;
						},
						removeOutputBlocks : function() {
							var worksheet = $("#" + this.options.worksheetId);
							for ( var x = 0; x < this.options.outputBlockIds.length; x++) {
								worksheet.worksheet("removeBlockById",
										this.options.outputBlockIds[x]);
							}
							this.options.outputBlockIds = [];
							this
									._trigger("optionsChanged", 0,
											[ this.options ]);
						},
						updateOptions : function() {
							this.options.editor = $(
									"#" + this.options.editor.id).editor(
									"updateOptions");
						},
						_setOption : function(key, value) {
							switch (key) {
							case "id":
								break;
							case "worksheetId":
								break;
							case "hasMenu":
								this._setHasMenu(value);
								break;
							case "menu":
								// TODO solve bug with missing click handler
								// after reinit
								// this._initMenu(value);
								break;
							case "editor":
								this._setEditor(value);
								break;
							case "isOutput":
								break;
							case "outputBlockIds":
								this.removeOutputBlocks();
								break;
							case "mark":
								this._setMarked(value);
								break;
							case "neitherInNorOutput":
								this._setNeitherInNorOutput(value);
								break;
								
							default:
								break;
							}
							this._super(key, value);
							this._trigger("optionsChanged", 0, this.options);
						},
						_initMenu : function(menuOptions) {
							var menu = this.element.find(".ui-block-menu ");
							if (menu.length > 0) {
								menu.menubar("destroy");
								menu.remove();
							}
							if (menuOptions != null && menuOptions.length > 0) {
								var blockMenu = this
									.createULFromNodeArrayRecursive(menuOptions);
								blockMenu.addClass(
								"ui-block-menu ui-visible-on-focus")
									.menubar();
								this.element.prepend(blockMenu);
							} 
							
						},
						_setHasMenu : function(hasMenu) {
							var menu = this.element.find(".ui-block-menu");
							if (hasMenu == false) {
								menu.hide();
							} else {
								menu.show();
							}
						},
						_setEditor : function(editorOptions) {
							var editor = this.element.find(".ui-editor");
							if (editor.length > 0) {
								editor.editor("destroy");
								editor.remove();
								this.element.find(".ui-block-content").remove();
							}
							if (editorOptions != null) {
								var blockContentContainer = $("<div></div>").addClass("ui-block-content");
								this.element.append(blockContentContainer);

								var newEditor = $("<div></div>");
								blockContentContainer.append(
										newEditor);
								newEditor.one("editorinitialized", $.proxy(
										function(editor) {
											editor.bind("editoroptionschanged", $.proxy(
													function(event, options) {
														this._editorOptionsChanged(options)
													}, this));
											editor.bind("editorcontentchanged",function(){
													$(".ui-worksheet").worksheet("setDirty",true)});
											
											this._triggerInitialized();
										}, this,newEditor));
								newEditor.editor(editorOptions);
								blockContentContainer.append($("<div style='clear:both'/>"));
								
							}

						},
						/*_javaSetDirty : function(content, dirty) {
							if (typeof setDirty == 'function') {
								if (dirty)
									setDirty(true);
							}
						},*/
						_editorOptionsChanged : function(options) {
							this.options.editor = options;
							this
									._trigger("optionsChanged", 0,
											[ this.options ]);

						},
						syncBlocked:false,
						_destroy : function() {
							this.syncBlocked=true;
							this._setEditor(null);
							this.element.empty();
							this.element
									.removeClass("ui-block ui-widget ui-widget-content ui-corner-all");
							this.element.removeAttr("id", "");
							this.element.removeAttr("tabindex", "");
						},
						_addParameter : function(res, key, value) {
							if (res != "")
								res += "&";
							res += encodeURIComponent(key) + "="
									+ encodeURIComponent(value);
							return res;
						},
						_triggerInitialized : function() {
							this._setOption("isInitialized", true);
							if (!$.browser.msie)
								window.console
										.debug("Event: initialized from Block");
							this
									._trigger("initialized", 0,
											[ this.options.id ]);
						},

						_setIsOutput:function(isOutput){
							if(isOutput){
								this.element.addClass("ui-output");
							}else{
								this.element.removeClass("ui-output");	
							}
						},
						_setNeitherInNorOutput:function(neitherInNorOutput){
							if(neitherInNorOutput){
								this.element.addClass("ui-no-in-no-out");
							}else{
								this.element.removeClass("ui-no-in-no-out");	
							}
						},
						_setMarked:function(marked){
							var marks=this.element.find(".block-marks");
							var bcontent=this.element.find(".ui-block-content");
							if(marked){
								if(marks.length==0){
									marks=$("<div class='block-marks'/>");
									bcontent.prepend(marks);
								}
								bcontent.addClass("hasMarks");
								marks.append($("<div class='block-mark marked'>!</div>"));
							}else{
								this.element.find(".marked").remove();
								if(marks.find(".block-mark").length==0){
									marks.remove();
									bcontent.removeClass("hasMarks");
								}
							}
						},
						switchBlock:function(name){
							this.element.focusout();
							this.element.closest(".ui-worksheet").worksheet("switchBlock",{type:name,blockId:this.options.id})
						},
						setFocus:function(){
							this.element.focusin();
							this.element.focus();
							this.element.find(".ui-editor").editor("setFocus");
						},
						menuKeys:{},
						menuKey:function(event){
							if(this.options.hasMenu){
								var char=String.fromCharCode(event.which);
								if(event.shiftKey){
									char=char.toUpperCase();
								}else{
									char=char.toLowerCase();
								}
								if($.isFunction(this.menuKeys[char])){
									this.menuactivate=false;
									$.proxy(this.menuKeys[char],this.element)();
								}else{
								}
								
							}
						},
						activateMenu:function(){
								var el=this.element.find(".ui-block-menu").find(">li>a").first();
								if(el.attr("aria-haspopup")=="true")
									el.click();	
								this.menuactivate=false;
						}

					});

}(jQuery));
