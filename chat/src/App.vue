<template>
  <div id="app">
    <MyHeader class="he" />
    <div
      id="page"
      :style="{ width: size.width + 'px', height: size.height + 'px' }"
    >
      <router-view />
    </div>
  </div>
</template>

<script>
import MyHeader from "./components/MyHeader";

export default {
  data() {
    return {
      size: { width: 0, height: 0 },
    };
  },
  components: { MyHeader },
  watch: {
    size: {
      deep: true,
      immediate: true,
      handler(val) {
        this.$store.state.size = val;
      },
    },
  },
  beforeCreate() {
    
  },
  beforeDestroy() {
  },
  mounted() {
    let winHeight = window.innerHeight;
    let winWidth = window.innerWidth;
    this.size.width = winWidth;
    this.size.height = winHeight;
    window.onresize = () => {
      winHeight = window.innerHeight;
      winWidth = window.innerWidth;
      this.size.width = winWidth;
      this.size.height = winHeight;
    };
    let token = localStorage.getItem("token");
    if (!token) {
      if (location.href.indexOf("/callVideo") !== -1) {
      } else this.$router.replace("/login");
    }
  },
  methods: {
    cs() {},
  },
};
</script>
<style lang="scss">
/* ----------------------滚动条样式---------------------------------- */
::-webkit-scrollbar {
  width: 2px;
}
::-webkit-scrollbar-thumb {
  background-image: linear-gradient(
    to bottom,
    white,
    red,
    yellow,
    green,
    blue,
    yellow
  );
}

/* ----------------------滚动条样式---------------------------------- */
* {
  margin: 0;
  padding: 0;
}
#app {
  overflow: hidden;
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  -webkit-app-region: drag;
  background: url("./assets/bg.jpeg");

  #page {
    overflow: auto;
  }
}
</style>
