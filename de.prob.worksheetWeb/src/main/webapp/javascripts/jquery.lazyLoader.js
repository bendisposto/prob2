(function($) {
	$.fn.lazyLoader = function() {

		var methods = {
			init : function(options) {
				var settings = $.extend({
					'jsUrls' : [ 		"javascripts/libs/jquery-1.8.3/jquery-1.8.3.js","javascripts/libs/jquery-ui-1.9.2/ui/jquery-ui.js","javascripts/jquery.lazyLoader.js","javascripts/jquery.json-2.4.js","javascripts/jquery.ui.editor.js","javascripts/jquery.ui.blocks.js","javascripts/jquery.ui.worksheet.js","javascripts/worksheet.js" ],
					'jsUrlQueue' : [ ],
					'cssUrls' : [ "stylesheets/jquery-ui-1.9.2/themes/base/jquery-ui.css","stylesheets/jquery-ui-1.9.2/themes/smoothness/jquery.ui.theme.css","stylesheets/jquery-ui-1.9.2/themes/base/jquery.ui.worksheet.css","stylesheets/jquery-ui-1.9.2/themes/base/jquery.ui.block.css","stylesheets/jquery-ui-1.9.2/themes/base/jquery.ui.editor.css","stylesheets/worksheet.css"],
					'cssUrlQueue' : [  ],
				}, options);
				this.each(function() {
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
					}
				});
				this.lazyLoader("loadStyles", []);
				return this.lazyLoader("loadScripts", []);
			},
			loadScripts : function(urls) {
				if(!$.isArray(urls))
					return this;
				return this.each(function(index) {
					var $this = $(this);
					var data = $this.data("lazyLoader");
					data.target.lazyLoader("_pushToJsQueue", urls);
					if (data.jsUrlQueue.length > 0) {
						if (!data.isJsLoading) {
							var nextURL = data.jsUrlQueue.shift();
							data.isJsLoading = true;
							jQuery.getScript(nextURL, function(content, status, jqxhr) {
								$("body").lazyLoader("scriptLoaded", this.url);
								if(typeof window.console.log  =='object')
									window.console.log(this.url+" loaded");
							}).fail(function(jqxhr, settings, exception) {
								// TODO make error handling complete;
								window.console.log("Triggered ajaxError handler.");
							});
						}
					} else {
						$this.trigger("scriptsLoaded", 0, []);
					}
				});
			},
			scriptLoaded : function(url) {
				return this.each(function(index) {
					var $this = $(this);
					var data = $this.data("lazyLoader");
					var urlCorrected = url.replace(/\??_=\d*/, "");
					data.jsUrls.push(urlCorrected);
					data.isJsLoading = false;
					data.target.lazyLoader("loadScripts", []);
				});
			},
			_pushToJsQueue : function(urls) {
				var data = this.data("lazyLoader");
				for ( var x = 0; x < urls.length; x++) {
					if (jQuery.inArray(urls[x], data.jsUrls) == -1) {
						data.jsUrlQueue.push(urls[x]);
					}
				}
			},
			loadStyles : function(urls) {
				if(!$.isArray(urls))
					return this;
				return this.each(function(index) {
					var $this = $(this);
					var data = $this.data("lazyLoader");
					data.target.lazyLoader("_pushToCssQueue", urls);

					while (data.cssUrlQueue.length > 0) {
						var newUrl = data.cssUrlQueue.shift()
						if (document.createStyleSheet) {
							if(typeof window.console.log  =='object')
								window.console.log(newUrl+" loaded");
							document.createStyleSheet(newUrl);
						} else {
							$("head").append($("<link rel='stylesheet' href='"+newUrl+"' type='text/css' media='screen' />"));
						}
						data.cssUrls.push(newUrl);
					}
					$this.trigger("stylesLoaded", 0, []);
				});
			},
			_pushToCssQueue : function(urls) {
				var data = this.data("lazyLoader");
				for ( var x = 0; x < urls.length; x++) {
					if (jQuery.inArray(urls[x], data.cssUrls) == -1) {
						data.cssUrlQueue.push(urls[x]);
					}
				}
			},
			loadCSSs : function(Css) {
			// GOOD
			},
			bindToScriptLoadOnce : function(callback, event, data) {

			},
			bindToCSSLoadOnce : function(callback, event, data) {
			// !!!
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
		// Do your awesome plugin stuff here

	};
}(jQuery));
$.fn.lazyLoader();