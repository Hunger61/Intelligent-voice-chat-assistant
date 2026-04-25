<template>
  <div id="app" :style="{
    filter: store.getters.getIsbackGround ? 'invert(88%) hue-rotate(180deg)' : 'invert(0%)',
    transition: 'filter 0.5s ease'
  }" :class="store.getters.getIsbackGround ? 'dark' : ''">
    <div class="pag cur" ref="physicsContainer" id="physics-page"> <!-- 添加ID -->
      <div id="sync-element" class="sync-element" @mousedown="startDrag"></div>
      <!-- <div v-for="(item, index) in dynamicElements" :key="index" :class="['sync-element', item.className]"
        :style="item.style" @mousedown="startDrag($event, index)" @dblclick="applyRandomForce(index)">
        新物体 {{ index + 1 }}
      </div> -->
      <div id="physics-container"></div>
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
          <div v-if="clickMessage" class="mt-4 flex gap-3">
            <button @click="clickMessage = false; issuccess = false; goBack()"
              class="flex-1 py-2 px-4 bg-gray-100 hover:bg-gray-200 text-gray-800 rounded-md transition-colors duration-200 text-sm font-medium">
              返回首页
            </button>
            <button @click="restart"
              class="flex-1 py-2 px-4 bg-green-500 hover:bg-green-600 text-white rounded-md transition-colors duration-200 text-sm font-medium">
              重启
            </button>
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
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
            <div>
              <h3 class="text-lg font-medium text-gray-900">操作失败</h3>
              <p class="mt-1 text-sm text-gray-500">{{ isErrMessage }}</p> <!-- 错误信息变量 -->
            </div>
          </div>
          <div v-if="clickMessage" class="mt-4 flex gap-3">
            <button @click="clickMessage = false; isErr = false; goBack()" class="flex-1 py-2 px-4 bg-gray-100 hover:bg-gray-200 text-gray-800 rounded-md transition-all duration-200 text-sm font-medium
            active:scale-95 active:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-300 focus:ring-offset-1
            touch-manipulation active:touch-manipulation" :active-class="active">
              返回首页
            </button>
            <button @click="restart" class="flex-1 py-2 px-4 bg-red-500 hover:bg-red-600 text-white rounded-md transition-all duration-200 text-sm font-medium
            active:scale-95 active:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-300 focus:ring-offset-1
            touch-manipulation active:touch-manipulation">
              重启
            </button>
          </div>

        </div>
      </div>

      <div class="w-full h-screen flex flex-col">
        <!-- 头部 -->
        <div class="pb-4 pl-5 border-b border-gray-300 overflow-hidden">
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


          <!-- 左列表 -->
          <div class="w-[40vw] p-5  bg-gray-50 border-r border-gray-300 overflow-y-auto ">
            <!-- 人格设置 -->
            <div class="bg-white p-4 rounded " :class="{ shake: isShaking }">
              <div class="flex justify-between items-center">
                <h3 class="font-medium">{{ localName }}</h3>
                <div class="flex">
                  <button @click="enableEditing" v-if="isEditing"
                    class="px-3 py-1   text-xs rounded bg-red-100 text-red-500 hover:bg-red-200 transition-all duration-200 active:scale-95 active:bg-red-300">
                    编辑
                  </button>
                  <button @click.stop=" changeAssistant()" v-else
                    class="px-3 py-1   text-xs rounded ml-2 bg-blue-100 text-blue-500 hover:bg-blue-200 transition-all duration-200 active:scale-95 active:bg-blue-300">
                    确定
                  </button>
                </div>
              </div>

              <p class="error text-red-500 text-xs text-center mt-1" v-if="errorMessage">{{ errorMessage }}</p>
              <div class="mt-3">
                <textarea v-model="localCharacter" :disabled="isEditing"
                  class="w-full h-80 p-4 border border-gray-300 rounded text-lg tracking-wide resize-none"
                  :class="{ 'cursor-not-allowed': isEditing }"></textarea>
              </div>
            </div>

            <!-- 知识库 -->
            <div class="bg-white p-4 rounded-lg mt-8 shadow-sm border border-gray-100 relative  ">
              <div class="flex justify-between items-center mb-1">
                <span class="text-sm font-medium text-gray-800">企业知识库</span>
                <button @click="toKonw()"
                  class="border   border-blue-500 text-blue-500 text-xs px-2.5 py-1 rounded hover:bg-blue-50 transition-colors duration-200 Bottom-touch-effect">
                  自定义知识库
                </button>
              </div>

              <!-- 增加滑动容器，限制高度并添加滚动条 -->
              <div class="mt-3 max-h-52 overflow-y-auto pr-1 scrollbar-thin ">

                <!-- 知识库列表项 -->

                <div v-for="(kb, index) in knowledgeBases" :key="kb.KnowledgeBaseID"
                  class="kb-item flex    justify-between items-center p-3 rounded-lg mt-2 cursor-pointer transition-all duration-200 "
                  :class="{
                    'bg-blue-50 border-l-4 border-blue-500': localKonwId == kb.KnowledgeBaseID,  // 选中状态样式
                    'bg-gray-50 hover:bg-gray-100': localKonwId != kb.KnowledgeBaseID            // 非选中状态样式
                  }" @click="handleKnowledgeBaseClick(kb.KnowledgeBaseID)">
                  <span class="text-gray-800 font-normal">{{ kb.KnowledgeBaseName }}</span>
                  <span class="text-gray-400 w-5 h-5 flex items-center justify-center">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"
                      class="w-4 h-4">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                    </svg>
                  </span>
                </div>
                <!-- 空状态提示（可选） -->
                <div v-if="knowledgeBases.length === 0"
                  class="flex flex-col items-center justify-center py-8 text-gray-500 text-sm">
                  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"
                    class="w-8 h-8 mb-2">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                      d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                  暂无知识库数据
                </div>
              </div>
            </div>
          </div>

          <!-- 右列表 -->
          <div class="flex-1 flex flex-col">
            <!-- Top Bar -->
            <div class="flex justify-between items-center p-4 border-b border-gray-300">
              <h3 class="text-sm font-medium flex">效果测试
              </h3>
              <button @click="resetConversation" class="flex   items-center text-sm text-gray-600 hover:text-gray-800">
                <RotateCcw color="#111" class="w-5 h-5" />
                重置对话
              </button>
            </div>

            <!-- Chat Content -->
            <div class="flex-1 bg-gray-50 overflow-y-auto relative  flex flex-col items-center " ref="scrollContainer">


              <div class=" w-11/12 mx-auto py-4">
                <div v-for="(message, index) in store.getters.getRTMbyId(store.getters.getSelectedId).messages"
                  :key="index" class="flex flex-col items-center">
                  <div class="text-xs mt-3">
                    {{ message.time }}
                  </div>
                  <div :class="{ 'justify-end': message.type === 'user', 'justify-start': message.type === 'ai' }"
                    class="flex mt-1 w-full">
                    <div v-if="message.type === 'ai'"
                      class="w-10 h-10 border-solid border flex justify-center items-center  rounded-full flex-shrink-0">
                      <svg t="1752828206160" v-if="message.type === 'ai'" class="icon w-6 h-6" viewBox="0 0 1179 1024"
                        version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="28052" width="200" height="200">
                        <path
                          d="M597.215632 994.574713h403.714943s43.549425-8.945287 43.549425-114.64092 94.16092-577.677241-459.976092-577.677241-457.151264 541.425287-457.151264 541.425287-25.423448 160.77977 54.848735 157.013333 415.014253-6.12046 415.014253-6.120459z"
                          fill="#FFFFFF" p-id="28053"></path>
                        <path
                          d="M1071.786667 712.798161h72.503908v136.297931h-72.503908zM36.016552 712.798161h72.503908v136.297931H36.016552z"
                          fill="#EA5D5C" p-id="28054"></path>
                        <path
                          d="M305.68366 559.40926l556.254412-1.165018 0.398364 190.20464-556.254412 1.165018-0.398364-190.20464Z"
                          fill="#4C66AF" p-id="28055"></path>
                        <path
                          d="M1129.931034 680.312644h-59.556781c-3.295632-152.069885-67.56046-258.942529-172.079081-324.384368l115.347127-238.462529a47.08046 47.08046 0 1 0-42.372414-20.48l-114.640919 236.57931a625.934713 625.934713 0 0 0-269.30023-53.200919 625.228506 625.228506 0 0 0-270.006437 54.848736l-115.817931-235.402299a47.08046 47.08046 0 1 0-42.372414 20.715402l117.701149 238.462529c-103.812414 65.441839-167.135632 173.02069-169.960459 324.61977H47.786667a47.08046 47.08046 0 0 0-47.08046 47.08046v117.701149a47.08046 47.08046 0 0 0 47.08046 47.08046h58.615172v57.908965a70.62069 70.62069 0 0 0 70.62069 70.62069l823.908046-1.647816a70.62069 70.62069 0 0 0 70.620689-70.62069v-57.908965h59.085977a47.08046 47.08046 0 0 0 47.08046-47.08046v-117.701149A47.08046 47.08046 0 0 0 1129.931034 680.312644zM94.16092 847.212874H47.08046v-117.70115h47.08046v117.70115z m929.83908 103.106206a23.54023 23.54023 0 0 1-23.54023 23.54023l-823.908046 1.647816a23.54023 23.54023 0 0 1-23.54023-23.540229v-258.942529c0-329.563218 303.668966-365.57977 434.788046-365.815173s435.494253 34.604138 436.20046 363.931954z m105.46023-105.224827h-47.08046v-117.70115h47.08046v117.70115z"
                          fill="#3F4651" p-id="28056">
                        </path>
                        <path
                          d="M464.684138 135.827126l22.363218-19.53839 40.018391 62.381609a30.131494 30.131494 0 0 0 25.423448 13.888735h2.824828a30.131494 30.131494 0 0 0 25.188046-19.067586l20.715402-79.095172 21.186207 74.387126v2.118621a30.366897 30.366897 0 0 0 52.494713 6.826667l30.366896-57.202759 13.182529 12.947126a30.131494 30.131494 0 0 0 21.186207 8.709886h57.673563a23.54023 23.54023 0 0 0 23.54023-23.54023 23.54023 23.54023 0 0 0-23.54023-23.54023h-50.140689l-23.54023-23.54023a30.366897 30.366897 0 0 0-45.668046 3.766437l-21.42161 40.01839L629.465747 19.302989a30.131494 30.131494 0 0 0-28.012873-19.067587 30.131494 30.131494 0 0 0-28.012874 19.067587l-26.60046 101.693793-29.660689-47.08046a30.366897 30.366897 0 0 0-20.48-13.653333 30.837701 30.837701 0 0 0-23.54023 6.826666l-32.250115 28.248276h-60.027586a23.54023 23.54023 0 0 0-23.54023 23.54023 23.54023 23.54023 0 0 0 23.54023 23.54023h66.148046a31.308506 31.308506 0 0 0 17.655172-6.591265zM776.121379 532.950805H404.421149A121.232184 121.232184 0 0 0 282.482759 639.352644a117.701149 117.701149 0 0 0 117.701149 129.000459h371.70023a121.232184 121.232184 0 0 0 121.938391-106.401839 117.701149 117.701149 0 0 0-117.70115-129.000459z m0 188.321839H402.302529a72.503908 72.503908 0 0 1-72.268506-56.496552 70.62069 70.62069 0 0 1 68.972874-84.744828h373.81885a72.503908 72.503908 0 0 1 72.268506 56.496552 70.62069 70.62069 0 0 1-68.502069 84.744828z"
                          fill="#3F4651" p-id="28057"></path>
                      </svg>
                    </div>


                    <div class=" max-w-[70%] " :class="{
                      'ml-3': message.type === 'ai',
                      'mr-3': message.type === 'user',
                      'text-gray-600': message.type === 'user'
                    }">
                      <div v-if="message.elapsedTime"
                        class="p-1.5 px-2 bg-slate-600 text-white text-xs rounded-md ml-2 w-auto max-w-36 opacity-80">
                        数据搜索时间 {{ message.elapsedTime }}s
                      </div>
                      <template v-if="message.type === 'ai'">
                        <div
                          class="p-4 rounded-lg leading-5 bg-gray-50 border border-gray-100 shadow-sm max-h-36 overflow-y-auto relative   mb-3"
                          v-if="typeof message.deepcontent === 'string' && message.deepcontent.trim() !== ''">
                          <div class="text-sm text-gray-500 mb-2 font-medium">深度思考</div>
                          <div class="text-gray-800 whitespace-pre-wrap text-xs">
                            {{ message.deepcontent }}
                          </div>
                        </div>

                        <!-- 普通内容 -->
                        <div class="p-4 rounded-lg leading-5 bg-white border border-gray-100 shadow-sm">
                          <div class="text-gray-800 whitespace-pre-wrap">
                            {{ message.content }}
                          </div>
                        </div>
                      </template>
                      <div v-if="message.type === 'user'"
                        class="information bg-blue-200 border-blue-100 p-4 rounded-lg shadow-sm  inline-block leading-5">
                        {{ message.content }}
                      </div>
                    </div>

                    <div v-if="message.type === 'user'"
                      class="w-10 h-10 border-solid border flex justify-center items-center  rounded-full flex-shrink-0">
                      <svg t="1754294706915" class="icon" viewBox="0 0 1024 1024" version="1.1"
                        xmlns="http://www.w3.org/2000/svg" p-id="9616" width="200" height="200">
                        <path
                          d="M512.524936 0.008332C229.809447 0.008332 0.587428 229.230351 0.587428 511.94584c0 174.13291 86.976883 327.943301 219.835665 420.423679 25.99266-90.522283 91.847122-158.622304 181.419521-189.5102a331.426209 331.426209 0 0 1 8.557288-2.807991c1.3665-0.429114 2.716335-0.870727 4.091168-1.283176a323.077229 323.077229 0 0 1 12.802603-3.562066l0.254136-0.058326a327.101737 327.101737 0 0 1 12.910924-2.978803c1.383164-0.291631 2.778827-0.554099 4.166158-0.829065a341.275007 341.275007 0 0 1 14.160772-2.512194 346.915985 346.915985 0 0 1 9.682151-1.354001c1.424826-0.179145 2.84132-0.37912 4.274478-0.5416a355.210806 355.210806 0 0 1 14.039953-1.354002h18.539404c1.891436 0.112486 3.778705 0.112486 5.670141 0h1.533146c-133.117084 0-241.028911-107.911827-241.028911-241.028911 0-1.266512 0.074991-2.516359 0.095822-3.774539-1.31234-152.377233 141.516058-244.178526 241.17056-237.37519 102.270849-6.965816 250.006982 89.397421 241.383034 248.353017-0.254136 0.158314-0.529102 0.279133-0.783237 0.43328-4.04534 129.575849-110.286537 233.392343-240.841434 233.392343h25.980162c4.724423 0.354123 9.394687 0.820733 14.035786 1.354002 1.437325 0.16248 2.857984 0.362456 4.282811 0.5416a359.614435 359.614435 0 0 1 14.402408 2.124741c3.153782 0.533268 6.2784 1.11653 9.386355 1.733121 1.412328 0.279133 2.837154 0.545767 4.236982 0.841564a327.626673 327.626673 0 0 1 12.59013 2.903813c0.279133 0.070825 0.566598 0.137483 0.84573 0.212474a318.461125 318.461125 0 0 1 12.42765 3.457911c1.470654 0.437447 2.912145 0.912389 4.366134 1.3665 2.753831 0.866561 5.490996 1.758119 8.207331 2.691338 89.605728 30.850401 155.497685 98.921258 181.536173 189.426876 132.742129-92.497042 219.635689-246.236608 219.635689-420.286195 0.008332-282.719655-229.20952-511.937508-511.929175-511.937508z"
                          fill="#A6D4AE" p-id="9617"></path>
                        <path
                          d="M753.36637 492.185752c-70.674706 43.161398-216.244436-31.629472-240.603963-134.708556-24.326197 103.449872-170.645836 178.444884-240.912258 134.271109a243.574433 243.574433 0 0 1-0.258302-10.977826c-0.020831 1.262346-0.095822 2.512193-0.095822 3.774539 0 133.117084 107.911827 241.028911 241.028911 241.028911 130.554896 0.004166 236.796094-103.812328 240.841434-233.388177z"
                          fill="#FCE9EA" p-id="9618"></path>
                        <path
                          d="M623.290581 742.805159c0.916555 0.316628 1.824777 0.645755 2.733 0.966549a119.589568 119.589568 0 0 1-10.448724 22.122299c17.243728 26.317621 12.931755 141.561886-12.944254 123.359941l-45.11116-31.650303-44.994507-31.650303 2.953806-2.083079c-0.983213 0.024997-1.96226 0.074991-2.953806 0.074991-1.033207 0-2.04975-0.05416-3.074625-0.079157l2.953806 2.087245-44.994507 31.650303-45.11116 31.650303c-25.809349 18.222776-30.167151-97.300622-12.877595-123.451596a119.714553 119.714553 0 0 1-10.373733-21.95982c0.929053-0.333293 1.858107-0.666585 2.791325-0.987379-89.572399 30.887896-155.42686 98.992083-181.41952 189.5102 82.84822 57.672127 183.519264 91.513829 292.101843 91.513828 108.670068 0 209.420269-33.895862 292.301818-91.651312-26.030156-90.501452-91.926279-158.57231-181.532007-189.42271zM468.467814 727.473697c1.424826-0.179145 2.84132-0.37912 4.274478-0.5416-1.433158 0.16248-2.845486 0.362456-4.274478 0.5416zM454.077904 729.594272zM440.458733 732.168957c1.383164-0.291631 2.778827-0.554099 4.166158-0.829065-1.387331 0.274966-2.782994 0.537434-4.166158 0.829065zM427.293673 735.20192l0.254136-0.058326c-0.083323 0.024997-0.170812 0.041662-0.254136 0.058326zM580.616624 731.331559c1.412328 0.279133 2.837154 0.545767 4.236983 0.841564-1.399829-0.295797-2.824655-0.562431-4.236983-0.841564zM610.721282 738.747321c1.470654 0.437447 2.912145 0.912389 4.366134 1.3665-1.453989-0.454111-2.899646-0.929053-4.366134-1.3665zM597.443737 735.076936c0.279133 0.070825 0.566598 0.137483 0.84573 0.212474-0.279133-0.074991-0.562431-0.141649-0.84573-0.212474zM410.399902 740.051328c1.3665-0.429114 2.716335-0.870727 4.091168-1.283176-1.370666 0.41245-2.724667 0.854062-4.091168 1.283176zM552.545051 726.932097c1.437325 0.16248 2.857984 0.362456 4.28281 0.5416-1.424826-0.179145-2.845486-0.37912-4.28281-0.5416zM566.497514 728.827698z"
                          fill="#F08E83" p-id="9619"></path>
                        <path
                          d="M409.425021 765.802352c3.43708-5.199365 7.724057-6.886659 12.877595-3.262102l45.11116 31.650303 42.036535 29.67971c1.024875 0.024997 2.041417 0.079157 3.074625 0.079157 0.991546 0 1.970593-0.049994 2.953806-0.074991l42.040701-29.683876 45.11116-31.650303c5.178535-3.645388 9.490508-1.920599 12.944254 3.353757a119.464584 119.464584 0 0 0 10.448724-22.122299c-0.912389-0.320794-1.820611-0.649921-2.733-0.966549a325.843558 325.843558 0 0 0-8.207331-2.691338c-1.453989-0.454111-2.89548-0.929053-4.366134-1.3665a320.515041 320.515041 0 0 0-12.427649-3.457911c-0.279133-0.074991-0.566598-0.141649-0.84573-0.212474a329.443118 329.443118 0 0 0-12.59013-2.903813c-1.399829-0.295797-2.824655-0.562431-4.236983-0.841564a336.479759 336.479759 0 0 0-23.788763-3.857862c-1.428992-0.179145-2.849652-0.37912-4.28281-0.5416a355.760739 355.760739 0 0 0-14.035787-1.354002h-27.513308c-1.891436 0.112486-3.778705 0.112486-5.670141 0h-18.539404a356.739786 356.739786 0 0 0-14.039953 1.354002c-1.433158 0.16248-2.849652 0.362456-4.274478 0.5416-3.245437 0.41245-6.478376 0.858229-9.682151 1.354001a346.299394 346.299394 0 0 0-14.160772 2.512194c-1.387331 0.274966-2.78716 0.537434-4.166158 0.829065a327.101737 327.101737 0 0 0-12.910924 2.978803l-0.254135 0.058326a337.958745 337.958745 0 0 0-12.802604 3.562066c-1.374832 0.41245-2.728834 0.854062-4.091167 1.283176a316.086415 316.086415 0 0 0-8.557289 2.807991c-0.933219 0.320794-1.862273 0.654087-2.791326 0.987379a119.514577 119.514577 0 0 0 10.369567 21.955654z"
                          fill="#FEFEFE" p-id="9620"></path>
                        <path
                          d="M602.630603 762.54025l-45.11116 31.650303-42.040701 29.683876-2.953806 2.083079 44.994507 31.650304 45.11116 31.650303c25.880174 18.201945 30.187982-97.042321 12.944254-123.359942-3.449579-5.278522-7.761553-7.003312-12.944254-3.357923zM422.302616 762.54025c-5.149371-3.624558-9.440514-1.937264-12.877595 3.262102-17.289556 26.146808-12.931755 141.674372 12.877595 123.451596l45.11116-31.650303 44.994507-31.650303-2.953806-2.087245-42.036535-29.67971-45.115326-31.646137z"
                          fill="#CFE07D" p-id="9621"></path>
                        <path
                          d="M512.762407 357.477196V243.399455C413.107905 236.596119 270.279507 328.393246 271.591847 480.774645c0.029163 3.637056 0.062492 7.274112 0.258302 10.977827 70.266423 44.165442 216.586061-30.825404 240.912258-134.275276z"
                          fill="#ADBE20" p-id="9622"></path>
                        <path
                          d="M512.762407 357.477196c24.359526 103.079084 169.929257 177.869954 240.603963 134.708556 0.254136-0.154148 0.529102-0.274966 0.783237-0.43328 8.619781-158.955596-139.116351-255.318833-241.383034-248.353017v114.077741z"
                          fill="#7EA701" p-id="9623"></path>
                      </svg>
                    </div>


                  </div>
                </div>

                <div class="flex flex-col items-center" v-if="store.getters.getisHaveTextMessage || store.getters.getElapsedTime != 0 ||
                  (store.getters.getTextMessageContent &&
                    ((typeof store.getters.getTextMessageContent.content === 'string' && store.getters.getTextMessageContent.content.trim() !== '')
                      || typeof store.getters.getTextMessageContent.deepcontent === 'string' &&
                      store.getters.getTextMessageContent.deepcontent.trim() !== ''))">
                  <div class="flex mt-1 w-full justify-start">
                    <div
                      class="w-10 h-10 border-solid border flex justify-center items-center  rounded-full flex-shrink-0">
                      <svg t="1752828206160" class="icon w-6 h-6" viewBox="0 0 1179 1024" version="1.1"
                        xmlns="http://www.w3.org/2000/svg" p-id="28052" width="200" height="200">
                        <path
                          d="M597.215632 994.574713h403.714943s43.549425-8.945287 43.549425-114.64092 94.16092-577.677241-459.976092-577.677241-457.151264 541.425287-457.151264 541.425287-25.423448 160.77977 54.848735 157.013333 415.014253-6.12046 415.014253-6.120459z"
                          fill="#FFFFFF" p-id="28053"></path>
                        <path
                          d="M1071.786667 712.798161h72.503908v136.297931h-72.503908zM36.016552 712.798161h72.503908v136.297931H36.016552z"
                          fill="#EA5D5C" p-id="28054"></path>
                        <path
                          d="M305.68366 559.40926l556.254412-1.165018 0.398364 190.20464-556.254412 1.165018-0.398364-190.20464Z"
                          fill="#4C66AF" p-id="28055"></path>
                        <path
                          d="M1129.931034 680.312644h-59.556781c-3.295632-152.069885-67.56046-258.942529-172.079081-324.384368l115.347127-238.462529a47.08046 47.08046 0 1 0-42.372414-20.48l-114.640919 236.57931a625.934713 625.934713 0 0 0-269.30023-53.200919 625.228506 625.228506 0 0 0-270.006437 54.848736l-115.817931-235.402299a47.08046 47.08046 0 1 0-42.372414 20.715402l117.701149 238.462529c-103.812414 65.441839-167.135632 173.02069-169.960459 324.61977H47.786667a47.08046 47.08046 0 0 0-47.08046 47.08046v117.701149a47.08046 47.08046 0 0 0 47.08046 47.08046h58.615172v57.908965a70.62069 70.62069 0 0 0 70.62069 70.62069l823.908046-1.647816a70.62069 70.62069 0 0 0 70.620689-70.62069v-57.908965h59.085977a47.08046 47.08046 0 0 0 47.08046-47.08046v-117.701149A47.08046 47.08046 0 0 0 1129.931034 680.312644zM94.16092 847.212874H47.08046v-117.70115h47.08046v117.70115z m929.83908 103.106206a23.54023 23.54023 0 0 1-23.54023 23.54023l-823.908046 1.647816a23.54023 23.54023 0 0 1-23.54023-23.540229v-258.942529c0-329.563218 303.668966-365.57977 434.788046-365.815173s435.494253 34.604138 436.20046 363.931954z m105.46023-105.224827h-47.08046v-117.70115h47.08046v117.70115z"
                          fill="#3F4651" p-id="28056">
                        </path>
                        <path
                          d="M464.684138 135.827126l22.363218-19.53839 40.018391 62.381609a30.131494 30.131494 0 0 0 25.423448 13.888735h2.824828a30.131494 30.131494 0 0 0 25.188046-19.067586l20.715402-79.095172 21.186207 74.387126v2.118621a30.366897 30.366897 0 0 0 52.494713 6.826667l30.366896-57.202759 13.182529 12.947126a30.131494 30.131494 0 0 0 21.186207 8.709886h57.673563a23.54023 23.54023 0 0 0 23.54023-23.54023 23.54023 23.54023 0 0 0-23.54023-23.54023h-50.140689l-23.54023-23.54023a30.366897 30.366897 0 0 0-45.668046 3.766437l-21.42161 40.01839L629.465747 19.302989a30.131494 30.131494 0 0 0-28.012873-19.067587 30.131494 30.131494 0 0 0-28.012874 19.067587l-26.60046 101.693793-29.660689-47.08046a30.366897 30.366897 0 0 0-20.48-13.653333 30.837701 30.837701 0 0 0-23.54023 6.826666l-32.250115 28.248276h-60.027586a23.54023 23.54023 0 0 0-23.54023 23.54023 23.54023 23.54023 0 0 0 23.54023 23.54023h66.148046a31.308506 31.308506 0 0 0 17.655172-6.591265zM776.121379 532.950805H404.421149A121.232184 121.232184 0 0 0 282.482759 639.352644a117.701149 117.701149 0 0 0 117.701149 129.000459h371.70023a121.232184 121.232184 0 0 0 121.938391-106.401839 117.701149 117.701149 0 0 0-117.70115-129.000459z m0 188.321839H402.302529a72.503908 72.503908 0 0 1-72.268506-56.496552 70.62069 70.62069 0 0 1 68.972874-84.744828h373.81885a72.503908 72.503908 0 0 1 72.268506 56.496552 70.62069 70.62069 0 0 1-68.502069 84.744828z"
                          fill="#3F4651" p-id="28057"></path>
                      </svg>
                    </div>
                    <div class=" ml-3 mt-1 max-w-[70%] ">
                      <div v-if="store.getters.getisHaveTextMessage">
                        <svg t="1753683262916" class="dots-svg" viewBox="0 0 1024 1024" version="1.1"
                          xmlns="http://www.w3.org/2000/svg" p-id="4487" width="200" height="200">
                          <!-- 第一个圆点 -->
                          <path class="dot dot-1" d="M224 512m-96 0a96 96 0 1 0 192 0 96 96 0 1 0-192 0Z" fill="#515151"
                            p-id="4488">
                          </path>
                          <!-- 第二个圆点 -->
                          <path class="dot dot-2" d="M800 512m-96 0a96 96 0 1 0 192 0 96 96 0 1 0-192 0Z" fill="#515151"
                            p-id="4489">
                          </path>
                          <!-- 第三个圆点 -->
                          <path class="dot dot-3" d="M512 512m-96 0a96 96 0 1 0 192 0 96 96 0 1 0-192 0Z" fill="#515151"
                            p-id="4490">
                          </path>
                        </svg>
                      </div>
                      <div v-if="store.getters.getElapsedTime != 0"
                        class="p-1.5 px-2 bg-slate-600 text-white text-xs rounded-md ml-2 w-auto max-w-36 opacity-80">
                        数据搜索时间 {{ store.getters.getElapsedTime }}s
                      </div>
                      <!-- 深度思考内容（带滚动条） -->
                      <div
                        class="p-4 rounded-lg leading-5 bg-gray-50 border border-gray-100 shadow-sm relative   mb-3"
                        v-if="store.getters.getTextMessageContent &&
                          typeof store.getters.getTextMessageContent.deepcontent === 'string' &&
                          store.getters.getTextMessageContent.deepcontent.trim() !== ''">
                        <div class="text-sm text-gray-500 mb-2 font-medium">深度思考</div>
                        <div class="text-gray-800 whitespace-pre-wrap text-xs  max-h-36 overflow-y-auto"  ref="deepContentRef">
                          {{ store.getters.getTextMessageContent.deepcontent }}
                        </div>
                      </div>

                      <!-- 普通内容 -->
                      <div class="p-4 rounded-lg leading-5 bg-white border border-gray-100 shadow-sm" v-if="store.getters.getTextMessageContent &&
                        typeof store.getters.getTextMessageContent.content === 'string' &&
                        store.getters.getTextMessageContent.content.trim() !== ''">
                        <div class="text-gray-800 whitespace-pre-wrap">
                          {{ store.getters.getTextMessageContent.content }}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Input Area -->
            <div class="h-24 bg-white p-1 rounded-2xl pb-7 border relative  ">
              <textarea v-model="userInput" @keyup.enter="sendtoChat" @input="updateCharacterCount"
                class="w-full h-full p-2 border-gray-300 rounded resize-none focus:outline-none cursor-text hover:cursor-pointer focus:cursor-auto"
                placeholder="请输入消息..." maxlength="120"></textarea>


              <div style="user-select: none; -webkit-user-select: none;" class="absolute bottom-2 left-2   flex ">
                <div @click="handleBindKnowledgeBase()" id="konw" :class="[' w-24 h-8 rounded-2xl flex text-xs justify-center items-center mx-1 cursor-pointer shadow-md Bottom-touch-effect  ',
                  isBound ? 'bg-blue-400' : 'bg-white']">
                  <svg t="1754460059897" class="icon mr-1" viewBox="0 0 1024 1024" version="1.1"
                    xmlns="http://www.w3.org/2000/svg" p-id="2036" width="18" height="18">
                    <path
                      d="M912.9 129.3H769.2c-24.9 0-45 20.1-45 45v677.8c0 24.9 20.1 45 45 45h143.7c24.9 0 45-20.1 45-45V174.3c0-24.8-20.1-45-45-45z m-27 72v466.9h-89.7V201.3h89.7z m-89.7 623.8v-84.9h89.7v84.9h-89.7zM636.8 129.3H493.1c-24.9 0-45 20.1-45 45v677.8c0 24.9 20.1 45 45 45h143.7c24.9 0 45-20.1 45-45V174.3c0-24.8-20.2-45-45-45z m-27 72v466.9h-89.7V201.3h89.7z m-89.7 623.8v-84.9h89.7v84.9h-89.7zM409.3 162.7l-140-32.5c-3.4-0.8-6.8-1.2-10.2-1.2-20.5 0-39 14.1-43.8 34.8L65.6 808.9c-5.6 24.2 9.5 48.4 33.7 54l140 32.5c3.4 0.8 6.8 1.2 10.2 1.2 20.5 0 39-14.1 43.8-34.8l116-499.9c0.3-1 0.6-2.1 0.9-3.2 0.2-1.1 0.4-2.1 0.6-3.2L443 216.6c5.6-24.1-9.5-48.3-33.7-53.9z m-130 43.7l87.4 20.3-18.7 80.6-87.4-20.3 18.7-80.6z m-50 612.8l-87.4-20.3 102.5-441.7 87.4 20.3-102.5 441.7z"
                      p-id="2037"></path>
                  </svg>
                  {{ isBound ? '知识库:开' : '知识库:关' }}
                </div>

                <div @click="toggleOnline()" id="online" :class="[' w-24 h-8 rounded-2xl flex text-xs justify-center items-center mx-1 cursor-pointer shadow-md Bottom-touch-effect  ',
                  isOnline ? 'bg-blue-400' : 'bg-white']">
                  <svg width="18" height="18" class=" mr-1" viewBox="0 0 20 20" fill="none"
                    xmlns="http://www.w3.org/2000/svg">
                    <circle cx="10" cy="10" r="9" stroke="currentColor" stroke-width="1.8"></circle>
                    <path d="M10 1c1.657 0 3 4.03 3 9s-1.343 9-3 9M10 19c-1.657 0-3-4.03-3-9s1.343-9 3-9M1 10h18"
                      stroke="currentColor" stroke-width="1.8"></path>
                  </svg>
                  联网搜索
                </div>

                <div @click="toggleDeep()" id="deep" :class="[' w-24 h-8 rounded-2xl flex text-xs justify-center items-center mx-1 cursor-pointer shadow-md Bottom-touch-effect  ',
                  isDeep ? 'bg-blue-400' : 'bg-white']">
                  <svg width="18" height="18" viewBox="0 0 20 20" fill="none" class=" mr-1"
                    xmlns="http://www.w3.org/2000/svg">
                    <path
                      d="M2.656 17.344c-1.016-1.015-1.15-2.75-.313-4.925.325-.825.73-1.617 1.205-2.365L3.582 10l-.033-.054c-.5-.799-.91-1.596-1.206-2.365-.836-2.175-.703-3.91.313-4.926.56-.56 1.364-.86 2.335-.86 1.425 0 3.168.636 4.957 1.756l.053.034.053-.034c1.79-1.12 3.532-1.757 4.957-1.757.972 0 1.776.3 2.335.86 1.014 1.015 1.148 2.752.312 4.926a13.892 13.892 0 0 1-1.206 2.365l-.034.054.034.053c.5.8.91 1.596 1.205 2.365.837 2.175.704 3.911-.311 4.926-.56.56-1.364.861-2.335.861-1.425 0-3.168-.637-4.957-1.757L10 16.415l-.053.033c-1.79 1.12-3.532 1.757-4.957 1.757-.972 0-1.776-.3-2.335-.86zm13.631-4.399c-.187-.488-.429-.988-.71-1.492l-.075-.132-.092.12a22.075 22.075 0 0 1-3.968 3.968l-.12.093.132.074c1.308.734 2.559 1.162 3.556 1.162.563 0 1.006-.138 1.298-.43.3-.3.436-.774.428-1.346-.008-.575-.159-1.264-.449-2.017zm-6.345 1.65l.058.042.058-.042a19.881 19.881 0 0 0 4.551-4.537l.043-.058-.043-.058a20.123 20.123 0 0 0-2.093-2.458 19.732 19.732 0 0 0-2.458-2.08L10 5.364l-.058.042A19.883 19.883 0 0 0 5.39 9.942L5.348 10l.042.059c.631.874 1.332 1.695 2.094 2.457a19.74 19.74 0 0 0 2.458 2.08zm6.366-10.902c-.293-.293-.736-.431-1.298-.431-.998 0-2.248.429-3.556 1.163l-.132.074.12.092a21.938 21.938 0 0 1 3.968 3.968l.092.12.074-.132c.282-.504.524-1.004.711-1.492.29-.753.442-1.442.45-2.017.007-.572-.129-1.045-.429-1.345zM3.712 7.055c.202.514.44 1.013.712 1.493l.074.13.092-.119a21.94 21.94 0 0 1 3.968-3.968l.12-.092-.132-.074C7.238 3.69 5.987 3.262 4.99 3.262c-.563 0-1.006.138-1.298.43-.3.301-.436.774-.428 1.346.007.575.159 1.264.448 2.017zm0 5.89c-.29.753-.44 1.442-.448 2.017-.008.572.127 1.045.428 1.345.293.293.736.431 1.298.431.997 0 2.247-.428 3.556-1.162l.131-.074-.12-.093a21.94 21.94 0 0 1-3.967-3.968l-.093-.12-.074.132a11.712 11.712 0 0 0-.71 1.492z"
                      fill="currentColor" stroke="currentColor" stroke-width=".1"></path>
                    <path d="M10.706 11.704A1.843 1.843 0 0 1 8.155 10a1.845 1.845 0 1 1 2.551 1.704z"
                      fill="currentColor" stroke="currentColor" stroke-width=".2"></path>
                  </svg>
                  深度思考
                </div>
              </div>



              <div class="absolute bottom-2 right-2 flex items-center gap-2">
                <!-- 字符计数 -->
                <div class="text-xs text-gray-500 px-1 transition-colors duration-300 
              hover:text-gray-700">
                  {{ characterCount }}/120
                </div>


                <!-- 停止输出按钮 -->
                <button v-if="store.getters.getIsTextChat" @click="sendInterrupt" title="停止输出" class="flex items-center justify-center w-8 h-8 rounded-full 
                  bg-blue-500 text-white shadow-md shadow-blue-200/50
                  hover:bg-red-500 hover:shadow-red-200/50
                  active:scale-90 active:shadow-inner
                  transition-all duration-300 ease-in-out
                  focus:outline-none focus:ring-2 focus:ring-red-300 focus:ring-offset-1">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24"
                    class="text-white transition-transform duration-300 hover:rotate-90">
                    <title>终止生成</title>
                    <path fill="currentColor" fill-rule="evenodd"
                      d="M12 23c6.075 0 11-4.925 11-11S18.075 1 12 1 1 5.925 1 12s4.925 11 11 11m0-20a9 9 0 1 1 0 18 9 9 0 0 1 0-18m-2 5.5A1.5 1.5 0 0 0 8.5 10v4a1.5 1.5 0 0 0 1.5 1.5h4a1.5 1.5 0 0 0 1.5-1.5v-4A1.5 1.5 0 0 0 14 8.5z"
                      clip-rule="evenodd"></path>
                  </svg>
                </button>

                <!-- 发送按钮 -->
                <button v-else @click="sendtoChat" :disabled="!userInput.trim()" class="px-2   py-1 rounded-lg font-medium transition-all duration-300 ease-in-out
                  focus:outline-none focus:ring-2 focus:ring-offset-1
                  disabled:opacity-50 disabled:cursor-not-allowed disabled:shadow-none
                  bg-blue-500 text-white shadow-md shadow-blue-200/50
                  hover:bg-blue-600 hover:shadow-lg hover:shadow-blue-200/70
                  active:bg-blue-700 active:scale-95 active:shadow-inner">
                  发送
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-show="showModal" id="cover"
        class="fixed inset-0 bg-black bg-opacity-30 flex justify-center items-center z-30">


        <div class="addBox w-96 h-44 p-5 bg-white rounded-lg " :class="{ shake: isShaking }">
          <h3 class="title text-lg font-bold text-center">是否选择该知识库</h3>
          <!-- 错误信息显示 -->
          <p class="error text-red-500 text-xs text-center mt-1" v-if="errorMessage">{{ errorMessage }}</p>

          <div class="coverBs flex justify-around mt-14">
            <button @click="showModal = false; localKonwId = belocalKonwId"
              class="cancel px-3 py-1 bg-gray-600 text-white text-xs rounded touch-effectBox ">取消</button>
            <button @click.stop="changeKonw()"
              class="sure px-3 py-1 bg-blue-500 text-white text-xs rounded touch-effectBox ">确定</button>
          </div>
        </div>



      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, nextTick, onUnmounted } from 'vue' //vue
