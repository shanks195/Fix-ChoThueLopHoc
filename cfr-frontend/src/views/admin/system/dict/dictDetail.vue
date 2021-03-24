<template>
  <div>
    <div v-if="query.dictName === ''">
      <div class="my-code">Click on the dictionary to view details</div>
    </div>
    <div v-else>

      <div class="head-container">
        <div v-if="crud.props.searchToggle">
          <el-input v-model="query.label" clearable size="small" placeholder="Enter dictionary tag query" style="width: 200px;" class="filter-item" @keyup.enter.native="toQuery" />
          <rrOperation />
        </div>
      </div>
      <el-dialog v-el-drag-dialog append-to-body :close-on-click-modal="false" :before-close="crud.cancelCU" :visible="crud.status.cu > 0" :title="crud.status.title" width="500px">
        <el-form ref="form" :model="form" :rules="rules" size="small" label-width="80px">
          <el-form-item label="Tag" prop="label">
            <el-input v-model="form.label" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="Value" prop="value">
            <el-input v-model="form.value" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="Sort" prop="sort">
            <el-input-number v-model.number="form.sort" :min="0" :max="999" controls-position="right" style="width: 370px;" />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="text" @click="crud.cancelCU">Cancel</el-button>
          <el-button :loading="crud.status.cu === 2" type="primary" @click="crud.submitCU">Submit</el-button>
        </div>
      </el-dialog>

      <el-table ref="table" v-loading="crud.loading" :data="crud.data" highlight-current-row style="width: 100%;" @selection-change="crud.selectionChangeHandler">
        <el-table-column label="Dictionary">
          <template>
            {{ query.dictName }}
          </template>
        </el-table-column>
        <el-table-column prop="label" label="Tag" />
        <el-table-column prop="value" label="Value" />
        <el-table-column prop="sort" label="Sort" />
        <el-table-column v-permission="['admin','dict:edit','dict:del']" label="Operating" width="130px" align="center" fixed="right">
          <template slot-scope="scope">
            <udOperation
              :data="scope.row"
              :permission="permission"
            />
          </template>
        </el-table-column>
      </el-table>
      <pagination />
    </div>
  </div>
</template>

<script>
import crudDictDetail from '@/api/dictDetail'
import CRUD, { presenter, header, form } from '@crud/crud'
import pagination from '@crud/Pagination'
import rrOperation from '@crud/RR.operation'
import udOperation from '@crud/UD.operation'
import elDragDialog from '@/components/el-drag-dialog'
const defaultForm = { id: null, label: null, value: null, sort: 999 }

export default {
  components: { pagination, rrOperation, udOperation },
  directives: { elDragDialog },
  cruds() {
    return [
      CRUD({ title: '_Details', url: 'api/dictDetail', query: { dictName: '' }, sort: ['sort,asc', 'id,desc'],
        crudMethod: { ...crudDictDetail },
        optShow: { add: true,
          edit: true,
          del: true,
          reset: false
        },
        queryOnPresenterCreated: false
      })
    ]
  },
  mixins: [
    presenter(),
    header(),
    form(function() {
      return Object.assign({ dict: { id: this.dictId }}, defaultForm)
    })],
  data() {
    return {
      dictId: null,
      rules: {
        label: [
          { required: true, message: 'Please enter a dictionary tag', trigger: 'blur' }
        ],
        value: [
          { required: true, message: 'Please enter a dictionary value', trigger: 'blur' }
        ],
        sort: [
          { required: true, message: 'Please enter the serial number', trigger: 'blur', type: 'number' }
        ]
      },
      permission: {
        add: ['admin', 'dict:add'],
        edit: ['admin', 'dict:edit'],
        del: ['admin', 'dict:del']
      }
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  /deep/ .el-input-number .el-input__inner {
    text-align: left;
  }
</style>
