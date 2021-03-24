import axios from 'axios'
import router from '@/router/routers'
import { Notification, MessageBox } from 'element-ui'
import store from '../store'
import { getToken } from '@/utils/auth'
import Config from '@/settings'

// Create axios instance
const service = axios.create({
  baseURL: process.env.NODE_ENV === 'production' ? process.env.VUE_APP_BASE_API : '/', // api  base_url
  timeout: Config.timeout // Request timeout
})

// request interceptor
service.interceptors.request.use(
  config => {
    if (getToken()) {
      config.headers['Authorization'] = getToken() // Let each request carry a custom token, please modify it according to the actual situation
    }
    config.headers['Content-Type'] = 'application/json'
    return config
  },
  error => {
    // Do something with request error
    console.log(error) // for debug
    Promise.reject(error)
  }
)

// response Interceptor
service.interceptors.response.use(
  response => {
    const code = response.status
    if (code < 200 || code > 300) {
      Notification.error({
        title: response.message
      })
      return Promise.reject('error')
    } else {
      return response.data
    }
  },
  error => {
    let code = 0
    try {
      code = error.response.data.status
    } catch (e) {
      if (error.toString().indexOf('Error: timeout') !== -1) {
        Notification.error({
          title: 'Network request timed out',
          duration: 5000
        })
        return Promise.reject(error)
      }
    }
    if (code) {
      if (code === 401) {
        MessageBox.confirm(
          'The login status has expired, you can stay on this page or log in again',
          'System hint',
          {
            confirmButtonText: 're-register',
            cancelButtonText: 'cancel',
            type: 'warning'
          }
        ).then(() => {
          store.dispatch('LogOut').then(() => {
            location.reload() // In order to re-instantiate the vue-router object to avoid bugs
          })
        })
      } else if (code === 403) {
        router.push({ path: '/401' })
      } else {
        const errorMsg = error.response.data.message
        if (errorMsg !== undefined) {
          Notification.error({
            title: errorMsg,
            duration: 5000
          })
        }
      }
    } else {
      Notification.error({
        title: 'Interface request failed',
        duration: 5000
      })
    }
    return Promise.reject(error)
  }
)
export default service
