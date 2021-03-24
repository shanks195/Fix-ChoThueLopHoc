<template>
  <div id="clientLayout">
    <nav class="navbar">
      <div class="content">
        <div class="logo"><router-link to="/"><img src="../../assets/images/client/logo/logo.jpg" alt=""></router-link></div>
        <ul class="menu-list">
          <div class="icon cancel-btn" @click="removeClickSo">
            <svg-icon icon-class="cancel" width="30%" />
          </div>
          <li><router-link to="/introduce">Giới thiệu</router-link></li>
          <li><router-link to="/price-sum">Bảng Giá</router-link></li>
          <li><router-link to="/contact">Liên hệ</router-link></li>

        </ul>
        <div class="icon menu-btn" @click="addCLickso">
          <svg-icon icon-class="menu-client" width="30%" />
        </div>
      </div>
    </nav>
    <div class="about">
      <div class="content">
        <router-view />
      </div>
    </div>
    <span v-for="cont in content" :key="cont.id">
      <div class="footer content">
        <div class="col-4 col-s-12">
          <h5>Thông tin liên hệ</h5>
          <p><b>Địa chỉ:  </b><span>89 Trần Văn Dư, P.13,Q.Tân Bình,TP HCM</span></p>
          <p><b>Gmal: </b><span class="sdt">{{cont.email}}</span><span>( Tư vấn 24/7)</span></p>

          <p><b>Hotline tư vấn: </b><span class="sdt">{{cont.phone}}</span><span>( Tư vấn 24/7)</span></p>
        </div>
        <div class="col-3 col-s-12">
          <h5>Nhận báo giá</h5>
          <input id="" type="email" name="email" v-model="posts.email" placeholder="Nhận Email cua bạn" @change="postData(e)">
          <button >Nhận báo giá và hình ảnh</button>
        </div>

      </div>
      <div class="call-user">
        <p>Gọi để xem và đặt phòng <button>{{cont.phone}}</button></p>
      </div>
      <go-top></go-top>
    </span>
    
  </div>
