const plugins = ['@vue/babel-plugin-transform-vue-jsx']
// Production environment removal console
if (process.env.NODE_ENV === 'production') {
  plugins.push('transform-remove-console')
}
module.exports = {
  plugins: plugins,
  presets: [
    '@vue/app'
  ]
}
