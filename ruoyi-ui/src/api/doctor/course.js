import request from '@/utils/request'

// 查询门诊科目列表
export function listCourse(query) {
  return request({
    url: '/doctor/course/list',
    method: 'get',
    params: query
  })
}

// 查询门诊科目列表(下拉列表)
export function listSelectCourse() {
  return request({
    url: '/doctor/course/selections',
    method: 'get'
  })
}
// 查询门诊科目详细
export function getCourse(id) {
  return request({
    url: '/doctor/course/' + id,
    method: 'get'
  })
}

// 新增门诊科目
export function addCourse(data) {
  return request({
    url: '/doctor/course',
    method: 'post',
    data: data
  })
}

// 修改门诊科目
export function updateCourse(data) {
  return request({
    url: '/doctor/course',
    method: 'put',
    data: data
  })
}

// 删除门诊科目
export function delCourse(id) {
  return request({
    url: '/doctor/course/' + id,
    method: 'delete'
  })
}
