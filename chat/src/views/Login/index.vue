<template>
  <div class="container">
    <div class="box" ref="login-box">
      <form id="form">
        <h2>Login</h2>
        <section>
          <input
            type="text"
            id="uname"
            v-model="info.username"
            autocomplete="off"
            required
          />
          <label for="uname">用户名</label>
        </section>
        <section>
          <input type="password" id="upwd" v-model="info.password" required />
          <label for="upwd">密码</label>
        </section>
        <section>
          <button class="btn" type="button" @click="login">登录</button>
          <a class="register" @click="toRegister">register</a>
        </section>
      </form>
      <div class="line-box">
        <span></span>
        <span></span>
        <span></span>
        <span></span>
      </div>
    </div>
    <div class="register-box" ref="register-box">
      <form id="form" ref="register-form" onsubmit="return false;">
        <h2>Register</h2>
        <section>
          <input type="text" name="userName" required autocomplete="off" />
          <span></span>
          <label for="username">用户名</label>
        </section>

        <section>
          <input type="password" name="password" required />
          <span></span>
          <label for="password">密码</label>
        </section>

        <section>
          <input type="password" name="checkPassword" required />
          <span></span>
          <label for="checkPassword">确认密码</label>
        </section>

        <section>
          <input type="text" name="nikeName" autocomplete="off" required />
          <span></span>
          <label for="nikeName">昵称</label>
        </section>

        <section>
          <input type="submit" @click="register" value="注册" />
          <a class="to-login" @click="toLogin">Login</a>
        </section>
      </form>
      <div class="line-box">
        <span></span>
        <span></span>
        <span></span>
        <span></span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      info: {
        username: "",
        password: "",
      },
      ws: {},
    };
  },
  mounted() {
    window.addEventListener("keydown", this.listenerEnter);
  },
  methods: {
    login() {
      // 登录
      let data = {
        userName: this.info.username,
        password: this.info.password,
      };
      this.$axios({
        url: "/user/login",
        data: data,
        method: "post",
        headers: {
          token: "",
        },
      }).then((res) => {
        this.$message.success(res.msg);
        localStorage.setItem("token", res.data.token);
        localStorage.setItem(
          "user_info",
          JSON.stringify(res.data.user_info.user)
        );
        this.$C.token = res.data.token;
        this.$store.state.user.user = res.data.user_info.user;
        window.removeEventListener("keydown", this.listenerEnter);
        this.$router.replace("/"); // 跳转首页
      });
    },
    async register(){
      let formData = new FormData(this.$refs['register-form'])
      let data = {};
      
      // 校验规则
      let regs = {
        userName: {
          reg: /^[A-Z0-9a-z]{6,12}$/,
          name: '用户名',
          message: "请输入6-12位大小写字母或数字"
        },
        password: {
          reg: /^[A-Z0-9a-z]{6,15}$/,
          name: '密码',
          message: "请输入6-15位大小写字母或数字"
        },
        checkPassword: {
          reg: /^[A-Z0-9a-z]{6,15}$/,
          name: '确认密码',
          message: "请输入6-15位大小写字母或数字"
        },
        nikeName: {
          reg: /^[^\s]{2,13}$/,
          name: '昵称',
          message: "请输入5-13位非空字符"
        },
      }
      for(const key of formData.keys()){
        if(!formData.get(key)) return;
      }
      // 校验内容是否正确
      for(const key of formData.keys()){
        if(!regs[key].reg.test(formData.get(key))){
          this.$message.error(regs[key].name + "格式错误： " + regs[key].message);
          return;
        }
      }
      // 表单内容转json对象
      for(const key of formData.keys()){
        data[key] = formData.get(key);
      }
      if(data.password !== data.checkPassword){
        this.$message.error("两次密码不一致");
        return;
      }

      // 发送请求
      let res = await this.$axios({
        url: "/user/register",
        method: "post",
        data: data,
        headers: {
          token: ""
        }
      });
      this.$message.success(res.msg);
      this.toLogin();
    },
    listenerEnter(e) {
      if (e.keyCode === 13) {
        // 回车键
        this.login();
      }
    },
    // 判断是否存在本人密钥对
    checkMeSecretKey() {},
    toRegister() {
      this.$refs["register-box"].style.opacity = 1;
      this.$refs["register-box"].style.left = "50%";
      this.$refs["register-box"].style.maxHeight = "500px";
      this.$refs["login-box"].style.opacity = 0;
      this.$refs["login-box"].style.left = "0%";
      this.$refs["login-box"].style.maxHeight = "0px";
    },
    toLogin() {
      this.$refs["register-box"].style.opacity = 0;
      this.$refs["register-box"].style.left = "0%";
      this.$refs["register-box"].style.maxHeight = "0px";
      this.$refs["login-box"].style.opacity = 1;
      this.$refs["login-box"].style.left = "50%";
      this.$refs["login-box"].style.maxHeight = "500px";
    },
  },
};
</script>


