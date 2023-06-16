import Vue from 'vue'
import contants from './contants.js'
import {formatDate} from "@/scripts/utils.js";
const statusLog = [];
const bus = new Vue()
try {
    let element = document.createElement("script");
    element.src = "https://s4.cnzz.com/z_stat.php?id=1280031557&web_id=1280031557";
    element.setAttribute('async', true);
    let s = document.getElementsByTagName("script")[0];
    s.parentNode.insertBefore(element, s);
    element.onload = element.onreadystatechange = function () {
        if (!this.readyState || this.readyState == 'loaded' || this.readyState == 'complete') {
            bus.$emit('report', contants.MAGIC_API_VERSION);
        }
    }
} catch (ignored) {

}
bus.$on('report', (eventId) => {
    try {
        window._czc.push(["_trackEvent",eventId,eventId])
    } catch (ignored) {

    }
})
bus.$on('status', (content) => {
    statusLog.push({
        timestamp: formatDate(new Date()),
        content
    })
})
bus.$getStatusLog = () => statusLog;
bus.$clearStatusLog = () => statusLog.length = 0
export default bus 