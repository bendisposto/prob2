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
				//DEBUG
				window.console.debug("Trace: jsInit");
				var obj = $($("#"+this.id).find(".editor-object").first());
				obj.change($.proxy($("#"+this.id).data("editor")._editorChanged,$("#"+this.id).data("editor")));
				return obj;
			},
			destroy : null,
			setContent : function(content) {
				//DEBUG
				window.console.debug("Trace: js:setContent");
				var obj = $($("#"+this.id).find(".editor-object").first());
				obj.val(content);
				$.proxy($("#"+this.id).data("editor")._editorChanged(),$("#"+this.id).data("editor"));
			},
			getContent : function() {
				//DEBUG
				window.console.debug("Trace: js:getContent");
				var obj = $($("#"+this.id).find(".editor-object").first());
				return obj.val();
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
			//DEBUG
			window.console.debug("Trace: _create");
			this.element.empty();
			if(typeof this.element.attr("id")=="string")	
				this.backupId=this.element.attr("id");
			this.options.isInitialized=false;
			this.element.addClass("ui-editor ui-widget");
			if (this.options.id) {
				this.element.attr("id", this.options.id);
			} else {
				this.element.uniqueId();
				this.options.id = this.element.attr("id");
			}
			var editorContentContainer = $("<div></div>").addClass("ui-editor-content");
			this.element.append(editorContentContainer);
			
			editorContentContainer.append($(this.options.html));
			this.options=$.recursiveFunctionTest(this.options);

			$("body").lazyLoader();
			$("body").lazyLoader("loadStyles", this.options.cssURLs);
			$("body").one("scriptsLoaded", $.proxy(this._create2, this));
			$("body").lazyLoader("loadScripts", this.options.jsURLs);
		},

		_create2 : function(event, data) {
			//DEBUG
			window.console.debug("Trace: _create2");
			this.setEditorObject(this.initEditor());
			this.initcontent=this.options.content;
			this.setContent(this.options.content);

			this._triggerInitialized();
		},
		
		

		getEditorObject : function() {
			//DEBUG
			window.console.debug("Trace: getEditorObject");
			return this.editorObject;
		},

		setEditorObject : function(newEditorObject) {
			//DEBUG
			window.console.debug("Trace: setEditorObject");
			this.editorObject = newEditorObject;
		},
		insertEditorHTML : function() {
			//DEBUG
			window.console.debug("Trace: _insertEditorHTML");
			if (this.options.html != "" && this.options.html != null) {
				// TODO decide wheter to allow more content then this html
				// (maybe empty ui-worksheet-block-editor-content and destroy
				// already existing editor)
				$(".ui-editor-content", this.element).append(this.options.html);
			}
		},
		initEditor : function() {
			//DEBUG
			window.console.debug("Trace: _initEditor");
			if ($.isFunction(this.options.init))
				return this.options.init();
			return null;
		},
		destroyEditor : function() {
			//DEBUG
			window.console.debug("Trace: _destroyEditor");
			if ($.isFunction(this.options.destroy))
				return this.options.destroy();
			return null;
		},

		setContent : function(content) {
			//DEBUG
			window.console.debug("Trace: setContent");
			this.options.setContent(content);
		},

		getContent : function() {
			//DEBUG
			window.console.debug("Trace: getContent");
			if ($.isFunction(this.options.getContent))
				return this.options.getContent();
			return null;
		},
		_setOption :function(key,val){
			//DEBUG
			window.console.debug("Trace: _setOption");
			switch (key) {
			case "id":
				break;
			case "content":
				this.setContent(value);
				break;
			case "cssURLs":
				this._setCSSUrls(value);
				break;
			case "jsURLs":
				this._setJSUrls(value);
				break;
			case "getContent":
				value=$.recursiveFunctionTest(value);
				break;
			case "setContent":
				value=$.recursiveFunctionTest(value);
				break;
			case "html":
				break;
			case "init":
				value=$.recursiveFunctionTest(value);
				break;
			case "objType":
				break;
			case "destroy":
				value=$.recursiveFunctionTest(value);
				break;


			default:
				break;
			}
			this._super( "_setOption", key, val );
			this._trigger("optionsChanged",0,this.options);		
		},
		_setCSSUrls:function(urls){
			//DEBUG
			window.console.debug("Trace: _setCSSUrls");
			$("body").lazyLoader("loadStyles", urls);	
		},
		_setJSUrls:function(urls){
			//DEBUG
			window.console.debug("Trace: _setJsUrls");
			$("body").lazyLoader("loadScripts", urls);	
		},
		_setOptionContent : function(content) {
			//DEBUG
			window.console.debug("Trace: _setOptionContent");
			this.options.content = content;
			this._trigger("optionsChanged", 0, [ this.options ]);
		},

		_getOptionContent : function() {
			//DEBUG
			window.console.debug("Trace: _getOptionContent");
			return this.options.content;
		},
		_triggerInitialized:function(){
			//DEBUG
			window.console.debug("Trace: _triggerInitialized");
			
			this.options.isInitialized=true;
			if(!$.browser.msie)
				window.console.debug("Event: initialized from Editor");
			this._trigger("initialized", 0, [ this ]);
		},
		_destroy : function() {
			//DEBUG
			window.console.debug("Trace: _destroy");
			
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
		initialSetContent:true,
		_editorChanged : function() {
			//DEBUG
			window.console.debug("Trace: _editorChanged");
			var content = this.getContent();
			if(!this.initialSetContent)
				this._trigger("contentChanged", 0, [ content,content!=this.initcontent ]);
			this.initialSetContent=false;
			this._setOptionContent(content);
		},


	});
}(jQuery));