import { RotateCcw } from 'lucide-vue-next';  //图形库依赖
import { useRouter } from 'vue-router'  //路由依赖
import { useStore } from 'vuex'  //全局变量依赖
import AssistantService from '../api/assistant.js';  //ai助手增删改查的api调用封装
import { issuccess, isErr, isErrMessage, issuccessMessage, showSuccessMessage, clickMessage } from '../assets/js/showErrOrTrue.js'; //成功失败消息提示
import { initPhysics, addNewObject } from '../assets/js/textMatter.js'; //小方块
import { createMieAnimation } from '../assets/js/miemie.js';//小羊

const store = useStore() //引入全局变量
const router = useRouter()//路由
const emit = defineEmits(['page-loaded'])

const assistantId = ref(store.getters.getSelectedId) // 获取选中id
const assistant = ref({}) //助手信息
const localCharacter = ref('') //助手人格内容
const localName = ref('')  //助手名字
const localKonwId = ref('')
const belocalKonwId = ref('')
// State
const isBound = ref(false)   //知识库是否开启
const isDeep = ref(false)
const showModal = ref(false);  //蒙板显示
const isEditing = ref(true); //编辑和确认是否可选中
const userInput = ref('');  //用户输入信息
const characterCount = ref(0); //输入信息长度
const isAdding = ref(false)   //防止连续点击
const scrollContainer = ref(null); // 定义一个ref变量关联滚动容器
const deepContentRef = ref(null)

