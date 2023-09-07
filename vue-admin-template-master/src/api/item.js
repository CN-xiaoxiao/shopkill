import request from '@/utils/request'
import http from '@/utils/https'

export async function getListApi(parm) {
  return await http.get("/api/item/list", parm);
}

export async function editApi(parm) {
  return await http.put("/api/item", parm);
}

export async function addApi(parm) {
  return await http.post("/api/item", parm)
}

export async function deleteApi(parm) {
  return await http.delete("/api/item", parm)
}

