<template>
  <div class="chat-list-box">
    <!-- 列表 -->
    <div
      class="list-box"
      :style="{ width: '250px', height: size.height - 60 + 'px' }"
    >
      <div v-if="chatListPage === 0">
        <!-- 聊天信息列表项 -->
        <chat-list-item
          v-for="(item, index) in chatListItems"
          :key="index"
          :chatListPage="chatListPage"
          :chatListItem="item"
          @contextmenu.native="showMenuList($event, item, index)"
          @click.native="showDetails(item)"
        />
      </div>
      <div v-else-if="chatListPage === 1">
        <chat-list-item
          v-for="(item, index) in chatListItems"
          :key="index"
          :chatListPage="chatListPage"
          :chatListItem="item"
          @click.native="showDetails(item)"
        />
      </div>
      <div v-else-if="chatListPage === 2">
        <!-- 聊天信息列表项 -->
        <span
          class="searchLabel"
          v-show="chatListItems.friends && chatListItems.friends.length > 0"
          >好友</span
        >
        <chat-list-item
          :chatListPage="chatListPage"
          v-for="item in chatListItems.friends"
          :key="item.id"
          :chatListItem="item"
          @click.native="showDetails(item)"
        />
        <span
          class="searchLabel"
          v-show="chatListItems.other && chatListItems.other.length > 0"
          >其他用户</span
        >
        <chat-list-item
          :chatListPage="chatListPage"
          v-for="item in chatListItems.other"
          :key="item.id"
          :chatListItem="item"
          @click.native="showDetails(item, true)"
        />
      </div>
      <!-- 通知 -->
      <div v-else-if="chatListPage === 3">
        <div class="notify" v-for="(item, index) in chatListItems" :key="index">
          <div class="avatar">
            <head-icon
              :size="{ height: 45, width: 45, url: item.fromUser.avatar }"
            />
          </div>
          <div class="context">
            <a class="nikeName">{{ item.fromUser.nikeName }}</a>
            <span class="add-text">请求添加您为好友</span>
          </div>
          <div class="btns">
            <div v-if="item.flag === 0">
              <section>
                <button @click="agreeAdd(index)">{{ option_value }}</button>
                <i class="selector el-icon-arrow-down" @click="showOption"></i>
              </section>
              <section class="option" v-show="show_option">
                <span
                  v-for="(item, index) in options"
                  :key="index"
                  @click="setOptionValue(index)"
                  >{{ item }}</span
                >
              </section>
            </div>
            <div class="argee" v-else>
              <span v-if="item.flag === 1">已同意</span>
              <span v-else>已拒绝</span>
            </div>
          </div>
        </div>
      </div>
      <!-- 设置 -->
      <div class="edit-div" v-else-if="chatListPage === 4">
        <section @click="showMeDetail">
          <span>个人信息</span>
        </section>

        <section @click="showResetPwd">
          <span>修改密码</span>
        </section>

        <section @click="logout">
          <span>退出登录</span>
        </section>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import ChatListItem from "../../common/ChatListItem";
import HeadIcon from "../../common/HeadIcon";
import dayjs from "dayjs";

