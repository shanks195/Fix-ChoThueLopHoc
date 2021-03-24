<template>
  <div id="index">
    <div class="index">
      <div v-for="cont in content" :key="cont.id" class="index">
        
          <p slot="name" v-html="cont.name">{{ cont.name }}</p>
          <p slot="title" v-html="cont.title">{{ cont.title }}</p>
        
        
        <img :src="cont.link" alt="">
        
      </div>
    </div>
    

  </div>
</template>
<script>
export default {
  name: 'PageIndexClient',
  data() {
    return {
      content: []
    }
  },
  created() {
    fetch('http://thuephonghoc.vn/rentroom/api/home')
      .then(Response => {
        return Response.json()
      })
      .then(data => {
        this.content = data.content
        // console.log(this.content)
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
  span.paragraph {
    font-size: 14px;
    display: inline-block;
    text-align: center;
    margin-top: 2%;
    margin-bottom: 2%;
  }

  h5.red {
    text-align: justify;
    color: red;
    font-size: 18px;
  }

  #index h6 {
    color: red;
    font-size: 18px;
  }

  #index img {
    height: auto;
    width: 100%;
  }

  span.paragraph-horizontal {
    text-align: justify;
    display: inline-block;
  }

  #index {
    text-align: center;
  }

  span.paragraph {
    font-size: 16px;
    display: inline-block;
    text-align: center;
    margin-top: 2%;
  }

  #index h6 {
    color: red;
    font-size: 16px;
  }

  #index img {
    height: auto;
    width: 100%;
  }

  span.paragraph-horizontal {
    text-align: justify;
    display: inline-block;
  }

  .paragraph-left {
    text-align: left;
  }

  h6.blue {
    color: blue !important;
  }

  h5.blue {
    color: #1f37dd;
    font-weight: 700;
    font-size: 18px;
  }

  h4.blue {
    color: blue;
  }

</style>
