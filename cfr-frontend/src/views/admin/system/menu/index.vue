<template>
  <div class="app-container">
    <div class="head-container">
      <div v-if="crud.props.searchToggle">
        <el-input
          v-model="query.blurry"
          clearable
          size="small"
          placeholder="Search"
          style="width: 200px;"
          class="filter-item"
          @keyup.enter.native="toQuery"
        />
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
    </div>
    <el-dialog
      v-el-drag-dialog
      append-to-body
      :close-on-click-modal="false"
      :before-close="crud.cancelCU"
      :visible.sync="crud.status.cu > 0"
      :title="crud.status.title"
      width="600px"
    >
      <el-form ref="form" :inline="true" :model="form" :rules="rules" size="small" label-width="100px">
        <el-form-item label="Type" prop="type">
          <el-radio-group v-model="form.type" size="mini" style="width: 178px">
            <el-radio-button label="0">Table</el-radio-button>
            <el-radio-button label="1">Menu</el-radio-button>
            <el-radio-button label="2">Button</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-show="form.type.toString() !== '2'" label="Icon" prop="icon">
          <el-popover
            placement="bottom-start"
            width="450"
            trigger="click"
            @show="$refs['iconSelect'].reset()"
          >
            <IconSelect ref="iconSelect" @selected="selected" />
            <el-input slot="reference" v-model="form.icon" style="width: 450px;" placeholder="Click the select icon" readonly>
              <svg-icon
                v-if="form.icon"
                slot="prefix"
                :icon-class="form.icon"
                class="el-input__icon"
                style="height: 32px;width: 16px;"
              />
              <i v-else slot="prefix" class="el-icon-search el-input__icon" />
            </el-input>
          </el-popover>
        </el-form-item>
        <el-form-item v-show="form.type.toString() !== '2'" label="Link" prop="iframe">
          <el-radio-group v-model="form.iframe" size="mini">
            <el-radio-button label="true">true</el-radio-button>
            <el-radio-button label="false">false</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-show="form.type.toString() === '1'" label="Cache" prop="cache">
          <el-radio-group v-model="form.cache" size="mini">
            <el-radio-button label="true">true</el-radio-button>
            <el-radio-button label="false">false</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-show="form.type.toString() !== '2'" label="Hidden" prop="hidden">
          <el-radio-group v-model="form.hidden" size="mini">
            <el-radio-button label="false">true</el-radio-button>
            <el-radio-button label="true">false</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-show="form.type.toString() !== '2'" label="Title" prop="name">
          <el-input
            v-model="form.name"
            :style=" form.type.toString() === '0' ? 'width: 450px' : 'width: 178px'"
            placeholder="Title"
          />
        </el-form-item>
        <el-form-item v-show="form.type.toString() === '2'" label="Name" prop="name">
          <el-input v-model="form.name" placeholder="Name" style="width: 178px;" />
        </el-form-item>
        <el-form-item v-show="form.type.toString() !== '0'" label="Permission" prop="permission">
          <el-input v-model="form.permission" :disabled="form.iframe" placeholder="Authority" style="width: 178px;" />
        </el-form-item>
        <el-form-item v-if="form.type.toString() !== '2'" label="Path" prop="path">
          <el-input v-model="form.path" placeholder="Routing address" style="width: 450px;" />
        </el-form-item>
        <el-form-item label="Sort" prop="sort">
          <el-input-number
            v-model.number="form.sort"
            :min="0"
            :max="999"
            controls-position="right"
            style="width: 450px;"
          />
        </el-form-item>
        <el-form-item v-show="!form.iframe && form.type.toString() === '1'" label="Name" prop="componentName">
          <el-input v-model="form.componentName" style="width: 178px;" placeholder="Name field in matching component" />
        </el-form-item>
        <el-form-item v-show="!form.iframe && form.type.toString() === '1'" label="Path" prop="component">
          <el-input v-model="form.component" style="width: 178px;" placeholder="Component path" />
        </el-form-item>
        <el-form-item label="Category" prop="pid">
          <treeselect v-model="form.pid" :options="menus" style="width: 450px;" placeholder="Choose superior category" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="text" @click="crud.cancelCU">Cancel</el-button>
        <el-button :loading="crud.status.cu === 2" type="primary" @click="crud.submitCU">Submit</el-button>
      </div>
    </el-dialog>
    <el-table
      ref="table"
      v-loading="crud.loading"
      :data="crud.data"
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
      row-key="id"
      @select="crud.selectChange"
      @select-all="crud.selectAllChange"
      @selection-change="crud.selectionChangeHandler"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column :show-overflow-tooltip="true" label="Name" width="125px" prop="name" />
      <el-table-column prop="icon" label="Icon" align="center" width="60px">
        <template slot-scope="scope">
          <svg-icon :icon-class="scope.row.icon ? scope.row.icon : ''" />
        </template>
      </el-table-column>
      <el-table-column prop="sort" align="center" label="Sort">
        <template slot-scope="scope">
          {{ scope.row.sort }}
        </template>
      </el-table-column>
      <el-table-column :show-overflow-tooltip="true" prop="path" label="Path" />
      <el-table-column :show-overflow-tooltip="true" prop="permission" label="Permission" />
      <el-table-column :show-overflow-tooltip="true" prop="component" label="Component" />
      <el-table-column prop="iframe" label="Link" width="75px">
        <template slot-scope="scope">
          <span v-if="scope.row.iframe">Yes</span>
          <span v-else>No</span>
        </template>
      </el-table-column>
      <el-table-column prop="cache" label="Cache" width="75px">
        <template slot-scope="scope">
          <span v-if="scope.row.cache">Yes</span>
          <span v-else>No</span>
        </template>
      </el-table-column>
      <el-table-column prop="hidden" label="Hidden" width="75px">
        <template slot-scope="scope">
          <span v-if="scope.row.hidden">Yes</span>
          <span v-else>No</span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="Date" width="135px">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column
        v-permission="['admin','menu:edit','menu:del']"
        label="Operating"
        width="130px"
        align="center"
        fixed="right"
      >
        <template slot-scope="scope">
          <udOperation
            :data="scope.row"
            :permission="permission"
            msg="Are you sure to delete? If there are lower-level nodes, delete them together. This operation cannot be undone！"
          />
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import crudMenu from '@/api/menu'
import IconSelect from '@/components/IconSelect'
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import CRUD, { presenter, header, form, crud } from '@crud/crud'
import rrOperation from '@crud/RR.operation'
import crudOperation from '@crud/CRUD.operation'
import udOperation from '@crud/UD.operation'
import elDragDialog from '@/components/el-drag-dialog'
const defaultForm = {
  id: null,
  name: null,
  sort: 999,
  path: null,
  component: null,
  componentName: null,
  iframe: false,
  roles: [],
  pid: 0,
  icon: null,
  cache: false,
  hidden: false,
  type: 0,
  permission: null
}

export default {
  name: 'Menu',
  components: { Treeselect, IconSelect, crudOperation, rrOperation, udOperation },
  directives: { elDragDialog },
  cruds() {
    return CRUD({ title: '_Menu', url: 'api/menus', crudMethod: { ...crudMenu }})
  },
  mixins: [presenter(), header(), form(defaultForm), crud()],
  data() {
    return {
      menus: [],
      permission: {
        add: ['admin', 'menu:add'],
        edit: ['admin', 'menu:edit'],
        del: ['admin', 'menu:del']
      },
      rules: {
        name: [
          { required: true, message: 'Please enter a name', trigger: 'blur' }
        ],
        path: [
          { required: true, message: 'Please enter the address', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    // Operations before adding and editing
    [CRUD.HOOK.afterToCU](crud, form) {
      crudMenu.getMenusTree().then(res => {
        this.menus = []
        const menu = { id: 0, label: 'Top category', children: [] }
        menu.children = res
        this.menus.push(menu)
      })
    },
    // Check icon
    selected(name) {
      this.form.icon = name
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  /deep/ .el-input-number .el-input__inner {
    text-align: left;
  }
</style>
