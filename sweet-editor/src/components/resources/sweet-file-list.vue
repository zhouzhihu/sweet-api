<template>
  <div class="ma-tree-wrapper">
    <div class="ma-tree-toolbar">
      <div class="ma-tree-toolbar-search"><i class="ma-icon ma-icon-search"></i><input placeholder="输入关键字搜索"
                                                                                       @input="e => doSearch(e.target.value)"/>
      </div>
      <div>
        <div class="ma-tree-toolbar-btn" title="新建文件夹" @click="openCreateFolderModal()">
          <i class="ma-icon ma-icon-group-add"></i>
        </div>
        <div class="ma-tree-toolbar-btn" title="刷新接口" @click="initData()">
          <i class="ma-icon ma-icon-refresh"></i>
        </div>
        <div class="ma-tree-toolbar-btn" title="折叠" @click="rebuildTree(true)">
          <i class="ma-icon ma-icon-folding"></i>
        </div>
      </div>
    </div>
    <sweet-tree :data="tree" :forceUpdate="forceUpdate" :loading="showLoading">
      <template #folder="{ item }">
        <div
            :id="'sweet-file-list-' + item.id"
            :style="{ 'padding-left': 17 * item.level + 'px' }"
            :title="(item.name || '') + '(' + (item.code || '') + ')'"
            @dblclick="children(item)"
            @contextmenu.prevent="e => folderRightClickHandle(e, item)"
            :class="item.selectRightItem ? 'ma-tree-item-header ma-tree-select' : 'ma-tree-item-header ma-tree-hover'"
        >
          <i :class="item.opened ? 'ma-icon-arrow-bottom' : 'ma-icon-arrow-right'" class="ma-icon" @click.stop="" @click="children(item)"/>
          <i class="ma-icon ma-icon-list"></i>
          <label>{{ item.name }}</label>
        </div>
      </template>
      <template #file="{ item }">
        <div
            :id="'sweet-file-list-' + item.id"
            :style="{ 'padding-left': 17 * item.level + 'px' }"
            :title="(item.name || '') + '(' + (item.code || '') + ')'"
            @contextmenu.prevent="e => fileRightClickHandle(e, item)"
            :class="item.selectRightItem ? 'ma-tree-item-header ma-tree-select' : 'ma-tree-item-header ma-tree-hover'"
        >
          <i class="ma-icon ma-icon-file"></i>
          <label>{{ item.name + "." + item.fileType }}</label>
          <span>({{ item.code }})</span>
        </div>
      </template>
    </sweet-tree>
    <magic-dialog v-model="createFolderObj.visible" :title="createFolderObj.id ? '修改' : '创建'"
                  align="right"
                  @onClose="createFolderAction(false)">
      <template #content>
        <label>名称：</label>
        <sweet-input placeholder="输入名称" style="width:100%" v-model="createFolderObj.name"/>
        <div style="height: 2px"></div>
        <label v-show="createFolderObj.code && createFolderObj.code !== ''">关键字：</label>
        <sweet-input placeholder="输入关键字" v-show="createFolderObj.code && createFolderObj.code !== ''" style="width:100%" v-model="createFolderObj.code"/>
      </template>
      <template #buttons>
        <button class="ma-button active" @click="createFolderAction(true)">{{ createFolderObj.id ? '修改' : '创建' }}</button>
        <button class="ma-button" @click="createFolderAction(false)">取消</button>
      </template>
    </magic-dialog>
    <magic-dialog title="上传文件" v-model="createFileObj.visible" align="right" @onClose="createFileAction(false)">
      <template #content>
        <label>文件：</label>
        <sweet-file ref="uploadFile" placeholder="未选择文件" />
        <div style="height: 2px"></div>
        <sweet-input placeholder="关键字" style="width:100%" v-model="createFileObj.code"/>
      </template>
      <template #buttons>
        <button class="ma-button active" @click="createFileAction(true)">上传</button>
        <button class="ma-button" @click="createFileAction(false)">取消</button>
      </template>
    </magic-dialog>
  </div>
