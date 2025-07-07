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
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
const route = useRoute()
const room = route.params.name as string
const messages = ref<string[]>([])
const name = ref('')
const content = ref('')
let ws: WebSocket

function connect() {
  ws = new WebSocket(import.meta.env.VITE_WS_URL)
  ws.onopen = () => ws.send(`join|${room}`)
  ws.onmessage = (event) => messages.value.push(event.data)
}

function sendMessage() {
  if (name.value && content.value) {
    ws.send(`send|${room}|${name.value}|${content.value}`)
    content.value = ''
  }
}

onMounted(connect)
</script>