<template>
  <div class="app-container">
    <div class="head-container">
      <div v-if="crud.props.searchToggle">
        <el-input
          v-model="query.filter"
          clearable
          size="small"
          placeholder="Full table search"
          style="width: 200px;"
          class="filter-item"
          @keyup.enter.native="crud.toQuery"
        />
        <rrOperation />
      </div>
      <crudOperation>
        <el-button
          slot="left"
          class="filter-item"
          type="danger"
          icon="el-icon-delete"
          size="mini"
          :loading="delLoading"
          :disabled="crud.selections.length === 0"
          @click="doDelete(crud.selections)"
        >
          Delete
        </el-button>
      </crudOperation>
    </div>
    <!--Table rendering-->
    <el-table
      ref="table"
      v-loading="crud.loading"
      :data="crud.data"
      style="width: 100%;"
      @selection-change="crud.selectionChangeHandler"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="userName" label="Name" :show-overflow-tooltip="true" />
      <el-table-column prop="nickName" label="Nick" :show-overflow-tooltip="true" />
      <el-table-column prop="job" label="Job" :show-overflow-tooltip="true" />
      <el-table-column prop="ip" label="IP" :show-overflow-tooltip="true" />
      <el-table-column :show-overflow-tooltip="true" prop="address" label="Address"  />
      <el-table-column prop="browser" label="Brower" :show-overflow-tooltip="true" />
      <el-table-column prop="loginTime" label="Time">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.loginTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Operating" width="100px" fixed="right">
        <template slot-scope="scope">
          <el-popover
            :ref="scope.$index"
            v-permission="['admin']"
            placement="top"
            width="180"
          >
            <p>Are you sure to force this user out?</p>
            <div style="text-align: right; margin: 0">
              <el-button size="mini" type="text" @click="$refs[scope.$index].doClose()">取消</el-button>
              <el-button
                :loading="delLoading"
                type="primary"
                size="mini"
                @click="delMethod(scope.row.key, scope.$index)"
              >delete
              </el-button>
            </div>
            <el-button slot="reference" size="mini" type="text">Refresh</el-button>
          </el-popover>
        </template>
      </el-table-column>
    </el-table>
    <pagination />
  </div>
</template>

<script>
import { del } from '@/api/online'
import CRUD, { presenter, header, crud } from '@crud/crud'
import rrOperation from '@crud/RR.operation'
import crudOperation from '@crud/CRUD.operation'
import pagination from '@crud/Pagination'
export default {
  name: 'OnlineUser',
  components: { pagination, crudOperation, rrOperation },
  cruds() {
    return CRUD({ url: 'auth/online', title: 'Online user' })
  },
  mixins: [presenter(), header(), crud()],
  data() {
    return {
      delLoading: false,
      permission: {}
    }
  },
  created() {
    this.crud.msg.del = 'Successful Delete!'
    this.crud.optShow = {
      add: false,
      edit: false,
      del: false,
      download: true
    }
  },
  methods: {
    // Set the interface address before getting data
    doDelete(datas) {
      this.$confirm(`Confirm forced exit selected${datas.length}Users?`, 'Notice', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        this.delMethod(datas)
      }).catch(() => {})
    },
    // Kick out users
    delMethod(key, index) {
      const ids = []
      if (key instanceof Array) {
        key.forEach(val => {
          ids.push(val.key)
        })
      } else ids.push(key)
      this.delLoading = true
      del(ids).then(() => {
        this.delLoading = false
        if (this.$refs[index]) {
          this.$refs[index].doClose()
        }
        this.crud.dleChangePage(1)
        this.crud.delSuccessNotify()
        this.crud.toQuery()
      }).catch(() => {
        this.delLoading = false
        if (this.$refs[index]) {
          this.$refs[index].doClose()
        }
      })
    }
  }
}
</script>
