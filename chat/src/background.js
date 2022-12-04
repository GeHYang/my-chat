'use strict'

import { app, protocol, BrowserWindow, ipcMain, session, desktopCapturer } from 'electron'
import { createProtocol } from 'vue-cli-plugin-electron-builder/lib';
import path from 'path';


protocol.registerSchemesAsPrivileged([
  { scheme: 'app', privileges: { secure: true, standard: true } }
]);

let win;
const winURL = process.env.NODE_ENV === 'development'
  ? `http://localhost:8080`
  : `file://${__dirname}/index.html`;

async function createWindow() {
  session.defaultSession.loadExtension(path.resolve(__dirname, "../vue-devtools/shells/chrome"));

  win = new BrowserWindow({
    width: 875,
    minWidth: 700,
    height: 655,
    minHeight: 500,
    frame: false,
    maximizable: false,// 禁止双击最大化
    webPreferences: {
      // 预加载脚本
      nodeIntegration: true,
      contextIsolation: false,
      enableRemoteModule: true,
    }
  });

  if (process.env.WEBPACK_DEV_SERVER_URL) {
    await win.loadURL(process.env.WEBPACK_DEV_SERVER_URL)
    if (!process.env.IS_TEST) win.webContents.openDevTools()// 开启控制台
  } else {
    createProtocol('app')

    win.loadURL('app://./index.html')
  }
  //创建完页面后
  win.hookWindowMessage(278, function (e) {
    win.setEnabled(false);//窗口禁用
    setTimeout(() => {
      win.setEnabled(true);
    }, 100) //延时太快会立刻启动，太慢会妨碍窗口其他操作，可自行测试最佳时间
    return true
  })

}

app.on('ready', async (e) => {
  createWindow();
})

// 关闭程序
ipcMain.on('close', () => {
  // if(dialog.showMessageBoxSync(win, {
  //   type: 'info',
  //   buttons: ['直接退出', '取消'],
  //   title: '提示',
  //   message: '确定要退出吗？',
  //   cancelId: 1
  // }) !== 1){
  app.exit()
  // }
})
// 最大化
ipcMain.on('maxWin', () => {
  // win.maximize();
  win.reload()
})
// 取消最大化
ipcMain.on('unmaxWin', () => {
  // win.unmaximize();
  win.reload()
})
// 最小化
ipcMain.on('minWin', () => {
  // win.minimize();
  if (!process.env.IS_TEST) win.webContents.openDevTools()// 开启控制台
});



/************************************************************************************** */
let newWin;
ipcMain.on("call", async () => {
  newWin = new BrowserWindow({
    width: 370,
    height: 700,
    // minHeight: 700,
    // minWidth: 370,
    // maxHeight: 700,
    // maxWidth: 370,
    parent: win,
    frame: false,
    webPreferences: {
      nodeIntegration: true,
      contextIsolation: false,
      enableRemoteModule: true
    }
  });
  if (!process.env.IS_TEST) newWin.webContents.openDevTools()// 开启控制台
  await newWin.loadURL(`${winURL}#/callVideo`);

});

ipcMain.on("closeNewWin", (e, data) => {
  // if (dialog.showMessageBoxSync(newWin, {
  //   type: 'info',
  //   buttons: ['关闭', '取消'],
  //   title: '提示',
  //   message: '关闭页面将会结束通话，是否关闭？',
  //   cancelId: 1
  // }) !== 1) {
  win.webContents.send("end-call", data);// 挂断
  newWin && newWin.destroy();
  // newWin = null;
  // }
});

// 拨打
ipcMain.on("callFriend", (e, data) => {
  newWin.webContents.send("call-friend", data);
});
// 结束通话
ipcMain.on("end-of-call", () => {
  win.webContents.send("end-call", 1);// 挂断
});
// 接收电话
ipcMain.on("recvCallFriend", (e, data) => {
  newWin.webContents.send("recv-call-friend", data);
})

