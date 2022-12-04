import axios from "./request";
const { ipcRenderer } = window.require("electron");
export default class rtc {

  constructor(fromId, toId, isCall, option) {
    this.pc = null;
    this.fromId = fromId;
    this.toId = toId;
    this.isCall = isCall;
    this.stream = null;
    this.localVideo = document.querySelector("#localVideo");// 本地视频
    this.remoteVideo = document.querySelector("#remoteVideo");// 远程视频
    this.timeout = 15 * 1000;// 35秒
    this.timeoutFun = null;// 超时处理
    this.callTimer = 0;// 通话时间
    this.callTimerFun = null;// 通话时间
    this.connectState = "";// 连接状态
    this.initOption = option ? option : {
      video: { width: 80, height: 150},
      audio: true
    };
  }

  // 开始
  async startHandle(str) {
    // 捕捉本地流
    await navigator.mediaDevices.getUserMedia(this.initOption)
      .then((stream) => {
        this.stream = stream;
        this.gotLocalMediaStream(stream);
        this.pc = new RTCPeerConnection();
        // 将本地流添加到连接中
        for (const track of this.stream.getTracks()) {
          this.pc.addTrack(track, this.stream);
        }
        // 监听ICE
        this.pc.addEventListener("icecandidate", (ev) => {
          this.handleICECandidateEvent(ev);
        });
        // 监听远程流
        this.pc.addEventListener("track", (ev) => {
          this.gotRemoteMediaStream(ev)
        });
        this.pc.addEventListener("connectionstatechange", ev => {
          this.connectState = this.pc.connectionState;
          switch (this.pc.connectionState) {
            case "disconnected":
              ipcRenderer.send("closeNewWin");
              break;
            case "connected":
              clearTimeout(this.timeoutFun);// 清除超时
              let that = this;
              this.callTimerFun = setInterval(() => {
                that.callTimer++;
              }, 1000);
              break;
          }
        });
        let that = this;
        this.timeoutFun = setTimeout(() => {
          ipcRenderer.send("closeNewWin", JSON.stringify({
            type: 3,
            msg: "对方未接听",
            data: {
              fromId: that.fromId,
              toId: that.toId,
              message: "未接听",
              type: 0
            }
          }));
        }, this.timeout);
        
        // 发送连接请求
        axios({
          url: "/friendMessage/call",
          method: 'post',
          data: {
            fromId: this.fromId,
            toId: this.toId,
            offer: {
              type: str,
              option: this.initOption
            },
          }
        }).catch(() => {
          ipcRenderer.send(
            "closeNewWin",
            JSON.stringify({
              type: 2,
              msg: "对方不是你好友",
            })
          );
        })
        
      })
      .catch((err) => {
        this.error(err);
      })

  }

  // handleVideoOfferMsg
  async handleVideoOfferMsg(data) {
    let description = data.offer;
    switch (description.type) {
      case "connect":
        this.startHandle("start");// 创建连接
        break;

      case "start":
        // 开始
        this.handleNegotiationNeededEvent();// 发送offer
        break;

      case "offer":
        // 接收offer
        await this.pc.setRemoteDescription(new RTCSessionDescription(description))
          .then(() => {

          })
          .catch((err) => {
            this.error("1", err)
          })
        // 创建应答
        await this.pc.createAnswer()
          .then(async (answer) => {
            // 将应答设置为本地描述
            this.pc.setLocalDescription(answer);
            // 发送应答
            await axios({
              url: "/friendMessage/call",
              method: 'post',
              data: {
                fromId: this.fromId,
                toId: this.toId,
                offer: answer,
              }
            });
          })
          .catch((err) => {
            this.error("2", err)
          })

        break;

      case "answer": // 处理应答
        this.pc.setRemoteDescription(new RTCSessionDescription(description))
          .then(() => {
            
          })
          .catch((err) => {
            this.error("3", err)
          })
        break;

      case "icecandidate":
        let iceCandidate = new RTCIceCandidate(description.icecandidate);
        // 将ice添加到远程端
        this.pc.addIceCandidate(iceCandidate)
          .then(() => {
            
          })
          .catch((err) => {
            this.error(err)
          });
        break;

      case "end":// 结束通话
        ipcRenderer.send(
          "closeNewWin",
          JSON.stringify({
            type: 1,
            msg: "已挂断",
            data: {
              fromId: that.fromId,
              toId: that.toId,
              message: "已挂断",
              type: 0
            }
          })
        );
        break;
      case "end-call":// 对方结束通话
        ipcRenderer.send(
          "closeNewWin",
          JSON.stringify({
            type: 4,
            msg: "已挂断",
            data: {}
          })
        );
        break;
    }
  }

  // 呼叫方处理
  async handleNegotiationNeededEvent() {
    await this.pc.createOffer() // 创建offer
      .then((offer) => {
        // 将offer设置为本地描述
        this.pc.setLocalDescription(offer)
          .then(async () => {
            // 发送offer
            await axios({
              url: "/friendMessage/call",
              method: 'post',
              data: {
                fromId: this.fromId,
                toId: this.toId,
                offer: offer,
                // option: this.initOption
              }
            });
            

          })
          .catch((err) => {
            this.error("4", err)
          })
      })
      .catch(err => {
        this.error("5", err)
      });

  }
  // 处理ICE候选人
  async handleICECandidateEvent(event) {
    if (event.candidate)
      await axios({
        url: "/friendMessage/call",
        method: 'post',
        data: {
          fromId: this.fromId,
          toId: this.toId,
          offer: {
            type: "icecandidate",
            icecandidate: event.candidate
          },
        }
      })
  }


  // 本地流
  gotLocalMediaStream(mediaStream) {
    for (const m of mediaStream.getTracks()) {
      if (m.kind == "audio") {
        m.enabled = false;// 关闭本地视频声音
      }
    }
    this.localVideo.srcObject = mediaStream;
  }

  // 接收远程端流
  gotRemoteMediaStream(event) {
    if (event.streams[0]) {
      this.remoteVideo.srcObject = event.streams[0];
    }
  }

  error(err) {
    ipcRenderer.send("closeNewWin", JSON.stringify({
      type: 2,
      msg: "设备被占用"
    }));
  }

}
