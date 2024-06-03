<template>
  <div>
    <div class="title1">预览</div>
    <iframe height="1000" :src="iframeSrc" width="1000"></iframe>
  </div>
</template>

<script setup>
import {onMounted, ref} from "vue";
import {useRoute} from "vue-router";


const url = ref('')
const pdfUrl = 'src/assets/pdfFiles/';
const route = useRoute();
const iframeSrc = ref('')

onMounted(() => {
  url.value = route.params.url;
  iframeSrc.value = `./src/assets/pdfFiles/${url.value}`;
});
</script>

<style scoped>

</style>