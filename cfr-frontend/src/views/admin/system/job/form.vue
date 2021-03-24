<template>
  <el-dialog
    append-to-body="append-to-body"
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
      <el-form-item label="Sort" prop="sort">
        <el-input-number
          v-model.number="form.sort"
          :min="0"
          :max="999"
          controls-position="right"
          style="width: 370px;"
        />
      </el-form-item>
      <el-form-item v-if="form.pid !== 0" label="Enabled" prop="enabled">
        <el-radio v-for="item in jobStatus" :key="item.id" v-model="form.enabled" :label="item.value === 'true'">{{
          item.label }}
        </el-radio>
      </el-form-item>
      <el-form-item label="Dept" prop="dept.id" :rules="rules.dept">
        <treeselect v-model="form.dept.id" :options="depts" style="width: 370px" placeholder="Select department" />
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button type="text" @click="crud.cancelCU">Cancel</el-button>
      <el-button :loading="crud.status.cu === 2" type="primary" @click="crud.submitCU">Submit</el-button>
    </div>
  </el-dialog>
</template>

<script>
import CRUD, { form } from '@crud/crud'
import { getDepts } from '@/api/dept'
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'

const defaultForm = {
  id: 1,
  name: '',
  sort: 999,
  enabled: true,
  dept: {
    id: null
  }
}
export default {
  components: { Treeselect },
  mixins: [form(defaultForm)],
  props: {
    jobStatus: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      depts: [],
      rules: {
        name: [
          { required: true, message: 'Please enter a name', trigger: 'blur' }
        ],
        sort: [
          { required: true, message: 'Please enter the serial number', trigger: 'blur', type: 'number' }
        ],
        dept: { required: true, message: 'Department cannot be empty', trigger: 'select' }
      }
    }
  },
  methods: {
    [CRUD.HOOK.beforeToCU]() {
      getDepts({ enabled: true }).then(res => {
        this.depts = res.content
      })
    },
    // Verification before submission
    [CRUD.HOOK.afterValidateCU]() {
      if (!this.form.dept.id) {
        this.$notify({
          title: 'Department cannot be empty',
          type: 'warning'
        })
        return false
      }
      return true
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  /deep/ .el-input-number .el-input__inner {
    text-align: left;
  }
</style>
