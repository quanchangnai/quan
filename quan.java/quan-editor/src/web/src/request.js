import axios from "axios";

FormData.encode = function (data) {
    if (data instanceof FormData) {
        return data;
    }
    let formData = new FormData();
    for (let key of Object.keys(data)) {
        formData.append(key, data[key]);
    }
    return formData;
};

let baseAxios = axios.create({baseURL: process.env.BASE_URL});

export default {
    async get(url) {
        return (await baseAxios.get(url)).data;
    },
    async post(url, data) {
        return (await baseAxios.post(url, data)).data;
    }
};
