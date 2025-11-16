package tcs.app.dev.homework1

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import tcs.app.dev.R
import tcs.app.dev.homework1.data.Cart
import tcs.app.dev.homework1.data.Discount
import tcs.app.dev.homework1.data.Euro
import tcs.app.dev.homework1.data.Item
import tcs.app.dev.homework1.data.MockData
import tcs.app.dev.homework1.data.Shop
import tcs.app.dev.homework1.data.minus
import tcs.app.dev.homework1.data.plus
import tcs.app.dev.homework1.data.times
import tcs.app.dev.homework1.data.update


/**
 * # Homework 3 — Shop App
 *
 * Build a small shopping UI with ComposeUI using the **example data** from the
 * `tcs.app.dev.homework.data` package (items, prices, discounts, and ui resources).
 * The goal is to implement three tabs: **Shop**, **Discounts**, and **Cart**.
 *
 * ## Entry point
 *
 * The composable function [ShopScreen] is your entry point that holds the UI state
 * (selected tab and the current `Cart`).
 *
 * ## Data
 *
 * - Use the provided **example data** and data types from the `data` package:
 *   - `Shop`, `Item`, `Discount`, `Cart`, and `Euro`.
 *   - There are useful resources in `res/drawable` and `res/values/strings.xml`.
 *     You can add additional ones.
 *     Do **not** hard-code strings in the UI!
 *
 * ## Requirements
 *
 * 1) **Shop item tab**
 *    - Show all items offered by the shop, each row displaying:
 *      - item image + name,
 *      - item price,
 *      - an *Add to cart* button.
 *    - Tapping *Add to cart* increases the count of that item in the cart by 1.
 *
 * 2) **Discount tab**
 *    - Show all available discounts with:
 *      - an icon + text describing the discount,
 *      - an *Add to cart* button.
 *    - **Constraint:** each discount can be added **at most once**.
 *      Disable the button (or ignore clicks) for discounts already in the cart.
 *
 * 3) **Cart tab**
 *    - Only show the **Cart** tab contents if the cart is **not empty**. Within the cart:
 *      - List each cart item with:
 *        - image + name,
 *        - per-row total (`price * amount`),
 *        - an amount selector to **increase/decrease** the quantity (min 0, sensible max like 99).
 *      - Show all selected discounts with a way to **remove** them from the cart.
 *      - At the bottom, show:
 *        - the **total price** of the cart (items minus discounts),
 *        - a **Pay** button that is enabled only when there is at least one item in the cart.
 *      - When **Pay** is pressed, **simulate payment** by clearing the cart and returning to the
 *        **Shop** tab.
 *
 * ## Navigation
 * - **Top bar**:
 *      - Title shows either the shop name or "Cart".
 *      - When not in Cart, show a cart icon.
 *        If you feel fancy you can add a badge to the icon showing the total count (capped e.g. at "99+").
 *      - The cart button is enabled only if the cart contains items. In the Cart screen, show a back
 *        button to return to the shop.
 *
 * - **Bottom bar**:
 *       - In Shop/Discounts, show a 2-tab bottom bar to switch between **Shop** and **Discounts**.
 *       - In Cart, hide the tab bar and instead show the cart bottom bar with the total and **Pay**
 *         action as described above.
 *
 * ## Hints
 * - Keep your cart as a single source of truth and derive counts/price from it.
 *   Rendering each list can be done with a `LazyColumn` and stable keys (`item.id`, discount identity).
 * - Provide small reusable row components for items, cart rows, and discount rows.
 *   This keeps the screen implementation compact.
 *
 * ## Bonus (optional)
 * Make the app feel polished with simple animations, such as:
 * - `AnimatedVisibility` for showing/hiding the cart,
 * - `animateContentSize()` on rows when amounts change,
 * - transitions when switching tabs or updating the cart badge.
 *
 * These can help if want you make the app feel polished:
 * - [NavigationBar](https://developer.android.com/develop/ui/compose/components/navigation-bar)
 * - [Card](https://developer.android.com/develop/ui/compose/components/card)
 * - [Swipe to dismiss](https://developer.android.com/develop/ui/compose/touch-input/user-interactions/swipe-to-dismiss)
 * - [App bars](https://developer.android.com/develop/ui/compose/components/app-bars#top-bar)
 * - [Pager](https://developer.android.com/develop/ui/compose/layouts/pager)
 *
 */
