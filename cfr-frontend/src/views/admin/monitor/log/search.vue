<template>
  <div v-if="crud.props.searchToggle">
    <el-input
      v-model="query.blurry"
      clearable
      size="small"
      placeholder="Please enter what you want to search"
      style="width: 200px;"
      class="filter-item"
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
</template>

<script>
import { header } from '@crud/crud'
import rrOperation from '@crud/RR.operation'

export default {
  components: { rrOperation },
  mixins: [header()]
}
</script>
