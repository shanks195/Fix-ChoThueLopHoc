<template>
  <div id="priceSum">
    <div class="row">

      <div v-for="cont in content" :key="cont" class="col-3 col-s-12">
        <img :src="cont.link" alt="" @click="addClickScliderImage">
        <h3 slot="name"  v-html="cont.name">{{ cont.name }}</h3>
        <h6 slot="title"  v-html="cont.title">{{ cont.title }}</h6>
        <div class="text-align" slot="description"  v-html="cont.description">
         {{ cont.description }}
          
        </div>
        <div class="slider-image">
          <div class="icon cancel-btn" @click="removeClickScliderImage">
            <svg-icon icon-class="cancel" width="50%" />
          </div>
          <div class="slider container">
          <el-carousel :interval="2000" arrow="always">
              <el-carousel-item v-for="item in content" :key="item.id">
                 <img :src="item.link">
              </el-carousel-item>
            </el-carousel>
          </div>
        </div>
      </div>

      

    </div>
    <p class="button">
        <router-link to="/contact">Tư vấn ngay</router-link>
      </p>
  </div>
</template>
<script>
import { mapGetters } from 'vuex'
import Carousel from '../../components/Client/Carousel'
import CarouselSlide from '../../components/Client/CarouselSlide'
export default {
  name: 'PagePriceSum',
  components: {
    Carousel,
    CarouselSlide
  },
  data() {
    return {
      content: [],
      
      visibleSlide: 0
    }
  },
  computed: {
    ...mapGetters([
      'baseApi',
      
    ]),
    slideLen() {
      return this.slides.length
    }
  },
  created() {
    fetch('http://thuephonghoc.vn/rentroom/api/price')
      .then(Response => {
        return Response.json()
      })
      .then(data => {
        this.content = data.content;
        
        // console.log(this.slides)
        // //console.log(this.content)
      })
  },
  methods: {
    addCLickButton() {
      const ScriptContent = document.querySelector('.script-content')
      ScriptContent.classList.add('active')
    },
    removeCLickButton() {
      const ScriptContent = document.querySelector('.script-content')
      ScriptContent.classList.remove('active')
    },
    addClickScliderImage() {
      const SliderImage = document.querySelector('.slider-image')
      SliderImage.classList.add('active')
    },
    removeClickScliderImage() {
      const SliderImage = document.querySelector('.slider-image')
      SliderImage.classList.remove('active')
    },
    next() {
      if (this.visibleSlide >= this.slideLen - 1) {
        this.visibleSlide = 0
      } else {
        this.visibleSlide++
      }
    },
    prev() {
      if (this.visibleSlide <= 0) {
        this.visibleSlide = this.slideLen - 1
      } else {
        this.visibleSlide--
      }
    }
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
  /* slider */
 
  .slider-image {
    display: none;
    background: #cbd8e2ef;
    left: 0;
    position: fixed;
    width: 100%;
    height: 200vh;
    top: 0%;
    z-index: 99999;
  }

  .slider-image.active {
    display: block;
  }
  #priceSum  .col-3.col-s-12 h3 {
    width:100%;
  }
 #priceSum  .col-3.col-s-12 h6 {
   margin-top: 2%;
 }
.text-align p {
  margin-top: 2%;
}
  .slider-image .icon.cancel-btn {
    width: 100%;
    display: inline-block;
  }

  .slider-image .icon.cancel-btn img {
    width: 4% !important;
    float: right;
    padding-right: 3%;
  }

  /* Position the image container (needed to position the left and right arrows) */
  .slider.container {
    position: relative;
    width: 1140px;
    margin: 0 auto;
  }

  .slider img {
    width: 100%;
    height: 400px;
  }

  /* end slider */
  
