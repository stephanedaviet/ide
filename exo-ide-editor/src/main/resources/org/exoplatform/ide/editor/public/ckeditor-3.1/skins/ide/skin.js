﻿CKEDITOR.skins.add('ide', (function () {
    var a = [];
    if (CKEDITOR.env.ie && CKEDITOR.env.version < 7) {
    }
    return{preload: a, editor: {css: ['editor.css']}, dialog: {css: ['dialog.css']}, templates: {css: ['templates.css']}, margins: [0, 0, 0, 0]};
})());
(function () {
    CKEDITOR.dialog ? a() : CKEDITOR.on('dialogPluginReady', a);
    function a() {
        CKEDITOR.dialog.on('resize', function (b) {
            var c = b.data, d = c.width, e = c.height, f = c.dialog, g = f.parts.contents;
            if (c.skin != 'ide')return;
            g.setStyles({width: d + 'px', height: e + 'px'});
            var h = function () {
                var i = 3, j = -20, k = f.parts.dialog.getChild([0, 0, 0]), l = k.getChild(0);
                if (l.$.offsetWidth == 0)return;
                var m = k.getChild(2);
                m.setStyle('width', l.$.offsetWidth + i + 'px');
                m = k.getChild(7);
                m.setStyle('width', l.$.offsetWidth + i + 'px');
                m = k.getChild(4);
                m.setStyle('height', l.$.offsetHeight + j + 'px');
                m = k.getChild(5);
                m.setStyle('height', l.$.offsetHeight + j + 'px');
            };
            setTimeout(h, 200);
            setTimeout(h, 500);
            setTimeout(h, 1000);
        });
    };
})();