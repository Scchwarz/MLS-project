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
      <el-table-column fixed="right" label="收藏" width="100">
        <template #default="scope">
          <el-button
              size="small"
              plain
              type="warning"
              :icon="Star"
              @click="toggleFavorite(scope.row)"
          >
            {{ isFileFavorite(scope.row.fileId) ? '已收藏' : '收藏' }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="点赞" width="100">
        <template #default="scope">
          <el-button
              size="small"
              plain
              type="success"
              :icon="Pointer"
              @click="toggleLike(scope.row)"
          >
            {{ isFileLike(scope.row.fileId) ? '已点赞' : '点赞' }}
          </el-button>
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
import {Pointer, Search, Star} from '@element-plus/icons-vue'
import {get, post} from "@/net";
import {computed, onMounted, reactive, ref} from "vue";
import {ElMessage} from "element-plus";

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
    userName: ''
  }]
})
const favouriteFiles = reactive({
  data: [{
    ffId: '',
    fileId: '',
    userName: ''
  }]
})
const likeFile = reactive({
  data: [{
    lfId: '',
    fileId: '',
    userName: ''
  }]
})
const iframeSrc = ref('');
const pdfUrl = 'src/assets/pdfFiles/';


onMounted(() => {
  // 调用接口，获取列表数据
  get('/api/auth/files?filename= ',(data) =>{
    searchResults.data = data
  })
  get('/api/auth/getFFile',(data) =>{
    favouriteFiles.data = data
  })
  get('/api/auth/getLFile',(data) =>{
    likeFile.data = data
  })
})
const searchByText = () => {
  get(`/api/auth/files?filename=${searchText.value}`, (data) => {
    searchResults.data = data
  }, () =>{
    ElMessage.error()
  })
};
const refreshTable = () => {
  // 调用接口，获取列表数据
  get(`/api/auth/files?filename=${searchText.value}`, (data) => {
    searchResults.data = data
  }, () =>{
    ElMessage.error()
  })
};
const refreshTableAfterF = () => {
  // 调用接口，获取列表数据
  get(`/api/auth/files?filename= `, (data) => {
    searchResults.data = data
  }, () =>{
    ElMessage.error()
  })
  get('/api/auth/getFFile',(data) =>{
    favouriteFiles.data = data
  })
  get('/api/auth/getLFile',(data) =>{
    likeFile.data = data
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

const isFileFavorite = (fileId) => {
  return favouriteFiles.data.some((file) => file.fileId === fileId);
};

const findFfIdByFileId = (fileId) => {
  const foundFile = favouriteFiles.data.find(file => file.fileId === fileId);
  return foundFile ? foundFile.ffId : null;
};
const addToFavourites = (fileId) => {
  const fileToAdd = searchResults.data.find((file) => file.fileId === fileId);
  if (fileToAdd) {
    favouriteFiles.data.push(fileToAdd);
    post('api/auth/favoriteFile', {
      id: fileId
    }, () => {
      ElMessage.success('收藏成功！');
      fileToAdd.isFavorite = true;
      refreshTableAfterF();
    });
  }
};
const removeFromFavourites = (fileId) => {
  favouriteFiles.data = favouriteFiles.data.filter((file) => file.fileId !== fileId);
  post('api/auth/undoFavorite', {
    id: fileId
  }, () => {
    ElMessage.success('取消收藏成功！');
    refreshTableAfterF();
  });
};
const toggleFavorite = (row) => {
  const fileId = row.fileId;
  const ffId = findFfIdByFileId(fileId);
  const isFavorite = isFileFavorite(fileId);

  if (isFavorite) {
    removeFromFavourites(ffId);
  } else {
    addToFavourites(fileId);
  }
};

const isFileLike = (fileId) => {
  return likeFile.data.some((file) => file.fileId === fileId);
};
const findLfIdByFileId = (fileId) => {
  const foundFile = likeFile.data.find(file => file.fileId === fileId);
  return foundFile ? foundFile.lfId : null;
};
const addToLike = (fileId) => {
  const fileToAdd = searchResults.data.find((file) => file.fileId === fileId);
  if (fileToAdd) {
    likeFile.data.push(fileToAdd);
    post('api/auth/likeFile', {
      id: fileId
    }, () => {
      ElMessage.success('点赞成功！');
      fileToAdd.isFavorite = true;
      refreshTableAfterF();
    });
  }
};
const removeFromLike = (fileId) => {
  likeFile.data = likeFile.data.filter((file) => file.fileId !== fileId);
  post('api/auth/undoLike', {
    id: fileId
  }, () => {
    ElMessage.success('取消点赞成功！');
    refreshTableAfterF();
  });
};
const toggleLike = (row) => {
  const fileId = row.fileId;
  const lfId = findLfIdByFileId(fileId);
  const isLike = isFileLike(fileId);

  if (isLike) {
    removeFromLike(lfId);
  } else {
    addToLike(fileId);
  }
};
</script>

<style scoped>

</style>