<style lang="scss" scoped>
.container {
  padding: 0;
  margin: 0;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background-color: #030600;
  user-select: none;
}
.box {
  opacity: 1;
  overflow: hidden;
  position: absolute;
  top: 50%;
  left: 50%;
  max-height: 500px;
  transform: translate(-50%, -50%);
  background-color: rgba(255, 255, 255, 0.1);
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
  -webkit-app-region: no-drag;
  transition: all 1s ease-in-out;

  #form {
    padding: 25px;
  }

  #form h2 {
    background: linear-gradient(45deg, #f05bca, #0066ff);
    text-align: center;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  #form section {
    position: relative;
  }

  #form input {
    padding: 5px;
    margin: 15px 0;
    font-size: 18px;
    border: none;
    border-bottom: 1px solid #ccc;
    outline: none;
    color: white;
    background-color: transparent;
    color: #f05bca;
    transition: all 0.5s;
  }

  #form label {
    position: absolute;
    top: 18px;
    left: 5px;
    font-size: 18px;
    color: #0066ff;
    transition: all 0.5s;
  }

  #form input:valid {
    -webkit-border-image: linear-gradient(45deg, #f05bca, #0066ff);
    border-image: linear-gradient(45deg, #f05bca, #0066ff) 1;
  }

  #form input:focus {
    -webkit-border-image: linear-gradient(45deg, #f05bca, #0066ff);
    border-image: linear-gradient(45deg, #f05bca, #0066ff) 1;
  }

  #form input:valid + label {
    top: 0;
    left: 0;
    font-size: 14px;
    color: #f05bca;
  }

  #form input:focus + label {
    top: 0;
    left: 0;
    font-size: 14px;
    color: #f05bca;
  }

  .btn {
    box-sizing: border-box;
    display: block;
    padding: 5px 20px;
    margin: 0 auto;
    background-color: transparent;
    color: #0066ff;
    font-size: 20px;
    border: 1px solid #ececec;
  }

  .btn:hover {
    cursor: pointer;
    transform: scale(1.1);
    font-size: 20px;
    box-shadow: 0 0 15px #f05bca, 0 0 30px #0066ff, 0 0 15px inset #f05bca,
      0 0 30px inset #0066ff;
  }

  .btn:active {
    transform: scale(0.9);
  }

  .register {
    cursor: pointer;
    position: absolute;
    right: -15px;
    bottom: -20px;
    padding: 1px 2px;
    text-align: center;
    color: rgba(255, 255, 255, 0.367);
    border-bottom: 1px solid rgb(0, 174, 255);

    &:hover {
      color: #0066ff;
    }
  }
}