const errorMessage = ref('')  // 错误信息
const isShaking = ref(false)  // 是否触发震动
let shakeTimeout = null;      // 震动定时器
let activeStreamController = null;

// 引用matter容器元素
const physicsContainer = ref(null);
let cleanupPhysics = null; // 用于存储清理函数

// 创建小羊元素引用
const mie = ref(null);
// 初始化小羊动画实例
const mieAnimation = createMieAnimation(mie);

const scrollToBottom = () => {
  // 直接使用导入的 nextTick，不要用 this.$nextTick
  nextTick(() => {
    if (deepContentRef.value) {
      deepContentRef.value.scrollTop = deepContentRef.value.scrollHeight;
    }
  });
};

// 正确的监听方式
watch(
  () => store.getters.getTextMessageContent?.deepcontent,
  (newVal) => {
    if (newVal && newVal.trim() !== '') {
      scrollToBottom();
    }
  }
);

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
  //
  assistant.value = store.getters.getAssistantById(assistantId.value) || {}
  localCharacter.value = assistant.value.character || ''
  localName.value = assistant.value.name || ''
  localKonwId.value = assistant.value.knowledge_base_id || ''
  updateCharacterCount();
  setupEventListeners()
  mieAnimation.init(200, 200);
  if (physicsContainer.value) {
    // 调用初始化函数，传入容器选择器（或直接传入DOM元素）
    cleanupPhysics = initPhysics('#physics-page');
    // 更简单的方式：如果容器ID唯一，可直接用ID选择器
    // cleanupPhysics = initPhysics('#physics-page');
  }
  
  // 加载聊天记录
  if (assistantId.value && assistantId.value !== 0) {
    AssistantService.getConversationLog(assistantId.value)
      .then(response => {
        
        // 处理后端返回的格式
        let logs = [];
        if (response && response.success && response.data) {
          logs = response.data;
        } else if (response && Array.isArray(response)) {
          // 兼容直接返回数组的情况
          logs = response;
        }
        
        
          // 清空现有消息
          store.commit('clearRTMbyId', assistantId.value);
          
          // 添加历史聊天记录
          logs.forEach((log, index) => {
            
            // 适配多种返回格式
            let role, content;
            if (log.left !== undefined && log.right !== undefined) {
              // 后端返回的是 Pair 对象，使用 left 和 right 字段
              role = log.left;
              content = log.right;
            } else if (Array.isArray(log)) {
              // 兼容数组格式
              role = log[0];
              content = log[1];
            } else if (typeof log === 'object') {
              // 处理键值对格式的对象
              const entries = Object.entries(log);
              if (entries.length > 0) {
                [role, content] = entries[0];
              } else {
                // 处理其他对象格式
                role = log.role || log.Role || 'USER';
                content = log.content || log.Content || '';

              }
            } else if (typeof log === 'string' && log.includes(',')) {
              // 处理字符串格式的元组
              const parts = log.substring(1, log.length - 1).split(',');
              role = parts[0].trim();
              content = parts[1].trim();
            } else {
              // 默认值
              role = 'USER';
              content = '';
            }
            
            const message = {
              type: role === 'USER' ? 'user' : 'ai',
              content: content || '',
              deepcontent: '',
              time: new Date().toLocaleString(),
              elapsedTime: 0
            };
            store.commit('addRTMbyId', {
              message: message,
              id: assistantId.value
            });
          });
      })
      .catch(error => {
        console.error('获取聊天记录失败:', error);
      });
  } else {
    console.log('assistantId为空或0，跳过获取聊天记录');
  }
  
  emit('page-loaded')
})

