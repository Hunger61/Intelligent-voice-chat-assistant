<template>
    <div class="w-full h-screen flex flex-col cur" :style="{
        filter: store.getters.getIsbackGround ? 'invert(88%) hue-rotate(180deg)' : 'invert(0%)',
        transition: 'filter 0.5s ease'
    }" :class="store.getters.getIsbackGround ? 'dark' : ''">
        <div ref="mie" id="mie" class="follow-element"></div>
        <div v-if="issuccess" class="fixed inset-0 flex items-center justify-center z-50 bg-black/50">
            <div
                class="bg-white rounded-lg shadow-xl p-6 max-w-md w-full mx-4 transform transition-all duration-300 scale-100 opacity-100">
                <div class="flex items-center">
                    <svg class="w-8 h-8 text-green-500 mr-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                        xmlns="http://www.w3.org/2000/svg">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                    </svg>
                    <div>
                        <h3 class="text-lg font-medium text-gray-900">操作成功</h3>
                        <p class="mt-1 text-sm text-gray-500">{{ issuccessMessage }}</p>
                    </div>
                </div>
            </div>
        </div>

        <div v-if="isErr" class="fixed inset-0 flex items-center justify-center z-50 bg-black/50">
            <div
                class="bg-white rounded-lg shadow-xl p-6 max-w-md w-full mx-4 transform transition-all duration-300 scale-100 opacity-100">
                <div class="flex items-center">
                    <!-- 错误图标（红色叉号） -->
                    <svg class="w-8 h-8 text-red-500 mr-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                        xmlns="http://www.w3.org/2000/svg">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12">
                        </path>
                    </svg>
                    <div>
                        <h3 class="text-lg font-medium text-gray-900">操作失败</h3>
                        <p class="mt-1 text-sm text-gray-500">{{ isErrMessage }}</p> <!-- 错误信息变量 -->
                    </div>
                </div>
            </div>
        </div>
        <!-- 头部 -->
        <div class="pb-4 pl-5 border-b border-gray-300">
            <h2 class="mt-2 text-xl font-medium flex  items-center webfont">
                <svg @click="goBack" t="1752904862514" class="icon   w-6 h-6 mr-1 hover:cursor-pointer "
                    viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="6359" width="200"
                    height="200">
                    <title>返回</title>
                    <path
                        d="M628.352 265.92h-176.64V128L64 314.24l387.712 186.304v-137.6h191.296c151.424 0 227.2 60.928 227.2 182.848 0 126.144-78.272 189.184-234.752 189.184H189.248V832H640c213.312 0 320-92.352 320-276.992 0-192.704-110.528-289.088-331.648-289.088z"
                        fill="#515151" p-id="6360"></path>
                </svg>
                智能语音机器人
            </h2>
            <p class="my-2 text-xs text-gray-600 ">
                提供一站式的智能语音解决方案，基于大语言模型全面升级NLP能力，显著降低运营成本与门槛。
            </p>
              <div class="iframe-container">
            <iframe ref="bongoIframe" src="/static/bongo.cat-master/index.html" frameborder="0" allowfullscreen
              class="bongo-iframe" @load="onIframeLoaded"></iframe>
          </div>
        </div>

        <!-- 主体 -->
        <div class="flex flex-1 overflow-hidden">


            <div
                class="w-[15vw] p-3 flex flex-col items-center bg-slate-200  shadow-md transition-all duration-300 hover:shadow-lg">
                <div @click="toFileList()" :class="select == 1 ? 'bg-blue-50 border-l-4 border-blue-500 shadow-md' : ''"
                    class="w-4/5 p-2    flex items-center bg-white mt-4 rounded-md cursor-pointer transition-all duration-200 hover:bg-blue-50 hover:translate-x-1 active:scale-95 shadow-sm hover:shadow">
                    <svg t="1753861616386" class="icon w-4 h-4 mr-2" viewBox="0 0 1024 1024" version="1.1"
                        xmlns="http://www.w3.org/2000/svg" p-id="3074" width="200" height="200">
                        <path
                            d="M843.2 64 175.552 64c-101.248 0-110.08 116.864-110.08 116.864s0 606.848 0 675.776c0 68.928 110.08 103.36 110.08 103.36l495.296 0 287.616-283.2c0 0 0-417.984 0-504.896C958.528 84.992 843.2 64 843.2 64zM888.768 648.64c0 35.328-37.312 37.184-37.312 37.184s-85.376 0-126.976 0c-41.536 0-41.152 31.488-41.152 31.488s0 81.28 0 125.888c0 44.48-22.528 46.4-22.528 46.4s-418.048 0-468.992 0-56.448-43.456-56.448-43.456 0-591.808 0-648.768 56.448-67.392 56.448-67.392 579.904 2.88 633.216 2.88c53.248 0 63.744 63.744 63.744 63.744S888.768 613.184 888.768 648.64z"
                            fill="#8A8F93" p-id="3075"></path>
                        <path
                            d="M754.752 373.696c0 19.264-15.488 34.752-34.688 34.752L300.288 408.448c-19.2 0-34.752-15.488-34.752-34.752l0 0c0-19.2 15.552-34.688 34.752-34.688l419.776 0C739.264 339.008 754.752 354.496 754.752 373.696L754.752 373.696z"
                            fill="#8A8F93" p-id="3076"></path>
                        <path
                            d="M680.64 550.336c0 18.56-15.104 33.664-33.728 33.664L299.264 584c-18.56 0-33.664-15.104-33.664-33.664l0 0c0-18.752 15.104-33.792 33.664-33.792l347.648 0C665.472 516.48 680.64 531.584 680.64 550.336L680.64 550.336z"
                            fill="#8A8F93" p-id="3077"></path>
                    </svg>
                    数据列表
                </div>
                <div @click="toChoose(2)" :class="select == 2 ? 'bg-blue-50 border-l-4 border-blue-500 shadow-md' : ''"
                    class="w-4/5 p-2    flex items-center bg-white mt-4 rounded-md cursor-pointer transition-all duration-200 hover:bg-blue-50 hover:translate-x-1 active:scale-95 shadow-sm hover:shadow">
                    <svg t="1753861471221" class="icon w-4 h-4 mr-2" viewBox="0 0 1024 1024" version="1.1"
                        xmlns="http://www.w3.org/2000/svg" p-id="2041" width="200" height="200">
                        <path
                            d="M414.273133 1024a19.76097 19.76097 0 0 1-19.741211-20.488101l8.762126-237.513979a19.749115 19.749115 0 0 1 4.202738-11.471084l503.439415-641.372015-822.359463 475.187017 249.409882 129.274208c9.688823 5.021748 13.47267 16.947289 8.450922 26.635125-5.023724 9.687835-16.946301 13.471682-26.635125 8.449934L38.362218 606.82539a19.758006 19.758006 0 1 1-0.793324-34.650361l932.344942-538.738859a19.759982 19.759982 0 0 1 29.505118 19.454706l-109.172395 912.697585a19.758994 19.758994 0 0 1-28.848132 15.124522L609.347756 847.568976l-181.518965 171.052626a19.754055 19.754055 0 0 1-13.555658 5.378398z m28.276109-250.126145l-6.748685 182.935685 156.731307-147.692555a19.76097 19.76097 0 0 1 22.780144-3.091294l239.112482 126.310359L950.834551 126.32913 442.549242 773.873855z"
                            p-id="2042"></path>
                    </svg>
                    提交数据
                </div>
                <div @click="toKonwledge()"
                    :class="select != 1 && select != 2 ? 'bg-blue-50 border-l-4 border-blue-500 shadow-md' : ''"
                    class="w-4/5 p-2    flex items-center bg-white mt-4 rounded-md cursor-pointer transition-all duration-200 hover:bg-blue-50 hover:translate-x-1 active:scale-95 shadow-sm hover:shadow">
                    <svg t="1753861684818" class="icon w-4 h-4 mr-2" viewBox="0 0 1024 1024" version="1.1"
                        xmlns="http://www.w3.org/2000/svg" p-id="4077" width="200" height="200">
                        <path
                            d="M256 51.2H51.2a51.2 51.2 0 0 0-51.2 51.2v819.2a51.2 51.2 0 0 0 51.2 51.2h204.8a51.2 51.2 0 0 0 51.2-51.2V102.4a51.2 51.2 0 0 0-51.2-51.2z m0 409.6H51.2V409.6h204.8z m0-102.4H51.2V307.2h204.8z m0-102.4H51.2V153.6h204.8zM972.8 51.2h-204.8a51.2 51.2 0 0 0-51.2 51.2v819.2a51.2 51.2 0 0 0 51.2 51.2h204.8a51.2 51.2 0 0 0 51.2-51.2V102.4a51.2 51.2 0 0 0-51.2-51.2z m0 409.6h-204.8V409.6h204.8z m0-102.4h-204.8V307.2h204.8z m0-102.4h-204.8V153.6h204.8zM614.4 51.2h-51.2v460.8H460.8V51.2H409.6a51.2 51.2 0 0 0-51.2 51.2v819.2a51.2 51.2 0 0 0 51.2 51.2h204.8a51.2 51.2 0 0 0 51.2-51.2V102.4a51.2 51.2 0 0 0-51.2-51.2z"
                            fill="#999999" p-id="4078"></path>
                    </svg>
                    知识库
                </div>

            </div>


            <!-- 右侧文件列表区域 -->
            <div class="flex-1 p-4 overflow-auto">

                <div v-if="select == 1" class="flex flex-col min-h-full p-6 bg-gray-50">
                    <!-- 顶部信息及操作栏 -->
                    <div class="flex items-center justify-end mb-4">

                        <div class="flex  items-center space-x-3">

                            <button @click="fetchFiles()"
                                class="px-4 py-2   bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400 transition-all">
                                <svg class="w-5 h-5 inline-block" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                                    xmlns="http://www.w3.org/2000/svg">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M4 4v5h.582m15.356 2A8.001 8.001 0 0 0 4.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 0 1-15.357-2m15.357 2H15">
                                    </path>
                                </svg>
                                刷新
                            </button>

                            <button @click="toChoose(2)"
                                class="px-4 py-2   bg-indigo-500 text-white rounded-md hover:bg-indigo-600 transition-all">+
                                导入数据
                            </button>
                        </div>
                    </div>

                    <!-- 文件列表表格 -->
                    <div class="bg-white rounded-md shadow overflow-hidden">
                        <table class="min-w-full">
                            <thead class="bg-gray-50 border-b border-gray-200">
                                <tr>
                                    <th class="px-4 py-3 text-left text-gray-700 font-medium">文件名称</th>
                                    <th class="px-4 py-3 text-left text-gray-700 font-medium">文件格式</th>

                                    <th class="px-4 py-3 text-left text-gray-700 font-medium">状态</th>

                                    <th class="px-4 py-3 text-left text-gray-700 font-medium">上传时间</th>
                                    <!-- 新增操作栏表头 -->
                                    <th class="px-4 py-3 text-left text-gray-700 font-medium">操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr class="border-b border-gray-200 hover:bg-gray-50 transition-all"
                                    v-for="(file, index) in userFiles" :key="file.file_id">
                                    <td class="px-4 py-3">
                                        <!-- 拆分文件名和扩展名 -->
                                        {{ file.file_name.includes('.') ? file.file_name.split('.').slice(0,
                                            -1).join('.') : file.file_name }}
                                    </td>
                                    <td class="px-4 py-3">
                                        <!-- 显示扩展名，如果没有则显示空或自定义文本 -->
                                        {{ file.file_name.includes('.') ? file.file_name.split('.').pop() : '' }}
                                    </td>
                                    <td class="px-4 py-3">
                                        <span class="flex items-center space-x-1">
                                            <svg class="w-4 h-4 text-green-500" fill="currentColor" viewBox="0 0 20 20">
                                                <path fill-rule="evenodd"
                                                    d="M10 18a8 8 0 1 0 0-16 8 8 0 0 0 0 16zm3.707-9.293a1 1 0 0 0-1.414-1.414L10 9.586 7.707 7.293a1 1 0 0 0-1.414 1.414l2.5 2.5a1 1 0 0 0 1.414 0l5-5z"
                                                    clip-rule="evenodd"></path>
                                            </svg>
                                            <span>{{ file.status }}</span>
                                        </span>
                                    </td>

                                    <td class="px-4 py-3">{{ formatDateTime(file.updated_at) }}</td>
                                    <!-- 新增操作栏内容 -->
                                    <td class="px-4 py-3 flex justify-between space-x-2">
                                        <button
                                            class="px-3 py-1 bg-red-500   text-white rounded hover:bg-red-600 transition-colors cursor-pointer"
                                            @click="deleteFun(() => deFile(file.file_id))">删除</button>
                                    </td>
                                </tr>
                                <!-- 可继续添加更多文件数据行 -->
                            </tbody>
                        </table>
                    </div>

                    <!-- 分页栏 -->
                    <div class="flex items-center justify-end mt-4">
                        <div class="flex items-center space-x-2">
                            <button class="px-3 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition-all">
                                <svg class="w-5 h-5 text-gray-700" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                                    xmlns="http://www.w3.org/2000/svg">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M15 19l-7-7 7-7"></path>
                                </svg>
                            </button>
                            <button class="px-3 py-2 bg-blue-500 text-white rounded-md">1</button>
                            <button class="px-3 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition-all">
                                <svg class="w-5 h-5 text-gray-700" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                                    xmlns="http://www.w3.org/2000/svg">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M9 5l7 7-7 7">
                                    </path>
                                </svg>
                            </button>

                        </div>
                    </div>
                </div>

                <div v-if="select == 2" class="flex flex-col min-h-full p-6 bg-gray-50">
                    <!-- 主内容容器 -->
                    <div class="flex-1 bg-white p-6 rounded-md shadow">
                        <!-- 表单区域 -->
                        <form class="space-y-6 " @submit.prevent="handleSubmit1">
                            <!-- 类目类型 -->
                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-1">类目类型</label>
                                <input type="text" value="本地类目"
                                    class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-blue-500"
                                    disabled>
                            </div>

                            <!-- 导入方式 -->
                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-2">导入方式</label>
                                <div class="flex items-center space-x-4">
                                    <label class="flex items-center cursor-pointer">
                                        <input type="radio" name="uploadType" value="local"
                                            v-model="formData1.uploadType" class="mr-2">
                                        <span class="text-gray-600">本地上传</span>
                                    </label>
                                    <label class="flex items-center cursor-pointer">
                                        <input type="radio" name="uploadType" value="oss" v-model="formData1.uploadType"
                                            class="mr-2">
                                        <span class="text-gray-600">OSS</span>
                                    </label>
                                </div>
                            </div>

                            <!-- 文件上传区域 -->
                            <div class="border-dashed border-2 relative    border-gray-300 rounded-md p-6 flex flex-col items-center justify-center space-y-2 cursor-pointer hover:border-blue-500 transition-colors"
                                @click="triggerFileInput1" @dragover.prevent @dragenter.prevent="handleDragEnter"
                                @dragleave.prevent="handleDragLeave" @drop.prevent="handleDrop">
                                <svg class="w-8 h-8 text-gray-500 hover:text-blue-500 transition-colors" fill="none"
                                    stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"></path>
                                </svg>
                                <p class="text-gray-600 hover:text-blue-500 transition-colors">点击或拖拽上传单个文件</p>
                                <p class="text-xs text-gray-500">
                                    支持.pdf,.doc,.docx,.txt,.md,.pptx,.png,.jpg,.jpeg,.bmp,.gif,.xls,.xlsx,.html 等格式<br>
                                    单文档最大限制100MB或1000页，excel文件建议不超过10MB，单图片最大限制20MB（同时要求图片短边大于15像素，长边小于8192像素，长宽比小于50）<br>
                                    已选择 ({{ fileList1.length }}/1) 个文件
                                </p>

                                <!-- 专门的文件选择按钮 -->
                                <button type="button"
                                    class="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition-colors">选择文件</button>

                                <!-- 已选文件列表 -->
                                <div class="w-full mt-4 space-y-2" v-if="fileList1.length > 0">
                                    <h4 class="text-sm font-medium text-gray-700">已选择文件：</h4>
                                    <div class="space-y-1 max-h-40 overflow-y-auto pr-2">
                                        <div class="flex items-center justify-between p-2 bg-gray-50 rounded"
                                            v-for="(file, index) in fileList1" :key="index">
                                            <span class="text-sm truncate max-w-[60%]">{{ file.name }}</span>
                                            <span class="text-xs text-gray-500">{{ formatFileSize1(file.size) }}</span>
                                            <button type="button" class="text-red-500 hover:text-red-700 ml-2"
                                                @click.stop="removeFile1(index)">
                                                <svg class="w-4 h-4" fill="none" stroke="currentColor"
                                                    viewBox="0 0 24 24">
                                                    <path stroke-linecap="round" stroke-linejoin="round"
                                                        stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                                                </svg>
                                            </button>
                                        </div>
                                    </div>
                                </div>

                                <!-- 文件输入框（限制单次选择1个文件） -->
                                <input type="file" ref="fileInput1" class="hidden" :accept="allowedFileTypes"
                                    @change="handleFileSelect1">
                            </div>

                            <!-- 文档识别 -->
                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-2">文档识别</label>
                                <div class="flex items-center space-x-4">
                                    <label
                                        class="flex-1 flex items-center justify-between bg-white border border-gray-300 rounded-md p-3 cursor-pointer hover:border-blue-500 transition-colors">
                                        <span class="text-gray-600">文档智能解析</span>
                                        <span class="text-sm text-gray-500">使用阿里云文档智能解析服务解析文档，抽取文档内容、层级结构等信息</span>
                                        <input type="radio" name="recognition" value="intelligent"
                                            v-model="formData1.recognition" class="ml-2">
                                    </label>
                                </div>
                            </div>

                            <!-- 操作按钮 -->
                            <div class="flex justify-end space-x-2">
                                <button type="button"
                                    class="px-4 py-2 bg-gray-300   text-gray-700 rounded-md hover:bg-gray-400 transition-colors"
                                    @click="resetForm1">
                                    取消
                                </button>
                                <button type="submit"
                                    class="px-4 py-2   bg-blue-500 text-white rounded-md hover:bg-blue-600 transition-colors flex items-center"
                                    :disabled="isSubmitting1 || fileList1.length === 0">
                                    <template v-if="isSubmitting1">
                                        <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white"
                                            xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor"
                                                stroke-width="4"></circle>
                                            <path class="opacity-75" fill="currentColor"
                                                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z">
                                            </path>
                                        </svg>
                                        上传中...
                                    </template>
                                    <template v-else>确认</template>
                                </button>
                            </div>
                        </form>
                    </div>
                </div>

                <div v-if="select == 3" class="flex flex-col min-h-full p-6 bg-gray-50">
                    <!-- 顶部信息及操作栏 -->
                    <div class="flex items-center justify-end mb-4">

                        <div class="flex  items-center space-x-3">

                            <button @click=" fetchKnowledges()"
                                class="px-4 py-2   bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400 transition-all">
                                <svg class="w-5 h-5 inline-block" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                                    xmlns="http://www.w3.org/2000/svg">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M4 4v5h.582m15.356 2A8.001 8.001 0 0 0 4.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 0 1-15.357-2m15.357 2H15">
                                    </path>
                                </svg>
                                刷新
                            </button>

                            <button @click="toChoose(4)"
                                class="px-4 py-2   bg-indigo-500 text-white rounded-md hover:bg-indigo-600 transition-all">+
                                新建知识库
                            </button>
                        </div>
                    </div>

                    <!-- 文件列表表格 -->
                    <div class="bg-white rounded-md shadow overflow-hidden">
                        <table class="min-w-full">
                            <thead class="bg-gray-50 border-b border-gray-200">
                                <tr>
                                    <th class="px-4 py-3 text-left text-gray-700 font-medium">名称</th>
                                    <th class="px-4 py-3 text-left text-gray-700 font-medium">任务数</th>
                                    <th class="px-4 py-3 text-left text-gray-700 font-medium">知识数</th>
                                    <th class="px-4 py-3 text-left text-gray-700 font-medium">更新时间</th>

                                    <th class="px-4 py-3 text-center text-gray-700 font-medium">操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <!-- 搜索无结果时显示 -->
                                <tr v-if="userKnowledges.length === 0">
                                    <td colspan="4" class="px-4 py-8 text-center text-gray-500">
                                        未创建自定义知识库
                                    </td>
                                </tr>

                                <!-- 文件数据行 -->
                                <tr class="border-b border-gray-200 hover:bg-gray-50 transition-all"
                                    v-for="(knowledge, index) in userKnowledges" :key="knowledge.knowledge_base_id">
                                    <td class="px-4 py-3">{{ knowledge.name }}</td>
                                    <td class="px-4 py-3">{{ knowledge?.job_list?.length || 0 }}</td>
                                    <td class="px-4 py-3">{{ knowledge.knowledge_file_num }}</td>
                                    <td class="px-4 py-3">{{ formatDateTime(knowledge.updated_at) }}</td>
                                    <td class="px-4 py-3 flex justify-around space-x-2">
                                        <button
                                            class="px-3 py-1 bg-blue-500   text-white rounded hover:bg-blue-600 transition-colors cursor-pointer"
                                            @click=" toViewKnows(knowledge.knowledge_base_id)">
                                            查看知识
                                        </button>
                                        <button
                                            class="px-3 py-1 bg-blue-500   text-white rounded hover:bg-blue-600 transition-colors cursor-pointer"
                                            @click="toViewTasks(knowledge)">
                                            查看任务
                                        </button>
                                        <button
                                            class="px-3 py-1 bg-yellow-500   text-white rounded hover:bg-yellow-600 transition-colors cursor-pointer"
                                            @click="toChooseKonws(knowledge.knowledge_base_id);">
                                            编辑
                                        </button>
                                        <button
                                            class="px-3 py-1 bg-red-500   text-white rounded hover:bg-red-600 transition-colors cursor-pointer"
                                            @click="deleteFun(() => toDeleteKonwledge(knowledge.knowledge_base_id))">
                                            删除
                                        </button>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <!-- 分页栏 -->
                    <div class="flex items-center justify-end mt-4">
                        <div class="flex items-center space-x-2">
                            <button class="px-3 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition-all">
                                <svg class="w-5 h-5 text-gray-700" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                                    xmlns="http://www.w3.org/2000/svg">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M15 19l-7-7 7-7"></path>
                                </svg>
                            </button>
                            <button class="px-3 py-2 bg-blue-500 text-white rounded-md">1</button>
                            <button class="px-3 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition-all">
                                <svg class="w-5 h-5 text-gray-700" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                                    xmlns="http://www.w3.org/2000/svg">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M9 5l7 7-7 7">
                                    </path>
                                </svg>
                            </button>

                        </div>
                    </div>
                </div>

                <div v-if="select == 4" class="flex flex-col min-h-full p-6 bg-gray-50">
                    <div class="knowledge-base-form">
                        <h2 class="text-xl font-bold mb-4">知识库基础信息</h2>
                        <form class="space-y-4">
                            <!-- 知识库名称 -->
                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-1">知识库名称*</label>
                                <input v-model="formData2.name" type="text"
                                    class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-blue-500"
                                    placeholder="请输入知识库名称" max-length="20" />
                                <p class="text-xs text-gray-500 mt-1">{{ formData2.name.length }}/20</p>
                            </div>

                            <!-- 知识库描述 -->
                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-1">知识库描述</label>
                                <textarea v-model="formData2.desc"
                                    class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-blue-500"
                                    placeholder="请输入知识库描述，对知识库包含的内容和数据的用途进行描述。" max-length="1000"></textarea>
                                <p class="text-xs text-gray-500 mt-1">{{ formData2.desc.length }}/1000</p>
                            </div>

                            <!-- 数据类型 -->
                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-2">数据类型</label>
                                <div class="flex space-x-4">
                                    <label class="flex items-center cursor-pointer">
                                        <input type="radio" name="dataType" value="unstructured"
                                            v-model="formData2.dataType" class="mr-2" />
                                        <span class="text-gray-600">
                                            非结构化数据
                                            <span class="text-xs text-gray-500 block">选择数据管理功能中已上传的 doc、pdf、md、txt
                                                等文件</span>
                                        </span>
                                    </label>
                                </div>
                            </div>

                            <!-- 知识库配置 -->
                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-2">知识库配置</label>
                                <div class="flex space-x-4">
                                    <label class="flex items-center cursor-pointer">
                                        <input type="radio" name="configMode" value="recommended"
                                            v-model="formData2.configMode" class="mr-2" />
                                        <span class="text-gray-600">
                                            推荐配置
                                            <span
                                                class="text-xs text-gray-500 block">自炼推荐配置，在效果、推理成本、检索时延等方面的最佳实践</span>
                                        </span>
                                    </label>

                                </div>
                                <!-- 配置模式对应的子配置，根据选择展示 -->
                                <div class="mt-2" v-if="formData2.configMode === 'recommended'">
                                    <div class="flex items-center mb-2">
                                        <span class="w-1/4 text-sm text-gray-700">多轮对话改写</span>
                                        <span class="text-gray-500">开启</span>
                                    </div>
                                    <div class="flex items-center mb-2">
                                        <span class="w-1/4 text-sm text-gray-700">Embedding模型</span>
                                        <span class="text-gray-500">官方向量v4(新版)</span>
                                    </div>
                                    <div class="flex items-center mb-2">
                                        <span class="w-1/4 text-sm text-gray-700">排序配置</span>
                                        <span class="text-gray-500">Rank模型 官方排序</span>
                                    </div>
                                    <div class="flex items-center mb-2">
                                        <span class="w-1/4 text-sm text-gray-700">相似度阈值</span>
                                        <input type="range" min="0.01" max="1" step="0.01"
                                            v-model.number="formData2.similarityThreshold" class="w-2/4 mr-2" />
                                        <input type="number" min="0.01" max="1" step="0.01"
                                            v-model.number="formData2.similarityThreshold"
                                            class="w-1/4 px-2 py-1 border border-gray-300 rounded-md" />
                                    </div>
                                </div>
                                <div class="mt-2" v-else-if="formData2.configMode === 'custom'">
                                    <!-- 自定义配置可扩展更多输入项，这里简单占位 -->
                                    <p class="text-gray-500">自定义配置内容，可根据实际需求添加表单字段...</p>
                                </div>
                            </div>

                            <!-- 向量存储类型 -->
                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-2">向量存储类型</label>
                                <div class="flex space-x-4">
                                    <label class="flex items-center cursor-pointer">
                                        <input type="radio" name="vectorStoreType" value="builtIn"
                                            v-model="formData2.vectorStoreType" class="mr-2" />
                                        <span class="text-gray-600">内置</span>
                                    </label>
                                </div>
                            </div>

                            <!-- 操作按钮 -->
                            <div class="flex justify-start space-x-2">
                                <button type="button"
                                    class="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 transition-colors relative  "
                                    @click="toChooseknowledge()">
                                    下一步
                                </button>
                                <button type="button"
                                    class="px-4 py-2 bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400 transition-colors relative  "
                                    @click="handleCancel">
                                    取消
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
                <div v-if="select == 5" class="flex flex-col min-h-full p-6 bg-gray-50">
                    <!-- 顶部信息及操作栏 -->
                    <div class="flex items-center justify-between mb-4">
                        <div>
                            <span @click="toChoose(3)" class=" cursor-pointer"> 知识库 ></span>
                            <span>> 知识列表</span>
                        </div>
                        <div class="flex  items-center space-x-3">

                            <button
                                class="px-4 py-2 bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400 transition-all">
                                <svg class="w-5 h-5 inline-block" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                                    xmlns="http://www.w3.org/2000/svg">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M4 4v5h.582m15.356 2A8.001 8.001 0 0 0 4.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 0 1-15.357-2m15.357 2H15">
                                    </path>
                                </svg>
                                刷新
                            </button>
                        </div>
                    </div>

                    <!-- 文件列表表格 -->
                    <div class="bg-white rounded-md shadow overflow-hidden">
                        <table class="min-w-full">
                            <thead class="bg-gray-50 border-b border-gray-200">
                                <tr>
                                    <th class="px-4 py-3 text-left text-gray-700 font-medium">任务名称</th>
                                    <th class="px-4 py-3 text-left text-gray-700 font-medium">上传时间</th>
                                    <!-- 新增操作栏表头 -->
                                    <th class="px-4 py-3 text-left text-gray-700 font-medium">操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr class="border-b border-gray-200 hover:bg-gray-50 transition-all"
                                    v-for="(know, index) in fileKonws" :key="know.file_id">
                                    <td class="px-4 py-3">{{ know.file_name }}</td>
                                    <td class="px-4 py-3">{{ know.updated_at }}</td>
                                    <!-- 新增操作栏内容 -->
                                    <td class="px-4 py-3 flex justify-between space-x-2">
                                        <button
                                            class="px-3 py-1 bg-red-500   text-white rounded hover:bg-red-600 transition-colors cursor-pointer"
                                            @click="deleteFun(() => toDeleteKonws(know.file_id))">删除</button>
                                    </td>
                                </tr>
                                <!-- 可继续添加更多文件数据行 -->
                            </tbody>
                        </table>
                    </div>

                    <!-- 分页栏 -->
                    <div class="flex items-center justify-end mt-4">
                        <div class="flex items-center space-x-2">
                            <button class="px-3 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition-all">
                                <svg class="w-5 h-5 text-gray-700" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                                    xmlns="http://www.w3.org/2000/svg">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M15 19l-7-7 7-7"></path>
                                </svg>
                            </button>
                            <button class="px-3 py-2 bg-blue-500 text-white rounded-md">1</button>
                            <button class="px-3 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition-all">
                                <svg class="w-5 h-5 text-gray-700" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                                    xmlns="http://www.w3.org/2000/svg">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M9 5l7 7-7 7">
                                    </path>
                                </svg>
                            </button>

                        </div>
                    </div>
                </div>

                <div v-if="select == 6" class="flex flex-col min-h-full p-6 bg-gray-50">
                    <!-- 顶部信息及操作栏 -->
                    <div class="flex items-center justify-between mb-4">
                        <div>
                            <span @click="toChoose(3)" class=" cursor-pointer"> 知识库 ></span>
                            <span>> 任务列表</span>
                        </div>
                        <div class="flex  items-center space-x-3">

                            <button @click="toViewKnows(chooseKonwId)"
                                class="px-4 py-2 bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400 transition-all">
                                <svg class="w-5 h-5 inline-block" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                                    xmlns="http://www.w3.org/2000/svg">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M4 4v5h.582m15.356 2A8.001 8.001 0 0 0 4.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 0 1-15.357-2m15.357 2H15">
                                    </path>
                                </svg>
                                刷新
                            </button>
                        </div>
                    </div>

                    <!-- 文件列表表格 -->
                    <div class="bg-white rounded-md shadow overflow-hidden">
                        <table class="min-w-full">
                            <thead class="bg-gray-50 border-b border-gray-200">
                                <tr>
                                    <th class="px-4 py-3 text-left text-gray-700 font-medium">任务名称</th>

                                    <th class="px-4 py-3 text-left text-gray-700 font-medium">状态</th>
                                    <th class="px-4 py-3 text-left text-gray-700 font-medium">创建时间</th>
                                    <th class="px-4 py-3 text-left text-gray-700 font-medium">上传时间</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr class="border-b border-gray-200 hover:bg-gray-50 transition-all"
                                    v-for="(task, index) in tasks" :key="task.id">
                                    <td class="px-4 py-3">{{ task.name }}</td>

                                    <td class="px-4 py-3">
                                        <span class="flex items-center space-x-1">
                                            <svg class="w-4 h-4 text-green-500" fill="currentColor" viewBox="0 0 20 20">
                                                <path fill-rule="evenodd"
                                                    d="M10 18a8 8 0 1 0 0-16 8 8 0 0 0 0 16zm3.707-9.293a1 1 0 0 0-1.414-1.414L10 9.586 7.707 7.293a1 1 0 0 0-1.414 1.414l2.5 2.5a1 1 0 0 0 1.414 0l5-5z"
                                                    clip-rule="evenodd"></path>
                                            </svg>
                                            <span>{{ task.status }}</span>
                                        </span>
                                    </td>
                                    <td class="px-4 py-3">{{ task.created_at }}</td>
                                    <td class="px-4 py-3">{{ task.updated_at }}</td>

                                </tr>
                                <!-- 可继续添加更多文件数据行 -->
                            </tbody>
                        </table>
                    </div>

                    <!-- 分页栏 -->
                    <div class="flex items-center justify-end mt-4">
                        <div class="flex items-center space-x-2">
                            <button class="px-3 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition-all">
                                <svg class="w-5 h-5 text-gray-700" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                                    xmlns="http://www.w3.org/2000/svg">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M15 19l-7-7 7-7"></path>
                                </svg>
                            </button>
                            <button class="px-3 py-2 bg-blue-500 text-white rounded-md">1</button>
                            <button class="px-3 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition-all">
                                <svg class="w-5 h-5 text-gray-700" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                                    xmlns="http://www.w3.org/2000/svg">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M9 5l7 7-7 7">
                                    </path>
                                </svg>
                            </button>

                        </div>
                    </div>
                </div>

                <div v-if="select == 7 || select == 8" class="flex flex-col min-h-full p-6 bg-gray-50">
                    <!-- 顶部信息及操作栏 -->
                    <div class="flex items-center justify-start mb-4">
                        <span @click="toChoose(3)" class=" cursor-pointer"> 知识库 ></span>
                        <span>> 选择</span>
                    </div>

                    <!-- 文件列表表格 -->
                    <div class="bg-white rounded-md shadow z-30 overflow-hidden">
                        <table class="min-w-full">
                            <thead class="bg-gray-50 border-b  border-gray-200">
                                <tr>
                                    <!-- 新增选项框表头 -->
                                    <!-- 在表头的全选框中添加v-model -->
                                    <th class="px-4 py-3 text-left text-gray-700 font-medium">
                                        <input type="checkbox"
                                            class="rounded border-gray-300  text-blue-500 focus:ring-blue-500"
                                            v-model="isAllChecked" @change="handleCheckAll">
                                    </th>
                                    <th class="px-4 py-3 text-left text-gray-700 font-medium">文件名称</th>
                                    <th class="px-4 py-3 text-left text-gray-700 font-medium">文件格式</th>

                                    <th class="px-4 py-3 text-left text-gray-700 font-medium">状态</th>


                                </tr>
                            </thead>
                            <tbody>
                                <!-- 仅渲染状态为 PARSE_SUCCESS 的文件行 -->
                                <tr class="border-b border-gray-200  hover:bg-gray-50 transition-all"
                                    v-for="(file, index) in userFiles" :key="file.file_id"> <!-- 直接在tr上判断状态 -->
                                    <!-- 新增行选项框 -->
                                    <td class="px-4 py-3">
                                        <input type="checkbox"
                                            class="rounded border-gray-300 text-blue-500 focus:ring-blue-500"
                                            v-model="file.checked" @change="handleCheckItem(file)">
                                    </td>
                                    <td class="px-4 py-3">
                                        <!-- 拆分文件名和扩展名 -->
                                        {{ file.file_name.includes('.') ? file.file_name.split('.').slice(0,
                                            -1).join('.') :
                                            file.file_name }}
                                    </td>
                                    <td class="px-4 py-3">
                                        <!-- 显示扩展名，如果没有则显示空或自定义文本 -->
                                        {{ file.file_name.includes('.') ? file.file_name.split('.').pop() : '' }}
                                    </td>
                                    <td class="px-4 py-3">
                                        <span class="flex items-center space-x-1">
                                            <svg class="w-4 h-4 text-green-500" fill="currentColor" viewBox="0 0 20 20">
                                                <path fill-rule="evenodd"
                                                    d="M10 18a8 8 0 1 0 0-16 8 8 0 0 0 0 16zm3.707-9.293a1 1 0 0 0-1.414-1.414L10 9.586 7.707 7.293a1 1 0 0 0-1.414 1.414l2.5 2.5a1 1 0 0 0 1.414 0l5-5z"
                                                    clip-rule="evenodd"></path>
                                            </svg>
                                            <span>{{ file.status }}</span>
                                        </span>
                                    </td>
                                </tr>
                                <!-- 无数据时显示 -->
                                <tr v-if="userFiles.length === 0">
                                    <td colspan="8" class="px-4 py-8 text-center text-gray-500">
                                        没有找到匹配的文件
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <!-- 分页栏 -->
                    <div class="flex items-center justify-end mt-4">
                        <div class="flex items-center space-x-2">
                            <button class="px-3 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition-all">
                                <svg class="w-5 h-5 text-gray-700" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                                    xmlns="http://www.w3.org/2000/svg">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M15 19l-7-7 7-7"></path>
                                </svg>
                            </button>
                            <button class="px-3 py-2 bg-blue-500 text-white rounded-md">1</button>
                            <button class="px-3 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition-all">
                                <svg class="w-5 h-5 text-gray-700" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                                    xmlns="http://www.w3.org/2000/svg">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M9 5l7 7-7 7">
                                    </path>
                                </svg>
                            </button>
                        </div>
                    </div>
                    <!-- 新增取消确认按钮区域 -->
                    <div class="flex items-center justify-end mt-4 space-x-3">

                        <button v-if="select == 7"
                            class="px-4 py-2 bg-blue-500   text-white rounded-md hover:bg-blue-600 transition-all"
                            @click="handleConfirm">
                            确认
                        </button>
                        <button v-if="select == 8"
                            class="px-4 py-2 bg-blue-500   text-white rounded-md hover:bg-blue-600 transition-all"
                            @click="handleAdd">
                            确认
                        </button>
                    </div>
                </div>

            </div>

        </div>


        <div v-show="showModal" id="cover"
            class="fixed inset-0 bg-black/30 flex items-center justify-center z-30 backdrop-blur-sm transition-opacity duration-300"
            @click="showModal = false; showD = false">
            <!-- 模态框容器（阻止事件冒泡） -->
            <div v-if="showD"
                class="addBox w-96 bg-white rounded-xl shadow-2xl overflow-hidden transform transition-all duration-300 scale-100 opacity-100"
                @click.stop>
                <!-- 标题区域 -->
                <div class="p-6 border-b">
                    <h3 class="title text-xl font-semibold text-gray-800 text-center">确认删除</h3>
                </div>

                <!-- 内容区域（可根据需要添加删除提示文本） -->
                <div class="p-6 text-center text-gray-600">
                    <p>此操作不可撤销，确定要删除吗？</p>
                </div>

                <!-- 按钮区域 -->
                <div class="px-6 pb-6 pt-2 flex justify-center gap-4">
                    <button @click="showModal = false; showD = false"
                        class="cancel px-6 py-2.5 mr-2 bg-gray-100 text-gray-700 text-sm font-medium rounded-lg  touch-effectBox transition-colors ">
                        取消
                    </button>
                    <button @click.stop="functionD(); showModal = false; showD = false"
                        class="sure px-6 py-2.5 ml-2 bg-red-500 text-white text-sm font-medium rounded-lg  touch-effectBox transition-colors ">
                        确定
                    </button>
                </div>
            </div>
        </div>
    </div>

