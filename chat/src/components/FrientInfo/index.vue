<template>
  <div class="friend-info" @click="closeEdit">
    <div class="info-box">
      <div class="top">
        <div class="intro">
          <span class="nikeName">{{
            friend_detail.friendDetail.nikeName
          }}</span>
          <span
            class="gender el-icon-s-custom"
            :style="{
              color:
                friend_detail.friendDetail.sex == 1 ? '#46B6EF' : '#F37E7D',
            }"
          ></span>
        </div>
        <head-icon
          style="cursor: pointer"
          :size="{
            width: 60,
            height: 60,
            url: friend_detail.friendDetail.avatar,
          }"
        />
      </div>
      <div class="center">
        <table>
          <tr>
            <td>备注<i></i></td>
            <td style="cursor: pointer; width: 230px">
              <input
                class="nikeNameInput"
                type="text"
                v-model="newNikeName"
                :placeholder="friend_detail.friendNikeName"
                :readonly="!editNikeName"
                :style="{ outline: !editNikeName ? 'none' : '' }"
                v-on:dblclick="editFriendNikeName"
              />
            </td>
            <i
              title="确认修改"
              :style="{ visibility: !showSuccessIcon ? 'hidden' : 'visible' }"
              class="el-icon-success editIcon"
              @click="resetNikeName"
            ></i>
          </tr>
          <tr>
            <td>地区<i></i></td>
            <td>{{ address }}</td>
          </tr>
          <tr>
            <td>好友ID<i></i></td>
            <td>{{ friend_detail.friendDetail.userName }}</td>
          </tr>
        </table>
      </div>
      <div class="bottom">
        <el-button type="success" @click="sendMessage">发消息</el-button><br />
        <el-button type="warning" @click="delFriend">删除好友</el-button>
      </div>
    </div>
  </div>

</template>

<script>
import HeadIcon from "@/common/HeadIcon";
import qs from "qs";
import { CodeToText } from "element-china-area-data";
import { mapState } from "vuex";

export default {
  components: { HeadIcon },
  props: ["friend_detail"],
  data() {
    return {
      detail: null,
      editNikeName: false,
      newNikeName: "",
      showSuccessIcon: false,
    };
  },
  computed: {
    ...mapState({
      state: (state) => state.user,
    }),
    address(){
      try{
        this.friend_detail.friendDetail.address = eval(this.friend_detail.friendDetail.address);
      } catch{
        
      }
      if(this.friend_detail.friendDetail.address instanceof Array)
        return CodeToText[this.friend_detail.friendDetail.address[0]] + " " + CodeToText[this.friend_detail.friendDetail.address[1]] + " " + CodeToText[this.friend_detail.friendDetail.address[2]];
      else
        return this.friend_detail.friendDetail.address
    }
  },
  watch: {
    newNikeName: {
      handler(val) {
        if (val) {
          this.showSuccessIcon = true;
        } else {
          this.showSuccessIcon = false;
        }
      },
    },
  },
  methods: {
    sendMessage() {
      this.$emit("sendMessageFromUserInfo", this.friend_detail);
    },
    editFriendNikeName(e) {
      this.editNikeName = true;
    },
    closeEdit(e) {
      if (
        e.target.className === "nikeNameInput" ||
        e.target.className.indexOf("editIcon") != -1
      ) {
        return;
      } else {
        this.newNikeName = "";
        this.editNikeName = false;
        this.showSuccessIcon = false;
        this.editNikeName && (this.editNikeName = false);
      }
    },
    async resetNikeName() {
      if (this.friend_detail.friendNikeName != this.newNikeName) {
        this.friend_detail.friendNikeName = this.newNikeName;
      }
      this.newNikeName = "";
      this.editNikeName = false;
      this.showSuccessIcon = false;
      let result = await this.$axios({
        url: "/friend/setNikeName",
        method: "post",
        data: this.friend_detail,
      });
      this.$message.success("修改成功");
    },
    async delFriend() {
      this.$confirm("此操作将永久删除该好友及聊天信息, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(async () => {
          // 发起删除好友请求
          let res = await this.$axios({
            url: "/friend",
            method: "delete",
            data: this.friend_detail
          });
          this.$message.success(res.msg);
          // 删除聊天列表
          for (const index in this.state.chatList) {
            if (
              this.state.chatList[index].friendId ===
              this.friend_detail.friendId
            ) {
              await this.state.chatList.splice(index, 1);
              this.state.chatList = JSON.parse(
                JSON.stringify(this.state.chatList)
              );
              break;
            }
          }
          // 删除聊天信息
          if (this.state.messages[this.friend_detail.friendId]) {
            delete this.state.messages[this.friend_detail.friendId];
            this.state.messages = JSON.parse(
              JSON.stringify(this.state.messages)
            );
          }
          // 删除好友
          for (const i in this.state.friendList) {
            if (
              this.state.friendList[i].friendId === this.friend_detail.friendId
            ) {
              await this.state.friendList.splice(i, 1);
              this.state.friendList = JSON.parse(
                JSON.stringify(this.state.friendList)
              );
              break;
            }
          }
          this.$bus.$emit("closeMyRight");
        })
        .catch(() => {});
    },
  },
};
</script>

<style lang="scss" scoped>
.friend-info {
  user-select: none;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  -webkit-app-region: drag;

  .info-box {
    width: 350px;
    -webkit-app-region: no-drag;

    .top {
      display: flex;
      justify-content: space-between;
      height: 60px;
      line-height: 60px;
      padding: 30px 5px;
      border-bottom: 1px solid #ccc;

      .intro {
        display: flex;
        width: 280px;
        text-align: left;

        .nikeName {
          box-sizing: border-box;
          display: inline-block;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          max-width: 250px;
          font-size: 20px;
        }
        .gender {
          line-height: 60px;
          padding-left: 5px;
        }
      }
    }

    .center {
      table {
        box-sizing: border-box;
        width: 350px;
        padding: 30px 0;
        border-bottom: 1px solid #ccc;
        font-size: 14px;
        tr {
          box-sizing: border-box;
          width: 350px;
        }
        td {
          display: inline-block;
          overflow: hidden;
          height: 30px;
          line-height: 30px;
          text-align: left;
        }

        td:nth-child(1) {
          width: 45px;
          padding: 0 5px;
          text-align: justify;
          color: #c3c3c3;

          i {
            display: inline-block;
            width: 100%;
          }
        }

        td:nth-child(2) {
          position: relative;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          width: 250px;
          padding: 0 15px;

          // .editIcon{
          //   position: absolute;
          //   top: 50%;
          //   right: 0;
          //   transform: translateY(-50%);
          // }
          input {
            box-sizing: border-box;
            padding: 5px 15px;
            width: 245px;
            margin-left: -15px;
            font-size: 14px;
            border: none;
            &::-webkit-input-placeholder {
              color: black;
            }
          }
        }
        .editIcon {
          display: inline-block;
          width: 20px;
          cursor: pointer;
          color: #01ad19;
        }
      }
    }

    .bottom {
      padding: 30px;

      .el-button--success {
        width: 150px;
        height: 40px;
        line-height: 40px;
        padding: 0;
        margin-bottom: 5px;
        background-color: #01ad19;
        border-radius: 0;

        &:hover {
          background-color: rgba($color: #007e11, $alpha: 0.8);
        }
      }
      .el-button--warning {
        width: 150px;
      }
    }
  }
}
</style>