//退出页面时
onUnmounted(() => {
  if (activeStreamController) {
    activeStreamController.abort();
    activeStreamController = null;
  }
  store.commit('clearRTMbyId', store.getters.getSelectedId)
  store.commit('clearTextMessageContent')
  store.commit('clearNowTime')
});


// 知识库内容 - 数组形式
const knowledgeBases = ref([
  { Id: 1, KnowledgeBaseID: "hf9bh1u5he", KnowledgeBaseName: "中国法律" },
  { Id: 2, KnowledgeBaseID: "avnnajka5r", KnowledgeBaseName: "IT技术" },
  { Id: 3, KnowledgeBaseID: "rqk0r2wkl1", KnowledgeBaseName: "全科医学" },
  { Id: 4, KnowledgeBaseID: "iqowgx5gzd", KnowledgeBaseName: "中医" },
  { Id: 5, KnowledgeBaseID: "cpypc7zzgh", KnowledgeBaseName: "健康管理" },
  { Id: 6, KnowledgeBaseID: "wu84zgzu7y", KnowledgeBaseName: "料理" },
  { Id: 7, KnowledgeBaseID: "xf1dmp6h3t", KnowledgeBaseName: "生活安全" }
]);

// 点击事件处理函数，获取点击项的Id
const handleKnowledgeBaseClick = (id) => {
  console.log("当前点击的知识库Id：", id);
  showModal.value = true
  belocalKonwId.value = localKonwId.value
  localKonwId.value = id
};

