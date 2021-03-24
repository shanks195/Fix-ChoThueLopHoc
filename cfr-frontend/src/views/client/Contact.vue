<template>
  <div id="contact" class="paragraph-left">
    <div class="contact" v-for="cont in content" :key="cont">
      <h1>{{ cont.title }}</h1>
      <p slot="description" v-html="cont.description">{{ cont.description }}</p>
    </div>
    
    
    <iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3919.0781057524996!2d106.64215351428717!3d10.805329861615396!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x31752945af1e4587%3A0xe6607a649f8ee737!2zODkgVHLhuqduIFbEg24gRMawLCBQaMaw4budbmcgMTMsIFTDom4gQsOsbmgsIFRow6BuaCBwaOG7kSBI4buTIENow60gTWluaCwgVmnhu4d0IE5hbQ!5e0!3m2!1svi!2s!4v1614053182377!5m2!1svi!2s" width="100%" height="450" style="border:0;" allowfullscreen="" loading="lazy" />
    <p>Để đặt lịch hẹn xem phòng học hoặc đặt thuê phòng học, bạn đừng ngần ngại liên hệ theo thông tin bên dưới.</p>
    <form >
      <div class="name"><label for="">Họ Tên:</label></div>
      <input id="name" type="text" name="fullname" v-model="posts.fullname" @change="postData(e)" size="50" placeholder="Vui lòng nhập Ho tên" required>
      <div class="email"><label for="">Email:</label></div>
      <input id="email" type="email" name="email" v-model="posts.email" placeholder="Vui lòng ghi địa chỉ email chính xác để chúng tôi có thể liên lạc với bạn và gửi lịch thuê phòng chi tiết." @change="postPost(e)" required>
      <div class="sdt"><label for="">Điện Thoại</label></div>
      <input id="number"  name="phone" type="tel"  v-model="posts.phone" placeholder="123-45-678"  @change="postData(e)" required>
      <div class="hour"><label for="">Thời gian muốn đặt lịch:</label></div>
      <input id="hour" type="text" name="schedule"  v-model="posts.schedule" placeholder="Vui lòng ghi rõ thứ, ngày,tháng,khung giờ,số người tham dự,máy chiếu,TV,micro..."  @change="postData(e)" required>
      <div class="order"><button class="orderbtn">Đặt lịch</button></div>
    </form>

  </div>
</template>
<script>
import axios from 'axios';

export default {
  name: 'PageContact',
  components: {

  },
  data() {
    return {
      content: [],
      posts :{
        id: '',
        fullname: '',
        email: '',
        phone: '',
        schedule: ''
      }
      
      
    }
  },
   // Gửi request lên server khi mà postPost() được gọi
  
  created() {
    fetch('http://thuephonghoc.vn/rentroom/api/contact')
      .then(Response => {
        return Response.json()
      })
      .then(data => {
        this.content = data.content
      
      })
  },
  methods: {
    postData(e){
      axios.post("http://thuephonghoc.vn/rentroom/api/contactclient",this.posts)
      .then(()=> {
      
        alert("Ban da dat lich thanh cong!!!")
      })
      e.preventDefault();
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
input#name,input#email,input#number,input#hour {
    width: 100%;
    height: 33px;
}
.paragraph-left h1 {
    margin-bottom: 1%;
}

.paragraph-left p {
    margin-bottom: 2%;
    margin-top: 1%;
}
form label {
    display: inline-block;
    margin-bottom: 1%;
    margin-top: 1%;
}
button.orderbtn {
    padding: 5px;
    margin-top: 2%;
}
input:focus {
    box-shadow: 0 0 5px #9ed4ff;
    border: 1px solid #558ab7;
    outline: none;
}
button.orderbtn {
    padding: 1%;
    color: white;
    background: black;
    border: none;
    border-radius: 3px;
}
input {
    padding-left: 2%;
    border: 1px solid #ccc;
    height: 54px!important;

}

</style>
