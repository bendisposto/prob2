/**
 * @license Copyright (c) 2003-2013, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.html or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here. For example:
	config.language = 'de';
	// config.uiColor = '#AADC6E';
	config.baseHref = '';
	
	config.extraPlugins='onchange,autogrow';
	config.toolbarCanCollapse = true;
	config.height = 30;
	config.autoGrow_onStartup=true;
	config.autoGrow_minHeight=30;
	config.blockedKeystrokes=[CKEDITOR.CTRL + 10, CKEDITOR.CTRL + 13];
	config.toolbarGroups = [
	                        { name: 'clipboard',   groups: [  'undo' ] },
	                        { name: 'links' },
	                        { name: 'tools' },
	                        { name: 'document',    groups: [ 'mode' ] },
	                        { name: 'others' },
	                        { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
	                        { name: 'paragraph',   groups: [ 'list', 'indent', 'blocks', 'align' ] },
	                        { name: 'styles' },
	                        { name: 'colors' },
	                        { name: 'about' }
	                    ];
};
