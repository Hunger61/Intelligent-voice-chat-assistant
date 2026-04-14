import { setAudioDataCallback } from './webRTCAll'
import gsap from 'gsap';

let bars = [];
let lastHeights = [];
let totalBars;

// 频率分段配置（针对7个柱子的优化分配）
// 人类听觉对中低频更敏感，所以做非均匀分配
const frequencyBands = [
  { min: 20, max: 150 },   // 低频 - 鼓点、贝斯
  { min: 150, max: 300 },  // 中低频
  { min: 300, max: 600 },  // 中低频
  { min: 600, max: 1200 }, // 中频 - 人声主要区域
  { min: 1200, max: 2500 },// 中高频
  { min: 2500, max: 5000 },// 高频
  { min: 5000, max: 16000 } // 超高音 - 泛音、细节
];

export function initWaveAnimation() {
  bars = document.querySelectorAll(".wave-bar");
  totalBars = bars.length;
  console.log("初始化的频谱柱数量：", totalBars);
  
  // 初始化上一帧高度
  lastHeights = new Array(totalBars).fill(4);
  
  setAudioDataCallback(updateAnimationFromAudio);
}

function updateAnimationFromAudio(audioData) {
  const { fftData, sampleRate, volume } = audioData;
  
  // 数据有效性检查
  if (!fftData || !sampleRate) return;
  
  // 计算每个频率段的能量
  const bandEnergies = calculateBandEnergies(fftData, sampleRate);
  
  // 动画速度基于整体音量动态调整
  const speed = Math.min(0.2 - (volume * 0.15), 0.15);
  
  bars.forEach((bar, index) => {
    // 基础能量高度（放大到可视化范围）
    let baseHeight = bandEnergies[index] * 80;
    
    // 低频增强（人类听觉特性）
    if (index < 2) {
      baseHeight *= 1.4;
    }
    // 高频补偿（通常能量较低）
    else if (index > 4) {
      baseHeight *= 1.6;
    }
    
    // 最小高度限制
    const targetHeight = Math.max(baseHeight, 3);
    
    // 平滑过渡处理（使用更高的上一帧权重，使动画更流畅）
    const smoothedHeight = lastHeights[index] * 0.6 + targetHeight * 0.4;
    lastHeights[index] = smoothedHeight;
    
    // 根据频率和能量设置视觉效果
    const hue = 20 + (index / totalBars) * 200; // 从橙色到青色的渐变
    const saturation = 70;
    const lightness = 45 + (smoothedHeight / 220) * 15;
    const glowIntensity = 3 + (smoothedHeight / 220) * 12;
    
    // 更新动画
    gsap.to(bar, {
      height: smoothedHeight,
      duration: Math.max(speed, 0.05), // 确保最小速度
      ease: "power1.out",
      width: 12 + (smoothedHeight / 220) * 6,
      backgroundColor: `hsl(${hue}, ${saturation}%, ${lightness}%)`,
      boxShadow: `0 0 ${glowIntensity}px hsl(${hue}, ${saturation}%, ${lightness}%)`,
      borderRadius: Math.min(3 + (smoothedHeight / 220) * 5, 8)
    });
  });
}

// 计算每个频率段的能量
function calculateBandEnergies(fftData, sampleRate) {
  const bufferLength = fftData.length;
  const bandEnergies = new Array(totalBars).fill(0);
  
  // 遍历每个频率柱
  frequencyBands.forEach((band, bandIndex) => {
    let energySum = 0;
    let count = 0;
    
    // 计算该频率段在FFT数据中的索引范围
    const minIndex = Math.floor((band.min * bufferLength * 2) / sampleRate);
    const maxIndex = Math.floor((band.max * bufferLength * 2) / sampleRate);
    
    // 累加该频段内的能量值
    for (let i = minIndex; i < maxIndex && i < bufferLength; i++) {
      // FFT数据是0-255，归一化到0-1
      energySum += fftData[i] / 255;
      count++;
    }
    
    // 计算平均能量（避免除零）
    bandEnergies[bandIndex] = count > 0 ? energySum / count : 0;
  });
  
  return bandEnergies;
}
    