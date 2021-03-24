<template>
  <div class="app-container">
    <!--toolbar-->
    <div class="head-container">
      <div v-if="crud.props.searchToggle">
        <!-- search for -->
        <el-input
          v-model="query.value"
          clearable
          placeholder="Enter search content"
          style="width: 200px;"
          class="filter-item"
          @keyup.enter.native="crud.toQuery"
        />
        <el-select v-model="query.type" clearable placeholder="Types of" class="filter-item" style="width: 130px">
          <el-option v-for="item in queryTypeOptions" :key="item.key" :label="item.display_name" :value="item.key" />
        </el-select>
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
      <el-dialog
        :close-on-click-modal="false"
        :before-close="crud.cancelCU"
        :visible.sync="crud.status.cu > 0"
        :title="crud.status.title"
        width="800px"
      >
        <el-form ref="form" :model="form" :rules="rules" size="small" label-width="120px">
            <el-form-item label="Name" prop="name">
            <mavon-editor ref="md" v-model="form.name" :style="'height:10px;' + height" language="en" @imgAdd="imgAdd" />
          </el-form-item>
          <el-form-item label="Title" prop="title">
            <mavon-editor ref="md" v-model="form.title" :style="'height:10px;' + height" language="en" @imgAdd="imgAdd" />
          </el-form-item>
          <el-form-item label="Description" prop="description">
            <mavon-editor ref="md" v-model="form.description" :style="'height:10px;' + height" language="en" @imgAdd="imgAdd" />
          </el-form-item>
            <el-form-item label="Link" prop="link">
            <el-input v-model="form.link" style="width: 640px;" placeholder="Please input link image" > 
            </el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="text" @click="crud.cancelCU">Cancel</el-button>
          <el-button :loading="crud.cu === 2" type="primary" @click="crud.submitCU">Submit</el-button>
        </div>
      </el-dialog>
      <!--loading-->
      <el-table
        ref="table"
        v-loading="crud.loading"
        :data="crud.data"
        size="small"
        style="width: 100%;"
        @selection-change="crud.selectionChangeHandler"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="title" label="Title" :show-overflow-tooltip="true" />
        <el-table-column prop="name" label="Name" :show-overflow-tooltip="true" />
        <el-table-column prop="description" label="Description" :show-overflow-tooltip="true" />
        <el-table-column prop="link" label="Link" :show-overflow-tooltip="true" />
        <el-table-column prop="createTime" label="Time">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
        <el-table-column v-permission="['admin','home:edit','home:del']" label="Operating" width="150px" align="center">
          <template slot-scope="scope">
            <udOperation :data="scope.row" :permission="permission" />
          </template>
        </el-table-column>
      </el-table>
      <!--pagination-->
      <pagination />
    </div>
  </div>
</template>

<script>
import crudHome from '@/api/home'
import CRUD, { presenter, header, form, crud } from '@crud/crud'
import rrOperation from '@crud/RR.operation'
import crudOperation from '@crud/CRUD.operation'
import udOperation from '@crud/UD.operation'
import pagination from '@crud/Pagination'
import { upload } from '@/utils/upload'
import { mapGetters } from 'vuex'
const defaultCrud = CRUD({
  title: '_Home',
  url: 'api/home',
  sort: 'id,desc',
  crudMethod: { ...crudHome }
})
const defaultForm = {
  title:
        '',
  name:
        '',
  link:
        '',
  description:
        ''
}
export default {
  name: 'Home',
  components: { pagination, crudOperation, rrOperation, udOperation },
  mixins: [presenter(defaultCrud), header(), form(defaultForm), crud()],
  data() {
    return {
      permission: {
        add: ['admin', 'home:add'],
        edit: ['admin', 'home:edit'],
        del: ['admin', 'home:del']
      },
      rules: {
        title:
              [
                { required: true, message: 'Title cannot be empty', trigger: 'blur' }
              ],
        name:
              [
                { required: true, message: 'Name can not be empty', trigger: 'blur' }
              ],
        description:
              [
                { required: true, message: 'Description can not be empty', trigger: 'blur' }
              ],
        link:
              [
                { required: true, message: 'Link can not be empty', trigger: 'blur' }
              ]
      },
      queryTypeOptions: [
        {
          key: 'title',
          display_name: 'Title'
        },
        {
          key: 'name',
          display_name: 'Name'
        },
        {
          key: 'description',
          display_name: 'Description'
        },
                {
          key: 'link',
          display_name: 'Link'
        }
      ],
      height: document.documentElement.clientHeight - 10 + 'px'
    }
  },
  computed: {
    ...mapGetters([
      'imagesUploadApi'
    ])
  },
  mounted() {
    const that = this
    window.onresize = function temp() {
      that.height = document.documentElement.clientHeight - 10 + 'px'
    }
  },
  methods: {
    [CRUD.HOOK.beforeRefresh]() {
      const query = this.query
      if (query.type && query.value) {
        this.crud.params[query.type] = query.value
      }
      return true
    },
    imgAdd(pos, $file) {
      upload(this.imagesUploadApi, $file).then(data => {
        this.$refs.md.$img2Url(pos, data.data.url)
      })
    }
  }
}
</script>
<style scoped>
</style>
