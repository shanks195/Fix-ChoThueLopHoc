<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!--Side department data-->
      <el-col :xs="9" :sm="6" :md="4" :lg="4" :xl="4">
        <div class="head-container">
          <el-input
            v-model="deptName"
            clearable
            size="small"
            placeholder="Enter department name to search"
            prefix-icon="el-icon-search"
            style="width: 100%;"
            class="filter-item"
            @input="getDeptDatas"
          />
        </div>
        <el-tree
          :data="deptDatas"
          :props="defaultProps"
          :expand-on-click-node="false"
          default-expand-all
          @node-click="handleNodeClick"
        />
      </el-col>
      <!--User data-->
      <el-col :xs="15" :sm="18" :md="20" :lg="20" :xl="20">
        <!--toolbar-->
        <div class="head-container">
          <div v-if="crud.props.searchToggle">
            <!-- search for -->
            <el-input
              v-model="query.blurry"
              clearable
              size="small"
              placeholder="Enter name or email search"
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
            <el-select
              v-model="query.enabled"
              clearable
              size="small"
              placeholder="Status"
              class="filter-item"
              style="width: 90px"
              @change="crud.toQuery"
            >
              <el-option
                v-for="item in enabledTypeOptions"
                :key="item.key"
                :label="item.display_name"
                :value="item.key"
              />
            </el-select>
            <rrOperation />
          </div>
          <crudOperation show="" :permission="permission" />
        </div>
        <!--Form rendering-->
        <el-dialog
          v-el-drag-dialog
          append-to-body
          :close-on-click-modal="false"
          :before-close="crud.cancelCU"
          :visible="crud.status.cu > 0"
          :title="crud.status.title"
          width="700px"
        >
          <el-form ref="form" :inline="true" :model="form" :rules="rules" size="small" label-width="100px">
            <el-form-item label="User Name" prop="username">
              <el-input v-model="form.username" />
            </el-form-item>
            <el-form-item label="Phone" prop="phone">
              <el-input v-model.number="form.phone" />
            </el-form-item>
            <el-form-item label="NickName" prop="nickName">
              <el-input v-model="form.nickName" />
            </el-form-item>
            <el-form-item label="Email" prop="email">
              <el-input v-model="form.email" />
            </el-form-item>
            <el-form-item label="Dept" prop="dept.id">
              <treeselect v-model="form.dept.id" :options="depts" style="width: 190px" placeholder="Select department" @select="selectFun" />
            </el-form-item>
            <el-form-item label="Position" prop="job.id">
              <el-select v-model="form.job.id" style="width: 188px" placeholder="Please select department first">
                <el-option
                  v-for="(item, index) in jobs"
                  :key="item.name + index"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="Gender">
              <el-radio-group v-model="form.sex" style="width: 178px">
                <el-radio label="male">Male</el-radio>
                <el-radio label="female">Female</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="Status">
              <el-radio-group v-model="form.enabled" :disabled="form.id === user.id">
                <el-radio v-for="item in dict.user_status" :key="item.id" :label="item.value">{{ item.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item style="margin-bottom: 0;" label="Roles" prop="roles">
              <el-select
                v-model="form.roles"
                style="width: 490px;"
                multiple
                placeholder="Please choose"
                @remove-tag="deleteTag"
                @change="changeRole"
              >
                <el-option
                  v-for="item in roles"
                  :key="item.name"
                  :disabled="level !== 1 && item.level <= level"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button type="text" @click="crud.cancelCU">Cancel</el-button>
            <el-button :loading="crud.status.cu === 2" type="primary" @click="crud.submitCU">Submit</el-button>
          </div>
        </el-dialog>
        <!--Table rendering-->
        <el-table
          ref="table"
          v-loading="crud.loading"
          :data="crud.data"
          style="width: 100%;"
          @selection-change="crud.selectionChangeHandler"
        >
          <el-table-column :selectable="checkboxT" type="selection" width="55" />
          <el-table-column :show-overflow-tooltip="true" prop="username" label="Username" />
          <el-table-column :show-overflow-tooltip="true" prop="nickName" label="Nickname" />
          <el-table-column prop="sex" label="Gender" />
          <el-table-column :show-overflow-tooltip="true" prop="phone" width="100" label="Phone" />
          <el-table-column :show-overflow-tooltip="true" width="125" prop="email" label="Email" />
          <el-table-column :show-overflow-tooltip="true" width="110" prop="dept" label="Department / Position">
            <template slot-scope="scope">
              <div>{{ scope.row.dept.name }} / {{ scope.row.job.name }}</div>
            </template>
          </el-table-column>
          <el-table-column label="Operating" align="center" prop="enabled">
            <template slot-scope="scope">
              <el-switch
                v-model="scope.row.enabled"
                :disabled="user.id === scope.row.id"
                active-color="#409EFF"
                inactive-color="#F56C6C"
                @change="changeEnabled(scope.row, scope.row.enabled)"
              />
            </template>
          </el-table-column>
          <el-table-column
            :show-overflow-tooltip="true"
            prop="createTime"
            width="140"
            label="Date"
          >
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column
            v-permission="['admin','user:edit','user:del']"
            label="Operating"
            width="125"
            align="center"
            fixed="right"
          >
            <template slot-scope="scope">
              <udOperation
                :data="scope.row"
                :permission="permission"
                :disabled-dle="scope.row.id === user.id"
              />
            </template>
          </el-table-column>
        </el-table>
        <!--Pagination-->
        <pagination />
      </el-col>
    </el-row>
  </div>
</template>

<script>
import crudUser from '@/api/user'
import { getDepts } from '@/api/dept'
import { getAll, getLevel } from '@/api/role'
import { getAllJob } from '@/api/job'
import CRUD, { presenter, header, form, crud } from '@crud/crud'
import rrOperation from '@crud/RR.operation'
import crudOperation from '@crud/CRUD.operation'
import udOperation from '@crud/UD.operation'
import pagination from '@crud/Pagination'
import Treeselect from '@riophae/vue-treeselect'
import { mapGetters } from 'vuex'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import elDragDialog from '@/components/el-drag-dialog'

let userRoles = []
const defaultForm = {
  id: null,
  username: null,
  nickName: null,
  sex: 'male',
  email: null,
  enabled: 'false',
  roles: [],
  job: { id: null },
  dept: { id: null },
  phone: null
}
export default {
  name: 'User',
  components: { Treeselect, crudOperation, rrOperation, udOperation, pagination },
  cruds() {
    return CRUD({ title: '_user', url: 'api/users', crudMethod: { ...crudUser }})
  },
  mixins: [presenter(), header(), form(defaultForm), crud()],
  // Data Dictionary
  dicts: ['user_status'],
  directives: { elDragDialog },
  data() {
    return {
      height: document.documentElement.clientHeight - 180 + 'px;',
      deptName: '', depts: [], deptDatas: [], jobs: [], level: 3, roles: [],
      defaultProps: { children: 'children', label: 'name' },
      permission: {
        add: ['admin', 'user:add'],
        edit: ['admin', 'user:edit'],
        del: ['admin', 'user:del']
      },
      enabledTypeOptions: [
        { key: 'true', display_name: 'activation' },
        { key: 'false', display_name: 'locking' }
      ],
      rules: {
        username: [
          { required: true, message: 'please enter user name', trigger: 'blur' },
          { min: 2, max: 30, message: '2 to 30 characters in length', trigger: 'blur' }
        ],
        nickName: [
          { required: true, message: 'Please enter user nickname', trigger: 'blur' },
          { min: 2, max: 30, message: '2 to 30 characters in length', trigger: 'blur' }
        ],
        email: [
          { required: true, message: 'Please input the email address', trigger: 'blur' },
          { type: 'email', message: 'Please input the correct email address', trigger: 'blur' }
        ],
        phone: [
          { required: true, trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters([
      'user'
    ])
  },
  created() {
    this.$nextTick(() => {
      this.getDeptDatas()
      this.crud.toQuery()
      this.crud.msg.add = 'Added successfully, default password：123456'
    })
  },
  mounted: function() {
    const that = this
    window.onresize = function temp() {
      that.height = document.documentElement.clientHeight - 180 + 'px;'
    }
  },
  methods: {
    changeRole(value) {
      userRoles = []
      value.forEach(function(data, index) {
        const role = { id: data }
        userRoles.push(role)
      })
    },
    [CRUD.HOOK.afterAddError](crud) {
      this.afterErrorMethod(crud)
    },
    [CRUD.HOOK.afterEditError](crud) {
      this.afterErrorMethod(crud)
    },
    afterErrorMethod(crud) {
      // select
      const initRoles = []
      userRoles.forEach(function(role, index) {
        initRoles.push(role.id)
      })
      crud.form.roles = initRoles
    },
    deleteTag(value) {
      userRoles.forEach(function(data, index) {
        if (data.id === value) {
          userRoles.splice(index, value)
        }
      })
    },
    // Operations before adding and editing
    [CRUD.HOOK.afterToCU](crud, form) {
      this.getDepts()
      this.getRoles()
      this.getRoleLevel()
      form.enabled = form.enabled.toString()
    },
    // What to do before opening the edit popup
    [CRUD.HOOK.beforeToEdit](crud, form) {
      this.getJobs(this.form.dept.id)
      userRoles = []
      const roles = []
      form.roles.forEach(function(role, index) {
        roles.push(role.id)
        // Initial editing role
        const rol = { id: role.id }
        userRoles.push(rol)
      })
      form.roles = roles
    },
    // Actions done before submission
    [CRUD.HOOK.afterValidateCU](crud) {
      if (!crud.form.dept.id) {
        this.$message({
          message: 'Department cannot be empty',
          type: 'warning'
        })
        return false
      } else if (!crud.form.job.id) {
        this.$message({
          message: 'Post cannot be empty',
          type: 'warning'
        })
        return false
      } else if (this.roles.length === 0) {
        this.$message({
          message: 'Role cannot be empty',
          type: 'warning'
        })
        return false
      }
      crud.form.roles = userRoles
      return true
    },
    // Get department data on the left
    getDeptDatas() {
      const sort = 'id,desc'
      const params = { sort: sort }
      if (this.deptName) {
        params['name'] = this.deptName
      }
      getDepts(params).then(res => {
        this.deptDatas = res.content
      })
    },
    // Get department data in the pop-up window
    getDepts() {
      getDepts({ enabled: true }).then(res => {
        this.depts = res.content
      })
    },
    // Switch department
    handleNodeClick(data) {
      if (data.pid === 0) {
        this.query.deptId = null
      } else {
        this.query.deptId = data.id
      }
      this.crud.toQuery()
    },
    // Change state
    changeEnabled(data, val) {
      this.$confirm('This action will "' + this.dict.label.user_status[val] + '" ' + data.username + ', Continue？', 'Notice', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        crudUser.edit(data).then(res => {
          this.crud.notify(this.dict.label.user_status[val] + 'Success', CRUD.NOTIFICATION_TYPE.SUCCESS)
        }).catch(() => {
          data.enabled = !data.enabled
        })
      }).catch(() => {
        data.enabled = !data.enabled
      })
    },
    // Get character data in the pop-up window
    getRoles() {
      getAll().then(res => {
        this.roles = res
      }).catch(() => {
      })
    },
    // Get job data in the pop-up window
    getJobs(id) {
      getAllJob(id).then(res => {
        this.jobs = res.content
      }).catch(() => {
      })
    },
    // Click on the department to search for the corresponding position
    selectFun(node, instanceId) {
      this.getJobs(node.id)
      this.form.job.id = null
    },
    // Get permission level
    getRoleLevel() {
      getLevel().then(res => {
        this.level = res.level
      }).catch(() => {
      })
    },
    checkboxT(row, rowIndex) {
      return row.id !== this.user.id
    }
  }
}
</script>

<style scoped>
</style>
