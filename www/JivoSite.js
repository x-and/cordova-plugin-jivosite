var exec = require('cordova/exec');

exports.openChat = function (userToken, success, error) {
    exec(success, error, 'JivoSite', 'open_chat', [userToken ? userToken.toString() : '']);
};
