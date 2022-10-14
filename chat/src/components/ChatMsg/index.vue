<template>
  <div class="chat-msg">
    <!-- 头部 -->
    <div class="header">
      <section class="nikename">{{ detail.friendNikeName }}</section>
      <section class="more" title="聊天设置">...</section>
    </div>
    <!-- 信息 -->
    <div
      class="msg-box"
      ref="msg-box"
      :style="{ height: size.height - 60 - 120 + 'px' }"
    >
      <message
        v-for="(message, index) in messages"
        :key="index"
        :message="message"
        :detail="detail"
      />
    </div>
    <!-- 输入 -->
    <div class="msg-in">
      <section class="orther">
        <span class="el-icon-folder-opened"></span>
        <span class="el-icon-video-camera" @click="videoCamera('video')"></span>
        <span class="el-icon-phone" @click="videoCamera('audio')"></span>
      </section>
      <el-form @keyup.native.enter="sendMsg">
        <el-input
          class="msg-in-text"
          type="textarea"
          :rows="2"
          placeholder="请输入内容"
          v-model="textarea"
          resize="none"
        >
        </el-input>
        <el-button
          @click="sendMsg"
          :disabled="textarea.length > 0 ? false : true"
          type="success"
          >发送</el-button
        >
      </el-form>
    </div>
  </div>
</template>

<script>
import Message from "./Message.vue";
import { mapState } from "vuex";
import dayjs from "dayjs";

export default {
  components: { Message },
  name: "ChatMsg",
  props: ["detail", "page"],
  data() {
    return {
      textarea: "",
    };
  },
  computed: {
    ...mapState({
      user: (state) => state.user,
    }),
    size() {
      return this.$store.state.size;
    },
    messages() {
      if (this.user.messages[this.detail.friendDetail.id]) {
        return this.user.messages[this.detail.friendDetail.id];
      } else {
        return null;
      }
    },
  },
  watch: {
    messages: {
      handler(val, oldVal) {
        if (val != oldVal) {
          // 重置未读数
          this.$store.dispatch("resetUnreadCount", this.detail.friendDetail.id);
          this.user.notify.chatMessageNotify = false;
          this.user.notify = {...this.user.notify};
          this.$nextTick(() => {
            this.$refs["msg-box"].scrollTop =
              this.$refs["msg-box"].scrollHeight - 90;
          });
        }
      },
    },
  },
  created() {
    this.$nextTick(() => {
      this.$refs["msg-box"].scrollTop = this.$refs["msg-box"].scrollHeight - 90;
    });
  },
  methods: {
    async sendMsg() {
      // 匹配是否为空白字符
      if (!/\S/.test(this.textarea)) {
        // 格式不正确
        this.$message("不能发送空白信息");
        this.textarea = "";
        return;
      }
      if (this.textarea[this.textarea.length - 1] == "\n") {
        this.textarea = this.textarea.substring(0, this.textarea.length - 1);
      }
      // 判断上次聊天时间与此次聊天时间间隔是否大于3分钟，大于则发送一条时间信息
      if (this.$store.state.user.messages[this.detail.friendDetail.id]) {
        let length =
          this.$store.state.user.messages[this.detail.friendDetail.id].length;
        let date = new Date(
          this.$store.state.user.messages[this.detail.friendDetail.id][
            length - 1
          ].createTime
        ).getTime();
        let nowDate = new Date().getTime();
        if (nowDate - date > 3 * 60 * 1000) {
          let data = {
            fromId: this.$store.state.user.user.id,
            toId: this.detail.friendDetail.id,
            message: dayjs().format("YYYY-MM-DD HH:mm:ss"),
            type: 2,
          };
          let res = await this.$axios({
            url: "/friendMessage",
            method: "post",
            data: data,
          });
          this.$store.commit("addMessage1", res.data);
          this.$store.dispatch("chatMessageList", {
            friendId: data.toId,
            isFriend: false,
          });
        }
      }

      let data = {
        fromId: this.$store.state.user.user.id,
        toId: this.detail.friendDetail.id,
        message: this.textarea,
        type: 0,
      };
      this.textarea = "";
      let res = await this.$axios({
        url: "/friendMessage",
        method: "post",
        data: data,
      });
      this.$store.commit("addMessage1", res.data);
      this.$store.dispatch("chatMessageList", {
        friendId: data.toId,
        isFriend: false,
      });
      setTimeout(() => {
        this.$refs["msg-box"].scrollTop =
          this.$refs["msg-box"].scrollHeight - 90;
      }, 100);
    },
    async videoCamera(callType) {
      this.$electron.ipcRenderer.send("call");
      setTimeout(() => {
        this.detail.token = this.$C.token;
        this.detail.callType = callType;
        this.$electron.ipcRenderer.send(
          "callFriend",
          JSON.stringify(this.detail)
        );
      }, 1000);
    },
  },
};
</script>

<style lang="scss" scoped>
$bg-color: #f5f5f5;

.chat-msg {
  user-select: none;
  overflow: hidden;
  box-sizing: border-box;
  position: relative;
  display: flex;
  flex-direction: column;
  width: 100%;
  text-align: left;

  // 头部
  .header {
    display: flex;
    justify-content: space-between;
    box-sizing: border-box;
    width: 100%;
    height: 30px;
    border-bottom: 1px solid #ccc;
    -webkit-app-region: drag;

    .nikename {
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
      width: 65%;
      height: 30px;
      padding-left: 30px;
      font-size: 19px;
    }

    .more {
      cursor: pointer;
      height: 30px;
      line-height: 20px;
      padding-right: 10px;
      color: #666;
      font-size: 25px;

      &:hover {
        color: black;
      }
    }
  }
  // 信息内容
  .msg-box {
    box-sizing: border-box;
    padding: 0 25px;
    overflow-y: auto;
    border-bottom: 1px solid #ccc;
    background-color: #f5f5f5;
  }
  // 输入框
  .msg-in {
    box-sizing: border-box;
    height: 120px;
    background-color: $bg-color;

    .orther {
      z-index: 1;
      position: absolute;
      line-height: 30px;
      font-size: 20px;

      span {
        padding: 0 5px;

        &:hover {
          color: green;
          cursor: pointer;
        }
      }
    }

    ::v-deep .el-textarea__inner {
      margin-top: 30px;
      border: none;
      border-top: 1px solid #ccc;
      border-radius: 0;
      background-color: transparent;
      font-size: 16px;
    }

    .el-button--success {
      position: absolute;
      right: 10px;
      bottom: 5px;
      height: 20px;
      line-height: 20px;
      padding: 0 20px;
    }
  }
}
</style>