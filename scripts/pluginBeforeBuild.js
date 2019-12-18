const fs = require('fs');
const util = require('util');
const path = require('path');

module.exports = function(ctx) {

    const filenameFrom = 'jivosite-conf.json';
    const filenameTo = 'jivosite-conf.js';

    const pathToJivosite = path.join(path.join(ctx.opts.projectRoot, 'platforms/android/app/src/main/assets/jivosite'), filenameTo);
    const pathToFile = path.join(ctx.opts.projectRoot, filenameFrom);

    try {
        fs.mkdirSync(pathToJivosite, { recursive: true });
    } catch (e) {}


    if (!fs.existsSync(pathToFile)) {
    	throw new Exception('no jivosite-conf.json found in root')
    }

	fs.copyFileSync(pathToFile, pathToJivosite);

	/* make valid js file from json */
	const data = fs.readFileSync(pathToJivosite)
	const fd = fs.openSync(pathToJivosite, 'w+')
	const insert = new Buffer("jivo_config = ")
	fs.writeSync(fd, insert, 0, insert.length, 0)
	fs.writeSync(fd, data, 0, data.length, insert.length)
	fs.close(fd, (err) => {
		if (err) throw err;
	});

    const plugin = path.join(ctx.opts.plugin.dir, 'www');

    ['bundle.js', 'index_ru.html', 'index_en.html', 'jivo.css'].forEach( el => {
        fs.copyFileSync(path.join(plugin, el), path.join(pathToJivosite, el));
    })

    return true;
};
