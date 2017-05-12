# Simple Netkeeper

一个Java版本的简单路由器设置工具，适用于Netkeeper类客户端用户，可用于Win/*nix/Mac OS。
目前版本支持TP-LINK旧版本以及水星/迅捷系列通杀
【基于非商业许可分发，此软件及其源代码不得被用于任何商业用途软件，包括湖北地区某些傻逼奸商口中“自己开发”的破解软件。】

请注意：此程序所提供的算法仅适用于Netkeeper 2.5或者星空极速3.x版本，由于信利更新Netkeeper客户端至4.0，此处算法在新版本使用地区是无效的，等待大牛进一步解救。

对于Netkeeper 4.X客户端使用路由器，请参考项目主页并获取使用授权（授权验证是为了尽可能避免出现2.5时期软件被恶意贩卖的情况出现，毕竟呢天朝不知道什么叫做协议的人还是太多了，而天朝不愿意动脑子动手的大学生小白还是有点多呢。）

项目主页：https://simplenetkeeper.com

论坛：https://simplenetkeeper.com/forum

短信验证码获取程序（新疆/陕西）：https://github.com/sunflyer/SimpleNetkeeper_ChinaNetValidationCode

我的博客：https://sunflyer.cn/

## 已支持地区
- 重庆Netkeeper 0055~0094（重庆地区2.5版本依然可用（部分学校不可用），请考虑使用动态密码。）
- 湖北e信（湖北电信4.X算法依然是2.5的算法，可以继续使用。至于心跳嘛，呵呵。）
- 浙江闪讯
- 青海电信 （随山东电信，可能已更新Netkeeper 4.0，此处可能不再有效）
- 新疆电信3.7.3  （4.X未强制，可继续使用，短信验证码请参考上述链接）
- 陕西电信 3.7.3 （根据反馈部分学校强制4.X）
- 甘肃电信 3.7.1 
- 上海联通 2.5.0059
- 浙江企业闪讯

## 失效地区
- 江西星空极速v32（已更新Netkeeper 4.9，大部分地区已不再有效）
- 山东电信 3.7.3 （已更新Netkeeper 4.9，此处不再有效）
- 河北联通Netkeeper 0096（已失效）
- 山东移动 0094  （已失效）

## 使用方式

- git clone源代码
- 导入Eclipse项目
- 修改 src/netkeeper/CXKUsername.java中radius部分和LR部分为你所在地区的数据
- 保存，导出可执行Java文件
- 执行，and go on your configurations.

## 许可

软件和源代码基于GPL v3协议以及额外的个人开源软件协议分发，此外，不允许以任何形式将软件、源代码以及它们的衍生物用于任何形式的商业用途。特别禁止各种所谓的技术人员和奸商。

附：个人软件开放源代码协议

软件及源代码基于'as-is'形式，这意味着我并不对你在使用过程中遇到的任何问题做出担保。软件及其源代码，以及基于软件/源代码所产生的衍生产品/衍生版本均不允许以任何形式商业化。此外，在你获取到软件或者软件的源代码以后，你对软件/源代码所做出的修改必须同样的开放源代码。除非你所做出的引用涉及到你个人的专利或知识产权，即便如此，上述非商业化要求仍然有效。 开放源代码授予你使用/修改/制作衍生产品并将他们非商业化的权利，但是原作者仍然保留对软件及其源代码的所有权利。如果被发现存在商业化行为，原作者将有权利要求你停止行为，否则依据相关著作权法律予以警告甚至上诉。

==================

A simple java application programming for who used to dial to Internet with Netkeeper.

This project is aimed to make every students in College in ChongQing could use their Broadband service easily with a Wireless Router. The wireless router is limited to be used in College due to the limitation of Netkeeper by encrypting the account name as real account name to access the internet services.

The encription of Netkeeper was cracked by a student in CQUPT for about 1 year, and the client for dialing on an Android device has been released long time ago. So it's time to release an application for us to use wireless router easily.

The application can help user who is in college in ChongQing and using Netkeeper to access Internet, by simply input their broadband accound info and router info, then click "Set Router Now" to configure the router to dial to Internet just like using Netkeeper. It's easy , simple and convinient.

The application will NOT collect any PERSONAL INFORMATION when running.

For the college with strategy in breaking network services in midnight, this application CAN NOT help you get out of it.YOU MAY NEED TO BUY AN ACCOUNT WITHOUT THIS LIMITATION.

This application is FREE,and you can get source code in this page.HOWEVER, YOU CAN NOT RESELL THIS APPLICATION OR ITS DERIVATIVE VERSIONS IN ANY WAY.USING THIS APPLICATION AND/OR DOWNLOADING THE SOURCE CODE MEANS YOU ACCEPT THIS LIMITS.

Original Author: CrazyChen @ CQUT.
E-mail : cx@itncre.com