const handleBindKnowledgeBase = () => {
  isBound.value = !isBound.value
}
const toggleDeep = () => {
  isDeep.value = !isDeep.value
}
//修改知识库
const changeKonw = async () => {

  if (isAdding.value) return;

  isAdding.value = true;

  try {
    triggerShake('知识库配置接口当前后端未实现');


  } catch (error) {
    console.error('修改助手错误:', error);
    triggerShake(`修改失败: ${error.message}`);


  } finally {
    isAdding.value = false;
    showModal.value = false
  }
}
//修改助手人格
const changeAssistant = async () => {

  if (isAdding.value) return;

  // 验证必填字段
  if (localCharacter.value.trim() === '') {
    triggerShake('人设不能为空');
    return;
  }

  isAdding.value = true;

  try {
    triggerShake('助手修改接口当前后端未实现');

  } catch (error) {
    console.error('修改助手错误:', error);
    triggerShake(`修改失败: ${error.message}`);

    // 更详细的错误处理
    if (error.response) {
      console.error('响应数据:', error.response.data);
      console.error('状态码:', error.response.status);
    }
  } finally {
    isAdding.value = false;
    isEditing.value = true;
  }
};

// 编辑,确认方法
const enableEditing = () => {

  isEditing.value = !isEditing.value;
};
//文本长度
const updateCharacterCount = () => {
  characterCount.value = userInput.value.length;
};

