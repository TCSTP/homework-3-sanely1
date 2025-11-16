package tcs.app.dev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import tcs.app.dev.homework1.ShopScreen
import tcs.app.dev.homework1.data.MockData
import tcs.app.dev.ui.theme.AppTheme

/**
 * The main activity of the app.
 * This is the entry point of the app.
 * More details about activities in the next lecture!
 */
class MainActivity : ComponentActivity() {

    /**
     * This function is called when the activity is created.
     * More about life cycles of activities in the next lecture!
     *
     * [setContentView] is the bridge between the Android framework and ComposeUI.
     * In [AppTheme] is our color/ font configuration.
     *
     * ## Tasks:
     * - Put here the composable function that should be the main entry point of your app.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                ShopScreen(
                    shop = MockData.ExampleShop,
                    availableDiscounts = MockData.ExampleDiscounts,
                    modifier = Modifier
                )
            }
        }
    }
}