enum class ShopTab {
    Shop,
    Discounts,
    Cart
}

@Composable
fun ShopScreen(
    shop: Shop,
    availableDiscounts: List<Discount>,
    modifier: Modifier = Modifier
) {
    var cart by rememberSaveable { mutableStateOf(Cart(shop = shop)) }
    var currentTab by rememberSaveable { mutableStateOf(ShopTab.Shop) }
    val hasItems = cart.itemCount > 0u

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            ShopTopBar(
                currentTab = currentTab,
                hasItems = hasItems,
                onBack = { currentTab = ShopTab.Shop },
                onOpenCart = { currentTab = ShopTab.Cart }
            )
        },
        bottomBar = {
            if (currentTab == ShopTab.Cart) {
                CartBottomBar(
                    total = cart.price,
                    hasItems = hasItems,
                    onPay = {
                        cart = Cart(shop = shop)
                        currentTab = ShopTab.Shop
                    }
                )
            } else {
                ShopBottomBar(
                    currentTab = currentTab,
                    onSelectTab = { tab -> currentTab = tab }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (currentTab) {
                ShopTab.Shop -> ShopTabContent(
                    shop = shop,
                    cart = cart,
                    onAddItem = { item -> cart = cart + item }
                )

                ShopTab.Discounts -> DiscountsTabContent(
                    availableDiscounts = availableDiscounts,
                    cart = cart,
                    onAddDiscount = { discount ->
                        if (!cart.discounts.contains(discount)) cart = cart + discount
                    }
                )

                ShopTab.Cart -> CartTabContent(
                    shop = shop,
                    cart = cart,
                    onIncrease = { item -> cart = cart + item },
                    onDecrease = { item ->
                        val amount = cart.items[item] ?: 0u
                        cart = if (amount > 1u) {
                            cart.update(item to (amount - 1u))
                        } else {
                            cart - item
                        }
                    },
                    onRemoveDiscount = { discount -> cart = cart - discount }
                )
            }
        }
    }
}

@Composable
fun ShopTopBar(
    currentTab: ShopTab,
    hasItems: Boolean,
    onBack: () -> Unit,
    onOpenCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (currentTab == ShopTab.Cart) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
                Text(
                    text = stringResource(R.string.title_cart),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            } else {
                Text(
                    text = stringResource(R.string.name_shop),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        if (currentTab != ShopTab.Cart) {
            IconButton(onClick = onOpenCart, enabled = hasItems) {
                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)
            }
        }
    }
}

@Composable
fun ShopBottomBar(
    currentTab: ShopTab,
    onSelectTab: (ShopTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = { onSelectTab(ShopTab.Shop) }) {
            Text(
                text = stringResource(R.string.name_shop),
                fontWeight = if (currentTab == ShopTab.Shop) FontWeight.Bold else FontWeight.Normal
            )
        }
        TextButton(onClick = { onSelectTab(ShopTab.Discounts) }) {
            Text(
                text = stringResource(R.string.title_discounts),
                fontWeight = if (currentTab == ShopTab.Discounts) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun CartBottomBar(
    total: Euro,
    hasItems: Boolean,
    onPay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = total.toString(), style = MaterialTheme.typography.titleMedium)
        Button(onClick = onPay, enabled = hasItems) {
            Text(stringResource(R.string.action_pay))
        }
    }
}

@Composable
fun ShopTabContent(
    shop: Shop,
    cart: Cart,
    onAddItem: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = shop.items.toList()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(items, key = { item -> item.id }) { item ->
            val nameRes = MockData.getName(item)
            val imageRes = MockData.getImage(item)
            val price = shop.prices[item] ?: Euro(0u)
            val amount = cart.items[item] ?: 0u

            ShopItemRow(
                item = item,
                name = stringResource(nameRes),
                price = price,
                imageRes = imageRes,
                amount = amount,
                onAddToCart = { onAddItem(item) }
            )
        }
    }
}

