import { sendStartLLMCommand, sendGenerateCommand, sendHangupCommand, sendInterruptCommand, sendResetCharacterCommand, sendResetHistoryCommand } from "./sendCommands";
import {showClickErrMessage,showErrMessage,showClickSuccessMessage} from './showErrOrTrue.js';
import { addNewObject } from './textMatter.js'; //小方块
import { classifyWeather } from './weather.js'; //天气分类
let ws;
let data;
// 建立与后端的websocket连接
export function connectwebsocket(store) {

    ws = new WebSocket('ws://localhost:9090/ai_assistant/text_chat');
    ws.addEventListener('error', (event => {
        console.error('websocket 连接失败')
        showClickErrMessage('websocket 连接失败,请返回主页重新尝试')
    }))
    ws.onopen = function () {
        console.log('与纯文本大模型的websocket连接已建立');

        // 启动大模型
        sendStartLLMCommand(store.getters.getSelectedId);
    }


    ws.onmessage = function (event) {

        data = JSON.parse(event.data);


        try {
            const data = JSON.parse(event.data);
            // 检查是否是Event类型（所有后端返回的数据都应包含event字段）
            if (!data.event) {
                console.warn('收到未知格式的消息:', data);
                return;
            }

            // 根据event类型进行分发处理
            switch (data.event) {
                case 'chat_res':  // 正常聊天回复
                console.log(data)
                    store.commit('setTextMessageContent', data)
                    store.commit('setIsHaveTextMessage', false)
                    if (data.chat_id == -1) {
                        let newMessage = store.getters.getTextMessageContent;
                        console.log("==========================",newMessage)

                        store.commit('addRTMbyId', {
                            message: {
                                type: 'ai', // 假设服务器返回的是AI消息
                                deepcontent:newMessage.deepcontent,
                                content: newMessage.content,
                                elapsedTime: newMessage.time,
                                play_id: data.play_id
                            },
                            id: store.getters.getSelectedId // 获取当前选中的会话ID
                        });
                        store.commit('setElapsedTime', 0)
                        store.commit('clearTextMessageContent')
                        store.commit('setIsTextChat', false)
                    }
                    // handleChatResponse(data.content);
                    break;

                case 'hangup':  // 大模型主动挂断
                    handleHangupEvent(data.reason);
                    break;
                case 'chat_func_event':
                    if (data.llm_event == "weather") {
                        let weatherArray = classifyWeather(data.message).categories
                        for (let i = 0; i < 10; i++) {
                            addNewObject(weatherArray, i)
                        }
                    } else if (data.llm_event == "browser_color_change") {
                        let message = data.message
                        if (message == 'dark') {
                            store.commit('setIsbackGround', true)
                        } else {
                            store.commit('setIsbackGround', false)
                        }

                    } else if (data.llm_event == "data_collection_cost") {
                        let platId = data.play_id
                        let elapsedTime = data.message

                        store.commit('setTextMessageTime', {
                            id: platId,
                            time: elapsedTime
                        })
                        store.commit('setIsHaveTextMessage', false)
                        store.commit('setElapsedTime', elapsedTime)
                    }
                    break;
                case 'error':  // 错误处理
                    handleErrorEvent(data);
                    break;

                default:
                    console.warn('收到未知的event类型:', data.event);
            }
        } catch (e) {
            console.error('解析消息出错:', e, '原始数据:', event.data);
        }


    }

}



// 处理聊天回复
function handleChatResponse(content) {
    console.log('收到AI回复:', content);

}

// 处理挂断事件a
function handleHangupEvent(reason) {
    console.log('连接已挂断，原因:', reason);

    // 1. 关闭WebSocket连接
    if (ws && ws.readyState === WebSocket.OPEN) {
        ws.close();
    }
    showClickSuccessMessage('已成功挂断，请返回首页')
}

// 处理错误事件
function handleErrorEvent(errorData) {
    console.error('收到错误:', errorData);

    // 根据command_type决定如何处理
    switch (errorData.command_type) {
        case 'start_llm':

            // 可以在这里自动重试，或者提示用户手动重试
            console.warn('启动大模型失败，需要重试');

            showClickErrMessage('大模型启动失败,请重试')

            break;

        case 'reset_character':
            console.warn('重设人格失败，需要重试');


            // 阻止人格更新到数据库
            // ...

            showErrMessage("重设人格失败，需要重试");

            break;

        case 'generate':
            console.warn('生成回复失败，可能需要重发消息');

            showErrMessage('请重新发送消息');

            break;

        case 'command':  // 通用命令错误
            if (errorData.message.includes('UnKnown command')) {
                console.error('发送了未知命令');

            } else {
                console.error('命令解析错误，可能需要重发');
            }
            break;

        default:
            console.warn('未知错误类型:', errorData.command_type);
    }
}

export function getWebsocket() {
    if (!ws) {
        throw new Error("websocket 已失效")
    }
    return ws;
}
