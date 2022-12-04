<template>
  <div class="home">
    <menu-bar @toPage="toPage" :toPage1="chatListPage" />
    <div style="border-right: 1px solid #f5f5f5">
      <!-- 搜索 -->
      <div class="search-box">
        <section title="搜索">
          <label class="search-icon el-icon-search" @click="search"></label>
          <input id="search" v-model="searchUser" :placeholder="searchText" />
        </section>
        <section title="添加" @click="showAdd">
          <span class="orther el-icon-circle-plus-outline"></span>
        </section>
      </div>
      <chat-list
        :chatListPage="chatListPage"
        :chatListItems="dataList[chatListPage]"
        @details-intro="showDetails"
      />
    </div>
    <div>
      <my-right
        :details="details"
        @sendMessageFromUserInfo="sendMessageFromUserInfo"
      />
    </div>
  </div>
</template>

<script>
/**
 * 模拟数据
 */

import MenuBar from "../../components/MenuBar";
import ChatList from "../../components/ChatList";
import MyRight from "@/components/MyRight";
import DH from "@/utils/DHUtil.js"
import { mapState } from "vuex";
import qs from "qs";

export default {
  name: "home",
  components: { MenuBar, ChatList, MyRight },
  data() {
    return {
      chatListPage: 0,
      dataList: [
        new Array(),
        new Array(),
        new Array(),
        new Array(),
        new Array(),
      ],
      details: undefined,
      user_info: {},
      ws: undefined,
      user: {},
      searchText: "搜索",
      searchUser: "",
    };
  },
  computed: {
    ...mapState({
      state: (state) => state.user,
    }),
  },

  watch: {
    "state.chatList": {
      immediate: true,
      deep: true,
      handler(val) {
        this.dataList[0] = val;
        this.dataList = JSON.parse(JSON.stringify(this.dataList));
      },
    },

    "$store.state.user.callFriend": {
      deep: true,
      handler(val) {
        this.recvCall(val);
      },
    },
    "state.addFriendNotify": {
      deep: true,
      handler(val) {
        this.dataList[3] = val;
        this.dataList = JSON.parse(JSON.stringify(this.dataList));
        this.findFriends(true);
      },
    },
    "state.friendList": {
      deep: true,
      handler(val) {
        this.dataList[1] = val;
        this.dataList = JSON.parse(JSON.stringify(this.dataList));
      },
    },
    chatListPage(val) {
      if (val == 2 && this.searchUser) {
        this.searchUser = "";
      }
    },
  },
  async beforeCreate() {
    let user_info = JSON.parse(localStorage.getItem("user_info"));
    let cache = localStorage.getItem(user_info.id + "cache");
    cache && (this.$store.state.user = JSON.parse(cache));
  },
  created() {
    try {
      window.addEventListener("beforeunload", (e) => this.cache());
      if (!this.$store.state.user.user) {
        this.$store.state.user.user = JSON.parse(
          localStorage.getItem("user_info")
        );
      }
      if (!this.$C.token) {
        if (!localStorage.getItem("token")) {
          return;
        }
        this.$C.token = localStorage.getItem("token");
      }
      this.user = this.$store.state.user.user;

    } catch (e) {
      this.$router.replace("/login");
    }
  },
  async mounted() {
    this.$store.dispatch("initWebSocket");
    // 查询好友信息
    if (this.$store.state.user.friendList.length <= 0) {
      await this.findFriends();
    }
    
    // 验证密钥
    this.verifySecret();
    
    await this.$axios({
      url: "/friendMessage/getAllUnreadMessage",
      method: "post",
      data: {
        fromId: this.user.id,
      },
    });
    this.toPage(0);
    // 聊天页面点击头像显示好友信息
    this.$bus.$on("showFriendDetail", async (detail) => {
      await this.toPage(1);
      this.showDetails(detail);
    });
    // 点击个人中心显示自己信息
    this.$bus.$on("showMeDetail", (detail) => {
      this.showDetails(detail);
    });
    // 点击修改密码
    this.$bus.$on("showResetPwd", (detail, flag) => {
      this.showDetails(detail, flag);
    });
    // 关闭right
    this.$bus.$on("closeMyRight", () => {
      this.showDetails(null, false);
    });
    // 根据好友关闭右边展示栏
    this.$bus.$on("closeMyRightByFriend", (friend) => {
      if (
        this.details?.detail &&
        this.details.detail.friendId === friend.friendId
      ) {
        delete this.details.detail;
        this.details = JSON.parse(JSON.stringify(this.details));
      }
    });

    let that = this;
    this.$electron.ipcRenderer.on("end-call", async (e, data) => {
      data = JSON.parse(data);
      if (data.type === 1) {
        that.$message.warning("通话结束");
        await that.$axios({
          url: "/friendMessage/call",
          method: "post",
          data: {
            fromId: data.data.fromId,
            toId: data.data.toId,
            offer: {
              type: "end-call",
            },
          },
        });
        this.$bus.$emit("sendMsg", {toId: data.data.toId, msg: data.data.message});
      } else if (data.type === 2) {
        that.$message.error(data.msg);
      } else if (data.type === 3) {
        that.$message.warning(data.msg);
        await that.$axios({
          url: "/friendMessage/call",
          method: "post",
          data: {
            fromId: data.data.fromId,
            toId: data.data.toId,
            offer: {
              type: "end-call",
            },
          },
        });
        // 发送信息
        this.$bus.$emit("sendMsg", {toId: data.data.toId, msg: data.data.message});
      } else if(data.type === 4){
        that.$message.warning("对方已挂断");
      }
    });
  },
  methods: {
    async toPage(page) {
      this.$bus.$emit("closeMyRight");
      this.searchText = "搜索";
      if (page === 0) {
        this.findMessageList();
      } else if (page === 1) {
        await this.findFriends();
      } else if (page === 2) {
        this.showAdd(true);
      } else if(page === 3){
        this.state.notify.addFriendNotify = false;
      }
      this.chatListPage = page;
    },
    async showDetails(e, flag) {
      if (this.chatListPage === 0 && e) {
        this.$store.dispatch("resetUnreadCount", e.friendId);
      }
      this.details = {
        chatListPage: this.chatListPage,
        detail: e,
        flag: flag,
      };

      this.details = { ...this.details };
    },
    // 查询好友列表
    async findFriends(flag) {
      if (this.state.friendList.length && !flag) {
        this.dataList[1] = this.state.friendList;
        return;
      }
      let result = await this.$axios({
        url: "/friend/findFriends",
        params: {
          userId: this.$store.state.user.user.id,
        },
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
        method: "post",
      });
      this.state.friendList = result.data;
      this.dataList[1] = result.data;
      this.dataList = JSON.parse(JSON.stringify(this.dataList));
    },
    // 查询最近信息列表
    findMessageList(e) {
      if (this.state.chatList.length > 0 && this.state.chatList[0]) {
        // e && this.state.chatList.unshift(e);
        this.dataList[0] = this.state.chatList;
      } else {
        if (e) this.dataList[0].push(e);
      }
    },
    sendMessageFromUserInfo(e) {
      this.chatListPage = 0;
      this.$store.dispatch("chatMessageList", {
        friendId: e.friendId,
        isFriend: false,
      });
      this.findMessageList(e);
      this.showDetails(this.dataList[0].length > 0 ? this.dataList[0][0] : e);
    },
    cache() {
      localStorage.setItem(
        this.user.id + "cache",
        JSON.stringify({ ...this.state, ws: null })
      );
    },
    // 接到呼叫，打开新页面
    recvCall(val) {
      if (val.offer.type === "connect") {
        for(const f of this.state.friendList){
          if(f.friendId == val.fromId){
            val.detail = f.friendDetail;
            break;
          }
        }
        this.$electron.ipcRenderer.send("call");
        setTimeout(() => {
          val.token = this.$C.token;
          this.$electron.ipcRenderer.send(
            "recvCallFriend",
            JSON.stringify(val)
          );
        }, 2000);
      } else {
        setTimeout(() => {
          val.token = null;
          this.$electron.ipcRenderer.send(
            "recvCallFriend",
            JSON.stringify(val)
          );
        }, 2000);
      }
    },
    // 添加
    showAdd(e) {
      e !== true && this.toPage(2);
      document.querySelector("#search").focus();
      this.searchText = "请输入用户id";
    },
    // 搜索
    async search() {
      this.showAdd();
      let users = await this.$axios({
        url: "/friend/searchFriend",
        method: "POST",
        data: qs.stringify({ userName: this.searchUser }),
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
      });
      this.dataList[2] = users.data;
      this.dataList = JSON.parse(JSON.stringify(this.dataList));
    },
    // DH算法
    verifySecret(){
      let mySecret = (this.state.mySecret && Object.keys(this.state.mySecret).length) ? this.state.mySecret : localStorage.getItem("my-secret");
      
      return new Promise(async (reason, rej) => {
        // 获取密钥信息
        for(const friend of this.state.friendList){
          // 密钥丢失情况，重新协商
          if(!this.state.secret[friend.friendId] || !mySecret){
            let res = await this.$axios({
              url: `/secret/${friend.friendId}`,
              method: "GET"
            });
            if(res.data){
              let dh = new DH(res.data.p, res.data.g);
              if(mySecret){
                mySecret = JSON.parse(mySecret);
                dh.setPrivateKey(mySecret.privateKey);
                dh.setPublicKey(mySecret.publicKey);
              } else {
                localStorage.setItem("my-secret", JSON.stringify(dh));
              }
              let s = dh.generateSecret(res.data.publicKey);
              this.state.secret[res.data.userId] = s;
              // 更新密钥信息
              if(!mySecret){
                this.$axios({
                  url: "/secret",
                  method: "POST",
                  data: {
                    userId: this.state.user.id,
                    p: dh.p,
                    g: dh.g,
                    publicKey: dh.publicKey
                  }
                });
                this.$axios({
                  url: "/friendMessage",
                  method: "post",
                  data: {
                    fromId: this.state.user.id,
                    toId: friend.friendId,
                    type: 10, // 更新
                  }
                });
                mySecret = JSON.stringify(dh);
              }
              this.state.mySecret = JSON.parse(JSON.stringify(dh));
            }
          }
        }
        reason();
      });
      
    }
  },
  beforeDestroy() {
    window.removeEventListener("beforeunload", (e) => this.cache());
  },
};
</script>

<style scoped lang="scss">
.home {
  display: flex;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, .3);

  .search-box {
    user-select: none;
    display: flex;
    justify-content: space-around;
    box-sizing: border-box;
    width: 100%;
    height: 60px;
    padding-top: 20px;
    border-bottom: 1px solid #ccc;
    -webkit-app-region: no-drag;

    section {
      position: relative;
      height: 25px;

      label {
        position: absolute;
        top: 5px;
        right: 3px;
        cursor: pointer;
        &:active {
          color: green;
        }
      }
      #search {
        box-sizing: border-box;
        height: 100%;
        padding: 0px 20px 0px 5px;
        border: 1px solid #ececec;
        border-radius: 3px;
        background-color: rgba($color: #ececec, $alpha: .3);
        outline: none;
        color: rgb(255, 255, 255);
        &::placeholder{
          color: #fff;
        }
        &:focus {
          background-color: rgba($color: #ececec, $alpha: .3);
        }
      }
      .orther {
        cursor: pointer;
        display: block;
        font-size: 25px;
        color: white;
      }
    }
  }
}
</style>