<template>
  <div class="app-container">
    <div class="head-container">
      <div v-if="crud.props.searchToggle">
        <el-input
          v-model="query.key"
          clearable
          size="small"
          placeholder="Enter key to search"
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
      <el-table-column type="selection" width="55" />
      <el-table-column :show-overflow-tooltip="true" prop="key" label="Key" />
      <el-table-column prop="value" label="Value" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <div style="word-break:keep-all;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">
            {{ scope.row.value }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="Operation" width="100px" fixed="right">
        <template slot-scope="scope">
          <el-popover
            :ref="scope.$index"
            v-permission="['admin','redis:del']"
            placement="top"
            width="180"
          >
            <p>Are you sure you want to delete this piece of data? </p>
            <div style="text-align: right; margin: 0">
              <el-button size="mini" type="text" @click="$refs[scope.$index].doClose()">Cancel</el-button>
              <el-button
                :loading="delLoading"
                type="primary"
                size="mini"
                @click="delMethod(scope.row.key, scope.$index)"
              >OK
              </el-button>
            </div>
            <el-button slot="reference" size="mini" type="text">Clear</el-button>
          </el-popover>
        </template>
      </el-table-column>
    </el-table>
    <pagination />
  </div>
</template>

<script>
import { del } from '@/api/redis'
import CRUD, { presenter, header, crud } from '@crud/crud'
import rrOperation from '@crud/RR.operation'
import crudOperation from '@crud/CRUD.operation'
import pagination from '@crud/Pagination'
// crud is held by presenter
const defaultCrud = CRUD({ url: 'api/redis', title: 'System cache' })
export default {
  name: 'Redis',
  components: { pagination, crudOperation, rrOperation },
  mixins: [presenter(defaultCrud), header(), crud()],
  data() {
    return {
      delLoading: false,
      permission: {
        del: ['admin', 'redis:del']
      }
    }
  },
  created() {
    this.crud.msg.del = 'Empty successfully!'
    this.crud.optShow = {
      add: false,
      edit: false,
      del: false,
      download: true
    }
  },
  methods: {
    doDelete(datas) {
      this.$confirm(`Confirm to clear selected${datas.length}Cache?`, 'Notice', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        this.delMethod(datas)
      }).catch(() => {})
    },
    // clear cache
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
