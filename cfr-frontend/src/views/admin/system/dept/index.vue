<template>
  <div class="app-container">
    <!--Toolbar-->
    <div class="head-container">
      <div v-if="crud.props.searchToggle">
        <!-- Search -->
        <el-input v-model="query.name" clearable size="small" placeholder="Enter department name search" style="width: 200px;" class="filter-item" @keyup.enter.native=" crud.toQuery" />
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
        <el-select v-model="query.enabled" clearable size="small" placeholder="state" class="filter-item" style="width: 90px" @change="crud.toQuery">
          <el-option v-for="item in enabledTypeOptions" :key="item.key" :label="item.display_name" :value="item.key" />
        </el-select>
        <rrOperation />
      </div>
      <crudOperation :permission="permission" />
    </div>
    <!--Form component-->
    <el-dialog v-el-drag-dialog append-to-body :close-on-click-modal="false" :before-close="crud.cancelCU" :visible.sync="crud.status.cu> 0" :title="crud .status.title" width="500px">
      <el-form ref="form" :model="form" :rules="rules" size="small" label-width="100px">
        <el-form-item label="Name" prop="name">
          <el-input v-model="form.name" style="width: 370px;" />
        </el-form-item>
        <el-form-item v-if="form.pid !== 0" label="Status" prop="enabled">
          <el-radio v-for="item in dict.dept_status" :key="item.id" v-model="form.enabled" :label="item.value">{{ item.label }}</el-radio>
        </el-form-item>
        <el-form-item v-if="form.pid !== 0" style="margin-bottom: 0;" label="Department" prop="pid">
          <treeselect v-model="form.pid" :options="depts" style="width: 370px;" placeholder="Select the superior category" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="text" @click="crud.cancelCU">Cancel</el-button>
        <el-button :loading="crud.status.cu === 2" type="primary" @click="crud.submitCU">Confirm</el-button>
      </div>
    </el-dialog>
    <!--Form rendering-->
    <el-table ref="table" v-loading="crud.loading" :tree-props="{children:'children', hasChildren:'hasChildren'}" default-expand-all :data="crud.data" row-key="id" @select="crud.selectChange" @select-all="crud.selectAllChange" @selection-change="crud.selectionChangeHandler">
      <el-table-column :selectable="checkboxT" type="selection" width="55" />
      <el-table-column label="name" prop="name" />
      <el-table-column label="state" align="center" prop="enabled">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.enabled"
            :disabled="scope.row.id === 1"
            active-color="#409EFF"
            inactive-color="#F56C6C"
            @change="changeEnabled(scope.row, scope.row.enabled,)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="creation date">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column v-permission="['admin','dept:edit','dept:del']" label="operation" width="130px" align="center" fixed="right">
        <template slot-scope="scope">
          <udOperation
            :data="scope.row"
            :permission="permission"
            :disabled-dle="scope.row.id === 1"
            msg="Are you sure you want to delete? If there are lower-level nodes, delete them together. This operation cannot be undone!"
          />
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import crudDept from '@/api/dept'
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import CRUD, { presenter, header, form, crud } from '@crud/crud'
import rrOperation from '@crud/RR.operation'
import crudOperation from '@crud/CRUD.operation'
import udOperation from '@crud/UD.operation'
import elDragDialog from '@/components/el-drag-dialog'
const defaultForm = { id: null, name: null, pid: 1, enabled: 'true' }
export default {
  name: 'Dept',
  components: { Treeselect, crudOperation, rrOperation, udOperation },
  directives: { elDragDialog },
  cruds() {
    return CRUD({ title: '_department', url: 'api/dept', crudMethod: { ...crudDept }})
  },
  mixins: [presenter(), header(), form(defaultForm), crud()],
  // Set up the data dictionary
  dicts: ['dept_status'],
  data() {
    return {
      depts: [],
      rules: {
        name: [
          { required: true, message: 'Please enter a name', trigger: 'blur' }
        ]
      },
      permission: {
        add: ['admin', 'dept:add'],
        edit: ['admin', 'dept:edit'],
        del: ['admin', 'dept:del']
      },
      enabledTypeOptions: [
        { key: 'true', display_name: 'Normal' },
        { key: 'false', display_name: 'Disable' }
      ]
    }
  },
  methods: {
    // Operations before adding and editing
    [CRUD.HOOK.afterToCU](crud, form) {
      form.enabled = `${form.enabled}`
      // Get all departments
      crudDept.getDepts({ enabled: true }).then(res => {
        this.depts = res.content
      })
    },
    // Verification before submission
    [CRUD.HOOK.afterValidateCU]() {
      if (!this.form.pid && this.form.id !== 1) {
        this.$message({
          message: 'The superior department cannot be empty',
          type: 'warning'
        })
        return false
      }
      return true
    },
    // Change state
    changeEnabled(data, val) {
      this.$confirm('This action will "' + this.dict.label.dept_status[val] + '" ' + data.name + 'continue?', 'Notice', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        crudDept.edit(data).then(res => {
          this.crud.notify(this.dict.label.dept_status[val] + 'success', CRUD.NOTIFICATION_TYPE.SUCCESS)
        }).catch(err => {
          data.enabled = !data.enabled
          console.log(err.response.data.message)
        })
      }).catch(() => {
        data.enabled = !data.enabled
      })
    },
    checkboxT(row, rowIndex) {
      return row.id !== 1
    }
  }
}
</script>

<style scoped>

</style>
