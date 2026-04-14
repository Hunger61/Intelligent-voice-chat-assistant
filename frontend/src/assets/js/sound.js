// 显式导入所有音频文件
import playSound from '../sound/play.mp3';
import closeSound from '../sound/close.mp3';
import bongo0Mp3 from '../sound/bongo0.mp3';
import bongo0Wav from '../sound/bongo0.wav';
import bongo1Mp3 from '../sound/bongo1.mp3';
import bongo1Wav from '../sound/bongo1.wav';
import cowbellMp3 from '../sound/cowbell.mp3';
import cowbellWav from '../sound/cowbell.wav';
import cymbalMp3 from '../sound/cymbal.mp3';
import cymbalWav from '../sound/cymbal.wav';
import keyboard0Mp3 from '../sound/keyboard0.mp3';
import keyboard0Wav from '../sound/keyboard0.wav';
import keyboard1Mp3 from '../sound/keyboard1.mp3';
import keyboard1Wav from '../sound/keyboard1.wav';
import keyboard2Mp3 from '../sound/keyboard2.mp3';
import keyboard2Wav from '../sound/keyboard2.wav';
import keyboard3Mp3 from '../sound/keyboard3.mp3';
import keyboard3Wav from '../sound/keyboard3.wav';
import keyboard4Mp3 from '../sound/keyboard4.mp3';
import keyboard4Wav from '../sound/keyboard4.wav';
import keyboard5Mp3 from '../sound/keyboard5.mp3';
import keyboard5Wav from '../sound/keyboard5.wav';
import keyboard6Mp3 from '../sound/keyboard6.mp3';
import keyboard6Wav from '../sound/keyboard6.wav';
import keyboard7Mp3 from '../sound/keyboard7.mp3';
import keyboard7Wav from '../sound/keyboard7.wav';
import keyboard8Mp3 from '../sound/keyboard8.mp3';
import keyboard8Wav from '../sound/keyboard8.wav';
import keyboard9Mp3 from '../sound/keyboard9.mp3';
import keyboard9Wav from '../sound/keyboard9.wav';
import marimba0Mp3 from '../sound/marimba0.mp3';
import marimba0Wav from '../sound/marimba0.wav';
import marimba1Mp3 from '../sound/marimba1.mp3';
import marimba1Wav from '../sound/marimba1.wav';
import marimba2Mp3 from '../sound/marimba2.mp3';
import marimba2Wav from '../sound/marimba2.wav';
import marimba3Mp3 from '../sound/marimba3.mp3';
import marimba3Wav from '../sound/marimba3.wav';
import marimba4Mp3 from '../sound/marimba4.mp3';
import marimba4Wav from '../sound/marimba4.wav';
import marimba5Mp3 from '../sound/marimba5.mp3';
import marimba5Wav from '../sound/marimba5.wav';
import marimba6Mp3 from '../sound/marimba6.mp3';
import marimba6Wav from '../sound/marimba6.wav';
import marimba7Mp3 from '../sound/marimba7.mp3';
import marimba7Wav from '../sound/marimba7.wav';
import marimba8Mp3 from '../sound/marimba8.mp3';
import marimba8Wav from '../sound/marimba8.wav';
import marimba9Mp3 from '../sound/marimba9.mp3';
import marimba9Wav from '../sound/marimba9.wav';
import meowMp3 from '../sound/meow.mp3';
import meowWav from '../sound/meow.wav';
import tambourineMp3 from '../sound/tambourine.mp3';
import tambourineWav from '../sound/tambourine.wav';

