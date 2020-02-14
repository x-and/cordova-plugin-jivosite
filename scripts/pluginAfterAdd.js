const fs = require('fs');
const util = require('util');
const path = require('path');

module.exports = function(ctx) {

    const jivosite = path.join(ctx.opts.projectRoot, 'dist/jivosite');

    const plugin = path.join(ctx.opts.plugin.dir, 'www');

    try {
        fs.mkdirSync(jivosite, { recursive: true });
    } catch (e) {}

    ['bundle.js', 'index_ru.html', 'index_en.html', 'jivo.css'].forEach( el => {
        fs.copyFileSync(path.join(plugin, el), path.join(jivosite, el));
    })

    return true;
};
