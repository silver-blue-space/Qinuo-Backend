import request from '@/utils/request'

// 查询排班管理列表
export function listScheduling(query) {
  return request({
    url: '/doctor/scheduling/list',
    method: 'get',
    params: query
  })
}

// 查询排班管理详细
export function getScheduling(id) {
  return request({
    url: '/doctor/scheduling/' + id,
    method: 'get'
  })
}

// 新增排班管理
export function addScheduling(data) {
  return request({
    url: '/doctor/scheduling',
    method: 'post',
    data: data
  })
}

// 修改排班管理
export function updateScheduling(data) {
  return request({
    url: '/doctor/scheduling',
    method: 'put',
    data: data
  })
}

// 删除排班管理
export function delScheduling(id) {
  return request({
    url: '/doctor/scheduling/' + id,
    method: 'delete'
  })
}
