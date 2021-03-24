<template>
  <div v-if="crud.props.searchToggle">
    <el-input
      v-model="query.name"
      class="filter-item"
      clearable="clearable"
      size="small"
      placeholder="Enter job name search"
      style="width: 200px;"
      @keyup.enter.native="crud.toQuery"
    />
    <el-date-picker
      v-model="query.createTime"
      class="date-item"
      :default-time="['00:00:00','23:59:59']"
      type="daterange"
      range-separator=":"
      size="small"
      value-format="yyyy-MM-dd HH:mm:ss"
      start-placeholder="Start date"
      end-placeholder="End date"
    />
    <el-select
      v-model="query.enabled"
      class="filter-item"
      clearable="clearable"
      size="small"
      placeholder="Status"
      style="width: 90px"
      @change="crud.toQuery"
    >
      <el-option v-for="item in dict.dict.job_status" :key="item.value" :label="item.label" :value="item.value" />
    </el-select>
    <rrOperation />
  </div>
</template>

<script>
import { header } from '@crud/crud'
import rrOperation from '@crud/RR.operation'

export default {
  components: { rrOperation },
  mixins: [header()],
  props: {
    dict: {
      type: Object,
      required: true
    },
    permission: {
      type: Object,
      required: true
    }
  }
}
</script>
