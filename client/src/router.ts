import { createRouter, createWebHistory } from 'vue-router'
import RoomList from './views/RoomList.vue'

export const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/', component: RoomList },
    ]
})