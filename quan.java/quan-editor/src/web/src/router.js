import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter);

const routes = [
    {path: '/', redirect: '/config-editor'},
    {path: '/config-editor', component: () => import("@/components/ConfigEditor")},
    {path: '/bbb', component: () => import("@/components/Page2")},
    {path: '/ccc/:type', component: () => import("@/components/Page3"), props: true},
];

export default new VueRouter({mode: 'history', routes: routes});