document.addEventListener('DOMContentLoaded', () => {
    const cart = localStorage.getItem('cart');
    if (cart === null) return;
    const cartObj = JSON.parse(cart);
    if (!cartObj || cartObj.length < 1) return;
    document.getElementById('empty-warning').remove();
    document.getElementById('checkout-form').style.display = 'block';
    let cartTotal = 0;
    let iTotal = 0;
   
    const cartEl = document.getElementById('cart');
    for (const x of cartObj) {

        const { color, price, quantity, name, sku, size  } = x;
        cartTotal += price * quantity;

        for (let i = 0; i < quantity; i++) {
            const element = document.createElement('div');
            element.innerHTML = (`
                <div class="level box mb-3">
                    <div class="level-left">
                        <div class="level-item">
                            <img class="image is-128x128" src="img/t-shirts/${color.toLowerCase()}.jpg" alt="Product preview"/>
                            <div class="container ml-5">
                                <p class="is-size-6"><b>${name}</b></p>
                                <p class="is-size-6">Price: ${price}</p>
                                <p class="is-size-6">SKU: ${sku}</p>
                                <p class="is-size-6">Size: ${size}</p>
                                <p class="is-size-6">Colour: ${color}</p>
                            </div>
                        </div>
                    </div>
                    <div class="level-right">
                        <div class="container">
                            <div class="container mt-3">
                                <p>Choose a Design:</p>
                                <label class="radio">
                                    <input checked type="radio" name="design[${i + iTotal}]" value="">
                                    None
                                </label>
                                <label class="radio">
                                    <input type="radio" name="design[${i + iTotal}]" value="https://gateway.pinata.cloud/ipfs/QmcLjsUDHVuy8GPUPmj4ZdGa3FWTfGnsKhWZ4dxqUaGGXk/cat.jpg">
                                    <img class="image is-64x64" src="/img/designs/cat.jpg" alt="Cat design"/>
                                </label>
                                <label class="radio">
                                    <input type="radio" name="design[${i + iTotal}]" value="https://gateway.pinata.cloud/ipfs/QmcLjsUDHVuy8GPUPmj4ZdGa3FWTfGnsKhWZ4dxqUaGGXk/doge.jpg">
                                    <img class="image is-64x64" src="/img/designs/doge.jpg" alt="Doge design"/>
                                </label>
                                <label class="radio">
                                    <input type="radio" name="design[${i + iTotal}]" value="https://gateway.pinata.cloud/ipfs/QmcLjsUDHVuy8GPUPmj4ZdGa3FWTfGnsKhWZ4dxqUaGGXk/yoda.jpg">
                                    <img class="image is-64x64" src="/img/designs/yoda.jpg" alt="Yoda design"/>
                                </label>
                                <label class="radio">
                                    <div class="level">
                                        <input type="radio" name="design[${i + iTotal}]">
                                        <input class="input is-small ml-2" type="text" name="design[${i + iTotal}]" placeholder="URL to Custom Design">
                                    </div>
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
                <input type="hidden" value="${sku}" name="sku[${i + iTotal}]"/>
            `);
            cartEl.appendChild(element);
        }
        iTotal += quantity;
    }
    document.getElementById('subtotal').innerHTML = cartTotal;
    document.getElementById('total').innerHTML = cartTotal;
});
