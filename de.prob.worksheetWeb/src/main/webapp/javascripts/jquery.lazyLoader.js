(function($) {
	"use strict";
	$.fn.lazyLoader = function() {
		
		//jTODO never load lazyLoader on more than one element in the document
		var methods = {
			init : function(options) {
				var settings = $.extend({
					'jsUrls' : [],
					'jsUrlQueue' : [],
					'cssUrls' : [],
					'cssUrlQueue' : []
				}, options);
				return this.each(function() {
					//DEBUG alert("lazyLoader init");
					var $this = $(this);
					var data = $this.data('lazyLoader');

					// If the plugin hasn't been initialized yet
					if (!data) {

						$this.data('lazyLoader', {
							target : $this,
							cssUrls : settings.cssUrls,
							cssUrlQueue : settings.cssUrlQueue,
							jsUrls : settings.jsUrls,
							jsUrlQueue : settings.jsUrlQueue
						});
						data = $this.data("lazyLoader");
						$this.addClass("lazyLoader");
						}
				});
			},
			load:function(){
				return this.each(function() {
					$(this).lazyLoader("_loadNextScript");
					$(this).lazyLoader("_loadNextStyles");
				});
			},
			loadScripts : function(urls) {				
				return this.each(function() {
					//DEBUG alert("worksheet loadScripts");
					var $this = $(this);
					var data = $this.data("lazyLoader");
					if(!$.isArray(urls) || urls.length===0){
						if(!data.isJsLoading)
							$this.trigger("scriptsLoaded", 0, []);
						return this;
					}
					data.target.lazyLoader("_pushToJsQueue", urls);
					data.target.lazyLoader("_loadNextScript");
				});
			},
			_loadNextScript:function(){
				return this.each(function() {
					var data = $(this).data("lazyLoader");
					if (data.jsUrlQueue.length > 0) {
						if (!data.isJsLoading) {
							var nextURL = data.jsUrlQueue[0];
							data.isJsLoading = true;
							jQuery.getScript(nextURL)
								.done(function() {
									$(".lazyLoader").lazyLoader("scriptLoaded", this.url);
								}).fail(function() {
									window.alert("Script loading error");
								});
						}
					} else {
						$(this).trigger("scriptsLoaded", 0, []);
					}
				});
			},
			scriptLoaded : function(url) {
				return this.each(function() {
					//DEBUG alert("worksheet scriptLoaded");
					var $this = $(this);
					$this.trigger("scriptLoaded", 0, [url]);
					var data = $this.data("lazyLoader");
					var urlCorrected = url.replace(/\??_=\d*/, "");
					data.jsUrlQueue.splice( $.inArray(urlCorrected, data.jsUrlQueue), 1 );
					data.jsUrls.push(urlCorrected);
					data.isJsLoading = false;
					data.target.lazyLoader("_loadNextScript");
				});
			},
			_pushToJsQueue : function(urls) {
				//DEBUG alert("worksheet _pushToJsQueue");
				return this.each(function() {
					var data = $(this).data("lazyLoader");
					for ( var x = 0; x < urls.length; x++) {
						if (jQuery.inArray(urls[x], data.jsUrls) === -1 && jQuery.inArray(urls[x], data.jsUrlQueue) === -1)  {
							data.jsUrlQueue.push(urls[x]);
						}
					}
				});
			},
			loadStyles : function(urls) {
				return this.each(function() {
					//DEBUG alert("worksheet loadStyles");
					var $this = $(this);
					if(!$.isArray(urls) || urls.length===0){
						$this.trigger("stylesLoaded", 0, []);
						return this;
					}
					var data = $this.data("lazyLoader");
					
					data.target.lazyLoader("_pushToCssQueue", urls);
					data.target.lazyLoader("_loadNextStyles");
				});
			},
			_loadNextStyles:function(){
				return this.each(function() {
					//DEBUG alert("worksheet loadStyles");
					var $this = $(this);
					var data = $this.data("lazyLoader");
					while (data.cssUrlQueue.length > 0) {
						var newUrl = data.cssUrlQueue.shift();
						if (document.createStyleSheet) {
							document.createStyleSheet(newUrl);
						} else {
							$("head").append($("<link rel='stylesheet' href='"+newUrl+"' type='text/css' />"));
						}
						data.cssUrls.push(newUrl);
					}
					$this.trigger("stylesLoaded", 0, []);
				});
			},
			_pushToCssQueue : function(urls) {
				//DEBUG alert("worksheet _pushToCssQueue");
				return this.each(function() {
					var data = $(this).data("lazyLoader");
					for ( var x = 0; x < urls.length; x++) {
						if (jQuery.inArray(urls[x], data.cssUrls) === -1 && jQuery.inArray(urls[x], data.cssUrlQueue) === -1) {
							data.cssUrlQueue.push(urls[x]);
						}
					}
				});
			}
		};

		$.fn.lazyLoader = function(method) {
			// Method calling logic
			if (methods[method]) {
				return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
			} else if (typeof method === 'object' || !method) {
				return methods.init.apply(this, arguments);
			} else {
				$.error('Method ' + method + ' does not exist on jQuery.lazyLoader');
			}
		};
	};
}(jQuery));
$.fn.lazyLoader();