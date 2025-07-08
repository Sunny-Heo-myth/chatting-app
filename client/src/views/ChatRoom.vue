<template>
  <div class="chatroom">
    <h1>Chatroom: {{ room }}</h1>
    <div class="inputs">
      <input v-model="name" placeholder="Your name" />
      <input v-model="content" @keyup.enter="sendMessage" placeholder="Message" />
    </div>
    <ul class="messages">
      <li v-for="(msg, i) in messages" :key="i" class="message">
        <div class="message-left">
          <strong>{{ msg.sender }}</strong>: {{ msg.text }}
        </div>
        <div class="message-right">
          <small class="timestamp">{{ formatDate(msg.timestamp) }}</small>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import {formatDate} from "../common/utils";

const route = useRoute()
const room = ref<string>('')

onMounted(() => {
  room.value = route.params.name as string
  connect()
})
const messages = ref<{ sender: string, text: string, timestamp: string }[]>([])
const name = ref('')
const content = ref('')
let socket: WebSocket | null = null

function connect() {
  socket = new WebSocket('ws://localhost:8080/ws/chat')

  socket.onopen = () => {
    socket?.send(`join|${room.value}`)
  }

  socket.onmessage = (event) => {
    const parsed = JSON.parse(event.data)
    messages.value.push(parsed)
  }
}

function sendMessage() {
  if (socket && name.value && content.value) {
    const message = {
      sender: name.value,
      text: content.value,
      timestamp: new Date().toISOString()
    }
    socket.send(`send|${room.value}|${JSON.stringify(message)}`)
    content.value = ''
  }
}


onUnmounted(() => socket?.close())
</script>

<style scoped>
.chatroom {
  max-width: 600px;
  margin: 2rem auto;
  padding: 1rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  background: #fdfdfd;
}
.inputs {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1rem;
}
input {
  flex: 1;
  padding: 0.5rem;
  font-size: 1rem;
}
.messages {
  list-style: none;
  padding: 0;
}
.message {
  padding: 0.25rem 0;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.timestamp {
  color: #888;
  font-size: 0.85rem;
}

</style>