// audioUtils.js
class SoundEffect {
  constructor() {
    // 初始化所有音频
    this.sounds = {
      play: new Audio(playSound),
      close: new Audio(closeSound),
      bongo0Mp3: new Audio(bongo0Mp3),
      bongo0Wav: new Audio(bongo0Wav),
      bongo1Mp3: new Audio(bongo1Mp3),
      bongo1Wav: new Audio(bongo1Wav),
      cowbellMp3: new Audio(cowbellMp3),
      cowbellWav: new Audio(cowbellWav),
      cymbalMp3: new Audio(cymbalMp3),
      cymbalWav: new Audio(cymbalWav),
      keyboard0Mp3: new Audio(keyboard0Mp3),
      keyboard0Wav: new Audio(keyboard0Wav),
      keyboard1Mp3: new Audio(keyboard1Mp3),
      keyboard1Wav: new Audio(keyboard1Wav),
      keyboard2Mp3: new Audio(keyboard2Mp3),
      keyboard2Wav: new Audio(keyboard2Wav),
      keyboard3Mp3: new Audio(keyboard3Mp3),
      keyboard3Wav: new Audio(keyboard3Wav),
      keyboard4Mp3: new Audio(keyboard4Mp3),
      keyboard4Wav: new Audio(keyboard4Wav),
      keyboard5Mp3: new Audio(keyboard5Mp3),
      keyboard5Wav: new Audio(keyboard5Wav),
      keyboard6Mp3: new Audio(keyboard6Mp3),
      keyboard6Wav: new Audio(keyboard6Wav),
      keyboard7Mp3: new Audio(keyboard7Mp3),
      keyboard7Wav: new Audio(keyboard7Wav),
      keyboard8Mp3: new Audio(keyboard8Mp3),
      keyboard8Wav: new Audio(keyboard8Wav),
      keyboard9Mp3: new Audio(keyboard9Mp3),
      keyboard9Wav: new Audio(keyboard9Wav),
      marimba0Mp3: new Audio(marimba0Mp3),
      marimba0Wav: new Audio(marimba0Wav),
      marimba1Mp3: new Audio(marimba1Mp3),
      marimba1Wav: new Audio(marimba1Wav),
      marimba2Mp3: new Audio(marimba2Mp3),
      marimba2Wav: new Audio(marimba2Wav),
      marimba3Mp3: new Audio(marimba3Mp3),
      marimba3Wav: new Audio(marimba3Wav),
      marimba4Mp3: new Audio(marimba4Mp3),
      marimba4Wav: new Audio(marimba4Wav),
      marimba5Mp3: new Audio(marimba5Mp3),
      marimba5Wav: new Audio(marimba5Wav),
      marimba6Mp3: new Audio(marimba6Mp3),
      marimba6Wav: new Audio(marimba6Wav),
      marimba7Mp3: new Audio(marimba7Mp3),
      marimba7Wav: new Audio(marimba7Wav),
      marimba8Mp3: new Audio(marimba8Mp3),
      marimba8Wav: new Audio(marimba8Wav),
      marimba9Mp3: new Audio(marimba9Mp3),
      marimba9Wav: new Audio(marimba9Wav),
      meowMp3: new Audio(meowMp3),
      meowWav: new Audio(meowWav),
      tambourineMp3: new Audio(tambourineMp3),
      tambourineWav: new Audio(tambourineWav)
    };

    // 批量预加载所有音频
    Object.values(this.sounds).forEach(sound => {
      sound.preload = 'auto';
    });
  }
  // 普通播放（单次）
  play(type) {
    const sound = this.sounds[type];
    if (sound) {
      sound.currentTime = 0; // 重置播放位置到开头
      sound.loop = false; // 确保关闭循环
      sound.play().catch(err => console.log(`播放${type}音效失败:`, err));
    }
  }

  // 循环播放
  playLoop(type) {
    const sound = this.sounds[type];
    if (sound) {
      sound.currentTime = 0; // 重置播放位置
      sound.loop = true; // 开启循环模式
      sound.play().catch(err => console.log(`循环播放${type}音效失败:`, err));
    }
  }

  // 终止循环播放（同时停止当前播放）
  stopLoop(type) {
    const sound = this.sounds[type];
    if (sound) {
      sound.loop = false; // 关闭循环模式
      sound.pause(); // 停止播放
      sound.currentTime = 0; // 重置播放位置（可选，根据需求决定是否重置）
    }
  }

  // 终止所有音效的循环播放（全局停止）
  stopAllLoops() {
    Object.values(this.sounds).forEach(sound => {
      if (sound.loop) { // 只处理正在循环的音效
        sound.loop = false;
        sound.pause();
        sound.currentTime = 0;
      }
    });
  }
}

export const soundEffect = new SoundEffect();