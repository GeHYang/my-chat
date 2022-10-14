import ws from "@/utils/ws"
import $C from "@/utils/config"

export default {
  state: {
    user: undefined,
    ws: null,
    messages: {},
    friendList: [],
    chatList: [],
    rtc: undefined,
    callFriend: null,
    addFriendNotify: [],
    // 新消息列表
    notify: {
      chatMessageNotify: false,// 聊天信息通知
      addFriendNotify: false, // 添加好友信息通知
    },
  },
  mutations: {
    async addMessage1(state, data) {
      if (!state.messages[data.toId]) {
        state.messages[data.toId] = new Array();
      }
      await state.messages[data.toId].push(data);
      state.messages = JSON.parse(JSON.stringify(state.messages));
    },
  },
  actions: {
    async initWebSocket({ state, commit, dispatch }) {
      // 建立websocket连接
      state.ws = new ws("ws://" + $C.localName + ":" + $C.port + "/websocket/" + state.user.id);
      state.ws.wsCallback(async function (res) {
        if (res.msg === "oneMessageSuccess") {
          // 生成聊天信息列表
          if (!state.messages[res.data.fromId]) {
            state.messages[res.data.fromId] = new Array();
          }
          await state.messages[res.data.fromId].push(res.data);
          state.messages = JSON.parse(JSON.stringify(state.messages));
          dispatch("chatMessageList", { friendId: res.data.fromId + "", isFriend: true });
          state.notify.chatMessageNotify = true;
        } else if(res.msg === "callFriend"){
          state.callFriend = await {...res.data};
        } else if(res.msg === "addFriendNotify"){
          dispatch("addFriendNotifyFun", res.data);
          state.notify.addFriendNotify = true;
        } else if(res.msg === "deleteFriend"){
          for(let i in state.friendList){
            if(state.friendList[i].friendId === res.data.userId){
              state.friendList.splice(i, 1);
              state.friendList = JSON.parse(JSON.stringify(state.friendList));
              break;
            }
          }
        }
      })
    },
    // 生成信息列表
    async chatMessageList({state, dispatch}, { friendId, isFriend }) {
      if (!friendId) return;
      for (let friend of state.friendList) {
        if (friend.friendId === friendId) {
          if (!(state.chatList instanceof Array)) {
            state.chatList = new Array();
          }
          if (state.messages[friendId]) {
            friend.lastChatMsg = state.messages[friendId][state.messages[friendId].length - 1].message;
            let lastChatTime = state.messages[friendId][state.messages[friendId].length - 1].createTime;
            friend.lastChatTime = lastChatTime;
          }
          state.chatList.unshift({...friend});
          break;
        }
      }
      // 去重
      for (let f in state.chatList) {
        if (f == 0) continue;
        if (friendId === state.chatList[f].friendId) {
          if(state.chatList[f] ?. unreadCount){
            state.chatList[0].unreadCount = state.chatList[f].unreadCount
          }
          state.chatList.splice(f, 1);
          break;
        }
      }
      
      if(state.chatList[0] ?. unreadCount){
        state.chatList[0].unreadCount++
      } else{
        state.chatList[0].unreadCount = 1
      };
      state.chatList = JSON.parse(JSON.stringify(state.chatList));
      if (!isFriend && friendId) {
        dispatch("resetUnreadCount", friendId);
      }
    },
    // 重置未读数
    resetUnreadCount({state}, friendId) {
      if (!friendId || state.chatList.length <= 0 || state.chatList[0] == null) return;
      for (let i in state.chatList) {
        if (state.chatList[i].friendId === friendId) {
          state.chatList[i].unreadCount = 0;
          state.chatList = JSON.parse(JSON.stringify(state.chatList));
          break;
        }
      }
    },
    // 添加好友通知
    async addFriendNotifyFun({state}, friendNotify){
      // 去重
      for(const notify of state.addFriendNotify){
        if(notify.fromUser.id === friendNotify.fromUser.id && notify.toUser.id === friendNotify.toUser.id){
          notify.flag = friendNotify.flag;
          return;
        }
      }
      state.addFriendNotify.unshift(friendNotify);
      
    },

  },
}