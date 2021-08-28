package sandbox

import test.sandbox.sayHello

@JsExport
fun sayWelcome(): String = sayHello()