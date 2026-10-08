/// Copyright (c) 2017.
/// All rights reserved.
/// <summary>
/// LibrerÍa para la creación de Editores
/// </summary>
/// <remarks>
/// Creacion: 	WECM 14092017 <br />
/// </remarks>
ns('Gob.Pcm.UI.Web.Components.TinyMCE');
Gob.Pcm.UI.Web.Components.TinyMCE = function (opts) {
    this.init(opts);
};

Gob.Pcm.UI.Web.Components.TinyMCE.prototype = {
    init: function (opts) {        
        tinyMCE.init({
            mode: (opts.mode != null ? opts.mode : 'exact'),
            elements: opts.input.attr('id'),
            entity_encoding: "raw",
            theme: "advanced",
            plugins: 'table',
            paste_as_text: true,
            theme_advanced_buttons1: "bold, italic, underline, separator, justifyleft, justifycenter, justifyright, justifyfull,bullist,numlist,forecolor,separator,outdent,indent,separator,undo,redo,link",
            theme_advanced_buttons2: " formatselect,fontselect,fontsizeselect",
            theme_advanced_buttons3: "",
            theme_advanced_toolbar_location: "top",
            theme_advanced_toolbar_align: "left",
            forced_root_block: false,
            force_br_newlines: true,
            force_p_newlines: false,
            relative_urls: false,
            convert_urls: false,
            theme_advanced_path : false,
            height: (opts.height != null ? opts.height : 200), 
            width: (opts.width != null ? opts.width : 300),
            convert_newlines_to_brs: true,

            setup: function (ed) {
                ed.onClick.add(function (ed, e) {

                });
            }
        });
    }
}