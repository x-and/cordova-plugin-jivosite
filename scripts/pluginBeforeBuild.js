const fs = require('fs');
const util = require('util');
const path = require('path');

module.exports = function(ctx) {

    const jivosite = path.join(ctx.opts.projectRoot, 'platforms/android/app/src/main/assets/jivosite');
    const filename = 'jivosite-conf.json';
    const pathToFile = path.join(ctx.opts.projectRoot, filename);

    if (!fs.existsSync(pathToFile)) {
    	throw new Exception('no jivosite-conf.json found in root')
    }

	fs.copyFileSync(pathToFile, path.join(jivosite, filename));

	/* make valid js file from json */
	const data = fs.readFileSync(path.join(jivosite, filename))
	const fd = fs.openSync(path.join(jivosite, filename), 'w+')
	const insert = new Buffer("jivo_config = ")
	fs.writeSync(fd, insert, 0, insert.length, 0)
	fs.writeSync(fd, data, 0, data.length, insert.length)
	fs.close(fd, (err) => {
		if (err) throw err;
	});

    return true;
};
