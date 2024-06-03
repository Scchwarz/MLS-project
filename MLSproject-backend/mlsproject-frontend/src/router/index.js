import { createRouter, createWebHistory } from 'vue-router'
import {unauthorized} from "@/net";
import FileView from '../views/index/FileView.vue'
import LocalView from '../views/index/LocalView.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'welcome',
            component: () => import('@/views/WelcomeView.vue'),
            children: [
                {
                    path: '',
                    name: 'welcome-login',
                    component: () => import('@/views/welcome/LoginPage.vue')
                }, {
                    path: 'register',
                    name: 'welcome-register',
                    component: () => import('@/views/welcome/RegisterPage.vue')
                }, {
                    path: 'forget',
                    name: 'welcome-forget',
                    component: () => import('@/views/welcome/ForgetPage.vue')
                }
            ]
        },{
            path: '/index',
            name: 'index',
            component: () => import('@/views/IndexView.vue'),
            children: [
                {
                    path: '',
                    name: 'index-main',
                    component: () => import('@/views/index/MainPage.vue')
                }, {
                    path: '/person',
                    name: 'index-person',
                    component: () => import('@/views/index/PersonPage.vue')
                }, {
                    path: '/file',
                    name: 'index-file',
                    component: () => import('@/views/index/FilePage.vue')
                }, {
                    path: '/search',
                    name: 'index-search',
                    component: () => import('@/views/index/SearchPage.vue')
                }, {
                    path: '/content-search',
                    name: 'content-search',
                    component: () => import('@/views/index/ContentSearchPage.vue')
                }, {
                    path: '/favourite',
                    name: 'favourite',
                    component: () => import('@/views/index/FavouriteFilePage.vue')
                }, {
                    path: '/like',
                    name: 'like',
                    component: () => import('@/views/index/LikeFilePage.vue')
                }, {
                    path: '/message',
                    name: 'message',
                    component: () => import('@/views/index/MessagePage.vue')
                }, {
                    path: '/content-search/:id/:content', // 使用动态路由参数，将文件id传递到FileView组件中
                    name: 'FileView',
                    component: FileView,
                    props: (route) => ({
                        id: route.params.id,
                        content: route.params.content,
                    }),
                },  {
                    path: '/:url',
                    name: 'LocalView',
                    component: LocalView,
                    props: (route) => ({
                        url: route.params.url,
                    }),
                }
            ]
        },{
            path: '/manager',
            name: 'manager',
            component: () => import('@/views/ManagerView.vue'),
            children: [
                {
                    path: '',
                    name: 'manager-main',
                    component: () => import('@/views/manager/ManagerPage.vue')
                }, {
                    path: '/check',
                    name: 'manager-check',
                    component: () => import('@/views//manager/CheckPage.vue')
                }, {
                    path: '/file-manage',
                    name: 'manage-file',
                    component: () => import('@/views//manager/FileManagePage.vue')
                }, {
                    path: '/user-manage',
                    name: 'manage-user',
                    component: () => import('@/views//manager/UserManagePage.vue')
                }
            ]
        }
    ]
})
router.beforeEach((to, from, next) => {
    const isUnauthorized = unauthorized();

    if (to.name && to.name.startsWith('welcome') && !isUnauthorized) {
        next('/index');
    } else if (to.fullPath && to.fullPath.startsWith('/index') && isUnauthorized) {
        next('/');
    } else {
        next();
    }
});



export default router