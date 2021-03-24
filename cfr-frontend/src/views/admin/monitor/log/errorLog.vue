<template>
  <div class="app-container">
    <div class="head-container">
      <Search />
      <crudOperation>
        <el-button
          slot="left"
          class="filter-item"
          type="danger"
          icon="el-icon-delete"
          size="mini"
          :loading="crud.delAllLoading"
          @click="confirmDelAll()"
        >
          Empty
        </el-button>
      </crudOperation>
    </div>
    <!--Form rendering-->
    <el-table
      ref="table"
      v-loading="crud.loading"
      :data="crud.data"
      style="width: 100%;"
      @selection-change="crud.selectionChangeHandler"
    >
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left" inline class="demo-table-expand">
            <el-form-item label="Request method">
              <span>{{ props.row.method }}</span>
            </el-form-item>
            <el-form-item label="request parameters">
              <span>{{ props.row.params }}</span>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column prop="username" label="Username" />
      <el-table-column prop="requestIp" label="IP request" />
      <el-table-column :show-overflow-tooltip="true" prop="address" label="IP" />
      <el-table-column prop="description" label="Description" />
      <el-table-column prop="browser" label="Browser" />
      <el-table-column prop="createTime" label="Date">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Details" width="100px">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="info(scope.row.id)">View details</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog :visible.sync="dialog" title="Details" append-to-body top="30px" width="85%">
      <pre v-highlightjs="errorInfo"><code class="java" /></pre>
    </el-dialog>
    <!--Paging component-->
    <pagination />
  </div>
</template>

<script>
import { getErrDetail, delAllError } from '@/api/log'
import Search from './search'
import CRUD, { presenter } from '@crud/crud'
import crudOperation from '@crud/CRUD.operation'
import pagination from '@crud/Pagination'

export default {
  name: 'ErrorLog',
  components: { Search, crudOperation, pagination },
  cruds() {
    return CRUD({ title: 'Exception log', url: 'api/logs/error' })
  },
  mixins: [presenter()],
  data() {
    return {
      errorInfo: '', dialog: false
    }
  },
  created() {
    this.crud.optShow = {
      add: false,
      edit: false,
      del: false,
      download: true
    }
  },
  methods: {
    // Get exception details
    info(id) {
      this.dialog = true
      getErrDetail(id).then(res => {
        this.errorInfo = res.exception
        console.log(this.errorInfo)
      })
    },
    confirmDelAll() {
      this.$confirm(`Are you sure to clear all exception logs?`, 'Notice', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        this.crud.delAllLoading = true
        delAllError().then(res => {
          this.crud.delAllLoading = false
          this.crud.dleChangePage(1)
          this.crud.delSuccessNotify()
          this.crud.toQuery()
        }).catch(err => {
          this.crud.delAllLoading = false
          console.log(err.response.data.message)
        })
      }).catch(() => {
      })
    }
  }
}
</script>

<style scoped>

  .demo-table-expand {
    font-size: 0;
  }

  .demo-table-expand label {
    width: 70px;
    color: #99a9bf;
  }

  .demo-table-expand .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
    width: 100%;
  }

  .demo-table-expand .el-form-item__content {
    font-size: 12px;
  }

   /deep/ .el-dialog__body {
    padding: 0 20px 10px 20px !important;
  }

  .java.hljs {
    color: #444;
    background: #ffffff !important;
    height: 630px !important;
  }
</style>
