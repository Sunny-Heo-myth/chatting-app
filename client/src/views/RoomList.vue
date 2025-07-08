<template>
  <div>
    <h1>Room List</h1>
    <input v-model="newRoom" placeholder="Enter room name" />
    <button @click="createRoom">Create</button>
    <ul>
      <li v-for="room in rooms" :key="room.name">
        <router-link :to="`/room/${room.name}`">{{ room.name }}</router-link>
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