import {
  initData,
  download
} from '@/api/data'
import {
  parseTime,
  downloadFile
} from '@/utils/index'
import checkPermission from '@/utils/permission'

export default {
  data() {
    return {
      // data
      data: [],
      // Sorting rules, the default id descending order, support multi-field sorting ['id,desc','createTime,asc']
      sort: ['id,desc'],
      // page
      page: 0,
      // Number of data per page
      size: 10,
      // Total number of data
      total: 0,
      // Request data url
      url: '',
      // Query data parameters
      params: {},
      // Object to be queried
      query: {},
      // waiting time
      time: 50,
      // Is it a new type of form
      isAdd: false,
      // Exported Loading
      downloadLoading: false,
      // Form Loading property
      loading: true,
      // Remove Loading attribute
      delLoading: false,
      delAllLoading: false,
      // Pop-up properties
      dialog: false,
      // Form
      form: {},
      // Reset form
      resetForm: {},
      // Title
      title: ''
    }
  },
  methods: {
    parseTime,
    downloadFile,
    checkPermission,
    async init() {
      if (!await this.beforeInit()) {
        return
      }
      return new Promise((resolve, reject) => {
        this.loading = true
        // Request data
        initData(this.url, this.getQueryParame()).then(data => {
          this.total = data.totalElements
          this.data = data.content
          // time Show table in milliseconds
          setTimeout(() => {
            this.loading = false
          }, this.time)
          resolve(data)
        }).catch(err => {
          this.loading = false
          reject(err)
        })
      })
    },
    beforeInit() {
      return true
    },
    getQueryParame: function() {
      return {
        page: this.page,
        size: this.size,
        sort: this.sort,
        ...this.query,
        ...this.params
      }
    },
    // Change page number
    pageChange(e) {
      this.page = e - 1
      this.init()
    },
    // Change the number of displays per page
    sizeChange(e) {
      this.page = 0
      this.size = e
      this.init()
    },
    // Prevents when deleting the last data of the second page, or multiple selections to delete the data of the second page, the page number error causes the request to have no data
    dleChangePage(size) {
      if (size === undefined) {
        size = 1
      }
      if (this.data.length === size && this.page !== 0) {
        this.page = this.page - 1
      }
    },
    // Query method
    toQuery() {
      this.page = 0
      this.init()
    },
    /**
     * General prompt package
     */
    submitSuccessNotify() {
      this.$notify({
        title: 'Submitted successfully',
        type: 'success',
        duration: 2500
      })
    },
    addSuccessNotify() {
      this.$notify({
        title: 'Added successfully',
        type: 'success',
        duration: 2500
      })
    },
    editSuccessNotify() {
      this.$notify({
        title: 'Edit successfully',
        type: 'success',
        duration: 2500
      })
    },
    delSuccessNotify() {
      this.$notify({
        title: 'Successfully deleted',
        type: 'success',
        duration: 2500
      })
    },
    notify(title, type) {
      this.$notify({
        title: title,
        type: type,
        duration: 2500
      })
    },
    /**
     * You can call beforeDelMethod to do some operations before deleting
     */
    beforeDelMethod() {
      return true
    },
    /**
     * Universal delete
     */
    delMethod(id) {
      if (!this.beforeDelMethod()) {
        return
      }
      this.delLoading = true
      this.crudMethod.del(id).then(() => {
        this.delLoading = false
        this.$refs[id].doClose()
        this.dleChangePage()
        this.delSuccessNotify()
        this.afterDelMethod()
        this.init()
      }).catch(() => {
        this.delLoading = false
        this.$refs[id].doClose()
      })
    },
    afterDelMethod() {
    },
    /**
     * Multi-select delete prompt
     */
    beforeDelAllMethod() {
      this.$confirm('Are you sure to delete the selected data?', 'Notice', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        this.delAllMethod()
      })
    },
    /**
     * Multiple selection delete
     */
    delAllMethod() {
      this.delAllLoading = true
      const data = this.$refs.table.selection
      const ids = []
      for (let i = 0; i < data.length; i++) {
        ids.push(data[i].id)
      }
      this.crudMethod.delAll(ids).then(() => {
        this.delAllLoading = false
        this.dleChangePage(ids.length)
        this.init()
        this.$notify({
          title: 'Successfully deleted',
          type: 'success',
          duration: 2500
        })
      }).catch(() => {
        this.delAllLoading = false
      })
    },
    /**
     * This method can be called before displaying the new popup
     */
    beforeShowAddForm() {
    },
    /**
     * Show new popup
     */
    showAddFormDialog() {
      this.isAdd = true
      this.resetForm = JSON.parse(JSON.stringify(this.form))
      this.beforeShowAddForm()
      this.dialog = true
    },
    /**
     * This method can be called before displaying the edit popup
     */
    beforeShowEditForm(data) {
    },
    /**
     * Show edit popup
     */
    showEditFormDialog(data = '') {
      this.isAdd = false
      if (data) {
        this.resetForm = JSON.parse(JSON.stringify(this.form))
        this.form = JSON.parse(JSON.stringify(data))
      }
      this.beforeShowEditForm(data)
      this.dialog = true
    },
    /**
     * New method
     */
    addMethod() {
      this.crudMethod.add(this.form).then(() => {
        this.addSuccessNotify()
        this.loading = false
        this.afterAddMethod()
        this.cancel()
        this.init()
      }).catch(() => {
        this.loading = false
        this.afterAddErrorMethod()
      })
    },
    /**
     * This method can be called after adding
     */
    afterAddMethod() { },
    /**
     * Call this method after adding failed
     */
    afterAddErrorMethod() {},
    /**
     * General editing method
     */
    editMethod() {
      this.crudMethod.edit(this.form).then(() => {
        this.editSuccessNotify()
        this.loading = false
        this.afterEditMethod()
        this.cancel()
        this.init()
      }).catch(() => {
        this.loading = false
      })
    },
    /**
     * You can call this method after editing
     */
    afterEditMethod() {},
    /**
     * This method can be called before submission
     */
    beforeSubmitMethod() {
      return true
    },
    /**
     * Submit
     */
    submitMethod() {
      if (!this.beforeSubmitMethod()) {
        return
      }
      if (this.$refs['form']) {
        this.$refs['form'].validate((valid) => {
          if (valid) {
            this.loading = true
            if (this.isAdd) {
              this.addMethod()
            } else this.editMethod()
          }
        })
      }
    },
    /**
     * Hide popup
     */
    cancel() {
      this.dialog = false
      if (this.$refs['form']) {
        this.$refs['form'].clearValidate()
        this.form = this.resetForm
      }
    },
    /**
     * Get the title of the popup
     */
    getFormTitle() {
      return this.isAdd ? `Add${this.title}` : `edit${this.title}`
    },
    /**
     * Universal export
     */
    downloadMethod() {
      this.beforeInit()
      this.downloadLoading = true
      download(this.url + '/download', this.params).then(result => {
        this.downloadFile(result, this.title + 'data', 'xlsx')
        this.downloadLoading = false
      }).catch(() => {
        this.downloadLoading = false
      })
    }
  }
}
