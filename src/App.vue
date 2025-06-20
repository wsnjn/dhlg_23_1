<!-- App.vue -->
<template>
  <div>
    <!-- 这里会根据路由显示对应的页面 -->
    <router-view />
  </div>
</template>

<script setup>
// 你也可以在这里监听路由等操作
</script>

<style scoped>
/* 局部样式 */
</style>

<style>
/* 全局样式，清除默认边距和设置背景 */
html, body {
  margin: 0;
  padding: 0;
}
#app {
  height: 100%;
}
</style>
