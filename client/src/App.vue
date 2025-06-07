<template>
  <div class="chat-container">
    <h2>Spring Boot Chat</h2>
    <div id="chat" class="chat-log">
      <div v-for="(msg, idx) in messages" :key="idx">
        <strong>{{ msg.from }}:</strong> {{ msg.content }}
      </div>
    </div>
    <input v-model="from" placeholder="Your name" class="chat-input" />
    <input v-model="content" @keyup.enter="sendMessage" placeholder="Type a message..." class="chat-input" />
    <button @click="sendMessage">Send</button>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import SockJS from 'sockjs-client';
import { Client, over } from 'stompjs';

const from = ref('');
const content = ref('');
const messages = ref<{ from: string; content: string }[]>([]);
let stompClient: Client;

const sendMessage = () => {
  if (stompClient && stompClient.connected && from.value && content.value.trim()) {
    stompClient.send('/app/send', {}, JSON.stringify({ from: from.value, content: content.value }));
    content.value = '';
  }
};

onMounted(() => {
  const socket = new SockJS('/ws');
  stompClient = over(socket);
  stompClient.connect({}, () => {
    stompClient.subscribe('/topic/messages', (message) => {
      const msg = JSON.parse(message.body);
      messages.value.push(msg);
    });
  });
});
</script>

<style>
.chat-container {
  max-width: 600px;
  margin: auto;
  font-family: sans-serif;
}
.chat-log {
  border: 1px solid #ccc;
  height: 300px;
  overflow-y: scroll;
  padding: 10px;
  margin-bottom: 10px;
}
.chat-input {
  width: calc(100% - 12px);
  margin-bottom: 8px;
  padding: 6px;
  box-sizing: border-box;
}
</style>
