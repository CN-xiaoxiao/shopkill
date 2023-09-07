import http from '@/utils/https'

export async function getListApi(parm) {
    return await http.get("/api/itemkill/allList", parm);
}

export async function editApi(parm) {
    return await http.put("/api/itemkill", parm);
}

export async function addApi(parm) {
    return await http.post("/api/itemkill", parm)
}

export async function deleteApi(parm) {
    return await http.delete("/api/itemkill", parm)
}
