<template>
  <div class="ma-tree-wrapper">
    <div class="ma-tree-toolbar">
      <div class="ma-tree-toolbar-search"><i class="ma-icon ma-icon-search"></i><input placeholder="输入关键字搜索"
                                                                                       @input="e => doSearch(e.target.value)"/>
      </div>
      <div>
        <div class="ma-tree-toolbar-btn" title="上传插件" @click="uploadPlugin()">
          <i class="ma-icon ma-icon-group-add"></i>
        </div>
        <div class="ma-tree-toolbar-btn" title="刷新插件" @click="pluginLoad()">
          <i class="ma-icon ma-icon-refresh"></i>
        </div>
      </div>
    </div>
    <div class="ma-tree-container">
      <ul v-show="!showLoading">
        <template v-for="(item,index) in plugins" >
        <li v-if="item._searchShow === true || item._searchShow === undefined" :key="index" @contextmenu.prevent="e => pluginContextMenu(e, item)">
          <i class="ma-icon ma-icon-plugin"/>
          <label>{{item.pluginId}}</label>
          <span class="state">状态：[{{ pluginStates[item.pluginState] }}]   版本：[{{item.version}}]</span>
          <span class="description">{{ item.pluginDescription }}</span>
        </li>
        </template>
      </ul>
      <div class="loading" v-show="showLoading">
      <div class="icon">
        <i class="ma-icon ma-icon-refresh "></i>
      </div>
      加载中...
    </div>
    <div class="no-data" v-show="!showLoading && (!plugins || plugins.length === 0)">无数据</div>
    </div>
    <magic-dialog title="安装插件" :value="showUploadDialog" align="right" @onClose="showUploadDialog = false">
      <template #content>
        <sweet-file ref="uploadFile" placeholder="未选择文件" />
      </template>
      <template #buttons>
        <button class="ma-button active" @click="() => doUpload('increment')">上传</button>
      </template>
    </magic-dialog>
  </div>
</template>

<script>
import bus from '@/scripts/bus.js'
import request from '@/api/request.js'
import MagicDialog from '@/components/common/modal/magic-dialog.vue'
import SweetFile from '@/components/common/sweet-file.vue'
import contants from '@/scripts/contants.js'

