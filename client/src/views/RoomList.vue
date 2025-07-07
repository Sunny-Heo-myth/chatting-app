<template>
  <div>
    <h1>Room List</h1>
    <input v-model="newRoom" placeholder="Enter room name" />
    <button @click="createRoom">Create</button>
    <ul>
      <li v-for="room in rooms" :key="room">
        <router-link :to="`/room/${room}`">{{ room }}</router-link>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
const newRoom = ref('')
const rooms = ref<string[]>([])
let ws: WebSocket

function connectSocket() {
  ws = new WebSocket(import.meta.env.VITE_WS_URL)
  ws.onmessage = (event: MessageEvent<string>) => {
    const [type, data] = event.data.split('|', 2)
    if (type === 'rooms') rooms.value = data ? data.split(',') : []
  }
  ws.onopen = () => ws.send(`join|roomlist`)
}

function createRoom() {
  if (newRoom.value.trim()) ws.send(`create|${newRoom.value}`)
}

onMounted(connectSocket)
</script>