<template>
  <div class="message">
    <!-- 好友发送的信息 -->
    <div class="not-me" v-if="message.fromId !== $store.state.user.user.id && message.type == 0">
      <section class="head-icon" v-on:dblclick="showFriendDetail">
        <head-icon :size="{url: detail.friendDetail.avatar}" />
      </section>
      <section class="msg">
        <i></i>
        <span>{{message.message}}</span>
      </section>
    </div>
    <!-- 自己发送的信息 -->
    <div class="is-me" v-else-if="message.fromId === $store.state.user.user.id && message.type == 0">
      <section class="head-icon">
        <head-icon :size="{url: $store.state.user.user.avatar}" />
      </section>
      <section class="msg">
        <i></i>
        <span>{{message.message}}</span>
      </section>
    </div>
    <!-- 系统信息 -->
    <div class="system-info" v-else-if="message.type == 2">
      <section class="info">
        <a></a>
        <span>{{nowChatTime}}</span>
        <a></a>
        <span></span>
      </section>
    </div>
  </div>
</template>

<script>
import HeadIcon from '@/common/HeadIcon';
import dateFormat from "@/utils/dateFormat"

export default {
  name: 'message',
  components: { HeadIcon },
  props: ["message", "detail"],
  data(){
    return{
      
    }
  },
  computed: {
    // 此次聊天时间
    nowChatTime(){
      return dateFormat(this.message.message);
    }
  },
  methods: {
    showFriendDetail(){
      this.$bus.$emit('showFriendDetail', this.detail)
    }
  },
}
</script>

<style lang="scss" scoped>
$bd-color: #ececec;
$l-color: rgb(255, 255, 255);
$r-color: green;
.message{
  margin: 10px 0;

  .not-me, .is-me{
    display: flex;
    .msg{
      position: relative;
      display: flex;
      align-items: center;
      width: 70%;

      i{
        position: absolute;
        top: 15px;
        // left: 5px;
        display: block;
        width: 5px;
        height: 5px;
        // border-left: 1px solid $bd-color;
        // border-bottom: 1px solid $bd-color;
        // background-color: white;
        transform: rotate(45deg);
      }
      span{
        user-select: text;
        display: block;
        // min-height: 23px;
        line-height: 25px;
        max-width: 100%;
        padding: 2px 5px;
        border: 1px solid $bd-color;
        border-radius: 3px;
        overflow: hidden;
        flex-wrap: wrap;
        word-wrap: break-word;
      }
    }
  }

  .not-me{
    
    .msg{

      i{
        left: 5px;
        border-left: 1px solid $bd-color;
        border-bottom: 1px solid $bd-color;
        background-color: $l-color;
      }
      span{
        margin-left: 8px;
        background-color: $l-color;
      }
    }
  }

  .is-me{
    flex-direction: row-reverse;
    position: relative;
    .msg{
      display: flex;
      flex-direction: row-reverse;
      i{
        right: 5px;
        border-right: 1px solid $bd-color;
        border-top: 1px solid $bd-color;
        background-color: $r-color;
      }
      span{
        margin-right: 8px;
        border-color: $r-color;
        background-color: $r-color;
        color: white;
      }
    }
  }

  .system-info{
    display: flex;
    justify-content: center;
    width: 100%;

    .info{
      max-width: 80%;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
      color: #c3c3c3;

      a{
        cursor: pointer;
        color: rgb(94, 94, 216);
      }
    }
  }
}
</style>