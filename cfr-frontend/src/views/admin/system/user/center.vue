<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="8" :lg="6" :xl="5" style="margin-bottom: 10px">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>Personal information</span>
          </div>
          <div>
            <div style="text-align: center">
              <el-upload
                :show-file-list="false"
                :on-success="handleSuccess"
                :on-error="handleError"
                :headers="headers"
                :action="updateAvatarApi"
                class="avatar-uploader"
              >
                <img :src="user.avatar ? baseApi + '/avatar/' + user.avatar : Avatar" title="Click to upload avatar" class="avatar">
              </el-upload>
            </div>
            <ul class="user-info">
              <li><div style="height: 100%"><svg-icon icon-class="login" /> Account<div class="user-right">{{ user.username }}</div></div></li>
              <li><svg-icon icon-class="user1" /> Nickname <div class="user-right">{{ user.nickName }}</div></li>
              <li><svg-icon icon-class="phone" /> Phone <div class="user-right">{{ user.phone }}</div></li>
              <li><svg-icon icon-class="email" /> Email <div class="user-right">{{ user.email }}</div></li>
              <li><svg-icon icon-class="dept" /> Dept <div class="user-right"> {{ user.dept.name }} / {{ user.job.name }}</div></li>
              <li>
                <svg-icon icon-class="anq" /> Security Settings
                <div class="user-right">
                  <a @click="$refs.pass.dialog = true">Change Password</a>
                  <a @click="$refs.email.dialog = true">Modify email</a>
                </div>
              </li>
            </ul>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="16" :lg="18" :xl="19">
        <!--    User information    -->
        <el-card class="box-card">
          <el-tabs v-model="activeName" @tab-click="handleClick">
            <el-tab-pane label="User information" name="first">
              <el-form ref="form" :model="form" :rules="rules" style="margin-top: 10px;" size="small" label-width="65px">
                <el-form-item label="Nick" prop="nickName">
                  <el-input v-model="form.nickName" style="width: 35%" />
                  <span style="color: #C0C0C0;margin-left: 10px;">User nickname is not used as login</span>
                </el-form-item>
                <el-form-item label="Phone" prop="phone">
                  <el-input v-model="form.phone" style="width: 35%;" />
                  <span style="color: #C0C0C0;margin-left: 10px;">Mobile phone number cannot be repeated</span>
                </el-form-item>
                <el-form-item label="Gender">
                  <el-radio-group v-model="form.sex" style="width: 178px">
                    <el-radio label="Male">Male</el-radio>
                    <el-radio label="Female">Female</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="">
                  <el-button :loading="saveLoading" size="mini" type="primary" @click="doSubmit">Submit</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            <!--    Operation log    -->
            <el-tab-pane label="Operation log" name="second">
              <el-table v-loading="loading" :data="data" style="width: 100%;">
                <el-table-column prop="description" label="Description" />
                <el-table-column prop="requestIp" label="IP" />
                <el-table-column :show-overflow-tooltip="true" prop="address" label="IP source" />
                <el-table-column prop="browser" label="Browser" />
                <el-table-column prop="time" label="Time" align="center">
                  <template slot-scope="scope">
                    <el-tag v-if="scope.row.time <= 300">{{ scope.row.time }}ms</el-tag>
                    <el-tag v-else-if="scope.row.time <= 1000" type="warning">{{ scope.row.time }}ms</el-tag>
                    <el-tag v-else type="danger">{{ scope.row.time }}ms</el-tag>
                  </template>
                </el-table-column>
                <el-table-column
                  align="right"
                >
                  <template slot="header">
                    <div style="display:inline-block;float: right;cursor: pointer" @click="init">Creation date<i class="el-icon-refresh" style="margin-left: 40px" /></div>
                  </template>
                  <template slot-scope="scope">
                    <span>{{ parseTime(scope.row.createTime) }}</span>
                  </template>
                </el-table-column>
              </el-table>
              <!--Paging component-->
              <el-pagination
                :total="total"
                :current-page="page + 1"
                style="margin-top: 8px;"
                layout="total, prev, pager, next, sizes"
                @size-change="sizeChange"
                @current-change="pageChange"
              />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
    <updateEmail ref="email" :email="user.email" />
    <updatePass ref="pass" />
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import updatePass from './center/updatePass'
import updateEmail from './center/updateEmail'
import { getToken } from '@/utils/auth'
import store from '@/store'
import { isvalidPhone } from '@/utils/validate'
import crud from '@/mixins/crud'
import Avatar from '@/assets/images/avatar.png'
import { editUser } from '@/api/user'
export default {
  name: 'Center',
  components: { updatePass, updateEmail },
  mixins: [crud],
  data() {
    // Custom verification
    const validPhone = (rule, value, callback) => {
      if (!value) {
        callback(new Error('Please enter your phone number'))
      } else if (!isvalidPhone(value)) {
        callback(new Error('Please enter the correct 10-digit mobile number'))
      } else {
        callback()
      }
    }
    return {
      Avatar: Avatar,
      activeName: 'first',
      saveLoading: false,
      headers: {
        'Authorization': getToken()
      },
      form: {},
      rules: {
        nickName: [
          { required: true, message: 'Please enter user nickname', trigger: 'blur' },
          { min: 2, max: 30, message: '2 to 30 characters in length', trigger: 'blur' }
        ],
        phone: [
          { required: true, trigger: 'blur', validator: validPhone }
        ]
      }
    }
  },
  computed: {
    ...mapGetters([
      'user',
      'updateAvatarApi',
      'baseApi'
    ])
  },
  created() {
    this.form = { id: this.user.id, nickName: this.user.nickName, sex: this.user.sex, phone: this.user.phone }
    store.dispatch('GetInfo').then(() => {})
  },
  methods: {
    handleClick(tab, event) {
      if (tab.name === 'second') {
        this.init()
      }
    },
    beforeInit() {
      this.url = 'api/logs/user'
      return true
    },
    handleSuccess(response, file, fileList) {
      this.$notify({
        title: 'Avatar modified successfully',
        type: 'success',
        duration: 2500
      })
      store.dispatch('GetInfo').then(() => {})
    },
    // Failed to monitor upload
    handleError(e, file, fileList) {
      const msg = JSON.parse(e.message)
      this.$notify({
        title: msg.message,
        type: 'error',
        duration: 2500
      })
    },
    doSubmit() {
      if (this.$refs['form']) {
        this.$refs['form'].validate((valid) => {
          if (valid) {
            this.saveLoading = true
            editUser(this.form).then(() => {
              this.editSuccessNotify()
              store.dispatch('GetInfo').then(() => {})
              this.saveLoading = false
            }).catch(() => {
              this.saveLoading = false
            })
          }
        })
      }
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
  .avatar-uploader-icon {
    font-size: 28px;
    width: 120px;
    height: 120px;
    line-height: 120px;
    text-align: center
  }

  .avatar {
    width: 120px;
    height: 120px;
    display: block;
    border-radius: 50%
  }
  .user-info {
    padding-left: 0;
    list-style: none;
    li{
      border-bottom: 1px solid #F0F3F4;
      padding: 11px 0;
      font-size: 13px;
    }
    .user-right {
      float: right;

      a{
        color: #317EF3;
      }
    }
  }
</style>
