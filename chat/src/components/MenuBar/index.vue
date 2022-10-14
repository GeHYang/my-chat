<template>
  <div class="menu-bar-box">
    <head-icon
      :size="{ url: user.avatar }"
      :show_detail="showDetail"
      @click.native="showDetail = !showDetail"
      :detail="user"
    />
    <div class="menu-list">
      <div>
        <div
          class="menu-item"
          v-for="(item, index) in menu_item_list"
          :key="index"
          :style="{ color: index === menu_item_index ? '#07c160' : '' }"
          @click="toPage(index)"
        >
          <i v-if="(index === 0 && chatMessageNotify) || (index === 3 && addFriendNotify)"></i>
          {{ item }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import HeadIcon from "../../common/HeadIcon";
export default {
  name: "MenuBar",
  components: { HeadIcon },
  props: ["toPage1"],
  data() {
    return {
      menu_item_list: ["消息", "通讯录", "搜索", "通知", "设置"],
      menu_item_index: 0,
      showDetail: false,
    };
  },
  mounted() {
    
  },
  computed: {
    ...mapState({
      user: (state) => state.user.user,
      notify: (state) => state.user.notify,
      chatList: (state) => state.user.chatList,
    }),
    chatMessageNotify(){
      for(const chat of this.chatList){
        if(chat ?. unreadCount && chat.unreadCount > 0){
          return true;
        }
      }
      return false;
    },
    addFriendNotify(){
      return this.notify.addFriendNotify;
    },
  },
  watch: {
    toPage1: {
      handler(val) {
        this.menu_item_index = val;
      },
    },
  },
  methods: {
    toPage(index) {
      this.menu_item_index = index;
      this.$emit("toPage", index);
    },
    showDetailFun() {
      console.log(123);
    },
  },
};
</script>

<style lang="scss" scoped>
.menu-bar-box {
  box-sizing: border-box;
  user-select: none;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 35px 0 0 0;
  width: 55px;
  height: 100%;
  background-color: #2e2e2e;
  -webkit-app-region: no-drag;

  .menu-list {
    width: 100%;
    .menu-item {
      position: relative;
      cursor: pointer;
      width: 100%;
      height: 30px;
      padding: 10px 0;
      line-height: 30px;
      font-size: 12px;
      color: white;
      &:hover {
        background-color: #ececec;
        color: green;
      }
      
      i{
        z-index: 0;
        position: absolute;
        top: 15px;
        right: 5px;
        width: 10px;
        height: 10px;
        background-color: red;
        border-radius: 50%;
      }
    }
  }
}
</style>