document.addEventListener('DOMContentLoaded', () => {
    const cart = localStorage.getItem('cart');
    const cartObj = JSON.parse(cart);
    let cartTotal = 0;
    if (cart !== null && cartObj.length > 0) {
        const cartEl = document.getElementById('cart');
        for (const x of cartObj) {
            cartTotal += x.price * x.quantity;
            const element = document.createElement('div');
            element.innerHTML = (`
                <div class="dropdown-item">
                    <p><b>${x.name}:</b> £${x.price}</p>
                    <div class="level">
                        <span class="tag is-light">${x.sku}</span>
                        <span class="tag is-light">x${x.quantity}</span>
                        <i onclick="removeProduct('${x.sku}')" class="fa fa-trash button is-small is-danger delete-product" aria-hidden="true"></i>
                    </div>
                </div>
                ${cartObj.indexOf(x) !== cartObj.length - 1 ? '<hr class="dropdown-divider">' : ''}
            `);
            cartEl.appendChild(element);
        }
    }
    document.getElementById('cart-total').innerHTML = cartTotal.toFixed(2);
});

// https://stackoverflow.com/questions/25764719/update-if-exists-or-add-new-element-to-array-of-objects-elegant-way-in-javascr
const appendOrUpdate = (array, element) => {
    const i = array.findIndex(_element => _element.sku === element.sku);
    if (i > -1) array[i] = element;
    else array.push(element);
}

const clearCart = () => {
    localStorage.clear();
    location.reload();
}

const addToCart = (product) => {
    let cart = localStorage.getItem('cart');
    const quantity = Number(localStorage.getItem(product.sku));
    localStorage.removeItem(product.sku)

    if (quantity === null || quantity < 1) {
        return;
    }

    if (cart === null) {
        localStorage.setItem('cart', JSON.stringify([]));
        cart = localStorage.getItem('cart');
    }

    const cartObj = JSON.parse(cart);
    appendOrUpdate(cartObj, { ...product, quantity });
    localStorage.setItem('cart', JSON.stringify(cartObj));
    
    console.log(localStorage.getItem('cart'));
    location.reload();
}

const removeProduct = (sku) => {
    const cart = localStorage.getItem('cart');
    const cartObj = JSON.parse(cart);
    const newCart = cartObj.filter((x) => x.sku !== sku);
    localStorage.setItem('cart', JSON.stringify(newCart));
    console.log(localStorage.getItem('cart'));
    location.reload();
};

const updateQuantity = (sku, e) => {
    const quantity = e.value;
    if (quantity && quantity > 0) {
        localStorage.setItem(sku, Number(e.value));
    }
}

const dropdown = document.querySelector('.dropdown');
dropdown.addEventListener('click', (event) => {
  event.stopPropagation();
  dropdown.classList.toggle('is-active');
});
