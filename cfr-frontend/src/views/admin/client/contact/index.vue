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
          <el-form-item label="Title" prop="title">
            <mavon-editor ref="md" v-model="form.title" required :style="'height:10px;' + height" language="en" @imgAdd="imgAdd" />
            <!-- <el-input v-model="form.title" style="width: 370px;" /> -->
          </el-form-item>
          <el-form-item label="Description" prop="description">
            <mavon-editor ref="md" v-model="form.description" required :style="'height:10px;' + height" language="en" @imgAdd="imgAdd" />
            <!-- <el-input v-model="form.description" style="width: 370px;" /> -->
          </el-form-item>
          <el-form-item label="Phone" prop="phone">
            <el-input v-model="form.phone" required style="width: 640px;" />
          </el-form-item>
          <el-form-item label="Email" prop="email">
            <el-input v-model="form.email" required email style="width: 640px;" />
          </el-form-item>
          <el-form-item label="Fullname" prop="fullname">
            <el-input v-model="form.fullname" required style="width: 640px;" />
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
        <el-table-column type="selection" width="60" />
        <el-table-column prop="title" label="Title" :show-overflow-tooltip="true" />
        <el-table-column prop="description" label="Description" :show-overflow-tooltip="true" />
        <el-table-column prop="email" label="Email" :show-overflow-tooltip="true" />
        <el-table-column prop="fullname" label="Name" :show-overflow-tooltip="true" />
        <el-table-column prop="phone" label="Phone" :show-overflow-tooltip="true" />
        <el-table-column v-permission="['admin','contact:edit','contact:del']" label="Operating" width="150px" align="center">
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
import crudContact from '@/api/contact'
import CRUD, { presenter, header, form, crud } from '@crud/crud'
import rrOperation from '@crud/RR.operation'
import crudOperation from '@crud/CRUD.operation'
import udOperation from '@crud/UD.operation'
import pagination from '@crud/Pagination'
import { upload } from '@/utils/upload'
import { mapGetters } from 'vuex'
const defaultCrud = CRUD({
  title: '_contact',
  url: 'api/contact',
  sort: 'id,desc',
  crudMethod: { ...crudContact }
})
const defaultForm = {
  title:
        '',
  email:
        '',
  fullname:
        '',
  description:
        '',
  phone:
        '',
  bookingtime:
        ''
}
export default {
  name: 'Contact',
  components: { pagination, crudOperation, rrOperation, udOperation },
  mixins: [presenter(defaultCrud), header(), form(defaultForm), crud()],
  data() {
    return {
      permission: {
        add: ['admin', 'contact:add'],
        edit: ['admin', 'contact:edit'],
        del: ['admin', 'contact:del']
      },
      rules: {
        title:
              [
                { required: true, message: 'Title cannot be empty', trigger: 'blur' }
              ],
        email:
              [
                { required: true, message: 'E-mail can not be empty', trigger: 'blur' }
              ],
        fullname:
              [
                { required: true, message: 'Fullname can not be empty', trigger: 'blur' }
              ],
        description:
              [
                { required: true, message: 'Description can not be empty', trigger: 'blur' }
              ],
        phone:
              [
                { required: true, message: 'Phone can not be empty', trigger: 'blur' }
              ]
      },
      queryTypeOptions: [
        {
          key: 'title',
          display_name: 'title'
        },
        {
          key: 'email',
          display_name: 'Email'
        },
        {
          key: 'fullname',
          display_name: 'Full name'
        },
        {
          key: 'description',
          display_name: 'Description'
        },
        {
          key: 'phone',
          display_name: 'Phone'
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
