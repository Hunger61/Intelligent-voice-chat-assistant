import { ref } from 'vue'; // 确保导入ref

// 状态变量
export const issuccess = ref(false); // 是否显示成功页面
export const isErr = ref(false); // 是否显示失败页面
export const isErrMessage = ref(''); // 错误消息
export const issuccessMessage = ref(''); // 成功消息
export const clickMessage=ref(false)
 let timeoutId = null; // 消息计时器

// 显示成功消息的函数
export const showSuccessMessage = (message) => {
  issuccessMessage.value = message;
  issuccess.value = true;

  // 清除之前的定时器
  if (timeoutId) {
    clearTimeout(timeoutId);
  }

  // 设置新的定时器，1秒后隐藏消息
  timeoutId = setTimeout(() => {
    issuccess.value = false;
  }, 1000);
};

// 显示错误消息的函数
export const showErrMessage = (message) => {
  isErrMessage.value = message;
  isErr.value = true;

  // 清除之前的定时器
  if (timeoutId) {
    clearTimeout(timeoutId);
  }

  // 设置新的定时器，1秒后隐藏消息
  timeoutId = setTimeout(() => {
    isErr.value = false;
  }, 1000);
};



// 显示成功消息的函数
export const showClickSuccessMessage = (message) => {
  issuccessMessage.value = message;
  issuccess.value = true;
  clickMessage.value=true
};

// 显示错误消息的函数
export const showClickErrMessage = (message) => {
  isErrMessage.value = message;
  isErr.value = true;
  clickMessage.value=true
};