import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
    plugins: [vue()],
    server: {
        proxy: {
            '/ws': {
                target: 'http://localhost:8080',
                ws: true
            },
            '/app': {
                target: 'http://localhost:8080',
                changeOrigin: true
            },
            '/topic': {
                target: 'http://localhost:8080',
                changeOrigin: true
            }
        }
    }
});