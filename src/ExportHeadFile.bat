@echo off
echo 正在尝试导出本地方法头文件
javah -d . -v -classpath . cn.sunflyer.simpnk.control.DialWindows
pause