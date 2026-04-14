import { createStore } from 'vuex'
import { toRaw } from 'vue';


const store = createStore({
    state: {
        // 定义全局共享状态
        assistants: [],  //助手数组
        selectedId: 0,   //选中的助手id
        userInfo: {},    //用户信息
        txetMessageContent: {},
        robotTestMessage: [], //助手测试消息数组 （结构：[{id: 助手ID, messages: [消息列表]}]）
        nowTime: null, // 用于存储上次添加时间的时间戳，

        oldMessage: [],  //助手聊天消息数组

        newMessageContent: {},//新消息的内容，流式输出的时候使用
        newMessage: [],  //助手聊天新增消息数组
        newMessageIndex: 0,  //助手聊天消息指针
        pageScore: 8,

        isTextChat: false,   //是否正在接收消息

        currentClickedAssistant: null,  //点击的助手信息

        isCallActive: false, //是否进行通话
        isHaveVoice: false,  //是否静音
        isStartKnow: false,  //是否启用知识库
        isOnline: false,//是否联网搜索

        isHaveNewMessage: false, //是否在发送新的聊天消息
        isHaveTextMessage: false, //是否在发送新的聊天消息
        elapsedTime: 0,

        lastMessageTime: null, // 用于存储最后一次收到消息的时间戳

        isbackGround: false,
        speaker: '101001'

    },
    mutations: {
        // 同步修改状态的方法（Vuex 规定必须通过 mutations 修改 state）
        setElapsedTime(state, elapsedTime) {
            state.elapsedTime = elapsedTime
        },

        setIsbackGround(state, isbackGround) {
            state.isbackGround = isbackGround
        },

        setIsOnline(state, isOnline) {
            state.isOnline = isOnline
        },
        setSpeaker(state, speaker) {
            state.speaker = speaker
        },

        setLastMessageTime(state, time) {
            // 确保传入的是有效的时间戳
            if (typeof time === 'number' && !isNaN(time)) {
                state.lastMessageTime = time;
            } else {
                console.warn('Invalid timestamp for lastMessageTime');
            }
        },
        clearLastMessageTime(state) {
            state.lastMessageTime = null;
        },

        //新消息的流式存入
        setNewMessageContent(state, newMessageContent) {
            // 1. 检查新消息是否有效
            if (!newMessageContent) {
                return; // 无效消息直接返回
            }
            if (newMessageContent.play_id == 'endOfStream') {
                return; // 无效消息直接返回
            }
            // 2. 处理现有消息存在的情况
            if (state.newMessageContent) {
                // 2.1 ID相同：合并内容
                if (state.newMessageContent.play_id == newMessageContent.play_id) {
                    // 确保content是字符串类型再拼接（增加类型校验）
                    if (typeof state.newMessageContent.content === 'string' &&
                        typeof newMessageContent.content === 'string') {
                        state.newMessageContent.content += newMessageContent.content.trim();
                    }
                    return; // 合并后直接返回
                }

                // 2.2 ID不同：先保存现有消息（通过返回值让action处理后续commit）
                // 注意：mutation中不能直接调用commit，改为返回需要额外处理的数据
                const messageToSave = {
                    assistantId: state.selectedId, // 建议从state获取，而非getter
                    role: 'assistant',
                    content: state.newMessageContent.content
                };

                // 2.3 替换为新消息
                state.newMessageContent = newMessageContent;

                // 2.4 返回需要保存的消息（由action处理实际的commit）
                return messageToSave;
            }

            // 3. 现有消息不存在：直接赋值
            state.newMessageContent = newMessageContent;
            return;
        },
        //新消息的流式存入
        setTextMessageContent(state, txetMessageContent) {
            // 1. 检查新消息是否有效
            if (!txetMessageContent) {
                return; // 无效消息直接返回
            }
            if (txetMessageContent.chat_id == -1) {
                return; // 无效消息直接返回
            }
            // 2. 处理现有消息存在的情况
            if (state.txetMessageContent) {
                // 2.1 ID相同：合并内容
                if (txetMessageContent.chat_id == -2) {
                    if(state.txetMessageContent.play_id != txetMessageContent.play_id){
                        state.txetMessageContent=txetMessageContent
                        state.txetMessageContent.content=''
                    }
                    // 先确保 deepcontent 是字符串类型（不是则初始化为空字符串）
                    if (typeof state.txetMessageContent.deepcontent !== 'string') {
                        state.txetMessageContent.deepcontent = '';
                    }

                    // 再判断要拼接的 content 是否为字符串，是则进行拼接
                    if (typeof txetMessageContent.content === 'string') {
                        state.txetMessageContent.deepcontent += txetMessageContent.content.trim();
                    }
                    console.log("ai:",txetMessageContent)
                    console.log(state.txetMessageContent)
                    return
                }
                if (state.txetMessageContent.play_id == txetMessageContent.play_id) {
                    // 确保content是字符串类型再拼接（增加类型校验）
                    if (typeof state.txetMessageContent.content === 'string' &&
                        typeof txetMessageContent.content === 'string') {
                        state.txetMessageContent.content += txetMessageContent.content.trim();
                    }
                    return; // 合并后直接返回
                }

            }

            // 3. 现有消息不存在：直接赋值
            state.txetMessageContent = txetMessageContent;
            console.log("kkkkkkkkkkkkkkkk", state.txetMessageContent )
            return;
        },
        setTextMessageTime(state, message) {
            state.txetMessageContent.play_id = message.id
            state.txetMessageContent.content = '';
            state.txetMessageContent.time = message.time

            return;
        },



        //切换是否发送聊天消息，方便监听
        setIsHaveNewMessage(state, isHave) {
            state.isHaveNewMessage = isHave
        },
        //切换是否发送聊天消息，方便监听
        setIsHaveTextMessage(state, isHave) {
            state.isHaveTextMessage = isHave
        },


        //更新通话状态
        setIsCallActive(state, callState) {
            state.isCallActive = callState
        },
        //更新声音状态
        setIsHaveVoice(state, voiceState) {
            state.isHaveVoice = voiceState
        },
        //更新知识库状态
        setIsStartKnow(state, isStartKnow) {
            state.isStartKnow = isStartKnow
        },


        // 更新当前点击的助手信息
        setCurrentClickedAssistant(state, assistantInfo) {
            state.currentClickedAssistant = assistantInfo
        },
        // 清空当前点击的信息（可选）
        clearCurrentClickedAssistant(state) {
            state.currentClickedAssistant = null
        },

        /**
   * 向助手列表添加新助手
   * @param {Object} state - 状态对象
   * @param {Object} newAssistant - 新助手数据
   */
        addAssistants(state, newAssistant) {
            // 确保 assistants 是数组
            if (!Array.isArray(state.assistants)) {
                state.assistants = [];
            }
            console.log("xxx", newAssistant)
            state.assistants.push(newAssistant);
        },

        /**
    * 整体设置助手列表
    * @param {Object} state - 状态对象
    * @param {Array} assistants - 新的助手列表数组
    */
        setAssistants(state, assistants) {
            state.assistants = assistants
        },

        /**
   * 清空助手列表
   * @param {Object} state - 状态对象
   */
        clearAssistants(state) {
            state.assistants = []
        },

        /**
   * 删除指定助手
   * @param {Object} state - 状态对象
   */
        deleteAssistantById(state, targetId) {
            // 过滤掉 id 匹配的元素，重新赋值给 state.assistants
            state.assistants = state.assistants.filter(assistant => assistant.id !== targetId);
        },

        /**
  * 设置当前选中的助手 ID
  * @param {Object} state - 状态对象
  * @param {number|string} selectedId - 选中的助手 ID
  */
        setSelectedId(state, selectedId) {
            state.selectedId = selectedId
        },

        /**
     * 清空当前选中的助手 ID（重置为初始值）
     * @param {Object} state - 状态对象
     */
        clearSelectedId(state) {
            state.selectedId = 0
        },

        /**
    * 设置当前登录用户信息
    * @param {Object} state - 状态对象
    * @param {Object} user - 用户信息对象
    */
        setUser(state, user) {
            state.userInfo = user
        },

        /**
    * 清空当前登录用户信息（如退出登录时）
    * @param {Object} state - 状态对象
    */
        clearUser(state) {
            state.userInfo = {}
        },

        /**
   * 整体设置机器人测试会话数据
   * @param {Object} state - 状态对象
   * @param {Array} robotTestMessage - 新的会话数组
   */
        setRTM(state, robotTestMessage) {
            state.robotTestMessage = robotTestMessage
        },

        /**
    * 向指定 ID 的机器人测试会话添加消息
    * @param {Object} state - 状态对象
    * @param {Object} param1 - 参数对象
    * @param {Object} param1.message - 要添加的消息内容（包含发送者、内容等）
    * @param {number|string} param1.id - 会话 ID
    */
        addRTMbyId(state, { message, id }) {
            // 查找对应会话
            const conversation = state.robotTestMessage.find(item => item.id === id);
            const date = new Date();
            const currentTime = date.getTime(); // 当前时间的时间戳（毫秒）

            // 获取小时和分钟
            const hours = String(date.getHours()).padStart(2, '0');
            const minutes = String(date.getMinutes()).padStart(2, '0');
            const timeStr = `${hours}:${minutes}`;

            // 判断是否需要添加时间（间隔超过1分钟或首次添加）
            const shouldAddTime = !state.nowTime || (currentTime - state.nowTime >= 60000);

            if (conversation) {
                // 会话存在，添加消息
                const newMessage = { ...message };
                if (shouldAddTime) {
                    newMessage.time = timeStr;
                    state.nowTime = currentTime; // 刷新时间戳
                }
                conversation.messages.push(newMessage);
            } else {
                // 会话不存在，创建新会话
                const newMessage = { ...message };
                if (shouldAddTime) {
                    newMessage.time = timeStr;
                    state.nowTime = currentTime; // 刷新时间戳
                }
                state.robotTestMessage.push({
                    id,
                    messages: [newMessage]
                });
            }
        },
        clearNowTime(state) {
            state.nowTime = null
        },



        /**
 * 向指定 ID 的机器人测试会话添加历史消息（添加到列表开头）
 * @param {Object} state - 状态对象
 * @param {Object} param1 - 参数对象
 * @param {Array} param1.messages - 要添加的历史消息数组
 * @param {number|string} param1.id - 会话 ID
 */
        addRTMHistorybyId(state, { messages, id }) {
            // 查找对应会话
            const conversation = state.robotTestMessage.find(item => item.id === id);

            if (conversation) {
                // 会话存在，在消息列表开头插入历史消息
                // 为每条历史消息添加时间戳（如果没有）
                const messagesWithTime = messages.map(msg => {
                    // 如果消息已有时间戳则使用，否则生成一个
                    if (msg.time) return msg;

                    const date = new Date();
                    // 为了区分历史消息，这里生成比现有消息更早的时间
                    // 实际应用中应该使用消息本身的时间戳
                    date.setMinutes(date.getMinutes() - (messages.length + 10));

                    const hours = String(date.getHours()).padStart(2, '0');
                    const minutes = String(date.getMinutes()).padStart(2, '0');
                    return {
                        ...msg,
                        time: `${hours}:${minutes}`
                    };
                });

                // 在现有消息数组开头插入历史消息
                conversation.messages.unshift(...messagesWithTime);
            } else {
                // 会话不存在，创建新会话并添加历史消息
                const messagesWithTime = messages.map(msg => {
                    const date = new Date();
                    date.setMinutes(date.getMinutes() - (messages.length + 10));

                    const hours = String(date.getHours()).padStart(2, '0');
                    const minutes = String(date.getMinutes()).padStart(2, '0');
                    return {
                        ...msg,
                        time: `${hours}:${minutes}`
                    };
                });

                state.robotTestMessage.push({
                    id,
                    messages: messagesWithTime
                });
            }
        },

        /**
    * 更新指定 ID 的机器人测试会话属性（非消息内容，如会话标题、状态等）
    * @param {Object} state - 状态对象
    * @param {Object} param1 - 参数对象
    * @param {number|string} param1.id - 会话 ID
    * @param {Object} param1.data - 要更新的会话属性（如 {title: '新标题'}）
    */
        setRTMbyId(state, { id, data }) {
            const index = state.robotTestMessage.findIndex(item => item.id === id);

            if (index !== -1) {
                state.robotTestMessage.splice(index, 1, {
                    ...state.robotTestMessage[index],  // 保留原有会话属性
                    ...data                            // 用新数据覆盖会话属性
                });
            } else {
                console.warn(`未找到 ID 为 ${id} 的会话`);
            }
        },

        /**
     * 清除指定 ID 的机器人测试会话（删除整个会话）
     * @param {Object} state - 状态对象
     * @param {number|string} id - 要清除的会话 ID
     */
        clearRTMbyId(state, id) {
            state.robotTestMessage = state.robotTestMessage.filter(
                conversation => conversation.id !== id
            );
        },

        //更新测试聊天状态
        setIsTextChat(state, isTextChat) {
            state.isTextChat = isTextChat
        },



        addAssistantMessagebyId(state, { message, id }) {
            // 查找对应会话
            const conversation = state.oldMessage.find(item => item.id === id);

            const date = new Date();
            const hours = String(date.getHours()).padStart(2, '0');
            const minutes = String(date.getMinutes()).padStart(2, '0');

            if (conversation) {
                conversation.messages.push({
                    ...message,
                    time: `${hours}:${minutes}`
                });
            } else {
                state.oldMessage.push({
                    id,
                    messages: [
                        {
                            ...message,
                            time: `${hours}:${minutes}`
                        }
                    ]
                });
            }
        },
        addOldMessageHistorybyId(state, { messages, id }) {
            // 查找对应会话
            const conversation = state.oldMessage.find(item => item.id === id);

            // 获取当前时间（只计算一次，确保多条消息时间一致）


            if (conversation) {

                conversation.messages.push(...messages);
            } else {
                // 如果会话不存在，创建新会话并添加所有消息

                state.oldMessage.push({
                    id,
                    messages: messages
                });
            }
        },

        setOldMessagebyId(state, { id, data }) {  // 这里期望接收的参数是 { id, data }
            const conversation = state.oldMessage.find(item => item.id === id);
            if (conversation) {
                conversation.messages = data;  // 使用data赋值
            } else {
                state.oldMessage.push({ id, messages: data });  // 使用data作为消息
            }
        },
        clearOldMessagebyId(state, id) {
            state.oldMessage = state.oldMessage.filter(
                conversation => conversation.id !== id
            );
        },

        setNewMessagebyId(state, { id, data }) {  // 这里期望接收的参数是 { id, data }
            const conversation = state.newMessage.find(item => item.id === id);
            if (conversation) {
                conversation.messages = data;  // 使用data赋值
            } else {
                state.newMessage.push({ id, messages: data });  // 使用data作为消息
            }
        },
        addnewMessagebyId(state, { message, id }) {
            console.log('收到的消息:', message);
            console.log('当前newMessage数组:', state.newMessage);
            console.log('newIndex', state.newMessageIndex)

            // 查找对应会话
            let conversation = state.newMessage.find(item => item.id === id);

            // 修复1：确保会话存在，且messages一定是数组（防止undefined）
            if (!conversation) {
                console.log('会话不存在，创建新会话:', id);
                // 强制初始化messages为数组，避免后续变为undefined
                conversation = {
                    id: id,
                    messages: [] // 先初始化为空数组
                };
                state.newMessage.push(conversation);
            }

            // 修复2：确保messages是数组（防止被其他逻辑修改为非数组）
            if (!Array.isArray(conversation.messages)) {
                console.warn('检测到messages不是数组，强制重置为数组');
                conversation.messages = [];
            }

            // 添加消息到数组
            conversation.messages.push({
                ...message,
            });
            console.log('添加消息后，当前会话messages:', conversation.messages);

            // 修复3：简化判断条件，直接用当前消息长度与pageScore比较
            if (conversation.messages.length - state.newMessageIndex - 1 >= state.pageScore) {
                // 当前后端未实现 add_messages，保留本地索引推进逻辑。
                state.newMessageIndex = conversation.messages.length - 1;
            }

            // 最终检查会话状态
            const finalConversation = state.newMessage.find(item => item.id === id);
            console.log('操作完成后，会话状态:', finalConversation);
        },

        // 在原有方法下方添加
        addDurationToMessageById(state, { id, playId, duration }) {
            // 1. 根据id查找对应的会话
            const conversation = state.newMessage.find(item => item.id === id);

            if (!conversation) {
                console.warn(`未找到id为${id}的会话`);
                return;
            }

            // 2. 确保messages是数组
            if (!Array.isArray(conversation.messages)) {
                console.warn('会话的messages不是数组，无法添加duration');
                return;
            }

            // 3. 根据playId查找具体消息
            const targetMessage = conversation.messages.find(msg => msg.play_id == playId);
            if (targetMessage) {
                // 4. 给找到的消息添加duration
                // 先检查是否为空，为空则初始化为 0，再累加
                targetMessage.duration = (targetMessage.duration || 0) + duration;
            } else {
                console.warn(`在id=${id}的会话中未找到play_id=${playId}的消息`);
            }
        },

        addClosenewMessagebyId(state, id) {
            // 查找对应会话
            const conversation = state.newMessage.find(item => item.id === id);

            console.log(conversation)
            if (conversation) {

                // 调用服务并传入当前消息数组
                console.log("开始添加消息")
                console.log("newMessageIndex", state.newMessageIndex)

                console.log(conversation.messages.slice(state.newMessageIndex + 1))
                // 解包 Proxy 数组并切片
                const rawMessages = toRaw(conversation.messages).slice(state.newMessageIndex + 1);
                console.log("原始消息:", rawMessages);

                // 2. 过滤消息字段，只保留后端需要的三个字段
                const formattedMessages = rawMessages.map(msg => ({
                    assistantId: msg.assistantId,  // 确保存在且为数字类型
                    role: msg.role,                // 确保是"user"或"assistant"
                    content: msg.content           // 确保是字符串类型
                    // 移除多余字段：_id、created_at、deleted_at、updated_at等
                }));
                state.newMessageIndex = conversation.messages.length - 1;


            }
        },

        clearnewMessagebyId(state, id) {
            state.newMessage = state.newMessage.filter(
                conversation => conversation.id !== id
            );
        },
        clearNewMessageContent(start) {
            start.newMessageContent = {}
        },

        clearTextMessageContent(start) {
            start.txetMessageContent = {}
        },
        addMessageIndex(state) {
            state.newMessageIndex = state.newMessageIndex + 1
        },

        setMessageIndex(state, score) {
            state.newMessageIndex = score
        },
        clearMessageIndex(state) {
            state.newMessageIndex = 0
        },

        addOldMessageIndex(state) {
            state.oldMessageIndex = state.oldMessageIndex + 1
        },

        setOldMessageIndex(state, score) {
            state.oldMessageIndex = score
        },

    },
    actions: {
        // 异步操作和业务逻辑
    },
    getters: {
        // 计算属性
        getElapsedTime: state => state.elapsedTime,
        getLastMessageTime: state => state.lastMessageTime,
        getNewMessageContent: state => state.newMessageContent,

        getTextMessageContent: state => state.txetMessageContent,

        getIsHaveNewMessage: state => state.isHaveNewMessage,
        getisHaveTextMessage: state => state.isHaveTextMessage,
        getIsCallActive: state => state.isCallActive,
        getIsHaveVoice: state => state.isHaveVoice,
        getIsStartKnow: state => state.isStartKnow,
        getIsOnline: state => state.isOnline,
        getIsbackGround: state => state.isbackGround,

        getCurrentAssistant: state => state.currentClickedAssistant,
        getAllAssistants: state => state.assistants,
        getUser: state => state.userInfo,
        getSpeaker: state => state.speaker,
        getSelectedId: state => state.selectedId,
        getIsTextChat: state => state.isTextChat,
        getMessageIndex: state => state.newMessageIndex,
        getPageScore: state => state.pageScore,
        getAssistantById: state => (id) => {
            return state.assistants.find(assistant => assistant.id == id)
        },
        // 获取特定会话对象
        getRTMbyId: state => (id) => {
            let conversation = state.robotTestMessage.find(item => item.id === id);

            if (!conversation) {
                console.warn(`会话 ID ${id} 不存在，自动创建（不推荐在 getter 中修改 state）`);

                conversation = {
                    id,
                    messages: []
                };

                state.robotTestMessage.push(conversation);
            }
            return conversation;
        },
        getNewMessagebyId: state => (id) => {
            let conversation = state.newMessage.find(item => item.id === id);

            if (!conversation) {
                console.warn(`会话 ID ${id} 不存在，自动创建（不推荐在 getter 中修改 state）`);

                conversation = {
                    id,
                    messages: []
                };

                state.newMessage.push(conversation);
            }
            return conversation;
        },
        getAssistantMessagebyId: state => (id) => {
            let conversation = state.oldMessage.find(item => item.id === id);

            if (!conversation) {
                console.warn(`会话 ID ${id} 不存在，自动创建（不推荐在 getter 中修改 state）`);

                conversation = {
                    id,
                    messages: []
                };

                state.oldMessage.push(conversation);
            }
            return conversation;
        },
        getNewMessage: state => state.newMessage,
        getOldMessage: state => state.oldMessage,


        // 新增：获取各数组的消息长度
        getRTMMessageLength: state => (id) => {
            const conversation = state.robotTestMessage.find(item => item.id === id);
            return conversation ? conversation.messages.length : 0;
        },
        getNewMessageLength: state => (id) => {
            const conversation = state.newMessage.find(item => item.id === id);
            if (!conversation || !Array.isArray(conversation.messages)) {
                return 0;
            }
            return conversation.messages.length;
        },
        getOldMessageLength: state => (id) => {
            const conversation = state.oldMessage.find(item => item.id === id);
            return conversation ? conversation.messages.length : 0;
        },

    },
    modules: {
        // 模块化
    }
})

export default store