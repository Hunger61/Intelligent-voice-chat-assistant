import gsap from 'gsap';
import store from '../../store';
import AssistantService from '../../api/assistant.js';

const imageModules = import.meta.glob('../img/*.jpg', { eager: true });
export const photobox = {
    // 添加交互状态控制变量，设为false则禁用所有交互
    interactive: false,  // 这里可以改为false来测试效果
    canautomove: true,

    // canvas对象容器
    canvas: {},
    // canvas 2d上下文
    content: {},
    // 图片的总数
    img_total: 35,
    // 图片排列的总列数
    row_max: 7,
    // 图片排列的总行数
    line_max: 5,
    // 源图片的实际宽高
    img_width: Math.floor(500 / 2),
    img_height: Math.floor(500 / 2),
    // 图片间的上下左右间距
    img_margin: 100,
    // 所有图片纵横排列之后的总宽高
    total_width: 0,
    total_height: 0,
    // 图片数据
    img_data: 0,
    assistants: [
        {
            "id": 0,
            "name": "IT 技术顾问",
            "description": "精通各类软硬件技术，能快速诊断系统故障、解答编程难题，提供实用的技术解决方案，助力解决 IT 领域各类问题",
            "character": "设定为技术专精型人格，语言专业且通俗易懂，善于拆解复杂技术问题，用清晰步骤指导用户解决问题",
            "knowledge_base_id":"avnnajka5r"
        },
        {
            "id": 0,
            "name": "律法咨询专家",
            "description": "熟悉各类法律法规，能解答法律疑问、分析法律风险，提供专业的法律建议，帮助用户理清法律事务头绪",
            "character": "设定为严谨专业型人格，语气沉稳客观，注重法律条款准确性，用规范表述提供合法合规的解决方案",
            "knowledge_base_id":"hf9bh1u5he"
        },
        {
            "id": 0,
            "name": "临床医生",
            "description": "具备丰富临床诊疗经验，能结合症状与检查结果进行专业诊断，制定科学治疗方案，处理常见病、多发病及复杂病症，提供规范的临床医疗指导",
            "character": "设定为专业严谨型人格，语气沉稳权威，注重病情细节分析，既体现医学专业性，又能以通俗易懂的方式解释诊疗逻辑，给予患者安心感",
            "knowledge_base_id":"rqk0r2wkl1"
        },
        {
            "id": 0,
            "name": "料理主厨",
            "description": "精通各类菜系烹饪技巧，能传授食材处理、调味搭配方法，提供详细菜谱指导，帮你提升厨艺水平",
            "character": "设定为热情分享型人格，语言生动接地气，善于用简单易懂的方式讲解烹饪细节，激发用户烹饪兴趣",
            "knowledge_base_id":"wu84zgzu7y"
        },
        {
            "id": 0,
            "name": "中医调理师",
            "description": "深谙中医理论与养生之道，能通过辨证分析提供中药调理、经络养生建议，助力改善体质、防治亚健康",
            "character": "设定为传统智慧型人格，语气平和沉稳，结合中医理念解释身体状况，传递自然调理的健康观念",
            "knowledge_base_id": "rqk0r2wkl1"
        },
        {
            "id": 0,
            "name": "全能教师",
            "description": "覆盖多学科知识教学，能因材施教解答学习疑问、指导学习方法，助力学生提升各学科学习能力",
            "character": "设定为耐心引导型人格，语气亲切鼓励，善于启发思考，用适合不同学习阶段的方式传递知识内容",
            "knowledge_base_id":""
        },

        {
            "id": 0,
            "name": "安全防护卫士",
            "description": "熟悉各类安全知识，能提供居家、出行、网络等场景的安全防护建议，帮助规避安全风险",
            "character": "设定为严谨警惕型人格，说话清晰有力，注重安全细节提醒，用明确的指导帮助用户建立安全意识",
            "knowledge_base_id":"xf1dmp6h3t"
        },
        {
            "id": 0,
            "name": "健康管理顾问",
            "description": "擅长健康规划与管理，能制定个性化饮食、运动方案，评估健康风险，帮助用户养成良好生活习惯",
            "character": "设定为细致规划型人格，说话耐心有条理，善于结合用户实际情况，提供可落地的健康管理计划",
            "knowledge_base_id":"cpypc7zzgh"
        },
        {
            "id": 0,
            "name": "烹饪大师",
            "description": "深耕烹饪领域多年，精通中西餐精髓与创新技法，能解析菜品灵魂、指导进阶厨艺，助你打造专业级美味",
            "character": "设定为资深权威型人格，语言精准且富有感染力，善于从食材本质与烹饪哲学角度，传授高阶烹饪智慧",
            "knowledge_base_id":"wu84zgzu7y"
        }, {
            "id": 0,
            "name": "法律权益守护顾问",
            "description": "专注于维护各类合法权益，能精准识别权益受损情况，提供针对性维权策略，协助处理合同纠纷、侵权维权等事务，为权益保障筑牢防线",
            "character": "设定为坚定守护型人格，语气果敢有力，既懂法律条文又通实务技巧，像盾牌一样为用户的合法权益据理力争",
            "knowledge_base_id":"hf9bh1u5he"
        },
        {
            "id": 0,
            "name": "代码诊疗专家",
            "description": "擅长代码问题深度诊断，能快速定位程序漏洞、优化代码性能，解决逻辑错误、兼容性问题等各类编码顽疾，让代码高效稳定运行",
            "character": "设定为细致排查型人格，语言专业精准，像代码医生一样用系统化分析找到问题根源，提供根治性解决方案",
            "knowledge_base_id":"avnnajka5r"
        }
    ],
    // 当前画布是否可以移动
    if_movable: false,
    // 新增：每列的垂直移动速度系数（视差核心）
    columnSpeed: [],
    // 放大相关参数
    scale: {
        factor: 1.2,       // 目标放大倍数
        hoveredIndex: -1,  // 当前被悬停的图片索引
        transitionTime: 0.3 // 缩放过渡时间(秒)
    },
    // 自动移动相关参数
    autoMove: {
        enabled: true, // 是否启用自动移动，默认启用
        timer: null,    // 计时器
        interval: 2000, // 3秒无操作后开始自动移动
        speed: 0.5,     // 自动移动速度
        angle: 0,       // 移动角度
        lastTime: 0,    // 上次移动时间
        intervalTime: 16 // 移动间隔(ms)，约60fps
    },
    // 滚轮相关参数
    wheel: {
        speed: 4,     // 滚轮移动速度
        damping: 0.82     // 滚轮惯性阻尼
    },
    ease: {
        x: 0,
        y: 0,
        id: null,
        damping: t => .90 * t //阻尼函数
    },
    // 初始化
    init(retryCount = 3) {
        this.canvas = document.querySelector(".photobox");
        if (!this.canvas) {
            console.warn(`Canvas element not found. Attempting retry (${retryCount} remaining)...`);
            if (retryCount > 0) {
                setTimeout(() => {
                    this.init(retryCount - 1);
                }, 100);
            }
            return;
        }
        this.content = this.canvas.getContext("2d");
        if (!this.content) {
            console.warn('Canvas context not available. Skipping initialization.');
            return;
        }
        this.total_width = this.row_max * (this.img_width + this.img_margin) - this.img_margin;
        this.total_height = this.line_max * (this.img_height + this.img_margin) - this.img_margin;

        // 根据交互状态设置CSS类
        if (this.interactive) {
            this.canvas.classList.add('interactive');
        } else {
            this.canvas.classList.remove('interactive');
        }

        // 初始化列速度系数（每列不同，范围0.6-1.4，确保视差效果明显且不混乱）
        this.columnSpeed = Array.from({ length: this.row_max }, (_, i) => {
            // 按列索引线性分配速度（从左到右逐渐加快），也可改为随机值：0.6 + Math.random() * 0.8
            return 0.4 + (i / (this.row_max - 1)) * 0.4;
        });

        // 获取用户助手列表信息
        const userAssistants = store.getters.getAllAssistants;

        // 增加判断：确保是数组再进行处理
        if (Array.isArray(userAssistants)) {
            // 从userAssistants中提取需要的字段并创建新数组
            const newAssistants = userAssistants.map(assistant => ({
                id: assistant?.id,          // 使用可选链避免assistant为null/undefined时出错
                character: assistant?.character,
                name: assistant?.name,
                description: assistant?.description
            }));
            // 将提取后的数组添加到this.assistants中
            this.assistants.push(...newAssistants);
        } else {
            // 非数组情况的处理（可选）
            console.warn('userAssistants不是一个有效的数组', userAssistants);
        }

        console.log(this.assistants)
        this.resize();
        this.creat_events();
        this.creat_img_data();
        // 初始化时不重置自动移动计时器，保持autoMove.enabled为true
        this.autoMove.lastTime = performance.now();
        this.animate();
    },
    resize() {
        if (!this.canvas) {
            console.warn('Canvas element not available. Skipping resize.');
            return;
        }
        this.canvas.width = this.canvas.clientWidth;
        this.canvas.height = this.canvas.clientHeight;
        if (this.img_data) this.render_images();
    },
    // 创建图片数据 - 已包含col_index，无需额外修改
    creat_img_data() {
        this.img_data = [];
        let j = 0;
        let h = 0
        for (let i = 0; i < this.img_total; i++) {

            h = i % this.assistants.length
            j = h % 11;
            const imgName = `ig${j + 1}.jpg`;
            const imgModule = imageModules[`../img/${imgName}`];

            if (!imgModule) {
                console.error(`找不到图片: ${imgName}`);
                continue;
            }

            const img = new Image();
            img.src = imgModule.default;

            // 添加错误监听
            img.onerror = () => {
                console.error(`图片加载失败: ../img/img${j + 1}.jpg`);
            };
            let col_index = i % this.row_max; // 列索引（0到row_max-1）
            let line_index = Math.floor(i / this.row_max);
            let x = col_index * (this.img_width + this.img_margin);
            let y = line_index * (this.img_height + this.img_margin);
            this.img_data.push({
                id: this.assistants[h].id,
                img,
                x,
                y,
                content: this.assistants[h].name,
                index: i + 1,
                col_index, // 保存列索引，用于后续速度计算
                isHovered: false,
                currentScale: 1, // 当前缩放值(1为原始大小)
                scaleTween: null // 缩放动画实例
            });
            img.onload = () => {
                this.render_images();
            };
        }
    },
    creat_events() {
        // 窗口大小调整事件（不受交互状态影响）
        window.addEventListener("resize", () => {
            this.resize();
        });

        if (!this.canvas) {
            console.warn('Canvas element not available. Skipping event creation.');
            return;
        }

        // 鼠标事件 - 全部添加交互状态判断
        this.canvas.addEventListener("mousedown", (e) => {
            if (!this.interactive) return; // 如果不可交互则直接返回
            this.if_movable = true;
            this.reset_auto_move_timer();
        });
        this.canvas.addEventListener("mouseup", (e) => {
            if (!this.interactive) return;
            this.if_movable = false;
            this.reset_auto_move_timer();
        });
        // 鼠标双击事件
        this.canvas.addEventListener("dblclick", (e) => {
            if (!this.interactive) return;
            this.if_movable = false;
            this.check_img(e.x, e.y);
            this.reset_auto_move_timer();
        });
        this.canvas.addEventListener("mouseleave", () => {
            if (!this.interactive) return;
            this.if_movable = false;
            this.scale.hoveredIndex = -1; // 离开时取消悬停状态
            this.updateHoverStates();
            this.reset_auto_move_timer();
        });
        this.canvas.addEventListener("mousemove", (e) => {
            if (!this.interactive) return;
            this.handlePointerMove(e.x, e.y);
            if (!this.if_movable) return;
            this.move_imgs(e.movementX, e.movementY);
            this.reset_auto_move_timer();
        });

        // 滚轮事件
        this.canvas.addEventListener("wheel", (e) => {
            if (!this.interactive) return;
            e.preventDefault();
            const delta = e.deltaY > 0 ? 1 : -1;
            this.move_imgs(0, delta * this.wheel.speed * 10);
            this.reset_auto_move_timer();
        });

        // 触摸事件
        this.canvas.addEventListener("touchstart", (e) => {
            if (!this.interactive) return;
            e.preventDefault();
            this.if_movable = true;
            const touch = e.touches[0];
            this.handlePointerMove(touch.clientX, touch.clientY);

        });
        this.canvas.addEventListener("touchend", (e) => {
            if (!this.interactive) return;
            e.preventDefault();
            this.if_movable = false;
            const touch = e.changedTouches[0];
            this.check_img(touch.clientX, touch.clientY);

        });
        this.canvas.addEventListener("touchmove", (e) => {
            if (!this.interactive) return;
            e.preventDefault();
            const touch = e.touches[0];
            this.handlePointerMove(touch.clientX, touch.clientY);
            if (!this.if_movable) return;
            const movementX = touch.movementX || 0;
            const movementY = touch.movementY || 0;
            this.move_imgs(movementX, movementY);

        });
    },

    // 处理指针移动，更新悬停状态
    handlePointerMove(x, y) {
        if (!this.interactive) {
            // 如果不可交互，强制取消所有悬停状态
            if (this.scale.hoveredIndex !== -1) {
                this.scale.hoveredIndex = -1;
                this.updateHoverStates();
            }
            return;
        }

        let hoveredIndex = -1;
        for (let i = 0; i < this.img_data.length; i++) {
            const img = this.img_data[i];
            const scaledWidth = this.img_width * img.currentScale;
            const scaledHeight = this.img_height * img.currentScale;
            const offsetX = (scaledWidth - this.img_width) / 2;
            const offsetY = (scaledHeight - this.img_height) / 2;

            if (x >= img.x - offsetX && x < img.x + scaledWidth - offsetX &&
                y >= img.y - offsetY && y < img.y + scaledHeight - offsetY) {
                hoveredIndex = i;
                break;
            }
        }
        if (this.scale.hoveredIndex !== hoveredIndex) {
            this.scale.hoveredIndex = hoveredIndex;
            this.updateHoverStates();
        }
    },

    // 更新图片悬停状态
    updateHoverStates() {
        this.img_data.forEach((img, index) => {
            const wasHovered = img.isHovered;
            img.isHovered = index === this.scale.hoveredIndex;

            if (img.isHovered !== wasHovered) {
                if (img.scaleTween) {
                    img.scaleTween.kill();
                }

                const targetScale = img.isHovered ? this.scale.factor : 1;
                img.scaleTween = gsap.to(img, {
                    currentScale: targetScale,
                    duration: this.scale.transitionTime,
                    ease: "power2.out",
                    onUpdate: () => {
                        this.render_images();
                    }
                });
            }
        });
    },

    reset_auto_move_timer() {
        if (this.autoMove.timer) {
            clearTimeout(this.autoMove.timer);
        }
        this.autoMove.enabled = false;
        this.autoMove.timer = setTimeout(() => {
            this.autoMove.enabled = true;
            this.autoMove.angle = Math.random() * Math.PI * 2;
            this.autoMove.speed = 1 + Math.random() * 2;
            this.autoMove.lastTime = performance.now();
        }, this.autoMove.interval);
    },

    // 累积移动量（x和y方向）
    move_imgs(t, i) {
        this.ease.x += 0.2 * t;  // x方向移动量（所有列统一）
        this.ease.y += 0.2 * i;  // y方向移动量（后续将按列速度分配）
    },

    // 渲染图片
    render_images() {
        if (!this.canvas || !this.content) {
            console.warn('Canvas or context not available. Skipping render.');
            return;
        }
        this.content.clearRect(0, 0, this.canvas.width, this.canvas.height);

        this.img_data.forEach((item) => {
            if (item.hidden) return;
            // 边界处理
            if (item.x > this.total_width - this.img_width) {
                item.x -= (this.total_width + this.img_margin);
            } else if (item.x < -this.img_width) {
                item.x += (this.total_width + this.img_margin);
            }

            if (item.y > this.total_height - this.img_height) {
                item.y -= (this.total_height + this.img_margin);
            } else if (item.y < -this.img_height) {
                item.y += (this.total_height + this.img_margin);
            }

            // 应用缩放
            const scale = item.currentScale;
            const scaledWidth = this.img_width * scale;
            const scaledHeight = this.img_height * scale;
            const offsetX = (scaledWidth - this.img_width) / 2;
            const offsetY = (scaledHeight - this.img_height) / 2;

            // 关键修复：设置透明度
            this.content.globalAlpha = item.opacity || 1;

            // 绘制图片
            this.content.drawImage(
                item.img,
                item.x - offsetX,
                item.y - offsetY,
                scaledWidth,
                scaledHeight
            );

            // 重置透明度（避免影响其他绘制）
            this.content.globalAlpha = 1;

            // 绘制文字编号
            const textScale = scale;
            this.content.font = `bold ${36 * textScale}px Arial`;
            if (item.id != 0) {
                this.content.fillStyle = 'black';
            } else {
                this.content.fillStyle = 'white';
            }
            this.content.textAlign = 'center';
            this.content.textBaseline = 'top';

            const textX = item.x + this.img_width / 2;
            const textY = item.y - 36 * textScale;

            this.content.shadowColor = 'rgba(0, 0, 0, 0.7)';
            this.content.shadowBlur = 5 * textScale;
            this.content.shadowOffsetX = 2 * textScale;
            this.content.shadowOffsetY = 2 * textScale;

            this.content.fillText(`${item.content}`, textX, textY);

            this.content.shadowColor = 'transparent';
            this.content.shadowBlur = 0;
            this.content.shadowOffsetX = 0;
            this.content.shadowOffsetY = 0;
        });
    },

    check_img(x, y) {
        if (!this.interactive) return; // 不可交互时不响应点击

        // 1. 立即关闭自动移动（核心修改：优先停止自动移动）
        this.autoMove.enabled = false; // 停止自动移动逻辑
        if (this.autoMove.timer) {
            setTimeout(() => {
                clearTimeout(this.autoMove.timer);
            }, 1000);
        }

        let img = this.img_data.find(img => {
            const scaledWidth = this.img_width * img.currentScale;
            const scaledHeight = this.img_height * img.currentScale;
            const offsetX = (scaledWidth - this.img_width) / 2;
            const offsetY = (scaledHeight - this.img_height) / 2;

            return x >= img.x - offsetX && x < img.x + scaledWidth - offsetX &&
                y >= img.y - offsetY && y < img.y + scaledHeight - offsetY;
        });
        if (img) {
            // 2. 计算当前点击的助手索引（避免越界）
            const assistantIndex = (img.index - 1) % this.assistants.length;
            const clickedAssistant = this.assistants[assistantIndex]; // 获取点击的助手信息

            // 3. 禁用交互防止重复点击
            this.interactive = false;
            this.if_movable = false;

            // 4. 计算屏幕中心位置
            const centerX = this.canvas.width / 2;
            const centerY = this.canvas.height / 2;
            const targetX = centerX - (this.img_width * this.scale.factor) / 2;
            const targetY = centerY - (this.img_height * this.scale.factor) / 2;

            // 5. 为所有图片添加透明度属性（首次点击时初始化）
            this.img_data.forEach(item => {
                if (item.opacity === undefined) item.opacity = 1;
            });

            // 6. 创建主图片动画：移动到中心并放大
            const mainImageTween = gsap.to(img, {
                x: targetX,
                y: targetY,
                currentScale: this.scale.factor * 1.5, // 比普通悬停更大一点
                duration: 1.2,
                ease: "power2.inOut",
                onUpdate: () => this.render_images()
            });

            // 7. 创建其他图片消失动画（关键修复）
            const otherImages = this.img_data.filter(item => item !== img);
            const fadeTweens = otherImages.map((item, index) => {
                return gsap.to(item, {
                    opacity: 0, // 控制透明度
                    duration: 0.8,
                    delay: index * 0.03, // 错开消失时间
                    ease: "power1.inOut",
                    onUpdate: () => this.render_images(),
                    onComplete: () => {
                        // 动画结束后彻底隐藏
                        item.hidden = true;
                    }
                });
            });

            // 8. 同步执行所有动画
            gsap.timeline()
                .add(mainImageTween)
                .add(fadeTweens, 0); // 0 表示同时开始



            // 9. 通过 Vuex 存储当前点击的助手信息（核心：将点击事件传递给 Vuex）
            // 延迟2秒执行操作
            setTimeout(async () => {
                try {
                    await AssistantService.add({
                        name: clickedAssistant.name,
                        description: clickedAssistant.description,
                        character: clickedAssistant.character,
                        knowledge_base_id: clickedAssistant.knowledge_base_id
                    });
                    console.log(`助手 ${clickedAssistant.name} 已保存到数据库`);
                } catch (error) {
                    console.error('保存助手失败:', error);
                }

                store.commit('setSelectedId', clickedAssistant.id)
                store.commit('setCurrentClickedAssistant', {
                    name: clickedAssistant.name,
                    description: clickedAssistant.description,
                    character: clickedAssistant.character,
                    knowledge_base_id: clickedAssistant.knowledge_base_id
                });


                console.log(`点击了助手: ${clickedAssistant.name}`, clickedAssistant);
            }, 2000); // 2000毫秒 = 2秒

        }
    },

    animate() {
        const now = performance.now();
        const deltaTime = now - this.autoMove.lastTime;

        if (this.autoMove.enabled && deltaTime >= this.autoMove.intervalTime) {
            const angle = this.autoMove.angle;
            const speed = this.autoMove.speed * (deltaTime / this.autoMove.intervalTime);
            const t = Math.cos(angle) * speed;
            const i = Math.sin(angle) * speed;
            this.move_imgs(t, i);
            this.autoMove.lastTime = now;
            if (now % 2000 < deltaTime) {
                this.autoMove.angle += (Math.random() * 0.5 - 0.25) * Math.PI;
            }
        }

        // 应用阻尼
        this.ease.x = this.ease.damping(this.ease.x);
        this.ease.y = this.ease.damping(this.ease.y);

        // 更新图片位置（核心修改：y方向应用列速度系数）
        if (Math.abs(this.ease.x) > 0.01 || Math.abs(this.ease.y) > 0.01) {
            this.img_data.forEach(img => {
                // x方向：所有列移动量相同
                img.x += this.ease.x;
                // y方向：根据图片所在列的速度系数调整移动量（视差效果）
                img.y += this.ease.y * this.columnSpeed[img.col_index];
            });
            this.render_images();
        }

        requestAnimationFrame(() => this.animate());
    }
};