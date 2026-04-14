// Matter.js核心模块
import Matter from 'matter-js';
const imageModules = import.meta.glob('../img/*.png', { eager: true });
const { Engine, Render, Bodies, Composite, Runner, Body } = Matter;

// 存储应用状态
const state = {
  container: null,
  physicsContainer: null,
  engine: null,
  render: null,
  runner: null,
  physicsBodies: [],
  dynamicElements: [],
  // 新增：存储每个物体的定时器ID
  objectTimers: [],
  containerSize: { width: 0, height: 0 },
  dragState: {
    isDragging: false,
    currentIndex: -1,
    offset: { x: 0, y: 0 },
    lastMouseX: 0,
    lastMouseY: 0,
    lastTimestamp: 0,
    finalVelocity: { x: 0, y: 0 }
  }
};

// 获取容器尺寸
function getContainerDimensions() {
  if (state.container) {
    const rect = state.container.getBoundingClientRect();
    state.containerSize.width = rect.width;
    state.containerSize.height = rect.height;
  }
  return state.containerSize;
}

// 创建边界
function createBoundaries() {
  const ground = Bodies.rectangle(
    state.containerSize.width / 2,
    state.containerSize.height - 5,
    state.containerSize.width,
    10,
    { isStatic: true }
  );
  const leftWall = Bodies.rectangle(
    5,
    state.containerSize.height / 2,
    10,
    state.containerSize.height,
    { isStatic: false }
  );
  const rightWall = Bodies.rectangle(
    state.containerSize.width - 5,
    state.containerSize.height / 2,
    10,
    state.containerSize.height,
    { isStatic: false }
  );
  const ceiling = Bodies.rectangle(
    state.containerSize.width / 2,
    5,
    state.containerSize.width,
    10,
    { isStatic: true }
  );
  return [ground, leftWall, rightWall, ceiling];
}

// 初始化第一个物体
function initFirstObject() {
  const domElement = document.getElementById('sync-element');
  if (!domElement || !state.container) return;

  const domRect = domElement.getBoundingClientRect();
  const containerRect = state.container.getBoundingClientRect();

  // 计算初始位置（相对于容器）
  const initialX = domRect.width / 2 + (domRect.left - containerRect.left);
  const initialY = domRect.height / 2 + (domRect.top - containerRect.top);

  // 创建物理体
  const physicsBody = Bodies.rectangle(
    initialX,
    initialY,
    0,
    0,
    {
      restitution: 0.8, // 弹性
      friction: 0.011, // 摩擦
      frictionAir: 0.01, // 空气阻力
      label: 'draggable'
    }
  );

  Composite.add(state.engine.world, physicsBody);
  state.physicsBodies.push(physicsBody);

  // 绑定初始物体事件
  domElement.addEventListener('mousedown', (e) => startDrag(e, -1));
}

// 开始拖拽
function startDrag(event, index = -1) {
  event.preventDefault();

  if (index === -1 && state.physicsBodies.length === 0) return;

  const targetBody = index === -1 ? state.physicsBodies[0] : state.physicsBodies[index + 1];
  if (!targetBody || !state.container) return;

  const containerRect = state.container.getBoundingClientRect();
  const bodyPosition = targetBody.position;

  // 设置拖拽状态
  state.dragState.offset.x = (event.clientX - containerRect.left) - bodyPosition.x;
  state.dragState.offset.y = (event.clientY - containerRect.top) - bodyPosition.y;
  state.dragState.isDragging = true;
  state.dragState.currentIndex = index;

  // 记录初始鼠标位置和时间
  state.dragState.lastMouseX = event.clientX - containerRect.left;
  state.dragState.lastMouseY = event.clientY - containerRect.top;
  state.dragState.lastTimestamp = performance.now();
  state.dragState.finalVelocity = { x: 0, y: 0 };

  // 拖拽时设为静态
  Body.setStatic(targetBody, true);

  // 提高z-index
  if (index !== -1) {
    const element = document.querySelector(`.dynamic-element[data-index="${index}"]`);
    if (element) element.style.zIndex = 20;
  } else {
    const firstElement = document.getElementById('sync-element');
    if (firstElement) firstElement.style.zIndex = 20;
  }
}

// 处理鼠标移动
function handleMouseMove(event) {
  if (!state.dragState.isDragging || (state.dragState.currentIndex === -1 && state.physicsBodies.length === 0)) return;
  if (!state.container) return;

  const containerRect = state.container.getBoundingClientRect();
  const targetBody = state.dragState.currentIndex === -1 ? state.physicsBodies[0] : state.physicsBodies[state.dragState.currentIndex + 1];
  if (!targetBody) return;

  const currentTime = performance.now();
  const deltaTime = currentTime - state.dragState.lastTimestamp;

  // 计算当前鼠标位置（相对于容器）
  const mouseX = event.clientX - containerRect.left;
  const mouseY = event.clientY - containerRect.top;

  // 计算鼠标移动速度
  if (deltaTime > 0) {
    state.dragState.finalVelocity.x = (mouseX - state.dragState.lastMouseX) / deltaTime;
    state.dragState.finalVelocity.y = (mouseY - state.dragState.lastMouseY) / deltaTime;
  }

  // 更新物理体位置
  Body.setPosition(targetBody, {
    x: mouseX - state.dragState.offset.x,
    y: mouseY - state.dragState.offset.y
  });
  Body.setAngle(targetBody, 0);

  // 更新上一帧数据
  state.dragState.lastMouseX = mouseX;
  state.dragState.lastMouseY = mouseY;
  state.dragState.lastTimestamp = currentTime;
}