//信息发送
const sendtoChat = async () => {
  if (userInput.value.trim() === '') return;
  const question = userInput.value.trim();
  const currentAssistantId = store.getters.getSelectedId;
  const playId = Date.now().toString();

  if (activeStreamController) {
    activeStreamController.abort();
  }
  activeStreamController = new AbortController();

  store.commit('addRTMbyId', {
    message: {
      type: 'user',
      content: question,
    },
    id: currentAssistantId
  });
  store.commit('setIsHaveTextMessage', true)
  store.commit('setIsTextChat', true)
  store.commit('clearTextMessageContent')

  nextTick(() => {
    if (scrollContainer.value) {
      scrollContainer.value.scrollTop = scrollContainer.value.scrollHeight;
    }
  });

  const startedAt = Date.now();
  try {
    await AssistantService.streamGenerateReply({
      question,
      aiAssistantId: currentAssistantId,
      signal: activeStreamController.signal,
      onToken: (token) => {
        store.commit('setTextMessageContent', {
          chat_id: 0,
          play_id: playId,
          content: token || ''
        });
      },
      onDone: () => {
        const generated = store.getters.getTextMessageContent;
        const content = generated?.content || '';
        if (content.trim()) {
          store.commit('addRTMbyId', {
            message: {
              type: 'ai',
              content,
              deepcontent: generated?.deepcontent || '',
              elapsedTime: Math.round((Date.now() - startedAt) / 1000)
            },
            id: currentAssistantId
          });
        }
        store.commit('setIsHaveTextMessage', false);
        store.commit('setIsTextChat', false);
        store.commit('clearTextMessageContent');
      },
      onError: (message) => {
        triggerShake(message || '生成失败，请稍后重试');
      }
    });
  } catch (error) {
    if (error?.name !== 'AbortError') {
      triggerShake(error.message || '生成失败，请稍后重试');
    }
  } finally {
    store.commit('setIsHaveTextMessage', false);
    store.commit('setIsTextChat', false);
    activeStreamController = null;
  }

  userInput.value = '';
  characterCount.value = 0;
};