</template>

<script setup>
import { ref, computed, onMounted, reactive, nextTick, watch } from 'vue' //vue
import { useRoute, useRouter } from 'vue-router'  //路由依赖
import KnowledgeService from '../api/knowledge';
import { useStore } from 'vuex'
import { createMieAnimation } from '../assets/js/miemie.js';
import { issuccess, isErr, isErrMessage, issuccessMessage, showSuccessMessage, showErrMessage } from '../assets/js/showErrOrTrue.js'; //成功失败消息提示
const store = useStore()
const router = useRouter()
// 创建元素引用
const mie = ref(null);
// 初始化动画实例
const mieAnimation = createMieAnimation(mie);

const showModal = ref(false)
const showD = ref(false)
const functionD = ref(null)  // 用于存储要执行的函数

// 保存函数并显示确认框
const deleteFun = (fun) => {
    functionD.value = fun  // 先赋值
    showModal.value = true
    showD.value = true
    // 不要在这里调用函数，应该在确认时调用
}

const emit = defineEmits(['page-loaded'])
const bongoIframe = ref(null)
let iframeWindow = null
let messageId = 0 // 用于追踪每个消息的ID

// 向子页面发送事件信息，并添加日志
const sendEventToIframe = (eventType, eventData) => {
  if (iframeWindow) {
    // 生成唯一消息ID用于追踪
    const currentId = messageId++;

    const message = {
      id: currentId,
      type: 'screenEvent',
      eventType,
      ...eventData,
      timestamp: new Date().toISOString()
    };

    try {
      iframeWindow.postMessage(message, '*'); // 生产环境中应指定具体域名而非*
    } catch (error) {
      console.error(`❌ 父页面发送${eventType}事件失败 (ID: ${currentId}):`, error);
    }
  } else {
    console.warn('⚠️ 无法发送事件 - iframe尚未加载完成');
  }
}

