<template>
  <div class="head-icon">
    <img
      :src="size.url ? size.url : require('../../assets/headIcon.jpeg')"
      :style="{
        width: (size.width ? size.width : 35) + 'px',
        height: (size.height ? size.height : 35) + 'px',
      }"
      alt=""
    />
    <div class="detail1" v-show="show_detail">
      <detail :detail="detail"></detail>
    </div>
  </div>
</template>

<script>
import Detail from "@/common/Detail";
export default {
  name: "HeadIcon",
  components: { Detail },
  props: {
    size: {
      type: Object,
      default() {
        return {
          width: 35,
          height: 35,
          url: "",
        };
      },
    },
    show_detail: {
      type: Boolean,
      default() {
        return false;
      },
    },
    detail: {
      type: Object,
      default() {
        return this.$store.state.user.user;
      },
    },
  },
  watch: {},
  methods: {},
};
</script>

<style lang="scss" scoped>
.head-icon {
  position: relative;
}
.head-icon img {
  cursor: pointer;
  background-color: white;
  border-radius: 3px;
}
.detail1 {
  z-index: 100;
  position: absolute;
  left: 15px;
  top: 15px;
}
</style>