<template>
  <magic-dialog v-show="true" :moveable="false" :shade="true" :showClose="false" title="登录">
    <template #content>
      <div class="login-form">
        <label>用户：</label>
        <sweet-input :onEnter="login" :value.sync="username"/>
      </div>
      <div class="login-form">
        <label>密码：</label>
        <sweet-input :onEnter="login" :value.sync="password" type="password"/>
      </div>
      <div class="login-form">
        <label>加密：</label>
        <div style="position: relative;display: inline-block;left: -3px;top: 10px;"><sweet-checkbox :value.sync="encrypt"/></div>
      </div>
    </template>
    <template #buttons>
      <button class="ma-button active" @click="login">登录</button>
    </template>
  </magic-dialog>
</template>

<script>
import SweetInput from '@/components/common/sweet-input'
import SweetCheckbox from '@/components/common/sweet-checkbox'
import MagicDialog from '@/components/common/modal/magic-dialog'
import request from '@/api/request.js'
import contants from '@/scripts/contants.js'
import store from '@/scripts/store.js'
import bus from "@/scripts/bus.js";
import { encryptStr } from '@/scripts/encrypt.js'

export default {
  name: 'SweetLogin',
  props: {
    onLogin: Function,
  },
  components: {
    SweetInput,
    SweetCheckbox,
    MagicDialog,
  },
  data() {
    return {
      username: '',
      password: '',
      baseUrl: '',
      esbServiceName: '',
      authServiceName: '',
      websocketUrl: '',
      egdVersion: '',
      egdTenant: '',
      encrypt: false,
      publicKey: ''
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    init() {  
      this.baseUrl = store.get(contants.BASE_URL_STORE) ?  store.get(contants.BASE_URL_STORE) : contants.BASE_URL
      this.esbServiceName = store.get(contants.ESB_SERVICE_NAME_STORE) ? store.get(contants.ESB_SERVICE_NAME_STORE) : contants.ESB_SERVICE_NAME
      this.authServiceName = store.get(contants.AUTH_SERVICE_NAME_STORE) ? store.get(contants.AUTH_SERVICE_NAME_STORE) : contants.AUTH_SERVICE_NAME
      this.websocketUrl = store.get(contants.WEBSOCKET_SERVER_STORE) ? store.get(contants.WEBSOCKET_SERVER_STORE) : contants.WEBSOCKET_SERVER
      this.egdVersion = store.get(contants.EGD_HEADER_VERSION_NAME) ? store.get(contants.EGD_HEADER_VERSION_NAME) : contants.EGD_HEADER_VERSION_VALUE
      this.egdTenant = store.get(contants.EGD_HEADER_TENANT_NAME) ? store.get(contants.EGD_HEADER_TENANT_NAME) : contants.EGD_HEADER_TENANT_VALUE
    },
    login() {
      if (this.encrypt) {
        request.send(`${this.baseUrl}/${this.authServiceName}/publicKey`, {}, {method: 'GET'}).success((res, response) => {
          if (res) {
            this.publicKey = res.publicKey
            this.doLogin()
          } else {
            bus.$emit('status', '登录失败')
            this.$magicAlert({
              title: '登录',
              content: '登录失败,公钥不正确'
            })
          }
        })
      } else {
        this.doLogin()
      }
    },
    doLogin() {
      contants.EGD_HEADER_TENANT_VALUE = this.egdTenant
      request.send(`${this.baseUrl}/${this.authServiceName}/login/token`, {
        username: this.username,
        password: this.encrypt ? encryptStr(this.publicKey, this.password) : this.password
      }).success((res, response) => {
        if (res) {
          bus.$emit('status', '登录成功')
          contants.HEADER_SWEET_TOKEN_VALUE = res.access_token;
          store.set(contants.HEADER_SWEET_TOKEN, contants.HEADER_SWEET_TOKEN_VALUE);
          this.onLogin();
        } else {
          bus.$emit('status', '登录失败')
          this.$magicAlert({
            title: '登录',
            content: '登录失败,用户名或密码不正确'
          })
        }
      })
    }
  }
}
</script>
<style scoped>
.login-form{
  margin-bottom: 5px;
  flex-direction: column;
}
.login-form label{
  margin-right: 5px;
  display: inline-block;
  width: 40px;
  height: 22px;
  line-height: 22px;
  text-align: right;
}
.login-form > div{
  flex: 1;
}
.login-form label:nth-of-type(2){
  margin: 0 5px;
}
</style>