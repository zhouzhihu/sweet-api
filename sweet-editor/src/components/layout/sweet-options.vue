<template>
  <div class="ma-bottom-container">
    <div v-show="selectedTab" ref="resizer" class="ma-resizer-y" @mousedown="doResizeY"></div>
    <div v-show="selectedTab" ref="content" :style="{ height: contentHeight }" class="ma-bottom-content-container">
      <sweet-bottom-panel v-for="item in tabs" :key="'bottom_tab_content_' + item.id"
                          :buttons="item.buttons" :class="{ visible: selectedTab === item.id }" :selectedTab.sync="selectedTab"
                          :title="item.name">
        <component v-bind:is="item.component" :info.sync="info"/>
      </sweet-bottom-panel>
    </div>
    <ul class="ma-bottom-tab not-select">
      <li v-for="item in tabs" :key="'bottom_tab_' + item.id" :class="{ selected: selectedTab === item.id, 'float-right': item.right }"
          @click="selectedTab = selectedTab === item.id ? null : item.id"><i :class="'ma-icon ma-icon-' + item.icon"/>{{item.name}}</li>
    </ul>
  </div>
</template>

<script>
import SweetBottomPanel from './sweet-bottom-panel.vue'
import SweetRequest from './sweet-request.vue'
import SweetGroup from './sweet-group.vue'
import SweetRun from './sweet-run.vue'
import SweetLog from './sweet-log.vue'
import SweetSettings from './sweet-settings.vue'
import SweetTodo from './sweet-todo.vue'
import SweetEvent from './sweet-event.vue'
import SweetActuator from './sweet-actuator.vue'
import contants from '@/scripts/contants.js'
import bus from '@/scripts/bus.js'
import request from '@/api/request.js'

export default {
  name: 'SweetOptions',
  data() {
    return {
      info: {},
      isApi: true,
      tabGroup: "api",
      contentHeight: '300px',
      selectedTab: '',
      tabs: [],
      apiTabs: [
        {id: 'request', name: '接口信息', icon: 'parameter', component: SweetRequest},
        {id: 'result', name: '执行结果', icon: 'run', component: SweetRun},
        {id: 'actuator', name: '执行器', icon: 'step-over', component: SweetActuator}
      ],
      apiGroupTabs : [
        {id: 'group', name: '分组信息', icon: 'parameter', component: SweetGroup}
      ],
      commonTabs: [
        {id: 'log', name: '运行日志', icon: 'log', component: SweetLog},
        {id: 'setting', name: '全局参数', icon: 'settings', component: SweetSettings},
        {id: 'todo', name: 'TODO', icon: 'todo', component: SweetTodo},
        {id: 'event', name: '事件', icon: 'event', component: SweetEvent, right: true}
      ]
    }
  },
  mounted() {
    this.tabs = this.apiTabs.concat(this.commonTabs)
    bus.$on('api-group-selected', group => {
      this.info = group;
      request.send(`/${contants.ESB_SERVICE_NAME}/api/${group.id}/info`, [], {method: 'GET'}).success(data => {
        this.info = {...this.info, paths: data.paths}
      });
      if (this.tabGroup !== "group") {
        this.tabs = this.apiGroupTabs.concat(this.commonTabs);
        //this.selectedTab = this.tabs[0].id
        this.selectedTab = null;
      }
      this.tabGroup = "group";
    })
    bus.$on('opened', info => {
      if(info.empty){
        return;
      }
      this.isApi = info._type === 'api';
      this.info = info
      if (this.isApi) {
        if (this.tabGroup !== "api") {
          this.tabs = this.apiTabs.concat(this.commonTabs)
          //this.selectedTab = this.tabs[0].id
          this.selectedTab = null;
        }
        this.$nextTick(() => {
          bus.$emit('update-request-body-definition', info.requestBodyDefinition);
          bus.$emit('update-request-body', info.requestBody);
          bus.$emit('update-response-body-definition', info.responseBodyDefinition);
          bus.$emit('update-response-body', info.responseBody);
        })
      }
      this.tabGroup = "api";
    })
    bus.$on('switch-tab', target => {
      if (!this.tabs.some(it => it.id === target)) {
        if(this.apiTabs.some(it => it.id === target)){
          this.tabs = this.apiTabs;
        }else if(this.apiGroupTabs.some(it => it.id === target)){
          this.tabs = this.apiGroupTabs;
        }
        this.tabs = this.tabs.concat(this.commonTabs)
      }
      this.$set(this, 'selectedTab', target)
      bus.$emit('update-window-size')
    })
  },
  components: {
    SweetBottomPanel
  },
  methods: {
    doResizeY() {
      let box = this.$refs.content.getClientRects()[0]
      document.onmousemove = e => {
        if (e.clientY > 150) {
          var move = box.height - (e.clientY - box.y)
          if (move > 30) {
            this.contentHeight = move + 'px'
            bus.$emit('update-window-size')
          }
        }
      }
      document.onmouseup = () => {
        document.onmousemove = document.onmouseup = null
        this.$refs.resizer.releaseCapture && this.$refs.resizer.releaseCapture()
      }
      bus.$emit('update-window-size')
    }
  },
  watch: {
    selectedTab() {
      bus.$emit('update-window-size')
    }
  }
}
</script>

