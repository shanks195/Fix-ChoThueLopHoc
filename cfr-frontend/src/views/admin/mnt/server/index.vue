<template>
  <div class="app-container">
    <!--Toolbar-->
    <div class="head-container">
      <div v-if="crud.props.searchToggle">
        <!-- Search -->
        <el-input
          v-model="query.id"
          clearable
          placeholder="Enter name or IP search"
          style="width: 200px"
          class="filter-item"
          @keyup.enter.native="crud.toQuery"
        />
        <el-date-picker
          v-model="query.createTime"
          :default-time="['00:00:00','23:59:59']"
          type="daterange"
          range-separator=":"
          size="small"
          class="date-item"
          value-format="yyyy-MM-dd HH:mm:ss"
          start-placeholder="Start date"
          end-placeholder="End date"
        />
        <rrOperation />
      </div>
      <crudOperation :permission="permission" />
    </div>
    <!--Form component-->
    <el-dialog
      append-to-body
      :close-on-click-modal="false"
      :before-close="crud.cancelCU"
      :visible.sync="crud.status.cu> 0"
      :title="crud.status.title"
      width="470px"
    >
      <el-form ref="form" :model="form" :rules="rules" size="small" label-width="55px">
        <el-form-item label="Name" prop="name">
          <el-input v-model="form.name" style="width: 370px" />
        </el-form-item>
        <el-form-item label="IP" prop="ip">
          <el-input v-model="form.ip" style="width: 370px" />
        </el-form-item>
        <el-form-item label="Port" prop="port">
          <el-input-number v-model.number="form.port" controls-position="right" style="width: 370px;" />
        </el-form-item>
        <el-form-item label="Account" prop="account">
          <el-input v-model="form.account" style="width: 370px" />
        </el-form-item>
        <el-form-item label="Password" prop="password">
          <el-input v-model="form.password" type="password" style="width: 200px" />
          <el-button :loading="loading" type="success" style="align: right;" @click="testConnectServer">Test connection</el-button>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="text" @click="crud.cancelCU">Cancel</el-button>
        <el-button :loading="crud.status.cu === 2" type="primary" @click="crud.submitCU">Confirm</el-button>
      </div>
    </el-dialog>
    <!--Form rendering-->
    <el-table
      ref="table"
      v-loading="crud.loading"
      :data="crud.data"
      style="width: 100%"
      @selection-change="crud.selectionChangeHandler"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="name" label="Name" />
      <el-table-column prop="ip" label="IP" />
      <el-table-column prop="port" label="Port" />
      <el-table-column prop="account" label="Account" />
      <el-table-column prop="createTime" label="Date">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column
        v-permission="['admin','serverDeploy:edit','serverDeploy:del']"
        label="action"
        width="150px"
        align="center"
      >
        <template slot-scope="scope">
          <udOperation
            :data="scope.row"
            :permission="permission"
          />
        </template>
      </el-table-column>
    </el-table>
    <!--Paging component-->
    <pagination />
  </div>
</template>

<script>
import crudServer from '@/api/serverDeploy'
import { testServerConnect } from '@/api/connect'
import { validateIP } from '@/utils/validate'
import CRUD, { presenter, header, form, crud } from '@crud/crud'
import rrOperation from '@crud/RR.operation'
import crudOperation from '@crud/CRUD.operation'
import udOperation from '@crud/UD.operation'
import pagination from '@crud/Pagination'
const defaultForm = { id: null, name: null, ip: null, port: 22, account: 'root', password: null }
export default {
  name: 'Server',
  components: { pagination, crudOperation, rrOperation, udOperation },
  cruds() {
    return CRUD({ title: 'server', url: 'api/serverDeploy', crudMethod: { ...crudServer }})
  },
  mixins: [presenter(), header(), form(defaultForm), crud()],
  data() {
    return {
      accountList: [],
      accountMap: {},
      loading: false,
      permission: {
        add: ['admin', 'serverDeploy:add'],
        edit: ['admin', 'serverDeploy:edit'],
        del: ['admin', 'serverDeploy:del']
      },
      rules: {
        name: [
          { required: true, message: 'Please enter a name', trigger: 'blur' }
        ],
        ip: [
          { required: true, message: 'Please enter IP', trigger: 'blur' },
          { validator: validateIP, trigger: 'change' }
        ],
        port: [
          { required: true, message: 'Please enter the port', trigger: 'blur', type: 'number' }
        ],
        account: [
          { required: true, message: 'Please enter account', trigger: 'blur' }
        ],
        password: [
          { required: true, message: 'Please enter password', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    testConnectServer() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          this.loading = true
          testServerConnect(this.form).then((res) => {
            this.loading = false
            this.$notify({
              title: res ? 'Connection succeeded' : 'Connection failed',
              type: res ? 'success' : 'error',
              duration: 2500
            })
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  /deep/ .el-input-number .el-input__inner {
    text-align: left;
  }
</style>
