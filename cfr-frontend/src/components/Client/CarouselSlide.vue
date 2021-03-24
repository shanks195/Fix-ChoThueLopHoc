<template>
  <transition name="left">
    <div v-show="visibleSlide === index" class="carousel-slide">
      <slot />
    </div>
  </transition>
</template>
<script>
export default {
  props: ['visibleSlide', 'index'],
  data() {
    return {

    }
  }
}
</script>
<style scoped>
*{
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Poppins', sans-serif;
}
    .carousel-slide {
        position: relative;
        top: 0;
        left: 0;
        right: 0;
    }
    .left-enter-active {
        animation:leftInAnimation 0.4s ease-in-out;
    }
    .left-leave-active {
        animation:leftOutAnimation 0.4s ease-in-out;
    }

    @keyframes leftInAnimation {
        from{
            transform: translateX(100%);
        }
        to {
            transform: translateX(0);
        }

    }
    @keyframes leftOutAnimation {
         from{
            transform: translateX(0);
        }
        to {
            transform: translateX(-100%);
        }
    }

      @media only screen and (max-width: 868px) {
        .carousel {
        position: relative;
        width:100% ;
        height: 400px;
        overflow: hidden;
    }
    .carousel-slide {
        width: 100%;
        position: relative;
        top: 0;
        left: 0;
        right: 0;
    }
    }
</style>
