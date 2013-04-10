(function($, undefined) {

	$.widget("ui.worksheet", {
		version : "0.1.0",
		options : {
			sessionId:-2,
			id:"ws-id-1",
			hasMenu:false,
			hasBody:false,
			blocks:[],
			menu:[],
			isInitialized:false,
			sessionId:0,
			initialized : function(event) {},
			optionsChanged : function(event, options) {}
		},
		//jTODO Blocks[] is not needed;
		blocks : [],
		blocksLoading : 0,
		initialBlocksLoading : 0,
		_create : function() {
			//DEBUG alert("worksheet _create");
			//DEBUG alert("worksheet _create2");
			this.element.addClass("ui-worksheet ui-widget");
			this.element.attr("id",this.options.id);
			this.element.attr("tabindex",0);
			this.element.css("min-height",$(window).height()-1+"px");
			$(window).resize($.proxy(function(){
				this.element.css("min-height",$(window).height()-1+"px");
			},this)); 
			
			if (this.options.hasMenu) {
				var worksheetMenu = null;
				if (this.options.menu.length > 0) {
					worksheetMenu = this.createULFromNodeArrayRecursive(this.options.menu);
				} else {
					worksheetMenu = $("<ul></ul>");
				}
				worksheetMenu.addClass("ui-worksheet-menu").menubar();
				this.element.append(worksheetMenu);

			}
			if (this.options.hasBody) {
				var worksheetBody = $("<div></div>").addClass("ui-worksheet-body");
				this.element.append(worksheetBody);
				worksheetBody.sortable({
					items : "> .ui-block",
					handle : ".ui-sort-handle",
					update : function(event, ui) {
						$("#ws-id-1").worksheet("moveInBlocks", parseInt(ui.item.attr("tabindex")) - 1, ui.item.prevAll().length);
					}
				});

				if (this.options.blocks.length > 0) {
					//JTODO Why Backup?
					this.initialBlocksLoading=this.options.blocks.length;
					var blockOptions = this.options.blocks;
					this.options.blocks = [];
					for( var x = 0; x < blockOptions.length; x++) {
						this.appendBlock(blockOptions[x]);
					}
				}
				this.element.bind("blockevaluate",$.proxy(function(event,id){this.evaluate(id)},this));
				this.element.bind("blockcontextmenu",$.proxy(function(event,id){
					$("#"+id+"");
				},this));
				this.element.bind("keydown",$.proxy(function(event){
						if(event.ctrlKey){
							switch(event.which){
								case $.ui.keyCode.UP:
									this.focusPreviousInputBlock();
									break;
								case $.ui.keyCode.DOWN:
									this.focusNextInputBlock();
									break;	
							}
						}
						if(event.ctrlKey && event.altKey){
							if(event.which==10 || event.which==13){
								this.evaluate(this.options.blocks[0].id);
							}
						}
	
					},this));
			}
		},
		createULFromNodeArrayRecursive : function(nodes) {
			//DEBUG alert("worksheet createULFromNodeArrayRecursive");
			
			var menu = $("<ul></ul>");
			for ( var x = 0; x < nodes.length; x++) {
				var menuItem = this.nodeToUL(nodes[x]);
				if (nodes[x].children.length > 0) {
					menuItem.append(this.createULFromNodeArrayRecursive(nodes[x].children));
				}
				menu.append(menuItem);
			}
			return menu;
		},
		nodeToUL : function(node) {
			//DEBUG alert("worksheet nodeToUL");
			
			var nodeItem = $("<li></li>");
			if (node.itemClass != "") {
				nodeItem.addClass(node.itemClass);
			}
			var item = $("<a></a>");

			if (node.iconClass != "") {
				var icon = $("<span></span>").addClass("ui-icon " + node.iconClass);
				item.append(icon);
			}
			if (node.text != "") {
				item.append(node.text);
			}
			item.click(node.click);
			nodeItem.append(item);
			return nodeItem;
		},
		appendBlock : function(blockOptions) {
			//DEBUG alert("worksheet appendBlock");
			
			var index = this.options.blocks.length;
			return this.insertBlock(blockOptions, index);
		},
		insertBlock : function(blockOptions,index){
			//DEBUG alert("worksheet insertBlock");
			if (blockOptions != null && blockOptions != {}){
				blockOptions.worksheetId = this.options.id;
				this.insertIntoBlocks(index, blockOptions);
			}
			this.blocksLoading++;
			var block = $("<div></div>");
			if (index < this.element.find(".ui-worksheet-body>.ui-block").size() ){
				$(this.element.find(".ui-worksheet-body>.ui-block")[index]).before(block);						
			}else{
				this.element.find(".ui-worksheet-body").append(block);				
			}
			block.one("blockinitialized",$.proxy(this.blockLoaded,this));
			block.block(blockOptions);
			block.bind("blockoptionschanged", $.proxy(function(event, options) {
				var index=this.getBlockIndexById(options.id);
				this.options.blocks[index]=options;
				this._trigger("optionsChanged", 0, [ this.options ]);
			}, this));
			block.attr("tabindex",index+1);
			this.element.find(".ui-worksheet-body").sortable("refresh");
			this._trigger("optionsChanged",0,this.options);
		},
		blockLoaded: function(event,id){
			//DEBUG alert("worksheet blockLoaded");

			this.blocksLoading--;
			if(!this.options.isInitialized)	
				this.initialBlocksLoading--;
			if(!this.options.isInitialized && this.initialBlocksLoading==0 && this._blocksInitialized()){
				this._triggerInitialized();
				this._trigger("optionsChanged",0,[this.options]);
			}
		},
		_blocksInitialized:function(){
			//DEBUG alert("worksheet _blocksInitialized");
			for(var x=0;x<this.options.blocks.length;x++){
				if(!$("#"+this.options.blocks[x].id).data("block").options.isInitialized)
					return false;
			}
			return true;
		},
		insertIntoBlocks : function(index, element) {
			//DEBUG alert("worksheet insertIntoBlocks");
			this.options.blocks.push(null);
			for ( var x = this.options.blocks.length - 1; x > index; x--) {
				this.options.blocks[x] = this.options.blocks[x - 1];
			}
			this.options.blocks[x] = element;
			$("#" + element.id).attr("tabindex", index + 1);
		},
		removeFromBlocks : function(index) {
			//DEBUG alert("worksheet removeFromBlocks");
			
			for ( var x = index; x < this.options.blocks.length - 1; x++) {
				this.options.blocks[x] = this.options.blocks[x + 1];
			}
			this.options.blocks.pop();
		},
		moveInBlocks : function(indexFrom, indexTo) {
			//DEBUG alert("worksheet moveInBlocks");
			
			var temp = this.options.blocks[indexTo];
			this.options.blocks[indexTo] = this.options.blocks[indexFrom];
			this.options.blocks[indexFrom] = temp;
			$("#" + this.options.blocks[indexTo].id).attr("tabindex", indexTo + 1);
			$("#" + this.options.blocks[indexFrom].id).attr("tabindex", indexFrom + 1);
			// TODO inform server about position change
			// TODO call eval (following)
		},
		removeBlock : function(index) {
			//DEBUG alert("worksheet removeBlock");
			
			var block=$("#" + this.options.blocks[index].id);
			block.block("destroy");
			block.remove();
			for ( var x = index; x < this.options.blocks.length - 1; x++) {
				this.options.blocks[x] = this.options.blocks[x + 1];
			}
			this.options.blocks.pop();
			this.element.find(".ui-worksheet-body").sortable("refresh");
		},
		getBlockIndexById : function(id) {
			//DEBUG alert("worksheet getBlockIndexById");
			for ( var x = 0; x < this.options.blocks.length; x++) {
				if (this.options.blocks[x].id == id)
					break;
			}
			return x;
		},
		removeBlockById : function(id) {
			//DEBUG alert("worksheet removeBlockById");
			
			var index = this.getBlockIndexById(id);
			this.removeBlock(index);
		},
		insertMenu : function(menu) {
			//DEBUG alert("worksheet insertMenu");
			
			this.element.children(".ui-worksheet-menu").empty().append(menu);
		},
		isLastBlock : function(blockId) {
			//DEBUG alert("worksheet isLastBlock");
			return this.blocks[this.blocks.length - 1].block("option", "id") == blockId;
		},
		getBlocks : function() {
			//DEBUG alert("worksheet getBlocks");
			return this.blocks;
		},
		_setOption :function(key,val){
			this._super( "_setOption", key, value );
			this._trigger("optionsChanged",0,this.options);
		},
		evaluate:function(blockId){
			//DEBUG alert("worksheet evaluate");
			this._trigger("evalStart",0,[blockId]);
			var msg=this.options.blocks[this.getBlockIndexById(blockId)];
			if(typeof msg =="undefined" || msg==null){
				//jTODO decide how to react on not existing blockId Error
				return;
			}
            if(typeof msg.menu !=="undefined" && msg.menu!=null)
            	delete msg.menu;
            
			var content = this._addParameter("", "block", $.toJSON(msg));
			content = this._addParameter(content,"worksheetSessionId", this.options.sessionId);
			$.ajax("setBlock", {
				type : "POST",
				data : content
			}).done($.proxy(function(data, status, xhr) {
				var content = this._addParameter("", "id", blockId);
	            content = this._addParameter(content, "worksheetSessionId", this.options.sessionId);
	            $.ajax("worksheetEvaluate", {
					type : "POST",
					data : content
				}).done($.proxy(function(data, status, xhr) {
					var text=xhr.responseText;
					data = jQuery.parseJSON(xhr.responseText);
					data = $.recursiveFunctionTest(data);

					var index=this.getBlockIndexById(data[0].id);
					for(var x=this.options.blocks.length-1;x>=index;x--){
						this.removeBlock(x);
					}
					var lastInputIndex=0;
					for ( var x = 0; x < data.length; x++) {
						 this.appendBlock(data[x]);
						 if(!data[x].isOutput){
							 lastInputIndex=x;
						 }
					}
					$("#"+data[lastInputIndex].id).block("setFocus");
					this.setDirty(true);
					this._trigger("evalEnd",0,[blockId]);
				},this)).fail($.proxy(function(jqXhr,textStatus,error){
					this._alert(jqXhr.responseText);
				},this));
			},this)).fail($.proxy(function(jqXhr,textStatus,error){
				this._alert(jqXhr.responseText);
			},this));
		},
		_addParameter : function(res, key, value) {
			if (res != "")
				res += "&";
			res += encodeURIComponent(key) + "="
					+ encodeURIComponent(value);
			return res;
		},
		_destroy : function(){
			$(".ui-block")
				.block("destroy")
				.remove();
			this.element
				.empty()
				.removeClass("ui-worksheet ui-widget ui-corner-none");
			if(this.element.attr("class")=="")
				this.element.removeAttr("class");
			//LazyLoader needs to be destroyed seperatly because it could be needed in future or by other plugins
		},
		_triggerInitialized:function(){
			//DEBUG alert("worksheet _triggerInitialized");
			this.options.isInitialized=true;
			if(!$.browser.msie){
				window.console.debug("Event: initialized from worksheet");				
			}
			this._trigger("initialized",0,[this.options.id]);
		},
		
		dirty:false,
		setDirty:function(dirty){
			if(!this.options.isInitialized)
				return;
			if(dirty==this.dirty)
				return dirty;	
			if(!this.dirty){
				this._trigger("dirtyStateChange",0,true);
				if (typeof setDirty == 'function') {
					setDirty(true);
				}
			}else{
				this._trigger("dirtyStateChange",0,false);
				if (typeof setDirty == 'function') {
					setDirty(false);
				}
			}
			this.dirty=dirty;
		},
		isDirty:function(){
			return this.dirty;
		},
		switchBlock:function(options){
			
			this._trigger("switchBlockStart",0,[options]);
			this._trigger("evalStart",0,[options.blockId]);
			var msg=this.options.blocks[this.getBlockIndexById(options.blockId)];
			if(typeof msg =="undefined" || msg==null){
				//jTODO decide how to react on not existing blockId Error
				return;
			}
            if(typeof msg.menu !=="undefined" && msg.menu!=null)
            	delete msg.menu;
            
			var content = this._addParameter("", "block", $.toJSON(msg));
			content = this._addParameter(content,"worksheetSessionId", this.options.sessionId);
			$.ajax("setBlock", {
				type : "POST",
				data : content
			}).done($.proxy(function(data, status, xhr) {
			
				var content = this._addParameter("", "blockId", options.blockId);
				content = this._addParameter(content, "type", options.type);
				content = this._addParameter(content,"worksheetSessionId", this.options.sessionId);
				$.ajax("switchBlock", {
					type : "POST",
					data : content
				}).done($.proxy(function(data, status, xhr) {
					var text=xhr.responseText;
					data = jQuery.parseJSON(xhr.responseText);
					data = $.recursiveFunctionTest(data);
	
					var index=this.getBlockIndexById(data[0].id);
					for(var x=this.options.blocks.length-1;x>=index;x--){
						this.removeBlock(x);
					}
	
					var lastInputIndex=0;
					for ( var x = 0; x < data.length; x++) {
						 this.appendBlock(data[x]);
						 if(!data[x].isOutput){
							 lastInputIndex=x;
						 }
					}
					$("#"+data[lastInputIndex].id).block("setFocus");
					this._trigger("evalEnd",0,[]);
					this.setDirty(true);
					this._trigger("blockSwitched",0,[]);
				},this)).fail($.proxy(function(jqXhr,textStatus,error){
					this._alert(jqXhr.responseText);
				},this));
			},this)).fail($.proxy(function(jqXhr,textStatus,error){
				this._alert(jqXhr.responseText);
			},this));
		},
		focusPreviousInputBlock:function(){
			var actId=this.element.find(".ui-block.focusin").data("block").options.id;
			var actIndex=this.getBlockIndexById(actId);
			
			for(var x=actIndex-1;x>=0;x--){
				if(!this.options.blocks[x].isOutput){
					this.element.find(".ui-block.focusin").focusout();
					$("#"+this.options.blocks[x].id).block("setFocus");
					break;
				}
			}
			
		},
		focusNextInputBlock:function(){
			var actId=this.element.find(".ui-block.focusin").data("block").options.id;
			var actIndex=this.getBlockIndexById(actId);
			
			for(var x=actIndex+1;x<this.options.blocks.length;x++){
				if(!this.options.blocks[x].isOutput){
					this.element.find(".ui-block.focusin").focusout();
					$("#"+this.options.blocks[x].id).block("setFocus");
					break;
				}
			}
		},
		addNewBlock:function(options){
			this._trigger("addBlockStart",0,[options]);
			if(options[0]=="Documentation" && !options[2]){
				var index=this.getBlockIndexById(options[1]);
				index++;
				while(index < this.options.blocks.length){
					if(!this.options.blocks[index].isOutput){
						options[1]=this.options.blocks[index-1].id;
						break;
					}
					if(index==this.options.blocks.length-1){
						options[1]=this.options.blocks[index-1].id;
					}
					index++;
				}
			}
			var content = this._addParameter("", "blockId", options[1]);
			content = this._addParameter(content, "type", options[0]);
			content = this._addParameter(content,"before", options[2]);
			content = this._addParameter(content,"worksheetSessionId", this.options.sessionId);
			
			$.ajax("newBlock", {
				type : "POST",
				data : content
			}).done($.proxy(function(id,before,data, status, xhr) {
				var text=xhr.responseText;
				data = jQuery.parseJSON(xhr.responseText);
				data = $.recursiveFunctionTest(data);
				if(data.length>1){
					//FEATURE add Handling for immediate EvaluationBlocks
				}else{
					var index=this.getBlockIndexById(id);
					if(index<0)
						return;
					if(!before)
						index++;
					this.insertBlock(data[0], index);
					$("#"+data[0].id).block("setFocus");
				}
				this.setDirty(true);
				this._trigger("addBlockEnd",0,[]);
			},this,options[1],options[2])).fail($.proxy(function(jqXhr,textStatus,error){
				this._alert(jqXhr.responseText);
			},this));
		},
		_alert:function(msg){
			msg=$("<div id='modalDialog'>"+msg+"</div>");
			$("BODY").append(msg);
			msg.dialog({
			      modal: true,
			      buttons: {
			        Ok: function() {
			          $( this ).dialog( "close" );
			        }
			      },
			      close:function(event,ui){
			          $("#modalDialog").remove();
			      }
			    });
		}

	});
}(jQuery));