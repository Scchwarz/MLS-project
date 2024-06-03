<template>
  <el-container class="layout-container" style="height: 100vh">
    <el-aside width="200px" style="height: 100%">
      <el-scrollbar>
        <el-menu
            active-text-color="#ffd04b"
            background-color="#545c64"
            text-color="#fff"
            style="height: 100vh"
        >
          <el-menu-item index="1" @click="router.push('/manager')">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="2" @click="router.push('/check')">
            <el-icon><FolderChecked /></el-icon>
            <span>审核文件</span>
          </el-menu-item>
          <el-menu-item index="3" @click="router.push('/file-manage')">
            <el-icon><FolderOpened /></el-icon>
            <span>文件管理</span>
          </el-menu-item>
          <el-menu-item index="4" @click="router.push('/user-manage')">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
        </el-menu>
      </el-scrollbar>
    </el-aside>


    <el-container>
      <el-header style="text-align: right; font-size: 12px">
        <div class="title" style="text-align: left; float: left;">
          <el-text>后台管理员系统</el-text>
        </div>
        <div class="toolbar" style="float: right;">
          <el-dropdown>
            <el-icon style="margin-right: 8px; margin-top: 1px"
            ><setting
            /></el-icon>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="userLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main>
        <router-view v-slot="{ Component }">
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import router from "@/router";
import {FolderChecked, FolderOpened, House, Setting, User} from '@element-plus/icons-vue'
import {logout} from "@/net";

function userLogout() {
  logout(() => router.push("/"))
}

</script>

<style scoped>
.layout-container .el-header {
  position: relative;
  background-color: var(--el-color-success-light-7);
  color: var(--el-text-color-primary);
}
.layout-container .el-aside {
  color: var(--el-text-color-primary);
  background: var(--el-color-success-dark-2);
}
.layout-container .el-menu {
  border-right: none;
}
.layout-container .el-main {
  padding: 0;
}
.layout-container .title{
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  right: 20px;
}
.layout-container .toolbar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  right: 20px;
}
</style>