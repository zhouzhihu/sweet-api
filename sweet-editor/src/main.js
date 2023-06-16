import Vue from 'vue'
import App from './App.vue'
import SweetContextMenu from './components/common/sweet-contextmenu'
import Modal from './components/common/modal'

Vue.config.productionTip = false

Vue.use(SweetContextMenu)
Vue.use(Modal)

new Vue({
    render: h => h(App),
}).$mount('#app')
