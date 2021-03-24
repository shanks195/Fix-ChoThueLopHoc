<template>
  <div class="app-container">
    <el-dialog
      v-el-drag-dialog
      append-to-body
      :close-on-click-modal="false"
      :before-close="crud.cancelCU"
      :visible="crud.status.cu > 0"
      :title="crud.status.title"
      width="500px"
    >
      <el-form ref="form" :model="form" :rules="rules" size="small" label-width="80px">
        <el-form-item label="Name" prop="name">
          <el-input v-model="form.name" style="width: 370px;" />
        </el-form-item>
        <el-form-item label="Remark">
          <el-input v-model="form.remark" style="width: 370px;" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="text" @click="crud.cancelCU">Cancel</el-button>
        <el-button :loading="crud.status.cu === 2" type="primary" @click="crud.submitCU">Submit</el-button>
      </div>
    </el-dialog>
    <!-- list -->
    <el-row :gutter="10">
      <el-col :xs="24" :sm="24" :md="10" :lg="11" :xl="11" style="margin-bottom: 10px">
        <el-card class="box-card">
          <!--toolbar-->
          <div class="head-container">
            <div v-if="crud.props.searchToggle">
              <!-- search for -->
              <el-input
                v-model="query.blurry"
                clearable
                size="small"
                placeholder="Enter name or description to search"
                style="width: 200px;"
                class="filter-item"
                @keyup.enter.native="crud.toQuery"
              />
              <rrOperation />
            </div>
            <crudOperation :permission="permission" />
          </div>
          <el-table
            ref="table"
            v-loading="crud.loading"
            :data="crud.data"
            highlight-current-row
            style="width: 100%;"
            @selection-change="crud.selectionChangeHandler"
            @current-change="handleCurrentChange"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column :show-overflow-tooltip="true" prop="name" label="Name" />
            <el-table-column :show-overflow-tooltip="true" prop="remark" label="Remark" />
            <el-table-column
              v-permission="['admin','dict:edit','dict:del']"
              label="Operating"
              width="130px"
              align="center"
              fixed="right"
            >
              <template slot-scope="scope">
                <udOperation
                  :data="scope.row"
                  :permission="permission"
                />
              </template>
            </el-table-column>
          </el-table>
          <pagination />
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="14" :lg="13" :xl="13">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>Dictionary details</span>
            <el-button
              v-if="checkPermission(['admin','dict:add']) && this.$refs.dictDetail && this.$refs.dictDetail.query.dictName"
              class="filter-item"
              size="mini"
              style="float: right;padding: 4px 10px"
              type="primary"
              icon="el-icon-plus"
              @click="$refs.dictDetail && $refs.dictDetail.crud.toAdd()"
            >Add
            </el-button>
          </div>
          <dictDetail ref="dictDetail" :permission="permission" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import checkPermission from '@/utils/permission'
import dictDetail from './dictDetail'
import crudDict from '@/api/dict'
import CRUD, { presenter, header, form } from '@crud/crud'
import crudOperation from '@crud/CRUD.operation'
import pagination from '@crud/Pagination'
import rrOperation from '@crud/RR.operation'
import udOperation from '@crud/UD.operation'
import elDragDialog from '@/components/el-drag-dialog'
const defaultForm = { id: null, name: null, remark: null }

export default {
  name: 'Dict',
  components: { crudOperation, pagination, rrOperation, udOperation, dictDetail },
  directives: { elDragDialog },
  cruds() {
    return [
      CRUD({ title: '_Dict', url: 'api/dict', crudMethod: { ...crudDict }})
    ]
  },
  mixins: [presenter(), header(), form(defaultForm)],
  data() {
    return {
      queryTypeOptions: [
        { key: 'name', display_name: 'Name' },
        { key: 'remark', display_name: 'Remark' }
      ],
      rules: {
        name: [
          { required: true, message: 'Please enter a name', trigger: 'blur' }
        ]
      },
      permission: {
        add: ['admin', 'dict:add'],
        edit: ['admin', 'dict:edit'],
        del: ['admin', 'dict:del']
      }
    }
  },
  methods: {
    checkPermission,
    // Set the interface address before getting data
    [CRUD.HOOK.beforeRefresh]() {
      if (this.$refs.dictDetail) {
        this.$refs.dictDetail.query.dictName = ''
      }
      return true
    },
    // After selecting the dictionary, set the dictionary details data
    handleCurrentChange(val) {
      if (val) {
        this.$refs.dictDetail.query.dictName = val.name
        this.$refs.dictDetail.dictId = val.id
        this.$refs.dictDetail.crud.toQuery()
      }
    }
  }
}
</script>

<style scoped>

</style>
