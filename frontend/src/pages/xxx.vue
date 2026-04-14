<template>
  <div class="iframe-container">
    <iframe 
      ref="bongoIframe"
      src="/static/bongo.cat-master/index.html" 
      frameborder="0"
      allowfullscreen
      class="bongo-iframe"
      @load="onIframeLoaded"
    ></iframe>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const emit = defineEmits(['page-loaded'])
const bongoIframe = ref(null)
let iframeWindow = null
let messageId = 0 // 用于追踪每个消息的ID

// 向子页面发送事件信息，并添加日志
const sendEventToIframe = (eventType, eventData) => {
  if (iframeWindow) {
    // 生成唯一消息ID用于追踪
    const currentId = messageId++;
    
    const message = {
      id: currentId,
      type: 'screenEvent',
      eventType,
      ...eventData,
      timestamp: new Date().toISOString()
    };
    
    try {
      iframeWindow.postMessage(message, '*'); // 生产环境中应指定具体域名而非*
      console.log(`📤 父页面发送${eventType}事件 (ID: ${currentId}):`, {
        eventType,
        id: currentId,
        data: eventData
      });
    } catch (error) {
      console.error(`❌ 父页面发送${eventType}事件失败 (ID: ${currentId}):`, error);
    }
  } else {
    console.warn('⚠️ 无法发送事件 - iframe尚未加载完成');
  }
}

// 监听各种事件并转发给子页面
const setupEventListeners = () => {
  // 鼠标事件
  ['mousedown', 'mouseup', 'click'].forEach(eventType => {
    document.addEventListener(eventType, (e) => {
      sendEventToIframe(eventType, {
        x: e.clientX,
        y: e.clientY,
        which: e.which
      });
    });
  });

  // 键盘事件
  ['keydown', 'keyup'].forEach(eventType => {
    document.addEventListener(eventType, (e) => {
      sendEventToIframe(eventType, {
        key: e.key,
        code: e.code
      });
    });
  });

  // 触摸事件（移动设备）
  ['touchstart', 'touchend', 'touchmove'].forEach(eventType => {
    document.addEventListener(eventType, (e) => {
      if (e.touches.length > 0) {
        sendEventToIframe(eventType, {
          x: e.touches[0].clientX,
          y: e.touches[0].clientY,
          touchCount: e.touches.length
        });
      }
    });
  });
}

const onIframeLoaded = () => {
  console.log('✅ Bongo Cat 页面已加载完成，开始监听事件')
  iframeWindow = bongoIframe.value.contentWindow
  
  // 监听子页面可能返回的响应（如果需要双向通信）
  window.addEventListener('message', (event) => {
    if (event.source === iframeWindow && event.data.type === 'eventProcessed') {
      console.log(`📥 父页面确认子页面已处理事件 (ID: ${event.data.id})`);
    }
  });
}

onMounted(() => {
  emit('page-loaded')
  setupEventListeners()
})
</script>

<style scoped>
.iframe-container { 
  position: relative;
  width: 100%;
  height: 100%;
 
}

.bongo-iframe {
  position: absolute;
  width: 100%;
  height: 100%;
   transform: scale(0.2);
  min-height: 600px;
  border: none;
 
}

/* 响应式调整 */
@media (max-width: 768px) {
  .bongo-iframe {
    min-height: 400px;
  }
}
</style>
    