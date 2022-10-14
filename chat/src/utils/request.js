import axios from "axios";
import $C from './config.js'
import router from "@/router/index.js";
import { Message, Loading } from 'element-ui'

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8';
// 创建axios实例
const service = axios.create({
  // 请求url公共部分
  baseURL: $C.baseURL,
  timeout: $C.timeout
});
let loading;
// 请求拦截
service.interceptors.request.use(config => {
  loading = Loading.service({text: "加载中"});
  // 添加token
  (config.url.indexOf("/user/login") == -1) && (config.headers.token = $C.token);
  return config;
})
// 响应拦截
service.interceptors.response.use(response => {
  loading.close();
  // 获取返回结果
  const result = response.data;
  // code为200，请求成功，返回
  if(result.code === 200){
    return Promise.resolve(result);
  } else if(result.code === 401){
    Message.error(result.msg);
    localStorage.removeItem("user_info", undefined);
    localStorage.removeItem("token", undefined);
    if(location.href.indexOf('/login') === -1) {
      router.replace("/login");
      window.location.reload();
    }
    return Promise.reject(result);
  } else{
    loading.close();
    Message.error(result.msg);
    return Promise.reject(result);
  }
}, () => {
  loading.close();
  Message.error('网络请求异常，请稍后再试');
  return Promise.reject("网络请求异常，请稍后再试")
})

export default service;