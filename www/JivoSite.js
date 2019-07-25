var exec = require('cordova/exec');

exports.openChat = function (opts, success, error) {
    exec(success, error, 'JivoSite', 'open_chat', [opts || {}]);
};
