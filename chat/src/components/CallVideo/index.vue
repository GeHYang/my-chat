<template>
  <div class="call">
    <div
      class="bg"
      :style="{ 'background-image': 'url(' + avatar + ')' }"
    ></div>
    <video id="localVideo" autoplay="autoplay"></video>
    <video id="remoteVideo" autoplay="autoplay"></video>
    <div class="btns">
      <button @click="closeWin" class="el-icon-close" title="关闭"></button>
      <button
        @click="noFullWin"
        class="el-icon-full-screen full"
        title="全屏"
      ></button>
      <div>
        <section ref="answerBtn" style="display: none">
          <a class="hang-up el-icon-phone" @click="answer"></a>
          <span>接听</span>
        </section>
        <section>
          <a class="hang-up el-icon-phone" @click="closeWin"></a>
          <span>挂断</span>
        </section>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import rtc from "@/utils/rtc";

export default {
  data() {
    return {
      isFull: false,
      detail: undefined,
      offer: null,
      avatar: "http://192.168.1.100:8090/static/avatars/ghr.jpg",
    };
  },
  computed: {
    ...mapState({
      user: (state) => state.user,
    }),
  },
  watch: {
    detail: {
      handler(value, oldVal) {
        if (value == oldVal) return;
        if (value.token) this.$C.token = value.token;
        this.avatar = value.friendDetail.avatar;
        if (value.callType === "video")
          this.startRTC(value.userId, value.friendId, true);
        else if (value.callType === "audio") {
          this.startRTC(value.userId, value.friendId, true, {
            video: false,
            audio: true,
          });
        }
      },
    },
    offer: {
      async handler(value, oldVal) {
        if (value == oldVal) return;
        value.token && (this.$C.token = value.token);
        if (!this.user.rtc) {
          this.$refs["answerBtn"].style.display = "";
          this.user.rtc = new rtc(value.toId, value.fromId, false);
        } else {
          this.user.rtc.handleVideoOfferMsg(value);
        }
      },
    },
  },
  beforeCreate() {
    let that = this;

    this.$electron.ipcRenderer.on("call-friend", (_event, value) => {
      that.detail = JSON.parse(value);
    });
    this.$electron.ipcRenderer.on("recv-call-friend", (_event, value) => {
      that.offer = JSON.parse(value);
    });
  },
  mounted() {},
  methods: {
    async closeWin() {
      // this.user.rtc = null;
      this.$axios({
        url: "/friendMessage/call",
        method: "post",
        data: {
          fromId: this.user.rtc.fromId,
          toId: this.user.rtc.toId,
          offer: {
            type: "end",
          },
        },
      });
      
      this.$electron.ipcRenderer.send(
        "closeNewWin",
        JSON.stringify({
          type: 1,
          msg: "结束通话",
          data: {
            fromId: this.user.rtc.fromId,
            toId: this.user.rtc.toId,
            message: "已挂断",
            type: 0,
          },
        })
      );
    },
    noFullWin() {
      // this.$electron.ipcRenderer.send("noFullWin");
    },
    async startRTC(userId, friendId, isCall, option) {
      this.user.rtc = new rtc(userId, friendId, isCall, option);
      this.user.rtc.startHandle("connect");
    },
    answer() {
      this.$loading("连接中");
      this.$refs["answerBtn"].style.display = "none";
      this.user.rtc.handleVideoOfferMsg(this.offer);
    },
  },
};
</script>

<style lang="scss" scoped>
body {
  overflow: hidden;
}
.call {
  // -webkit-app-region: no-drag;
  position: relative;
  z-index: 10000;
  position: fixed;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  box-sizing: border-box;
  padding: 20px 0px 0px;
  background-color: black;

  .bg {
    box-sizing: border-box;
    position: absolute;
    top: 30px;
    left: 0;
    width: 100%;
    height: 100%;
    background-repeat: no-repeat;
    background-size: cover;
    background-position: center;
    filter: blur(13px);
  }

  #localVideo {
    cursor: pointer;
    // -webkit-app-region: no-drag;
    z-index: 1;
    position: absolute;
    top: 40px;
    left: 10px;
    width: 80px;
    height: 120px;
    border: none;
    border-radius: 5px;
    background-color: transparent;
  }
  #remoteVideo {
    position: absolute;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: transparent;
  }
  .btns {
    button {
      position: absolute;
      top: 0;
      right: 0;
      width: 20px;
      height: 20px;
      border: none;
      font-size: 18px;
      background-color: rgba(255, 255, 255, 0.3);
      cursor: pointer;
      -webkit-app-region: no-drag;
      &.full {
        right: 20px;
      }

      &:hover {
        background-color: red;
        color: white;
      }
    }
    div {
      box-sizing: border-box;
      position: absolute;
      bottom: 30px;
      width: 100%;
      display: flex;
      justify-content: space-between;
      section {
        // position: absolute;
        // bottom: 30px;
        display: flex;
        flex-direction: column;
        align-items: center;
        width: 100%;

        .hang-up {
          width: 30px;
          height: 30px;
          line-height: 30px;
          padding: 8px;
          border-radius: 50%;
          background-color: red;
          color: white;
          font-size: 25px;
          transform: rotate(135deg);
          -webkit-app-region: no-drag;
          cursor: pointer;

          &:hover {
            background-color: rgb(155, 7, 7);
            color: #ccc;
          }
        }
        &:nth-child(1) > .hang-up {
          background-color: green;
        }
        span {
          margin-top: 5px;
          width: 40px;
          font-size: 12px;
          color: rgba(255, 255, 255, 0.7);
        }
      }
    }
  }
}
</style>