<template>
  <div class="me-detail">
    <el-form v-if="!flag" class="form" :rules="rules1" :model="userDetail">
      <table>
        <tr>
          <td colspan="2">
            <el-upload
              class="avatar-uploader"
              action="#"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="changePicture"
            >
              <head-icon
                :size="{ width: 90, height: 90, url: userDetail.avatar }"
                @click.native="resetIcon"
              />
            </el-upload>
          </td>
        </tr>

        <tr>
          <td>我的ID<i></i></td>
          <td>{{ userDetail.userName }}</td>
        </tr>
        <tr>
          <td>注册时间<i></i></td>
          <td>{{ userDetail.createTime }}</td>
        </tr>
        <tr>
          <td>性别<i></i></td>
          <td>
            <section>
              <el-radio v-model="radio" label="0">男</el-radio>
              <el-radio v-model="radio" label="1">女</el-radio>
            </section>
          </td>
        </tr>
        <tr>
          <td>昵称<i></i></td>
          <td>
            <el-input v-model="userDetail.nikeName" clearable> </el-input>
          </td>
        </tr>
        <tr>
          <td>生日<i></i></td>
          <td>
            <el-date-picker
              type="date"
              placeholder="选择日期"
              v-model="userDetail.birthday"
              value-format="yyyy-MM-dd"
              clearable=""
              class="date-picker"
            >
            </el-date-picker>
          </td>
        </tr>
        <tr>
          <td>地区<i></i></td>
          <td>
            <el-cascader
              size="large"
              :options="cityOptions"
              v-model="userDetail.address"
            >
            </el-cascader>
          </td>
        </tr>
        <tr>
          <td style="height: 60px; line-height: 60px">邮箱<i></i></td>
          <td style="height: 60px; line-height: 60px">
            <el-form-item prop="email">
              <el-input
                placeholder="请输入邮箱"
                v-model="userDetail.email"
                clearable
              >
              </el-input>
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td style="height: 60px; line-height: 60px">手机号<i></i></td>
          <td style="height: 60px; line-height: 60px">
            <el-form-item prop="phoneNumber">
              <el-input
                placeholder="请输入手机号"
                onkeyup="value=value.replace(/[^\d]/g,'')"
                v-model="userDetail.phoneNumber"
                clearable
              >
              </el-input>
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td colspan="2">
            <el-button type="primary" @click="submit">保存</el-button>
            <el-button @click="resetForm">取消</el-button>
          </td>
        </tr>
      </table>
    </el-form>
    <div v-else>
      <el-form
        label-width="100px"
        ref="pwd-form"
        class="pwd-form"
        :model="pwdObj"
        :rules="rules"
        label-position="right"
      >
        <el-form-item label="原密码" prop="oldPwd" required>
          <el-input
            placeholder="请输入原密码"
            v-model="pwdObj.oldPwd"
            show-password
          ></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPwd" required>
          <el-input
            placeholder="请输入新密码"
            v-model="pwdObj.newPwd"
            show-password
          ></el-input>
        </el-form-item>
        <el-form-item label="确认新密码" prop="newPwdSuc" required>
          <el-input
            placeholder="确认新密码"
            v-model="pwdObj.newPwdSuc"
            show-password
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="resetPassword">确定修改</el-button>
          <el-button @click="resetPwdForm">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import HeadIcon from "@/common/HeadIcon";
import qs from "qs";
import { regionData, CodeToText } from "element-china-area-data";

