import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter);

const routes = [
    {path: '/config', component: () => import("@/components/config/ConfigList")},
    {path: '/bbb', component: () => import("@/components/Page2")},
    {path: '/ccc/:type', component: () => import("@/components/Page3"), props: true},
    {path: '/**', redirect: '/config'}
];

export default new VueRouter({routes: routes, mode: 'history', base: process.env.BASE_URL});