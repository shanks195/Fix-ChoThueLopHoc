<template>
  <div class="crud-opts">
    <span class="crud-opts-left">
      <!--Left slot-->
      <slot name="left" />
      <el-button
        v-if="crud.optShow.edit"
        v-permission="permission.edit"
        class="filter-item"
        size="mini"
        type="success"
        icon="el-icon-edit"
        :disabled="crud.selections.length !== 1"
        @click="crud.toEdit(crud.selections[0])"
      >Edit</el-button>
      <el-button
        v-if="crud.optShow.del"
        slot="reference"
        v-permission="permission.del"
        class="filter-item"
        type="danger"
        icon="el-icon-delete"
        size="mini"
        :loading="crud.delAllLoading"
        :disabled="crud.selections.length === 0"
        @click="toDelete(crud.selections)"
      >Delete</el-button>
      <el-button
        v-if="crud.optShow.download"
        class="filter-item"
        :loading="crud.downloadLoading"
        :disabled="!crud.data.length"
        size="mini"
        type="warning"
        icon="el-icon-download"
        @click="crud.doExport"
      >Download</el-button>
      <!--Right-->
      <slot name="right" />
    </span>
    <el-button-group class="crud-opts-right">
      <el-button size="mini" plain="plain" type="info" icon="el-icon-search" @click="toggleSearch()" />
      <el-button size="mini" icon="el-icon-refresh" @click="crud.refresh()" />
      <el-popover placement="bottom-end" width="150" trigger="click">
        <el-button slot="reference" size="mini" icon="el-icon-s-grid"><i class="fa fa-caret-down" aria-hidden="true" />
        </el-button>
        <el-checkbox
          v-model="allColumnsSelected"
          :indeterminate="allColumnsSelectedIndeterminate"
          @change="handleCheckAllChange"
        >Select all
        </el-checkbox>
        <el-checkbox
          v-for="item in crud.props.tableColumns"
          :key="item.label"
          v-model="item.visible"
          @change="handleCheckedTableColumnsChange(item)"
        >{{ item.label }}
        </el-checkbox>
      </el-popover>
    </el-button-group>
  </div>
</template>
<script>
import CRUD, { crud } from '@crud/crud'

export default {
  mixins: [crud()],
  props: {
    permission: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  data() {
    return {
      allColumnsSelected: true,
      allColumnsSelectedIndeterminate: false
    }
  },
  created() {
    this.crud.updateProp('searchToggle', true)
  },
  methods: {
    toDelete(datas) {
      this.$confirm(`Confirm to delete selected${datas.length}data?`, 'DDT', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        this.crud.delAllLoading = true
        this.crud.doDelete(datas)
      }).catch(() => {
      })
    },
    handleCheckAllChange(val) {
      if (val === false) {
        this.allColumnsSelected = true
        return
      }
      this.crud.props.tableColumns.forEach(column => {
        if (!column.visible) {
          column.visible = true
          this.updateColumnVisible(column)
        }
      })
      this.allColumnsSelected = val
      this.allColumnsSelectedIndeterminate = false
    },
    handleCheckedTableColumnsChange(item) {
      let totalCount = 0
      let selectedCount = 0
      this.crud.props.tableColumns.forEach(column => {
        ++totalCount
        selectedCount += column.visible ? 1 : 0
      })
      if (selectedCount === 0) {
        this.crud.notify('Please select at least one column', CRUD.NOTIFICATION_TYPE.WARNING)
        this.$nextTick(function() {
          item.visible = true
        })
        return
      }
      this.allColumnsSelected = selectedCount === totalCount
      this.allColumnsSelectedIndeterminate = selectedCount !== totalCount && selectedCount !== 0
      this.updateColumnVisible(item)
    },
    updateColumnVisible(item) {
      const table = this.crud.props.table
      const vm = table.$children.find(e => e.prop === item.property)
      const columnConfig = vm.columnConfig
      if (item.visible) {
        let columnIndex = -1
        // Find a suitable insertion point
        table.store.states.columns.find(e => {
          columnIndex++
          return e.__index !== undefined && e.__index > columnConfig.__index
        })
        vm.owner.store.commit('insertColumn', columnConfig, columnIndex, null)
      } else {
        vm.owner.store.commit('removeColumn', columnConfig, null)
      }
    },
    toggleSearch() {
      this.crud.props.searchToggle = !this.crud.props.searchToggle
    }
  }
}
</script>

<style>
  .crud-opts {
    padding: 4px 0;
    display: -webkit-flex;
    display: flex;
    align-items: center;
  }

  .crud-opts .crud-opts-right {
    margin-left: auto;
  }
</style>