export default {
  components: { HeadIcon },
  props: ["flag"],
  data() {
    // 邮箱校验
    const checkEmail = (rule, value, callback) => {
      if (!value) return callback();
      let reg =
        /^[A-Za-z\d]+([-_\.][A-Za-z\d]+)*@([A-Za-z\d]+[-\.])+[A-Za-z\d]{2,4}(,[A-Za-z\d]+([-_\.][A-Za-z\d]+)*@([A-Za-z\d]+[-\.])+[A-Za-z\d]{2,4})*$/;
      if (reg.test(value)) {
        callback();
      } else {
        return callback(new Error("请输入正确的邮箱格式"));
      }
    };
    // 邮箱手机号
    const checkPhoneNumber = (rule, value, callback) => {
      if (!value) return callback();
      let reg =
        /^1((34[0-8])|(8\d{2})|(([35][0-35-9]|4[579]|66|7[35678]|9[1389])\d{1}))\d{7}$/;
      if (reg.test(value)) {
        callback();
      } else {
        return callback(new Error("请输入正确的手机号"));
      }
    };

    return {
      userDetail: {
        ...this.$store.state.user.user,
        address:
          this.$store.state.user.user.address === "暂未填写"
            ? ""
            : eval(this.$store.state.user.user.address),
      },
      radio: this.$store.state.user.user.sex + "",
      file: null,
      pwdObj: {
        oldPwd: "",
        newPwd: "",
        newPwdSuc: "",
      },
      rules: {
        oldPwd: [
          { required: true, message: "请输入原密码", trigger: "blur" },
          {
            min: 6,
            max: 15,
            message: "长度在 6 到 15 个字符",
            trigger: "blur",
          },
        ],
        newPwd: [
          { required: true, message: "请输入新密码", trigger: "blur" },
          {
            min: 6,
            max: 15,
            message: "长度在 6 到 15 个字符",
            trigger: "blur",
          },
        ],
        newPwdSuc: [
          { required: true, message: "请输入新密码", trigger: "blur" },
          {
            min: 6,
            max: 15,
            message: "长度在 6 到 15 个字符",
            trigger: "blur",
          },
        ],
      },
      rules1Form: {
        email: "",
        phone: "",
      },
      rules1: {
        email: [{ required: true, validator: checkEmail, trigger: "blur" }],
        phoneNumber: [
          { required: true, validator: checkPhoneNumber, trigger: "blur" },
        ],
      },
      cityOptions: regionData,
    };
  },
  methods: {
    resetForm() {
      this.userDetail = { ...this.$store.state.user.user };
      this.userDetail.address = eval(this.userDetail.address);
    },
    resetPwdForm() {
      this.$refs["pwd-form"].resetFields();
    },
    resetIcon() {},
    changePicture(file) {
      this.file = file;
      this.userDetail.avatar = URL.createObjectURL(file.raw);
    },
    async submit() {
      let data = new FormData();
      if (this.file) {
        data.append("file", this.file.raw);
        data.append(
          "user",
          JSON.stringify({
            ...this.userDetail,
            avatar: "",
            sex: this.radio,
            createTime: "",
            updateTime: "",
          })
        );
      } else {
        data.append(
          "user",
          JSON.stringify({
            ...this.userDetail,
            sex: this.radio,
            createTime: "",
            updateTime: "",
          })
        );
      }
      setTimeout(async () => {
        let res = await this.$axios({
          url: "/user/editInfo",
          method: "post",
          headers: { "Content-Type": "multipart/form-data" },
          data: data,
        });
        this.$message.success("修改成功");
        this.$store.state.user.user = res.data;
        this.$store.state.user.user = JSON.parse(
          JSON.stringify(this.$store.state.user.user)
        );
        // 重新保存数据
        localStorage.setItem("user_info", JSON.stringify(this.$store.state.user.user))
        this.resetForm();
      }, 500);
    },
    resetPassword() {
      if (this.pwdObj.oldPwd === this.pwdObj.newPwd) {
        this.$message.warning("原密码与新密码一致");
        return;
      }
      if (this.pwdObj.newPwd !== this.pwdObj.newPwdSuc) {
        this.$message.warning("新密码不一致");
        return;
      }
      this.$axios({
        url: "/user/resetPassword",
        method: "post",
        data: qs.stringify({
          ...this.pwdObj,
          userId: this.$store.state.user.user.id,
        }),
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
      })
        .then((res) => {
          this.$message.success(res.msg);
          this.resetPwdForm();
        })
        .catch(() => {
          this.resetPwdForm();
        });
    },
  },
};
</script>

<style scoped lang="scss">
.me-detail {
  .date-picker {
    -webkit-app-region: no-drag;
  }
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  -webkit-app-region: drag;

  .form {
    user-select: none;
    -webkit-app-region: no-drag;
    table {
      td {
        display: inline-block;
        line-height: 40px;
        height: 40px;

        &:nth-child(1) {
          display: inline-block;
          box-sizing: border-box;
          width: 100px;
          height: 40px;
          padding-right: 15px;
          text-align: justify;

          i {
            display: inline-block;
            width: 100%;
          }
        }
        &:nth-child(2) {
          width: 220px;
        }
      }
      tr:nth-child(1),
      tr:nth-last-child(1) {
        height: 100px;
        td {
          overflow: hidden;
          width: 100%;
          height: 100px;
          line-height: 100px;
          text-align: center;
        }
      }
    }
  }

  .pwd-form {
    -webkit-app-region: no-drag;
  }
}
</style>