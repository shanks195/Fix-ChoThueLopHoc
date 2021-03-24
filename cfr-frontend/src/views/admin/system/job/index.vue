<template>
  <div class="app-container">
    <div class="head-container">
      <eHeader :dict="dict" :permission="permission" />
      <crudOperation :permission="permission" />
    </div>
    <el-table
      ref="table"
      v-loading="crud.loading"
      :data="crud.data"
      style="width: 100%;"
      @selection-change="crud.selectionChangeHandler"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="name" label="Name" />
      <el-table-column prop="dept" label="Dept">
        <template slot-scope="scope">
          <div>{{ scope.row.deptSuperiorName ? scope.row.deptSuperiorName + ' / ' : '' }}{{ scope.row.dept.name }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="sort" label="Sort">
        <template slot-scope="scope">{{ scope.row.sort }}</template>
      </el-table-column>
      <el-table-column prop="status" label="Status" align="center">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.enabled"
            active-color="#409EFF"
            inactive-color="#F56C6C"
            @change="changeEnabled(scope.row, scope.row.enabled)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="Date">
        <template slot-scope="scope"><span>{{ parseTime(scope.row.createTime) }}</span></template>
      </el-table-column>
      <!--Edit and delete-->
      <el-table-column
        v-permission="['admin','job:edit','job:del']"
        label="Operating"
        width="130px"
        align="center"
        fixed="right"
      >
        <template slot-scope="scope">
          <udOperation :data="scope.row" :permission="permission" />
        </template>
      </el-table-column>
    </el-table>
    <pagination />
    <eForm :job-status="dict.job_status" />
  </div>
</template>

<script>
import crudJob from '@/api/job'
import eHeader from './header'
import eForm from './form'
import CRUD, { presenter } from '@crud/crud'
import crudOperation from '@crud/CRUD.operation'
import pagination from '@crud/Pagination'
import udOperation from '@crud/UD.operation'
export default {
  name: 'Job',
  components: { eHeader, eForm, crudOperation, pagination, udOperation },
  cruds() {
    return CRUD({
      title: 'Post',
      url: 'api/job',
      sort: ['sort,asc', 'id,desc'],
      crudMethod: { ...crudJob }
    })
  },
  mixins: [presenter()],
  dicts: ['job_status'],
  data() {
    return {
      permission: {
        add: ['admin', 'job:add'],
        edit: ['admin', 'job:edit'],
        del: ['admin', 'job:del']
      }
    }
  },
  methods: {
    // Change state
    changeEnabled(data, val) {
      this.$confirm('This action will "' + this.dict.label.job_status[val] + '" ' + data.name + 'Post, do you continue?', 'Notice', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        // eslint-disable-next-line no-undef
        crud.crudMethod.edit(data).then(() => {
          // eslint-disable-next-line no-undef
          crud.notify(this.dict.label.job_status[val] + 'Success', 'success')
        }).catch(err => {
          data.enabled = !data.enabled
          console.log(err.data.message)
        })
      }).catch(() => {
        data.enabled = !data.enabled
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
