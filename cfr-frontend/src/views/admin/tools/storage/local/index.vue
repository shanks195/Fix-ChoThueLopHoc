<template>
  <div class="app-container" style="padding: 8px;">
    <!--toolbar-->
    <div class="head-container">
      <div v-if="crud.props.searchToggle">
        <!-- search for -->
        <el-input v-model="query.blurry" clearable size="small" placeholder="Input content search" style="width: 200px;" class="filter-item" @keyup.enter.native="crud.toQuery" />
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
      <crudOperation :permission="permission">
        <!-- add -->
        <el-button
          slot="left"
          v-permission="['admin','storage:add']"
          class="filter-item"
          size="mini"
          type="primary"
          icon="el-icon-upload"
          @click="crud.toAdd"
        >Add
        </el-button>
      </crudOperation>
    </div>
    <!--dialog-->
    <el-dialog append-to-body :close-on-click-modal="false" :before-close="crud.cancelCU" :visible.sync="crud.status.cu > 0" :title="crud.status.add ? 'File upload' : 'Edit file'" width="500px">
      <el-form ref="form" :model="form" size="small" label-width="80px">
        <el-form-item label="file name">
          <el-input v-model="form.name" style="width: 370px;" />
        </el-form-item>
        <!--   Upload files   -->
        <el-form-item v-if="crud.status.add" label="Upload">
          <el-upload
            ref="upload"
            :limit="1"
            :before-upload="beforeUpload"
            :auto-upload="false"
            :headers="headers"
            :on-success="handleSuccess"
            :on-error="handleError"
            :action="fileUploadApi + '?name=' + form.name"
          >
            <div class="eladmin-upload"><i class="el-icon-upload" /> Add files</div>
            <div slot="tip" class="el-upload__tip">Files in any format can be uploaded, and no more than 100M</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="text" @click="crud.cancelCU">Cancel</el-button>
        <el-button v-if="crud.status.add" :loading="loading" type="primary" @click="upload">Upload</el-button>
        <el-button v-else :loading="crud.status.cu === 2" type="primary" @click="crud.submitCU">Submit</el-button>
      </div>
    </el-dialog>
    <!--Table rendering-->
    <el-table ref="table" v-loading="crud.loading" :data="crud.data" style="width: 100%;" @selection-change="crud.selectionChangeHandler">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="name" label="File name">
        <template slot-scope="scope">
          <el-popover
            :content="'file/' + scope.row.type + '/' + scope.row.realName"
            placement="top-start"
            title="Path"
            width="200"
            trigger="hover"
          >
            <el-link
              slot="reference"
              :underline="false"
              :href="baseApi + '/file/' + scope.row.type + '/' + scope.row.realName"
              target="_blank"
              type="primary"
            >{{ scope.row.name }}
            </el-link>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column prop="suffix" label="Suffix" />
      <el-table-column prop="type" label="Type" />
      <el-table-column prop="path" label="Path">
        <template slot-scope="{row}">
          <el-image
            :src=" baseApi + '/file/' + row.type + '/' + row.realName"
            :preview-src-list="[baseApi + '/file/' + row.type + '/' + row.realName]"
            fit="contain"
            lazy
            style="width: 60px; height: 40px"
          >
            <div slot="error">
              <span style="line-height: 50px">Preview is not supported</span>
            </div>
          </el-image>
        </template>
      </el-table-column>
      <el-table-column prop="size" label="Size" />
      <el-table-column prop="operate" label="Operate" />
      <el-table-column prop="createTime" label="Time">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
    </el-table>
    <!--Paging component-->
    <pagination />
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getToken } from '@/utils/auth'
import crudFile from '@/api/localStorage'
import CRUD, { presenter, header, form, crud } from '@crud/crud'
import rrOperation from '@crud/RR.operation'
import crudOperation from '@crud/CRUD.operation'
import pagination from '@crud/Pagination'
const defaultForm = { id: null, name: '' }

export default {
  components: { pagination, crudOperation, rrOperation },
  cruds() {
    return CRUD({ title: ' file', url: 'api/localStorage', crudMethod: { ...crudFile }})
  },
  mixins: [presenter(), header(), form(defaultForm), crud()],
  data() {
    return {
      delAllLoading: false,
      loading: false,
      headers: { 'Authorization': getToken() },
      permission: {
        edit: ['admin', 'storage:edit'],
        del: ['admin', 'storage:del']
      }
    }
  },
  computed: {
    ...mapGetters([
      'baseApi',
      'fileUploadApi'
    ])
  },
  created() {
    this.crud.optShow.add = false
  },
  methods: {
    // upload files
    upload() {
      this.$refs.upload.submit()
    },
    beforeUpload(file) {
      let isLt2M = true
      isLt2M = file.size / 1024 / 1024 < 100
      if (!isLt2M) {
        this.loading = false
        this.$message.error('Upload file size cannot exceed 100MB!')
      }
      this.form.name = file.name
      return isLt2M
    },
    handleSuccess(response, file, fileList) {
      this.crud.notify('Upload successfully', CRUD.NOTIFICATION_TYPE.SUCCESS)
      this.$refs.upload.clearFiles()
      this.crud.status.add = CRUD.STATUS.NORMAL
      this.crud.resetForm()
      this.crud.toQuery()
    },
    // Failed to monitor upload
    handleError(e, file, fileList) {
      const msg = JSON.parse(e.message)
      this.$notify({
        title: msg.message,
        type: 'error',
        duration: 2500
      })
      this.loading = false
    }
  }
}
</script>

<style scoped>
  /deep/ .el-image__error, .el-image__placeholder {
    background: none;
  }

  /deep/ .el-image-viewer__wrapper {
    top: 55px;
  }
</style>
