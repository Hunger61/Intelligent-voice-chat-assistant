import { ref } from 'vue';

export const issuccess = ref(false);
export const isErr = ref(false);
export const isErrMessage = ref('');
export const issuccessMessage = ref('');

let timeoutId = null;

export const showSuccessMessage = (message) => {
  issuccessMessage.value = message;
  issuccess.value = true;

  if (timeoutId) {
    clearTimeout(timeoutId);
  }

  timeoutId = setTimeout(() => {
    issuccess.value = false;
  }, 1000);
};

export const showErrMessage = (message) => {
  isErrMessage.value = message;
  isErr.value = true;

  if (timeoutId) {
    clearTimeout(timeoutId);
  }

  timeoutId = setTimeout(() => {
    isErr.value = false;
  }, 1000);
};
