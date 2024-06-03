<template>
  <div class="background">
    <el-container>
      <el-header height="190px">
        <div class="title">
          <h1>{{ fileInfo.fileName }}</h1>
          <el-table :data="[fileInfo]" style="width: 100%">
            <el-table-column prop="fileId" label="文件编号" />
            <el-table-column label="文件大小">
              {{ formattedSize }}
            </el-table-column>
            <el-table-column prop="downloadCounts" label="下载量" />
            <el-table-column label="上传时间">
              {{ formattedUploadTime }}
            </el-table-column>
            <el-table-column prop="userName" label="所属用户" />
            <el-table-column prop="favCounts" label="收藏量" />
            <el-table-column prop="likeCounts" label="点赞量" />
          </el-table>
        </div>
      </el-header>
      <div class="button-group">
        <el-button @click="downloadFile" plain type="info" :icon="Download">下载</el-button>
        <el-button @click="navigateToLocalView" plain type="primary" :icon="View">在线预览</el-button>
      </div>
      <el-main height="400px">
        <div class="overview">
          <h2>结果概要:</h2>
          <div v-html="highlightText(content)"></div>
        </div>
      </el-main>
    </el-container>
  </div>
</template>
<script setup>
import {Download, Pointer, Star, View} from "@element-plus/icons-vue";
import {onMounted, ref, reactive,computed} from "vue";
import { useRoute } from "vue-router";
import {get, post} from "@/net";
import {ElMessage} from "element-plus";
import router from "@/router";

const id = ref();
const content = ref('');
const route = useRoute();
const fileInfo = reactive(
    {
      fileId: '',
      fileName: '',
      path: '',
      size: '',
      downloadCounts: '',
      uploadTime: '',
      userName: '',
      favCounts: '',
      likeCounts: '',
    }
)
const highlightText = (text) => {
  // 使用正则表达式替换<em>为实际的HTML高亮标签
  return text.replace('<em>', '<span style=\'color:red\'>').replace('</em>', '</span>');
};
const navigateToLocalView = (row) => {
  // 直接使用 router.push 进行程序化导航
  router.push({ name: 'LocalView', params: {url: fileInfo.fileName} });
};

onMounted(() => {
  id.value = route.params.id;
  content.value = route.params.content;
  get(`/api/auth/file?fileId=${id.value}`,(data) =>{
    Object.assign(fileInfo, data);
  })
});
const downloadFile = () => {
  const fileId = fileInfo.fileId;
  fetch(`http://localhost:8080/api/auth/downloadFile?fileId=${fileId}`)
      .then(response => {
        const filename = fileInfo.fileName;
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
// 大小和上传时间的转换
const formattedSize = computed(() => {
  const units = ['B', 'KB', 'MB', 'GB', 'TB'];
  let size = parseInt(fileInfo.size, 10);
  let unitIndex = 0;

  while (size >= 1024 && unitIndex < units.length - 1) {
    size /= 1024;
    unitIndex++;
  }

  return `${size.toFixed(2)} ${units[unitIndex]}`;
});

const formattedUploadTime = computed(() => {
  const uploadTime = new Date(fileInfo.uploadTime);
  const options = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
  return uploadTime.toLocaleDateString('en-US', options);
});
</script>
<style scoped>

</style>