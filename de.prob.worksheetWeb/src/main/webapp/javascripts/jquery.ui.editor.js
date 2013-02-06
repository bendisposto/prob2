(function($, undefined) {

	$.widget("ui.editor", {
		version : "0.1.0",
		options : {
			isInitialized:false,
			worksheetId : null,
			html : "<textarea id=\"editorObject\"></textarea>",
			content : "Testinhalt",
			jsUrls:[],
			cssUrls:[],
			init : function() {
				var obj = $("#editorObject");
				obj.change(this.id, function(eventData) {
					$("#" + eventData.data).editor("setOptionContent", eventData.target.value);
				});
				return obj;
			},
			destroy : null,
			setContent : function(content) {
				$("#" + this.id).editor("getEditorObject").val(content);
			},
			getContent : function() {
				return $("#" + this.id).editor("getEditorObject").val();
			},

			contentChanged : function(event, content) {
				if(!$.browser.msie)
					window.console.debug("Event: contentChanged from Editor");
			},
			initialized : function(event, data) {},
			optionsChanged : function(event, options) {
				if(!$.browser.msie)
					window.console.debug("Event: optionsChanged from editor: " + options.id);
			}
		},

		_create : function() {
			this.options.isInitialized=false;
			$("body").lazyLoader();
			$("body").lazyLoader("loadStyles", this.options.cssURLs);
			$("body").one("scriptsLoaded", $.proxy(this._create2, this));
			$("body").lazyLoader("loadScripts", this.options.jsURLs);
		},

		_create2 : function(event, data) {
			this.element.uniqueId().addClass("ui-editor ui-widget");
			this.options.id = this.element.attr("id");
			
			var editorContentContainer = $("<div></div>").addClass("ui-editor-content");
			this.element.append(editorContentContainer);
			
			editorContentContainer.append($(this.options.html));
			this.options=$.recursiveFunctionTest(this.options);

			this.setEditorObject(this.initEditor());
			this.initcontent=this.options.content;
			this.setContent(this.options.content);

			this._triggerInitialized();
			this._trigger("optionsChanged",0,[this.options]);
		},
		_destroy : function() {

			// TODO maybe remove scriptsLoaded Listener from lazyLaoder
			this.destroyEditor();
			this.element.empty();
			this.element.removeClass("ui-editor ui-widget");
			if (this.element.attr("class") == "")
				this.element.removeAttr("class")
			this.element.removeAttr("id");
		},
		initcontent:"",
		initialSetContent:true,
		_editorChanged : function() {
			
			var content = this.getContent();
			if(!this.initialSetContent)
				this._trigger("contentChanged", 0, [ content,content!=this.initcontent ]);
			this.initialSetContent=false;
			this._setOptionContent(content);
		},

		getEditorObject : function() {
			return this.editorObject;
		},

		setEditorObject : function(newEditorObject) {
			this.editorObject = newEditorObject;
		},
		insertEditorHTML : function() {
			if (this.options.html != "" && this.options.html != null) {
				// TODO decide wheter to allow more content then this html
				// (maybe empty ui-worksheet-block-editor-content and destroy
				// already existing editor)
				$(".ui-editor-content", this.element).append(this.options.html);
			}
		},
		initEditor : function() {
			if ($.isFunction(this.options.init))
				return this.options.init();
			return null;
		},
		destroyEditor : function() {
			if ($.isFunction(this.options.destroy))
				return this.options.destroy();
			return null;
		},

		setContent : function(content) {
			this.options.setContent(content);
		},

		getContent : function() {
			if ($.isFunction(this.options.getContent))
				return this.options.getContent();
			return null;
		},

		_setOptionContent : function(content) {
			this.options.content = content;
			this._trigger("optionsChanged", 0, [ this.options ]);
		},

		_getOptionContent : function() {
			return this.options.content;
		},
		_setOption :function(key,val){
			this._super( "_setOption", key, val );
			this._trigger("optionsChanged",0,this.options);
		},
		_triggerInitialized:function(){
			this.options.isInitialized=true;
			if(!$.browser.msie)
				window.console.debug("Event: initialized from Editor");
			this._trigger("initialized", 0, [ this ]);
		}


	});
}(jQuery));