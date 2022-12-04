<template>
  <div class="chat-list-item">
    <!-- 聊天列表项 -->
    <div class="chat-item-box" v-if="chatListPage === 0">
      <div class="left">
        <head-icon
          :size="{ width: 40, height: 40, url: chatListItem.friendDetail.avatar }"
        />
      </div>
      <div class="right">
        <div class="top">
          <!-- 昵称 -->
          <section class="nikename">{{ chatListItem.friendNikeName }}</section>
          <!-- 上次聊天时间 -->
          <section class="last-chat-time">
            {{ formatDate }}
          </section>
        </div>
        <div class="bottom">
          <!-- 上一次聊天信息 -->
          <section class="last-msg">{{ chatListItem.lastChatMsg }}</section>
          <!-- 未读数 -->
          <section
            class="unread-count"
            v-show="chatListItem.unreadCount > 0 ? true : false"
          >
            {{ chatListItem.unreadCount > 99 ? '99+' : chatListItem.unreadCount }}
          </section>
        </div>
      </div>
    </div>
    <!-- 好友列表项 -->
    <div class="friend-item-box" v-else-if="chatListPage === 1">
      <div class="left">
        <head-icon
          :size="{
            width: 35,
            height: 35,
            url: chatListItem.friendDetail.avatar,
          }"
        />
      </div>
      <div class="right">
        <span class="nikename">{{chatListItem.friendNikeName}}</span>
      </div>
    </div>
    <!-- 搜索添加列表项 -->
    <div class="group-item-box" v-else-if="chatListPage === 2">
      <div class="left">
        <head-icon :size="{ width: 40, height: 40, url: chatListItem.avatar }" />
      </div>
      <div class="right">
        <span class="nikename">{{chatListItem.nikeName}}</span>
      </div>
    </div>
  </div>
</template>

<script>
import HeadIcon from "../HeadIcon";
export default {
  props: {
    chatListPage: {
      type: Number,
      require: true,
    },
    chatListItem: undefined,
  },
  name: "ChatListItem",
  components: { HeadIcon },
  computed: {
    formatDate(){
      let date = this.chatListItem.lastChatTime;
      return this.formatLastDate(date);
    },
    
  },
  methods: {
    // 时间格式化
    formatLastDate(lastDate){
      if(!lastDate) return;
      // 当前时间
      let date = new Date();
      // 最后一条信息时间
      let date1 = new Date(lastDate);
      // 判断是否同年
      if(date.getFullYear() != date1.getFullYear()){
        return lastDate.split(" ");// 返回年月日
      }
      // 判断是否为四天以前
      if(date.getMonth() !== date1.getMonth() || 
        (date.getDate() - date1.getDate() > 3)
      ){
        lastDate = lastDate.split(" ")[0];
        lastDate = lastDate.substring(lastDate.indexOf("-") + 1);
        return lastDate;
      }
      // 判断是否为1-3天前
      if(date.getDate() - date1.getDate() > 1){
        return date.getDate() - date1.getDate() + "天前";
      }
      // 判断是否为昨天
      if(date.getDate() - date1.getDate() === 1) {
        return "昨天";
      }
      // 当天，显示信息
      lastDate = lastDate.split(" ")[1];
      lastDate = lastDate.substring(0, lastDate.lastIndexOf(":"));
      return lastDate;
    }
  },
};
</script>

<style lang="scss" scoped>
.chat-list-item {
  box-sizing: border-box;
  overflow: hidden;
  width: 100%;
  background-color: rgba(240,240,240, .3);

  .chat-item-box,
  .friend-item-box,
  .group-item-box {
    box-sizing: border-box;
    display: flex;
    align-items: center;
    padding: 10px;

    .left {
      padding-right: 10px;
    }

    .right {
      overflow: hidden;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      width: 250 - 70px;
      height: 40px;
      text-align: left;
    }
  }

  .chat-item-box {
    .right {
      .top {
        display: flex;
        justify-content: space-between;

        .nikename {
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
          font-size: 15px;
          color: black;
        }

        .last-chat-time {
          font-size: 12px;
          text-align: right;
          color: rgb(115, 108, 108);
        }
      }

      .bottom {
        display: flex;
        justify-content: space-between;
        height: 20px;
        font-size: 12px;

        .last-msg {
          overflow: hidden;
          line-height: 25px;
          white-space: nowrap;
          text-overflow: ellipsis;
          color: rgb(115, 108, 108);
        }

        .unread-count {
          flex-shrink: 0;
          min-width: 10px;
          max-width: 25px;
          padding: 0 5px;
          line-height: 20px;
          border-radius: 10px;
          background-color: red;
          text-align: center;
          color: white;
        }
      }
    }
  }

  .friend-item-box,
  .group-item-box {
    .nikename {
      display: block;
      line-height: 40px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  &:hover {
    background-color: #d3d3d3;
  }
}
</style>