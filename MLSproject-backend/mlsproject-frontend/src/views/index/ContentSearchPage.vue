<template>
  <div>
    <el-input
        size="large"
        placeholder="请输入要检索的关键字"
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
      <el-table-column prop="id" label="文件编号" width="150" />
      <el-table-column prop="title" label="文件名" width="300">
        <template #default="{ row }">
          <!-- 使用 v-html 渲染包含高亮标签的内容 -->
          <div v-html="highlightText(row.title)"></div>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="文件内容" width="500">
        <template #default="{ row }">
          <!-- 使用 v-html 渲染包含高亮标签的内容 -->
          <div v-html="highlightText(row.content)"></div>
        </template>
      </el-table-column>
      <el-table-column prop="downloadCounts" label="下载数量" width="150" />
      <el-table-column prop="favCounts" label="收藏数量" width="150" />
      <el-table-column prop="likeCounts" label="点赞数量" width="150" />
      <el-table-column label="上传时间" width="250">
        <template #default="{ row }">
          {{ formattedDataMap[row.id].formattedUploadTime }}
        </template>
      </el-table-column>
      <!-- 新增的查看详情按钮列 -->
      <el-table-column fixed="right" label="操作" width="150">
        <template #default="{ row }">
          <!-- 使用 el-button 按钮触发导航到 FileView 组件 -->
          <el-button size="small" type="text" @click="navigateToFileView(row)">查看详情</el-button>
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
            {{ isFileFavorite(scope.row.id) ? '已收藏' : '收藏' }}
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
            {{ isFileLike(scope.row.id) ? '已点赞' : '点赞' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import {Pointer, Search, Star} from '@element-plus/icons-vue'
import {get, post} from "@/net";
import {computed, onMounted, reactive, ref} from "vue";
import {ElMessage} from "element-plus";
import {useRouter} from "vue-router";

const searchText = ref()
const searchResults = reactive({
  data: [{
    id: '',
    title: '',
    content: '',
    registerTime: '',
    likeCounts: '',
    favCounts: '',
    downloadCounts: '',
    checked: '',
    state: ''
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

// 使用 useRouter 获取路由对象
const router = useRouter();

// 自定义方法，用于导航到 FileView 组件
const navigateToFileView = (row) => {
  // 直接使用 router.push 进行程序化导航
  router.push({ name: 'FileView', params: { id: row.id, content: row.content} });
};
const highlightText = (text) => {
  // 使用正则表达式替换<em>为实际的HTML高亮标签
  return text.replace('<em>', '<span style=\'color:red\'>').replace('</em>', '</span>');
};

// 模拟从Elasticsearch获取高亮信息的操作
onMounted(() => {
});
const searchByText = () => {
  get(`api/auth/fileMatchingQuery/${searchText.value}`, (data) => {
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
// 计算属性：格式化文件大小和上传时间的映射
const formattedDataMap = computed(() => {
  return searchResults.data.reduce((map, item) => {
    const rawUploadTime = item.uploadTime;
    const uploadTime = new Date(rawUploadTime);

    // 使用适当的日期格式化选项
    const options = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
    const formattedUploadTime = uploadTime.toLocaleDateString('en-US', options);

    map[item.id] = {
      formattedUploadTime
    };

    return map;
  }, {});
});
const refreshTableAfterF = () => {
  // 调用接口，获取列表数据
  get('/api/auth/getFFile',(data) =>{
    favouriteFiles.data = data
  })
  get('/api/auth/getLFile',(data) =>{
    likeFile.data = data
  })
  get(`api/auth/fileMatchingQuery/${searchText.value}`, (data) => {
    searchResults.data = data
  }, () =>{
    ElMessage.error()
  })
};
const isFileFavorite = (fileId) => {
  return favouriteFiles.data.some((file) => file.fileId === fileId);
};

const findFfIdByFileId = (fileId) => {
  const foundFile = favouriteFiles.data.find(file => file.fileId === fileId);
  return foundFile ? foundFile.ffId : null;
};
const addToFavourites = (fileId) => {
  const fileToAdd = searchResults.data.find((file) => file.id === fileId);
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
  const fileId = row.id;
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
  const fileToAdd = searchResults.data.find((file) => file.id === fileId);
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
  const fileId = row.id;
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