</template>

<script>
import bus from '../../scripts/bus.js'
import SweetTree from '../common/sweet-tree.vue'
import MagicDialog from '@/components/common/modal/magic-dialog.vue'
import SweetInput from '@/components/common/sweet-input.vue'
import SweetFile from '@/components/common/sweet-file.vue'
import {download as downloadFile} from '@/scripts/utils.js'
import request from '@/api/request.js'
import contants from '@/scripts/contants.js'

export default {
  name: 'SweetApiList',
  props: {
    apis: Array
  },
  components: {
    SweetTree,
    MagicDialog,
    SweetFile,
    SweetInput
  },
  data() {
    return {
      bus: bus,
      tree: [],
      forceUpdate: true,
      // 是否展示tree-loading
      showLoading: true,
      // 新建
      createFolderObj: {
        visible: false,
        id: '',
        parentId: '',
        name: '',
        code: ''
      },
      createFileObj: {
        visible: false,
        id: '',
        parentId: '',
        code: ''
      }
    }
  },
  methods: {
    doSearch(keyword) {
      this.changeForceUpdate()
    },
    // 初始化数据
    initData() {
      bus.$emit('status', '正在初始化文件列表')
      this.showLoading = true
      this.tree = []
      return new Promise((resolve) => {
        request.send(`/${contants.ESB_SERVICE_NAME}/file/children`, {parentId: -1}, {method: 'GET'}).success(data => {
          let rootFileList = data || []
          rootFileList.forEach(element => {
            element.level = 0
            element.folder = true
          });
          this.tree = rootFileList
          this.showLoading = false
          resolve()
        })
      })
    },
    children(item) {
      let parentId = item.id
      let level = item.level
      this.$set(item, 'opened', !item.opened)
      if (item.children !== undefined) {
        this.changeForceUpdate()
        return;
      }
      request.send(`/${contants.ESB_SERVICE_NAME}/file/children`, {parentId: parentId}, {method: 'GET'}).success(data => {
          let childrenList = data || []
          childrenList.forEach(element => {
            element.level = level + 1
            if (element.unid === undefined || null === element.unid)
              element.folder = true
            else
              element.file = true
          })
          item.children = childrenList
          this.changeForceUpdate()
      });
    },
    // 更新树元素
    updateTreeItem(newItem) {
      let findItem = (item) => {
        if (item.id === newItem.id) {
          item.name = newItem.name
          item.code = newItem.code
          this.changeForceUpdate()
          return item
        }
        if (item.children !== undefined) {
          item.children.forEach(childrenItem => {
            findItem(childrenItem)
          })
        }
      }
      this.tree.forEach(item => findItem(item))
    },
    // 增加树元素
    addTreeItem(newItem) {
      let findItem = (item) => {
        if (item.id === newItem.parentId) {
          if(!item.opened) {
            this.children(item)
          } else {
            newItem.level = item.level + 1
            if (newItem.unid === undefined || null === newItem.unid){
              newItem.folder = true
              item.children.unshift(newItem)
            } else {
              newItem.file = true
              item.children.push(newItem)
            }
          }
          this.changeForceUpdate()
          return item
        }
        if (item.children !== undefined) {
          item.children.forEach(childrenItem => {
            findItem(childrenItem)
          })
        }
      }
      this.tree.forEach(item => findItem(item))
    },
    // 删除树元素
    delTreeItem(delItem) {
      let findItem = (item) => {
        if (item.id === delItem.parentId) {
          item.children.splice(item.children.findIndex(o => o.id === delItem.id), 1)
          this.changeForceUpdate()
          return item
        }
        if (item.children !== undefined) {
          item.children.forEach(childrenItem => {
            findItem(childrenItem)
          })
        }
      }
      this.tree.forEach(item => findItem(item))
    },
    // 文件夹右键菜单
    folderRightClickHandle(event, item) {
      this.$set(item, 'selectRightItem', true)
      this.$magicContextmenu({
        menus: [
          {
            label: '上传文件',
            icon: 'ma-icon-plus',
            onClick: () => {
              this.openCreateFileModal(item)
            }
          },
          {
            label: '新建文件夹',
            icon: 'ma-icon-group-add',
            onClick: () => {
              this.openCreateFolderModal(null, item)
            }
          },
          {
            label: '修改文件夹',
            icon: 'ma-icon-update',
            onClick: () => {
              this.openCreateFolderModal(item)
            }
          },
          {
            label: '删除文件夹',
            icon: 'ma-icon-delete',
            divided: true,
            onClick: () => {
              this.deleteFolderAction(item)
            }
          },
          {
            label: '刷新',
            divided: true,
            icon: 'ma-icon-refresh',
            onClick: () => {
              this.initData()
            }
          },
          {
            label: '复制ID',
            divided: true,
            icon: 'ma-icon-copy',
            onClick: () => {
              this.copyIdToClipboard(item)
            }
          }
        ],
        event,
        zIndex: 9999,
        destroy: () => {
          this.$set(item, 'selectRightItem', false)
          this.changeForceUpdate()
        }
      })
      this.changeForceUpdate()
      return false
    },
    // 文件右键菜单
    fileRightClickHandle(event, item) {
      this.$set(item, 'selectRightItem', true)
      this.$magicContextmenu({
        menus: [
          {
            label: '下载文件',
            icon: 'ma-icon-copy',
            onClick: () => {
              this.download(item)
            }
          },
          {
            label: '修改文件',
            icon: 'ma-icon-update',
            onClick: () => {
              this.openCreateFolderModal(item)
            }
          },
          {
            label: '删除文件',
            icon: 'ma-icon-delete',
            divided: true,
            onClick: () => {
              this.deleteFolderAction(item)
            }
          },
          {
            label: '复制KEY',
            icon: 'ma-icon-copy',
            onClick: () => {
              this.copyKeyToClipboard(item)
            }
          }
        ],
        event,
        zIndex: 9999,
        destroy: () => {
          this.$set(item, 'selectRightItem', false)
          this.changeForceUpdate()
        }
      })
      this.changeForceUpdate()
      return false
    },
    // 打开新建文件夹弹窗
    openCreateFolderModal(item, parentItem) {
      if (item) {
        for (const key in this.createFolderObj) {
          this.createFolderObj[key] = item[key]
        }
        console.log(this.createFolderObj)
      }
      if (parentItem) {
        this.createFolderObj.parentId = parentItem.id
      }
      this.createFolderObj.visible = true
    },
    // 保存|修改文件夹
    createFolderAction(flag) {
      if (flag === true) {
        // 验证数据
        if (!this.createFolderObj.name) {
          this.$magicAlert({content: '文件夹名称不能为空'})
          return false
        }
        // id存在发送更新请求，不存在发送新增请求
        if (this.createFolderObj.id) {
          request.send(`/${contants.ESB_SERVICE_NAME}/file`, JSON.stringify(this.createFolderObj), {
            method: 'post',
            headers: {
              'Content-Type': 'application/json'
            },
            transformRequest: []
          }).success(data => {
              bus.$emit('status', `文件夹「${data.name}」更新成功`)
              this.updateTreeItem(data)
              this.initCreateFolderObj()
          })
        } else {
          request.send(`/${contants.ESB_SERVICE_NAME}/file`, JSON.stringify(this.createFolderObj), {
            method: 'post',
            headers: {
              'Content-Type': 'application/json'
            },
            transformRequest: []
          }).success(data => {
            bus.$emit('status', `文件夹「${data.name}」创建成功`)
            this.addTreeItem(data)
            this.initCreateFolderObj()
          })
        }
      } else {
        this.initCreateFolderObj()
      }
    },
    // 打开上传文件弹窗
    openCreateFileModal(item) {
      if (item) {
        this.createFileObj.parentId = item.id
        this.createFileObj.visible = true
      }
    },
    // 上传文件
    createFileAction(flag) {
      if (flag === true) {
        // 验证数据
        if (!this.createFileObj.code) {
          this.$magicAlert({content: '文件关键字不能为空'})
          return false
        }
        let file = this.$refs.uploadFile.getFile()
        if (file) {
          let formData = new FormData();
          formData.append('file', file, file.name)
          bus.$emit('status', `准备上传文件`)
          request.send(`/${contants.ESB_SERVICE_NAME}/file/upload?parentId=${this.createFileObj.parentId}&code=${this.createFileObj.code}`, formData, {
            method: 'post',
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }).success((data) => {
            this.$magicAlert({
              content: '上传成功!'
            })
            bus.$emit('status', `上传成功`)
            this.addTreeItem(data)
            this.initCreateFileObj()
          })
        }
      } else {
        this.initCreateFileObj()
      }
    },
    // 下载文件
    download(item) {
      request.send(`/${contants.ESB_SERVICE_NAME}/file/download`, {code: item.code}, {
          method: 'get',
          headers: {
            'Content-Type': 'application/json'
          },
          transformRequest: [],
          responseType: 'blob'
        }).success(blob => {
          downloadFile(blob, `${item.name}.${item.fileType}`)
          bus.$emit('status', `全部数据已导出完毕`)
        })
    },
    // 复制文件关键字到剪贴板
    copyKeyToClipboard(item) {
      try {
        var copyText = document.createElement('textarea')
        copyText.style = 'position:absolute;left:-99999999px'
        document.body.appendChild(copyText)
        copyText.innerHTML = item.code
        copyText.readOnly = false
        copyText.select()
        document.execCommand('copy')
        bus.$emit('status', `关键字「${item.code}」复制成功`)
      } catch (e) {
        this.$magicAlert({title: '复制关键字失败，请手动复制', content: item.code})
        console.error(e)
      }
    },
    copyIdToClipboard(item) {
      try {
        var copyText = document.createElement('textarea')
        copyText.style = 'position:absolute;left:-99999999px'
        document.body.appendChild(copyText)
        copyText.innerHTML = item.id
        copyText.readOnly = false
        copyText.select()
        document.execCommand('copy')
        bus.$emit('status', `关键字「${item.id}」复制成功`)
      } catch (e) {
        this.$magicAlert({title: '复制关键字失败，请手动复制', content: item.id})
        console.error(e)
      }
    },
    // 删除文件夹
    deleteFolderAction(item) {
      bus.$emit('status', `准备删除文件夹「${item.name}」`)
      this.$magicConfirm({
        title: '删除文件夹',
        content: `是否要删除文件夹「${item.name}」`,
        onOk: () => {
          request.send(`/${contants.ESB_SERVICE_NAME}/file`, {ids: item.id}, {method: 'DELETE'}).success(data => {
            if (data) {
              bus.$emit('status', `文件夹「${item.name}」已删除`)
              this.delTreeItem(item)
            } else {
              this.$magicAlert({content: '删除失败'})
            }
          })
        }
      })
    },
    // 初始化createFolderObj对象
    initCreateFolderObj() {
      this.createFolderObj = {
        visible: false,
        id: '',
        parentId: '',
        name: '',
        code: ''
      }
    },
    initCreateFileObj() {
      this.createFileObj = {
        visible: false,
        id: '',
        parentId: '',
        code: ''
      }
    },
    // 强制触发子组件更新
    changeForceUpdate() {
      this.forceUpdate = !this.forceUpdate
    },
  },
  mounted() {
    this.bus.$on('logout', () => this.tree = [])
    this.bus.$on('refresh-resource', () => {
      this.initData()
    })
  }
}
</script>

<style>
@import './sweet-resource.css';
</style>
