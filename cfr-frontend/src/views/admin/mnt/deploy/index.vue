<template>
  <div class="app-container">
    <!--Toolbar-->
    <div class="head-container">
      <div v-if="crud.props.searchToggle">
        <!-- Search -->
        <el-input
          v-model="query.appName"
          clearable
          placeholder="Enter application name to query"
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
      <crudOperation :permission="permission">
        <template slot="right">
          <el-button
            v-permission="['admin','deploy:add']"
            :disabled="!selectIndex"
            class="filter-item"
            size="mini"
            type="primary"
            icon="el-icon-upload"
            @click="sysRestore"
          >System Restore
          </el-button>
          <el-button
            v-permission="['admin','deploy:add']"
            :disabled="!selectIndex"
            class="filter-item"
            size="mini"
            type="primary"
            icon="el-icon-upload"
            @click="serverStatus"
          >Status query
          </el-button>
          <el-button
            v-permission="['admin','deploy:add']"
            :disabled="!selectIndex"
            class="filter-item"
            size="mini"
            type="success"
            icon="el-icon-upload"
            @click="startServer"
          >Start
          </el-button>
          <el-button
            v-permission="['admin','deploy:add']"
            :disabled="!selectIndex"
            class="filter-item"
            size="mini"
            type="danger"
            icon="el-icon-upload"
            @click="stopServer"
          >Stop
          </el-button>
          <el-button
            v-permission="['admin','deploy:add']"
            :disabled="!selectIndex"
            class="filter-item"
            size="mini"
            type="warning"
            icon="el-icon-upload"
            @click="deploy"
          >One-click deployment
          </el-button>
        </template>
      </crudOperation>
    </div>
    <!--Form component-->
    <el-dialog
      append-to-body
      :close-on-click-modal="false"
      :before-close="crud.cancelCU"
      :visible.sync="crud.status.cu> 0"
      :title="crud.status.title"
      width="500px"
    >
      <el-form ref="form" :model="form" :rules="rules" size="small" label-width="80px">
        <el-form-item label="application" prop="app.id">
          <el-select v-model.number="form.app.id" placeholder="Please select" style="width: 370px">
            <el-option v-for="item in apps" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="server" prop="deploys">
          <el-select v-model="form.deploys" multiple placeholder="Please select" style="width: 370px">
            <el-option v-for="item in servers" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="text" @click="crud.cancelCU">Cancel</el-button>
        <el-button :loading="crud.status.cu === 2" type="primary" @click="crud.submitCU">Confirm</el-button>
      </div>
    </el-dialog>
    <!--System Restore Components-->
    <fForm ref="sysRestore" :key="times" :app-name="appName" />
    <dForm ref="deploy" />
    <!--Form rendering-->
    <el-table
      ref="table"
      v-loading="crud.loading"
      :data="crud.data"
      highlight-current-row
      stripe
      style="width: 100%"
      @selection-change="crud.selectionChangeHandler"
      @current-change="handleCurrentChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="app.name" label="Name" />
      <el-table-column prop="servers" label="List" />
      <el-table-column prop="createTime" label="Date">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column v-permission="['admin','deploy:edit','deploy:del']" label="operation" width="150px" align="center">
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
import crudDeploy from '@/api/deploy'
import dForm from './deploy'
import fForm from './sysRestore'
import CRUD, { presenter, header, form, crud } from '@crud/crud'
import rrOperation from '@crud/RR.operation'
import crudOperation from '@crud/CRUD.operation'
import udOperation from '@crud/UD.operation'
import pagination from '@crud/Pagination'

const defaultForm = { id: null, app: { id: null }, deploys: [] }
export default {
  name: 'Deploy',
  components: { dForm, fForm, pagination, crudOperation, rrOperation, udOperation },
  cruds() {
    return CRUD({ title: '部署', url: 'api/deploy', crudMethod: { ...crudDeploy }})
  },
  mixins: [presenter(), header(), form(defaultForm), crud()],
  data() {
    return {
      currentRow: {}, selectIndex: '', appName: '', urlHistory: '',
      times: 0, appId: '', deployId: '', apps: [], servers: [],
      permission: {
        add: ['admin', 'deploy:add'],
        edit: ['admin', 'deploy:edit'],
        del: ['admin', 'deploy:del']
      },
      rules: {
        'app.id': [
          { required: true, message: 'Application cannot be empty', trigger: 'blur', type: 'number' }
        ],
        deploys: [
          { required: true, message: 'Server cannot be empty', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    [CRUD.HOOK.beforeRefresh]() {
      this.selectIndex = ''
      return true
    },
    // Add operations before editing
    [CRUD.HOOK.beforeToCU](crud, form) {
      this.initSelect()
      const deploys = []
      form.deploys.forEach(function(deploy, index) {
        deploys.push(deploy.id)
      })
      this.form.deploys = deploys
    },
    // Before submitting
    [CRUD.HOOK.beforeSubmit]() {
      const deploys = []
      this.form.deploys.forEach(function(data, index) {
        const deploy = { id: data }
        deploys.push(deploy)
      })
      this.form.deploys = deploys
      return true
    },
    deploy() {
      this.$refs.deploy.dialog = true
      this.$refs.deploy.deployInfo = this.currentRow
    },
    sysRestore() {
      this.$refs.sysRestore.dialog = true
    },
    handleCurrentChange(row) {
      this.currentRow = row
      this.selectIndex = !row ? null : row.id
      this.appName = !row ? null : row.app.name
      this.times = this.times + !row ? 0 : 1
      this.appId = !row ? null : row.appId
      this.deployId = !row ? null : row.id
    },
    startServer() {
      crudDeploy.startServer(JSON.stringify(this.currentRow))
        .then(res => {
        })
        .catch(err => {
          console.log('error:' + err.response.data.message)
        })
    },
    stopServer() {
      crudDeploy.stopServer(JSON.stringify(this.currentRow))
        .then(res => {
        })
        .catch(err => {
          console.log('error:' + err.response.data.message)
        })
    },
    serverStatus() {
      crudDeploy.serverStatus(JSON.stringify(this.currentRow))
        .then(res => {
        })
        .catch(err => {
          console.log('error:' + err.response.data.message)
        })
    },
    initSelect() {
      crudDeploy.getApps().then(res => {
        this.apps = res.content
      })
      crudDeploy.getServers().then(res => {
        this.servers = res.content
      })
    }
  }
}
</script>

<style scoped>
</style>
