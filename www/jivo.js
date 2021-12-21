var my_jivo_config = jivo_config;
var data = {
	chat: { mode: 'offline', open: null, dom: false },
	connection_online: navigator.onLine
};
var lang = 'en';
var canUploadFiles = false;

var iter = 0;

var state = 'loading';

var messages = {
	'loading' :  'Пожалуйста, подождите. Чат загружается...',
	'loading_timeout': 'Чат загружается дольше обычного...',
	'offline': 'Нет соединения с интернетом. Мы вернёмся, как только он появится!',
	'no_dom': 'В данный момент чат недоступен, попробуйте позже',
	'no_operators':  'В данный момент чат недоступен, попробуйте позже', // TODO 'В данный момент все операторы заняты. Оставьте сообщение в этой форме и мы обязательно ответим',
	'ok' : ''
};

window.addEventListener('online', () => {
	var prev = data.connection_online;
	data.connection_online = true;
	if (!prev) {
		JivoInterface.send('page.reload', '');
		window.location.reload(); // ios can do it
	}
});
window.addEventListener('offline', () => data.connection_online = false);

/*
	Периодически проверяем статус чата, открытие чата и наличие DOM чата
*/
var interval = setInterval(
	function() {
		iter++;
		stateChecker();
	},
	3000
);

var stateChecker = function() {
	console.log('stateChecker', iter);
	if (window['jivo_api']) {
		data.chat.mode = jivo_api.chatMode();
		data.chat.open = jivo_api.open()['result'];
		data.chat.dom = document.querySelector('[class*=mobileContainer_]') != null;
	}

	/* Если DOM построен, считаем что загрузились успешно.*/
	if (data.chat.dom) {
		clearInterval(interval);
		document.querySelector('.loading').remove();
	}

	if (!data.connection_online) {
		state = 'offline';
	} else if (!window['jivo_api']) {
		state = iter < 5 ? 'loading' : 'loading_timeout';
	} else if (!data.chat.dom && data.chat.mode == 'offline') {
		state = 'no_operators';
	} else if (!data.chat.dom || !data.chat.open) {
		state = 'no_dom';
	} else {
		state = 'ok';
	}

	if (iter > 10 && state != 'ok' && data.connection_online) {
		state = 'no_dom';
	}

	console.log('stateChecker end', state);
	var el = document.querySelector('.loading > .loading-text-bottom');
	// TODO попробовать отдебажить, как будет работать кастомная отправка оффлайн-сообщения, когда JS прогрузился а DOM`а нет
	// document.querySelector('.fake-container-input-show').classList.toggle('hidden', state != 'no_dom' && data.chat.open == 'open');
	if (el) {
		document.querySelector('.loading > .loading-text-bottom').innerText = messages[state] ?? '';
	}
};

window.onload = function() {
	JivoInterface.send('page.ready', '');
};

document.onclick = function(event) {
	var el = event.target;
	if (el.tagName == 'A' && el.href) {
		event.preventDefault();
		JivoInterface.send('url.click', "'" + el.href + "'");
	}
	if (el.classList.contains('fake-send-button') && state === 'no_dom' && data.chat.open == 'open') {
		var status = jivo_api.sendMessage(document.querySelector('.fake-text-area').value)
	}
}

function loadJivosite() {
	var old = document.querySelector('#jivosite_js');
	if (old != null) {
		old.remove();
	}
	var script = document.createElement('script');
	script.type = 'text/javascript';
	script.src = 'https://code.jivosite.com/script/widget/%s.js'.replace('%s', my_jivo_config.widget_id);
	script.id = 'jivosite_js';
	document.getElementsByTagName('head')[0].appendChild(script);
	iter = 0;
	stateChecker()
}

function jivo_onLoadCallback() {
	console.log('jivo_onLoadCallback');
	jivo_api.showProactiveInvitation(my_jivo_config.active_message, my_jivo_config.department_id);

	jivo_api.setContactInfo({
		description: "Мобильное приложение"
	});

	applyCustomData();

	JivoInterface.send('chat.ready', '');jivo

	stateChecker()
}

function setUserToken(str) {
	console.log('setUserToken!', str);
	if (window['jivo_api']) {
		jivo_api.setUserToken(str);
	}
}

function setPayload(str) {
	console.log('setPayload', str);
	if (!str) { return; }
	if (typeof str === 'object') {
		data = Object.assign(data, str);
		applyCustomData();
	}
}

function setData(lang_, canUploadFiles_) {
	lang  = lang_;
	canUploadFiles = canUploadFiles_;
	document.querySelector('body').classList.toggle('no-files', !canUploadFiles)
}

function applyCustomData() {
	if (window['jivo_api'] && data.customData) {
		jivo_api.setCustomData(data.customData);
	}
}

loadJivosite();
