# Follow Your Heart

"Follow Your Heart" means express your mind, speak out your ideas and do what you want.

When you want to vote something, naturally, you just raise your hand. When you want to get a chance to speak or persuade, you may raise your hand. When you are in the happiness of victory, you may raise your hand. We develop a wearable application for TicWatch which is base on Android Wear, and this app can detect your raising hand gesture and do something cool.

This project contains two parts: one is the Android app that runs on TicWatch; another one is a web page in JavaScript that can display the average heart rate and the number of people who raise their hands. We could use this project in ballot, concert(or others live show) and many creative scenario(if we add some new features).

Technical Details:

1. 如果Web端无法加载LeanCloud SDK请使用官网提供的node.js方式安装  
2. wearable应用需要自行新建项目导入libs作为依赖项，并加入Manifest文件中的所有权限声明
