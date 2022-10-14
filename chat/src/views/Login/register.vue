<template>
  <!-- 注册界面 -->
  <div class="container iconfont">
    <form id="form">
      <h2>Registeraa</h2>
      <section>
        <input type="text" id="username" required autocomplete="off" />
        <span></span>
        <label for="username">用户名</label>
      </section>

      <section>
        <input type="password" id="password" required />
        <span></span>
        <label for="password">密码</label>
      </section>

      <section>
        <input type="password" id="checkPassword" required />
        <span></span>
        <label for="checkPassword">确认密码</label>
      </section>

      <section>
        <input type="text" id="nikeName" autocomplete="off" required />
        <span></span>
        <label for="nikeName">昵称</label>
      </section>

      <section>
        <input type="submit" value="注册" />
        <a class="to-login">Login</a>
      </section>
    </form>
    <div class="line-box">
      <span></span>
      <span></span>
      <span></span>
      <span></span>
    </div>
  </div>
</template>

<script>
export default {};
</script>

<style scoped>
* {
  padding: 0;
  margin: 0;
  box-sizing: border-box;
}

body {
  background-color: #000;
}

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

.container {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  overflow: hidden;
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
}

#form h2 {
  user-select: none;
  padding: 10px 0 30px 0;
  background: linear-gradient(-45deg, #0066ff, #f05bca);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

#form section {
  display: flex;
  justify-content: center;
  position: relative;
  width: 300px;
  height: 30px;
  line-height: 30px;
  margin: 15px 0;
}

section input {
  z-index: 10;
  padding: 0 5px;
  width: 200px;
  font-size: 20px;
  outline: none;
  color: #0066ff;
}

section > span {
  position: absolute;
  width: 0;
  height: 30px;
  border-bottom: 2px solid;
  -webkit-border-image: linear-gradient(45deg, #f05bca, #0066ff);
  transition: all 0.5s;
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
  bottom: -20px;
  color: #004aba;
  border-bottom: 1px solid;
}
.to-login:active {
  color: #0069fd;
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