# 28_vue 商城

## 1 项目结构建立

1. 搭建最基础的目录结构

```sh
vue_mall
	|-src
		|-main.js				# 项目打包的入口文件
		|-App.vue				# 项目的根组件
	|-package.json    			 # 使用命令 npm init -y 生成 , 记录安装的包
	|-webpack.config.dev.js 	 # 开发阶段的webpack配置文
```

package.json

```json
{
  "name": "vue_mall",
  "version": "1.0.0",
  "description": "",
  "main": "webpack.config.dev.js",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1"
  },
  "keywords": [],
  "author": "",
  "license": "ISC",
  "dependencies": {
    "vue": "^2.5.16"
  },
  "devDependencies": {
    "css-loader": "^0.28.11",
    "html-webpack-plugin": "^3.0.6",
    "style-loader": "^0.20.3",
    "vue-loader": "^14.2.1",
    "vue-template-compiler": "^2.5.16",
    "webpack": "^3.8.1",
    "webpack-dev-server": "^2.11.2"
  }
}

```

> 注意 : `devDependencies` 里的依赖 webpack 和 webpack-dev-server 最好在 github 上查询对应的版本,如果两个版本差异太大会导致运行项目时,报缺少 xxx module, 具坑!!!
>
> https://github.com/webpack/webpack-dev-server/blob/v2.11.2/package.json

2. 安装搭建项目用到的包

 1.  名称 : `vue`

     应用场景 : 再根组件中渲染App.vue,需要创建一个根实例,所以需要安装vue这个包

     命令 : `cnpm  i vue --save/-S`

2. 名称 : `vue-loader` `vue-template-compiler` `css-loader style-loader`

   应用场景 : 在webpack.config.dev.js中需要配置对 .vue 结尾文件打包支持的时候

   命令 : `cnpm i vue-loader vue-template-compiler css-loader style-loader -D`

3. 名称 : `html-webpack-plugin` `webpack` `webpack-dev-server`

   应用场景 : 使用 `html-webpack-plugin`生成index.html时使用到

   命令 : `cnpm i html-webpack-plugin webpack@3.8.1 webpack-dev-server@2.11.2 -D`

卸载安装出错的包的命令 : `cnpm uninstall 包名 -D`



3. 使用 `html-webpack-plugin` 创建 & `webpack-dev-server` 打包运行,让用户看到结果

   1. 在项目根目录下创建一个参考文件 `template.html` , 里边只写一个id=app 的 div 标签

   ```html
   <!DOCTYPE html>
   <html lang="en">
   <head>
       <meta charset="UTF-8">
       <title>vue 商城</title>
   </head>
   <body>
       <div id="app">
           
       </div>
   </body>
   </html>
   ```

   2. 在 `webpack.config.dev.js` 中配置plugins

   ```js
   var HtmlWebpackPlugin = require('html-webpack-plugin');
   module.exports = {
       entry: './src/main.js',
       module: {
           rules: [
               {
                   test: /\..vue$/,
                   use: [
                       {
                           loader: 'vue-loader',  // 安装包vue :  cnpm i vue --save/-S
                       }
                   ]
               }
           ]
       },
       plugins:[ // 插件中的内容都是new出来的
           new HtmlWebpackPlugin({
               template:'./template.html', // 参考文件的路径
               filename:'index.html'
           })
       ]
   }
   ```

   3. 项目根目录打开终端,输入如下命令

   ```sh
   webpack-dev-server --config webpack.config.dev.js --progress --open --hot
   ```

   ​