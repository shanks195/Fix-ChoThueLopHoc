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
        <el-table-column prop="email" label="Email" :show-overflow-tooltip="true" />
        <el-table-column prop="fullname" label="Name" :show-overflow-tooltip="true" />
        <el-table-column prop="phone" label="Phone" :show-overflow-tooltip="true" />
        <el-table-column prop="schedule" label="Schedule" :show-overflow-tooltip="true" />
        <el-table-column v-permission="['admin','contactc:edit','contactc:del']" label="Operating" width="150px" align="center">
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
import crudContactCL from '@/api/contactclient'
import CRUD, { presenter, header, form, crud } from '@crud/crud'
import rrOperation from '@crud/RR.operation'
import crudOperation from '@crud/CRUD.operations'
import udOperation from '@crud/UD.operation'
import pagination from '@crud/Pagination'
import { upload } from '@/utils/upload'
import { mapGetters } from 'vuex'
const defaultCrud = CRUD({
  title: '_contact',
  url: 'api/contactclient',
  sort: 'id,desc',
  crudMethod: { ...crudContactCL }
})
const defaultForm = {
  email:
        '',
  fullname:
        '',
  schedule:
        '',
  phone:
        '',
}
export default {
  name: 'ContactClient',
  components: { pagination, crudOperation, rrOperation, udOperation },
  mixins: [presenter(defaultCrud), header(), form(defaultForm), crud()],
  data() {
    return {
      permission: {
        edit: ['admin', 'contactc:edit'],
        del: ['admin', 'contactc:del']
      },
      rules: {
        email:
              [
                { required: true, message: 'E-mail can not be empty', trigger: 'blur' }
              ],
        fullname:
              [
                { required: true, message: 'Fullname can not be empty', trigger: 'blur' }
              ],
        schedule:
              [
                { required: true, message: 'Schedule can not be empty', trigger: 'blur' }
              ],
        phone:
              [
                { required: true, message: 'Phone can not be empty', trigger: 'blur' }
              ]
      },
      queryTypeOptions: [
        {
          key: 'email',
          display_name: 'Email'
        },
        {
          key: 'fullname',
          display_name: 'Full name'
        },
        {
          key: 'schedule',
          display_name: 'Schedule'
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
