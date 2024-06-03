<template>
    <div class="main">
      <div
        class="dropzone-container"
        @dragover="dragover"
        @dragleave="dragleave"
        @drop="drop"
      >
        <input
          type="file"
          multiple
          name="file"
          id="fileInput"
          class="hidden-input"
          @change="onChange"
          ref="file"
          accept=".pdf"
        />
   
        <label for="fileInput" class="file-label">
          <div v-if="isDragging">释放pdf文件来上传</div>
          <div v-else>拖动文件到这或 <u>点击</u> 来上传.</div>
        </label>
      </div>
    </div>
    <div class="preview-container mt-4" v-if="files.length">
        <div v-for="file in files" :key="file.name" class="preview-card">
            <div>
                <p>
                    {{ file.name }}
                </p>
                <p>
                    {{ file.name }} -
                    {{ Math.round(file.size / 1000) + "kb" }}
                </p>
            </div>
            <div>
                <button
                class="ml-2"
                type="button"
                @click="remove(files.indexOf(file))"
                title="Remove file"
                >
                <b>×</b>
                </button>
            </div>
        </div>
    </div>
    <div>
        <div>
         <el-button size="large" round @click="uploadFile">上传文件</el-button>
        </div>
      </div>
</template>

<script>
  export default {
    data() {
      return {
        isDragging: false,
        files: [],
      };
    },
    methods: {
      onChange() {
        this.files.push(...this.$refs.file.files);
      },
      dragover(e) {
        e.preventDefault();
        this.isDragging = true;
      },
      dragleave() {
        this.isDragging = false;
      },
      drop(e) {
        e.preventDefault();
        this.$refs.file.files = e.dataTransfer.files;
        this.onChange();
        this.isDragging = false;
      },
      remove(i) {
        this.files.splice(i, 1);
      },
      uploadFile() {
        },
    },
  };
</script>

<style scoped>
.main {
    display: flex;
    flex-grow: 1;
    align-items: center;
    height: 300px;
    justify-content: center;
    text-align: center;
}
 
.dropzone-container {
    padding: 8rem;
    background: #f7fafc;
    border: 1px solid #e2e8f0;
}
 
.hidden-input {
    opacity: 0;
    overflow: hidden;
    position: absolute;
    width: 1px;
    height: 1px;
}
 
.file-label {
    font-size: 20px;
    display: block;
    cursor: pointer;
}
 
.preview-container {
    display: flex;
    margin-top: 2rem;
}
 
.preview-card {
    display: flex;
    border: 1px solid #a2a2a2;
    padding: 5px;
    margin-left: 5px;
}
 
.preview-img {
    width: 50px;
    height: 50px;
    border-radius: 5px;
    border: 1px solid #a2a2a2;
    background-color: #a2a2a2;
}

</style>  