.register-box {
  opacity: 0;
  position: absolute;
  top: 50%;
  left: 0%;
  max-height: 0px;
  transform: translate(-50%, -50%);
  overflow: hidden;
  -webkit-app-region: no-drag;
  transition: all 1s ease-in-out;

  input {
    cursor: pointer;
    border: none;
    background-color: transparent;
  }

  label {
    user-select: none;
    cursor: pointer;
    position: absolute;
    top: -2px;
    left: 60px;
    color: #0049b8;
    transition: all 1s;
  }

  #form {
    box-sizing: content-box;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    font-size: 18px;
    padding: 20px;
    background-color: rgba(192, 192, 192, 0.1);
    box-shadow: 0 0 15px rgba(192, 192, 192, 0.1) inset,
      0 0 45px rgba(192, 192, 192, 0.1) inset;

    h2 {
      user-select: none;
      padding: 10px 0 30px 0;
      background: linear-gradient(-45deg, #0066ff, #f05bca);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }

    section {
      display: flex;
      justify-content: center;
      position: relative;
      width: 300px;
      height: 30px;
      line-height: 30px;
      margin: 15px 0;

      input {
        z-index: 10;
        padding: 0 5px;
        width: 200px;
        font-size: 20px;
        outline: none;
        color: #0066ff;
      }
      & > span {
        position: absolute;
        width: 0;
        height: 30px;
        border-bottom: 2px solid;
        -webkit-border-image: linear-gradient(45deg, #f05bca, #0066ff);
        transition: width 0.5s;
      }

      input:focus + span {
        width: 200px;
        border-image: linear-gradient(45deg, #0066ff, #f05bca) 1;
      }

      input:valid + span {
        width: 200px;
        border-image: linear-gradient(45deg, #05ffd6, #f05bca) 1;
      }

      input:focus ~ label,
      input:valid ~ label {
        top: -25px;
        left: 50%;
        transform: translateX(-50%);
        background: linear-gradient(45deg, #0066ff, #f05bca);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        font-size: 16px;
      }
      input[type="submit"] {
        height: 50px;
        line-height: 50px;
        width: 80px;
        color: #004fbe;
        border: 1px solid transparent;
        border-left-color: #0066ff;
        border-top-color: #0066ff;
        border-right-color: #001c46;
        border-bottom-color: #001c46;
        transition: all 0.5s;
      }
      input[type="submit"]:hover {
        color: rgba(255, 255, 255, 0.6);
        border: 1px solid transparent;
        box-shadow: 0 0 5px #003d98 inset, 0 0 15px #003d98 inset,
          0 0 35px #003d98 inset, 0 0 50px #003d98 inset;
        transform: scale(1.1);
        border-radius: 5px;
      }
      input[type="submit"]:active {
        transform: scale(0.9);
      }
      .to-login {
        user-select: none;
        cursor: pointer;
        position: absolute;
        right: 0;
        line-height: 25px;
        bottom: -20px;
        color: #004aba;
        border-bottom: 1px solid;

        &:hover {
          color: #0069fd;
        }
      }
    }
  }
}

.line-box span {
  position: absolute;
  display: block;
}

.line-box span:nth-child(1) {
  top: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(45deg, #000, #f05bca, #0066ff);
  transform: translateX(-100%);
  animation: animate1 1s infinite linear;
}

@keyframes animate1 {
  0% {
    transform: translateX(-100%);
  }

  100% {
    transform: translateX(100%);
  }
}

.line-box span:nth-child(2) {
  top: 0;
  right: 0;
  width: 2px;
  height: 100%;
  background: linear-gradient(45deg, #0066ff, #f05bca, #000);
  transform: translateY(-100%);
  animation: animate2 1s infinite linear;
  animation-delay: 0.5s;
}

@keyframes animate2 {
  0% {
    transform: translateY(-100%);
  }

  100% {
    transform: translateY(100%);
  }
}

.line-box span:nth-child(3) {
  bottom: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(45deg, #0066ff, #f05bca, #000);
  transform: translateX(100%);
  animation: animate3 1s infinite linear;
  animation-delay: 1s;
}

@keyframes animate3 {
  0% {
    transform: translateX(100%);
  }

  100% {
    transform: translateX(-100%);
  }
}

.line-box span:nth-child(4) {
  top: 0;
  left: 0;
  width: 2px;
  height: 100%;
  background: linear-gradient(45deg, #000, #f05bca, #0066ff);
  transform: translateY(100%);
  animation: animate4 1s infinite linear;
  animation-delay: 1.5s;
}

@keyframes animate4 {
  0% {
    transform: translateY(100%);
  }

  100% {
    transform: translateY(-100%);
  }
}
</style>
	