const {app, BrowserWindow} = require('electron');
const url = require('url');
const path = require('path');

// const __filename = url(import.meta.url);
// const __dirname = path.dirname(__filename);

function onReady () {
	win = new BrowserWindow({width: 1600, height: 1200})
	win.loadURL(url.format({
		pathname: path.join(
			__dirname,
			'dist/frontend/index.html'),
		protocol: 'file:',
		slashes: true
	}))
}

app.on('ready', onReady);
