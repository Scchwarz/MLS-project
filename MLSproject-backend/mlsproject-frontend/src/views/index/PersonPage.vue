<template>
  <div class="profile">
    <el-card class="profile-card">
      <div slot="header" class="profile-header">
        <el-avatar :src="formData.avatarUrl" size="large"></el-avatar>
        <div class="profile-name">{{ formData.username }}</div>
      </div>
      <div class="profile-body">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="个人资料">
            <el-form :model="formData" ref="form" label-width="100px">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="formData.username" disabled></el-input>
              </el-form-item>
              <el-form-item label="用户ID" prop="id">
                <el-input v-model="formData.id" disabled></el-input>
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="formData.email"></el-input>
              </el-form-item>
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="formData.phone"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitForm()">保存</el-button>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="uploadAvatar('form')">上传头像</el-button>
                <div class="profile-none1"></div>
                <input type="file" ref="fileInput" @change="handleFileChange" />
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted, computed} from 'vue'
import {get, post, takeAccessToken} from "@/net";
import {ElMessage} from "element-plus";
import axios from "axios";
import router from "@/router";


const loading = ref(false) // 是否显示加载效果
const avatarUrl= 'src/assets/avatar'

let formData = reactive({
    id: '',
    username: '',
    email: '',
    phone: '',
    avatarUrl: '',
    role: ''
});


const activeTab= '0'


function handleFileChange(event) {
  file.value = event.target.files[0];
}
const submitForm = () => {
  // 调用接口，提交表单数据
  post('/api/auth/user/update', formData,() => {
    ElMessage.success('保存成功')
  })
}

const file = ref(null);

function uploadAvatar() {
  let formData = new FormData();
  formData.append('file', file.value);

  axios.post('/api/auth/user/uploadAvatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
      'Authorization': `Bearer ${takeAccessToken()}`,
    },
  })
      .then(response => {
        ElMessage.success("头像上传成功")
        console.log('Avatar uploaded successfully:', response);
      })
      .catch(error => {
        ElMessage.error("发生了一些错误")
        console.error('File upload failed:', error);
      });
}



onMounted(() => {
  // 调用接口，获取列表数据
  get('/api/auth/user/thisUser',(data) =>{
    formData.id = data.id;
    formData.username = data.username;
    formData.email = data.email;
    formData.phone = data.phone;
    formData.avatarUrl = data.avatarUrl;
    formData.role = data.role;
  })
})
</script>

<script>

</script>

<style scoped>
.profile {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.profile-card {
  width: 1500px;
}

.profile-header {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
}

.profile-name {
  font-size: 24px;
  margin-left: 20px;
}

.profile-body {
  padding: 20px;
}

.profile-none {
  margin-top: 15px;
}

.profile-none1 {
  margin-left: 15px;
}
</style>