import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui';// 引入element-ui
import 'element-ui/lib/theme-chalk/index.css'; // 引入element-ui样式
import request from '@/utils/request'
import $C from '@/utils/config'
import Contextmenu from "vue-contextmenujs"

const electron =window.require('electron');

Vue.config.productionTip = false
Vue.prototype.$electron = electron;
Vue.prototype.$size = {width: 0, height: 0};// 页面大小
Vue.prototype.$axios = request;
Vue.prototype.$C = $C;

Vue.use(Contextmenu);
Vue.use(ElementUI); // 使用
new Vue({
  router,
  store,
  render: h => h(App),
  beforeCreate(){
    Vue.prototype.$bus = this;
  }
}).$mount('#app')
