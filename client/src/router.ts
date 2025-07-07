import { createRouter, createWebHistory } from 'vue-router'
import RoomList from './views/RoomList.vue'
import ChatRoom from './views/ChatRoom.vue'

export const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/', component: RoomList },
        { path: '/room/:name', component: ChatRoom }
    ]
})