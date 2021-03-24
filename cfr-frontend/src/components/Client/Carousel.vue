<template>
  <div class="carousel">
    <slot />
    <button class="next" @click="next">Next</button>
    <button class="prev" @click="prev">Prev</button>
  </div>
</template>
<script>
export default {
  name: 'Carousel',
  components: {

  },
  data() {
    return {

    }
  },
  created() {

  },
  methods: {
    next() {
      this.$emit('next')
    },
    prev() {
      this.$emit('prev')
    }

  }
}
</script>
<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Poppins', sans-serif;
}
    .carousel {
        position: relative;
        width:100% ;
        height: 400px;
        overflow: hidden;
    }
    button {
        z-index: 100000;
        position:absolute;
        height: 100px;
        width:100px;
        top:30%;
        background-color: rgba(0,0,0,0.8);
        border:none;
       color:white;
    }
    button:focus, button:hover {
          outline: none;
          cursor:pointer;
    }
    .next {
        right:0;
    }
    .prev {
        left: 0;
    }

</style>
