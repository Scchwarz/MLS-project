<template>
  <el-table
      v-loading="loading"
      :data="tableData.data"
      border
      empty-text="没有数据"
      stripe
      style="width: 100%"
  >
    <el-table-column prop="id" label="审核编号" width="150" />
    <el-table-column prop="mid" label="处理员编号" width="150" />
    <el-table-column prop="uname" label="所属用户" width="150" />
    <el-table-column prop="fid" label="文件编号" width="150" />
    <el-table-column prop="message" label="留言" width="300" />
    <el-table-column prop="checked" label="审核结果" width="150" />
    <el-table-column fixed="right" label="删除" width="165">
      <template #default="scope">
        <el-button link type="danger" @click="deleteMessage(scope)">删除</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import {onMounted, reactive, ref} from "vue";
import {get, post} from "@/net";
import {ElMessage} from "element-plus";
import axios from "axios";

const loading = ref(false) // 是否显示加载效果

const tableData = reactive({
  data: [{
    id: '',
    mid: '',
    uname: '',
    fid: '',
    message: '',
    checked: '',
  }]
})
onMounted(() => {
  loading.value = true
  // 调用接口，获取列表数据
  get('/api/auth/getUMessage',(data) =>{
    tableData.data = data
  })
  loading.value = false
})
const deleteMessage = (scope) =>{
  axios.post('api/auth/deleteMessage', {
    id: scope.row.id
  })
  ElMessage.success("删除成功!");
  refreshTable();
}
const refreshTable = () => {
  loading.value = true
  // 调用接口，获取列表数据
  get('/api/auth/getUMessage',(data) =>{
    tableData.data = data
  })
  loading.value = false
};
</script>

<style scoped>

</style>