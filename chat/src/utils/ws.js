import ElementUI from "element-ui";
import router from "@/router/index.js";
import store from "@/store";

export default class ws{
  constructor(url){
    this.url = url;
    this.socket = null;
    this.isOnline = false; // 是否在线
    this.timeout = 30 * 1000; // 心跳
    this.isConnect = false; // 是否重连
    this.sendTimeoutFun = null;
    this.serverTimeoutFun = null;
    this.loading = null;
    this.reConnectCount = 0;// 重连次数
    this.wsCallback = this.onMessage;
    this.close = this.socketClose;
    this.connectSocket();
  }
  startHeartCheck(){
    let that = this;
    that.sendTimeoutFun = setTimeout(() => {
      that.socket.send("ping");
      that.serverTimeoutFun = setTimeout( () => {
        that.onClose();
      }, that.timeout)
    }, that.timeout)
  }
  resetHeartCheck(){
    clearTimeout(this.sendTimeoutFun);
    clearTimeout(this.serverTimeoutFun);
  }

  connectSocket(){
    let that = this;
    this.socket = new WebSocket(this.url);
    this.socket.onopen = function(){
      that.onOpen();
    }
    this.socket.onclose = function(){
      that.onClose();
    };
    // this.socket.onmessage = function(e){
    //   that.onMessage(e)
    // };
    this.socket.onerror = function(){
      that.onError();
    };
  }

  onOpen(){
    try{
      this.loading.close();
    } catch{
      
    }
    this.isOnline = true;
    this.isConnect = false;
    this.resetHeartCheck();
    this.startHeartCheck();
  }
  onClose(e){
    this.close();
    this.loading = ElementUI.Loading.service({
      text: "尝试重连中"
    });
    this.resetHeartCheck();
    setTimeout(() => {
      this.reConnectCount++;
      this.reConnect();
    }, 5000);
    if(this.reConnectCount === 5){
      localStorage.removeItem("user_info");
      localStorage.removeItem("token");
      store.state.user.ws = null;
      router.replace("/login");
      window.location.reload();
    }
  }
  onError(){
    if(!this.isConnect)
      ElementUI.Message({
        message: 'error',
        type: 'error'
      });
  }
  onMessage(callback){
    this.socket.onmessage = ({data}) => {
      try{
        let d = JSON.parse(data);
        callback && callback(d)
        // console.log(d);
      } catch(e){
        
      }
      // 重置心跳
      this.resetHeartCheck();
      this.startHeartCheck();
    }
  }
  reConnect(){
    if(this.isConnect) {
      this.connectSocket();
      return;
    }
    this.isConnect = true;
    this.connectSocket();
    
  }
  socketClose(){
    this.socket.close();
    this.isOnline = false;
    // 重置心跳
    this.resetHeartCheck();
    this.startHeartCheck();
  }

}