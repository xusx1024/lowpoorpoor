# DesignPatternDemoCode
工厂方法模式说明
详见：[here](http://xusx1024.com/2017/05/24/design-patterns-factory-method/)

> 有一点疑问，调用者也许直接new相应的对象，而绕过我们的Factory，我的处理是把对象得访问都设置为包访问权限的，这样防止外部直接new。但是我们的Factory就要和相对应的类放在一个包里了，也不太好呀。
