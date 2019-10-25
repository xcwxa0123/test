let funcConfig = {
    func : ()=>{
        return testFunc();
    },
    success: ()=>{
        console.log("this is success");
    },
    fail: ()=>{
        console.log("this is fail");
    }
}

let testFunc = function(){
    console.log("this is testFun");
}
let compare = function(flag){
    console.log("this is compareFunc"+flag);
    return flag;
}
!function(){
    testFunc();

}();