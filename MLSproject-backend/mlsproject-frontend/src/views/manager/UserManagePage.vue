<template>
  <div>
    <el-input
        size="large"
        placeholder="请输入检索的用户名"
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
      <el-table-column prop="id" label="用户编号" width="150" />
      <el-table-column prop="username" label="用户名" width="200" />
      <el-table-column prop="email" label="邮箱地址" width="150" />
      <el-table-column prop="phone" label="电话号码" width="100" />
<!--      <el-table-column label="注册时间" width="200">-->
<!--        <template #default="{ row }">-->
<!--          {{ formattedDataMap[row.id].formattedUploadTime }}-->
<!--        </template>-->
<!--      </el-table-column>-->
      <el-table-column prop="registerTime" label="注册时间" width="200" />
      <el-table-column prop="role" label="权限" width="150" />
      <el-table-column fixed="right" label="注销账号" width="165">
        <template #default="scope">
          <el-button link type="danger" @click="deleteAccount(scope)">注销</el-button>
        </template>
      </el-table-column>
      <el-table-column label="修改权限" width="150" >
        <template #default="scope">
          <el-button text @click="updateRole(scope)">修改权限</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        background
        layout="prev, pager, next"
        style="margin-top: 15px"
    ></el-pagination>
  </div>
</template>
<script setup>
import { Search } from '@element-plus/icons-vue'
import {get, post} from "@/net";
import {computed, onMounted, reactive, ref} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";

const searchText = ref()
const searchResults = reactive({
  data: [{
    id: '',
    username: '',
    email: '',
    role: '',
    registerTime: '',
    phone: '',
    avatarUrl: '',
  }]
})


onMounted(() => {
  // 调用接口，获取列表数据
  get('/api/auth/manager/accounts?accountName= ',(data) =>{
    searchResults.data = data
  })
})
const searchByText = () => {
  get(`/api/auth/manager/accounts?accountName=${searchText.value}`, (data) => {
    searchResults.data = data
  }, () =>{
    ElMessage.error()
  })
};
const refreshTable = () => {
  // 调用接口，获取列表数据
  get(`/api/auth/manager/accounts?accountName= `, (data) => {
    searchResults.data = data
  }, () =>{
    ElMessage.error()
  })
};
const deleteAccount = (scope) =>{
  post('api/auth/manager/deleteAccount', {
    id: scope.row.id
  },() => {
    ElMessage.success('注销成功！')
    refreshTable()
  })
};
const updateRole = (scope) => {
  ElMessageBox.confirm(
      '是否确定修改用户权限?',
      '提示',
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
  )
      .then(() => {
        let newRoleValue;

        switch (scope.row.role) {
          case 'user':
            newRoleValue = 'manager'; // 设置新的角色值，根据实际需求修改
            break;
          case 'manager':
            newRoleValue = 'user';
            break;

          default:
            newRoleValue = 'user';
            break;
        }

        post('api/auth/manager/updateRole', {
          id: scope.row.id,
          role: newRoleValue
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



const formattedDataMap = computed(() => {

  return searchResults.data.reduce((map, item) => {
    const rawRegisterTime = item.registerTime;
    const RegisterTime = new Date(rawRegisterTime);

    // 使用适当的日期格式化选项
    const options = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
    const formattedRegisterTime = RegisterTime.toLocaleDateString('en-US', options);

    map[item.id] = {
      formattedRegisterTime
    };

    return map;
  }, {});
});

</script>

<style scoped>

</style>