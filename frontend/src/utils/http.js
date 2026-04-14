import axios from "axios";

axios.defaults.baseURL = import.meta.env.VITE_BASE_URL;
axios.defaults.withCredentials = true;
axios.defaults.timeout = 60000;

const requests = async ({
    url,
    method = "get",
    params = {}, //get请求参数
    data = {}, //post请求参数
//    headers: {
//        'Content-Type': 'application/json'
//    }
} = {}) => {
    method = method.toLocaleLowerCase();
    const res = await axios.request({ method,url,params,data});
    return res.data;
};

export default requests;