// 监听各种事件并转发给子页面
const setupEventListeners = () => {
  // 鼠标事件
  ['mousedown', 'mouseup', 'click'].forEach(eventType => {
    document.addEventListener(eventType, (e) => {
      sendEventToIframe(eventType, {
        x: e.clientX,
        y: e.clientY,
        which: e.which
      });
    });
  });
  // 监听所有 input 和 textarea 的键盘事件
  document.querySelectorAll('input, textarea').forEach(input => {
    ['keydown', 'keyup'].forEach(eventType => {
      input.addEventListener(eventType, (e) => {
        sendEventToIframe(eventType, {
          key: e.key,
          code: e.code,
          targetTag: e.target.tagName, // 可选：标记事件来源
        });
      });
    });
  });

  // 补充监听输入法组合事件
  ['compositionstart', 'compositionend'].forEach(eventType => {
    document.addEventListener(eventType, (e) => {
      sendEventToIframe(eventType, {
        isComposing: eventType === 'compositionstart', // 开始/结束组合
        data: e.data, // 组合的文本内容（如拼音、候选字）
        targetTag: e.target.tagName
      });
    });
  });

  // 优化现有键盘事件监听（过滤无效的组合阶段事件）
  ['keydown', 'keyup', 'keypress'].forEach(eventType => {
    document.addEventListener(eventType, (e) => {
      // 即使在组合阶段，也可以选择发送事件（根据需求调整）
      sendEventToIframe(eventType, {
        key: e.key,
        code: e.code,
        isComposing: e.isComposing,
        targetTag: e.target.tagName
      });
    });
  });

  // 触摸事件（移动设备）
  ['touchstart', 'touchend', 'touchmove'].forEach(eventType => {
    document.addEventListener(eventType, (e) => {
      if (e.touches.length > 0) {
        sendEventToIframe(eventType, {
          x: e.touches[0].clientX,
          y: e.touches[0].clientY,
          touchCount: e.touches.length
        });
      }
    });
  });
}

