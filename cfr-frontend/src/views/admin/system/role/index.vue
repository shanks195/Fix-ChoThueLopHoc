<template>
  <div class="app-container">
    <div class="head-container">
      <div v-if="crud.props.searchToggle">
        <el-input
          v-model="query.blurry"
          size="small"
          clearable
          placeholder="Enter name or description to search"
          style="width: 200px;"
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
    <el-dialog
      v-el-drag-dialog
      append-to-body
      :close-on-click-modal="false"
      :before-close="crud.cancelCU"
      :visible.sync="crud.status.cu > 0"
      :title="crud.status.title"
      width="600px"
    >
      <el-form ref="form" :inline="true" :model="form" :rules="rules" size="small" label-width="120px">
        <el-form-item label="Name" prop="name">
          <el-input v-model="form.name" style="width: 145px;" />
        </el-form-item>
        <el-form-item label="Permission" prop="permission">
          <el-input v-model="form.permission" style="width: 145px;" />
        </el-form-item>
        <el-form-item label="Scope" prop="dataScope">
          <el-select v-model="form.dataScope" style="width: 145px" placeholder="Please select data range" @change="changeScope">
            <el-option
              v-for="item in dateScopes"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="Role level" prop="level">
          <el-input-number v-model.number="form.level" :min="1" controls-position="right" style="width: 145px;" />
        </el-form-item>
        <el-form-item v-if="form.dataScope === 'Customize'" style="width: 140px;" label="Permission" prop="depts">
          <treeselect v-model="form.depts" :options="depts" multiple class="branch" style="width: 480px" placeholder="please choose" />
        </el-form-item>
        <el-form-item label="Description" prop="remark">
          <el-input v-model="form.remark" style="width: 420px;" rows="5" type="textarea" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="text" @click="crud.cancelCU">Cancel</el-button>
        <el-button :loading="crud.status.cu === 2" type="primary" @click="crud.submitCU">Submit</el-button>
      </div>
    </el-dialog>
    <el-row :gutter="15">
      <el-col :xs="24" :sm="24" :md="16" :lg="16" :xl="17" style="margin-bottom: 10px">
        <el-card class="box-card" shadow="never">
          <div slot="header" class="clearfix">
            <span class="role-span">Role list</span>
          </div>
          <el-table
            ref="table"
            v-loading="crud.loading"
            highlight-current-row
            style="width: 100%;"
            :data="crud.data"
            @selection-change="crud.selectionChangeHandler"
            @current-change="handleCurrentChange"
          >
            <el-table-column :selectable="checkboxT" type="selection" width="55" />
            <el-table-column prop="name" label="Name" />
            <el-table-column prop="dataScope" label="Scope" />
            <el-table-column prop="permission" label="Permission" />
            <el-table-column prop="level" label="Level" />
            <el-table-column :show-overflow-tooltip="true" prop="remark" label="Remark" />
            <el-table-column :show-overflow-tooltip="true" width="135px" prop="createTime" label="Time">
              <template slot-scope="scope">
                <span>{{ parseTime(scope.row.createTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column
              v-permission="['admin','roles:edit','roles:del']"
              label="Operating"
              width="130px"
              align="center"
              fixed="right"
            >
              <template slot-scope="scope">
                <udOperation
                  v-if="scope.row.level >= level"
                  :data="scope.row"
                  :permission="permission"
                />
              </template>
            </el-table-column>
          </el-table>
          <pagination />
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="7">
        <el-card class="box-card" shadow="never">
          <div slot="header" class="clearfix">
            <el-tooltip class="item" effect="dark" content="Select the assigned role assignment menu" placement="top">
              <span class="role-span">Menu distribution</span>
            </el-tooltip>
            <el-button
              v-permission="['admin','roles:edit']"
              :disabled="!showButton"
              :loading="menuLoading"
              icon="el-icon-check"
              size="mini"
              style="float: right; padding: 6px 9px"
              type="primary"
              @click="saveMenu"
            >Save
            </el-button>
          </div>
          <el-tree
            ref="menu"
            :data="menus"
            :default-checked-keys="menuIds"
            :props="defaultProps"
            check-strictly
            accordion
            show-checkbox
            node-key="id"
          />
        </el-card>

      </el-col>
    </el-row>
  </div>
</template>

<script>
import crudRoles from '@/api/role'
import { getDepts } from '@/api/dept'
import { getMenusTree } from '@/api/menu'
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import CRUD, { presenter, header, form, crud } from '@crud/crud'
import rrOperation from '@crud/RR.operation'
import crudOperation from '@crud/CRUD.operation'
import udOperation from '@crud/UD.operation'
import pagination from '@crud/Pagination'
import elDragDialog from '@/components/el-drag-dialog'
const defaultForm = { id: null, name: null, depts: [], remark: null, dataScope: 'All', level: 3, permission: null }

export default {
  name: 'Role',
  components: { Treeselect, pagination, crudOperation, rrOperation, udOperation },
  directives: { elDragDialog },
  cruds() {
    return CRUD({ title: '_Character', url: 'api/roles', sort: 'level,asc', crudMethod: { ...crudRoles }})
  },
  mixins: [presenter(), header(), form(defaultForm), crud()],
  data() {
    return {
      defaultProps: { children: 'children', label: 'label' },
      dateScopes: ['All', 'This level', 'Customize'], level: 3,
      currentId: 0, menuLoading: false, showButton: false,
      menus: [], menuIds: [], depts: [],
      permission: {
        add: ['admin', 'roles:add'],
        edit: ['admin', 'roles:edit'],
        del: ['admin', 'roles:del']
      },
      rules: {
        name: [
          { required: true, message: 'Please enter a name', trigger: 'blur' }
        ],
        permission: [
          { required: true, message: 'Please enter permissions', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getMenus()
    crudRoles.getLevel().then(data => {
      this.level = data.level
    })
    this.$nextTick(() => {
      this.crud.toQuery()
    })
  },
  methods: {
    [CRUD.HOOK.afterRefresh]() {
      this.$refs.menu.setCheckedKeys([])
    },
    [CRUD.HOOK.beforeToEdit](crud, form) {
      if (form.dataScope === 'Customize') {
        this.getDepts()
      }
      const depts = []
      form.depts.forEach(function(dept, index) {
        depts.push(dept.id)
      })
      form.depts = depts
    },
    [CRUD.HOOK.afterValidateCU](crud) {
      if (crud.form.dataScope === 'Customize' && crud.form.depts.length === 0) {
        this.$message({
          message: 'Custom data permission cannot be empty',
          type: 'warning'
        })
        return false
      } else if (crud.form.dataScope === 'Customize') {
        const depts = []
        crud.form.depts.forEach(function(data, index) {
          const dept = { id: data }
          depts.push(dept)
        })
        crud.form.depts = depts
      } else {
        crud.form.depts = []
      }
      return true
    },
    [CRUD.HOOK.afterAddError](crud) {
      this.afterErrorMethod(crud)
    },
    [CRUD.HOOK.afterEditError](crud) {
      this.afterErrorMethod(crud)
    },
    afterErrorMethod(crud) {
      const depts = []
      crud.form.depts.forEach(function(dept, index) {
        depts.push(dept.id)
      })
      crud.form.depts = depts
    },
    getMenus() {
      getMenusTree().then(res => {
        this.menus = res
      })
    },
    // trigger single selection
    handleCurrentChange(val) {
      if (val) {
        const _this = this
        // Clear the selection of the menu
        this.$refs.menu.setCheckedKeys([])
        // Save the current role id
        this.currentId = val.id
        this.showButton = this.level <= val.level
        // initialization
        this.menuIds = []
        // Menu data needs special processing
        val.menus.forEach(function(data, index) {
          _this.menuIds.push(data.id)
        })
      }
    },
    // Save menu
    saveMenu() {
      this.menuLoading = true
      const role = { id: this.currentId, menus: [] }
      // Get the half-selected parent node data and save it
      this.$refs.menu.getHalfCheckedNodes().forEach(function(data, index) {
        const menu = { id: data.id }
        role.menus.push(menu)
      })
      // Get the selected key value
      this.$refs.menu.getCheckedKeys().forEach(function(data, index) {
        const menu = { id: data }
        role.menus.push(menu)
      })
      crudRoles.editMenu(role).then(res => {
        this.crud.notify('Saved successfully', CRUD.NOTIFICATION_TYPE.SUCCESS)
        this.menuLoading = false
        this.update()
      }).catch(err => {
        this.menuLoading = false
        console.log(err.response.data.message)
      })
    },
    // Change data
    update() {
      // No refresh update table data
      crudRoles.get(this.currentId).then(res => {
        for (let i = 0; i < this.crud.data.length; i++) {
          if (res.id === this.crud.data[i].id) {
            this.crud.data[i] = res
            break
          }
        }
      })
    },
    // Get departmental data
    getDepts() {
      getDepts({ enabled: true }).then(res => {
        this.depts = res.content
      })
    },
    // Get departmental data if the data permission is custom
    changeScope() {
      if (this.form.dataScope === 'Customize') {
        this.getDepts()
      }
    },
    checkboxT(row, rowIndex) {
      return row.level >= this.level
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
  .role-span {
    font-weight: bold;
    color: #303133;
    font-size: 15px;
  }
  .branch {
    padding-left: 25%;
    margin-top: -6%;
  }
</style>

<style rel="stylesheet/scss" lang="scss" scoped>
  /deep/ .el-input-number .el-input__inner {
    text-align: left;
  }
</style>
