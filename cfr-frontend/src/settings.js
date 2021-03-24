module.exports = {
  /**
   * @description Site title
   */
  title: 'Thuê Phòng Học',
  /**
   * @description show tagsView
   */
  tagsView: true,
  /**
   * @description Fixed head
   */
  fixedHeader: true,
  /**
   * @description The number of days that the token in the state of remembering the password is stored in the cookie, the default is 1 day
   */
  tokenCookieExpires: 1,
  /**
   * @description The number of days that the password in the state of remembering the password is stored in the cookie, the default is 1 day s
   */
  passCookieExpires: 1,
  /**
   * @description to keep only one submenu expanded
   */
  uniqueOpened: true,
  /**
   * @description token key
   */
  TokenKey: 'CFR-TOEKN',
  /**
   * @description Request timeout time, milliseconds (default 2 minutes)
   */
  timeout: 1200000,
  /**
   * @description display the logo
   */
  sidebarLogo: true,
  /**
   * display the bottom information of the setting
   */
  showFooter: false,
  /**
   * Bottom text, support html syntax
   */
  footerTxt: '',
  /**
   * case number
   */
  caseNumber: '',
  /**
   * @type {string | array} 'production' | ['production', 'development']
   * @description Need show err logs component.
   * The default is only used in the production env
   * If you want to also use it in dev, you can pass ['production', 'development']
   */
  errorLog: ['production', 'development']
}
