function splitter(options) {
	var extern = {};	
	// required
	extern.options = options;
	extern.splitter = options.splitter;
	extern.parent = (typeof options.parent === 'undefined' ? extern.splitter.parent() : options.parent);
	// optional
	extern.container1 = options.container1 || null;
	extern.container2 = options.container2 || null;
	extern.min1 = options.min1 || 0;
	extern.min2 = options.min2 || 0;
	extern.resize = options.resize || resize;
	// intern
	extern.isVertical = extern.splitter.hasClass('splitter_vertical');
	extern.splitterSize = (extern.isVertical ? extern.splitter.height() + 2 : extern.splitter.width() + 2);
	extern.lastPos = null;
	extern.containerSize = 0;
	extern.enabled = false;
	
	extern.enable = function() {
		if (!extern.enabled) {
			extern.splitter.mousedown(extern.start);
			$(window).mouseup(extern.stop);
			$(window).mousemove(extern.move);
			if (extern.container1 != null) {
				extern.containerSize = (extern.isVertical ? extern.container1.height() : extern.container1.width());	
			}	
			extern.enabled = true;
		}		
	};
	
	extern.disable = function() {
		if (extern.enabled) {
			extern.splitter.unbind('mousedown', extern.start);
			$(window).unbind('mouseup', extern.stop);
			$(window).unbind('mousemove', extern.move);	
			extern.enabled = false;
		}
	};	
	
	extern.start = function(e) {
		e.preventDefault();
		extern.lastPos = getPos(e);		
	}
	
	extern.stop = function(e) {
		if (extern.lastPos != null) {
			e.preventDefault();
			extern.moveSplitter(getPos(e));
			extern.lastPos = null;
		}
	}
	
	extern.move = function(e) {
		if (extern.lastPos != null) {
			e.preventDefault();
			extern.moveSplitter(getPos(e));
		}
	}
	
	extern.moveSplitter = function(pos) {
		var parentSize = (extern.isVertical ? extern.parent.height() : extern.parent.width());
		if (parentSize > extern.min1 + extern.min2 + extern.splitterSize) {
			var diff = extern.lastPos - pos;
			extern.containerSize = Math.max(extern.min1, extern.containerSize - diff);
			
			var size = parentSize - extern.containerSize;
			extern.containerSize += Math.min(0, size - (extern.splitterSize + extern.min2));
			
			extern.resize(extern, diff, extern.containerSize, extern.containerSize + extern.splitterSize);
		}
		extern.lastPos = pos;		
	}
	
	function resize(self, diff, size1, start2) {
		if (extern.isVertical) {				
			extern.container1.css({ height: size1 });
			extern.splitter.css({ top: size1 });
			extern.container2.css({ top: start2 });
		} else {
			extern.container1.css({ width: size1 });
			extern.splitter.css({ left: size1 });
			extern.container2.css({ left: start2 });
		}
	}
	
	function getPos(e) {
		return (extern.isVertical ? e.pageY : e.pageX);
	}
	
	// Enabled on start
	if (options.enable) {
		extern.enable();
	}
	
	return extern;
}