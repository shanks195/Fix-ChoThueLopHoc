import request from '@/utils/request'

export function del(ids) {
  return request({
    url: 'api/deployHistory',
    method: 'delete',
    data: ids
  })
}

/**
 * @param data
 */
export function reducte(data) {
  return request({
    url: 'api/deploy/serverReduction',
    method: 'post',
    data
  })
}
