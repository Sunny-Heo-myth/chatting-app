<!--<template>-->
<!--  <div>-->
<!--    <h1>Room List</h1>-->
<!--    <input v-model="newRoom" placeholder="Enter room name" />-->
<!--    <button @click="createRoom">Create</button>-->
<!--    <ul>-->
<!--      <li v-for="room in rooms" :key="room.name">-->
<!--        <router-link :to="`/room/${room.name}`">-->
<!--          {{ room.name }} <small>({{ formatDate(room.createdAt) }})</small>-->
<!--        </router-link>-->
<!--      </li>-->
<!--    </ul>-->
<!--  </div>-->
<!--</template>-->
<template>
  <div class="container">
    <h1 class="title">Chat Rooms</h1>
    <div class="form">
      <input v-model="newRoom" placeholder="Enter room name" />
      <button @click="createRoom">Create</button>
    </div>
    <ul class="room-list">
      <li v-for="room in rooms" :key="room.name">
        <router-link :to="`/room/${room.name}`">
          <strong>{{ room.name }}</strong>
          <small>({{ formatDate(room.createdAt) }})</small>
        </router-link>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

interface Room {
  name: string
  createdAt: string
}

const newRoom = ref('')
const rooms = ref<Room[]>([])

async function fetchRooms() {
  const res = await axios.get<Room[]>(`/api/rooms`)
  rooms.value = res.data
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr)
  return date.toLocaleString()
}

async function createRoom() {
  const name = newRoom.value.trim()
  if (name && !rooms.value.find(r => r.name === name)) {
    const room: Room = { name, createdAt: new Date().toISOString() }
    await axios.post(`/api/rooms`, room)
    rooms.value.push(room)
    newRoom.value = ''
  }
}

onMounted(fetchRooms)
</script>

<style scoped>
.container {
  max-width: 600px;
  margin: 2rem auto;
  padding: 1rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  background: #fafafa;
}
.title {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  text-align: center;
}
.form {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}
input {
  flex: 1;
  padding: 0.5rem;
  font-size: 1rem;
}
button {
  padding: 0.5rem 1rem;
}
.room-list li {
  margin: 0.5rem 0;
}
</style>