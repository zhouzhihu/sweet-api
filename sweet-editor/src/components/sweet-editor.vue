<template>
  <div ref="container" :style="themeStyle" class="ma-container" tabindex="0">
    <sweet-loading v-if="loading" :title.sync="config.title" :version.sync="config.version" />
    <sweet-login v-if="showLogin" :onLogin="onLogin" />
    <sweet-setting v-if="showSetting" :onSetting="onSetting" />
    <!-- 顶部Header -->
    <sweet-header :config="config" :themeStyle.sync="themeStyle" class="not-select" />
    <ul class="ma-toolbar-container not-select">
      <li v-for="(item, index) in toolbars" :key="'toolbar_' + index" :class="{ selected: toolbarIndex === index }" @click="toolbarIndex = toolbarIndex === index ? -1 : index">{{ item.text }}<i :class="'ma-icon ' + item.icon"></i></li>
    </ul>
    <!-- 中间主要内容 -->
    <div class="ma-main-container">
      <div class="ma-middle-container">
        <sweet-api-list v-show="toolbarIndex === 0" ref="apiList" :style="{ width: toolboxWidth }" class="not-select" />
        <sweet-plugin-list v-show="toolbarIndex === 1" ref="pluginList" :style="{ width: toolboxWidth }" class="not-select" />
        <sweet-datasource-list v-show="toolbarIndex === 2" ref="datasourceList" :style="{ width: toolboxWidth }" class="not-select" />
        <sweet-file-list v-show="toolbarIndex === 3" ref="fileList" :style="{ width: toolboxWidth }" class="not-select" />
        <div ref="resizer" class="ma-resizer-x" @mousedown="doResizeX"></div>
        <sweet-script-editor class="ma-editor-container" />
      </div>
      <!-- 底部区域 -->
      <sweet-options />
    </div>
    <!-- 最近打开 -->
    <sweet-recent-opened />
    <!-- 状态条 -->
    <sweet-status-bar :config="config" />
  </div>
</template>

<script>
import '@/assets/index.css'
import '@/assets/iconfont/iconfont.css'
import bus from '@/scripts/bus.js'
import SweetLoading from './common/sweet-loading.vue'
import SweetHeader from './layout/sweet-header.vue'
import SweetLogin from './layout/sweet-login.vue'
import SweetSetting from './layout/sweet-setting.vue'
import SweetStatusBar from './layout/sweet-status-bar.vue'
import SweetOptions from './layout/sweet-options.vue'
import SweetApiList from './resources/sweet-api-list.vue'
import SweetFileList from './resources/sweet-file-list.vue'
import SweetPluginList from './resources/sweet-plugin-list.vue'
import SweetDatasourceList from './resources/sweet-datasource-list.vue'
import SweetScriptEditor from './editor/sweet-script-editor.vue'
import SweetRecentOpened from './resources/sweet-recent-opened.vue'
import request from '@/api/request.js'
import contants from '@/scripts/contants.js'
import SweetWebSocket from '@/scripts/websocket.js'
import store from '@/scripts/store.js'
import Key from '@/scripts/hotkey.js'
import {getQueryVariable, replaceURL} from '@/scripts/utils.js'
import {defineTheme} from '@/scripts/editor/theme.js'
import defaultTheme from '@/scripts/editor/default-theme.js'
import darkTheme from '@/scripts/editor/dark-theme.js'

