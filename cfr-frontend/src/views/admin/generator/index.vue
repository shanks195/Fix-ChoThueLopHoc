<template>
  <div class="app-container">
    <div class="head-container">
      <div v-if="crud.props.searchToggle">
        <el-input
          v-model="query.name"
          class="filter-item"
          clearable="clearable"
          size="small"
          placeholder="Please enter the table name"
          style="width: 200px;"
          @keyup.enter.native="crud.toQuery"
        />
        <rrOperation />
      </div>
      <crudOperation>
        <el-tooltip slot="right" class="item" effect="dark" content="Use this function when the table fields in the database change" placement="top-start">
          <el-button
            class="filter-item"
            size="mini"
            type="success"
            icon="el-icon-refresh"
            :loading="syncLoading"
            :disabled="crud.selections.length === 0"
            @click="sync"
          >Sync
          </el-button>
        </el-tooltip>
      </crudOperation>
    </div>
    <!-- Table rendering-->
    <el-table
      ref="table"
      v-loading="crud.loading"
      :data="crud.data"
      style="width: 100%;"
      @selection-change="crud.selectionChangeHandler"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column :show-overflow-tooltip="true" prop="tableName" label="Name" />
      <el-table-column :show-overflow-tooltip="true" prop="engine" label="Database" />
      <el-table-column :show-overflow-tooltip="true" prop="coding" label="Encoding" />
      <el-table-column :show-overflow-tooltip="true" prop="remark" label="Remarks" />
      <el-table-column prop="createTime" label="Date">
        <template slot-scope="scope"><span>{{ parseTime(scope.row.createTime) }}</span></template>
      </el-table-column>
      <el-table-column label="Operation" width="160px" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" style="margin-right: 2px" type="text">
            <router-link :to="'/sys-tools/generator/preview/' + scope.row.tableName">Preview</router-link>
          </el-button>
          <el-button
            size="mini"
            style="margin-left: -1px;margin-right: 2px"
            type="text"
            @click="toDownload(scope.row.tableName)"
          >Download
          </el-button>
          <el-button size="mini" style="margin-left: -1px;margin-right: 2px" type="text">
            <router-link :to="'/sys-tools/generator/config/' + scope.row.tableName">Edit</router-link>
          </el-button>
          <el-button type="text" style="margin-left: -1px" size="mini" @click="toGen(scope.row.tableName)">Generate
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!--Paging component-->
    <pagination />
  </div>
</template>

<script>
import { generator, sync } from '@/api/generator'
import { downloadFile } from '@/utils/index'
import CRUD, { presenter, header } from '@crud/crud'
import rrOperation from '@crud/RR.operation'
import crudOperation from '@crud/CRUD.operation'
import pagination from '@crud/Pagination'

export default {
  name: 'GeneratorIndex',
  components: { pagination, crudOperation, rrOperation },
  cruds() {
    return CRUD({ url: 'api/generator/tables' })
  },
  mixins: [presenter(), header()],
  data() {
    return {
      syncLoading: false
    }
  },
  created() {
    this.crud.optShow = { add: false, edit: false, del: false, download: false }
  },
  methods: {
    toGen(tableName) {
      // Generate code
      generator(tableName, 0).then(data => {
        this.$notify({
          title: 'Generated successfully',
          type: 'success',
          duration: 2500
        })
      })
    },
    toDownload(tableName) {
      // Download package
      generator(tableName, 2).then(data => {
        downloadFile(data, tableName, 'zip')
      })
    },
    sync() {
      const tables = []
      this.crud.selections.forEach(val => {
        tables.push(val.tableName)
      })
      this.syncLoading = true
      sync(tables).then(() => {
        this.crud.refresh()
        this.crud.notify('Sync successfully', CRUD.NOTIFICATION_TYPE.SUCCESS)
        this.syncLoading = false
      }).then(() => {
        this.syncLoading = false
      })
    }
  }
}
</script>

<style scoped>

</style>
