function getter(person: String){
    return "Hello" + person;
}

let user = [0,1,2,3,4];
document.body.innerHTML = getter(user);