// 结束拖拽
function handleMouseUp() {
  if (!state.dragState.isDragging || (state.dragState.currentIndex === -1 && state.physicsBodies.length === 0)) return;

  const targetBody = state.dragState.currentIndex === -1 ? state.physicsBodies[0] : state.physicsBodies[state.dragState.currentIndex + 1];
  if (!targetBody) return;

  // 恢复物理特性
  Body.setStatic(targetBody, false);

  // 应用速度
  const throwSpeed = 2;
  Body.setVelocity(targetBody, {
    x: state.dragState.finalVelocity.x * throwSpeed,
    y: state.dragState.finalVelocity.y * throwSpeed
  });

  // 重置拖拽状态
  state.dragState.isDragging = false;

  // 恢复z-index
  if (state.dragState.currentIndex !== -1) {
    const element = document.querySelector(`.dynamic-element[data-index="${state.dragState.currentIndex}"]`);
    if (element) element.style.zIndex = 10;
  } else {
    const firstElement = document.getElementById('sync-element');
    if (firstElement) firstElement.style.zIndex = 10;
  }

  state.dragState.currentIndex = -1;
}

// 新增：删除物体的函数
function removeObject(index) {
  // 清除定时器
  if (state.objectTimers[index]) {
    clearTimeout(state.objectTimers[index]);
    state.objectTimers[index] = null;
  }

  // 从DOM中移除元素
  if (state.dynamicElements[index]) {
    const element = state.dynamicElements[index];
    if (element.parentNode) {
      element.parentNode.removeChild(element);
    }
    state.dynamicElements.splice(index, 1);
  }

  // 从物理世界中移除物体
  if (state.physicsBodies.length > index + 1) {
    const body = state.physicsBodies[index + 1];
    Composite.remove(state.engine.world, body);
    state.physicsBodies.splice(index + 1, 1);
  }

  // 更新剩余元素的索引和定时器
  for (let i = index; i < state.dynamicElements.length; i++) {
    state.dynamicElements[i].dataset.index = i;
    // 如果有定时器，调整其索引
    if (state.objectTimers[i + 1]) {
      state.objectTimers[i] = state.objectTimers[i + 1];
      state.objectTimers[i + 1] = null;
    }
  }
}

// 添加新物体 - 从屏幕两侧以抛物线形式进入
export function addNewObject(text,i) {
  if (!state.container) return;

  const index = state.dynamicElements.length;
  const newElement = document.createElement('div');

  // 设置元素基础样式
  newElement.className = 'sync-element dynamic-element';
  newElement.dataset.index = index;
  newElement.style.width = '40px';
  newElement.style.height = '40px';
  newElement.style.zIndex = 50;
  newElement.style.position = 'absolute';
  newElement.style.opacity = '0.7';
  newElement.style.backgroundSize = 'contain';
  newElement.style.backgroundPosition = 'center';
  newElement.style.backgroundRepeat = 'no-repeat';

  // 处理背景图片
  let bgImgModule;
  if (text) {
    bgImgModule = imageModules[`../img/${text}.png`];
    if (!bgImgModule) {
      console.warn(`图片 ../img/${text}.png 不存在`);
    }
  }

  if (bgImgModule && bgImgModule.default) {
    newElement.style.backgroundImage = `url(${bgImgModule.default})`;
    newElement.style.backgroundColor = 'transparent';
  } else {
    newElement.style.backgroundColor = '#f0f0f0';
  }

  // 绑定事件
  newElement.addEventListener('mousedown', (e) => startDrag(e, index));
  newElement.addEventListener('dblclick', () => {
    applyRandomForce(index);
    removeObject(index);
  });

  state.container.appendChild(newElement);
  state.dynamicElements.push(newElement);

  // 抛物线入场效果参数
  const containerWidth = state.containerSize.width;
  const containerHeight = state.containerSize.height;
  
  // 随机选择左侧或右侧进入
  let isLeft = false;

  if(i%2==0){
    isLeft=true
  }
  
  // 初始位置（屏幕外）
  const initialX = isLeft ? -50 : containerWidth + 50; // 左右两侧屏幕外
  const initialY = Math.random() * (containerHeight * 0.3) + containerHeight * 0.2; // 屏幕上方区域

  // 创建对应的物理体
  const newPhysicsBody = Bodies.rectangle(
    initialX,
    initialY,
    40,  // 与元素宽度匹配
    40,  // 与元素高度匹配
      {
      restitution: 0.9,
      friction: 0.01,
      frictionAir: 0.01,
      label: 'draggable'
    }
  );

  // 设置初始速度，形成抛物线
  const horizontalSpeed = isLeft ? 5 + Math.random() * 5 : -(5 + Math.random() * 5); // 水平速度
  const verticalSpeed = -3 - Math.random() * 3; // 初始向上的垂直速度，结合重力形成抛物线

  // 应用初始速度
  Body.setVelocity(newPhysicsBody, {
    x: horizontalSpeed,
    y: verticalSpeed
  });

  Composite.add(state.engine.world, newPhysicsBody);
  state.physicsBodies.push(newPhysicsBody);

  // 设置定时器，5秒后自动删除
  const removalTime = 2500;
  const timerId = setTimeout(() => {
    if (newElement) {
      newElement.style.transition = 'opacity 0.5s ease-out';
      newElement.style.opacity = '0';

      setTimeout(() => {
        removeObject(index);
      }, 500);
    } else {
      removeObject(index);
    }
  }, removalTime);

  state.objectTimers[index] = timerId;
}

