import { ref } from 'vue'; // 确保导入了ref

// 定义变量
export const callStartTime = ref(null);
export const callDuration = ref(0);
export const timerInterval = ref(null);

// 导出开始计时函数
export const startCallTimer = () => {
  callStartTime.value = Date.now();
  callDuration.value = 0;

  timerInterval.value = setInterval(() => {
    callDuration.value = Math.floor((Date.now() - callStartTime.value) / 1000);
  }, 1000);
};

// 导出结束计时函数
export const stopCallTimer = () => {
  if (timerInterval.value) {
    clearInterval(timerInterval.value);
    timerInterval.value = null;
  }
  callDuration.value = 0;
};