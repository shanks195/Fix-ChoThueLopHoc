<template>
  <el-dialog :visible.sync="dialog" append-to-body title="execution log" width="88%">
    <!-- Search -->
    <div class="head-container">
      <el-input v-model="query.jobName" clearable size="small" placeholder="Enter job name search" style="width: 200px;" class="filter-item" @keyup.enter.native=" toQuery" />
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
      <el-select v-model="query.isSuccess" placeholder="log status" clearable class="filter-item" style="width: 110px" @change="toQuery">
        <el-option v-for="item in enabledTypeOptions" :key="item.key" :label="item.display_name" :value="item.key" />
      </el-select>
      <el-button class="filter-item" size="mini" type="success" icon="el-icon-search" @click="toQuery">Search</el-button>
      <!-- Export -->
      <div style="display: inline-block;">
        <el-button
          :loading="downloadLoading"
          size="mini"
          class="filter-item"
          type="warning"
          icon="el-icon-download"
          @click="downloadMethod"
        >Export</el-button>
      </div>
    </div>
    <!--Form rendering-->
    <el-table v-loading="loading" :data="data" style="width: 100%;margin-top: -10px;">
      <el-table-column :show-overflow-tooltip="true" prop="jobName" label="Name" />
      <el-table-column :show-overflow-tooltip="true" prop="beanName" label="BeanName" />
      <el-table-column :show-overflow-tooltip="true" prop="methodName" label="Method" />
      <el-table-column :show-overflow-tooltip="true" prop="params" width="120px" label="Parameters" />
      <el-table-column :show-overflow-tooltip="true" prop="cronExpression" label="Cron" />
      <el-table-column prop="createTime" label="Exception details" width="110px">
        <template slot-scope="scope">
          <el-button v-show="scope.row.exceptionDetail" size="mini" type="text" @click="info(scope.row.exceptionDetail)">View details</el-button>
        </template>
      </el-table-column>
      <el-table-column :show-overflow-tooltip="true" align="center" prop="time" width="100px" label="Time-consuming (milliseconds)" />
      <el-table-column align="center" prop="isSuccess" width="80px" label="status">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isSuccess?'success':'danger'">{{ scope.row.isSuccess?'Success':'Failure' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :show-overflow-tooltip="true" prop="createTime" label="creation date">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog :visible.sync="errorDialog" append-to-body title="Exception details" width="85%">
      <pre v-highlightjs="errorInfo"><code class="java" /></pre>
    </el-dialog>
    <!--Paging component-->
    <el-pagination
      :total="total"
      :current-page="page + 1"
      :page-size="6"
      style="margin-top:8px;"
      layout="total, prev, pager, next"
      @size-change="sizeChange"
      @current-change="pageChange"
    />
  </el-dialog>
</template>

<script>
import crud from '@/mixins/crud'
export default {
  mixins: [crud],
  data() {
    return {
      errorInfo: ' ', errorDialog: false,
      enabledTypeOptions: [
        { key: 'true', display_name: 'success' },
        { key: 'false', display_name: 'Failed' }
      ]
    }
  },
  methods: {
    doInit() {
      this.$nextTick(() => {
        this.init()
      })
    },
    // Set the interface address before getting the data
    beforeInit() {
      this.url = 'api/jobs/logs'
      this.size = 6
      return true
    },
    // Exception details
    info(errorInfo) {
      this.errorInfo = errorInfo
      this.errorDialog = true
    }
  }
}
</script>

<style scoped>
  .java.hljs{
    color: #444;
    background: #ffffff !important;
  }
  /deep/ .el-dialog__body{
    padding: 0 20px 10px 20px !important;
  }
</style>
