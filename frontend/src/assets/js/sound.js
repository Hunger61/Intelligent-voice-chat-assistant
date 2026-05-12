import playSound from '../sound/play.mp3';
import closeSound from '../sound/close.mp3';

class SoundEffect {
  constructor() {
    this.sounds = {
      play: new Audio(playSound),
      close: new Audio(closeSound),
    };

    Object.values(this.sounds).forEach(sound => {
      sound.preload = 'auto';
    });
  }

  play(type) {
    const sound = this.sounds[type];
    if (!sound) return;
    sound.currentTime = 0;
    sound.loop = false;
    sound.play().catch(err => console.log(`播放${type}音效失败:`, err));
  }

  playLoop(type) {
    const sound = this.sounds[type];
    if (!sound) return;
    sound.currentTime = 0;
    sound.loop = true;
    sound.play().catch(err => console.log(`循环播放${type}音效失败:`, err));
  }

  stopLoop(type) {
    const sound = this.sounds[type];
    if (!sound) return;
    sound.loop = false;
    sound.pause();
    sound.currentTime = 0;
  }

  stopAllLoops() {
    Object.values(this.sounds).forEach(sound => {
      if (sound.loop) {
        sound.loop = false;
        sound.pause();
        sound.currentTime = 0;
      }
    });
  }
}

export const soundEffect = new SoundEffect();