export default {
  name: 'SweetEditor',
  props: {
    config: {
      type: Object,
      required: true
    }
  },
  components: {
    SweetHeader,
    SweetStatusBar,
    SweetApiList,
    SweetFileList,
    SweetPluginList,
    SweetScriptEditor,
    SweetOptions,
    SweetLoading,
    SweetLogin,
    SweetSetting,
    SweetDatasourceList,
    SweetRecentOpened
  },
  data() {
    return {
      loading: true,
      toolbars: [{text: '接口', icon: 'ma-icon-api'}, {text: '插件', icon: 'ma-icon-plugin'}, {text: '数据源', icon: 'ma-icon-datasource'}, {text: '文件管理', icon: 'ma-icon-list'}],
      toolbarIndex: 0,
      toolboxWidth: 'auto', //工具条宽度
      themeStyle: {},
      showLogin: false,
      showSetting: false,
      websocket: null,
      onLogin: () => {
        this.showLogin = false
        Promise.all([
          this.$refs.apiList.initData(),
          this.$refs.datasourceList.initData(),
          this.$refs.pluginList.initData(),
          this.$refs.fileList.initData(),
        ]).then(()=>{
          bus.$emit('login')
        })
      },
      onSetting: () => {
        this.showSetting = false
        this.onLogin()
        this.createWebSocket();
      }
    }
  },
  beforeMount() {
    // 设置变量到store
    null !== store.get(contants.BASE_URL_STORE) ? contants.BASE_URL = store.get(contants.BASE_URL_STORE) : ""
    null !== store.get(contants.ESB_SERVICE_NAME_STORE) ? contants.ESB_SERVICE_NAME = store.get(contants.ESB_SERVICE_NAME_STORE) : ""
    null !== store.get(contants.AUTH_SERVICE_NAME_STORE) ? contants.AUTH_SERVICE_NAME = store.get(contants.AUTH_SERVICE_NAME_STORE) : ""
    null !== store.get(contants.WEBSOCKET_SERVER_STORE) ? contants.WEBSOCKET_SERVER = store.get(contants.WEBSOCKET_SERVER_STORE) : ""
    null !== store.get(contants.EGD_HEADER_VERSION_NAME) ? contants.EGD_HEADER_VERSION_VALUE = store.get(contants.EGD_HEADER_VERSION_NAME) : ""
    null !== store.get(contants.EGD_HEADER_TENANT_NAME) ? contants.EGD_HEADER_TENANT_VALUE = store.get(contants.EGD_HEADER_TENANT_NAME) : ""

    let link = `${location.protocol}//${location.host}${location.pathname}`.replace('/index.html', '');
    if (contants.BASE_URL.startsWith('http')) { // http开头
      link = contants.BASE_URL
    } else if (contants.BASE_URL.startsWith('/')) { // / 开头的
      link = `${location.protocol}/${location.host}${contants.BASE_URL}`
    } else {
      link = link + '/' + contants.BASE_URL
    }
    bus.$on('login', () => {
      this.createWebSocket()
    })
    contants.DEFAULT_EXPAND = this.config.defaultExpand !== false
    contants.JDBC_DRIVERS = this.config.jdbcDrivers || []
    contants.DATASOURCE_TYPES = this.config.datasourceTypes || []
    contants.OPTIONS = this.config.options || []
    if(this.config.editorFontFamily !== undefined){
      contants.EDITOR_FONT_FAMILY = this.config.editorFontFamily
    }
    if(this.config.editorFontSize !== undefined){
      contants.EDITOR_FONT_SIZE = this.config.editorFontSize
    }
    if(this.config.logMaxRows !== undefined){
      contants.LOG_MAX_ROWS = Math.max(this.config.logMaxRows, 10)
    }
    this.config.version = contants.MAGIC_API_VERSION_TEXT
    this.config.title = this.config.title || 'sweet-api'
    this.config.themes = this.config.themes || {}
    this.config.defaultTheme = this.config.defaultTheme || 'dark'
    this.config.header = this.config.header || {
      skin: true
    }
    contants.AUTO_SAVE = this.config.autoSave !== false
    if (this.config.decorationTimeout !== undefined) {
      contants.DECORATION_TIMEOUT = this.config.decorationTimeout
    }
    this.config.request = this.config.request || {
      beforeSend: config => config,
      onError: err => Promise.reject(err)
    }
    this.config.response = this.config.response || {
      onSuccess: resp => resp,
      onError: err => Promise.reject(err)
    }
    request.setBaseURL(contants.BASE_URL)
    request.getAxios().interceptors.request.use(
      config => {
        if (this.config.request.beforeSend) {
          return this.config.request.beforeSend(config)
        }
        return config
      },
      err => {
        if (this.config.request.onError) {
          return this.config.request.onError(err)
        }
        return Promise.reject(err)
      }
    )
    request.getAxios().interceptors.response.use(
      resp => {
        if (this.config.response.onSuccess) {
          return this.config.response.onSuccess(resp)
        }
        return resp
      },
      err => {
        if (this.config.response.onError) {
          return this.config.response.onError(err)
        }
        return Promise.reject(err)
      }
    )
    defineTheme('default', defaultTheme)
    defineTheme('dark', darkTheme)
    Object.keys(this.config.themes || {}).forEach(themeKey => {
      defineTheme(themeKey, this.config.themes[themeKey])
    })
  },
  mounted() {
    if(this.config.blockClose !== false){
      window.onbeforeunload = () => '系统可能不会保存您所做的更改。'
    }
    this.bindKey()
    // 初始化数据
    Promise.all([this.loadConfig()])
      .then(e => {
        this.hideLoading()
        this.login()
        bus.$emit('status', '初始化完成！')
      })
      .catch(e => {
        this.hideLoading()
        this.$magicAlert({
          title: '加载失败',
          content: '请检查配置项baseURL是否正确！'
        })
      })
    bus.$on('search-open', item => {
      if (item.type === 1) {
        this.toolbarIndex = 0
      } else if (item.type === 2) {
        this.toolbarIndex = 1
      }
    })
    bus.$on('logout', () => {
      this.showLogin = true
      this.websocket.close()
    })
    bus.$on('setting', () => {
      this.showSetting = true
    })
    bus.$on('cancelSetting', () => {
      this.showSetting = false
    })
    bus.$on('showLogin', () => this.showLogin = true)
    bus.$on('position-api', (id) => {
      this.toolbarIndex = 0
      this.$refs.apiList.position(id)
    })
    this.open()
  },
  destroyed() {
    bus.$off();
    Key.unbind();
    if (this.websocket)
      this.websocket.close();
  },
  methods: {
    // 隐藏loading
    hideLoading() {
      this.loading = false
      if (typeof hideMaLoading === 'function') {
        hideMaLoading()
      }
    },
    bindKey() {
      let element = this.$refs.container
      // 绑定保存快捷键
      Key.bind(element, Key.Ctrl | Key.S, () => bus.$emit('doSave'))
      // 绑定测试快捷键
      Key.bind(element, Key.Ctrl | Key.Q, () => bus.$emit('doTest'))
    },
    async loadConfig() {
      
    },
    doResizeX() {
      let rect = this.$refs.resizer.getBoundingClientRect()
      let container = this.$refs.apiList
      if (this.toolbarIndex === 1) {
        container = this.$refs.pluginList
      } else if (this.toolbarIndex === 2) {
        container = this.$refs.datasourceList
      }
      let width = container.$el.clientWidth
      document.onmousemove = e => {
        let move = e.clientX - rect.x + +width
        if (move >= 274 && move < 700) {
          this.toolboxWidth = move + 'px'
        }
      }
      document.onmouseup = () => {
        document.onmousemove = document.onmouseup = null
        this.$refs.resizer.releaseCapture && this.$refs.resizer.releaseCapture()
      }
      bus.$emit('update-window-size')
    },
    async login() {
      contants.HEADER_SWEET_TOKEN_VALUE = store.get(contants.HEADER_SWEET_TOKEN) || contants.HEADER_SWEET_TOKEN_VALUE
      bus.$emit('status', '尝试自动登录')
      if ("" !== contants.AUTH_SERVICE_NAME) {
        request.send(`/${contants.AUTH_SERVICE_NAME}/current/user`, [], {method: 'GET'}).success(isLogin => {
          if (isLogin) {
            bus.$emit('status', '自动登录成功')
            this.onLogin()
          } else {
            this.showLogin = true
          }
        }).error(info => {
          bus.$emit('status', '自动登录失败！')
          this.showLogin = true
        })
      } else {
        bus.$emit('status', '无需登录')
        this.onLogin();
        this.createWebSocket();
      }
    },
    createWebSocket() {
      contants.HEADER_REQUEST_SESSION_VALUE = new Date().getTime() + '' + Math.floor(Math.random() * 100000)
      let socketUrl = contants.WEBSOCKET_SERVER.replace("https","ws").replace("http","ws") + "/imserver/" + contants.HEADER_REQUEST_SESSION_VALUE
      this.websocket = new SweetWebSocket(socketUrl)
    },
    /**
     * 传入id来打开对应api或者function
     */
    open(openIds) {
      try {
        JSON.parse(store.get(contants.RECENT_OPENED_TAB)).forEach(id => {
          this.$refs.apiList.openItemById(id)
        })
      } catch (e) {
        // ignore
      }
      openIds = openIds || getQueryVariable('openIds')
      if (openIds) {
        if (typeof openIds === 'string') {
          openIds = openIds.split(',')
        }
        openIds.forEach(id => {
          this.$refs.apiList.openItemById(id)
        })
      }
    }
  },
  watch: {
    toolbarIndex() {
      bus.$emit('update-window-size')
    }
  }
}
</script>
<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.ma-main-container {
  position: absolute;
  top: 30px;
  left: 22px;
  bottom: 24px;
  right: 0px;
  display: flex;
  flex-direction: column;
}

.ma-middle-container {
  flex: 1;
  display: flex;
  overflow: auto;
  background: var(--middle-background);
  border-bottom: 1px solid var(--border-color);
}

.ma-toolbar-container {
  background: var(--background);
  border-right: 1px solid var(--toolbox-border-color);
  width: 22px;
  position: absolute;
  left: 0;
  bottom: 24px;
  top: 30px;
}

.ma-toolbar-container > li {
  padding: 6px 3px;
  cursor: pointer;
  letter-spacing: 2px;
  text-align: center;
  color: var(--color);
  border-bottom: 1px solid var(--toolbox-border-color)
}

.ma-toolbar-container > li > i {
  color: var(--icon-color);
  font-size: 16px;
  padding-top: 3px;
  display: inline-block;
}

.ma-toolbar-container > li:hover {
  background: var(--hover-background);
}

.ma-toolbar-container > li.selected {
  background: var(--selected-background);
  color: var(--selected-color);
}

.ma-resizer-x {
  float: left;
  width: 10px;
  height: 100%;
  margin-left: -5px;
  background: none;
  cursor: e-resize;
  z-index: 1000;
}
</style>