export default {
  name: "ChatList",
  components: { ChatListItem, HeadIcon },
  props: {
    chatListPage: {
      type: Number,
      require: true,
    },
    chatListItems: {},
  },
  data() {
    return {
      show_option: false,
      option_value: "接受",
      options: ["拒绝"],
    };
  },
  computed: {
    ...mapState({
      chatList: (state) => state.user.chatList,
    }),
    size() {
      return this.$store.state.size;
    },
  },
  watch: {},
  methods: {
    showDetails(data, isFriend) {
      if (isFriend) {
        data.isFriend = true;
      }
      this.$emit("details-intro", data);
    },
    showOption() {
      this.show_option = !this.show_option;
    },
    setOptionValue(index) {
      this.option_value = this.options.splice(index, 1, this.option_value)[0];
      this.show_option = false;
    },
    async agreeAdd(index) {
      if (this.option_value === "接受") {
        let addFriendNotify = {
          fromUser: this.chatListItems[index].toUser,
          toUser: this.chatListItems[index].fromUser,
          flag: 1,
        };
        let result = await this.$axios({
          url: "/friend/addFriend",
          method: "post",
          data: addFriendNotify,
        });

        // 添加好友成功
        if (result.msg === "addFriendNotifySuccess") {
          // 得到好友信息
          result.data.lastChatTime = dayjs().format("YYYY-MM-DD HH:mm:ss");
          result.data.lastChatMsg = "我同意了你的好友请求";
          this.$store.state.user.chatList.unshift(result.data);
          this.$store.state.user.chatList = JSON.parse(
            JSON.stringify(this.$store.state.user.chatList)
          );
          // 发送添加成功信息
          let data = {
            fromId: this.$store.state.user.user.id,
            toId: result.data.friendId,
            message: "我同意了你的好友请求",
            type: 0,
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
        this.chatListItems[index].flag = 1;
        this.$store.state.user.addFriendNotify[index].flag = 1;
      } else {
        let addFriendNotify = {
          fromUser: this.chatListItems[index].toUser,
          toUser: this.chatListItems[index].fromUser,
          flag: 2,
        };
        let result = await this.$axios({
          url: "/friend/addFriend",
          method: "post",
          data: addFriendNotify,
        });
      }
    },
    // 显示自己信息
    showMeDetail() {
      this.$bus.$emit("showMeDetail", this.$store.state.user.user);
    },
    // 显示修改密码
    showResetPwd() {
      this.$bus.$emit("showResetPwd", this.$store.state.user.user, true);
    },
    showMenuList(e, item, index) {
      this.$contextmenu({
        items: [
          {
            label: "删除该聊天",
            onClick: () => {
              this.$bus.$emit("closeMyRightByFriend", { ...item });
              this.$store.state.user.chatList.splice(index, 1);
              this.$store.state.user.chatList = JSON.parse(
                JSON.stringify(this.$store.state.user.chatList)
              );
            },
          },
        ],
        e,
        x: e.clientX,
        y: e.clientY,
        customClass: "class-a",
        zIndex: 3,
        minWidth: 100,
      });
    },
    logout() {
      this.$confirm("确定退出？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        localStorage.removeItem("user_info");
        localStorage.removeItem("token");
        // 发送退出登录请求
        this.$axios({
          url: "/user/logout",
          method: "post",
        });
        this.$store.state.user.ws.close();
        this.$router.replace("/login");
        window.location.reload();
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.chat-list-box {
  -webkit-app-region: no-drag;
  user-select: none;
  display: flex;
  flex-direction: column;
  width: 250px;

  .list-box {
    overflow-y: auto;
    background-color: #ececec;

    .searchLabel {
      display: block;
      padding: 5px 0;
      background-color: rgb(31, 147, 65);
      color: white;
    }

    .notify {
      display: flex;
      align-items: center;
      padding: 10px;
      padding-right: 0;
      margin-bottom: 2px;
      background-color: #dadada;

      .avatar {
        margin-right: 5px;
      }

      .context {
        .nikeName {
          display: block;
          width: 130px;
          margin-bottom: 5px;
          text-align: left;
          cursor: pointer;

          &:hover {
            text-decoration: underline;
            color: blue;
          }
        }
        .add-text {
          display: block;
          width: 130px;
          text-align: left;
          font-size: 12px;
        }
      }

      .btns {
        box-sizing: border-box;
        position: relative;
        cursor: pointer;
        //
        section {
          background-color: #ccc;
          box-shadow: 0 0 2px #0398;

          button {
            box-sizing: border-box;
            padding: 5px;
            font-size: 12px;
            background-color: transparent;
            border: none;
            box-shadow: 0 0 1px #0398;

            &:active {
              background-color: #0398;
              color: white;
            }
          }

          .selector {
            position: relative;
            &:active {
              color: green;
            }
          }

          .option {
            box-sizing: border-box;
            position: absolute;
            left: 0;
            margin-top: 1px;
            width: 100%;
            background-color: #ccc;
            box-shadow: 0 0 2px #399567;

            span {
              box-sizing: border-box;
              display: block;
              width: inherit;
              padding: 5px 0px;
              padding-left: 7px;
              text-align: left;
              font-size: 12px;

              &:hover {
                background-color: #399567;
                color: #ededed;
              }
            }
          }
        }

        .argee {
          cursor: default;
          font-size: 14px;
          border: none;
        }
      }
    }

    // 设置
    .edit-div {
      cursor: pointer;
      color: white;

      section {
        padding: 10px;
        margin-bottom: 2px;
        background-color: #399567;

        &:hover {
          background-color: #ecabdd;
        }
        &:active {
          transform: scale(0.98);
        }
      }
    }
  }
}
</style>