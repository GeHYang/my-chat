<template>
  <div>
    <div class="my-header">
      <div style="width: 50px;"></div>
      <div class="my-title"></div>
      <div class="btns">
        <button class="my-minimize el-icon-minus" @click="minWin"></button>
        <button v-if="!isMaxWindow" class="my-maximize" @click="maxWin">▢</button>
        <button v-else class="my-maximize el-icon-copy-document" @click="unmaxWin"></button>
        <button class="my-close el-icon-close" @click="closeWindow"></button>
      </div>
    </div>
  </div>
</template>

<script>

export default {

  data(){
    return{
      isMaxWindow: false,
      isMinWindow: false,
    }
  },

  methods: {
    closeWindow(){
      this.$electron.ipcRenderer.send('close')
    },
    maxWin(){
      this.isMaxWindow = !this.isMaxWindow
      this.$electron.ipcRenderer.send('maxWin');
    },
    unmaxWin(){
      this.isMaxWindow = !this.isMaxWindow
      this.$electron.ipcRenderer.send('unmaxWin');
    },
    minWin(){
      !this.isMinWindow && this.$electron.ipcRenderer.send('minWin');
    },
  }
}
</script>

<style lang="scss" scoped>
.my-header{
  user-select: none;
  display: flex;
  justify-content: space-between;
  position: absolute;
  top: -2px;
  z-index: 10000;
  width: 100%;
  height: 30px;
  line-height: 30px;
  margin: 0;
  padding: 0;
  background-color: transparent;
  -webkit-app-region: drag;

  .btns{
    height: inherit;
    // background-color: #ececec;
    button{
      cursor: pointer;
      width: 40px;
      height: inherit;
      font-size: 10px;
      background-color: transparent;
      color: rgb(45, 45, 45);
      border: none;
      -webkit-app-region: no-drag !important;
      &.my-close:hover{
        background-color: red;
        color: white;
      }
      &.my-maximize:hover, &.my-minimize:hover{
        background-color: #dadada;
        color: black;
      }
      
    }
  }
  
}
</style>