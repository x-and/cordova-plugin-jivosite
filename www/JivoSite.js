var exec = require('cordova/exec');

exports.openChat = function (success, error) {
    exec(success, error, 'JivoSite', 'open_chat', []);
};
