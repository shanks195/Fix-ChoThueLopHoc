<template>
  <div style="display: inline-block">
    <el-dialog :visible.sync="dialog" :close-on-click-modal="false" :before-close="cancel" :title="title" append-to-body width="600px" @close="cancel">
      <el-form ref="form" :model="form" :rules="rules" size="small" label-width="120px">
        <el-form-item label="OldPass" prop="oldPass">
          <el-input v-model="form.oldPass" type="password" auto-complete="on" style="width: 370px;" />
        </el-form-item>
        <el-form-item label="New Pass" prop="newPass">
          <el-input v-model="form.newPass" type="password" auto-complete="on" style="width: 370px;" />
        </el-form-item>
        <el-form-item label="Confirm Pass" prop="confirmPass">
          <el-input v-model="form.confirmPass" type="password" auto-complete="on" style="width: 370px;" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="text" @click="cancel">Cancel</el-button>
        <el-button :loading="loading" type="primary" @click="doSubmit">Submit</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import store from '@/store'
import { updatePass } from '@/api/user'
export default {
  data() {
    const confirmPass = (rule, value, callback) => {
      if (value) {
        if (this.form.newPass !== value) {
          callback(new Error('The two passwords entered are inconsistent'))
        } else {
          callback()
        }
      } else {
        callback(new Error('Please enter the password again'))
      }
    }
    return {
      loading: false, dialog: false, title: 'Change Password', form: { oldPass: '', newPass: '', confirmPass: '' },
      rules: {
        oldPass: [
          { required: true, message: 'Please enter the old password', trigger: 'blur' }
        ],
        newPass: [
          { required: true, message: 'Please enter a new password', trigger: 'blur' },
          { min: 6, max: 20, message: '6 to 20 characters in length', trigger: 'blur' }
        ],
        confirmPass: [
          { required: true, validator: confirmPass, trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    cancel() {
      this.resetForm()
    },
    doSubmit() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          this.loading = true
          updatePass(this.form).then(res => {
            this.resetForm()
            this.$notify({
              title: 'Password changed successfully, please log in again',
              type: 'success',
              duration: 1500
            })
            setTimeout(() => {
              store.dispatch('LogOut').then(() => {
                location.reload() // In order to re-instantiate the vue-router object to avoid bugs
              })
            }, 1500)
          }).catch(err => {
            this.loading = false
            console.log(err.response.data.message)
          })
        } else {
          return false
        }
      })
    },
    resetForm() {
      this.dialog = false
      this.$refs['form'].resetFields()
      this.form = { oldPass: '', newPass: '', confirmPass: '' }
    }
  }
}
</script>

<style scoped>

</style>