</template>
<script>
import axios from 'axios';
import GoTop from '@/components/Scroll/GoTop'
export default {
  name: 'ClientLayoutPage',
  components: {
      GoTop
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
      .then((result)=> {
      
        alert("Ban da gửi thành công!!!")
      })
      e.preventDefault();
    },
    addCLickso() {
      const body = document.querySelector('body')

      const menu = document.querySelector('.menu-list')
      const menuBtn = document.querySelector('.menu-btn')
      const cancelBtn = document.querySelector('.cancel-btn')

      menu.classList.add('active')
      menuBtn.classList.add('hide')
      cancelBtn.classList.add('show')
      body.classList.add('disabledScroll')
    },
    removeClickSo() {
      const body = document.querySelector('body')
      const menu = document.querySelector('.menu-list')
      const menuBtn = document.querySelector('.menu-btn')
      const cancelBtn = document.querySelector('.cancel-btn')
      menu.classList.remove('active')
      menuBtn.classList.remove('hide')
      cancelBtn.classList.remove('show')
      body.classList.remove('disabledScroll')
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

html {
  scroll-behavior: smooth;
}

body {
  margin:0;
}
/* custom scroll bar */
::-webkit-scrollbar {
    width: 10px;
}
::-webkit-scrollbar-track {
    background: #f1f1f1;
}
::-webkit-scrollbar-thumb {
    background: #888;
}
::selection{
  background: rgb(0,123,255,0.3);
}
.content{
  max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
}
.navbar{
    background-color: #ffffff;
    position: fixed;
    z-index: 5;
    width: 100%;
    padding: 6px 0;
    transition: all 0.3s ease;

}
.logo img {
    width: 33%;
}
.navbar.sticky{
  padding: 10px 0;
  background: #f8f5f5;
  box-shadow: 0px 3px 5px 0px rgba(0,0,0,0.1);
}
.navbar .content{
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.navbar .logo a{
  color: rgb(3, 2, 2);
  font-size: 30px;
  font-weight: 600;
  text-decoration: none;
}
.navbar .menu-list{
  display: inline-flex;
}
.menu-list li{
  list-style: none;
}
.menu-list li a{
 color: rgb(3, 2, 2);
  font-size: 18px;
  font-weight: 500;
  margin-left: 25px;
  text-decoration: none;
  transition: all 0.3s ease;
}
.menu-list li a:hover{
  color: #007bff;
}

.about {
    
    padding: 147px 0;
    background: rgb(245, 240, 240);
}
.about .title{
  font-size: 35px;
  font-weight: 700;
}
.logo img {
    width: 33%;
}
.about p{
  padding-top: 20px;
  text-align: justify;
}
.icon{
  color: #fff;
  font-size: 20px;
  cursor: pointer;
  display: none;
}
.icon.cancel-btn{
  position: absolute;
  right: 30px;
  top: 20px;
}
.navbar.sticky .icon.cancel-btn{
  top: 10px;
}
@media only screen and (max-width: 868px) {
  .footer .col-4 {
    margin-top:20px;
  }
  .footer .col-3 {
    margin-top:20px;
  }

  .footer .col-4.col-s-12 {
    width: 100%;
  }
   .footer .col-3.col-s-12 {
    width: 100%;
  }
  body.disabledScroll {
    overflow: hidden;
  }
 .icon{
        margin-bottom: 3%;
    display: block;
    height: 22px;
    width: 57px;
    text-align: center;
    line-height: 50px;
    border-radius: 50%;
    z-index: 9;
    margin-right: 1%;
}
  .icon.cancel-btn{
    display: none;
  }
  .icon.cancel-btn.show{
    display: block;
  }
  .icon.hide{
    display: none;
  }
  .navbar .menu-list{
    position: fixed;
    top: 0;
    left: 0;
    height: 100vh;
    width: 100%;
    background: rgb(201, 197, 197);
    display: block;
    padding: 40px 0;
    text-align: center;
    clip-path: circle(25px at calc(100% - 55px) calc(0% + 50px));
    transition: all 0.3s ease;
  }
  .navbar.sticky .menu-list{
    clip-path: circle(25px at calc(100% - 55px) calc(0% + 35px));
  }
  .navbar .menu-list.active{
    clip-path: circle(75%);
  }
  .navbar .menu-list li{
    margin-top: 45px;
  }
  .navbar .menu-list li a{
    font-size: 23px;
  }
}

/* footer */
.call-user {
    position: fixed;
    bottom:0;
    padding: 0.5% 0%;
    text-align: center;
    background: #0f25c7;
    
}

.call-user p {
    color: white;
}
.footer {
  padding-top: 50px;
  overflow: hidden;
  padding-bottom: 50px;
}
.footer .col-4 {
  margin-right: 25px;
  width:50%;
  display: inline-block;
  float: left;
}
.footer .col-4 h5,.footer .col-3 h5 {
    font-size: 16px;
    margin: 6px 0 10px;
    padding: 0;
}

.footer .col-4 p {
  font-size: 14px;
    padding: 0 0 25px;
}

.footer .col-3 {
  width: 25%;
  float: left;
  display: inline-block;
}
.footer .col-3 input[type="email"] {
    width: 100%;
    height: 38.19px;
}
.footer .col-3 button {
    border: none;
    padding: 3%;
    margin-top: 4%;
    background: lightgreen;
    color: black;
}
.call-user {

    z-index: 5;
    z-index: 9999;
    width: 100%;

    text-align: center;
    background: #0f25c7;

}

.call-user p {
    color: white;
}

.call-user p button {
    background: red;

    color: #ece6e6;
    border: none;
}
.icon.menu-btn img {
    margin-right: 36%;
}
/* nut chuyen tren trang xuong trang */
.icon-top {
    position: fixed;
    bottom: 70px;
    right: -125px;
    z-index: 1000;
    width: 14%;
    display: inline-block;
}
.icon-top.none {
  display: none;
}
.icon-top img {
    width: 27%;
    padding: 7%;
    background: red;
    color: #574e4e24;
}

.icon-bottom {
    position: fixed;
    top: 18%;
    right: -120px;
    z-index: 1000;
    width: 14%;
    display: none;
}
.icon-bottom.active {
  display: block;
}

.icon-bottom img {
    width: 27%;
    padding: 7%;
    background: red;
    color: #574e4e24;
}

</style>
