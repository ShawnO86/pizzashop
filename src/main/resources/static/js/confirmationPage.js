document.addEventListener('DOMContentLoaded', ()=> {
    if (sessionStorage.getItem("menuItems")) {
        sessionStorage.removeItem("menuItems");
    }
    if (sessionStorage.getItem("customPizzas")) {
        sessionStorage.removeItem("customPizzas");
    }
});