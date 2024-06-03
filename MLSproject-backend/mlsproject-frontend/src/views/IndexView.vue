<template>
    <el-container class="layout-container" style="height: 100vh">
      <el-aside width="200px" style="height: 100vh">
        <el-scrollbar>
          <el-menu
              style="height: 100vh">
            <el-menu-item index="1" @click="router.push('/')">
              <el-icon><House /></el-icon>
              <span>首页</span>
            </el-menu-item>
            <el-menu-item index="2" @click="router.push('/person')">
              <el-icon><IconMenu /></el-icon>
              <span>个人中心</span>
            </el-menu-item>
            <el-menu-item index="3" @click="router.push('/file')">
              <el-icon><files /></el-icon>
              <span>文件管理</span>
            </el-menu-item>
            <el-menu-item index="4" @click="router.push('/search')">
              <el-icon><DocumentCopy /></el-icon>
              <span>文档总览</span>
            </el-menu-item>
            <el-menu-item index="5" @click="router.push('/content-search')">
              <el-icon><search /></el-icon>
              <span>文档搜索</span>
            </el-menu-item>
            <el-menu-item index="6" @click="router.push('/favourite')">
              <el-icon><Star /></el-icon>
              <span>文件收藏</span>
            </el-menu-item>
            <el-menu-item index="7" @click="router.push('/like')">
              <el-icon><Pointer /></el-icon>
              <span>文件点赞</span>
            </el-menu-item>
            <el-menu-item index="8" @click="router.push('/message')">
              <el-icon><Bell /></el-icon>
              <span>审核信息</span>
            </el-menu-item>
          </el-menu>
        </el-scrollbar>
      </el-aside>


      <el-container>
        <el-header style="text-align: right; font-size: 12px">
          <div class="title" style="text-align: left; float: left;">
            <el-text size="large">智能医疗文献检索系统</el-text>
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
import { logout } from '@/net'
import { takeAccessToken } from '@/net'
import router from "@/router";
import axios from 'axios';
import { ref } from 'vue'
import {
  Bell,
  Discount,
  DocumentCopy,
  Files,
  House,
  Menu as IconMenu,
  Pointer,
  Search,
  Setting,
  Star
} from '@element-plus/icons-vue'

function userLogout() {
  logout(() => router.push("/"))
}
</script>

<style scoped>
.layout-container .el-header {
  position: relative;
  background-color: var(--el-color-primary-light-7);
  color: var(--el-text-color-primary);
}
.layout-container .el-aside {
  color: var(--el-text-color-primary);
  background: var(--el-color-primary-light-8);
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
