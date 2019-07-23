# cordova-plugin-jivosite

wrap around https://github.com/JivoSite/MobileSdk for use as cordova plugin

## tested cordova versions
- cordova-lib@9.0.1
- cordova-android@8.0.0


## jivo-config
- project root should contain file jivosite-conf.json with widget-id and site-id (from https://github.com/JivoSite/MobileSdk)
```json
{
	/*
	 "plane_color":"red",

	 "agentMessage_bg_color":'green', //цвет агентского сообщения
	 "agentMessage_txt_color":'blue', //цвет текста агентского сообщения

	 "clientMessage_bg_color":'yellow', //цвет клиентского сообщения
	 "clientMessage_txt_color":'black', //цвет текста клиентского сообщения
	*/
	"plane_color":"#ffaa22",
	"active_message": "Здравствуйте! Я могу вам чем-то помочь?",
	"widget_id": "jWQigXS11b", //widget_id
	"site_id": 431188, //site_id
	"app_link": "",//ссылка, которая будет светиться у оператора
	"placeholder": "Введите сообщение",
	"secure": true
}

```


## js api

### open_chat(userToken = null)
This will start activity with jivochat. parameter userToken is provided to use jivosite `setUserToken`

```js
cordova.plugins.JivoSite.openChat(0099887766); // some userToken
```


