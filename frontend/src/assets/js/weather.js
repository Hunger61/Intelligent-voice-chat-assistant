const weatherCategories = {
    '晴': ['晴', '热'],
    '多云': ['少云', '晴间多云', '多云'],
    '阴': ['阴', '霾', '中度霾', '重度霾', '严重霾', '未知'],
    '风': ['有风', '平静', '微风', '和风', '清风', '强风', '劲风', '疾风', '大风', '烈风', '风暴', '狂爆风', '飓风', '热带风暴', '龙卷风'],
    '雨': ['阵雨', '雷阵雨', '雷阵雨并伴有冰雹', '小雨', '中雨', '大雨', '暴雨', '大暴雨', '特大暴雨', '强阵雨', '强雷阵雨', '极端降雨', '毛毛雨', '细雨', '雨', '小雨 - 中雨', '中雨 - 大雨', '大雨 - 暴雨', '暴雨 - 大暴雨', '大暴雨 - 特大暴雨', '冻雨'],
    '雪': ['雪', '阵雪', '小雪', '中雪', '大雪', '暴雪', '小雪 - 中雪', '中雪 - 大雪', '大雪 - 暴雪', '冷'],
    '雾': ['浮尘', '扬沙', '沙尘暴', '强沙尘暴', '雾', '浓雾', '强浓雾', '轻雾', '大雾', '特强浓雾'],
    '雨夹雪': ['雨雪天气', '雨夹雪', '阵雨夹雪']
};

// 反转映射，创建关键词到分类的映射
const keywordToCategory = {};
for (const [category, keywords] of Object.entries(weatherCategories)) {
    for (const keyword of keywords) {
        keywordToCategory[keyword] = category;
    }
}

// 分类函数
export function classifyWeather(description) {
    const result = {
        categories: new Set(),
        details: []
    };

    // 检查每个关键词是否出现在描述中
    for (const [keyword, category] of Object.entries(keywordToCategory)) {
        if (description.includes(keyword)) {
            result.categories.add(category);
            result.details.push({
                keyword,
                category
            });
        }
    }

    // 将Set转换为数组
    result.categories = Array.from(result.categories);

    return result;
}