<template>
  <div class="app-container">
    <!--toolbar-->
    <div class="head-container">
      <div v-if="crud.props.searchToggle">
        <!-- Search for -->
        <el-input v-model="query.jobName" clearable size="small" placeholder="Enter task name to search" style="width: 200px;" class="filter-item" @keyup.enter.native="toQuery" />
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
        <!-- Task log -->
        <el-button
          slot="right"
          class="filter-item"
          size="mini"
          type="info"
          icon="el-icon-tickets"
          @click="doLog"
        >Log</el-button>
      </crudOperation>
      <Log ref="log" />
    </div>
    <!--Form-->
    <el-dialog v-el-drag-dialog :close-on-click-modal="false" :before-close="crud.cancelCU" :visible.sync="crud.status.cu > 0" :title="crud.status.title" append-to-body width="600px">
      <el-form ref="form" :model="form" :rules="rules" size="small" label-width="100px">
        <el-form-item label="Name" prop="jobName">
          <el-input v-model="form.jobName" style="width: 460px;" />
        </el-form-item>
        <el-form-item label="Bean" prop="beanName">
          <el-input v-model="form.beanName" style="width: 460px;" />
        </el-form-item>
        <el-form-item label="Method" prop="methodName">
          <el-input v-model="form.methodName" style="width: 460px;" />
        </el-form-item>
        <el-form-item label="Parameter">
          <el-input v-model="form.params" style="width: 460px;" />
        </el-form-item>
        <el-form-item label="Cron" prop="cronExpression">
          <el-input v-model="form.cronExpression" style="width: 460px;" />
        </el-form-item>
        <el-form-item label="Status">
          <el-radio v-model="form.isPause" :label="false">Enable</el-radio>
          <el-radio v-model="form.isPause" :label="true">Time out</el-radio>
        </el-form-item>
        <el-form-item label="Mission details">
          <el-input v-model="form.remark" style="width: 460px;" rows="2" type="textarea" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="text" @click="crud.cancelCU">Cancel</el-button>
        <el-button :loading="crud.status.cu === 2" type="primary" @click="crud.submitCU">Submit</el-button>
      </div>
    </el-dialog>
    <!---->
    <el-table ref="table" v-loading="crud.loading" :data="crud.data" style="width: 100%;" @selection-change="crud.selectionChangeHandler">
      <el-table-column :selectable="checkboxT" type="selection" width="55" />
      <el-table-column :show-overflow-tooltip="true" prop="jobName" width="100px" label="Name" />
      <el-table-column :show-overflow-tooltip="true" prop="beanName" label="Bean" />
      <el-table-column :show-overflow-tooltip="true" prop="methodName" width="90px" label="Method" />
      <el-table-column :show-overflow-tooltip="true" prop="params" width="80px" label="Params" />
      <el-table-column :show-overflow-tooltip="true" prop="cronExpression" width="100px" label="cron" />
      <el-table-column :show-overflow-tooltip="true" prop="isPause" width="90px" label="Status">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isPause ? 'warning' : 'success'">{{ scope.row.isPause ? 'paused ':' running' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :show-overflow-tooltip="true" prop="remark" label="Remark" />
      <el-table-column :show-overflow-tooltip="true" prop="createTime" label="Time">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column v-permission="['admin','timing:edit','timing:del']" label="Operating" width="180px" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button v-permission="['admin','timing:edit']" size="mini" style="margin-right: 3px;" type="text" @click="crud.toEdit(scope.row)">Edit</el-button>
          <el-button v-permission="['admin','timing:edit']" style="margin-left: -2px" type="text" size="mini" @click="execute(scope.row.id)">Execute</el-button>
          <el-button v-permission="['admin','timing:edit']" style="margin-left: 3px" type="text" size="mini" @click="updateStatus(scope.row.id,scope.row.isPause ? 'resume':'pause')">
            {{ scope.row.isPause ? 'Resume':'Pause' }}
          </el-button>
          <el-popover
            :ref="scope.row.id"
            v-permission="['admin','timing:del']"
            placement="top"
            width="200"
          >
            <p>Are you sure to stop and delete this task?</p>
            <div style="text-align: right; margin: 0">
              <el-button size="mini" type="text" @click="$refs[scope.row.id].doClose()">Cancel</el-button>
              <el-button :loading="delLoading" type="primary" size="mini" @click="delMethod(scope.row.id)">Confirm</el-button>
            </div>
            <el-button slot="reference" type="text" size="mini">Delete</el-button>
          </el-popover>
        </template>
      </el-table-column>
    </el-table>
    <!--pagination-->
    <pagination />
  </div>
</template>

<script>
import crudJob from '@/api/timing'
import Log from './log'
import CRUD, { presenter, header, form, crud } from '@crud/crud'
import rrOperation from '@crud/RR.operation'
import crudOperation from '@crud/CRUD.operation'
import pagination from '@crud/Pagination'
import elDragDialog from '@/components/el-drag-dialog'
const defaultForm = { id: null, jobName: null, beanName: null, methodName: null, params: null, cronExpression: null, isPause: false, remark: null }
export default {
  name: 'Timing',
  components: { Log, pagination, crudOperation, rrOperation },
  directives: { elDragDialog },
  cruds() {
    return CRUD({ title: 'Timed task', url: 'api/jobs', crudMethod: { ...crudJob }})
  },
  mixins: [presenter(), header(), form(defaultForm), crud()],
  data() {
    return {
      delLoading: false,
      permission: {
        add: ['admin', 'timing:add'],
        edit: ['admin', 'timing:edit'],
        del: ['admin', 'timing:del']
      },
      rules: {
        jobName: [
          { required: true, message: 'Please enter the task name', trigger: 'blur' }
        ],
        beanName: [
          { required: true, message: 'Please enter the Bean name', trigger: 'blur' }
        ],
        methodName: [
          { required: true, message: 'Please enter the method name', trigger: 'blur' }
        ],
        cronExpression: [
          { required: true, message: 'Please enter cron expression', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    execute(id) {
      crudJob.execution(id).then(res => {
        this.crud.notify('Please enter cron expression', CRUD.NOTIFICATION_TYPE.SUCCESS)
      }).catch(err => {
        console.log(err.response.data.message)
      })
    },
    updateStatus(id, status) {
      crudJob.updateIsPause(id).then(res => {
        this.crud.toQuery()
        this.crud.notify(status + 'Success', CRUD.NOTIFICATION_TYPE.SUCCESS)
      }).catch(err => {
        console.log(err.response.data.message)
      })
    },
    delMethod(id) {
      this.delLoading = true
      crudJob.del([id]).then(() => {
        this.delLoading = false
        this.$refs[id].doClose()
        this.crud.dleChangePage(1)
        this.crud.delSuccessNotify()
        this.crud.toQuery()
      }).catch(() => {
        this.delLoading = false
        this.$refs[id].doClose()
      })
    },
    doLog() {
      this.$refs.log.dialog = true
      this.$refs.log.doInit()
    },
    checkboxT(row, rowIndex) {
      return row.id !== 1
    }
  }
}
</script>
