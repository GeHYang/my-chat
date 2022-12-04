<template>
  <div class="detail" v-show="true">
    <div class="top">
      <div class="left">
        <div class="nikeName">{{ detail.nikeName }}</div>
        <div class="userID">
          <span>ID:</span>
          <span>{{ detail.userName }}</span>
        </div>
      </div>
      <div class="avatar">
        <img :src="detail.avatar" alt="" />
      </div>
    </div>
    <div class="bottom">
      <section>
        <span>地区<i></i></span>
        <span class="addr"> {{ address }}</span>
      </section>
    </div>
  </div>
</template>

<script>
import { CodeToText } from "element-china-area-data";
export default {
  props: ["detail"],
  data(){
    return{
      
    }
  },
  computed: {
    address(){
      try{
        this.detail.address = eval(this.detail.address);
      } catch{
        
      }
      if(this.detail.address instanceof Array)
        return CodeToText[this.detail.address[0]] + " " + CodeToText[this.detail.address[1]] + " " + CodeToText[this.detail.address[2]];
      else
        return this.detail.address
    }
  },
};
</script>

<style scoped lang="scss">
.detail {
  user-select: none;
  box-sizing: border-box;
  overflow: hidden;
  z-index: 100;
  width: 300px;
  height: 200px;
  padding: 30px;
  background-color: rgb(255, 255, 255);
  box-shadow: 0 0 5px #899;
  border-radius: 2px;

  .top {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    padding-bottom: 10px;
    border-bottom: 1px solid #ccc;

    .left {
      overflow: hidden;

      text-align: left;
      max-width: 300 - 60 - 70px;

      .nikeName {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        font-size: 20px;
      }

      .userID {
        margin-top: 5px;
        color: #899;

        span:nth-child(1) {
          display: block;
          float: left;
          width: 25px;
        }

        span:nth-child(2) {
          display: block;
          float: left;
          overflow: hidden;
          text-overflow: ellipsis;
          word-wrap: break-word;
          max-width: 300 - 60 - 70 - 30px;
        }
      }
    }

    .avatar {
      img {
        width: 60px;
        height: 60px;
      }
    }
  }

  .bottom {
    padding-top: 20px;
    span:nth-child(1) {
      display: block;
      float: left;
      width: 50px;
      padding-right: 20px;
      text-align: justify;
      color: #899;
      i {
        display: inline-block;
        width: 100%;
      }
    }
    span:nth-child(2) {
      display: block;
      float: left;
    }
  }
}
</style>