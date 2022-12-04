<template>
  <div class="friend-info">
    <div class="info-box">
      <div class="top">
        <div class="intro">
          <span class="nikeName">{{ friend_detail.nikeName }}</span>
          <span
            class="gender el-icon-s-custom"
            :style="{
              color: friend_detail.sex == 1 ? '#46B6EF' : '#F37E7D',
            }"
          ></span>
        </div>
        <head-icon
          style="cursor: pointer"
          :size="{
            width: 60,
            height: 60,
            url: friend_detail.avatar,
          }"
        />
      </div>
      <div class="center">
        <table>
          <tr>
            <td>备注<i></i></td>
            <td>
              <span class="nikeNameInput" readonly type="text">{{
                friend_detail.nikeName
              }}</span>
            </td>
          </tr>
          <tr>
            <td>地区<i></i></td>
            <td>{{ friend_detail.address }}</td>
          </tr>
          <tr>
            <td>好友ID<i></i></td>
            <td>{{ friend_detail.userName }}</td>
          </tr>
        </table>
      </div>
      <div class="bottom">
        <el-button v-if="friend_detail.isFriend" type="success" @click="sendAddFriendMsg"
          >添加好友</el-button
        >
      </div>
    </div>
  </div>
</template>

<script>
import HeadIcon from "@/common/HeadIcon";

export default {
  components: { HeadIcon },
  props: ["friend_detail"],
  data() {
    return {
      detail: null,
      showSuccessIcon: false,
    };
  },
  watch: {},
  methods: {
    sendMessage() {
      this.$emit("sendMessageFromUserInfo", this.friend_detail);
    },
    async sendAddFriendMsg() {
      let addFriendNotify = {
        fromUser: this.$store.state.user.user,
        toUser: this.friend_detail,
        flag: 0,
      };
      let result = await this.$axios({
        url: "/friend/addFriend",
        method: "post",
        data: addFriendNotify,
      });
      this.$message.success("发送好友申请成功")
      if (!this.$store.state.user.addFriendNotify)
        this.$store.state.user.addFriendNotify = new Array();
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
    padding: 0 20px;
    -webkit-app-region: no-drag;
    border-radius: 15px;
    background-color: rgba(77, 94, 111, 0.4);

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
          color: rgb(70, 70, 70);

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
        background-color: #01ad19;
        border-radius: 0;

        &:hover {
          background-color: rgba($color: #007e11, $alpha: 0.8);
        }
      }
    }
  }
}
</style>