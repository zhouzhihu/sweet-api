<template>
  <magic-dialog v-show="true" :moveable="false" :shade="true" :showClose="false" title="系统设置">
    <template #content>
      <div class="login-form">
        <label>基础地址：</label>
        <sweet-input :value.sync="baseUrl"/>
      </div>
      <div class="login-form">
        <label>服务名或上下文：</label>
        <sweet-input :value.sync="esbServiceName"/>
      </div>
      <div class="login-form">
        <label>WebSocket地址：</label>
        <sweet-input :value.sync="websocketUrl"/>
      </div>
      <div class="login-form">
        <label>认证服务名：</label>
        <sweet-input :value.sync="authServiceName"/>
      </div>
      <div class="login-form">
        <label>租户：</label>
        <sweet-input :value.sync="egdTenant"/>
      </div>
      <div class="login-form">
        <label>运行版本(本地调试时使用)：</label>
        <sweet-input :value.sync="egdVersion"/>
      </div>
    </template>
    <template #buttons>
      <button class="ma-button" @click="doReset">恢复默认</button>
      <button class="ma-button active" @click="doSetting">确定</button>
      <button class="ma-button" @click="doCancel">取消</button>
    </template>
  </magic-dialog>
</template>

<script>
import SweetInput from '@/components/common/sweet-input'
import MagicDialog from '@/components/common/modal/magic-dialog'
import request from '@/api/request.js'
import contants from '@/scripts/contants.js'
import store from '@/scripts/store.js'
import bus from "@/scripts/bus.js";

export default {
  name: 'SweetSetting',
  props: {
    onSetting: Function,
  },
  components: {
    SweetInput,
    MagicDialog,
  },
  data() {
    return {
      baseUrl: '',
      esbServiceName: '',
      authServiceName: '',
      websocketUrl: '',
      egdVersion: '',
      egdTenant: ''
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
    doSetting() {
      contants.EGD_HEADER_TENANT_VALUE = this.egdTenant
      request.setBaseURL(this.baseUrl)
      contants.BASE_URL = this.baseUrl
      store.set(contants.BASE_URL_STORE, this.baseUrl)
      contants.ESB_SERVICE_NAME = this.esbServiceName
      store.set(contants.ESB_SERVICE_NAME_STORE, this.esbServiceName)
      contants.AUTH_SERVICE_NAME = this.authServiceName
      store.set(contants.AUTH_SERVICE_NAME_STORE, this.authServiceName)
      contants.WEBSOCKET_SERVER = this.websocketUrl
      store.set(contants.WEBSOCKET_SERVER_STORE, this.websocketUrl)
      contants.EGD_HEADER_TENANT_VALUE = this.egdTenant
      store.set(contants.EGD_HEADER_TENANT_NAME, this.egdTenant)
      contants.EGD_HEADER_VERSION_VALUE = this.egdVersion
      store.set(contants.EGD_HEADER_VERSION_NAME, this.egdVersion)
      this.onSetting()
    },
    doCancel() {
      bus.$emit('cancelSetting', '取消设置')
    },
    doReset() {
      store.remove(contants.HEADER_SWEET_TOKEN)
      store.remove(contants.BASE_URL_STORE)
      store.remove(contants.ESB_SERVICE_NAME_STORE)
      store.remove(contants.AUTH_SERVICE_NAME_STORE)
      store.remove(contants.WEBSOCKET_SERVER_STORE)
      store.remove(contants.EGD_HEADER_VERSION_NAME)
      store.remove(contants.EGD_HEADER_TENANT_NAME)
      window.onbeforeunload = null;
      window.location.reload()
    }
  }
}
</script>
<style scoped>
.login-form{
  margin-bottom: 5px;
  display: flex;
  flex-direction: column;
}
.login-form label{
  margin-right: 5px;
  display: inline-block;
  width: 100%;
  height: 22px;
  line-height: 22px;
}
.login-form > div{
  flex: 1;
}
.login-form label:nth-of-type(2){
  margin: 0 5px;
}
</style>