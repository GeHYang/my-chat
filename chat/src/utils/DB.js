// indexedDB使用他的open方法创建一个浏览器数据库
var indB = indexedDB.open('indB', 1)
// 用来存储数据库内的表
var db;
// 数据库打开成功的回调
indB.onsuccess = function (tx) {
    // 如果以前就有这个是数据库的话就将里面的表放到db中
    db = indB.result
    console.log('数据库开启成功');
}
// 第一次打开数据库时调用
indB.onupgradeneeded = function (tx) {
    // 并将里面的表放入到db中
    db = tx.target.result
    // 如果没有person这张表的话那么就创建一个
    if (!db.objectStoreNames.contains('person')) {
        // 规定必须有一个键key
        // autoIncrement:true
        db.createObjectStore('person', { keyPath: 'id' })
    }
}
// 添加 add
export function add(value) {
    // transaction开启一个事物
    db.transaction(['person'], 'readwrite')
        // 拿到其中的一张表
        .objectStore('person')
        // 添加的值
        .add(value)
}
// 删除 delete
export function del() {
    db.transaction(['person'], 'readwrite')
        .objectStore('person')
        .delete(1)
}
// 编辑 put
export function edit() {
    db.transaction(['person'], 'readwrite')
        .objectStore('person')
        .put({ id: 2, name: '张三' })
}
// 查询 get
export function get(key) {
    return new Promise((reslove, reject) => {
        var req = db.transaction(['person'], 'readwrite')
            .objectStore('person')
            .get(key)
        req.onsuccess = function (res) {
            reslove(res.target.result)
        }
    })
}