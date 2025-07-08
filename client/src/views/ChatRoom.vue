<template>
  <div>
    <h1>Chatroom: {{ room }}</h1>
    <input v-model="name" placeholder="Your name" />
    <input v-model="content" @keyup.enter="sendMessage" placeholder="Message" />
    <ul>
      <li v-for="(msg, i) in messages" :key="i">{{ msg }}</li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const room = route.params.name as string
const messages = ref<string[]>([])
const name = ref('')
const content = ref('')
let socket: WebSocket | null = null

function connect() {
  socket = new WebSocket('ws://localhost:8080/ws/chat')

  socket.onopen = () => {
    socket?.send(`join|${room}`)
  }

  socket.onmessage = (event) => {
    messages.value.push(event.data)
  }
}

function sendMessage() {
  if (socket && name.value && content.value) {
    socket.send(`send|${room}|${name.value}|${content.value}`)
    content.value = ''
  }
}

onMounted(connect)
onUnmounted(() => socket?.close())
</script>