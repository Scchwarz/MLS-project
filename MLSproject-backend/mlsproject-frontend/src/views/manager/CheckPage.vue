<template>
  <div>
    <el-input
        size="large"
        placeholder="请输入检索文献的文件名"
        type="text"
        v-model="searchText"
    >
      <template #append>
        <el-button @click="searchByText" :icon="Search" />
      </template>
    </el-input>
  </div>
  <div>
    <el-table
        :data="searchResults.data"
        border
        empty-text="没有数据"
        stripe
        style="width: 100%"
    >
      <el-table-column prop="fileId" label="文件编号" width="150" />
      <el-table-column prop="fileName" label="文件名" width="300" />
      <el-table-column label="文件大小" width="150">
        <template #default="{ row }">
          {{ formattedDataMap[row.fileId].formattedSize }}
        </template>
      </el-table-column>
      <el-table-column prop="downloadCounts" label="下载数量" width="150" />
      <el-table-column label="上传时间" width="250">
        <template #default="{ row }">
          {{ formattedDataMap[row.fileId].formattedUploadTime }}
        </template>
      </el-table-column>
      <el-table-column prop="userName" label="所属用户" width="150" />
      <el-table-column fixed="right" label="查看详情" width="165">
        <template #default="scope">
          <el-button size="small" type="text" @click="showDrawer(scope.row.fileName)">查看详情</el-button>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="下载" width="165">
        <template #default="scope">
          <el-button size="small" type="text" @click="downloadFile(scope)">下载文件</el-button>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="审核" width="165">
        <template #default="scope">
          <el-button text @click="showDialog(scope.row)">
            输入审核结果
          </el-button>
          <el-dialog v-model="dialogFormVisible" title="审核界面">
            <el-form :model="form">
              <el-form-item label="审核结果留言" :label-width="formLabelWidth">
                <el-input v-model="form.message" autocomplete="off" />
              </el-form-item>
              <el-form-item label="审核结果" :label-width="formLabelWidth">
                <el-select v-model="form.checked" placeholder="请选择审核结果">
                  <el-option label="通过" value="已审核" />
                  <el-option label="打回" value="未通过" />
                </el-select>
              </el-form-item>
            </el-form>
            <span class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirm(scope.row)">
          确定
        </el-button>
      </span>
          </el-dialog>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        background
        layout="prev, pager, next"
        style="margin-top: 15px"
    ></el-pagination>
    <el-drawer v-model="drawer" title="详情页面">
      <div>
        <iframe height="800" :src="iframeSrc" width="1000"></iframe>
      </div>
      <div style="text-align: center;">
        <el-button type="primary" @click="drawer = false">关闭</el-button>
      </div>
    </el-drawer>
  </div>
</template>
<script setup>
import { Search } from '@element-plus/icons-vue'
import {get, post} from "@/net";
import {computed, onMounted, reactive, ref} from "vue";
import {ElMessage} from "element-plus";
import router from "@/router";

const drawer = ref(false)
const searchText = ref()
const searchResults = reactive({
  data: [{
    fileId: '',
    fileName: '',
    path: '',
    size: '',
    downloadCounts: '',
    uploadTime: '',
    userName: '',
    checked: ''
  }]
})
const form = reactive({
  message: '',
  checked:'',
})

const iframeSrc = ref('');
const pdfUrl = 'src/assets/pdfFiles/';
const dialogFormVisible = ref(false)
const formLabelWidth = '140px'

onMounted(() => {
  // 调用接口，获取列表数据
  get('/api/auth/manager/uncheckedFiles?fileName= ',(data) =>{
    searchResults.data = data
  })
})
const showDialog = (rowData) => {
  // Handle logic to show dialog based on rowData
  dialogFormVisible.value = true;
};
const handleConfirm = (row) => {
  // 执行第二个POST请求，例如更新权限状态的接口地址为'/api/updatePermissionStatus'
  post('/api/auth/manager/checkFile', {
    Uname: row.userName,
    check: form.checked,
    message: form.message,
    id: row.fileId,
  }, () => {
    console.log('权限修改成功');
    refreshTable();
    // 其他操作，例如显示成功消息
    ElMessage.success('审核成功');
  });
  dialogFormVisible.value = false;
};
const searchByText = () => {
  get(`/api/auth/manager/uncheckedFiles?fileName=${searchText.value}`, (data) => {
    searchResults.data = data
  }, () =>{
    ElMessage.error()
  })
};
const refreshTable = () => {
  // 调用接口，获取列表数据
  get(`/api/auth/manager/uncheckedFiles?fileName= `, (data) => {
    searchResults.data = data
  }, () =>{
    ElMessage.error()
  })
};
const downloadFile = (scope) => {
  const fileId = scope.row.fileId;
  fetch(`http://localhost:8080/api/auth/downloadFile?fileId=${fileId}`)
      .then(response => {
        const filename = scope.row.fileName;
        console.log(filename)

        return response.blob().then(blob => {
          // 创建一个带有文件名的 Blob 对象
          const file = new Blob([blob], { type: response.headers.get('Content-Type') });
          const link = document.createElement('a');

          // 使用解析后的文件名
          link.href = window.URL.createObjectURL(file);
          link.download = filename;
          console.log(filename);

          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);

          ElMessage.success('下载成功');
        });
      })
      .catch(error => {
        console.error('下载请求出错', error);
      });
};
const showDrawer = (fileName) => {
  drawer.value = true;
  iframeSrc.value = pdfUrl + fileName;
};

// 计算属性：格式化文件大小和上传时间的映射
const formattedDataMap = computed(() => {
  const units = ['B', 'KB', 'MB', 'GB', 'TB'];

  return searchResults.data.reduce((map, item) => {
    const rawSize = item.size;
    let size = parseInt(rawSize, 10);
    let unitIndex = 0;

    while (size >= 1024 && unitIndex < units.length - 1) {
      size /= 1024;
      unitIndex++;
    }

    const formattedSize = `${size.toFixed(2)} ${units[unitIndex]}`;

    const rawUploadTime = item.uploadTime;
    const uploadTime = new Date(rawUploadTime);

    // 使用适当的日期格式化选项
    const options = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
    const formattedUploadTime = uploadTime.toLocaleDateString('en-US', options);

    map[item.fileId] = {
      formattedSize,
      formattedUploadTime
    };

    return map;
  }, {});
});

</script>

<style scoped>

</style>