#priceSum p.button {
      position: absolute;
    top: 700px;
    left: 40%;
    text-align: center;
    border-radius: 16px;
    background: black;
    width: 15%;
    color: white;
    padding: 1%;
} 
#priceSum .row {
  overflow: hidden;
  position:relative;
}
  #priceSum .row .col-3 img {
    height:50%;
    border-radius: 16px;
    position: absolute;
    top:0;
    left:2%;
    width: 96%;
  }

  #priceSum .row .col-3 h6 {
    font-size: 16px;
    
  }

  #priceSum .row .col-3 h3 {
    font-size: 22px;
    margin-top: 80%;
   
  }

  #priceSum {
    width: 100%;
    
  }

  #priceSum .row .col-3 {
    border-radius: 16px;
    padding: 0px 2%;
    position: relative;
    height: 450px;
    margin-right: 2%;
    margin-top: 2%;
    width: 30%;
    display: inline-block;
    float: left;
    background-color: white;
  }

  .script-content .row .col-3 img {
    width: 50% !important;
    margin-left: 30%;
  }

  .script-content .row .col-3 {
    height: 30px !important;
  }

  .script-content {
    z-index: 100;
    top: 830px;
    left: 40%;
    position: absolute;
    border: 1px solid rgb(218, 183, 183);
    display: none;
    margin-top: -10%;
    width: 20%;
    background: white;
    height: 50px;
    box-sizing: border-box;
  }

  .icon.cancel-btn {
    box-sizing: border-box;
    padding-top: 3%;
    margin-left: 2%;
  }

  #priceSum .row {
    position: relative;
    margin-left: 2%;
  }

  #priceSum .row button {
    position: absolute;
    top: 430px;
    left: 40%;
    border-radius: 16px;
    width: 20%;
    padding: 2%;
    background: #0066ff;
    color: white;
    border: none;
  }

  #priceSum .col-3 {
    position: relative;
  }

  #priceSum .script-content.active {
    z-index: 100;
    display: flex;
  }
 .slider.container .el-carousel {
    width: 100%;
    height: 508px;
}

.el-carousel__item.is-animating {}

.el-carousel__item img {
    height: 330px;
}

.el-carousel__item {
    height: 500px;
    border-radius:16px
}
.el-carousel__item.is-animating img {
    height: 550px!important;
}

.slider.container .el-carousel.el-carousel--horizontal .el-carousel__container {
    height: 600px!important;
}
.icon.cancel-btn {
    display: inline-block;
    float: right;
    width: 20%!important;
    margin-bottom: 5%;
}
svg.svg-icon {
    position: absolute;
    top: 4%;
    z-index: 999999;
}
  @media only screen and (max-width: 868px) {
    .slider.container {
      width: 100%;
    }
    .el-carousel__item {
      width: 100%;
    } 
    .slider img {
      width: 100%;
      height: 400px;
    }
#priceSum p.button {
  display: inline-block;
  
  top:1900px;
}
    #priceSum {
    width: 100%;
    
}

    #priceSum .col-3.col-s-12 {
      width: 100%;
      height: 600px;
      margin-bottom: 2%;
    }

    #priceSum .row {
      overflow: hidden;
      position: relative;
    }

    .script-content {
      z-index: 100;
      top: 3500px;
      left: 40%;
      position: absolute;
      border: 1px solid rgb(218, 183, 183);
      display: none;
      margin-top: -10%;
      width: 20%;
      background: white;
      height: 50px;
      box-sizing: border-box;
    }

    .script-content .col-3.col-s-3 img {
      margin-top: 30% !important;
      width: 60% !important;
    }

    /* //slide */
    .carousel {
      margin: 1%;
      position: relative;
      width: 60%;
      height: 400px;
      overflow: hidden;
    }

    button {
      z-index: 100000;
      position: absolute;
      height: 100px;
      width: 100px;
      top: 30%;
      background-color: rgba(0, 0, 0, 0.8);
      border: none;
      color: white;
    }

    button:focus,
    button:hover {
      outline: none;
      cursor: pointer;
    }

    .next {
      right: 0;
    }

    .prev {
      left: 0;
    }
  }
  


</style>
