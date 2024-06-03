<template>
  <div ref="tbContainerRef" class="tb-container">
      <div>
        <div>
          <input type="file" ref="fileInput" @change="handleFileChange" />
         <el-button @click="uploadFile">上传文件</el-button>
        </div>
      </div>
    <h2 style="margin:0;text-align:center;">我上传的文件</h2>
    <!-- 表格 -->
    <el-table
        v-loading="loading"
        :data="tableData.data"
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
      <el-table-column prop="state" label="文件权限" width="150" />
      <el-table-column prop="checked" label="是否审核" width="150" />
      <el-table-column fixed="right" label="修改权限" width="150" >
        <template #default="scope">
          <el-button text @click="updateState(scope)">修改权限</el-button>
        </template>
      </el-table-column>
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
      <el-table-column fixed="right" label="删除" width="165">
        <template #default="scope">
          <el-button link type="danger" @click="deleteFile(scope)">删除</el-button>
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

<script>
</script>

<script setup>
import {ref, reactive, onMounted, computed} from 'vue'
import {get, post, takeAccessToken} from "@/net";
import {ElLoading, ElMessage, ElMessageBox} from "element-plus";
import axios from "axios";
import router from "@/router";
const drawer = ref(false)
const loading = ref(false) // 是否显示加载效果
const iframeSrc = ref('');
const pdfUrl = 'src/assets/pdfFiles/';

const tableData = reactive({
  data: [{
    fileId: '',
    fileName: '',
    path: '',
    size: '',
    downloadCounts: '',
    uploadTime: '',
    userName: '',
    state:'',
    check:''
  }]
})

function handleFileChange(event) {
  // 将文件存储在组件的 data 中
  file.value = event.target.files[0];
}

function uploadFile() {
  loading.value = true
  let formData = new FormData();
  formData.append('file', file.value);

  axios.post('/api/auth/uploadFile', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
      'Authorization': `Bearer ${takeAccessToken()}`,
    },
  })
      .then(response => {
        loading.value = false
        ElMessage.success("文件上传成功")
        refreshTable()
        console.log('File uploaded successfully:', response);
      })
      .catch(error => {
        loading.value = false
        ElMessage.error("发生了一些错误")
        console.error('File upload failed:', error);
      });
}

const file = ref(null);

onMounted(() => {
  loading.value = true
  // 调用接口，获取列表数据
  get('/api/auth/files/thisUser',(data) =>{
    tableData.data = data
  })
  loading.value = false
})

const updateState = (scope) => {
  ElMessageBox.confirm(
      '是否确定修改文件权限?',
      '提示',
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
  )
      .then(() => {
        let newStateValue;

        switch (scope.row.state) {
          case '公共':
            newStateValue = '私有';
            break;
          case '私有':
            newStateValue = '公共';
            break;

          default:
            newStateValue = '私有';
            break;
        }

        post('api/auth/setFileState', {
          id: scope.row.fileId,
          state: newStateValue
        }, () => {
          ElMessage.success('权限修改成功！');
          refreshTable();
        });
      })
      .catch(() => {
        ElMessage({
          type: 'info',
          message: '修改权限操作已取消',
        });
      });
};

const refreshTable = () => {
  loading.value = true
  // 调用接口，获取列表数据
  get('/api/auth/files/thisUser',(data) =>{
    tableData.data = data
  })
  loading.value = false
};

const deleteFile = (scope) =>{
  post('api/auth/deleteFile', {
    id: scope.row.fileId
  },() => {
    ElMessage.success('删除成功！')
    refreshTable()
  })
}
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
          refreshTable()
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

  return tableData.data.reduce((map, item) => {
    const rawSize = item.size;
    const sizeInBytes = parseInt(rawSize, 10);

    let size = sizeInBytes;
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
<style lang="scss" scoped>
.box-orc-input {
  size: 70px;
}

.promotionbackground {
  display: block;
  width: 170px;
  height: 50px;
  position: relative;
  color: rgb(0, 0, 0);
  background-color: rgb(0, 255, 221);
  margin-left: 0px;
  left: 0px;
  top: 0px;
  right: 0px;
  bottom: 0px;
}

.tb-container {
  position: relative;
}

#contextmenu {
  position: absolute;
  top: 0;
  left: 0;
  height: auto;
  width: 120px;
  border-radius: 3px;
  border: 1px solid #999999;
  background-color: #f4f4f4;
  padding: 10px;
  z-index: 12;

  button {
    display: block;
    margin: 0 0 5px;
  }
}

.el-table .el-table__cell {
  height: 48px;
  padding: 0;
}

.details .el-form-item {
  width: 100%;
}
</style>