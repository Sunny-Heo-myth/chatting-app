
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
import {formatDate} from "../common/utils";

interface Room {
  name: string | null
  createdAt: string
}

const newRoom = ref(null)
const rooms = ref<Room[]>([])

const fetchRooms = async () => {
  const res = await axios.get<Room[]>(`/api/rooms`)
  rooms.value = res.data
}

const createRoom = async () => {
  // const name = newRoom.value.trim()
  if (!rooms.value.find(r => r.name === newRoom.value)) {
    const room: Room = { name: newRoom.value, createdAt: new Date().toISOString() }
    await axios.post(`/api/rooms`, room)
    rooms.value.push(room)
    newRoom.value = null
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