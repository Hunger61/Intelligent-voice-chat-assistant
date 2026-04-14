export function createMieAnimation(mieRef) {
  // 动画所需变量
  let deg = 0;
  let imgx = 0;
  let imgy = 0;
  let imgl = 0;
  let imgt = 0;
  let y = 0;
  let index = 0;
  let timer = null;

  // 鼠标移动事件处理函数
  const handleMouseMove = (xyz) => {
    const mie = mieRef.value;
    if (!mie) return;

    // 计算鼠标相对元素中心的位置
    imgx = xyz.x - mie.offsetLeft - mie.clientWidth / 2;
    imgy = xyz.y - mie.offsetTop - mie.clientHeight / 2;

    // 计算旋转角度
    deg = 360 * Math.atan(imgy / imgx) / (2 * Math.PI);

    // 重置计时器
    index = 0;

    // 确定Y轴旋转方向
    if (mie.offsetLeft + mie.clientWidth / 2 < xyz.clientX) {
      y = -180;
    } else {
      y = 0;
    }
  };

  // 动画更新函数
  const updateAnimation = () => {
    const mie = mieRef.value;
    if (!mie) return;

    // 应用旋转效果
    mie.style.transform = `rotateZ(${deg}deg) rotateY(${y}deg)`;

    // 更新计数器
    index++;

    // 平滑移动参数
    const t = 50;
    const a = 50;

    // 计算位置并应用
    if (index < a) {
      imgl += imgx / t;
      imgt += imgy / t;
    }

    mie.style.left = `${imgl}px`;
    mie.style.top = `${imgt}px`;
  };

  // 初始化函数
  const init = (initialLeft = 100, initialTop = 100) => {
    const mie = mieRef.value;
    if (mie) {
      // 设置初始位置
      imgl = initialLeft;
      imgt = initialTop;
      mie.style.left = `${imgl}px`;
      mie.style.top = `${imgt}px`;
    }
    
    // 启动定时器
    timer = setInterval(updateAnimation, 10);
    // 绑定鼠标事件
    window.addEventListener('mousemove', handleMouseMove);
  };

  // 销毁函数
  const destroy = () => {
    // 清除定时器
    if (timer) {
      clearInterval(timer);
      timer = null;
    }
    // 移除事件监听
    window.removeEventListener('mousemove', handleMouseMove);
  };

  // 暴露公共方法
  return {
    init,
    destroy,
    // 可选：暴露更新方法以便手动控制
    update: updateAnimation
  };
}
