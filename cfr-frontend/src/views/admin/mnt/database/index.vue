<template>
  <div class="app-container">
    <!--Toolbar-->
    <div class="head-container">
      <div v-if="crud.props.searchToggle">
        <!-- Search -->
        <el-input
          v-model="query.blurry"
          clearable
          placeholder="fuzzy search"
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
          start-placeholder="start date"
          end-placeholder="end date"
        />
        <rrOperation />
      </div>
      <crudOperation :permission="permission">
        <el-button
          slot="right"
          v-permission="['admin','database:add']"
          :disabled="!selectIndex"
          class="filter-item"
          size="mini"
          type="warning"
          icon="el-icon-upload"
          @click="execute"
        >Execute script
        </el-button>
      </crudOperation>
    </div>
    <!--Form component-->
    <eForm ref="execute" :database-info="currentRow" />
    <el-dialog
      append-to-body
      :close-on-click-modal="false"
      :before-close="crud.cancelCU"
      :visible.sync="crud.status.cu> 0"
      :title="crud.status.title"
      width="530px"
    >
      <el-form ref="form" :model="form" :rules="rules" size="small" label-width="100px">
        <el-form-item label="Name" prop="name">
          <el-input v-model="form.name" style="width: 370px" />
        </el-form-item>
        <el-form-item label="JDBC" prop="jdbcUrl">
          <el-input v-model="form.jdbcUrl" style="width: 300px" />
          <el-button :loading="loading" type="success" @click="testConnectDatabase">Test</el-button>
        </el-form-item>
        <el-form-item label="User" prop="userName">
          <el-input v-model="form.userName" style="width: 370px" />
        </el-form-item>
        <el-form-item label="Password" prop="pwd">
          <el-input v-model="form.pwd" type="password" style="width: 370px" />
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
      highlight-current-row
      stripe
      style="width: 100%"
      @selection-change="crud.selectionChangeHandler"
      @current-change="handleCurrentChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="name" width="130px" label="Name" />
      <el-table-column prop="jdbcUrl" label="Address" />
      <el-table-column prop="userName" width="200px" label="Username" />
      <el-table-column prop="createTime" width="200px" label="Date">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column v-permission="['admin','database:edit','database:del']" label="operation" width="150px" align="center">
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
import crudDatabase from '@/api/database'
import { testDbConnect } from '@/api/connect'
import eForm from './execute'
import CRUD, { presenter, header, form, crud } from '@crud/crud'
import rrOperation from '@crud/RR.operation'
import crudOperation from '@crud/CRUD.operation'
import udOperation from '@crud/UD.operation'
import pagination from '@crud/Pagination'
const defaultForm = { id: null, name: null, jdbcUrl: 'jdbc:mysql://', userName: null, pwd: null }
export default {
  name: 'DataBase',
  components: { eForm, pagination, crudOperation, rrOperation, udOperation },
  cruds() {
    return CRUD({ title: 'database', url: 'api/database', crudMethod: { ...crudDatabase }})
  },
  mixins: [presenter(), header(), form(defaultForm), crud()],
  data() {
    return {
      currentRow: {},
      selectIndex: '',
      databaseInfo: '',
      loading: false,
      permission: {
        add: ['admin', 'database:add'],
        edit: ['admin', 'database:edit'],
        del: ['admin', 'database:del']
      },
      rules: {
        name: [
          { required: true, message: 'Please enter the database name', trigger: 'blur' }
        ],
        jdbcUrl: [
          { required: true, message: 'Please enter the database connection address', trigger: 'blur' }
        ],
        userName: [
          { required: true, message: 'Please enter user name', trigger: 'blur' }
        ],
        pwd: [
          { required: true, message: 'Please enter the database password', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    testConnectDatabase() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          this.loading = true
          testDbConnect(this.form).then((res) => {
            this.loading = false
            this.crud.notify(res ? 'Connection succeeded' : 'Connection failed', res ? 'success' : 'error')
          }).catch(() => {
            this.loading = false
          })
        }
      })
    },
    execute() {
      this.$refs.execute.dialog = true
    },
    handleCurrentChange(row) {
      this.currentRow = row
      this.selectIndex = !row ? null : row.id
    }
  }
}
</script>

<style scoped>
</style>
