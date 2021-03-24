import defaultSettings from '@/settings'

const title = defaultSettings.title || 'Thue Phong Hoc - Classrooms For Rents'

export default function getPageTitle(pageTitle) {
  if (pageTitle) {
    return `${pageTitle} - ${title}`
  }
  return `${title}`
}