// 双击施加随机力
function applyRandomForce(index) {
  if (index + 1 >= state.physicsBodies.length) return;

  const force = 0.05;
  Body.applyForce(state.physicsBodies[index + 1], state.physicsBodies[index + 1].position, {
    x: (Math.random() - 0.5) * force,
    y: (Math.random() - 0.5) * force
  });
}

// 同步物理状态到DOM
function syncPhysicsToDOM() {
  // 同步第一个物体
  if (state.physicsBodies.length > 0) {
    const firstBody = state.physicsBodies[0];
    const firstElement = document.getElementById('sync-element');
    if (firstElement) {
      firstElement.style.left = `${firstBody.position.x - 50}px`;
      firstElement.style.top = `${firstBody.position.y - 50}px`;
      firstElement.style.transform = `rotate(${firstBody.angle}rad)`;
    }
  }

  // 同步动态添加的物体
  state.dynamicElements.forEach((element, index) => {
    if (state.physicsBodies.length > index + 1) {
      const body = state.physicsBodies[index + 1];
      element.style.left = `${body.position.x - 50}px`;
      element.style.top = `${body.position.y - 50}px`;
      element.style.transform = `rotate(${body.angle}rad)`;
    }
  });

  requestAnimationFrame(syncPhysicsToDOM);
}

// 处理窗口大小变化
function handleResize() {
  const newSize = getContainerDimensions();
  if (state.render) {
    state.render.canvas.width = newSize.width;
    state.render.canvas.height = newSize.height;
    Render.setSize(state.render, newSize.width, newSize.height);
  }

  // 更新边界
  if (state.engine) {
    const boundaries = state.engine.world.bodies.filter(body => body.isStatic);
    Composite.remove(state.engine.world, boundaries);
    Composite.add(state.engine.world, createBoundaries());
  }
}

// 清理函数
export function cleanup() {
  document.removeEventListener('mousemove', handleMouseMove);
  document.removeEventListener('mouseup', handleMouseUp);
  window.removeEventListener('resize', handleResize);

  const addButton = document.getElementById('add-button');
  if (addButton) {
    addButton.removeEventListener('click', addNewObject);
  }

  // 新增：清除所有定时器
  state.objectTimers.forEach(timerId => {
    if (timerId) clearTimeout(timerId);
  });
  state.objectTimers = [];

  if (state.runner) {
    Runner.stop(state.runner);
  }

  if (state.render) {
    Render.stop(state.render);
  }
}

// 初始化函数
export function initPhysics(containerSelector) {
  // 获取容器元素
  state.container = document.querySelector(containerSelector);
  if (!state.container) {
    console.error('容器元素未找到');
    return;
  }

  // 获取物理容器
  state.physicsContainer = document.getElementById('physics-container');
  if (!state.physicsContainer) {
    console.error('物理容器元素未找到');
    return;
  }

  // 初始化容器尺寸
  getContainerDimensions();

  // 创建引擎
  state.engine = Engine.create();
  state.engine.world.gravity.y = 0.3;

  // 创建渲染器
  state.render = Render.create({
    element: state.physicsContainer,
    engine: state.engine,
    options: {
      width: state.containerSize.width,
      height: state.containerSize.height,
      wireframes: false,
      background: 'transparent'
    }
  });

  // 添加边界
  Composite.add(state.engine.world, createBoundaries());

  // 初始化第一个物体
  initFirstObject();

  // 启动同步
  syncPhysicsToDOM();

  // 运行渲染器和引擎
  Render.run(state.render);
  state.runner = Runner.create();
  Runner.run(state.runner, state.engine);

  // 绑定全局事件
  document.addEventListener('mousemove', handleMouseMove);
  document.addEventListener('mouseup', handleMouseUp);
  window.addEventListener('resize', handleResize);

  // 绑定添加按钮事件
  const addButton = document.getElementById('add-button');
  if (addButton) {
    addButton.addEventListener('click', addNewObject);
  }

  // 监听页面卸载事件
  window.addEventListener('beforeunload', cleanup);

  // 返回清理函数
  return cleanup;
}
