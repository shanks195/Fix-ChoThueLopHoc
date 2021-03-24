<template>
  <div class="app-container">
    <p class="warn-content">
      Markdown based
    </p>
    <mavon-editor ref="md" :style="'height:' + height" @imgAdd="imgAdd"  :xssOptions="xssOptions"  language="en"/>
  </div>
</template>

<script>
import { upload } from '@/utils/upload'
import { mapGetters } from 'vuex'

export default {
  name: 'Markdown',
  data() {
    return {
      xssOptions: {
        whiteList: {
          img: ["src", "alt", "width", "height"],
        },
        stripIgnoreTagBody: true
      },
      height: document.documentElement.clientHeight - 200 + 'px'
    }
  },
  computed: {
    ...mapGetters([
      'imagesUploadApi'
    ])
  },
  mounted() {
    const that = this
    window.onresize = function temp() {
      that.height = document.documentElement.clientHeight - 200 + 'px'
    }
  },
  methods: {
    imgAdd(pos, $file) {
      upload(this.imagesUploadApi, $file).then(data => {
        this.$refs.md.$img2Url(pos, data.data.url)
      })
    }
  }
}
</script>

<style scoped>
</style>
