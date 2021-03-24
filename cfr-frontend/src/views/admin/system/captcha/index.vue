<template>
  <div class="app-container">
    <div class="head-container">
      <el-dialog
        :append-to-body="true"
        :close-on-click-modal="false"
        :before-close="cancel"
        :visible.sync="dialog"
        :title="getFormTitle()"
        width="500px"
      >
        <el-form ref="form" :model="form" :rules="rules" size="small" label-width="80px">
          <el-form-item label="Type" prop="type">
            <el-radio v-for="item in dict.captcha_type" :key="item.id" v-model="form.type" :label="item.value">{{
              item.label }}
            </el-radio>
          </el-form-item>
          <el-form-item label="Name">
            <el-radio v-for="item in dict.font_name" :key="item.id" v-model="form.fontName" :label="item.value">{{
              item.label }}
            </el-radio>
          </el-form-item>
          <el-form-item label="Style">
            <el-radio
              v-for="item in dict.captcha_fontstyle"
              :key="item.id"
              v-model="form.fontStyle"
              :label="item.value"
            >{{ item.label }}
            </el-radio>
          </el-form-item>
          <el-form-item label="Size">
            <el-input v-model="form.fontSize" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="Width">
            <el-input v-model="form.width" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="Height">
            <el-input v-model="form.height" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="Leght">
            <el-input v-model="form.len" style="width: 370px;" />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="text" @click="cancel">Cancel</el-button>
          <el-button :loading="loading" type="primary" @click="submitMethod">Submit</el-button>
        </div>
      </el-dialog>
      <!--Form rendering-->
      <el-table ref="table" v-loading="loading" :data="data" size="small" style="width: 100%;">
        <el-table-column prop="type" label="Type">
          <template slot-scope="scope">
            {{ dict.label.captcha_type[scope.row.type] }}
          </template>
        </el-table-column>
        <el-table-column prop="fontName" label="Name">
          <template slot-scope="scope">
            {{ dict.label.font_name[scope.row.fontName] }}
          </template>
        </el-table-column>
        <el-table-column prop="fontStyle" label="Style">
          <template slot-scope="scope">
            {{ dict.label.captcha_fontstyle[scope.row.fontStyle] }}
          </template>
        </el-table-column>
        <el-table-column prop="fontSize" label="Size" />
        <el-table-column prop="width" label="Width" />
        <el-table-column prop="height" label="Height" />
        <el-table-column prop="len" label="Lenght" />
        <el-table-column
          v-if="checkPermission(['admin','captcha:edit'])"
          label="Operation"
          width="150px"
          align="center"
        >
          <template slot-scope="scope">
            <el-button
              v-permission="['admin','captcha:edit']"
              size="mini"
              type="primary"
              icon="el-icon-edit"
              @click="showEditFormDialog(scope.row)"
            />
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import crud from '@/mixins/crud'
import crudCaptcha from '@/api/captcha'

export default {
  mixins: [crud],
  dicts: ['captcha_type', 'font_name', 'captcha_fontstyle'],
  data() {
    return {
      title: 'captcha',
      crudMethod: { ...crudCaptcha },
      form: {
        id: null,
        type: null,
        fontName: null,
        fontStyle: null,
        fontSize: null,
        width: null,
        height: null,
        len: null
      },
      rules: {
        id: [
          { required: true, message: 'id cannot be empty', trigger: 'blur' }
        ],
        type: [
          { required: true, message: 'Verification code type cannot be empty', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.$nextTick(() => {
      this.init()
    })
  },
  methods: {
    // Set the interface address before getting data
    beforeInit() {
      this.url = 'api/captcha'
      const sort = 'id,desc'
      this.params = { page: this.page, size: this.size, sort: sort }
      return true
    }
  }
}
</script>
<style scoped>
</style>