const onIframeLoaded = () => {
  console.log('✅ Bongo Cat 页面已加载完成，开始监听事件')
  iframeWindow = bongoIframe.value.contentWindow

  // 监听子页面可能返回的响应（如果需要双向通信）
  window.addEventListener('message', (event) => {
    if (event.source === iframeWindow && event.data.type === 'eventProcessed') {
      console.log(`📥 父页面确认子页面已处理事件 (ID: ${event.data.id})`);
    }
  });
}

//页面加载时
onMounted(() => {
    fetchFiles()
    mieAnimation.init(200, 200);
    fetchKnowledges()
      setupEventListeners()
    emit('page-loaded')
})

let timer = null // 用于存储定时器实例
const select = ref(1)
// 清除现有定时器的工具函数
const clearTimer = () => {
    if (timer) {
        clearInterval(timer)
        timer = null
    }
}

const toChoose = (x) => {
    select.value = x
}
const toFileList = () => {
    fetchFiles()
    select.value = 1
}
const toKonwledge = () => {
    fetchKnowledges()
    select.value = 3
}
const formatDateTime = (dateString) => {
    if (!dateString) return '';

    const date = new Date(dateString);

    // 处理无效日期
    if (isNaN(date.getTime())) return dateString;

    // 格式化日期：年-月-日 时:分:秒
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');

    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
const userFiles = ref([])
const isLoading = ref(false) // 添加加载状态变量


// 获取用户文件列表
const fetchFiles = async () => {
    isLoading.value = true


    try {
        const userId = store.getters.getUser.id
        if (!userId) {
            return
        }

        // 调用API获取文件列表
        const response = await KnowledgeService.getFilesByUserId(userId)

        // 检查响应是否有效
        if (!response) {

            return
        }

        // 检查响应状态
        if (response.success) {
            userFiles.value = response.data || []
            userFiles.value = userFiles.value.map(file => ({
                ...file,
                checked: false // 初始化未勾选
            }));
            console.log('获取文件列表成功:', userFiles.value)
        } else {

            console.error('获取文件列表失败:', response.message)
        }
    } catch (error) {
        console.error('获取文件列表异常:', error)

    } finally {
        isLoading.value = false
    }
}

const deFile = async (id) => {
    isLoading.value = true
    try {
        const userId = store.getters.getUser.id

        // 调用API获取文件列表
        const response = await KnowledgeService.deleteFile(id)
        // 检查响应是否有效
        if (!response) {
            console.log("没有响应")
            return
        }

        // 检查响应状态
        if (response.success) {
            const result = await KnowledgeService.getFilesByUserId(userId)
            if (response.success) {
                userFiles.value = result.data || []
                console.log('获取文件列表成功:', userFiles.value)
            }

            console.log('删除文件成功', userFiles.value)
            showSuccessMessage('删除文件成功')
        } else {

            console.error('删除文件失败:', response.message)
        }
    } catch (error) {
        console.error('删除文件异常:', error)

    } finally {
        isLoading.value = false
    }
}

// 表单数据
const formData1 = reactive({
    uploadType: 'local', // 上传方式：local/oss
    recognition: 'intelligent', // 识别方式
    tags: [] // 已添加标签
});

// 文件相关（限制最多1个文件）
const fileInput1 = ref(null);
const fileList1 = ref([]);
const maxFiles = 1; // 限制最多选择1个文件
const maxFileSizeMap = {
    '.png': 20, '.jpg': 20, '.jpeg': 20, '.bmp': 20, '.gif': 20, // 图片20MB
    '.xls': 10, '.xlsx': 10, // Excel 10MB
    default: 100 // 其他100MB
};
const allowedFileTypes = computed(() => {
    return [
        '.pdf', '.doc', '.docx', '.txt', '.md', '.pptx',
        '.png', '.jpg', '.jpeg', '.bmp', '.gif',
        '.xls', '.xlsx', '.html'
    ].join(',');
});

// 状态
const isSubmitting1 = ref(false);

// 触发文件选择
const triggerFileInput1 = () => {
    if (fileInput1.value) {
        // 重置input值，确保可以重复选择同一文件
        fileInput1.value.value = '';
        fileInput1.value.click();
    }
};

// 处理文件选择（单次只能选择1个文件）
const handleFileSelect1 = (e) => {
    const newFiles = Array.from(e.target.files);
    if (newFiles.length === 0) return;

    // 强制只取第一个文件
    const singleFile = newFiles[0];
    processSingleFile(singleFile);

    // 清空input值，允许再次选择
    e.target.value = '';
};

// 处理拖放文件（只保留第一个拖放的文件）
const handleDrop = (e) => {
    const newFiles = Array.from(e.dataTransfer.files);
    if (newFiles.length === 0) return;

    // 只处理第一个拖放的文件
    const singleFile = newFiles[0];
    processSingleFile(singleFile);

    // 移除拖放高亮
    const dropArea = e.currentTarget;
    dropArea.classList.remove('border-blue-500');
};

// 拖放进入时高亮
const handleDragEnter = (e) => {
    e.preventDefault();
    const dropArea = e.currentTarget;
    dropArea.classList.add('border-blue-500');
};

// 拖放离开时取消高亮
const handleDragLeave = (e) => {
    e.preventDefault();
    const dropArea = e.currentTarget;
    dropArea.classList.remove('border-blue-500');
};

// 处理单个文件（校验并添加到列表）
const processSingleFile = (file) => {
    // 校验文件是否符合要求
    if (!validateFile1(file)) return;

    // 由于限制1个文件，添加前先清空列表
    fileList1.value = [];
    fileList1.value.push(file);

    console.log(`已选择文件：${file.name}`);
};

// 文件校验（完整校验规则）
const validateFile1 = (file) => {
    const fileExt = file.name.substring(file.name.lastIndexOf('.')).toLowerCase();
    const fileName = file.name;

    // 1. 格式校验
    if (!allowedFileTypes.value.includes(fileExt)) {
        console.error(`错误：不支持${fileExt}格式文件，仅支持${allowedFileTypes.value}`);
        return false;
    }

    // 2. 大小校验
    const maxSizeMB = maxFileSizeMap[fileExt] || maxFileSizeMap.default;
    const maxSizeBytes = maxSizeMB * 1024 * 1024;
    if (file.size > maxSizeBytes) {
        console.error(`错误：${fileName}超过${maxSizeMB}MB限制`);
        return false;
    }

    // 3. 图片额外校验（尺寸和比例）
    if (['.png', '.jpg', '.jpeg', '.bmp', '.gif'].includes(fileExt)) {
        return new Promise((resolve) => {
            const img = new Image();
            img.onload = () => {
                const minSide = Math.min(img.width, img.height);
                const maxSide = Math.max(img.width, img.height);
                const aspectRatio = maxSide / minSide;

                // 短边校验
                if (minSide <= 15) {
                    console.error(`错误：${fileName}短边必须大于15像素（当前：${minSide}px）`);
                    resolve(false);
                    return;
                }

                // 长边校验
                if (maxSide >= 8192) {
                    console.error(`错误：${fileName}长边必须小于8192像素（当前：${maxSide}px）`);
                    resolve(false);
                    return;
                }

                // 长宽比校验
                if (aspectRatio >= 50) {
                    console.error(`错误：${fileName}长宽比必须小于50（当前：${aspectRatio.toFixed(2)}）`);
                    resolve(false);
                    return;
                }

                // 图片校验通过
                resolve(true);
            };
            img.onerror = () => {
                console.error(`错误：无法解析图片文件${fileName}，可能已损坏`);
                resolve(false);
            };
            img.src = URL.createObjectURL(file);
        });
    }

    // 4. 文档页数校验（前端无法直接获取，这里仅做提示性记录）
    if (['.pdf', '.doc', '.docx', '.pptx'].includes(fileExt)) {
        console.log(`提示：${fileName}需注意页数限制（最多1000页），上传后将由服务器校验`);
    }

    return true;
};

// 移除文件
const removeFile1 = (index) => {
    const removedFile = fileList1.value[index].name;
    fileList1.value.splice(index, 1);
    console.log(`已移除文件：${removedFile}`);
};

// 格式化文件大小
const formatFileSize1 = (bytes) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

// 表单提交（调用API上传）
const handleSubmit1 = async () => {
    // 基础校验
    if (fileList1.value.length === 0) {
        showErrMessage('请选择要上传的文件');
        return;
    }

    const file = fileList1.value[0];

    // 处理文件名(去除后缀) - 与后端要求一致
    const lastDotIndex = file.name.lastIndexOf('.');
    const fileNameWithoutExt = lastDotIndex > -1
        ? file.name.substring(0, lastDotIndex)
        : file.name;

    // 获取用户ID
    const userId = store.getters.getUser.id;
    if (!userId) {
        showErrMessage('无法获取用户信息，请重新登录');
        return;
    }

    isSubmitting1.value = true;

    try {
        const response = await KnowledgeService.uploadFile({
            file: file,
            fileName: fileNameWithoutExt,  // 无后缀的文件名（作为FormData字段名）
            userId: userId
        });

        if (response.success) {
            resetForm1();
            showSuccessMessage('上传成功')
        } else {
            showErrMessage(`上传失败：${response.message}`);
        }
    } catch (error) {
        console.error('上传异常:', error);
        showErrMessage(`上传失败：${error.message}`);
    } finally {
        isSubmitting1.value = false;
    }
};

// 重置表单
const resetForm1 = () => {
    // 重置表单数据
    formData1.uploadType = 'local';
    formData1.recognition = 'intelligent';
    formData1.tags = [];

    // 清空文件列表
    fileList1.value = [];
    if (fileInput1.value) {
        fileInput1.value.value = '';
    }

    console.log('表单已重置');
};

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
const userKnowledges = ref([])
const fileKonws = ref([])
const tasks = ref([])

// 新增全选状态变量
const isAllChecked = ref(false);

// 表单数据
const formData2 = reactive({
    name: '',
    desc: '',
    dataType: 'unstructured', // 默认非结构化数据
    configMode: 'recommended', // 默认推荐配置
    similarityThreshold: 0.20, // 相似度阈值默认值
    vectorStoreType: 'builtIn', // 默认内置存储
    knowledgeIds: []
});
const chooseKonwId = ref('')

// 获取用户文件列表
const toViewKnows = async (id) => {
    isLoading.value = true
    try {

        // 调用API获取知识库知识数据列表
        const response = await KnowledgeService.getKnowledgeBaseFiles(id)

        // 检查响应状态
        if (response.success) {
            fileKonws.value = response.data || []
            chooseKonwId.value = id
            console.log('获取知识库知识数据成功:', fileKonws.value)
            toChoose(5)
        } else {

            console.error('获取知识库知识数据失败:', response.message)
        }
    } catch (error) {
        console.error('获取知识库知识数据异常:', error)

    } finally {
        isLoading.value = false
    }
}
const toDeleteKonws = async (id) => {
    isLoading.value = true
    try {
        let array = []
        array.push(id)
        console.log(array)
        // 调用API获取文件列表
        const response = await KnowledgeService.removeFilesFromKnowledgeBase({
            knowledgeBaseId: chooseKonwId.value,
            fileIds: array
        })

        // 检查响应状态
        if (response.success) {
            toViewKnows(chooseKonwId.value)
            console.log('删除知识库文件成功:')
            showSuccessMessage('删除知识库文件成功')
        } else {
            showErrMessage('删除知识库文件失败')
            console.error('删除识库文件失败:', response.message)
        }
    } catch (error) {
        console.error('删除知识库文件异常:', error)

    } finally {
        isLoading.value = false
    }
}
const toViewTasks = async (knowledge) => {
    // 前置校验：避免knowledge为undefined导致的错误
    if (!knowledge || !knowledge.job_list) {
        console.error('无效的知识库数据：缺少job_list');
        tasks.value = []; // 清空任务列表
        toChoose(6); // 跳转到任务视图
        return;
    }

    isLoading.value = true;
    try {
        // 1. 提取job_ids（收集任务ID）
        const jobIds = [];
        // 先收集原始任务信息（如存在其他字段，如名称等）
        const originalJobs = [];
        for (let i = 0; i < knowledge.job_list.length; i++) {
            const job = knowledge.job_list[i];
            if (job && job.id) {
                jobIds.push(job.id);
                originalJobs.push(job); // 保存原始任务信息（用于后续合并）
            }
        }

        // 2. 若没有有效任务ID，提前处理
        if (jobIds.length === 0) {
            console.warn('该知识库没有可查看的任务ID');
            tasks.value = [];
            toChoose(6);
            return;
        }


        // 3. 调用API获取任务状态
        const response = await KnowledgeService.getKnowledgeBaseJobStatus({
            knowledgeBaseId: knowledge.knowledge_base_id,
            jobIds: jobIds
        });

        // 4. 处理响应数据（转换为任务对象数组）
        if (response?.success) {
            const taskData = response.data || {}; // 格式：{ "taskId1": "状态1", "taskId2": "状态2" }
            // 转换为数组：[{ id: "taskId1", status: "状态1", ...其他原始字段 }, ...]
            tasks.value = Object.entries(taskData).map(([id, status]) => {
                // 合并原始任务信息（如名称、创建时间等，若有）
                const originalJob = originalJobs.find(job => job.id === id) || {};
                return {
                    id: id, // 任务ID
                    status: status, // 任务状态（如"COMPLETED"）
                    ...originalJob // 合并原始任务的其他字段（如name、createTime等）
                };
            });

            console.log('获取知识库任务数据成功:', tasks.value);
            toChoose(6); // 跳转到任务视图
        } else {
            console.error('获取知识库任务数据失败:', response?.message || '未知错误');
            tasks.value = []; // 失败时清空任务列表
        }
    } catch (error) {
        console.error('获取知识库任务异常:', error);
        tasks.value = []; // 异常时清空任务列表
    } finally {
        isLoading.value = false;
    }
};

// 获取用户文件列表
const fetchKnowledges = async () => {
    isLoading.value = true
    try {
        const userId = store.getters.getUser.id
        if (!userId) {
            return
        }

        // 调用API获取文件列表
        const response = await KnowledgeService.getKnowledgeBasesByUserId(userId)
        console.log(response)

        // 检查响应是否有效
        if (!response) {

            return
        }

        // 检查响应状态
        if (response.success) {
            userKnowledges.value = response.data || []
            console.log('获取知识库成功:', userKnowledges.value)
        } else {

            console.error('获取知识库失败:', response.message)
        }
    } catch (error) {
        console.error('获取知识库异常:', error)

    } finally {
        isLoading.value = false
    }
}

const toDeleteKonwledge = async (id) => {
    isLoading.value = true
    try {
        if(!isLoading.value){
            return
        }

        // 调用API获取文件列表
        const response = await KnowledgeService.deleteKnowledgeBase(id)

        // 检查响应状态
        if (response.success) {
            fetchKnowledges()
            console.log('删除知识库成功:')
            showSuccessMessage('删除知识库成功')
        } else {
            showErrMessage('删除知识库失败')
            console.error('删除识库失败:', response.message)
        }
    } catch (error) {
        console.error('删除知识库异常:', error)

    } finally {
        isLoading.value = false
    }
}

const toChooseKonws = async (id) => {
    isLoading.value = true;
    try {
        const response = await KnowledgeService.getKnowledgeBaseFiles(id);
        chooseKonwId.value = id
        if (response.success) {
            fileKonws.value = response.data || [];

            // 筛选并勾选userFiles中与fileKonws有相同file_id的项，同时同步到knowledgeIds
            userFiles.value = userFiles.value.map(file => {
                // 检查当前file的file_id是否存在于fileKonws中（强制类型转换避免数字/字符串不匹配）
                const shouldCheck = fileKonws.value.some(item =>
                    String(item.file_id) === String(file.file_id)
                );

                return {
                    ...file,
                    checked: shouldCheck // 仅根据fileKonws匹配结果决定是否勾选
                };
            });

            // 同步勾选的file_id到formData2.knowledgeIds
            formData2.knowledgeIds = userFiles.value
                .filter(file => file.checked) // 筛选所有勾选的文件
                .map(file => file.file_id);   // 提取它们的file_id组成数组



            console.log(userFiles.value)

            // 更新全选状态
            updateAllCheckStatus();
            toChoose(8);
        } else {
            console.error('获取知识库知识数据失败:', response.message);
        }
    } catch (error) {
        console.error('获取知识库知识数据异常:', error);
    } finally {
        isLoading.value = false;
    }
};

const handleAdd = async () => {
    isLoading.value = true
    if (formData2.knowledgeIds.length === 0) {
        // 可以添加提示：请至少选择一个文件
        showErrMessage('请至少选择一个文件');
        return;
    }
    // 执行确认逻辑，例如提交表单
    console.log('选中的文件ID:', formData2.knowledgeIds);
    // ...其他逻辑

    try {
        const userId = store.getters.getUser.id
        if (!userId) {
            return
        }

        // 调用API获取文件列表
        const response = await KnowledgeService.addFilesToKnowledgeBase({
            knowledgeBaseId: chooseKonwId.value,
            fileIds: formData2.knowledgeIds
        })

        // 检查响应状态
        if (response.success) {
            console.log('添加知识数据成功:', userFiles.value)
            showSuccessMessage('添加知识数据成功')
            toKonwledge()
        } else {
            showErrMessage('添加知识数据失败')
            console.error('添加知识数据失败:', response.message)
        }
    } catch (error) {
        console.error('添加知识数据异常:', error)

    } finally {
        isLoading.value = false
    }
};

// 处理单个文件勾选
const handleCheckItem = (file) => {
    // 勾选时添加ID
    if (file.checked) {
        // 避免重复添加
        if (!formData2.knowledgeIds.includes(file.file_id)) {
            formData2.knowledgeIds.push(file.file_id);
        }
    }
    // 取消勾选时移除ID
    else {
        formData2.knowledgeIds = formData2.knowledgeIds.filter(id => id !== file.file_id);
    }

    // 检查是否所有可选文件都被勾选，更新全选状态
    updateAllCheckStatus();
};

// 完善全选处理函数
const handleCheckAll = (e) => {
    const isChecked = e.target.checked;
    isAllChecked.value = isChecked; // 同步更新全选状态

    if (!Array.isArray(userFiles.value)) {
        userFiles.value = []; // 强制转为数组
        return;
    }

    // 更新所有文件的勾选状态
    userFiles.value.forEach(file => {
        if (file.status === 'PARSE_SUCCESS') {
            file.checked = isChecked;
        }
    });

    // 更新knowledgeIds数组
    if (isChecked) {
        formData2.knowledgeIds = userFiles.value
            .filter(file => file.status === 'PARSE_SUCCESS')
            .map(file => file.file_id);
    } else {
        formData2.knowledgeIds = [];
    }
};

// 新增：更新全选状态的函数
const updateAllCheckStatus = () => {
    if (!Array.isArray(userFiles.value)) return;

    const validFiles = userFiles.value.filter(file => file.status === 'PARSE_SUCCESS');
    if (validFiles.length === 0) {
        isAllChecked.value = false;
        return;
    }

    // 检查是否所有有效文件都被勾选
    isAllChecked.value = validFiles.every(file => file.checked);
};



// 确认按钮处理（可选：添加验证）
const handleConfirm = async () => {
    isLoading.value = true
    if (formData2.knowledgeIds.length === 0) {
        // 可以添加提示：请至少选择一个文件
        showErrMessage('请至少选择一个文件');
        return;
    }
       if(!isLoading.value){
            return
        }
    // 执行确认逻辑，例如提交表单
    console.log('选中的文件ID:', formData2.knowledgeIds);
    // ...其他逻辑

    try {
        const userId = store.getters.getUser.id
        if (!userId) {
            return
        }

        // 调用API获取文件列表
        const response = await KnowledgeService.createKnowledgeBase({
            name: formData2.name,
            description: formData2.desc,
            userId: userId,
            fileIds: formData2.knowledgeIds
        })

        // 检查响应是否有效
        if (!response) {
            return
        }

        // 检查响应状态
        if (response.success) {
            console.log('创建知识库成功:', userFiles.value)
            toKonwledge()
            showSuccessMessage('创建知识库成功')
        } else {

            console.error('创建知识库失败:', response.message)
            showErrMessage('创建知识库失败')
        }
    } catch (error) {
        console.error('创建知识库异常:', error)
        showErrMessage('创建知识库失败')
    } finally {
        isLoading.value = false
    }
};

// 
const toChooseknowledge = () => {
    // 同样可先校验必要字段，然后调用创建空知识库的接口等
    if (!formData2.name.trim()) {
        showErrMessage('请填写知识库名称');
        return;
    }
    toChoose(7)
    console.log('创建空知识库，表单数据：', formData2);
};

// 取消按钮逻辑
const handleCancel = () => {
    // 可清空表单或跳转到其他页面等
    formData2.name = '';
    formData2.desc = '';
    formData2.dataType = 'unstructured';
    formData2.configMode = 'recommended';
    formData2.similarityThreshold = 0.20;
    formData2.vectorStoreType = 'builtIn';
    console.log('取消操作，表单已重置');
    // 实际可跳转到列表页：router.push('/knowledge-base-list');
};


const goBack = () => {
    nextTick(() => {
        router.push('/robot_test')
    })
}


// 监听 select.value 的变化，动态切换定时器
watch(
    () => select.value,
    (newVal) => {
        // 无论任何值变化，先清除之前的定时器（关键修改）
        clearTimer();

        // 只在值为 1 或 3 时创建新定时器
        if (newVal === 3) {
            console.log("xxxx")
            fetchKnowledges(); // 立即执行
            timer = setInterval(fetchKnowledges, 10000);
        } else if (newVal === 1) {
            fetchFiles(); // 立即执行
            timer = setInterval(fetchFiles, 10000);
        } else if (newVal == 5) {
            if (chooseKonwId.value != null) {
                timer = setInterval(toViewKnows(chooseKonwId.value), 10000);
            }

        }
        // 其他值：不创建新定时器，由于已执行 clearTimer()，旧定时器也会被清除
    },
    { immediate: true }
);
</script>
