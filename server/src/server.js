// 1강: 기본 WebSocket 채팅 서버
const express = require('express');
const http = require('http');
const socketIo = require('socket.io');
const path = require('path');

const app = express();
const server = http.createServer(app);
const io = socketIo(server, {
    cors: {
        origin: "*",
        methods: ["GET", "POST"]
    }
});

// 정적 파일 제공 (프론트엔드 파일들)
app.use(express.static(path.join(__dirname, 'public')));

// 기본 라우트
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

// 현재 접속자 수 추적
let connectedUsers = 0;
let activeRooms = new Map(); // 방별 사용자 관리

// Socket.IO 연결 처리
io.on('connection', (socket) => {
    connectedUsers++;
    console.log(`사용자 연결됨: ${socket.id}, 총 ${connectedUsers}명 접속`);
    
    // 모든 클라이언트에게 접속자 수 전송
    io.emit('user-count', connectedUsers);
    
    // 기본 채팅 메시지 처리
    socket.on('chat-message', (data) => {
        console.log('메시지 수신:', data);
        
        // 모든 클라이언트에게 메시지 브로드캐스트
        io.emit('chat-message', {
            id: Date.now(),
            username: data.username || '익명',
            message: data.message,
            timestamp: new Date().toLocaleTimeString('ko-KR')
        });
    });
    
    // 방 입장 처리
    socket.on('join-room', (roomName) => {
        socket.join(roomName);
        
        // 방 사용자 수 업데이트
        if (!activeRooms.has(roomName)) {
            activeRooms.set(roomName, new Set());
        }
        activeRooms.get(roomName).add(socket.id);
        
        console.log(`${socket.id}가 방 '${roomName}'에 입장`);
        
        // 방 입장 알림
        socket.to(roomName).emit('user-joined', {
            message: `새로운 사용자가 방에 입장했습니다.`,
            roomName: roomName,
            userCount: activeRooms.get(roomName).size
        });
        
        // 방 사용자에게 현재 방 정보 전송
        socket.emit('room-info', {
            roomName: roomName,
            userCount: activeRooms.get(roomName).size
        });
    });
    
    // 방별 채팅 메시지 처리
    socket.on('room-message', (data) => {
        const { roomName, username, message } = data;
        
        // 해당 방의 모든 사용자에게 메시지 전송
        io.to(roomName).emit('room-message', {
            id: Date.now(),
            username: username || '익명',
            message: message,
            roomName: roomName,
            timestamp: new Date().toLocaleTimeString('ko-KR')
        });
    });
    
    // 타이핑 상태 처리
    socket.on('typing', (data) => {
        socket.broadcast.emit('user-typing', {
            username: data.username,
            isTyping: data.isTyping
        });
    });
    
    // 연결 해제 처리
    socket.on('disconnect', () => {
        connectedUsers--;
        console.log(`사용자 연결 해제: ${socket.id}, 총 ${connectedUsers}명 접속`);
        
        // 방에서 사용자 제거
        activeRooms.forEach((users, roomName) => {
            if (users.has(socket.id)) {
                users.delete(socket.id);
                socket.to(roomName).emit('user-left', {
                    message: `사용자가 방을 떠났습니다.`,
                    roomName: roomName,
                    userCount: users.size
                });
                
                // 빈 방 정리
                if (users.size === 0) {
                    activeRooms.delete(roomName);
                }
            }
        });
        
        // 모든 클라이언트에게 접속자 수 업데이트
        io.emit('user-count', connectedUsers);
    });
});

const PORT = process.env.PORT || 3007;
server.listen(PORT, () => {
    console.log(`서버가 포트 ${PORT}에서 실행 중입니다.`);
    console.log(`http://localhost:${PORT} 에서 접속 가능합니다.`);
});

// 에러 처리
process.on('uncaughtException', (err) => {
    console.error('예기치 않은 에러:', err);
    process.exit(1);
});

process.on('unhandledRejection', (err) => {
    console.error('처리되지 않은 Promise 거부:', err);
    process.exit(1);
});