const baseUrl = process.env.VUE_APP_BASE_API
const api = {
  state: {
    // Deployment package upload
    deployUploadApi: baseUrl + '/api/deploy/upload',
    // SQL script upload
    databaseUploadApi: baseUrl + '/api/database/upload',
    // Real-time console
    socketApi: baseUrl + '/websocket?token=kl',
    // Upload picture
    imagesUploadApi: baseUrl + '/api/pictures',
    // Modify avatar
    updateAvatarApi: baseUrl + '/api/users/updateAvatar',
    // Sql druid
    sqlApi: baseUrl + '/druid',
    // swagger
    swaggerApi: baseUrl + '/swagger-ui.html',
    // File Upload
    fileUploadApi: baseUrl + '/api/localStorage',
    // baseUrl，
    baseApi: baseUrl
  }
}

export default api
