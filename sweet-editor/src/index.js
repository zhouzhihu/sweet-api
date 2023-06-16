import './assets/index.css'

import SweetEditor from './components/sweet-editor'
import SweetContextMenu from './components/common/sweet-contextmenu'
import Modal from './components/common/modal'
import _Vue from 'vue'

/* 打包组件使用 */
import 'monaco-editor/esm/vs/editor/editor.main.js';

export function install(Vue) {
    if (install.installed) return
    install.installed = true
    Vue.component('SweetEditor', SweetEditor)
    Vue.use(SweetContextMenu)
    Vue.use(Modal)
}

const plugin = {
    install
}

let GlobalVue = null
if (typeof window !== 'undefined' && window.Vue) {
    GlobalVue = window.Vue
} else if (typeof global !== 'undefined' && global.Vue) {
    GlobalVue = global.Vue
} else {
    GlobalVue = _Vue;
}
if (GlobalVue) {
    GlobalVue.use(plugin)
}

export default SweetEditor