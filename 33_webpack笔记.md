##  NRM 安装

​	NRM 提供常用的NPM镜像的地址,方便切换而已

```
npm i nrm -g
nrm ls
nrm use 镜像名
```



## 1 webpack 安装

​	以安装 webpack 3.8.1 版本为例


1 nodejs 安装

[node-v10.13.0-x64.msi](https://nodejs.org/dist/v10.13.0/node-v10.13.0-x64.msi)

2 安装cnpm

```sh
npm install -g cnpm --registry=https://registry.npm.taobao.org
```

### 1 全局安装 webpack

```sh
cnpm i webpack@3.8.1 -g
```



### 2 项目级别安装 webpack

​	**在项目的根目录执行**

```js
.
├─dist    			--目录
├─main.js 			--文件
├─index.html 		--文件
└─src 				--目录
    ├─css 			--目录
    ├─images 		--目录
    └─js 			--目录
```

```sh
# 初始化项目信息
cnpm init -y
cnpm i webpack@3.8.1 -D
# cnpm i -D webpack-cli
```
### 3 使用webpack的配置文件简化打包命令

```js
# webpack 常规打包命令:
webpack  入口文件路径  输出文件路径
```

1 项目根目录创建配置文件 `webpack.config.js`

2 在配置文件中指定入口文件和输出文件的路径

```js
var path = require('path');
// 配置对象,webpack在启动时,加在webpack.config.js时,会读取该对象
module.exports = {
  // 指定入口文件
  entry:path.resolve(__dirname,'src/js/main.js'),
  // 指定输出文件
  output:{
    path:path.resolve(__dirname,'./dist'), // 指定目录
    filename:'bundle.js'
  }
}
```

### 4 安装 webpack-dev-server 实现实时打包构建

1 安装  `webpack-dev-server` 到项目本地

```js
cnpm i webpack-dev-server --save-dev
# 或
cnpm i webpack-dev-server -D
```

2 安装 webpack 到项目本地

​	webpack-web-server 要求本地项目必须安装 webpack , 否则运行时报错

```js
# 安装3.8.1版本 webpack 到本地
cnpm i webpack@3.8.1 -D
```

3 在配置文件 package.json 文件中 script 节点添加运行 webpack-dev-server 的命令

```json
"dev":"webpack-dev-server --contentBase src --open --port 8088"
```

> 1 webpack-dev-server 是本地安装,需指定全局路径才能运行 , 故执行此配置
>
> 2 webpack-dev-server 打包的bundle.js 文件只放在内存中,本地没生成
>
> 3   `--contentBase src`	  用来指定启动的 `/` 对应的根目录 , 类似 javaWeb 中的 `contextPath` 
>
> 4  `--open`  打开浏览器 ,  `--port 8088`  指定端口  ,  `--hot` 异步刷新页面(对css)

​	配置好后,则启动webpack-dev-server 的命令为  `npm run dev`

### 5 使用 `html-webpack-plugin` 插件配置启动页面

​	通过 ``--contentBase`` 指令比较繁琐 , 可以使用插件简化操作

1 安装 `html-webpack-plugin` 插件到项目本地

```js
npm i html-webpack-plugin -D
```

2 修改 `webpack.config.js` 配置文件:

```json
var path = require('path');
var htmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
  enty:path.resolve(__dirname,'src/js/main.js'),
  output : {
    path : path.resolve(__dirname,'dist'),
    filename : 'bundle.js'
  },
  plugins:[
    new htmlWebpackPlugin({
      template:path.resolve(__dirname,'src/index.html'), // 模版路径
      filename:'index.html'  // 自动生成的html文件的名称
    })
  ]
}
```

3 修改 package.json 中的 script 节点的 dev指令为

```
"dev":"webpack-dev-server"
```

4 将index.html中引用 bundle.js 的script 便签注释掉 , 插件生成页面时会自动注入

### 6 使用webpack打包css , less , sass 文件

​	webpack默认只能打包 js文件 , 要打包css,less等文件需要引入第三方模块

1 安装 `style-loader`  `css-loader` 模块到项目本地

```js
cnpm i style-loader css-loader less-loader sass-loader -D
```

2 修改 webpack.config.js

```

module:{
  rules:[
    {test:/\.css/,use:['style-loader','css-loader']},
    {test:/\.less/,use:['style-loader','css-loader','less-loader']},
    {test:/\.sass/,use:['style-loader','css-loader','sass-loader']}
  ]
}
```

> `use` 指定使用哪些第三方模块来处理 test 匹配到的文件 ; use 中相关 loader 模块的调用顺序是从右向左处理

### 7 使用 webpack 处理css中的路径和字体文件

​	css中引用背景图时,webpack 不能直接处理,需要使用第三方模块

1 安装 url-loader  file-loader

```
cnpm i url-loader file-loader -D
```

2 在webpack.config.js中添加处理url路径的 loader 模块

```
{test:/\.(png|jpg|gif)$/,use:'url-loader'}
{test:/\.(ttf|eot|svg|woff|woff2)$/,use:'url-loader'}
```

​	webpack默认打包时会将图片替换层base64的字符串,图片名称也用hash值来替换,如果想定制可传递参数实现

```js
// 大于 1024字节的图片不转为base64 , 文件名 保持原来的名字,并在前面加8位hash值
{test:/\.(png|jpg|gif)$/,use:'url-loader?limit=1024&name=[hash:8]-[name].[ext]'}
```

### 8 使用 babel 处理高级JS语法

​	webpack不能直接处理高级的js语法,需要使用插件进行处理

1 安装babel相关的loader包

```js
cnpm i babel-core babel-loader babel-plugin-transform-runtime -D
```

2 安装babel转换语法的依赖

```
cnpm i babel-preset-env babel-preset-stage-0 D
```

3 在webpack.config.js中添加相关loader模块 , **一定要排除node_modules文件夹**

```js
{test:/\.js$/,use:"babel-loader",exclude:/node_modules/}
```

4 在项目根目录添加 .babelrc 文件 , 并添加如下内容

```js
{
  "presets":["env","stage-0"],
  "plugins":["transform-runtime"]
}
```

### 9 webpack配置.vue组件页面的解析

1 安装vue 为运行依赖

```js
cnpm i vue -S
```

2 本地安装转换vue的依赖

```js
cnpm i vue-loader vue-template-compiler -D
```

3 本地安装转换css的依赖 

```js
cnpm i style-loader css-loader -D
```

> .vue 文件中会引用 css 文件,所以需要导入转换

4  在 webpack.config.js , 添加如下 module 规则

```json
module:{
	rules:[
      {test:/\.css$/,use:['style-loader','css-loader']},
      {test:/\.vue$/,use:'vue-loader'}
	]
}
```

5 创建 `App.js` 组件页面

```html
<!--注意:在.vue组件中,template中必须有且只有唯一的根元素进行包裹,一般都用div作为唯一根元素-->
<template>
	<div>
  		<h1>这是APP组件,--{{msg}}</h1>
  	</div>
</template>

<script>
// 在 .vue组件中,通过 script 标签来定义组件的功能,需要使用 ES6 中提供的 export default 方式 ,导出一个vue实例对象
  	export default{
      	data(){
          	return {
               msg:'OK'
          	}
      	}
  	}
</script>

<style scoped>
	h1 {
      	color:red,
	}
</style>
```

6 创建 main.js 入口文件

```js
// 导入vue组件
import Vue from 'vue'
// 导入app组件
import App from './components/App.vue'

// 创建一个 Vue 实例,使用 render 函数,渲染指定的组件
var vm = new Vue({
  el:'#app',
  render: c => c(App)
});
```

7 修改 Vue 被导入时候的包的路径

```js
 resolve: {
    alias: { // 修改 Vue 被导入时候的包的路径
      // "vue$": "vue/dist/vue.js"
    }
  }
```

## ES6语法使用总结

1 使用 export default 和 export 导出模块中的成员 , 对应 ES5 中的 module.exports 和 export

2 使用 `import  * from *` 和 `import   '路径'`  还有  `import   {a,b}  from  '模块标识'` 导入其他模块  

3 箭头函数 `(a,b) => {return a+b;}`



## 安装 vue-cli

```js
# 安装
npm install vue-cli -g
# 检测安装结果
vue -V
```

