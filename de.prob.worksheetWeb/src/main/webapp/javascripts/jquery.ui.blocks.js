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

						// callbacks
							initialized : function(event, data) {
								window.console.debug("Event: initialized from Block");
							},
							optionsChanged : function(event, options) {
								window.console.debug("Event: optionsChanged from block: " + options.id);
							},
						},
						_create : function() {
							this.options=$.recursiveFunctionTest(this.options);
							this.element
									.addClass("ui-block ui-widget ui-widget-content ui-corner-all");
							this.element.attr("id", this.options.id);

							this._initMenu(this.options.menu);
							this._setHasMenu(this.options.hasMenu);

							var blockContentContainer = $("<div></div>")
									.addClass("ui-block-content");
							this.element.append(blockContentContainer);

							this._setEditor(this.options.editor);

							this.element
									.append("<div class=\"ui-sort-handle ui-icon ui-icon-arrow-4 ui-widget-content ui-corner-all\"></div>");
							this.element.focusin($.proxy(function() {
								if(this.options.hasMenu){
									this.element.find(".ui-visible-on-focus").show();
								}
								window.console.debug("in");
							},this));
							this.element.focusout($.proxy(function() {
								this.element.find(".ui-visible-on-focus").hide();
								this.syncToServer();
								window.console.debug("out");
							},this));
							
							// TODO give this Block the Focus;

							this.element.focusout();
							this._trigger("initialized",0,[]);
							this._trigger("optionsChanged",0,[this.options]);
						},
						syncToServer:function(){
							var msg=jQuery.extend(true, {}, this.options);
				            delete msg.menu;
				            
							var content = this._addParameter("", "block", $.toJSON(msg));
							content = this._addParameter(content,"worksheetSessionId", "1");
							$.ajax("setBlock", {
								type : "POST",
								data : content
							});
							//TODO add Handling
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
									nodeItem.append(node.text);
								}
							} else {
								if (node.text != "") {
									item.append(node.text);
								}
								item.click(node.click);
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
						eval : function() {
							var msg = [ this.options ];
							msg[0].editor = this.element.find(".ui-editor")
									.editor("option");
							var content = this.addParameter("", "blocks", $
									.toJSON(msg));
							content = this.addParameter(content,
									"worksheetSessionId", "1");
							$.ajax("blockEval", {
								type : "POST",
								data : content
							}).done(
									$.proxy(function(data, status, xhr) {
										var text=xhr.responseText;
										//var text=text.replace("\n","\\n").replace("\r","\\r");
										data = (new Function( "return " + text))();
										data = $.recursiveFunctionTest(data);
										// var caller = $("#" + data.id);
										//this.removeOutputBlocks();
										this._setOptions(data[0]);
										var worksheet=$("#"+this.options.worksheetId);
										for ( var x = 1; x < data.length; x++) {
											var block = worksheet.worksheet(
													"appendBlock", data[x]);
										}
									}, this));
						},
						addParameter : function(res, key, value) {
							if (res != "")
								res += "&";
							res += encodeURIComponent(key) + "="
									+ encodeURIComponent(value);
							return res;
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
							this._trigger("optionsChanged",0,[this.options]);
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
								//TODO solve bug with missing click handler after reinit
								//this._initMenu(value);
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
								break;

							default:
								break;
							}
							this._super(key, value);
							this._trigger("optionsChanged", 0, this.options);
						},
						_initMenu : function(menuOptions) {
							var blockMenu = null;
							var menu=this.element.find(".ui-block-menu ");
							if(menu.length>0){
								menu.menubar("destroy");
								menu.remove();
							}
							if (menuOptions != null && menuOptions.length > 0) {
								blockMenu = this
										.createULFromNodeArrayRecursive(menuOptions);
							} else {
								blockMenu = $("<ul></ul>");
							}
							blockMenu
									.addClass(
											"ui-block-menu ui-visible-on-focus")
									.menubar();
							this.element.prepend(blockMenu);
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
							}
							if (editorOptions != null) {
								var newEditor = $("<div></div>");
								this.element
										.find(".ui-block-content")
										.append(newEditor);
								newEditor.editor(editorOptions);
								newEditor.bind("editoroptionschanged", $.proxy(function(event, options) {this._editorOptionsChanged(options)}, this));
							}
						},
						_editorOptionsChanged:function(options){
							this.options.editor=options;
							this._trigger("optionsChanged", 0, [ this.options ]);
							
						},
						_destroy:function(){
							this._setEditor(null);
							this.element.empty();
							this.element.removeClass("ui-block ui-widget ui-widget-content ui-corner-all");
							this.element.removeAttr("id","");
							this.element.removeAttr("tabindex","");
						},
						_addParameter : function(res, key, value) {
							if (res != "")
								res += "&";
							res += encodeURIComponent(key) + "="
									+ encodeURIComponent(value);
							return res;
						}
						
					});

}(jQuery));