export default {
  name: 'SweetPluginList',
  props: {
    groups: Array
  },
  components: {
    MagicDialog,
    SweetFile
  },
  data() {
    return {
      bus: bus,
      // 插件数据
      plugins: [],
      pluginStates: {
        "CREATED": "已创建",
        "DISABLED": "已禁用",
        "RESOLVED": "已准备",
        "STARTED": "已启动",
        "STOPPED": "已停止",
        "FAILED": "失败",
      },
      // 绑定给sweet-tree组件，用来触发子组件强制更新
      forceUpdate: true,
      // 是否展示loading
      showLoading: true,
      showUploadDialog: false
    }
  },
  methods: {
    doSearch(keyword) {
      keyword = keyword.toLowerCase()
      this.plugins.forEach(it => {
        it._searchShow = keyword ? (it.pluginId&&it.pluginId.toLowerCase().indexOf(keyword) > -1) || (it.pluginDescription && it.pluginDescription.toLowerCase().indexOf(keyword) > -1) : true;
      })
      this.$forceUpdate();
    },
    pluginContextMenu(event,item){
      if(item.pluginId){
        this.$magicContextmenu({
          menus: [{
            label : '启动插件',
            icon: 'ma-icon-run',
            onClick: ()=>this.pluginStart(item)
          },{
            label : '停止插件',
            icon: 'ma-icon-stop',
            onClick: ()=>this.pluginStop(item)
          },{
            label : '卸载插件',
            icon: 'ma-icon-clear',
            onClick: ()=>this.pluginUnload(item)
          },{
            label : '删除插件',
            divided: true,
            icon: 'ma-icon-delete',
            onClick: ()=>this.pluginDelete(item)
          },{
            label : '复制ID',
            icon: 'ma-icon-copy',
            onClick: ()=>this.copyIdToClipboard(item)
          }],
          event,
          zIndex: 9999
        });
      }
      return false;
    },
    // 启动插件
    pluginStart(item) {
      if (item.pluginState === "STARTED") {
        this.$magicAlert({content: `插件「${item.pluginId}(${item.version})」处于启动状态`})
        return;
      }
      bus.$emit('status', `准备启动插件「${item.pluginId}(${item.version})」`)
      this.$magicConfirm({
        title: '启动插件',
        content: `是否要启动插件「${item.pluginId}(${item.version}))」`,
        onOk: () => {
          request.send(`/${contants.ESB_SERVICE_NAME}/plugin/start?ids=${item.pluginId}`).success(data => {
            bus.$emit('status', `插件「${item.pluginId}(${item.version}))」已启动`)
            this.initData();
          })
        }
      })
    },
    // 停止插件
    pluginStop(item) {
      if (item.pluginState !== "STARTED") {
        this.$magicAlert({content: `插件「${item.pluginId}(${item.version})」处于停止状态`})
        return;
      }
      bus.$emit('status', `准备停止插件「${item.pluginId}(${item.version})」`)
      this.$magicConfirm({
        title: '停止插件',
        content: `是否要停止插件「${item.pluginId}(${item.version}))」`,
        onOk: () => {
          request.send(`/${contants.ESB_SERVICE_NAME}/plugin/stop?ids=${item.pluginId}`).success(data => {
            bus.$emit('status', `插件「${item.pluginId}(${item.version}))」已停止`)
            this.initData();
          })
        }
      })
    },
    // 卸载插件
    pluginUnload(item) {
      if (item.pluginState === "RESOLVED") {
        this.$magicAlert({content: `插件「${item.pluginId}(${item.version})」处于卸载状态`})
        return;
      }
      bus.$emit('status', `准备卸载插件「${item.pluginId}(${item.version})」`)
      this.$magicConfirm({
        title: '卸载插件',
        content: `是否要卸载插件「${item.pluginId}(${item.version}))」`,
        onOk: () => {
          request.send(`/${contants.ESB_SERVICE_NAME}/plugin/unload?ids=${item.pluginId}`).success(data => {
            bus.$emit('status', `插件「${item.pluginId}(${item.version}))」已卸载`)
            this.initData();
          })
        }
      })
    },
    // 删除插件
    pluginDelete(item) {
      if (item.pluginState !== "STOPPED") {
        this.$magicAlert({content: `请先停止插件「${item.pluginId}(${item.version})」`})
        return;
      }
      bus.$emit('status', `准备删除插件「${item.pluginId}(${item.version})」`)
      this.$magicConfirm({
        title: '删除插件',
        content: `是否要删除插件「${item.pluginId}(${item.version}))」`,
        onOk: () => {
          request.send(`/${contants.ESB_SERVICE_NAME}/plugin?ids=${item.pluginId}`, [], {method: 'DELETE'}).success(data => {
            bus.$emit('status', `插件「${item.pluginId}(${item.version}))」已删除`)
            this.initData();
          })
        }
      })
    },
    copyIdToClipboard(item) {
      try {
        var copyText = document.createElement('textarea')
        copyText.style = 'position:absolute;left:-99999999px'
        document.body.appendChild(copyText)
        copyText.innerHTML = item.pluginId
        copyText.readOnly = false
        copyText.select()
        document.execCommand('copy')
        bus.$emit('status', `插件ID「${item.pluginId}」复制成功`)
      } catch (e) {
        this.$magicAlert({title: '复制插件ID失败，请手动复制', content: item.pluginId})
        console.error(e)
      }
    },
    // 加载插件
    pluginLoad(item) {
      request.send(`/${contants.ESB_SERVICE_NAME}/plugin/load`).success(data => {
        this.initData();
      })
    },
    // 初始化数据
    initData() {
      bus.$emit('status', '正在初始化插件列表')
      this.showLoading = true
      this.plugins = []
      return new Promise((resolve) => {
        request.send(`/${contants.ESB_SERVICE_NAME}/plugin/list`, [], {method: 'GET'}).success(data => {
          this.plugins = data || []
          setTimeout(() => {
            this.showLoading = false
          }, 500)
          bus.$emit('status', '数据源初始化完毕')
          resolve()
        })
      })
    },
    // 安装插件
    uploadPlugin() {
      this.showUploadDialog = true;
    },
    doUpload() {
      let file = this.$refs.uploadFile.getFile();
      if (file) {
        let formData = new FormData();
        formData.append('file', file, file.name);
        this.showUploadDialog = false;
        bus.$emit('status', '准备安装插件')
        request.send(`/${contants.ESB_SERVICE_NAME}/plugin/upload`, formData, {
          method: 'post',
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }).success(() => {
          bus.$emit('status', '插件安装成功')
          this.initData();
        })
      }
    },
    // 强制触发子组件更新
    changeForceUpdate() {
      this.forceUpdate = !this.forceUpdate
    }
  },
  mounted() {
    this.bus.$on('logout', () => this.plugins = []);
    this.bus.$on('refresh-resource',() => {
      this.initData()
    })
  }
}
</script>

<style>
@import './sweet-resource.css';
</style>
<style scoped>
ul li {
  line-height: 20px;
  padding-left: 5px;
}
ul li:hover{
  background: var(--toolbox-list-hover-background);
}
ul li .state {
  padding-left: 17px;
  display: block;
  text-align: left;
  white-space: pre-wrap;
  word-break:break-all;
}
ul li .description {
  width: 300px;
  height: auto;
  padding-left: 17px;
  padding-right: 20px;
  display: block;
  text-align: left;
  white-space: pre-wrap;
  word-break:break-all;
}
</style>
