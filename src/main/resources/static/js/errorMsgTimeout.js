document.addEventListener('DOMContentLoaded', ()=> {

    const errorMessageElements = document.querySelectorAll(".error");

    for (let i = 0; i < errorMessageElements.length; i++) {
        setTimeout(() => {
            errorMessageElements[i].style.display = "none";
        }, 6000);
    }

});