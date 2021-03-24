<template>
  <div style="display: inline-block;">
    <el-dialog v-el-drag-dialog :visible.sync="dialog" :close-on-click-modal="false" :before-close="cancel" :title="title" append-to-body width="475px" @close="cancel">
      <el-form ref="form" :model="form" :rules="rules" size="small" label-width="88px">
        <el-form-item label="Email" prop="email">
          <el-input v-model="form.email" auto-complete="on" style="width: 200px;" />
          <el-button :loading="codeLoading" :disabled="isDisabled" size="small" @click="sendCode">{{ buttonName }}</el-button>
        </el-form-item>
        <el-form-item label="Code" prop="code">
          <el-input v-model="form.code" style="width: 320px;" />
        </el-form-item>
        <el-form-item label="Password" prop="pass">
          <el-input v-model="form.pass" type="password" style="width: 320px;" />
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
import { validEmail } from '@/utils/validate'
import { updateEmail } from '@/api/user'
import { resetEmail } from '@/api/code'
import elDragDialog from '@/components/el-drag-dialog'
export default {
  directives: { elDragDialog },
  props: {
    email: {
      type: String,
      required: true
    }
  },
  data() {
    const validMail = (rule, value, callback) => {
      if (value === '' || value === null) {
        callback(new Error('New mail cannot be empty'))
      } else if (value === this.email) {
        callback(new Error('The new mail cannot be the same as the old mail'))
      } else if (validEmail(value)) {
        callback()
      } else {
        callback(new Error('Email format error'))
      }
    }
    return {
      loading: false, dialog: false, title: 'Modify email', form: { pass: '', email: '', code: '' },
      user: { email: '', password: '' }, codeLoading: false,
      codeData: { type: 'email', value: '' },
      buttonName: 'Get verification code', isDisabled: false, time: 60,
      rules: {
        pass: [
          { required: true, message: 'The current password cannot be empty', trigger: 'blur' }
        ],
        email: [
          { required: true, validator: validMail, trigger: 'blur' }
        ],
        code: [
          { required: true, message: 'Verification code must be filled', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    cancel() {
      this.resetForm()
    },
    sendCode() {
      if (this.form.email && this.form.email !== this.email) {
        this.codeLoading = true
        this.buttonName = 'Verification code sending'
        this.codeData.value = this.form.email
        const _this = this
        resetEmail(this.codeData).then(res => {
          this.$message({
            showClose: true,
            message: 'Successfully sent, the verification code is valid for 5 minutes',
            type: 'success'
          })
          this.codeLoading = false
          this.isDisabled = true
          this.buttonName = this.time-- + 'Resend in seconds'
          this.timer = window.setInterval(function() {
            _this.buttonName = _this.time + 'Resend in seconds'
            --_this.time
            if (_this.time < 0) {
              _this.buttonName = 'Resend'
              _this.time = 60
              _this.isDisabled = false
              window.clearInterval(_this.timer)
            }
          }, 1000)
        }).catch(err => {
          this.resetForm()
          this.codeLoading = false
          console.log(err.response.data.message)
        })
      }
    },
    doSubmit() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          this.loading = true
          updateEmail(this.form).then(res => {
            this.loading = false
            this.resetForm()
            this.$notify({
              title: 'Mail modified successfully',
              type: 'success',
              duration: 1500
            })
            store.dispatch('GetInfo').then(() => {})
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
      window.clearInterval(this.timer)
      this.time = 60
      this.buttonName = 'Get verification code'
      this.isDisabled = false
      this.form = { pass: '', email: '', code: '' }
    }
  }
}
</script>

<style scoped>

</style>
