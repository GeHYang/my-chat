<template>
  <div
    class="my-right"
    v-if="details"
    :style="{
      width: size.width - 250 - 56 + 'px',
      height: size.height - 30 + 'px',
    }"
  >
    <chat-msg
      v-if="details.chatListPage === 0 && details.detail"
      :detail="details.detail"
      :page="details.chatListPage"
    />
    <friend-info
      @sendMessageFromUserInfo="sendMessageFromUserInfo"
      :page="details.chatListPage"
      v-else-if="details.chatListPage === 1 && details.detail"
      :friend_detail="details.detail"
    />
    <group-info
      v-else-if="details.chatListPage === 2 && details.detail"
      :friend_detail="details.detail"
      @sendMessageFromUserInfo="sendMessageFromUserInfo"
    />
    <me-detail v-else-if="details.chatListPage === 4 && details.detail" :flag="details.flag" />
  </div>
</template>

<script>
import ChatMsg from "../ChatMsg";
import FriendInfo from "../FrientInfo";
import GroupInfo from "../GroupInfo";
import MeDetail from "../MeDetail";
export default {
  components: { ChatMsg, FriendInfo, GroupInfo, MeDetail },
  props: ["details"],
  computed: {
    size() {
      return this.$store.state.size;
    },
  },
  created(){
    
  },
  methods: {
    sendMessageFromUserInfo(e) {
      this.$emit("sendMessageFromUserInfo", e);
    },
  },
};
</script>

<style>
.my-right {
  overflow: hidden;
  position: relative;
  z-index: 0;
  margin-top: 30px;
  -webkit-app-region: no-drag;
}
</style>