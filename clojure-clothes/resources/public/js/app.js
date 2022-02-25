document.addEventListener('DOMContentLoaded', () => {
    let cardToggles = document.getElementsByClassName('card-toggle');
    for (let i = 0; i < cardToggles.length; i++) {
        cardToggles[i].addEventListener('click', (e) => {
            e.currentTarget.parentElement.parentElement.childNodes[3].classList.toggle('is-hidden');
        });
    }
});

const clearCart = () => localStorage.clear();

const addToCart = (product) => {
    const cart = localStorage.getItem('cart');
    if (cart === null) {
        localStorage.setItem('cart', JSON.stringify([product]));
    } else {
        const cartObj = JSON.parse(cart);
        const newCart = [...cartObj, product];
        localStorage.setItem('cart', JSON.stringify(newCart));
    }
    console.log(localStorage.getItem('cart'));
}

const updateQuantity = (sku, e) => {
    const quantity = e.value;
    localStorage.setItem(sku, quantity);
}

const dropdown = document.querySelector('.dropdown');
dropdown.addEventListener('click', (event) => {
  event.stopPropagation();
  dropdown.classList.toggle('is-active');
});