//重置对话
const resetConversation = () => {
  if (activeStreamController) {
    activeStreamController.abort();
    activeStreamController = null;
  }
  store.commit('clearRTMbyId', store.getters.getSelectedId)
  store.commit('clearTextMessageContent')
  store.commit('clearNowTime')
};
//消息中断命令
const sendInterrupt = () => {
  if (activeStreamController) {
    activeStreamController.abort();
    activeStreamController = null;
  }
  store.commit('setIsTextChat', false)
  store.commit('setIsHaveTextMessage', false)
}

// 触发震动效果的函数（1秒后自动停止）
const triggerShake = (message) => {
  // 清除之前的定时器，避免冲突
  if (shakeTimeout) clearTimeout(shakeTimeout);
  // 设置错误信息
  errorMessage.value = message;
  // 启动震动
  isShaking.value = true;
  // 1秒后停止震动
shakeTimeout = setTimeout(() => {
    isShaking.value = false;
    errorMessage.value = '';
    localCharacter.value = assistant.value.character || ''
  }, 1000);
};
const isOnline = ref(false)
const toggleOnline = () => {
  isOnline.value = !isOnline.value
};


//返回首页
const goBack = () => {
  store.commit('clearRTMbyId', store.getters.getSelectedId);
  store.commit('setIsTextChat', false)
  store.commit('clearTextMessageContent')
  store.commit('clearNowTime')
  nextTick(() => {
    router.push('/home_page')
  })
}
const toKonw = () => {
  store.commit('clearRTMbyId', store.getters.getSelectedId);
  store.commit('setIsTextChat', false)
  store.commit('clearTextMessageContent')
  store.commit('clearNowTime')
  nextTick(() => {
    router.push('/konw_page')
  })
}
const restart = () => {
  store.commit('setIsTextChat', false)
  clickMessage.value = false;
  issuccess.value = false;
  isErr.value = false;
  isOnline.value = false
  isBound.value = false
  isDeep.value = false
  resetConversation()
}
</script>

<style scoped>

#sync-element {
  opacity: 0;
}

.sync-element {
  position: absolute;
  width: 20px;
  height: 20px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  cursor: move;
  user-select: none;
  z-index: 10;
  pointer-events: none;
}

#physics-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

#add-button {
  position: fixed;
  bottom: 30px;
  right: 30px;
  padding: 10px 20px;
  background-color: #2196F3;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  z-index: 100;
}

#add-button:hover {
  background-color: #0b7dda;
}

.scrollbar-thin::-webkit-scrollbar {
  width: 4px;
}

.scrollbar-thin::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 2px;
}

.scrollbar-thin::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 2px;
}

.scrollbar-thin::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}

/* 触摸设备点击样式 */
.kb-item:active {
  background-color: #e5e7eb !important;
  transform: scale(0.99);
}

.webfont {
  font-family: "阿里妈妈东方大楷 Regular";
  font-variation-settings: normal;
  /* Chrome 140 以下版本需要 */
}
</style>