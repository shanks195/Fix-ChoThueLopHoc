import Vue from 'vue'
import Router from 'vue-router'
import Layout from '../layout/index'

Vue.use(Router)

export const constantRouterMap = [
  {
    path: '/',
    component: () => import('../layout/client/ClientLayout.vue'),
    children: [
      { path: '', component: () => import('@/views/client/Index.vue') },
      { path: '/price-sum', component: () => import('@/views/client/priceSum.vue') },
      { path: '/contact', component: () => import('@/views/client/Contact.vue') },
      { path: '/introduce', component: () => import('@/views/client/Introduce.vue') },
    ]
  },
  { path: '/login',
    meta: { title: 'Log in', noCache: true },
    component: () => import('@/views/login'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/features/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/features/401'),
    hidden: true
  },
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path*',
        component: () => import('@/views/features/redirect')
      }
    ]
  },
  {
    path: '/admin/cfr/thuephonghoc',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: '',
        component: () => import('@/views/home'),
        name: 'Dashboard',
        meta: { title: 'Home', icon: 'index', affix: true, noCache: true }
      },
      {
        hidden: true,
        path: 'center',
        component: () => import('@/views/admin/system/user/center'),
        name: 'Personal center',
        meta: { title: 'Personal center' }
      }
    ]
  }
]

export default new Router({
  mode: 'hash',
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRouterMap
})
