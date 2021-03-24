<template>
  <div id="Introduce" class="paragraph-left">
    <div v-for="cont in content" :key="cont.id" class="introduce">
      <h1 slot="name" v-html="cont.name">{{ cont.name}}</h1>
      <p slot="title"  v-html="cont.title"> {{ cont.title }}</p>
      <p slot="description" v-html="cont.description" >{{ cont.description }}</p>
      <img :src="cont.link" alt="">
    </div>

  </div>
</template>
<script>
export default {
  name: 'PageIntroduce',
  data() {
    return {
      content: []
    }
  },
  created() {
    fetch('http://thuephonghoc.vn/rentroom/api/introduce')
      .then(Response => {
        return Response.json()
      })
      .then(data => {
        this.content = data.content
      })
  },
  methods: {

  }
}
</script>
<style scoped>
*{
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Open Sans', sans-serif;
}
#Introduce img {
    margin-bottom: 2%;
    margin-top: 2%;
}

.introduce p {
    margin-top: 2%;
}
.paragraph-left {
    text-align: left;
}
#Introduce img {
    height: auto;
    width: 100%;
}
</style>
