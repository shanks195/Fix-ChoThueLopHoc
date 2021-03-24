<template>
  <div class="app-container">
    <!--Toolbar-->
    <div class="head-container">
      <div v-if="crud.props.searchToggle">
        <!-- Search -->
        <el-input
          v-model="query.name"
          clearable
          placeholder="Enter name to search"
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
        <el-button
          slot="left"
          v-permission="['admin','app:add']"
          :disabled="!currentRow"
          class="filter-item"
          size="mini"
          type="primary"
          icon="el-icon-plus"
          @click="copy"
        >Copy
        </el-button>
      </crudOperation>
    </div>
    <!--Form component-->
    <el-dialog
      append-to-body
      :close-on-click-modal="false"
      :before-close="crud.cancelCU"
      :visible.sync="crud.status.cu> 0"
      :title="crud.status.title"
      width="800px"
    >
      <el-form ref="form" :model="form" :rules="rules" size="small" label-width="100px">
        <el-form-item label="Name" prop="name">
          <el-input v-model="form.name" style="width: 670px" placeholder="The name of the file or directory after deployment, used for backup" />
        </el-form-item>
        <el-form-item label="Port" prop="port">
          <el-input-number v-model.number="form.port" placeholder="For example: 8080" />
        </el-form-item>
        <el-form-item label="Upload" prop="uploadPath">
          <el-input v-model="form.uploadPath" style="width: 670px" placeholder="for example: /opt/upload" />
        </el-form-item>
        <el-form-item label="Deployment" prop="deployPath">
          <el-input v-model="form.deployPath" style="width: 670px" placeholder="for example: /opt/app" />
        </el-form-item>
        <el-form-item label="Backup" prop="backupPath">
          <el-input v-model="form.backupPath" style="width: 670px" placeholder="For example: /opt/backup" />
        </el-form-item>
        <el-form-item label="Script" prop="deployScript">
          <el-input v-model="form.deployScript" :rows="3" type="textarea" autosize style="width: 670px" placeholder="" />
        </el-form-item>
        <el-form-item label="Start" prop="startScript">
          <el-input v-model="form.startScript" :rows="3" type="textarea" autosize style="width: 670px" placeholder="" />
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
      style="width: 100%"
      @selection-change="crud.selectionChangeHandler"
      @current-change="handleCurrentChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="name" label="Name" />
      <el-table-column prop="port" label="Port" />
      <el-table-column prop="uploadPath" label="Upload" />
      <el-table-column prop="deployPath" label="Directory" />
      <el-table-column prop="backupPath" label="Backup" />
      <el-table-column prop="createTime" label="Date">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column v-permission="['admin','app:edit','app:del']" label="Operation" width="150px" align="center">
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
import crudApp from '@/api/app'
import CRUD, { presenter, header, form, crud } from '@crud/crud'
import rrOperation from '@crud/RR.operation'
import crudOperation from '@crud/CRUD.operation'
import udOperation from '@crud/UD.operation'
import pagination from '@crud/Pagination'

const defaultForm = {
  id: null,
  name: null,
  port: 8080,
  uploadPath: '',
  deployPath: '',
  backupPath: '',
  startScript: null,
  deployScript: null
}
export default {
  name: 'App',
  components: { pagination, crudOperation, rrOperation, udOperation },
  cruds() {
    return CRUD({ title: 'App', url: 'api/app', crudMethod: { ...crudApp }})
  },
  mixins: [presenter(), header(), form(defaultForm), crud()],
  data() {
    return {
      currentRow: null,
      permission: {
        add: ['admin', 'app:add'],
        edit: ['admin', 'app:edit'],
        del: ['admin', 'app:del']
      },
      rules: {
        name: [
          { required: true, message: 'Please enter the application name', trigger: 'blur' }
        ],
        port: [
          { required: true, message: 'lease enter the application port', trigger: 'blur', type: 'number' }
        ],
        uploadPath: [
          { required: true, message: 'Please enter the upload directory', trigger: 'blur' }
        ],
        deployPath: [
          { required: true, message: 'Please enter the deployment directory', trigger: 'blur' }
        ],
        backupPath: [
          { required: true, message: 'Please enter the backup directory', trigger: 'blur' }
        ],
        startScript: [
          { required: true, message: 'Please enter the startup script', trigger: 'blur' }
        ],
        deployScript: [
          { required: true, message: 'Please enter the deployment script', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    copy() {
      for (const key in this.currentRow) {
        this.form[key] = this.currentRow[key]
      }
      this.form.id = null
      this.form.createTime = null
      this.crud.toAdd()
    },
    handleCurrentChange(row) {
      this.currentRow = JSON.parse(JSON.stringify(row))
    }
  }
}
</script>

<style scoped>
</style>
