<template>
  <div class="chat-container">
    <h2>Spring Boot Chat</h2>
    <div id="chat" class="chat-log">
      <div v-for="(msg, idx) in messages" :key="idx">
        <strong>{{ msg.from }}:</strong> {{ msg.content }}
      </div>
    </div>
    <input v-model="from" placeholder="Your name" class="chat-input"/>
    <input v-model="content" @keyup.enter="sendMessage" placeholder="Type a message..." class="chat-input" />
    <button @click="sendMessage">Send</button>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import SockJS from 'sockjs-client';
import {Client, Message, over} from 'stompjs';
import {ChatMessage} from "./room/service/roomService";

const from = ref<string>('');
const content = ref<string>('');

const messages = ref<ChatMessage[]>([]);
const client = ref<Client>(null);

onMounted(async () => {
  try {
    client.value = createStompClient();
    await connectStompClient();
    await subscribeToTopic('/topic/messages');  // Subscribe this channel
  } catch (err) {
    console.error('WebSocket setup failed:', err);
  }
});

const createStompClient = (): Client => {
  const socket = new SockJS('/ws'); // This is the endpoint it's targeting.
  return over(socket);
}

const connectStompClient = async (): Promise<void> => {
  await new Promise<void>((resolve, reject) => {
    client.value.connect({}, () => resolve(), error => reject(error));
  });
}

const subscribeToTopic = async (topic: string): Promise<void> => {
  await new Promise<void>((resolve) => {
    client.value.subscribe(topic, (message: Message) => {
      const msg: ChatMessage = JSON.parse(message.body);
      messages.value.push(msg);
    });
    resolve();
  });
}

const sendMessage = () => {
  if (client && client.value.connected && from.value && content.value.trim()) {
    client.value.send('/app/send', {},
        JSON.stringify({ from: from.value, content: content.value }));
    content.value = '';
  }
};

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