@Composable
fun DiscountsTabContent(
    availableDiscounts: List<Discount>,
    cart: Cart,
    onAddDiscount: (Discount) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(availableDiscounts) { discount ->
            val alreadyInCart = cart.discounts.contains(discount)
            DiscountRow(
                discount = discount,
                enabled = !alreadyInCart,
                onAdd = { if (!alreadyInCart) onAddDiscount(discount) }
            )
        }
    }
}

@Composable
fun CartTabContent(
    shop: Shop,
    cart: Cart,
    onIncrease: (Item) -> Unit,
    onDecrease: (Item) -> Unit,
    onRemoveDiscount: (Discount) -> Unit,
    modifier: Modifier = Modifier
) {
    if (cart.items.isEmpty() && cart.discounts.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(R.string.cart_empty), textAlign = TextAlign.Center)
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(cart.items.toList(), key = { pair -> pair.first.id }) { (item, amount) ->
                val nameRes = MockData.getName(item)
                val imageRes = MockData.getImage(item)
                val price = shop.prices[item] ?: Euro(0u)

                CartItemRow(
                    item = item,
                    name = stringResource(nameRes),
                    price = price,
                    imageRes = imageRes,
                    amount = amount,
                    onIncrease = { onIncrease(item) },
                    onDecrease = { onDecrease(item) }
                )
            }

            if (cart.discounts.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.title_discounts),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
                items(cart.discounts) { discount ->
                    CartDiscountRow(
                        discount = discount,
                        onRemove = { onRemoveDiscount(discount) }
                    )
                }
            }
        }
    }
}

@Composable
fun ShopItemRow(
    item: Item,
    name: String,
    price: Euro,
    imageRes: Int,
    amount: UInt,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = name,
                modifier = Modifier.size(64.dp)
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = name, style = MaterialTheme.typography.titleMedium)
                Text(text = price.toString(), style = MaterialTheme.typography.bodyMedium)
                if (amount > 0u) {
                    Text(
                        text = stringResource(R.string.cart_amount, amount.toInt()),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        Button(onClick = onAddToCart) {
            Text(stringResource(R.string.action_add_to_cart))
        }
    }
}

@Composable
fun DiscountRow(
    discount: Discount,
    enabled: Boolean,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DiscountLabel(
            discount = discount,
            modifier = Modifier.weight(1f)
        )
        Button(onClick = onAdd, enabled = enabled) {
            Text(stringResource(R.string.action_add_to_cart))
        }
    }
}

@Composable
fun CartItemRow(
    item: Item,
    name: String,
    price: Euro,
    imageRes: Int,
    amount: UInt,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lineTotal = price * amount

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = name,
                modifier = Modifier.size(56.dp)
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = name, style = MaterialTheme.typography.titleMedium)
                Text(text = lineTotal.toString(), style = MaterialTheme.typography.bodyMedium)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = onDecrease) { Text("-") }
            Text(text = amount.toString(), modifier = Modifier.padding(horizontal = 8.dp))
            OutlinedButton(onClick = onIncrease) { Text("+") }
        }
    }
}

@Composable
fun CartDiscountRow(
    discount: Discount,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DiscountLabel(discount = discount, modifier = Modifier.weight(1f))
        TextButton(onClick = onRemove) { Text(stringResource(R.string.action_remove)) }
    }
}

@Composable
fun DiscountLabel(
    discount: Discount,
    modifier: Modifier = Modifier
) {
    val text = when (discount) {
        is Discount.Fixed -> stringResource(R.string.discount_fixed, discount.amount.toString())
        is Discount.Percentage -> stringResource(
            R.string.discount_percentage,
            discount.value.toInt()
        )

        is Discount.Bundle -> {
            val nameRes = MockData.getName(discount.item)
            val itemName = stringResource(nameRes)
            stringResource(
                R.string.discount_bundle,
                discount.amountItemsGet.toInt(),
                discount.amountItemsPay.toInt(),
                itemName
            )
        }
    }
    Text(text = text, modifier = modifier.padding(end = 8.dp))
}