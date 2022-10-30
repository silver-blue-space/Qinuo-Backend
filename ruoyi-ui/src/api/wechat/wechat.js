import request from '@/utils/request'

// 查询微信用户列表
export function listWechat(query) {
  return request({
    url: '/wechat/wechat/list',
    method: 'get',
    params: query
  })
}

// 查询微信用户详细
export function getWechat(id) {
  return request({
    url: '/wechat/wechat/' + id,
    method: 'get'
  })
}

// 新增微信用户
export function addWechat(data) {
  return request({
    url: '/wechat/wechat',
    method: 'post',
    data: data
  })
}

// 修改微信用户
export function updateWechat(data) {
  return request({
    url: '/wechat/wechat',
    method: 'put',
    data: data
  })
}

// 删除微信用户
export function delWechat(id) {
  return request({
    url: '/wechat/wechat/' + id,
    method: 'delete'
  })
}