<style>
.ma-bottom-container {
  background: var(--background);
}

.ma-bottom-container .ma-bottom-content-container {
  border-bottom: 1px solid var(--tab-bar-border-color);
  height: 300px;
}

.ma-bottom-container .ma-bottom-content-container > div {
  display: none;
}

.ma-bottom-container .ma-bottom-content-container > .visible {
  display: block;
}

.ma-bottom-tab li {
  float: left;
  cursor: pointer;
  padding: 0 8px;
  height: 24px;
  line-height: 24px;
  color: var(--color);
}
.ma-bottom-tab li.float-right{
  float: right;
}

.ma-bottom-tab li i {
  color: var(--icon-color);
  padding: 0 2px;
  display: inline-block;
  vertical-align: top;
}

.ma-bottom-tab li:hover {
  background: var(--hover-background);
}

.ma-bottom-tab li.selected {
  background: var(--selected-background);
}

.ma-resizer-y {
  position: absolute;
  width: 100%;
  height: 10px;
  margin-top: -5px;
  background: none;
  cursor: n-resize;
}

.ma-nav {
  border-bottom: 1px solid var(--tab-bar-border-color);
  height: 24px;
}

.ma-nav li {
  display: inline-block;
  height: 24px;
  line-height: 24px;
  padding: 0 10px;
  cursor: pointer;
}

.ma-nav li.selected {
  background: var(--selected-background);
}

.ma-nav li:hover:not(.selected) {
  background: var(--hover-background);
}

.ma-layout {
  display: flex;
  flex: auto;
  flex-direction: row;
  height: 100%;
}

.ma-layout .ma-layout-container {
  flex: auto;
  height: 100%;
  width: 100%;
}

.ma-layout .ma-header > * {
  padding: 0 2px;
  border-right: none !important;
}

.ma-layout .ma-table-row > * {
  display: inline-block;
  width: 60%;
  height: 23px;
  line-height: 23px;
  border-bottom: 1px solid var(--input-border-color);
  border-right: 1px solid var(--input-border-color);
  background: var(--background);
}

.ma-layout .ma-table-row input:focus {
  border-color: var(--input-border-foucs-color);
}

.ma-layout .ma-table-row input {
  border-color: transparent;
  border-top-color: transparent;
  border-right-color: transparent;
  border-bottom-color: transparent;
  border-left-color: transparent;
}

.ma-layout .ma-table-row > *:first-child,
.ma-layout .ma-table-row > *:last-child {
  width: 20%;
}

.ma-layout .ma-content {
  flex: auto;
  overflow-x: hidden;
  height: calc(100% - 50px);
}

.ma-layout .ma-sider {
  border-right: 1px solid var(--tab-bar-border-color);
}

.ma-layout .ma-sider > * {
  width: 18px;
  height: 18px;
  line-height: 18px;
  margin: 3px;
  text-align: center;
  padding: 0;
  color: var(--icon-color);
  border-radius: 2px;
}

.ma-layout .ma-sider > *:hover {
  background: var(--hover-background);
}

.ma-nav-tab li {
  display: inline-block;
  height: 24px;
  line-height: 24px;
  padding: 0 10px;
  cursor: pointer;
}

.ma-nav-tab li.selected {
  background: var(--selected-background);
}

.ma-nav-tab li:hover {
  background: var(--hover-background);
}

.ma-nav-tab {
  border-bottom: 1px solid var(--tab-bar-border-color);
  height: 24px;
}
</style>
